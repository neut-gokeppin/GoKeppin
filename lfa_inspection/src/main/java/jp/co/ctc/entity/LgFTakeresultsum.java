package jp.co.ctc.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * 取出結果を扱うクラスです。
 * @author CJ01729
 *
 */
@Entity
public class LgFTakeresultsum implements Serializable {

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
	 * SPS台車コード
	 */
	@Id
	public Integer spsCode;

	/**
	 * SPS台車のステータス
	 */
	public Integer spsStatus;

	/**
	 * 作成者　ユーザーコード
	 */
	public String insertUser;

	/**
	 * 更新者　ユーザーコード
	 */
	public String updateUser;

	/**
	 * 作成日時
	 */
	public Timestamp insertDate;

	/**
	 * 更新日時
	 */
	public Timestamp updateDate;

	/**
	 * 関連エンティティ：BCデータ
	 */
	@ManyToOne
	@JoinColumns ({
		@JoinColumn (name = "body_no", referencedColumnName = "body_no"),
		@JoinColumn (name = "recv_day", referencedColumnName = "recv_day") })
	public FBcdata fBcdata; // FBcdata.fTakeResultsumListのmappedByの値と変数名を合わせる
}
