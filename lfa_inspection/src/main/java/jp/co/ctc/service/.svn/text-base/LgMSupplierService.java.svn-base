/**
 *
 */
package jp.co.ctc.service;

import java.util.List;

import jp.co.ctc.entity.LgMSupplier;

import org.seasar.extension.jdbc.JdbcManager;

/**
 * @author CJ01786
 *
 */
public class LgMSupplierService {
	/**
	 * JdbcManagerを使います。
	 */
	public JdbcManager jdbcManager;

	/**
	 * 仕入先のリストを取得する。
	 * @return 取得したユーザのリスト
	 */
	public List<LgMSupplier> getMSuppliers() {
		return jdbcManager.from(LgMSupplier.class)
			.getResultList();
	}

}
