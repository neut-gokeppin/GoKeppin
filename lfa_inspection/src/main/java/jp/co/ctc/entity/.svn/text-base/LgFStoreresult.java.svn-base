package jp.co.ctc.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * 搬入結果を扱うクラスです。
 *
 * @author CJ01785
 *
 */
@Entity
public class LgFStoreresult implements Serializable {

	/**
	 * Serializableインタフェースの実装に必要。
	 */
	static final long serialVersionUID = 1L;

	/**
	 * 部品QRコード
	 */
	@Id
	public String partQrcode;
	/**
	 * 棚QRコード
	 */
	public String rackQrcode;
	/**
	 * 部品読取時刻
	 */
	public Timestamp partReaddate;
	/**
	 * 棚読取時刻
	 */
	public Timestamp rackReaddate;

	/**
	 * 棚照合結果
	 */
	public String resultDiv;

	/**
	 * 担当者Code
	 */
	public String storeUser;

	/**
	 * 関連エンティティ：従業員テーブル
	 */
	@ManyToOne
	@JoinColumns({ @JoinColumn(name = "store_user", referencedColumnName = "user_code") })
	public LgMUser lgMUser;

	/**
	 * 関連エンティティ：取出結果テーブル
	 */
	@ManyToOne
	@JoinColumns({ @JoinColumn(name = "part_qrcode", referencedColumnName = "part_qrcode") })
	public LgFTakeresult lgFTakeresult;

}
