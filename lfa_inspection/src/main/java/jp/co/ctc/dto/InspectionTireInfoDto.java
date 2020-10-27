package jp.co.ctc.dto;

import java.io.Serializable;

import jp.co.ctc.entity.AbstractEntity;

/**
 * タイヤメーカー検査情報
 *
 * @author DA 2014/04/07
 */
public class InspectionTireInfoDto extends AbstractEntity implements Serializable
{
	/**
	 * Serializableインタフェースの実装に必要。
	 */
	static final long serialVersionUID = 1L;

	/**
	 * デフォルトコンストラクター
	 */
	public InspectionTireInfoDto()
	{
	}

	/**
	 * 項目名称
	 */
	public String itemName;

	/**
	 * アイデントNo
	 */
	public String idno;

	/**
	 * ラインオフ計画日
	 */
	public String loDate;

//	/**
//	 * フレーム区分
//	 */
//	public String frameCode;
//
//	/**
//	 * フレーム連番
//	 */
//	public String frameSeq;

	// 2016/02/24 DA ins start
	/**
	 * ボデーNO
	 */
	public String bodyNo;

	/**
	 * 受信日
	 */
	public String recvDay;
	// 2016/02/24 DA ins end

	/**
	 * 項目Code
	 */
	public Integer itemCode;

	/**
	 * 検査回数
	 */
	public Integer inspecNo;

	// 2016/02/24 DA ins start
	/**
	 * 選択回数
	 */
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
	 * マスタバージョン
	 */
	public Integer mstVer;

	/**
	 * 工程No
	 */
	public String groupNo;

	/**
	 * 検査順
	 */
	public Integer inspecOrder;

	/**
	 * 内容
	 */
	public String signContents;

	/**
	 * 選択状態（1：正解、以外：正解以外）
	 */
	public Integer selectStatus;

	// 2020/01/22 DA ins start
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
	// 2020/01/22 DA ins end
}
