package jp.co.ctc.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * 本番予約マスタ
 *
 * @author DA 2016/02/24
 */
@Entity
public class MReserve extends AbstractEntity implements Serializable
{
	/**
	 * ?
	 */
	static final long serialVersionUID = 1L;

	/**
	 * マスタバージョン
	 */
	@Id
	public Integer mstVer;

	/**
	 * BC車種区分コード
	 */
	@Id
	public String bctype;

	/**
	 * 予約フラグ
	 */
	public String reserveFlag;

	/**
	 * 予約者
	 */
	public String reserveUser;

	/**
	 * 予約日
	 */
	public Timestamp reserveDate;

	/**
	 * 本番登録方法
	 */
	public String registrationMethod;

	/**
	 * 関連エンティティ：車種
	 */
	@OneToOne(mappedBy = "mReserve")
	public MVehicle mVehicle;

	/**
	 * 関連エンティティ：予約者ユーザー
	 */
	@ManyToOne
	@JoinColumn(name = "reserve_user", referencedColumnName = "user_code")
	public MUser reserveMUser;

	/**
	 * 関連エンティティ：予約者ユーザー
	 */
	@OneToOne(mappedBy = "mReserve")
	public MUser mUser;

	/**
	 * エンティティを文字列に変換します。
	 * @return String配列
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[MReserve:");
		buffer.append(" mstVer: ");
		buffer.append(mstVer);
		buffer.append(" bctype: ");
		buffer.append(bctype);
		buffer.append(" reserveFlag: ");
		buffer.append(reserveFlag);
		buffer.append(" reserveUser: ");
		buffer.append(reserveUser);
		buffer.append(" reserveDate: ");
		buffer.append(reserveDate);
		buffer.append(" registrationMethod: ");
		buffer.append(registrationMethod);
		buffer.append("]");
		return buffer.toString();
	}
}
