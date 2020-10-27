package jp.co.ctc.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;


/**
 * タイヤメーカークラス
 *
 * @author NEUT
 *
 */
@Entity
public class LgMTireMaker extends AbstractEntity implements Serializable {

	/**
	 * Serializableインタフェースの実装に必要。
	 */
	static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@Id
	public String id;

	/**
	 * タイヤメーカー略称
	 */
	public String tire_maker_abbreviation;

	/**
	 * タイヤメーカー名称
	 */
	public String tire_maker_name;

	/**
	 * 削除フラグ
	 */
	public String delete_flag;


	/**
	 * 関連エンティティ：搬入結果
	 */
	@OneToMany (mappedBy = "lgMTireMaker")
	public List<LgFStoreresult> lgFStoreresultList;

	/**
	 * 関連エンティティ：取出結果
	 */
	@OneToMany (mappedBy = "lgMTireMaker")
	public List<LgFTakeresult> lgFTakeresultList;

	/**
	 * 関連エンティティ：取出結果(View)
	 */
	@OneToMany (mappedBy = "lgMTireMaker")
	public List<LgVResult> lgVResultList;

	/**
	 * クラスの各要素をArrayで返します。
	 * @return array
	 */
	public Object[] toArray() {
		Object[] array = { this.tire_maker_abbreviation, this.tire_maker_name, this.delete_flag };
		return array;
	}

}
