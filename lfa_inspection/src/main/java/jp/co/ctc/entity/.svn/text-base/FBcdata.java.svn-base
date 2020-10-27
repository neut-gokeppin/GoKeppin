/**
 *
 */
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
 * BCデータ（更新情報を持たないため、AbstractEntityを継承しない)
 *
 * @author sugihara
 *
 */
@Entity
public class FBcdata implements Serializable {

	/**
	 * Serializableインタフェースの実装に必要。
	 */
	static final long serialVersionUID = 1L;

	/**
	 * ボデーNo.
	 */
	public String bodyNo;

	/**
	 * 受信日
	 */
	public String recvDay;

	/**
	 * 物流マスタバージョン
	 */
	public Integer lgmstVer;


	/**
	 * パターン区分
	 */
	public String ptnDiv;

	/**
	 * 作成者
	 */
	public String insertUser;

	/**
	 * 作成日時
	 */
	public Timestamp insertDate;

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

	/**
	 * フレーム区分
	 */
	public String frameCode;

	/**
	 * フレーム連番
	 */
	public String frameSeq;

//	/**
//	 * アイデントNo.
//	 */
//	public String idNo;

	/**
	 * BC車種区分コード
	 */
	public String bctype;

	/**
	 * 組立連番
	 */
	public String bcnoH0;

	/**
	 * TP通過日時（艤装）。（旧：H0通過日時）
	 * 元はN0通過日時であったが、N0だとインプット忘れがあるため、H0に変更
	 */
	public Timestamp tpN0;

	// 2014/04/07 DA ins start
	/**
	 * ライン（艤装用）
	 */
	public String lineGiso;

	/**
	 * ライン（セールス用）
	 */
	public String lineSales;

	/**
	 * ライン（拡張用）
	 */
	public String lineArea03;

	/**
	 * TP通過日時（セールス）
	 */
	public Timestamp tpSales;

	/**
	 * TP通過日時（拡張用）
	 */
	public Timestamp tpArea03;
	// 2014/04/07 DA ins start

	/**
	 * コントロール型式
	 */
	public String ctrlKata;


	/**
	 * 関連エンティティ：検査結果
	 */
	@OneToMany (mappedBy = "fBcdata")
	public List<FResult> fResultList;


	/**
	 * 関連エンティティ：取出結果
	 */
	@OneToMany (mappedBy = "fBcdata")
	public List<LgFTakeresult> fTakeResultList;

	/**
	 * 関連エンティティ：検査結果（サマリー）
	 */
	@OneToMany (mappedBy = "fBcdata")
	public List<FResultsum> fResultsumList;

	/**
	 * 関連エンティティ：取出結果（サマリー）
	 */
	@OneToMany (mappedBy = "fBcdata")
	public List<LgFTakeresultsum> fTakeResultsumList;

	/**
	 * 関連エンティティ：車種マスタ
	 */
	@ManyToOne
	@JoinColumn(name = "bctype", referencedColumnName = "bctype")
	public MVehicle mVehicle;

	/**
	 * 関連エンティティ：SF基本テーブル
	 */
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "idno", referencedColumnName = "idno"),
		@JoinColumn(name = "lo_date", referencedColumnName = "lo_date")
	})
	public TSf tSf;

	/**
	 * 関連エンティティ：SF部品1テーブル
	 */
	@OneToOne
	@JoinColumns({
		@JoinColumn(name = "idno", referencedColumnName = "idno"),
		@JoinColumn(name = "lo_date", referencedColumnName = "lo_date")
	})
	public TSfbc1 tSfbc1;

	/**
	 * 関連エンティティ：SF部品2テーブル
	 */
	@OneToOne
	@JoinColumns({
		@JoinColumn(name = "idno", referencedColumnName = "idno"),
		@JoinColumn(name = "lo_date", referencedColumnName = "lo_date")
	})
	public TSfbc2 tSfbc2;

	/**
	 * 関連エンティティ：SF部品3テーブル
	 */
	@OneToOne
	@JoinColumns({
		@JoinColumn(name = "idno", referencedColumnName = "idno"),
		@JoinColumn(name = "lo_date", referencedColumnName = "lo_date")
	})
	public TSfbc3 tSfbc3;

	/**
	 * 関連エンティティ：SF部品4テーブル
	 */
	@OneToOne
	@JoinColumns({
		@JoinColumn(name = "idno", referencedColumnName = "idno"),
		@JoinColumn(name = "lo_date", referencedColumnName = "lo_date")
	})
	public TSfbc4 tSfbc4;

	/**
	 * 関連エンティティ：ダミー車両、記号
	 */
	@OneToMany (mappedBy = "fBcdata")
	public List<FBcdataDummy> fBcdataDummy;

	// 2017/12/01 DA ins start
	/**
	 * 関連エンティティ：撮影画像
	 */
	@OneToOne
	@JoinColumns({
		@JoinColumn(name = "idno", referencedColumnName = "idno"),
		@JoinColumn(name = "lo_date", referencedColumnName = "lo_date")
	})
	public FShotimage fShotimageFBcdata; // mappedByの値と変数名を合わせる
	// 2017/12/01 DA ins end
}
