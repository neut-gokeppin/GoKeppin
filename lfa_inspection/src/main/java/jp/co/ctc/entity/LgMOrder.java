/**
 *
 */
package jp.co.ctc.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import jp.co.ctc.service.CsvService;

/**
 * @author kaidu
 *
 */
@Entity
public class LgMOrder extends AbstractEntity implements Serializable {

	/**
	 * Serializableインタフェースの実装に必要。
	 */
	static final long serialVersionUID = 1L;

	/**
	 * マスタバージョン
	 */
	@Id
	public Integer mstVer;
	/**
	 * sps識別子
	 */
	public Integer spsCode;

	/**
	 * 取出し順
	 */
	public Integer takeOrder;

	/**
	 * 部品識別子
	 */
	@Id
	public Integer partCode;

	/**
	 * 号口フラグ
	 */
	public String sopFlag;

	/**
	 * 編集区分
	 */
	@Transient
	public String editDiv;

	/**
	 * 関連エンティティ：部品
	 */
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "mst_ver", referencedColumnName = "mst_ver"),
			@JoinColumn(name = "part_code", referencedColumnName = "part_code") })
	public LgMPart mPart; // LgMPart.mOrderListのmappedByの値と変数名を合わせる

	/**
	 * 関連エンティティ：SPS台車
	 */
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "mst_ver", referencedColumnName = "mst_ver"),
			@JoinColumn(name = "sps_code", referencedColumnName = "sps_code") })
	public LgMSps mSps; // LgMSps.mOrderListのmappedByの値と変数名を合わせる

	/**
	 * 関連エンティティ：BCデータ
	 */
	//	@ManyToOne
	//@JoinColumns({
	//		@JoinColumn(name = "mst_ver", referencedColumnName = "mst_ver"),
	//		@JoinColumn(name = "ptn_div", referencedColumnName = "ptn_div") })
	//public FBcdata fBcdata;

	/**
	 * 関連エンティティ：検査結果
	 */
	//@OneToMany(mappedBy = "mOrder")
	//public List<FResult> fResultList;

	/**
	 * エンティティを配列に変換する。 CSV出力に使用
	 *
	 * @return 配列
	 */
	public Object[] toArray() {
		Object[] array = { this.mstVer, this.spsCode, this.takeOrder,
				this.partCode, this.sopFlag, this.insertUser,
				this.updateUser, this.insertDate, this.updateDate };
		return array;
	}

	/**
	 *
	 * @param item 値の格納されたオブジェクト配列
	 * @return 生成されたMOrder型のインスタンス
	 */
	public LgMOrder getClass(Object[] item) {

		LgMOrder od = new LgMOrder();

		od.mstVer = CsvService.itemCastInteger(item[1]);
		od.spsCode = CsvService.itemCastInteger(item[2]);
		od.takeOrder = CsvService.itemCastInteger(item[3]);
		od.partCode = CsvService.itemCastInteger(item[4]);
		od.sopFlag = CsvService.itemCastString(item[5]);
		od.insertUser = CsvService.itemCastString(item[6]);
		od.updateUser = CsvService.itemCastString(item[7]);

		od.insertDate = CsvService.itemCastTimestamp(item[8]);
		od.updateDate = CsvService.itemCastTimestamp(item[9]);

		return od;
	}

}
