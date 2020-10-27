package jp.co.ctc.service;

import org.seasar.extension.jdbc.JdbcManager;

import jp.co.ctc.util.Utils;
import jp.co.ctc.entity.LgFTakeresult;
import jp.co.ctc.entity.LgVResult;

import java.util.Date;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author CJ01785
 *
 */
public class LgVResultService {

	/**
	 * JdbcManagerを使います
	 */
	public JdbcManager jdbcManager;

	/**
	 * 初期表示として取出結果を全件取得します
	 *
	 * @return 取出結果
	 */
	public List<LgVResult> getVResult() {

		// "yyyy/MM/dd"形式の文字列をDateに変換します。
		Date fromDate = Utils.itemCastDate("1900/1/1");
		Date toDate = Utils.itemCastDate("9999/1/1");

		return this.getVResult(fromDate, toDate, "", "", "", "");
	}

	/**
	 * 検索対象の取出結果を返します。
	 *
	 * @param fromDate
	 *            検索対象日時from
	 * @param toDate
	 *            検索対象日時to
	 * @param carNum
	 *            号車No
	 * @param takeUser
	 *            取出者
	 * @param rackError
	 *            棚チェック
	 * @param partError
	 *            部品チェック
	 * @return 検索対象の取出結果
	 */
	public List<LgVResult> getVResult(Date fromDate, Date toDate,
			String carNum, String takeUser, String rackError, String partError) {

		Date endDate = new Date();
		// 検索対象終了日に１日加える(そのままではその日の"00:00:00"であるため)
		endDate.setTime(toDate.getTime() + 86400000L);

		carNum = carNum + "%";
		takeUser = takeUser + "%";
		rackError = rackError + "%";
		partError = partError + "%";

		return jdbcManager.from(LgVResult.class)
				.leftOuterJoin("lgMUser")
				.where("rackDate > ? AND rackDate < ? "
								+ "AND comment1 like ? "
								+ "AND lgMUser.userName like ? "
								+ "AND CAST(rackResult as CHAR) like ? "
								+ "AND CAST(partResult as CHAR) like ?", fromDate, endDate,
								carNum, takeUser, rackError, partError).getResultList();
	}

	/**
	 * ボデーNo、受信日、部品Codeから、取出結果を取出回数の降順に取得する。
	 *
	 * @param bodyNo
	 *            ボデーNo.
	 * @param recvDay
	 *            受信日
	 * @param partCode
	 *            部品Code
	 * @return 取得した取出結果のリスト
	 */
	public List<LgFTakeresult> getFTakeresultByKeys(String bodyNo,
			String recvDay, Integer partCode) {
		return jdbcManager.from(LgFTakeresult.class).where(
				"bodyNo = ? AND recvDay = ? AND partCode = ?", bodyNo, recvDay,
				partCode).orderBy("takeNo Desc").getResultList();
	}

	/**
	 * 取出結果をテーブルに挿入します。
	 *
	 * @param result
	 *            取出結果
	 * @return 識別子が設定された後の取出結果
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean create(LgFTakeresult result) {
		try {
			jdbcManager.insert(result).excludes("sopFlag", "deleteFlag")
					.execute();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
