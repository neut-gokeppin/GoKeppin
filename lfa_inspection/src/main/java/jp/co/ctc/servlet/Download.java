/**
 *
 */
package jp.co.ctc.servlet;

import java.io.ByteArrayInputStream;
import java.util.List;

import jp.co.ctc.entity.FBcdata;
import jp.co.ctc.service.TransactionService;
import jp.co.ctc.util.ServletUtil;
import jp.co.ctc.util.ServletUtil.LockState;

import org.seasar.framework.container.SingletonS2Container;

/**
 * ダウンロード要求を処理するための抽象クラス
 * このクラスを継承して処理を作成する。
 * @author CJ01786
 *
 */
public abstract class Download extends CreateResponse {

	/**
	 * クライアントへのレスポンスを取得
	 * ロック状態の確認をしない。
	 * @param inputStream クライアントから送られてくるストリーム
	 * @return クライアントへ送る値
	 */
	@Override
	public String getResponse(ByteArrayInputStream inputStream) {
		return this.getResponse(inputStream, this.lockCheckFlg);
	}

	/**
	 * クライアントへのレスポンスを取得。
	 * ロック状態の確認を行う。
	 * @param inputStream クライアントから送られてくるストリーム
	 * @param checkLockFlg ロック状態をチェックするか
	 * @return クライアントへ送る値
	 */
	public String getResponse(ByteArrayInputStream inputStream, boolean checkLockFlg) {

		//受信したXMLをインスタンス化する。
		List<?> rcvGroup = this.getRecvEntity(inputStream);	//配列生成処理を行う。

		//データアクセスサービス郡
		TransactionService srvTran = SingletonS2Container.getComponent(TransactionService.class);

		boolean ngFlg = false;	//例外等が発生したかどうか
		FBcdata recBcdata = null;	//処理するボデーのデータ

		try {
			if (checkLockFlg) {
				//ロックをチェックする
				LockState lState = this.getLockState(rcvGroup);
				switch (lState) {
					case anotherLock:
						//ほかのユーザがロックしていればNGを返す
						return ServletUtil.RESPONSE_NG;
					case userLock:
						//自身がロックしていればLOCKを返す
						return ServletUtil.RESPONSE_LOCK;
					default:
						//そうでない（＝ロックなし）の場合、データの取得を続ける。
						break;
				}
			}
		} catch (Exception e) {
			return ServletUtil.RESPONSE_NG;
		}

		this.srvXmlWriter.createDataset();	//データセットの作成

		//排他テーブルのトランザクションを生成する。
		if (srvTran.startTransaction()) {
			try {
				for (Object rDownload : rcvGroup) {

					//ボデーは単一なので、初回だけBCデータを読む。
					if (recBcdata == null) {
						recBcdata = this.getBcdata(rDownload);
					}

					//グループCodeから該当するグループレコードを取得する
					Object recGroup = this.getGroup(recBcdata, rDownload);

					//BCデータにマスタバージョンを設定
					this.setMstVer(recBcdata, recGroup);

					boolean noData = true;		//送信するデータがないかどうか。

					//ロックをかける。
					this.createLock(rDownload);

					List<?> mOrderList = this.getOrderList(recGroup);

					for (Object mOrder : mOrderList) {

						if (this.setGroupXmlByOrder(recBcdata, recGroup, mOrder, rDownload)) {
							//処理が完遂した場合
							noData = false;		//データが存在するとみなし、データなしフラグをfalseにする。
						}
					}
					if (noData) {
						//データがない場合、さきほど設定したロックを解除する。
						this.removeLock(rDownload);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				ngFlg = true;
			}
		}
		if (ngFlg) {
			//NG箇所があった場合、それまで設定したロックを解除してNGを返す。
			srvTran.rollback();
			return ServletUtil.RESPONSE_NG;
		} else {
			//NGでなければロック状態をコミットし、XMLを返す。
			srvTran.commit();
			return this.srvXmlWriter.getXMLData();
		}
	}

	/**
	 * ストリームからXMLを読み出し、エンティティに格納してリスト化する。
	 * @param inputStream XMLを取得するためのストリーム
	 * @return 生成されたインスタンスのリスト
	 */
	protected abstract List<?> getRecvEntity(ByteArrayInputStream inputStream);

	/**
	 * 受信したXMLの値全体のロック状態をチェックする。
	 * @param rDownload 受信したXMLのエンティティリスト
	 * @return ロック状態
	 */
	protected abstract LockState getLockState(List<?> rDownload);

	/**
	 * 指定したダウンロード要求に対しロックをかける
	 * @param rDownload 受信したXMLのエンティティリスト
	 * @exception Exception 処理例外
	 */
	protected abstract void createLock(Object rDownload) throws Exception;

	/**
	 * 指定したダウンロード要求に対してかけたロックを解除する
	 * @param rDownload 受信したXMLのエンティティリスト
	 * @exception Exception 処理例外
	 */
	protected abstract void removeLock(Object rDownload) throws Exception;

	/**
	 * XMLで指定された値からBCデータを取得する。
	 * @param rDownload 処理中の要求
	 * @return 取得したインスタンス
	 * @throws Exception 処理例外
	 */
	protected abstract FBcdata getBcdata(Object rDownload) throws Exception;

	/**
	 * XMLで指定されたグループ・SPS台車コードから、該当するデータを取得する。
	 * @param bcdata BCデータ
	 * @param rDownload 処理中の要求
	 * @return 取得したインスタンス
	 * @exception Exception 処理例外
	 */
	protected abstract Object getGroup(FBcdata bcdata, Object rDownload) throws Exception;

	/**
	 * BCデータにマスタバージョンが設定されていない場合設定する。
	 * @param bcdata BCデータ
	 * @param group グループ・SPS台車のインスタンス
	 * @throws Exception 処理例外
	 */
	protected abstract void setMstVer(FBcdata bcdata, Object group) throws Exception;

	/**
	 * 順番を指定するマスタからデータを所得する。
	 * @param group 順番を取得したいグループ・SPS台車のインスタンス
	 * @return 取得したインスタンス
	 * @throws Exception 処理例外
	 */
	protected abstract List<?> getOrderList(Object group) throws Exception;

	/**
	 * 項目（部品）および指示記号からなるDTOインスタンスからXMLを設定する。
	 * @param item 項目（部品）
	 * @return XMLが生成されたか
	 * @throws Exception 処理例外
	 */
	protected abstract boolean setGroupXmlByItem(Object item) throws Exception;

	/**
	 * BCデータ、グループ（SPS台車）、検査（取出し）順のエンティティからXMLを設定する。
	 * @param bcdata BCデータ
	 * @param group グループ（SPS台車）エンティティ
	 * @param order 検査（取出し）順エンティティ
	 * @param rDownload 受信したXMLのエンティティ
	 * @return XMLが生成されたか
	 * @exception Exception 処理例外
	 */
	protected abstract boolean setGroupXmlByOrder(FBcdata bcdata, Object group, Object order, Object rDownload) throws Exception;

	/**
	 * ボデーNo、受信日、項目（部品）コードから検査（取出し）結果を設定
	 * @param bodyNo ボデーNo
	 * @param recvDay 受信日
	 * @param itemCode 項目（部品）コード
	 * @return 検査結果がOKかどうか
	 */
	protected abstract boolean setXmlResultflg(String bodyNo, String recvDay, Integer itemCode);

}
