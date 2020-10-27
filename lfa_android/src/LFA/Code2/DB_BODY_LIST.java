package LFA.Code2;

/**
 *
 * @author cj01779
 *
 */
@SuppressWarnings("serial")
public class DB_BODY_LIST extends FileCommon {

	/**
	 * カラム名
	 */
	public static final String[] Columns = { "_id", // 項番
		    "idno", // アイデントNo
		    "loDate", // ラインオフ計画日
		    "frameCode", // フレーム区分
		    "frameSeq", // フレーム連番
		    "bodyNo", // ﾎﾞﾃﾞｰNo
		    "recvDay", // BCデータ受信日
		    "vehicleName", // 車種名
			"groupName", // 工程名称
			"groupState", // グループ状態 integer 未検査：０　再検査：１　検査中：２
			"groupNo", // ｸﾞﾙｰﾌﾟ順
			"groupCode", // 工程コード
			//"tp", // T/P通過時刻					// 2016/08/22 DA del
		    "bcnoH0", // 組立連番（H0のBC連番）
		    "shotimageState", // 撮影画像状態 integer 未取得：0　取得済：1
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
	 * フレーム区分
	 */
	public static final Integer INDEX_FRAMECODE = 3; // フレーム区分

	/**
	 * フレーム連番
	 */
	public static final Integer INDEX_FRAMESEQ = 4; // フレーム連番

	/**
	 * ボデーNO
	 */
	public static final Integer INDEX_BODYNO = 5; // ﾎﾞﾃﾞｰNO

	/**
	 * BCデータ受信日
	 */
	public static final Integer INDEX_RECVDAY = 6; // データ受信日

	/**
	 * 車種名
	 */
	public static final Integer INDEX_VEHICLENAME = 7; // 車種名

	/**
	 * 工程名称
	 */
	public static final Integer INDEX_GROUPNAME = 8; // 工程名称

	/**
	 * グループ状態INTEGER未検査：０　再検査：１　検査中：２
	 */
	public static final Integer INDEX_GROUPSTATE = 9; // グループ状態

	/**
	 * グループ順
	 */
	public static final Integer INDEX_GROUPNO = 10; // ｸﾞﾙｰﾌﾟ順

	/**
	 * 工程コード
	 */
	public static final Integer INDEX_GROUPCODE = 11; // 工程コード

	// 2016/08/22 DA del start
	///**
	// * TP通過時刻
	// */
	//public static final Integer INDEX_TP = 12; // TP通過時刻
	// 2016/08/22 DA del end

	/**
	 * 組立連番（H0のBC連番）
	 */
	// 2016/08/22 DA upd start
	//public static final Integer INDEX_BCNO_H0 = 13; // 組立連番（H0のBC連番）
	public static final Integer INDEX_BCNO_H0 = 12; // 組立連番（H0のBC連番）
	// 2016/08/22 DA upd end

	// 2017/12/01 DA ins start
	/**
	 * 撮影画像状態
	 */
	public static final Integer INDEX_SHOTIMAGESTATE = 13; // 撮影画像状態
	// 2017/12/01 DA ins end

	/**
	 * レコード長
	 */
	public static final Integer[] Lengths = { 0, // 項番
		    10, // アイデントNo
		    8, // ラインオフ計画日
		    3, // フレーム区分
		    7, // フレーム連番
		    5, // ﾎﾞﾃﾞｰNO
		    8, // データ受信日
		    20, // 車種名
			30, // 工程名称
			1, // グループ状態 INTEGER 未検査：０　再検査：１　検査中：２
			2, // ｸﾞﾙｰﾌﾟ順
			5, // 工程コード
			//14, // TP通過時刻						// 2016/08/22 DA del
			3, // 組立連番（H0のBC連番）
			1, // 撮影画像状態 integer 未取得：0　取得済：1
	};

	/**
	 * テーブル定義
	 */
	public DB_BODY_LIST() {
		// ファイル名
		FILE_NAME = "body_list.DAT";
		// テーブル名
		TABLE_NAME = "P_ordersingItem";

		COLUMNS = Columns;
		LENGTHS = Lengths;

		COLUMN_CNT = Columns.length;
	}

}
