package jp.co.ctc.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

/**
 * 車種マスタ
 *
 * @author CJ01615
 *
 */
@Entity
public class MVehicle extends AbstractEntity implements Serializable {

	/**
	 * Serializableインタフェースの実装に必要。
	 */
	static final long serialVersionUID = 1L;

	/**
	 * BC車種区分
	 */
	@Id
	public String bctype;

	/**
	 * 車種名称
	 */
	public String vehicleName;

	// 2016/02/24 DA ins start
	/**
	 * 画像一覧出力OKフラグ
	 */
	public Boolean listOkFlag;
	// 2016/02/24 DA ins end

	/**
	 * 生産終了
	 */
	public Boolean endOfProd;

	/* ITAGE JYO 2013-04-16 仮マスタ本番登録 001 S */
	/**
	 * 切替組立連番
	 */
	public String bcnoH0;

	/**
	 * 切替ボデーNo
	 */
	public String bodyNo;

	/**
	 * 予約の状態
	 * true  => 予約中
	 * false => 未予約
	 */
	@Transient
	public Boolean bookStatus;

	// 2014/11/20 DA ins start
	/**
	 * 編集区分
	 */
	@Transient
	public String editDiv;
	// 2014/11/20 DA ins end

	/* ITAGE JYO 2013-04-16 仮マスタ本番登録 001 E */

	/**
	 * 関連エンティティ：工程
	 */
	@OneToMany (mappedBy = "mVehicle")
	public List<MGroup> mGroupList;


	/**
	 * 関連エンティティ：BCデータ（車両）
	 */
	@OneToMany (mappedBy = "mVehicle")
	public List<FBcdata> fBcdataList;


	// 2016/02/24 DA ins start
	/**
	 * 関連エンティティ：車種
	 */
	@OneToOne
	@JoinColumns ({
		@JoinColumn (name = "bctype", referencedColumnName = "bctype") })
	public MReserve mReserve;

	/**
	 * 指示記号予約の状態
	 * true  => 予約中
	 * false => 未予約
	 */
	@Transient
	public Boolean bcsignReserve;
	/**
	 * ライン
	 */
	@Transient
	public String line;
	// 2016/02/24 DA ins end
}
