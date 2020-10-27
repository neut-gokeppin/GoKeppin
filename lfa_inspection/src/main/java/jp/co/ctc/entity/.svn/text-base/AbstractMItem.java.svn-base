package jp.co.ctc.entity;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * 検査項目
 *
 * @author kaidu
 */
@MappedSuperclass
public class AbstractMItem extends AbstractEntity implements Serializable {

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
	 * BC車種区分
	 */
	public String bctype;

	/**
	 * 名前
	 */
	public String itemName;

	/**
	 * 結果区分
	 */
	public String resultDiv;

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
	 * タイヤメーカー検査
	 */
	public Boolean tireDiv;

	/**
	 * 一択検査（OK/NG検査）
	 */
	public Boolean okngDiv;

	/**
	 * 基準値上限度
	 */
	public String tLimit;

	/**
	 * 基準値下限度
	 */
	public String bLimit;

	/**
	 * 削除フラグ
	 */
	public String deleteFlag;

	/**
	 * 号口フラグ
	 */
	public String sopFlag;

	/**
	 * 備考
	 */
	public String notes;

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
