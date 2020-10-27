/**
 *
 */
package jp.co.ctc.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import jp.co.ctc.entity.FBcdata;
import jp.co.ctc.entity.MItem;
import jp.co.ctc.entity.TSfbc1;
import jp.co.ctc.entity.TSfbc2;
import jp.co.ctc.entity.TSfbc3;
import jp.co.ctc.entity.TSfbc4;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.extension.jdbc.where.SimpleWhere;
import org.seasar.framework.convention.NamingConvention;
import org.seasar.framework.convention.PersistenceConvention;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.FieldUtil;

/**
 * @author CJ01786
 *
 */
public class TSfbcService {

	/**
	 * JDBCマネージャです。
	 */
	@Resource
	public JdbcManager jdbcManager;

	/**
	 * PersistenceConvention
	 */
	@Resource
	protected PersistenceConvention persistenceConvention;

	/**
	 * NamingConvention
	 */
	@Resource
	protected NamingConvention namingConvention;


	/**
	 * t_sfbc1～t_sfbc4のデータを取得します。
	 * @param fBcdata 対象車両
	 * @return t_sfbc1～t_sfbc4の入ったFBcdataオブジェクト
	 */
	public FBcdata getTSfbcAll(FBcdata fBcdata) {

		fBcdata.tSfbc1 = jdbcManager.from(TSfbc1.class)
				.id(fBcdata.idno, fBcdata.loDate)
				.getSingleResult();

		fBcdata.tSfbc2 = jdbcManager.from(TSfbc2.class)
				.id(fBcdata.idno, fBcdata.loDate)
				.getSingleResult();

		fBcdata.tSfbc3 = jdbcManager.from(TSfbc3.class)
				.id(fBcdata.idno, fBcdata.loDate)
				.getSingleResult();

		fBcdata.tSfbc4 = jdbcManager.from(TSfbc4.class)
				.id(fBcdata.idno, fBcdata.loDate)
				.getSingleResult();

		return fBcdata;
	}

	/**
	 * SF部品テーブルのエンティティを取得する
	 * @param clazz 取得したいエンティティのクラス
	 * @param idNo アイデントNo
	 * @return 指定したクラスのエンティティ
	 */
	public Object getTSfbc(Class<?> clazz, String idNo) {
		return jdbcManager.from(clazz)
			.where("idno = ?", idNo)
			.getSingleResult();
	}

	/**
	 * SF部品テーブルから部品セクションを取得します。
	 * @param idno アイデントNo
	 * @param loDate ラインオフ計画日
	 * @param tblName 取得先テーブル名
	 * @param colmunName 取得先列名
	 * @return 部品セクション
	 */
	public String getTSfbcValue(String idno, String loDate, String tblName, String colmunName) {
		//テーブル名から値の取得先を決定し、値を取得したエンティティを取得。
		//エンティティのもととなるクラスをClass型のclazzに
		//エンティティをObject型のsfbcに格納する。
		String tbl = tblName.trim().toLowerCase();
		String col = colmunName.trim().toLowerCase();
		String entityName = persistenceConvention.fromTableNameToEntityName(tbl);
		String fieldName = persistenceConvention.fromColumnNameToPropertyName(col);
		fieldName = persistenceConvention.fromPropertyNameToFieldName(fieldName);

		String entityPkg = "jp.co.ctc.entity";

		// TODO パッケージ名を取得するための検証用
		//logger.info("namingConvention.getEntityPackageName:" + namingConvention.getEntityPackageName());

		// クラスを取得
		Class<?> clazz = ClassUtil.forName(entityPkg + "." + entityName);

		// BCデータを取得
		Object sfbc = jdbcManager.from(clazz)
				.where(new SimpleWhere()
						.eq("idno", idno)
						.eq("loDate", loDate))
				.getSingleResult();

		// クラス名、フィールド名（＝列名）からフィールドを取得する。
		Field field = ClassUtil.getField(clazz, fieldName);

		// エンティティのフィールドから値を取得する。
		return FieldUtil.getString(field, sfbc);
	}


	/**
	 * 車両情報から指示記号を取得します。
	 *
	 * @param bcdata 車両情報
	 * @param tblName 取得先テーブル名
	 * @param colmunName 取得先列名
	 * @return 指示記号
	 */
	public String getTSfbcValue(FBcdata bcdata, String tblName, String colmunName) {
		//テーブル名から値の取得先を決定し、値を取得したエンティティを取得。
		//エンティティのもととなるクラスをClass型のclazzに
		//エンティティをObject型のsfbcに格納する。
		String tbl = tblName.trim().toLowerCase();
		String col = colmunName.trim().toLowerCase();
		String tSfbcPropertyName = persistenceConvention.fromColumnNameToPropertyName(tbl);
		// fromColumnNameToPropertyNameだと先頭の文字が大文字になってしまうため、
		// 先頭文字のみ小文字に変換。
		// FBcdata.tSfbc* のフィールド名が間違ってるかも？？
		String tSfbcFieldName = StringUtils.uncapitalize(persistenceConvention.fromPropertyNameToFieldName(tSfbcPropertyName));
		String msgnoPropertyName = persistenceConvention.fromColumnNameToPropertyName(col);
		String msgnoFieldName = persistenceConvention.fromPropertyNameToFieldName(msgnoPropertyName);

		Field fieldTSfbc = ClassUtil.getField(FBcdata.class, tSfbcFieldName);
		Object tSfbc = FieldUtil.get(fieldTSfbc, bcdata);
		Field fieldMsgno = ClassUtil.getField(tSfbc.getClass(), msgnoFieldName);
		String bcsign = FieldUtil.getString(fieldMsgno, tSfbc);

		// エンティティのフィールドから値を取得する。
		return bcsign;
	}


	/**
	 * 指定された検査項目（MsgNo）の記号に該当するBCデータを取得します。
	 * @param item 検索対象の検査項目
	 * @param specifiedSign 検索対象の記号
	 * @param startBody 検索を開始する車両
	 * @return 検索にヒットしたBCデータのリスト
	 */
	public List<FBcdata> getSpecifiedBodies(MItem item, String specifiedSign, FBcdata startBody) {
		//テーブル名から値の取得先を決定し、値を取得したエンティティを取得。
		//エンティティのもととなるクラスをClass型のclazzに
		//エンティティをObject型のsfbcに格納する。
		String tbl = item.tMsgno.tblname.trim().toLowerCase();
		String col = item.tMsgno.colname.trim().toLowerCase();
		String entityName = persistenceConvention.fromTableNameToEntityName(tbl);
		String fieldName = persistenceConvention.fromColumnNameToPropertyName(col);
		fieldName = persistenceConvention.fromPropertyNameToFieldName(fieldName);

		String entityPkg = "jp.co.ctc.entity";

		// クラスを取得
		Class<?> clazz = ClassUtil.forName(entityPkg + "." + entityName);

		// クラス名、フィールド名（＝列名）からフィールドを取得する。
		Field field = ClassUtil.getField(clazz, "fBcdata");

		// BCデータを取得
		List<?> sfbcList = jdbcManager.from(clazz)
				.innerJoin("fBcdata")
				.innerJoin("fBcdata.mVehicle")
				.where(new SimpleWhere()
						.eq("fBcdata.bctype", item.bctype)
						.eq(fieldName, specifiedSign)
						.ge("fBcdata.tpN0", startBody.tpN0))
				.orderBy("fBcdata.tpN0 ASC")
				.getResultList();

		// BCデータ格納用のList
		List<FBcdata> bcdataList = new ArrayList<FBcdata>();

		for (Object sfbc : sfbcList) {
			// エンティティのフィールドから値を取得する。
			bcdataList.add((FBcdata) FieldUtil.get(field, sfbc));
		}

		// エンティティのフィールドから値を取得する。
		return bcdataList;
	}
}
