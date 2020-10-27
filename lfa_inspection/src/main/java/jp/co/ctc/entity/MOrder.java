/**
 *
 */
package jp.co.ctc.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import jp.co.ctc.service.CsvService;

/**
 * @author kaidu
 *
 */
@Entity
public class MOrder extends AbstractEntity implements Serializable {

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
	 * グループ識別子
	 */
	public Integer groupCode;

	/**
	 * パターン（左H, 右H）
	 */
	@Id
	public String ptnDiv;

	/**
	 * 検査順
	 */
	public Integer inspecOrder;

	/**
	 * 検査項目識別子
	 */
	@Id
	public Integer itemCode;

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
	 * 関連エンティティ：検査項目
	 */
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "mst_ver", referencedColumnName = "mst_ver"),
			@JoinColumn(name = "item_code", referencedColumnName = "item_code") })
	public MItem mItem; // MItem.mOrderListのmappedByの値と変数名を合わせる

	/**
	 * 関連エンティティ：検査グループ
	 */
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "mst_ver", referencedColumnName = "mst_ver"),
			@JoinColumn(name = "group_code", referencedColumnName = "group_code") })
	public MGroup mGroup; // MGroup.mOrderListのmappedByの値と変数名を合わせる

	/**
	 * 関連エンティティ：検査結果サマリ
	 */
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "mst_ver", referencedColumnName = "mst_ver"),
			@JoinColumn(name = "group_code", referencedColumnName = "group_code") })
	public FResultsum fResultsum; // FResultsum.mOrderListのmappedByの値と変数名を合わせる

	/**
	 * 関連エンティティ：検査結果
	 */
	@OneToMany(mappedBy = "mOrder")
	public List<FResult> fResultList;

	// 2017/12/01 DA ins start
	/**
	 * 関連エンティティ：撮影画像
	 * 使用する場合はマスタバージョンとパターン区分を結合条件を付けること
	 */
	@OneToOne
	@JoinColumns({
			@JoinColumn(name = "item_code", referencedColumnName = "item_code") })
	public FShotimage fShotimageMOrder; // mappedByの値と変数名を合わせる
	// 2017/12/01 DA ins end

	/**
	 * エンティティを配列に変換する。 CSV出力に使用
	 *
	 * @return 配列
	 */
	public Object[] toArray() {
		Object[] array = { this.mstVer, this.groupCode, this.ptnDiv,
				this.inspecOrder, this.itemCode, this.sopFlag, this.insertUser,
				this.updateUser, this.insertDate, this.updateDate };
		return array;
	}

	/**
	 *
	 * @param item 値の格納されたオブジェクト配列
	 * @return 生成されたMOrder型のインスタンス
	 */
	public MOrder getClass(Object[] item) {

		MOrder od = new MOrder();

		int i = 0;
		od.mstVer = CsvService.itemCastInteger(item[++i]);
		od.groupCode = CsvService.itemCastInteger(item[++i]);
		od.ptnDiv = CsvService.itemCastString(item[++i]);
		od.inspecOrder = CsvService.itemCastInteger(item[++i]);
		od.itemCode = CsvService.itemCastInteger(item[++i]);
		od.sopFlag = CsvService.itemCastString(item[++i]);
		od.insertUser = CsvService.itemCastString(item[++i]);
		od.updateUser = CsvService.itemCastString(item[++i]);

		od.insertDate = CsvService.itemCastTimestamp(item[++i]);
		od.updateDate = CsvService.itemCastTimestamp(item[++i]);

		return od;
	}

}
