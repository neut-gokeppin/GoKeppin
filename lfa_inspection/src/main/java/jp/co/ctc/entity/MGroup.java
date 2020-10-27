package jp.co.ctc.entity;


import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * 検査グループ
 *
 * @author kaidu
 *
 */
@Entity
public class MGroup extends AbstractMGroup implements Serializable {
	/**
	 * Serializableインタフェースの実装に必要。
	 */
	static final long serialVersionUID = 1L;

	/**
	 * 識別子
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer groupCode;

	/**
	 * 関連エンティティ：検査順
	 */
	@OneToMany (mappedBy = "mGroup")
	public List<MOrder> mOrderList;

	/**
	 * 関連エンティティ : FResultsum
	 */
	@OneToMany (mappedBy = "mGroup")
	public List<FResultsum> fResultsumList;


	/**
	 * エンティティを配列に変換する。
	 * CSV出力に使用
	 *
	 * @return 配列
	 */
	public Object[] toArray() {
		Object[] array = {
			this.mstVer,
			this.groupCode,
			this.bctype,
			this.groupNo,
			this.groupName,
			// 2014/04/07 DA ins start
			this.line,
			this.area,
			// 2014/04/07 DA ins end
			// 2016/02/24 DA ins start
			this.nonDisplayFlag,
			// 2016/02/24 DA ins end
			this.deleteFlag,
			this.sopFlag,
			this.insertUser,
			this.updateUser,
			this.insertDate,
			this.updateDate,
			};
		return array;
	}

}