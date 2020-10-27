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
 * 検査項目
 *
 * @author kaidu
 */
@Entity
public class MItem extends AbstractMItem implements Serializable {

	/**
	 * Serializableインタフェースの実装に必要。
	 */
	static final long serialVersionUID = 1L;

	/**
	 * 識別子
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer itemCode;


	/**
	 * 関連エンティティ：検査順
	 */
	@OneToMany(mappedBy = "mItem")
	public List<MOrder> mOrderL;

	/**
	 * 関連エンティティ：検査順
	 */
	@OneToMany(mappedBy = "mItem")
	public List<MOrder> mOrderR;

	/**
	 * 関連エンティティ：メッセージ
	 */
	@ManyToOne
	@JoinColumn(name = "msg_no", referencedColumnName = "msgno")
	public TMsgno tMsgno;

	/**
	 * 関連エンティティ：指示記号
	 */
	@OneToMany(mappedBy = "mItem")
	public List<MBcsign> mBcsignList;

	/**
	 * 関連エンティティ：ダミー車両、記号
	 */
	@OneToMany(mappedBy = "mItem")
	public List<FBcdataDummy> fBcdataDummy;

	// 2014/11/13 DA ins start
	/**
	 * 関連エンティティ：ダミー車両、記号
	 */
	@OneToMany(mappedBy = "mItem")
	public List<FResult> fResultList;
	// 2014/11/13 DA ins end

	// 2016/02/24 DA ins start
	/**
	 * 関連エンティティ：指示記号
	 */
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "mst_ver", referencedColumnName = "mst_ver"),
			@JoinColumn(name = "item_code", referencedColumnName = "item_code") })
	public MBcsign mBcsign;
	// 2016/02/24 DA ins end

	/**
	 * エンティティを配列に変換する。 CSV出力に使用
	 *
	 * @return 配列
	 */
	public Object[] toArray() {
		Object[] array = { this.mstVer, this.itemCode, this.bctype,
				this.itemName, this.resultDiv, this.msgDiv, this.msgNo,
				this.bcPosition, this.bcLength, this.tireDiv, this.okngDiv,
				this.tLimit, this.bLimit,
				this.deleteFlag, this.sopFlag, this.notes, this.insertUser,
				this.updateUser, this.insertDate, this.updateDate };
		return array;
	}

}
