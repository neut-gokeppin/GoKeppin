package jp.co.ctc.dto;

import java.io.Serializable;

import jp.co.ctc.entity.AbstractEntity;
import jp.co.ctc.entity.LgMBcsign;
import jp.co.ctc.entity.LgMOrder;
import jp.co.ctc.entity.LgMPart;

/**
 * 指示記号マスタメンテナンス画面用のData Transfer Object
 *
 * @author kaidu
 */
public class LgMBcsignDTO extends AbstractEntity implements Serializable {

	/**
	 * Serializableインタフェースの実装に必要。
	 */
	static final long serialVersionUID = 1L;

	/**
	 * デフォルトコンストラクター
	 */
	public LgMBcsignDTO() {
	}

	/**
	 * @param mBcsign
	 *            指示記号マスタのレコード
	 */
	public LgMBcsignDTO(final LgMBcsign mBcsign) {
		this(mBcsign.mPart);

		this.signCode = mBcsign.signCode;
		this.bcSign = mBcsign.bcSign;
		this.supplierName = mBcsign.supplierName;
		this.backNo = mBcsign.backNo;
		this.partNo = mBcsign.partNo;
		this.identName = mBcsign.identName;
		this.fileName = mBcsign.fileName;
		this.notuseFlag = mBcsign.notuseFlag;
		this.deleteFlag = mBcsign.deleteFlag;
		this.sopFlag = mBcsign.sopFlag;

		this.checkFlag = mBcsign.checkFlag;
		this.rackAddress = mBcsign.rackAddress;

		this.insertUser = mBcsign.insertUser;
		this.insertDate = mBcsign.insertDate;
		this.updateUser = mBcsign.updateUser;
		this.updateDate = mBcsign.updateDate;
	}

	/**
	 * @param part 検査部品マスタのレコード
	 */
	public LgMBcsignDTO(final LgMPart part) {
		super();
		this.mstVer = part.mstVer;
		this.partCode = part.partCode;
		this.partName = part.partName;
		this.msgDiv = part.msgDiv;
		this.msgNo = part.msgNo;
		this.bcPosition = part.bcPosition;
		this.bcLength = part.bcLength;
//		this.checkFlag = part.checkFlag;
//		this.rackAddress = part.rackAddress;

		// SPS台車名と取出順
		if (part.mOrderList != null && part.mOrderList.size() > 0) {
			LgMOrder order = part.mOrderList.get(0);
			if (order.mSps != null) {
				this.spsNo = order.mSps.spsNo;
				this.spsName = order.mSps.spsName;
				this.spsOrder = order.takeOrder;
			}
		}
	}

	/**
	 * LgMPart型からLgMBcsignDTO型に型変換します。
	 * @param part 変換元のLgMPart型インスタンス
	 * @return 変換後のLgMBcsignDTO型インスタンス
	 */
	public static LgMBcsignDTO parse(LgMPart part) {
		return new LgMBcsignDTO(part);
	}

	/**
	 * LgMBcsign型からLgMBcsignDTO型に型変換します。
	 * @param bcSign 変換元のLgMBcsign型インスタンス
	 * @return 変換後のLgMBcsignDTO型インスタンス
	 */
	public static LgMBcsignDTO parse(LgMBcsign bcSign) {
		return new LgMBcsignDTO(bcSign);
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
	 * 部品コード
	 */
	public Integer partCode;

	/**
	 * 名前
	 */
	public String partName;

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
	 * 棚照合フラグ
	 */
	public Boolean checkFlag;

	/**
	 * 所番地
	 */
	public String rackAddress;

	/**
	 * BC記号
	 */
	public String bcSign;

	/**
	 *
	 */
	public String supplierName;

	/**
	 *
	 */
	public String backNo;

	/**
	 *
	 */
	public String partNo;

	/**
	 *
	 */
	public String identName;

	/**
	 * ファイル名
	 */
	public String fileName;

	/**
	 * 不要フラグ
	 */
	public boolean notuseFlag;

	/**
	 * ファイル本体
	 */
	public byte[] fileBody;

	/**
	 * 削除フラグ
	 */
	public String deleteFlag;

	/**
	 * 号口フラグ
	 */
	public String sopFlag;

	/**
	 * Sps台車No
	 */
	public String spsNo;

	/**
	 * Sps台車名
	 */
	public String spsName;

	/**
	 * Sps台車取出順
	 */
	public Integer spsOrder;

	/**
	 * 編集区分
	 */
	public String editDiv;

	/**
	 * LgMBcsignオブジェクトを返す
	 *
	 * @return LgMBcsignオブジェクト
	 */
	public LgMBcsign getLgMBcsign() {
		LgMBcsign bcsign = new LgMBcsign();
		bcsign.mstVer = this.mstVer;
		bcsign.signCode = this.signCode;
		bcsign.partCode = this.partCode;
		bcsign.bcSign = this.bcSign;
		bcsign.supplierName = this.supplierName;
		bcsign.backNo = this.backNo;
		bcsign.partNo = this.partNo;
		bcsign.identName = this.identName;
		bcsign.fileName = this.fileName;
		bcsign.notuseFlag = this.notuseFlag;
		bcsign.deleteFlag = this.deleteFlag;
		bcsign.sopFlag = this.sopFlag;
		bcsign.checkFlag = this.checkFlag;
		bcsign.rackAddress = this.rackAddress;
		bcsign.insertUser = this.insertUser;
		bcsign.insertDate = this.insertDate;
		bcsign.updateUser = this.updateUser;
		bcsign.updateDate = this.updateDate;

		return bcsign;
	}
}
