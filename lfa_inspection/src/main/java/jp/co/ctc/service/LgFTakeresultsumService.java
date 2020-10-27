package jp.co.ctc.service;

import java.sql.Timestamp;
import java.util.List;

import jp.co.ctc.dto.LgMBcsignDTO;
import jp.co.ctc.entity.FBcdata;
import jp.co.ctc.entity.LgFTakeresult;
import jp.co.ctc.entity.LgFTakeresultsum;
import jp.co.ctc.entity.LgMOrder;
import jp.co.ctc.entity.LgMSps;
import jp.co.ctc.util.ServletUtil;
import jp.co.ctc.util.Utils;
import jp.co.ctc.util.ServletUtil.State;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.JdbcManager;

/**
 *
 * @author CJ01729
 *
 */
public class LgFTakeresultsumService {

	/**
	 * JdbcManagerを使います
	 */
	public JdbcManager jdbcManager;

	/**
	 * FBcdataServiceを使います
	 */
	public FBcdataService fBcdataService;

	/**
	 * LgMSpsServiceを使います
	 */
	public LgMSpsService lgMSpsService;

	/**
	 * 取出結果と一致する行データを削除します。
	 *
	 * @param resultSum 削除対象の結果サマリーレコード
	 * @return 削除データ数
	 */
	public int remove(LgFTakeresultsum resultSum) {
		return jdbcManager.delete(resultSum)
			.execute();
	}

	/**
	 * 取出結果をテーブルに挿入します。
	 *
	 * @param insResultSum
	 *            取出結果
	 * @return 作成データ数
	 */
	public int create(LgFTakeresultsum insResultSum) {
		return jdbcManager.insert(insResultSum)
			.execute();

	}


	/**
	 * 取出結果と一致する行データを削除後挿入します。
	 *
	 * @param insResultSum
	 *            取出結果
	 * @return データ作成結果
	 */
	public int delins(LgFTakeresultsum insResultSum) {

		//既存データを削除する。
		remove(insResultSum);
		//結果を追加する。
		return create(insResultSum);
	}

	/**
	 * 状態テーブルにデータを登録
	 * @param sumResult 登録用エンティティ
	 */
	public void setResultsum(LgFTakeresultsum sumResult) {
		// SPS台車の状態を算出
		State gState = calcGroupState(sumResult.bodyNo, sumResult.recvDay, sumResult.spsCode);
		// 状態算出できなかった場合、もしくは未着手の場合、サマリーレコードをクリアして終了
		if (gState == null || gState == State.noCheck) {
			remove(sumResult);
			return;
		}

		// 更新レコードにセットするタイムスタンプ
		Timestamp timestamp = Utils.nowts();

		// サマリテーブル登録用のレコード作成
		sumResult.spsStatus = gState.ordinal();
		sumResult.insertDate = timestamp;
		sumResult.updateDate = timestamp;

		// サマリテーブルに登録
		delins(sumResult);
	}

	/**
	 * グループ状態を算出する
	 * @param bodyNo ボデーNO
	 * @param recvDay 受信日
	 * @param spsCode SPS台車NO
	 * @return グループ状態
	 */
	private State calcGroupState(String bodyNo, String recvDay, int spsCode) {
		// DBからBCデータを読む
		FBcdata fBcdata = fBcdataService.getLogFBcdata(bodyNo, recvDay).get(0);
		// DBからグループ（SPS台車）を読む
		LgMSps lgMSps = lgMSpsService.getLgMSpsByName(1, spsCode, fBcdata.lgmstVer).get(0);
		// グループ状態を算出する
		return calcGroupState(fBcdata, lgMSps);
	}

	/**
	 * グループ状態を算出する
	 * @param fBcdata BCデータ
	 * @param lgMSps SPS台車データ
	 * @return グループ状態
	 */
	private State calcGroupState(FBcdata fBcdata, LgMSps lgMSps) {

		//ステータスを格納する変数。まずは完了扱いにしておく。
		State gState = State.compleat;
		//取出し結果がアップロードされているか
		boolean uploadFlg = false;

		// 結果リストを取得し、1件も結果が無ければ未着手とする
		List<LgFTakeresult> lgFTakeresultList = fBcdata.fTakeResultList;
		if (Utils.isEmpty(lgFTakeresultList)) {
			return State.noCheck;
		}

		// 部品順リストを取得し、1件も部品が無ければ完了とする
		List<LgMOrder> mOrderList = lgMSps.mOrderList;
		if (Utils.isEmpty(mOrderList)) {
			return State.compleat;
		}

		// 部品ごとにループする
		for (LgMOrder mOrder : mOrderList) {

			// 取り出し不要の場合は無視。次の部品に進む
			if (!this.getCheckFlg(fBcdata, mOrder)) {
				continue;
			}

			//現在処理しているアイテムに対し、検査／取出しが行われているか。
			boolean itemChecked = false;

			// 取出し結果を処理する。
			for (LgFTakeresult fResult : lgFTakeresultList) {

				//順番テーブルと結果テーブルのアイテムコードが異なる場合、次の結果の処理に進む
				if (!mOrder.partCode.equals(fResult.partCode)) {
					continue;
				}

				// 取出し結果を取得
				if (!StringUtils.isBlank(fResult.partResult)) {
					//取出し結果が未取出しでなければ取出し履歴ありとする。
					itemChecked = true;
					uploadFlg = true;
					String checkResult = fResult.partResult.trim();
					if (!checkResult.equals("0")) {
						//取出し結果がOKではない場合、再取出し（NGあり）とする。
						gState = State.reCheck;
					}
				}
				// 最近の取出し結果だけを判定材料とするためにbreakする
				break;
			}

			if (!itemChecked && gState != State.reCheck) {
				//現在のアイテムに対し取出し結果がなく、現時点で再取出し（NGあり）でないなら、まずは未確認ありとする。
				gState = State.yetCheck;
			}
		}

		//取出し結果が1件もなければアップロードされていないとみなし未取出し扱い
		if (!uploadFlg) {
			gState = State.noCheck;
		}

		return gState;
	}

	/**
	 * 取出し不要フラグを確認する
	 * @param bcdata BCデータ
	 * @param mOrder 順データ
	 * @return 取出しが必要か
	 */
	private boolean getCheckFlg(FBcdata bcdata, LgMOrder mOrder) {
		try {
			LgMBcsignDTO mBcsign = (LgMBcsignDTO) ServletUtil.getMsgSign(ServletUtil.Attribute.logistics, bcdata, mOrder.mPart);
			return !mBcsign.notuseFlag;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * LG_F_TAKERESULTSUMテーブルのデータを全件作成しなおす
	 */
	public void refreshAll() {
		// ボデーのデータを取得
		List<FBcdata> fBcdataList = jdbcManager.from(FBcdata.class)
				.where("lgmstVer>0")
				.orderBy("bodyNo, recvDay")
				.getResultList();

		// ボデーごとに処理
		for (FBcdata fBcdata : fBcdataList) {
			// SPS台車のデータを取得
			List<LgMSps> lgMSpsList = lgMSpsService.getLgMSpss(1, fBcdata.lgmstVer);

			// SPS台車ごとに処理
			for (LgMSps lgMSps : lgMSpsList) {
				// 部品の紐付いていない台車はスキップする
				if (Utils.isEmpty(lgMSps.mOrderList)) {
					continue;
				}

				// LgFTakeresultsumレコードを作成してDBに登録
				LgFTakeresultsum sumResult = new LgFTakeresultsum();
				sumResult.bodyNo = fBcdata.bodyNo;
				sumResult.recvDay = fBcdata.recvDay;
				sumResult.spsCode = lgMSps.spsCode;
				sumResult.insertUser = "CJ01615";
				sumResult.updateUser = "CJ01615";
				setResultsum(sumResult);
			}
		}
	}
}
