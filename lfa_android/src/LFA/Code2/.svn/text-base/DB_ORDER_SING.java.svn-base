package LFA.Code2;

/**
 *
 * @author cj01779
 *
 */
@SuppressWarnings("serial")
public class DB_ORDER_SING extends FileCommon {

	// カラム名
	public static final String[] Columns = { "_id",// 項番
		    "idno", // アイデントNo
		    "loDate", // ラインオフ計画日
		    "bodyNo", // ボデーNo
		    "recvDay", // 受信日
		    "vehicleName", // 車種名
		    "groupName", // 工程名称
		    "groupState", // グループ状態
		    "groupNo", // グループ順
		    "groupCode", // 工程コード
			"ordersignNo", // 検査順
			"ordersignName", // 検査項目名
			"ordersignContents", // 検査内容
			"fileName", // 画像ファイル名
			"imageFlg", // 結果区分 検査画像確認画面：1　検査数値確認画面：2
			"resultFlg", // 検査結果フラグ OK：0　NG：1　未検査：NULL
			"ordersingFlg", // 検査履歴フラグ 未検査：0　再検査：1
			"inputData", // 測定値
			"ngContents", // NG内容 誤品：0 欠品：１　不要：２　その他３
			"itemCode", // 項目Code
			"inspecNo", // 検査回数
			"imgRecvDay", // 画像更新日
			"tireDiv", // タイヤフラグ
			"okngDiv", // ＯＫＮＧフラグ
			"signOrder", // 検索画像表示順
			"bcSign", // 指示記号
			"ordersignContents2", // ダミー検査内容
			"fileName2", // ダミー画像ファイル名
			"imgRecvDay2", // ダミー画像更新日
			"signOrder2", // ダミー検索画像表示順
			"bcSign2", // ダミー指示記号
			"userID", // 従業員ｺｰﾄﾞ
			"orderTime", // 検査時間
			"shotimageFileName", // 撮影画像ファイル名
			"shotimageFileSize", // 撮影画像ファイルサイズ
			"shotimageFileDate", // 撮影画像ファイル日
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

//	/**
//	 * フレーム区分
//	 */
//	public static final Integer INDEX_FRAMECODE = 1; // フレーム区分
//
//	/**
//	 * フレーム連番
//	 */
//	public static final Integer INDEX_FRAMESEQ = 2; // フレーム連番

	/**
	 * ボデーNO
	 */
	public static final Integer INDEX_BODYNO = 3; // ﾎﾞﾃﾞｰNO

	/**
	 * BCデータ受信日
	 */
	public static final Integer INDEX_RECVDAY = 4; // データ受信日

	/**
	 * 車種名
	 */
	public static final Integer INDEX_VEHICLENAME = 5; // 車種名

	/**
	 * 工程名称
	 */
	public static final Integer INDEX_GROUPNAME = 6; // 工程名称

	/**
	 * グループ状態INTEGER未検査：０　再検査：１　検査中：２
	 */
	public static final Integer INDEX_GROUPSTATE = 7; // グループ状態

	/**
	 * グループ順
	 */
	public static final Integer INDEX_GROUPNO = 8; // ｸﾞﾙｰﾌﾟ順

	/**
	 * 工程コード
	 */
	public static final Integer INDEX_GROUPCODE = 9; // 工程コード

	/**
	 * 検査順
	 */
	public static final Integer INDEX_ORDERSINGNO = 10; // 検査順

	/**
	 * 検査項目名
	 */
	public static final Integer INDEX_ORDERSINGNAME = 11; // 検査項目名

	/**
	 * 検査内容
	 */
	public static final Integer INDEX_ORDERSINGCONTENTS = 12; // 検査内容

	/**
	 * 画像ファイル名
	 */
	public static final Integer INDEX_FILENAME = 13; // 画像ファイル名

	/**
	 * 結果区分
	 */
	public static final Integer INDEX_IMAGEFLG = 14; // 結果区分
													 // 検査画像確認画面：1　検査数値確認画面：2

	/**
	 * 検査結果フラグ
	 */
	public static final Integer INDEX_RESULTFLG = 15; // 検査結果フラグ
													  // OK：0　NG：1　未検査：NULL

	/**
	 * 検査履歴フラグ
	 */
	public static final Integer INDEX_ORDERSINGFLG = 16; // 検査履歴フラグ 未検査：0　再検査：1

	/**
	 * 測定値
	 */
	public static final Integer INDEX_INPUTDATA = 17; // 測定値

	/**
	 * NG内容
	 */
	public static final Integer INDEX_NGCONTENTS = 18; // NG内容 誤品：0
													   // 欠品：１　不要：２　その他３

	/**
	 * 項目Code
	 */
	public static final Integer INDEX_ITEMCODE = 19; // 項目Code

	/**
	 * 検査回数
	 */
	public static final Integer INDEX_INSPECNO = 20; // 検査回数

	/**
	 * 画像更新日
	 */
	public static final Integer INDEX_IMGRECVDAY = 21; // 画像更新日

	/**
	 * タイヤフラグ
	 */
	public static final Integer INDEX_TIREFLG = 22; // タイヤフラグ

	/**
	 * ＯＫＮＧフラグ
	 */
	public static final Integer INDEX_OKNGFLG = 23; // ＯＫＮＧフラグ

	/**
	 * 検索画像表示順
	 */
	public static final Integer INDEX_SIGNORDER = 24; // 検索画像表示順

	/**
	 * 指示記号
	 */
	public static final Integer INDEX_BCSIGN = 25; // 指示記号

	/**
	 * 検査内容（ダミー）
	 */
	public static final Integer INDEX_ORDERSINGCONTENTS2 = 26; // 検査内容（ダミー）

	/**
	 * 画像ファイル名（ダミー）
	 */
	public static final Integer INDEX_FILENAME2 = 27; // 画像ファイル名（ダミー）

	/**
	 * 画像更新日（ダミー）
	 */
	public static final Integer INDEX_IMGRECVDAY2 = 28; // 画像更新日（ダミー）

	/**
	 * 検索画像表示順（ダミー）
	 */
	public static final Integer INDEX_SIGNORDER2 = 29; // 検索画像表示順（ダミー）

	/**
	 * 指示記号（ダミー）
	 */
	public static final Integer INDEX_BCSIGN2 = 30; // 指示記号（ダミー）

	/**
	 * 従業員コード
	 */
	public static final Integer INDEX_USERID = 31; // 従業員ｺｰﾄﾞ

	/**
	 * 検査時間
	 */
	public static final Integer INDEX_ORDERTIME = 32; // 検査時間

	// 2017/12/01 DA ins start
	/**
	 * 撮影画像ファイル名
	 */
	public static final Integer INDEX_SHOTIMAGEFILENAME = 33; // 撮影画像ファイル名

	/**
	 * 撮影画像ファイルサイズ
	 */
	public static final Integer INDEX_SHOTIMAGEFILESIZE = 34; // 撮影画像ファイルサイズ

	/**
	 * 撮影画像ファイル日
	 */
	public static final Integer INDEX_SHOTIMAGEFILEDATE = 35; // 撮影画像ファイル日
	// 2017/12/01 DA ins end

	/**
	 * レコード長
	 */
	public static final Integer[] Lengths = {
			0, // 項番
		    10, // アイデントNo
		    8, // ラインオフ計画日
			5, // ﾎﾞﾃﾞｰNO
			8, // データ受信日
			20, // 車種名
			30, // 工程名称
			1, // グループ状態　INTEGER　未検査：０　再検査：１　検査中：２
			2, // ｸﾞﾙｰﾌﾟ順
			5, // 工程コード   INTEGER
			4, // 検査順   INTEGER
			30, // 検査項目名
			30, // 検査内容
			1000, // 画像ファイル名
			1, // 結果区分  INTEGER 検査画像確認画面：1　検査数値確認画面：2
			1, // 検査結果フラグ    INTEGER OK：0　NG：1　未検査：NULL
			1, // 検査履歴フラグ    INTEGER 未検査：0　再検査：1
			10, // 測定値
			1, // NG内容   INTEGER 誤品：0欠品：１　不要：２　その他３
			5, // 項目Code INTEGER
			7, // 検査回数    INTEGER
			29, // 画像更新日  TIMESTAMP
			1, // タイヤフラグ   INTEGER
			1, // ＯＫＮＧフラグ	INTEGER
			4, // 検査画像表示順   INTEGER
			10, // 指示記号
			30, // 検査内容（ダミー）
			1000, // 画像ファイル名（ダミー）
			29,  // 画像ファイル受信日（ダミー）  TIMESTAMP
			4, // 検査画像表示順（ダミー）	INTEGER
			10, // 指示記号（ダミー）
			7, // 従業員ｺｰﾄﾞ
			21, // 検査時間
			1000, // 撮影画像ファイル名
			9, // 撮影画像ファイルサイズ
			21 // 撮影画像ファイル日
			};

	/**
	 * テーブル定義
	 */
	public DB_ORDER_SING() {
		// ファイル名
		FILE_NAME = "ordersing_list.DAT";
		// テーブル名
		TABLE_NAME = "P_ordersing";

		COLUMNS = Columns;
		LENGTHS = Lengths;

		COLUMN_CNT = Columns.length;
	}

}
