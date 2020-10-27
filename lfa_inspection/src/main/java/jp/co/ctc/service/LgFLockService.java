/**
 *
 */
package jp.co.ctc.service;

import java.sql.Timestamp;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import jp.co.ctc.entity.LgFLock;

import org.seasar.extension.jdbc.JdbcManager;

/**
 * 排他テーブルを扱うサービスです。
 *
 * @author CJ01786
 *
 */
public class LgFLockService {

	/**
	 * JDBCマネージャです。
	 */
	public JdbcManager jdbcManager;

	/**
	 * 排他テーブルを取得します。
	 * @return 取得した排他テーブルのリスト
	 */
	public List<LgFLock> getLgFLock() {
		return jdbcManager.from(LgFLock.class)
			.getResultList();

	}

	/**
	 * ボデーNo. 受信日 SPS台車コードからデータを取得する
	 * @param bodyNo ボデーNo.
	 * @param recvDay 受信日
	 * @param spsCode SPS台車コード
	 * @return 取得した排他テーブルのリスト
	 */
	public List<LgFLock> getLgFLock(String bodyNo, String recvDay, Integer spsCode) {
		return jdbcManager.from(LgFLock.class)
			.where("bodyNo = ? AND recvDay = ? AND spsCode = ?",
					bodyNo,
					recvDay,
					spsCode.toString()
					)
			.getResultList();

	}

	/**
	 * 排他情報をテーブルに挿入します。
	 * @param item 挿入する排他情報
	 * @return 挿入の成功可否
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean create(LgFLock item) {
		try {
			// 更新レコードにセットするタイムスタンプ
			Timestamp timestamp = new Timestamp(new java.util.Date().getTime());
			item.insertDate = timestamp;
			item.insertUser = item.userCode;
			jdbcManager.insert(item).execute();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	/**
	 * 排他情報をテーブルから削除します。
	 *
	 * @param bodyNo ボデーNo
	 * @param recvDay 受信日
	 * @param spsCode SPS台車コード
	 * @return 削除が成功したかどうか
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean remove(String bodyNo, String recvDay, Integer spsCode) {
		List<LgFLock> listLock = jdbcManager.from(LgFLock.class)
				.where("bodyNo = ? AND recvDay = ? AND spsCode = ?",
						bodyNo,
						recvDay,
						spsCode.toString()
						)
				.getResultList();
		if (listLock.size() == 0) {
			//ロックがなければ失敗
			return false;
		} else {
			return jdbcManager.delete(listLock.get(0)).execute() == 1;
		}
	}
}
