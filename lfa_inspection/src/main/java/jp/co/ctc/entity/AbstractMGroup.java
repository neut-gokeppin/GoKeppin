package jp.co.ctc.entity;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import jp.co.ctc.util.ServletUtil.State;

/**
 * 検査グループ
 *
 * @author kaidu
 *
 */
@MappedSuperclass
public class AbstractMGroup extends AbstractEntity implements Serializable {

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
	 * BC車種区分コード
	 */
	public String bctype;

	/**
	 * 工程No
	 */
	public String groupNo;

	/**
	 * 工程名称
	 */
	public String groupName;

	// 2014/04/07 DA ins start
	/**
	 * ライン
	 */
	public String line;

	/**
	 * エリア
	 */
	public String area;
	// 2014/04/07 DA ins end

	// 2016/02/24 DA ins start
	/**
	 * PDA非表示フラグ
	 */
	public Boolean nonDisplayFlag;
	// 2016/02/24 DA ins end

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
	 * グループ状態
	 */
	@Transient
	public State groupState;

	/**
	 * 関連エンティティ：車種
	 */
	@ManyToOne
	@JoinColumn(name = "bctype", referencedColumnName = "bctype")
	public MVehicle mVehicle; // MVehicle.mGroupListのmappedByの値と変数名を合わせる

}