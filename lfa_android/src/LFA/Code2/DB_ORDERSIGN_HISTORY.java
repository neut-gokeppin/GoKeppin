package LFA.Code2;

/**
 *
 * @author cj01779
 *
 */
@SuppressWarnings("serial")
public class DB_ORDERSIGN_HISTORY extends FileCommon {

	// カラム名
	public static final String[] Columns = { "_id",// 項番
		    "idno", // アイデントNo
		    "loDate", // ラインオフ計画日
			"itemCode", // 項目Code
			"selectNumber", // 選択回数
		    "resultFlg", // 検査結果フラグ OK：0　NG：1　未検査：NULL
			"inputData", // 測定値
			"ngContents", // NG内容 誤品：0 欠品：１　不要：２　その他３
			"userID", // 従業員ｺｰﾄﾞ
			"orderTime", // 検査時間
	};

	/**
	 * 項番
	 */
	public static final Integer INDEX_ID = 0; // 項番

	/**
	 * アイデントNo
	 */
	public static final Integer INDEX_IDNO = 1; // アイデントNo

	/**
	 * ラインオフ計画日
	 */
	public static final Integer INDEX_LODATE = 2; // ラインオフ計画日

	/**
	 * 項目Code
	 */
	public static final Integer INDEX_ITEMCODE = 3; // 項目Code

	/**
	 * 選択回数
	 */
	public static final Integer INDEX_SELECT_NUMBER = 4; // 検査回数


	/**
	 * 検査結果フラグ
	 */
	public static final Integer INDEX_RESULTFLG = 5; // 検査結果フラグ
													  // OK：0　NG：1　未検査：NULL

	/**
	 * 測定値
	 */
	public static final Integer INDEX_INPUTDATA = 6; // 測定値

	/**
	 * NG内容
	 */
	public static final Integer INDEX_NGCONTENTS = 7; // NG内容 誤品：0
													   // 欠品：１　不要：２　その他３

	/**
	 * 従業員コード
	 */
	public static final Integer INDEX_USERID = 8; // 従業員ｺｰﾄﾞ

	/**
	 * 検査時間
	 */
	public static final Integer INDEX_ORDERTIME = 9; // 検査時間

	/**
	 * レコード長
	 */
	public static final Integer[] Lengths = {
			0, // 項番
		    10, // アイデントNo
		    8, // ラインオフ計画日
			5, // 項目Code INTEGER
			7, // 選択回数    INTEGER
			1, // 検査結果フラグ    INTEGER OK：0　NG：1　未検査：NULL
			10, // 測定値
			1, // NG内容   INTEGER 誤品：0欠品：１　不要：２　その他３
			7, // 従業員ｺｰﾄﾞ
			21 // 検査時間
			};

	/**
	 * テーブル定義
	 */
	public DB_ORDERSIGN_HISTORY() {
		// ファイル名
		FILE_NAME = "ordersign_history.DAT";
		// テーブル名
		TABLE_NAME = "P_ordersignHistory";

		COLUMNS = Columns;
		LENGTHS = Lengths;

		COLUMN_CNT = Columns.length;
	}

}
