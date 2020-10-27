package jp.co.ctc.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import jp.co.ctc.entity.AbstractEntity;
import jp.co.ctc.entity.MBcsign;
import jp.co.ctc.entity.MItem;
import jp.co.ctc.entity.MOrder;
import jp.co.ctc.entity.MUser;

/**
 * 指示記号マスタメンテナンス画面用のData Transfer Object
 *
 * @author kaidu
 */
public class MBcsignDTO extends AbstractEntity implements Serializable {

	/**
	 * Serializableインタフェースの実装に必要。
	 */
	static final long serialVersionUID = 1L;

	/**
	 * デフォルトコンストラクター
	 */
	public MBcsignDTO() {
	}

	/**
	 * @param mBcsign
	 *            指示記号マスタのレコード
	 */
	public MBcsignDTO(final MBcsign mBcsign) {
		this(mBcsign.mItem);

		this.signCode = mBcsign.signCode;
		this.bcSign = mBcsign.bcSign;
		this.signContents = mBcsign.signContents;
		this.dummySign = mBcsign.dummySign;
		this.signOrder = mBcsign.signOrder;
		this.fileName = mBcsign.fileName;
		this.tLimit = mBcsign.tLimit;
		this.bLimit = mBcsign.bLimit;
		// 2016/02/24 DA ins start
		this.reserveFlag = mBcsign.reserveFlag;
		this.reserveUser = mBcsign.reserveUser;
		this.reserveDate = mBcsign.reserveDate;
		this.reserveMUser = mBcsign.reserveMUser;
		this.sopDeleteFlag = mBcsign.sopDeleteFlag;
		// 2016/02/24 DA ins end
		this.deleteFlag = mBcsign.deleteFlag;
		this.sopFlag = mBcsign.sopFlag;
		this.notes = mBcsign.notes;
		this.insertUser = mBcsign.insertUser;
		this.insertDate = mBcsign.insertDate;
		this.updateUser = mBcsign.updateUser;
		this.updateDate = mBcsign.updateDate;
		this.updateMUser = mBcsign.updateMUser;
	}

	/**
	 * @param item 検査項目マスタのレコード
	 */
	public MBcsignDTO(final MItem item) {
		super();
		this.mstVer = item.mstVer;
		this.itemCode = item.itemCode;
		this.itemName = item.itemName;
		this.resultDiv = item.resultDiv;
		this.msgDiv = item.msgDiv;
		this.msgNo = item.msgNo;
		this.bcPosition = item.bcPosition;
		this.bcLength = item.bcLength;

		// 左Hグループ名と検査順
		if (item.mOrderL != null && item.mOrderL.size() > 0) {
			MOrder order = item.mOrderL.get(0);
			if (order.mGroup != null) {
				this.groupNoL = order.mGroup.groupNo;
				this.groupNameL = order.mGroup.groupName;
				this.groupOrderL = order.inspecOrder;
			}
		}

		// 右Hグループ名と検査順
		if (item.mOrderR != null && item.mOrderR.size() > 0) {
			MOrder order = item.mOrderR.get(0);
			if (order.mGroup != null) {
				this.groupNoR = order.mGroup.groupNo;
				this.groupNameR = order.mGroup.groupName;
				this.groupOrderR = order.inspecOrder;
			}
		}
	}

	/**
	 * MItem型からMBcsignDTO型に型変換します。
	 * @param item 変換元のMItem型インスタンス
	 * @return 変換後のMBcsignDTO型インスタンス
	 */
	public static MBcsignDTO parse(MItem item) {
		return new MBcsignDTO(item);
	}

	/**
	 * MBcsign型からMBcsignDTO型に型変換します。
	 * @param bcSign 変換元のMBcsign型インスタンス
	 * @return 変換後のMBcsignDTO型インスタンス
	 */
	public static MBcsignDTO parse(MBcsign bcSign) {
		return new MBcsignDTO(bcSign);
	}
	/**
	 * マスタバージョン
	 */
	public Integer mstVer;

	/**
	 * 識別子
	 */
	public Integer signCode;

	/**
	 * 検査項目コード
	 */
	public Integer itemCode;

	/**
	 * 項目No
	 */
	public String itemNo;

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
	 * BC記号
	 */
	public String bcSign;

	/**
	 * 検査内容
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
	 * ファイル名
	 */
	public String fileName;

	/**
	 * ファイル本体
	 */
	public byte[] fileBody;

	/**
	 * 基準値上限度
	 */
	public String tLimit;

	/**
	 * 基準値下限度
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
	public MUser reserveMUser;
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
	 * 備考
	 */
	public String notes;

	/**
	 * 左HグループNo
	 */
	public String groupNoL;

	/**
	 * 左Hグループ名
	 */
	public String groupNameL;

	/**
	 * 左Hグループ検査順
	 */
	public Integer groupOrderL;

	/**
	 * 右HグループNo
	 */
	public String groupNoR;

	/**
	 * 右Hグループ名
	 */
	public String groupNameR;

	/**
	 * 右Hグループ検査順
	 */
	public Integer groupOrderR;

	/**
	 * 編集区分
	 */
	public String editDiv;

	// 2016/02/24 DA ins start
	/**
	 * 記号比較結果 
	 */
	public Boolean bcSignUnMat;
	/**
	 * 検査内容比較結果 
	 */
	public Boolean signContentsUnMat;
	/**
	 * ダミー内容比較結果 
	 */
	public Boolean dummySignUnMat;
	/**
	 * ファイル名比較結果 
	 */
	public Boolean fileNameUnMat;
	/**
	 * 差異確認結果メッセージ 
	 */
	public String diffResultMsg; 
	/**
	 * 本番削除フラグ。
	 */
	public String sopDeleteFlag;
	// 2016/02/24 DA ins end

	/**
	 * MBcsignオブジェクトを返す
	 *
	 * @return MBcsignオブジェクト
	 */
	public MBcsign getMBcsign() {
		MBcsign bcsign = new MBcsign();
		bcsign.mstVer = this.mstVer;
		bcsign.signCode = this.signCode;
		bcsign.itemCode = this.itemCode;
		bcsign.bcSign = this.bcSign;
		bcsign.signContents = this.signContents;
		bcsign.dummySign = this.dummySign;
		bcsign.signOrder = this.signOrder;
		bcsign.fileName = this.fileName;
		bcsign.tLimit = this.tLimit;
		bcsign.bLimit = this.bLimit;
		// 2016/02/24 DA ins start
		bcsign.reserveFlag = this.reserveFlag;
		bcsign.reserveUser = this.reserveUser;
		bcsign.reserveDate = this.reserveDate;
		bcsign.reserveMUser = this.reserveMUser;
		bcsign.sopDeleteFlag = this.sopDeleteFlag;
		// 2016/02/24 DA ins end
		bcsign.deleteFlag = this.deleteFlag;
		bcsign.sopFlag = this.sopFlag;
		bcsign.notes = this.notes;
		bcsign.insertUser = this.insertUser;
		bcsign.insertDate = this.insertDate;
		bcsign.updateUser = this.updateUser;
		bcsign.updateDate = this.updateDate;

		return bcsign;
	}
}
