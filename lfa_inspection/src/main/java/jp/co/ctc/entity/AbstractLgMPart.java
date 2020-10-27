package jp.co.ctc.entity;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * 部品
 *
 * @author kaidu
 */
@MappedSuperclass
public abstract class AbstractLgMPart extends AbstractEntity implements Serializable {

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
	 * 名前
	 */
	public String partName;

	/**
	 * 固定区分
	 */
	public Boolean msgDiv;

	/**
	 * メッセージNo
	 */
	public String msgNo;

	/**
	 * 開始位置
	 */
	public Integer bcPosition;

	/**
	 * 桁数
	 */
	public Integer bcLength;

	/**
	 * 棚照合フラグ
	 */
//  AbstractLgMPartに移動
//	public Boolean checkFlag;

	/**
	 * 所番地
	 */
//  AbstractLgMPartに移動
//	public String rackAddress;

	/**
	 * 削除フラグ
	 */
	public String deleteFlag;

	/**
	 * 号口フラグ
	 */
	public String sopFlag;

	/**
	 * 編集区分
	 */
	@Transient
	public String editDiv;

	/**
	 * 画面表示更新用変数
	 */
	@Transient
	public int dummy;




}
