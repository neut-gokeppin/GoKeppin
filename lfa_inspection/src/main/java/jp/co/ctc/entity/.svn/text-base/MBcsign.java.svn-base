package jp.co.ctc.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * 指示記号マスタ。
 *
 * @author kaidu
 */
@Entity
public class MBcsign extends AbstractMBcsign implements Serializable {

	/**
	 * Serializableインタフェースの実装に必要。
	 */
	static final long serialVersionUID = 1L;

	/**
	 * 識別子。
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer signCode;

	/**
	 * 関連エンティティ：検査項目。
	 */
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "mst_ver", referencedColumnName = "mst_ver"),
			@JoinColumn(name = "item_code", referencedColumnName = "item_code") })
	public MItem mItem;

	// 2016/02/24 DA ins start
	/**
	 * 関連エンティティ：検査項目
	 */
	@OneToMany (mappedBy = "mBcsign")
	public List<MItem> mItemList;
	// 2016/02/24 DA ins end

	// 2017/12/01 DA ins start
	/**
	 * 関連エンティティ：正解の指示記号
	 */
	@OneToMany(mappedBy = "mBcsignOk")
	public List<FResult> mBcsignOkList;

	/**
	 * 関連エンティティ：不正解の指示記号
	 */
	@OneToMany(mappedBy = "mBcsignNg")
	public List<FResult> mBcsignNgList;
	// 2017/12/01 DA ins end

	/**
	 * エンティティを配列に変換する。 CSV出力に使用
	 *
	 * @return 配列
	 */
	public Object[] toArray() {
		Object[] array = { this.mstVer, this.signCode, this.itemCode,
				this.bcSign, this.signContents, this.dummySign, this.signOrder,
				this.fileName, this.tLimit, this.bLimit,
				// 2016/02/24 DA ins start
				this.reserveFlag, this.reserveUser, this.reserveDate,
				this.sopDeleteFlag,
				// 2016/02/24 DA ins end
				this.deleteFlag, this.sopFlag, this.notes, this.insertUser,
				this.updateUser, this.insertDate, this.updateDate };
		return array;
	}
}