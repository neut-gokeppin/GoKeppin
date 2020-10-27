package LFA.Code2;

/**
 *
 * @author cj01779
 *
 */
@SuppressWarnings("serial")
public class DB_USER_LIST extends FileCommon {

	/**
	 * カラム名
	 */
	public static final String[] Columns = { "_id", // 項番
			"userID", // ユーザID
			"userName", // ユーザー名
	};

	/**
	 * 項番
	 */
	public static final Integer INDEX_ID = 0; // 項番

	/**
	 * ユーザID
	 */
	public static final Integer INDEX_USERID = 1; // ユーザID

	/**
	 * ユーザー名
	 */
	public static final Integer INDEX_USERNAME = 2; // ユーザ名

	/**
	 * レコード長
	 */
	public static final Integer[] Lengths = { 0, // 項番
			10, // ユーザID
			50, // ユーザー名
	};

	/**
	 * テーブル定義
	 */
	public DB_USER_LIST() {
		// ファイル名
		FILE_NAME = "user_list.DAT";
		// テーブル名
		TABLE_NAME = "P_userlist";

		COLUMNS = Columns;
		LENGTHS = Lengths;

		COLUMN_CNT = Columns.length;
	}

}
