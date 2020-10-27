package jp.co.ctc.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 取出結果を扱うクラスです。
 * @author CJ01785
 *
 */
@Entity
public class LgFTakeresult implements Serializable {

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
	 * 部品Code
	 */
	@Id
	public Integer partCode;
	/**
	 * 取出回数
	 */
	@Id
	public int takeNo;
	/**
	 * 棚チェック結果
	 */
	public String rackResult;
	/**
	 * 部品チェック結果
	 */
	public String partResult;
	/**
	 * 作成者
	 */
	public String takeUser;
	/**
	 * 棚照合時間
	 */
	public Timestamp rackDate;
	/**
	 * 部品チェック時間
	 */
	public Timestamp partDate;
	/**
	 * 部品QRコード
	 */
	public String partQrcode;
	/**
	 * 棚QRコード
	 */
	public String rackQrcode;

	/**
	 * 関連エンティティ：従業員テーブル
	 */
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "take_user", referencedColumnName = "user_code") })
	public LgMUser lgMUser;

	/**
	 * 関連エンティティ：部品マスタ
	 */
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "part_code", referencedColumnName = "part_code") })
	public LgMPart mPart;

	/**
	 * 関連エンティティ：指示記号マスタ
	 */
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "part_code", referencedColumnName = "part_code") })
	public LgMBcsign mBcsign;

	/**
	 * 関連エンティティ：BCデータ
	 */
	@ManyToOne
	@JoinColumns ({
		@JoinColumn (name = "body_no", referencedColumnName = "body_no"),
		@JoinColumn (name = "recv_day", referencedColumnName = "recv_day") })
	public FBcdata fBcdata; // FBcData.fResultListのmappedByの値と変数名を合わせる

	/**
	 * 関連エンティティ：搬入結果テーブル
	 */
	@OneToMany (mappedBy = "lgFTakeresult")
	public List<LgFStoreresult> lgFStoreresultList;

}
