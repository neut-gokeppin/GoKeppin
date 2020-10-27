package jp.co.ctc.entity;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * 指示記号マスタ。
 *
 * @author kaidu
 */
@MappedSuperclass
public class AbstractLgMBcsign extends AbstractEntity implements Serializable {

	/**
	 * Serializableインタフェースの実装に必要。
	 */
	static final long serialVersionUID = 1L;

	/**
	 * マスタバージョン。
	 */
	@Id
	public Integer mstVer;

	/**
	 * 部品コード。
	 */
	public Integer partCode;

	/**
	 * BC記号。
	 */
	public String bcSign;

	/**
	 *
	 */
	public String supplierName;

	/**
	 *
	 */
	public String backNo;

	/**
	 *
	 */
	public String partNo;

	/**
	 *
	 */
	public String identName;

	/**
	 * ファイル名。
	 */
	public String fileName;


	/**
	 * 不要フラグ
	 */
	public boolean notuseFlag;

	/**
	 * 削除フラグ。
	 */
	public String deleteFlag;

	/**
	 * 号口フラグ。
	 */
	public String sopFlag;

	/**
	 * 棚照合フラグ
	 */
	public Boolean checkFlag;

	/**
	 * 所番地
	 */
	public String rackAddress;

}