package jp.co.ctc.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * 指示記号マスタ。
 *
 * @author kaidu
 */
@Entity
public class LgMBcsign extends AbstractLgMBcsign implements Serializable {

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
			@JoinColumn(name = "part_code", referencedColumnName = "part_code") })
	public LgMPart mPart;

	/**
	 * エンティティを配列に変換する。 CSV出力に使用
	 *
	 * @return 配列
	 */
	public Object[] toArray() {
		Object[] array = { this.mstVer, this.signCode, this.partCode, this.bcSign,
				this.supplierName, this.backNo, this.partNo, this.identName,
				this.fileName, this.notuseFlag, this.deleteFlag, this.sopFlag, this.insertUser,
				this.updateUser, this.insertDate, this.updateDate, this.checkFlag, this.rackAddress };
		return array;
	}
}