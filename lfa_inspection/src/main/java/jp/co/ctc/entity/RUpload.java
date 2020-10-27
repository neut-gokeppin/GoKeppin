/**
 *
 */
package jp.co.ctc.entity;


/**
 * id=uploadの時に送られてきたデータを格納するクラス
 *
 * @author CJ01786
 *
 */
public class RUpload {
	/**
	 * 検査結果フラグ
	 */
	public String resultFlg;

	/**
	 * 従業員コード
	 */
	public String userID;

	/**
	 * 検査時間
	 */
	public String ordertime;

	/**
	 * NG内容
	 */
	public String ngContents;

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
	 * 項目Code
	 */
	public Integer itemCode;

	/**
	 * グループコード
	 */
	public Integer groupCode;

	/**
	 * アイデントNo
	 */
	public String idno;

	/**
	 * ラインオフ計画日
	 */
	public String loDate;

//	/**
//	 * フレーム連番
//	 */
//	public String frameCode;
//
//	/**
//	 * フレーム区分
//	 */
//	public String frameSeq;

	/**
	 * 測定値
	 */
	public String inputData;

	// 2016/02/24 DA ins start
	/**
	 * 選択回数
	 */
	public Integer selectNumber;
	// 2016/02/24 DA ins end
}
