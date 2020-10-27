package jp.co.ctc.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * 部品
 *
 * @author kaidu
 */
@Entity
public class LgMPart extends AbstractLgMPart implements Serializable {

	/**
	 * Serializableインタフェースの実装に必要。
	 */
	static final long serialVersionUID = 1L;

	/**
	 * 識別子
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer partCode;


	/**
	 * 関連エンティティ：取出順
	 */
	@OneToMany(mappedBy = "mPart")
	public List<LgMOrder> mOrderList;

	/**
	 * 関連エンティティ：指示記号
	 */
	@OneToMany(mappedBy = "mPart")
	public List<LgMBcsign> mBcsignList;

	/**
	 * 関連エンティティ：メッセージ
	 */
	@ManyToOne
	@JoinColumn(name = "msg_no", referencedColumnName = "msgno")
	public TMsgno tMsgno;

	/**
	 * 関連エンティティ：取出結果
	 */
	@OneToMany(mappedBy = "mPart")
	public List<LgFTakeresult> fTakeresultList;

	/**
	 * エンティティを配列に変換する。 CSV出力に使用
	 *
	 * @return 配列
	 */
	public Object[] toArray() {
		Object[] array = { this.mstVer, this.partCode, this.partName,
				this.msgDiv, this.msgNo, this.bcPosition, this.bcLength,
//				this.checkFlag, this.rackAddress,
				this.deleteFlag, this.sopFlag, this.insertUser,
				this.updateUser, this.insertDate, this.updateDate };
		return array;
	}
}
