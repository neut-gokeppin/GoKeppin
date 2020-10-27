/**
 *
 */
package jp.co.ctc.service;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.transaction.UserTransaction;

/**
 * トランザクション制御を行うサービスクラス
 *
 * @author CJ01786
 *
 */
public class TransactionService {

	/**
	 * トランザクション
	 */
	public UserTransaction userTransaction;

	/**
	 * トランザクションが開始しているか。
	 */
	private boolean tranStarted;

	/**
	 * コンストラクタ
	 */
	public TransactionService() {
		this.tranStarted = false;
	}

	/**
	 * トランザクションの開始状態を取得する。
	 * @return true:トランザクション実行中　false:トランザクションなし
	 */
	public boolean isStarted() {
		return tranStarted;
	}

	/**
	 * トランザクションを開始します。
	 * @return 成功の可否
	 */
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public boolean startTransaction() {
		try {
			if (this.tranStarted) {
				//すでにトランザクションを開始していた場合失敗とする。
				return false;
			}
			userTransaction.begin();
			this.tranStarted = true;
			return true;
		} catch (Exception e) {
			this.tranStarted = false;
			return false;
		}
	}

	/**
	 * コミットします。
	 * @return 成功の可否。
	 */
	@TransactionAttribute(TransactionAttributeType.MANDATORY)
	public boolean commit() {
		try {
			if (!this.tranStarted) {
				//トランザクションが開始されていない場合失敗とする。
				return false;
			}
			userTransaction.commit();
			this.tranStarted = false;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * ロールバックします。
	 * @return 成功の可否
	 */
	@TransactionAttribute(TransactionAttributeType.MANDATORY)
	public boolean rollback() {
		try {
			if (!this.tranStarted) {
				//トランザクションが開始されていない場合失敗とする。
				return false;
			}
			userTransaction.rollback();
			this.tranStarted = false;
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
