package jp.co.ctc.service;

import org.seasar.extension.jdbc.JdbcManager;

import jp.co.ctc.entity.LgFStoreresult;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author CJ01785
 *
 */
public class LgFStoreresultService {

	/**
	 * JdbcManagerを使います
	 */
	public JdbcManager jdbcManager;


	/**
	 * 搬入結果をテーブルに挿入します。
	 *
	 * @param result
	 *            搬入結果
	 * @return 識別子が設定された後の搬入結果
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean create(LgFStoreresult result) {
		try {
			jdbcManager.insert(result).excludes("sopFlag", "deleteFlag")
					.execute();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
