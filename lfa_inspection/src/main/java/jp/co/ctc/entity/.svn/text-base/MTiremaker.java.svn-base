package jp.co.ctc.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * タイヤメーカーマスタ
 *
 * @author kato
 *
 */
@Entity
public class MTiremaker extends AbstractEntity implements Serializable {

	/**
	 * ?
	 */
	static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@Id
	public int id;

	/**
	 * タイヤメーカー略称
	 */
	public String TireMakerAbbreviation;

	/**
	 * タイヤメーカー名称
	 */
	public String TireMakerName;

	/**
	 * 削除フラグ
	 */
	public String deleteFlag;

	/**
	 * クラスの各要素をArrayで返します。
	 *
	 * @return array
	 */
	public Object[] toArray() {
		Object[] array = { this.id, this.TireMakerAbbreviation,
				this.TireMakerName, this.deleteFlag };
		return array;
	}

}
