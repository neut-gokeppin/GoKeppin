/**
 *
 */
package jp.co.ctc.servlet;

import java.io.ByteArrayInputStream;
import java.util.List;

import jp.co.ctc.util.ServletUtil;
import jp.co.ctc.util.Utils;


/**
 * @author CJ01786
 *
 */
public abstract class Userid extends CreateResponse {

	/* (非 Javadoc)
	 * @see jp.co.ctc.servlet.CreateResponse#getResponse(java.io.ByteArrayInputStream)
	 */
	@Override
	public String getResponse(ByteArrayInputStream inputStream) {
		try {
			List<?> userList = this.getUserList();

			if (Utils.isEmpty(userList)) {
				return ServletUtil.RESPONSE_NG;
			}

			this.srvXmlWriter.createDataset();

			for (Object mUser : userList) {
				this.srvXmlWriter.ceateTable();

				String userCode = this.getUserCode(mUser);
				String userName = this.getUserName(mUser);

				this.srvXmlWriter.addData("userID", userCode.toUpperCase());
				this.srvXmlWriter.addData("userName", userName);
			}

			return this.srvXmlWriter.getXMLData();
		} catch (Exception e) {
			return ServletUtil.RESPONSE_NG;
		}
	}

	/**
	 * 従業員リストを取得します。
	 * @return 従業員リスト
	 * @exception Exception 処理例外
	 */
	protected abstract List<?> getUserList() throws Exception;

	/**
	 * 受け取ったエンティティから従業員コードを取得
	 * @param user 従業員エンティティ
	 * @return 従業員コード
	 * @exception Exception 処理例外
	 */
	protected abstract String getUserCode(Object user) throws Exception;

	/**
	 * 受け取ったエンティティから従業員名を取得
	 * @param user 従業員エンティティ
	 * @return 従業員名
	 * @exception Exception 処理例外
	 */
	protected abstract String getUserName(Object user) throws Exception;

}
