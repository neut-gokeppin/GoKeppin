/**
 *
 */
package jp.co.ctc.service;

import java.lang.reflect.Field;

import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.FieldUtil;


/**
 * @author CJ01786
 *
 */
public abstract class UpdateService {

	/**
	 * JDBCマネージャです。
	 */
	public JdbcManager jdbcManager;

	/**
	 * 工程選択で「全て」を表す文字列
	 */
	public static final String ALL_GROUPS = "_all groups";

	/**
	 * 工程選択で「工程なし」を表す文字列
	 */
	public static final String NO_GROUPS = "_no groups";

	/**
	 * テーブルのエンティティを更新します。
	 *
	 * @param entity 更新したいテーブルのエンティティ
	 * @return 更新した行数
	 */
	public int updateEntity(Object entity) {
		return jdbcManager.update(entity)
		.excludes("sopFlag")
		.execute();
	}

	/**
	 * テーブルからエンティティを論理的に削除します。
	 *
	 * @param entity 削除したいテーブルのエンティティ
	 * @return 更新した行数
	 */
	public int deleteEntity(Object entity) {
		Field field = ClassUtil.getField(entity.getClass(), "deleteFlag");
		FieldUtil.set(field, entity, "1");
		return jdbcManager.update(entity)
		.includes("updateUser", "updateDate", "deleteFlag")
		.execute();
	}
}
