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
 * 検査結果（更新情報を持たないため、AbstractEntityを継承しない)
 *
 * @author sugihara
 *
 */
@Entity
public class FResult implements Serializable {

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
//	 * フレームNo
//	 */
//	@Id
//	public String frameSeq;

	/**
	 * ボデーNo
	 */
	public String bodyNo;

	/**
	 * 受信日
	 */
	public String recvDay;

	/**
	 * マスタバージョン
	 */
	@Id
	public Integer mstVer;

	/**
	 * 項目Code
	 */
	@Id
	public Integer itemCode;

	/**
	 * 検査回数
	 */
	@Id
	public Integer inspecNo;

	// 2016/02/24 DA ins start
	/**
	 * 選択回数
	 */
	@Id
	public Integer selectNumber;
	// 2016/02/24 DA ins end

	/**
	 * 検査結果
	 */
	public String inspecResult;

	/**
	 * NG理由
	 */
	public String ngReason;

	/**
	 * 入力値
	 */
	public String inputData;

	/**
	 * 作成者
	 */
	public String inspecUser;

	/**
	 * 作成日時
	 */
	public Timestamp inspecDate;

	// 2017/12/01 DA ins start
	/**
	 * 撮影画像ファイル名
	 */
	public String shotImage;

	/**
	 * 正解指示記号
	 */
	public String okBcSign;

	/**
	 * 不正解指示記号
	 */
	public String ngBcSign;

	// 2017/12/01 DA ins end

	// 2014/11/17 DA ins start
	/**
	 * BC記号。
	 */
	@Transient
	public String bcSign;
	/**
	 * 検査内容。
	 */
	@Transient
	public String signContents1;
	// 2014/11/17 DA ins end

	/**
	 * 関連エンティティ：BCデータ
	 */
	@ManyToOne
	@JoinColumns ({
		@JoinColumn (name = "idno", referencedColumnName = "idno"),
		@JoinColumn (name = "lo_date", referencedColumnName = "lo_date")
	})
	public FBcdata fBcdata; // FBcdata.fResultListのmappedByの値と変数名を合わせる


	/**
	 * 関連エンティティ：検査順
	 */
	@ManyToOne
	@JoinColumns ({
		@JoinColumn (name = "mst_ver", referencedColumnName = "mst_ver"),
		@JoinColumn (name = "item_code", referencedColumnName = "item_code")
	})
	public MOrder mOrder; // TGroup.mOrderListのmappedByの値と変数名を合わせる


	/**
	 * 関連エンティティ：検査員
	 */
	@ManyToOne
	@JoinColumn (name = "inspec_user", referencedColumnName = "user_code")
	public MUser mUser;

	// 2014/11/11 DA ins start
	/**
	 * 関連エンティティ：検査項目
	 */
	@ManyToOne
	@JoinColumns ({
		@JoinColumn (name = "mst_ver", referencedColumnName = "mst_ver"),
		@JoinColumn (name = "item_code", referencedColumnName = "item_code")
	})
	public MItem mItem;
	// 2014/11/11 DA ins end

	// 2017/12/01 DA ins start
	/**
	 * 関連エンティティ：正解の指示記号
	 */
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "mst_ver", referencedColumnName = "mst_ver"),
		@JoinColumn(name = "item_code", referencedColumnName = "item_code"),
		@JoinColumn(name = "ok_bc_sign", referencedColumnName = "bc_sign")
	})
	public MBcsign mBcsignOk; // mappedByの値と変数名を合わせる

	/**
	 * 関連エンティティ：不正解の指示記号
	 */
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "mst_ver", referencedColumnName = "mst_ver"),
		@JoinColumn(name = "item_code", referencedColumnName = "item_code"),
		@JoinColumn(name = "ng_bc_sign", referencedColumnName = "bc_sign")
	})
	public MBcsign mBcsignNg; // mappedByの値と変数名を合わせる

	/**
	 * 関連エンティティ：撮影画像
	 */
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "idno", referencedColumnName = "idno"),
		@JoinColumn(name = "lo_date", referencedColumnName = "lo_date"),
		@JoinColumn(name = "item_code", referencedColumnName = "item_code")
	})
	public FShotimage fShotimageFResult; // mappedByの値と変数名を合わせる
	// 2017/12/01 DA ins end
}
