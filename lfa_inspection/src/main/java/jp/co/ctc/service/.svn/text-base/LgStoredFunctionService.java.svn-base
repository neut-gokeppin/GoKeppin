package jp.co.ctc.service;

import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.extension.jdbc.annotation.ResultSet;


/**
* サーバーの関数をCallするサービスです。
*
* @author Kato
*
*/
public class LgStoredFunctionService {

	/**
	 * 引数なし(リターンコードのみ取得)のStoredFunction Call時用クラス
	 */
	private class onlyRetCode {
		@ResultSet
		int retCode;
	}

	/**
	 * JDBCマネージャ
	 */
	public JdbcManager jdbcManager;

	public int tempLgMstRegist() {
		onlyRetCode funcPara = new onlyRetCode();

		jdbcManager.call("lg_mst_regist", funcPara).execute();

		if (funcPara.retCode == 0) {
			// 正常終了
			return 0;
		}
		// 異常終了
		return -1;

	}

}
