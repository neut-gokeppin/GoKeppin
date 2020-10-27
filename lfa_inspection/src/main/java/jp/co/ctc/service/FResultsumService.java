package jp.co.ctc.service;

import java.util.List;

import javax.annotation.Resource;

import jp.co.ctc.entity.FBcdata;
import jp.co.ctc.entity.FResult;
import jp.co.ctc.entity.FResultsum;
import jp.co.ctc.entity.MGroup;
import jp.co.ctc.entity.MOrder;
import jp.co.ctc.entity.RUpload;
import jp.co.ctc.util.ServletUtil.State;
import jp.co.ctc.util.Utils;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.service.S2AbstractService;
import org.seasar.extension.jdbc.where.SimpleWhere;

/**
 * 検査結果サマリテーブルを扱うサービス
 */
public class FResultsumService extends S2AbstractService<FResultsum> {

	/**
	 * FBcdataServiceを使います
	 */
	@Resource
	public FBcdataService fBcdataService;

	/**
	 * MGroupServiceを使います
	 */
	@Resource
	public MGroupService mGroupService;

	/**
	 * MOrderServiceを使います
	 */
	@Resource
	public MOrderService mOrderService;


	/**
	 * 検査結果サマリデータを検索します。
	 * @param idno アイデントNo
	 * @param loDate ラインオフ計画日
	 * @return 検査結果サマリのリスト
	 */
	public List<FResultsum> selectByFrame(String idno, String loDate) {
		List<FResultsum> fResultsumList = select()
				.innerJoin("mGroup")
				.where(new SimpleWhere()
						.eq("idno", idno)
						.eq("loDate", loDate))
				.getResultList();

		return fResultsumList;
	}


	/**
	 * 検査結果サマリの初期データを作成します。
	 * @param bcdata 対象の車両
	 * @param group 対象の工程
	 */
	public void setInitialData(FBcdata bcdata, MGroup group) {
		// 検査結果サマリ初期データ作成
		FResultsum sum = create(bcdata);
		sum.mstVer = group.mstVer;
		sum.groupCode = group.groupCode;
		sum.groupStatus = State.noCheck.ordinal();

		// 既存データがあれば更新。その際、groupStatusは引き継ぐ
		int count = jdbcManager.update(sum)
				.excludes("groupStatus", "insertUser", "insertDate")
				.execute();

		// 既存データが無ければ追加
		if (count == 0) {
			insert(sum);
		}
	}


	/**
	 * 検査結果サマリテーブルにデータを登録
	 * @param rUpload 登録用エンティティ
	 */
	public void setResultsum(RUpload rUpload) {

		// TODO groupCode=0 の場合、全工程の検査結果サマリを更新する
		// とりあえずエラー回避
		if (rUpload.groupCode == 0) {
			return;
		}

		// グループの状態を算出し、FResultsumを作成
		FResultsum fResultsum = getResultsum(rUpload.idno, rUpload.loDate, rUpload.mstVer, rUpload.groupCode, rUpload.userID);

		// 既存データがあれば更新
		int count = jdbcManager.update(fResultsum)
			.includes("groupStatus", "mstVer", "updateUser", "updateDate")
			.execute();

		// 既存データが無ければ追加
		if (count == 0) {
			insert(fResultsum);
		}

	}


	/**
	 * 工程の検査結果状態を算出し、FResultsumオブジェクトに格納して返します。
	 * @param idno アイデントNo
	 * @param loDate ラインオフ計画日
	 * @param mstVer マスタバージョン
	 * @param groupCode グループコード
	 * @param userCode 検査員の従業員コード
	 * @return FResultsumオブジェクト
	 */
	private FResultsum getResultsum(String idno, String loDate, Integer mstVer, Integer groupCode, String userCode) {
		FBcdata fBcdata = fBcdataService.getSimpleFBcdata(idno, loDate);

		// 検査結果の取得
		List<MOrder> mOrderList = mOrderService.getMOrderList(fBcdata, groupCode, mstVer);

		// 検索結果が0件（グループに紐付く検査項目が無い）の場合、nullを返して終了
		if (mOrderList.isEmpty()) {
			return null;
		}

		// グループ状態を算出する
		State gState = calcGroupState(mOrderList);

		FResultsum fResultsum = create(fBcdata);
		fResultsum.mstVer = mstVer;
		fResultsum.groupCode = groupCode;
		fResultsum.groupStatus = gState != null ? gState.ordinal() : fResultsum.groupStatus;
		fResultsum.insertUser = userCode;
		fResultsum.updateUser = userCode;

		return fResultsum;
	}


	/**
	 * 工程の検査状態を算出する。
	 * @param mOrderList 検査結果を含む検査順リスト
	 * @return 工程の検査状態
	 */
	private State calcGroupState(List<MOrder> mOrderList) {

		State gState = State.compleat;		//ステータスを格納する変数
		boolean uploadFlg = false; //検査結果がアップロードされているか
		boolean noData = true; // グループに検査項目が存在しないか

		for (MOrder mOrder : mOrderList) {

			// 検査項目は存在する
			noData = false;

			boolean itemChecked = false;
			for (FResult fResult : mOrder.fResultList) {
				itemChecked = true;
				if (StringUtils.isEmpty(fResult.inspecResult)) {
					//検査結果が未検査の場合、まずは再検査扱いにする。
					gState = State.reCheck;
				} else {
					uploadFlg = true;
					if (!fResult.inspecResult.equals("0")) {
						gState = State.reCheck;
					}
				}
				break;
			}

			if (!itemChecked) {
				//検査結果が取得できない場合（＝未検査の場合）、まずは再検査扱いにする。
				gState = State.reCheck;
			}
		}

		if (noData) {
			//検査項目が1件もなければ完了あつかい
			gState = State.compleat;
		} else {
			if (!uploadFlg) {
				// グループ内に検査項目が存在し、かつ検査結果が1件もない場合、
				// アップロードされていないとみなし未検査扱い
				gState = State.noCheck;
			}
		}

		return gState;
	}


	/**
	 * FResultsumオブジェクト作成。
	 * @param fBcdata FBcdataオブジェクト
	 * @return 作成したFResultsumオブジェクト
	 */
	public FResultsum create(FBcdata fBcdata) {
		FResultsum fResultsum = new FResultsum();
		fResultsum.idno = fBcdata.idno;
		fResultsum.loDate = fBcdata.loDate;
		fResultsum.bodyNo = fBcdata.bodyNo;
		fResultsum.recvDay = fBcdata.recvDay;
		fResultsum.groupStatus = State.noCheck.ordinal();
		fResultsum.insertUser = "SYSTEM";
		fResultsum.updateUser = fResultsum.insertUser;
		fResultsum.insertDate = Utils.nowts();
		fResultsum.updateDate = fResultsum.insertDate;
		fResultsum.fBcdata = fBcdata;

		return fResultsum;
	}


	/**
	 * F_RESULTSUMテーブルのデータを全件作成しなおす
	 * @deprecated 修正が必要です
	 */
	@Deprecated
	public void refreshAll() {
		// ボデーのデータを取得
		List<FBcdata> fBcdataList = jdbcManager.from(FBcdata.class)
				.where("mstVer>0")
				.orderBy("bodyNo, recvDay")
				.getResultList();

		// ボデーごとに処理
		for (FBcdata fBcdata : fBcdataList) {
			// グループのデータを取得
			List<MGroup> mGroupList = mGroupService.getMGroups(1, 0, fBcdata.bctype);

			// グループごとに処理
			for (MGroup mGroup : mGroupList) {
				// FResultsumレコードを作成してDBに登録
				FResultsum sumResult = new FResultsum();
				sumResult.bodyNo = fBcdata.bodyNo;
				sumResult.recvDay = fBcdata.recvDay;
				sumResult.groupCode = mGroup.groupCode;
				sumResult.insertUser = "CJ01615";
				sumResult.updateUser = "CJ01615";
				insert(sumResult);
			}
		}
	}
}
