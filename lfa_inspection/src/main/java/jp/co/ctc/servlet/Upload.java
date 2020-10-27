/**
 *
 */
package jp.co.ctc.servlet;

import java.io.ByteArrayInputStream;
import java.util.List;

import jp.co.ctc.service.TransactionService;
import jp.co.ctc.util.ServletUtil;

import org.seasar.framework.container.SingletonS2Container;

/**
 * アップロード要求を処理するための抽象クラス
 * このクラスを継承して処理を作成する。
 * @author CJ01786
 *
 */
public abstract class Upload extends CreateResponse {

	/**
	 * 前処理で処理したコードを保持しておく。
	 * コード：グループ・SPS台車などの排他制御単位のコード
	 */
	protected Integer saveCode;

	/**
	 * クライアントへのレスポンスを取得
	 * @param inputStream クライアントから送られてくるストリーム
	 * @return クライアントへ送る値
	 */
	@Override
	public String getResponse(ByteArrayInputStream inputStream) {
		//受信したXMLをインスタンス化する。
		List<?> rcvUpload = this.getRecvEntity(inputStream);

		//サービスクラスのインスタンス生成
		TransactionService srvTran = SingletonS2Container.getComponent(TransactionService.class);

		this.saveCode = null;

		//トランザクションを生成する。
		if (srvTran.startTransaction()) {
			try {
				// 結果をXMLから読み取り、DBに登録する
				this.setResult(rcvUpload);

				//トランザクションのコミット
				if (srvTran.commit()) {
					//コミットに成功すればOKを返す
					return ServletUtil.RESPONSE_OK;
				} else {
					//コミットに失敗すればNGを返す。
					return ServletUtil.RESPONSE_NG;
				}
			} catch (Exception e) {
				e.printStackTrace();
				srvTran.rollback();
				return ServletUtil.RESPONSE_NG;
			}
		} else {
			//トランザクション生成に失敗したらNGを返す。
			return ServletUtil.RESPONSE_NG;
		}
	}

	/**
	 * 受信したXMLのインスタンスから結果を登録する。
	 * @param rcvUpload XMLから生成したインスタンス
	 * @exception Exception 処理例外
	 */
	protected abstract void setResult(List<?> rcvUpload) throws Exception;

	/**
	 * ストリームからXMLを読み出し、エンティティに格納してリスト化する。
	 * @param inputStream XMLを取得するためのストリーム
	 * @return 生成されたインスタンスのリスト
	 */
	protected abstract List<?> getRecvEntity(ByteArrayInputStream inputStream);
}
