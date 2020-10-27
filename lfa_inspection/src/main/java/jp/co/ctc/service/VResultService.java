package jp.co.ctc.service;

import java.util.Date;
import java.util.List;
import jp.co.ctc.entity.VResult;
import jp.co.ctc.util.Utils;

import org.seasar.extension.jdbc.JdbcManager;

/**
 * 検査結果を扱うクラスです。
 *
 * @author CJ01785
 *
 */
public class VResultService {
	/**
	 * jdbcマネージャです。
	 */
	public JdbcManager jdbcManager;

	/**
	 * 初期ロード時に利用
	 *
	 * @return 全件
	 */
	public List<VResult> getVResultByKeys() {

		// "yyyy/MM/dd"形式の文字列をDateに変換します。
		Date fromDate = Utils.itemCastDate("1900/1/1");
		Date toDate = Utils.itemCastDate("9999/1/1");

		return this.getVResultByKeys("", "", fromDate, toDate);

	}

	/**
	 * 検索条件から検査結果を取得
	 * チェックボックスが押されなかった時
	 *
	 * @param bodyNo
	 *            ボデーNo
	 * @param groupName
	 *            グループ名
	 * @param startDate
	 *            検索From
	 * @param endDate
	 *            検索To
	 * @return VResultのList
	 */
	public List<VResult> getVResultByKeys(String bodyNo,
			 String groupName,  Date startDate,  Date endDate) {

		// ワイルドカード付与
		bodyNo = "%" + bodyNo + "%";
		groupName = "%" + groupName + "%";

		return jdbcManager.from(VResult.class).leftOuterJoin("mUser")
			.where(
				"bodyNo like ? AND group_name like ? AND inspec_date > ? "
						+ "AND inspec_date <= ?", bodyNo, groupName, startDate,
				endDate).orderBy("bodyNo,groupName").getResultList();

	}

	/**
	 * 検索条件から検査結果を取得
	 * チェックボックスが押されている時
	 *
	 * @param bodyNo
	 *            ボデーNo
	 * @param groupName
	 *            グループ名
	 * @param startDate
	 *            検索From
	 * @param endDate
	 *            検索To
	 * @param inspecResult
	 *            検索結果
	 * @return VResultのList
	 */
	public List<VResult> getVResultByKeys(final String bodyNo,
			final String groupName, final Date startDate, final Date endDate,
			final int inspecResult) {

		String bodyNO = bodyNo;
		String groupname = groupName;

		// ワイルドカード付与
		bodyNO = "%" + bodyNO + "%";
		groupname = "%" + groupname + "%";

		return jdbcManager.from(VResult.class).leftOuterJoin("mUser")
		.where(
				"body_no like ? AND group_name like ? AND inspec_date > ? "
						+ "AND inspec_date <= ? AND inspec_result = ?", bodyNO,
				groupname, startDate, endDate, inspecResult).orderBy(
				"body_no,group_name").getResultList();

	}
}
