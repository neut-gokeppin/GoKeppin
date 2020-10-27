package LFA.Code2;

/**
 * @param args
 */

@SuppressWarnings("serial")
public class DB_TIREMAKER_LIST extends FileCommon{

	/**
	 * カラム名
	 */
	public static final String[] Columns = {
		 "tireMakerAbbreviation", // タイヤメーカー略称
			"tireMakerName", // タイヤメーカー名称
	};

	/**
	 * タイヤメーカー略称
	 */
	public static final Integer INDEX_TIREABBREVIATION = 0; // タイヤメーカー略称

	/**
	 * タイヤメーカー名称
	 */
	public static final Integer INDEX_TIRENAME = 1; // タイヤメーカー名称

	/**
	 * レコード長
	 */
	public static final Integer[] Lengths = {
			3, // タイヤメーカー略称
			20, // タイヤメーカー名称
	};

	/**
	 * テーブル定義
	 */
	public DB_TIREMAKER_LIST() {
		// ファイル名
		FILE_NAME = "tire_list.DAT";
		// テーブル名
		TABLE_NAME = "P_tiremaker";

		COLUMNS = Columns;
		LENGTHS = Lengths;

		COLUMN_CNT = Columns.length;
	}
}
