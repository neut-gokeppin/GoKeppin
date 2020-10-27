/**
 *
 */
package jp.co.ctc.service;

import javax.annotation.Resource;

import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.extension.jdbc.service.S2AbstractService;

/**
 * G-ALCデータベース読み込み用のAbstractService
 * @param <T> エンティティの型
 * @author Z087567
 *
 */
public abstract class GalcAbstractService<T> extends S2AbstractService<T> {

	/**
	 * G-ALCのデータベース読み込み用のJdbcManager設定。
	 * データベース接続設定は、"s2jdbc-galc.dicon", "jdbc-galc.dicon"にあります。
	 * このメソッドを開発者が利用する必要はありません。
	 * @param jdbcManager jdbcManager
	 */
	@Resource(name = "galcJdbcManager")
	public void setJdbcManager(JdbcManager jdbcManager) {
		this.jdbcManager = jdbcManager;
	}

}
