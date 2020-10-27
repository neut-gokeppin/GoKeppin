/**
 *
 */
package jp.co.ctc.servlet;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import jp.co.ctc.entity.FBcdata;
import jp.co.ctc.entity.LgFTakeresultsum;
import jp.co.ctc.entity.LgMSps;
import jp.co.ctc.service.LgMSpsService;
import jp.co.ctc.service.TransactionService;
import jp.co.ctc.util.ServletUtil;
import jp.co.ctc.util.ServletUtil.LockState;
import jp.co.ctc.util.ServletUtil.State;
import jp.co.ctc.util.Utils;

import org.seasar.framework.container.SingletonS2Container;

/**
 * @author CJ01786
 *
 */
public abstract class Body extends CreateResponse {

	/**
	 * 排他情報を格納するリスト
	 */
	protected List<?> lockList;

	/**
	 * クライアントへのレスポンスを取得
	 * @param inputStream クライアントから送られてくるストリーム
	 * @return クライアントへ送る値
	 */
	@Override
	public String getResponse(ByteArrayInputStream inputStream) {

		List<FBcdata> resBcdata;

		//トランザクション開始
		TransactionService srvTran = SingletonS2Container.getComponent(TransactionService.class);
		try {
			if (srvTran.startTransaction()) {

				//データセット生成の開始
				this.srvXmlWriter.createDataset();

				//排他情報のリストを取得する。
				this.lockList = this.getLockList();

				//SPS台車のリストを取得する
				LgMSpsService srvGroup = SingletonS2Container.getComponent(LgMSpsService.class);
				List<LgMSps> lgMSpsList = srvGroup.getLgMSps4Body();

				//BCデータを読み、ボデーNoの数だけ繰り返す。
				resBcdata = this.getFBcdataList();
				for (FBcdata fBcdata : resBcdata) {
					ArrayList<Object> saveGroup = new ArrayList<Object>();	//SPS台車の情報を格納するリスト

					// マスタバージョンがボデーと一致するSPS台車データを抜き出す
					List<LgMSps> resGroup = new ArrayList<LgMSps>();
					for (LgMSps lgMSps : lgMSpsList) {
						int lgmstVer = fBcdata.lgmstVer == null ? 0 : fBcdata.lgmstVer.intValue();
						if ((lgmstVer == 0 && lgMSps.sopFlag.equals("1"))
								|| (lgmstVer > 0 && lgMSps.mstVer.equals(lgmstVer))) {
							resGroup.add(lgMSps);
						}
					}

					//ボデー状態を格納する
					State bState = State.compleat;

					for (Object group : resGroup) {
						//SPS台車の情報を取得する。
						try {
							group = this.setGroupState(fBcdata, group);

							State groupState = this.getGroupState(group);

							//排他テーブルを読み、ロックの有無を取得する。
							LockState lockState;
							Integer mstVer = this.getMstver(fBcdata);
							if (mstVer == 0) {
								//マスタバージョンが０の場合ロックはないと判断
								lockState = LockState.nothing;
							} else {
								//そうでないならばロックを確認する。
								lockState = this.getLockState(fBcdata, group);
							}


							bState = this.getBodyState(bState, groupState, lockState);

							if (lockState != LockState.nothing) {
								//ロックされていれば検査中・取出し中と判断する。
								group = this.setGroupState(group, State.checking);
							}

						} catch (Exception e) {
							//例外発生したものは処理しない。
							e.printStackTrace();
						}
						//情報をリストに格納する。
						saveGroup.add(group);
					}

					if (bState != State.compleat) {
						//ボデー状態が完了でないならばデータを返す。
						try {
							for (Object mGroup : saveGroup) {
								if (this.getGroupState(mGroup) != State.compleat) {
									//状態が完了でない場合データを返す。
									this.setXml(fBcdata, mGroup, bState);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

				srvTran.commit();
				return this.srvXmlWriter.getXMLData();
			}
			return ServletUtil.RESPONSE_NG;

		} catch (Exception e) {
			e.printStackTrace();
			srvTran.rollback();
			return ServletUtil.RESPONSE_NG;
		}
	}

	/**
	 * BCデータからグループ・SPS台車の一覧を取得する。
	 * @param bcdata BCデータ
	 * @return 取得したデータの一覧
	 */
	public abstract List<?> getGroupList(FBcdata bcdata);

	/**
	 * グループ状態を取得する
	 * ※組立用です。検査では動作しません
	 * @param bcdata BCデータエンティティ
	 * @param group グループ/SPS台車エンティティ
	 * @return グループ状態が設定されたエンティティ
	 * @throws Exception 処理例外
	 */
	protected Object setGroupState(FBcdata bcdata, Object group) throws Exception {

		State gState = State.noCheck;		//ステータスを格納する変数。まずは未着手扱いにしておく。
		List<LgFTakeresultsum> resultList = bcdata.fTakeResultsumList;

		// 取出結果が無い場合未着手と判断する。
		if (Utils.isEmpty(resultList)) {
			return this.setGroupState(group, State.noCheck);
		}

		// 結果サマリーの情報の中から、該当のSPS台車の結果を検索する
		for (LgFTakeresultsum res : resultList) {
			if (res.spsCode.equals(((LgMSps) group).spsCode)) {
				gState = State.values()[res.spsStatus];
				break;
			}
		}

		return this.setGroupState(group, gState);
	}

	/**
	 * グループ・SPS台車状態を設定する。
	 * @param group 設定対象となるオブジェクト
	 * @param state 設定する状態
	 * @return 設定したあとのオブジェクト
	 * @throws Exception 処理例外
	 */
	protected abstract Object setGroupState(Object group, State state) throws Exception;

	/**
	 * グループ・SPS台車状態を取得する。
	 * @param group グループ・SPS台車
	 * @return グループ・SPS台車状態
	 * @throws Exception 処理例外
	 */
	protected abstract State getGroupState(Object group) throws Exception;

	/**
	 * 現在のボデー状態とグループ・SPS台車状態からボデー状態を取得する。
	 * @param bodyState 現在のボデー状態
	 * @param groupState グループ・SPS台車状態
	 * @param lockState ロック状態
	 * @return 新たなボデー状態
	 */
	protected abstract State getBodyState(State bodyState, State groupState, LockState lockState);

	/**
	 * 	BCデータとグループ（SPS台車）からロックの状態を取得する。
	 * @param bcdata BCデータ
	 * @param group グループ・SPS台車
	 * @return ロック状態
	 * @exception Exception 処理例外
	 */
	protected abstract LockState getLockState(FBcdata bcdata, Object group) throws Exception;

	/**
	 * XML文を生成する。
	 * @param bcdata BCデータ
	 * @param group グループ・SPS台車
	 * @param bodyState ボデー状態
	 * @return 成功可否
	 * @throws Exception 処理例外
	 */
	protected abstract boolean setXml(FBcdata bcdata, Object group, State bodyState) throws Exception;

	/**
	 * BCデータからマスタバージョンを取得する。
	 * @param bcdata マスタバージョンを取得するBCデータ
	 * @return 取得したマスタバージョン
	 */
	protected abstract int getMstver(FBcdata bcdata);

	/**
	 * 排他制御のリストを取得する。
	 * @return 取得した排他情報のリスト
	 */
	protected abstract List<?> getLockList();

	/**
	 * BCデータを取得する。
	 * @return 取得したBCデータのリスト
	 */
	protected abstract List<FBcdata> getFBcdataList();
}
