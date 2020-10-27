package jp.co.ctc.entity;

/* Code Generator Information.
 * generator Version 1.0.0 release 2007/10/10
 * generated Date Thu Sep 09 20:41:17 JST 2010
 */
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * 検査結果を扱うクラスです。
 * @author CJ01785
 *
 */
@Entity
public class VResult implements Serializable {

	/**
	 * Serializableインタフェースの実装に必要。
	 */
	static final long serialVersionUID = 1L;

	/**
	 * bodyNo:bpchar(5)
	 */
	public String bodyNo;

	/**
	 * groupName:varchar(30)
	 */
	public String groupName;

	/**
	 * inspecResult:bpchar(1)
	 */
	public String inspecResult;

	/**
	 * inspecUser:bpchar(7)
	 */
	public String inspecUser;

	/**
	 * inspecDate:timestamp(29,6)
	 */
	public java.sql.Timestamp inspecDate;

	/**
	 * グループコード
	 */
	public String groupCode;

	/**
	 * 受信日
	 */
	public String recvDay;

	/**
	 * Constractor
	 */
	public VResult() {
	}

	/**
	 * 関連エンティティ:従業員マスタ
	 */
	@ManyToOne
	@JoinColumn (name = "inspec_user", referencedColumnName = "user_code")
	public MUser mUser;

}
