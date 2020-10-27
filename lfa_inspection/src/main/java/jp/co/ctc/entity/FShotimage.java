package jp.co.ctc.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 * 撮影画像を扱うクラスです。
 *
 * @author DA 2017/12/01
 *
 */
@Entity
public class FShotimage implements Serializable {

	/**
	 * Serializableインタフェースの実装に必要。
	 */
	static final long serialVersionUID = 1L;

	/**
	 * ボデーNo.
	 */
	@Id
	public String bodyNo;

	/**
	 * BC車種区分コード
	 */
	@Id
	public String bctype;

	/**
	 * 工程コード
	 */
	@Id
	public Integer groupCode;

	/**
	 * 項目Code
	 */
	@Id
	public Integer itemCode;

	/**
	 * 撮影日時
	 */
	public String shotDate;

	/**
	 * 生産指示記号
	 */
	public String productionSign;

	/**
	 * 撮影画像ファイル名
	 */
	public String shotImage;

	/**
	 * 撮影完了フラグ
	 */
	public String completionFlag;

	/**
	 * アイデントNo
	 */
	public String idno;

	/**
	 * ラインオフ計画日
	 */
	public String loDate;

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
	 * 関連エンティティ：検査結果
	 */
	@OneToMany (mappedBy = "fShotimageFResult")
	public List<FResult> fResultList;

	/**
	 * 関連エンティティ：検査結果サマリ
	 */
	@OneToOne (mappedBy = "fShotimageFResultsum")
	public FResultsum fResultsum;

	/**
	 * 関連エンティティ：BCデータ
	 */
	@OneToOne (mappedBy = "fShotimageFBcdata")
	public FBcdata fBcdata;

	/**
	 * 関連エンティティ：検査順
	 */
	@OneToOne (mappedBy = "fShotimageMOrder")
	public MOrder mOrderList;
}
