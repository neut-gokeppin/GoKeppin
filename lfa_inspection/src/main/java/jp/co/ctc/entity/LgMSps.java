package jp.co.ctc.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;


/**
 * SPS台車
 *
 * @author kaidu
 *
 */
@Entity
public class LgMSps extends AbstractLgMSps implements Serializable {

	/**
	 * Serializableインタフェースの実装に必要。
	 */
	static final long serialVersionUID = 1L;

	/**
	 * 識別子
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer spsCode;

	/**
	 * エンティティを配列に変換する。
	 * CSV出力に使用
	 *
	 * @return 配列
	 */
	public Object[] toArray() {
		Object[] array = {
			this.mstVer,
			this.spsCode,
			this.spsNo,
			this.spsName,
			this.deleteFlag,
			this.sopFlag,
			this.insertUser,
			this.updateUser,
			this.insertDate,
			this.updateDate,
			};
		return array;
	}

	/**
	 * 関連エンティティ：取出順
	 */
	@OneToMany (mappedBy = "mSps")
	public List<LgMOrder> mOrderList;
}