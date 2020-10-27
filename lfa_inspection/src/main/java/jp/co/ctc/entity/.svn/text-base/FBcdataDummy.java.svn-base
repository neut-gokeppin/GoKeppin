package jp.co.ctc.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;


/**
 * ダミーチェック用の車両、記号を取り扱うエンティティ
 *
 * @author T.Kaizu
 *
 */
@Entity
public class FBcdataDummy implements Serializable {

	/**
	 * Serializableインタフェースの実装に必要。
	 */
	static final long serialVersionUID = 1L;

	/**
	 * アイデントNo
	 */
	@Id
	public String idno;

	/**
	 * ラインオフ計画日
	 */
	@Id
	public String loDate;

//	/**
//	 * フレーム区分
//	 */
//	@Id
//	public String frameCode;
//
//	/**
//	 * フレーム連番
//	 */
//	@Id
//	public String frameSeq;

	/**
	 * マスタバージョン
	 */
	@Id
	public Integer mstVer;

	/**
	 * 検査項目コード
	 */
	@Id
	public Integer itemCode;

	/**
	 * 正解記号
	 */
	@Transient
	public String specifiedSign;

	/**
	 * 正解の検査内容
	 */
	@Transient
	public String specifiedContents;

	/**
	 * ダミー記号
	 */
	public String dummySign;

	/**
	 * ダミーの検査内容
	 */
	@Transient
	public String dummyContents;

	/**
	 * 更新者
	 */
	public String updateUser;

	/**
	 * 更新日時
	 */
	public Timestamp updateDate;


	/**
	 * 関連エンティティ：BCデータ
	 */
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "idno", referencedColumnName = "idno"),
		@JoinColumn(name = "lo_date", referencedColumnName = "lo_date")
	})
	public FBcdata fBcdata;

	/**
	 * 関連エンティティ：検査項目
	 */
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "mst_ver", referencedColumnName = "mst_ver"),
		@JoinColumn(name = "item_code", referencedColumnName = "item_code")
	})
	public MItem mItem;

	/**
	 * 関連エンティティ：ユーザー
	 */
	@ManyToOne
	@JoinColumn(name = "update_user", referencedColumnName = "user_code")
	public MUser updateMUser;
}
