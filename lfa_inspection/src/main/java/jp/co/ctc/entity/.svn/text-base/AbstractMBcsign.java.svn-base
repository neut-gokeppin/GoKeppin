package jp.co.ctc.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * 指示記号マスタ。
 *
 * @author kaidu
 */
@MappedSuperclass
public class AbstractMBcsign extends AbstractEntity implements Serializable {

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
	 * 検査項目コード。
	 */
	public Integer itemCode;

	/**
	 * BC記号。
	 */
	public String bcSign;

	/**
	 * 検査内容。
	 */
	public String signContents;

	/**
	 * ダミー記号
	 */
	public String dummySign;

	/**
	 * 検査画像表示順
	 */
	public Integer signOrder;

	/**
	 * ファイル名。
	 */
	public String fileName;

	/**
	 * 基準値上限度。
	 */
	public String tLimit;

	/**
	 * 基準値下限度。
	 */
	public String bLimit;
	
	// 2016/02/24 DA ins start
	/**
	 * 予約フラグ
	 */
	public String reserveFlag;
	
	/**
	 * 予約者
	 */
	public String reserveUser;
	
	/**
	 * 予約日
	 */
	public Timestamp reserveDate;
	
	/**
	 * 関連エンティティ：予約者情報
	 */
	@ManyToOne
	@JoinColumn(name = "reserve_user", referencedColumnName = "user_code")
	public MUser reserveMUser;
	// 2016/02/24 DA ins end

	/**
	 * 削除フラグ。
	 */
	public String deleteFlag;

	/**
	 * 号口フラグ。
	 */
	public String sopFlag;

	/**
	 * 備考
	 */
	public String notes;

	/**
	 * 車両と紐付けたときの記号の意味。
	 *
	 *     1: 正解。生産指示より指定された車両仕様
	 *     2: ダミーチェック用
	 */
	@Transient
	public Character usageForBody;

	// 2016/02/24 DA ins start
	/**
	 * 本番削除フラグ。
	 */
	public String sopDeleteFlag;
	// 2016/02/24 DA ins end
}