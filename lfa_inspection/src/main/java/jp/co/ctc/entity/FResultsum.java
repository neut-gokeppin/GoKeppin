package jp.co.ctc.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 * 検査結果を扱うクラスです。
 * @author CJ01729
 *
 */
@Entity
public class FResultsum implements Serializable {

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
	 * グループコード
	 */
	@Id
	public Integer groupCode;

	/**
	 * ボデーNo.
	 */
	public String bodyNo;

	/**
	 * 受信日
	 */
	public String recvDay;

	/**
	 * マスタバージョン
	 */
	public Integer mstVer;

	/**
	 * グループのステータス
	 */
	public Integer groupStatus;

	/**
	 * 作成者　ユーザーコード
	 */
	public String insertUser;

	/**
	 * 更新者　ユーザーコード
	 */
	public String updateUser;

	/**
	 * 作成日時
	 */
	public Timestamp insertDate;

	/**
	 * 更新日時
	 */
	public Timestamp updateDate;

	/**
	 * 関連エンティティ：BCデータ
	 */
	@ManyToOne
	@JoinColumns ({
		@JoinColumn (name = "idno", referencedColumnName = "idno"),
		@JoinColumn (name = "lo_date", referencedColumnName = "lo_date") })
	public FBcdata fBcdata; // FBcdata.fResultsumListのmappedByの値と変数名を合わせる

	/**
	 * 関連エンティティ：工程
	 */
	@ManyToOne
	@JoinColumns ({
		@JoinColumn (name = "mst_ver", referencedColumnName = "mst_ver"),
		@JoinColumn (name = "group_code", referencedColumnName = "group_code") })
	public MGroup mGroup; // MGroup.fResultsumListのmappedByの値と変数名を合わせる

	/**
	 * 関連エンティティ：検査順
	 */
	@OneToMany(mappedBy = "fResultsum")
	public List<MOrder> mOrderList;

	// 2017/12/01 DA ins start
	/**
	 * 関連エンティティ：撮影画像
	 */
	@OneToOne
	@JoinColumns({
		@JoinColumn(name = "idno", referencedColumnName = "idno"),
		@JoinColumn(name = "lo_date", referencedColumnName = "lo_date"),
		@JoinColumn(name = "group_code", referencedColumnName = "group_code") })
	public FShotimage fShotimageFResultsum; // mappedByの値と変数名を合わせる
	// 2017/12/01 DA ins end
}
