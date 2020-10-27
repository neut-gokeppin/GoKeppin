/**
 *
 */
package jp.co.ctc.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 排他テーブルのエンティティ
 *
 * @author sugihara
 *
 */
@Entity
public class LgFLock implements Serializable {

	/**
	 * Serializableインタフェースの実装に必要。
	 */
	static final long serialVersionUID = 1L;

	/**
	 * ボデーNo.
	 */
	@Id
	public String bodyNo;

	/**
	 * 受信日
	 */
	@Id
	public String recvDay;

	/**
	 * SPSコード
	 */
	@Id
	public String spsCode;

	/**
	 * 従業員コード
	 */
	public String userCode;


	/**
	 * 作成者
	 */
	public String insertUser;

	/**
	 * 作成日時
	 */
	public Timestamp insertDate;

}
