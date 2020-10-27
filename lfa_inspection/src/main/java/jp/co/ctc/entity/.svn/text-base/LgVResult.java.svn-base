package jp.co.ctc.entity;


import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
/**
 * 取出し結果を扱うクラスです。
 * @author CJ01785
 *
 */
@Entity
public class LgVResult implements Serializable {

	/**
	 * Serializableインタフェースの実装に必要。
	 */
	static final long serialVersionUID = 1L;

	/**
	 * ボデーNo
	 */
	public String bodyNo;

	/**
	 * 受信日
	 */
	public String recvDay;

	/**
	 * SPS台車コード
	 */
	public int spsCode;

	/**
	 * 取出者
	 */
	public String takeUser;

	/**
	 * 棚読込時間
	 */
	public Timestamp rackDate;

	/**
	 * 部品読込時間
	 */
	public Timestamp partDate;

	/**
	 * 棚チェック
	 */
	public int rackResult;

	/**
	 * 部品チェック
	 */
	public int partResult;

	/**
	 * SPS台車名
	 */
	public String spsName;

	/**
	 * 号車No
	 */
	public String comment1;

	/**
	 * Constractor
	 */
	public LgVResult() { }

	/**
	 * 関連エンティティ：従業員マスタ
	 */
	@ManyToOne
	@JoinColumn (name = "take_user", referencedColumnName = "user_code")
	public LgMUser lgMUser;

}
