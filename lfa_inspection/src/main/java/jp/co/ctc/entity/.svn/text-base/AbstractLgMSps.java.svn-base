package jp.co.ctc.entity;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import jp.co.ctc.util.ServletUtil.State;

/**
 * SPS台車
 *
 * @author kaidu
 *
 */
@MappedSuperclass
public abstract class AbstractLgMSps extends AbstractEntity implements Serializable {

	/**
	 * Serializableインタフェースの実装に必要。
	 */
	static final long serialVersionUID = 1L;

	/**
	 * マスタバージョン
	 */
	@Id
	public Integer mstVer;

	/**
	 * SPSNo
	 */
	public String spsNo;

	/**
	 * 名前
	 */
	public String spsName;

	/**
	 * 号口フラグ
	 */
	public String sopFlag;

	/**
	 * 削除フラグ
	 */
	public String deleteFlag;

	/**
	 * 編集区分
	 */
	@Transient
	public String editDiv;

	/**
	 * SPS台車状態
	 */
	@Transient
	public State spsState;

}