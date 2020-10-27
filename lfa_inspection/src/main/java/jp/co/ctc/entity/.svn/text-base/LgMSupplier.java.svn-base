package jp.co.ctc.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 従業員マスタ
 *
 * @author sugihara
 *
 */
@Entity
public class LgMSupplier extends AbstractEntity implements Serializable {

	/**
	 * Serializableインタフェースの実装に必要。
	 */
	static final long serialVersionUID = 1L;

	/**
	 * 仕入先コード
	 */
	@Id
	public String supplierCode;

	/**
	 * 仕入先名
	 */
	public String supplierName;


//	/**
//	 * 関連エンティティ：搬入結果
//	 */
//	@OneToMany (mappedBy = "lgMSupplier")
//	public List<LgFStoreresult> lgFStoreresultList;

}
