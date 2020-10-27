package LFA.Code2;

import java.io.Serializable;

import android.database.sqlite.SQLiteDatabase;

/**
 *
 * @author cj01779
 *
 */
@SuppressWarnings("serial")
abstract class FileCommon implements Serializable {
	/**
	 * ファイル名
	 */
	public String FILE_NAME = null;

	/**
	 *  テーブル名
	 */
	public String TABLE_NAME = null;

	/**
	 *  COLUMNSの数
	 */
	public Integer COLUMN_CNT;

	/**
	 *  カラム名
	 */
	public String[] COLUMNS = null;

	/**
	 *  レコード長
	 */
	public Integer[] LENGTHS = null;

	/**
	 *
	 * @param db data
	 * @throws Exception e
	 */
	public void CreateTableSSS(SQLiteDatabase db) throws Exception {
		// 内部にテーブルを作成する
		db.beginTransaction();
		try {
			StringBuilder createSql;

			createSql = new StringBuilder();
			createSql.append("create table " + TABLE_NAME + " (");
			createSql.append(COLUMNS[0]
					+ " integer primary key autoincrement not null,");
			for (Integer columnPos = 1; columnPos < (COLUMN_CNT - 1);
					columnPos++) {
				createSql.append(COLUMNS[columnPos] + " text,");
			}
			createSql.append(COLUMNS[COLUMN_CNT - 1] + " text");
			createSql.append(")");
			db.execSQL(createSql.toString());
			db.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			db.endTransaction();
		}
	}

	/**
	 *
	 * @param db DB
	 * @throws Exception e
	 */
	public void DeleteTable(SQLiteDatabase db) throws Exception {
		// 内部にテーブルを作成する
		db.beginTransaction();
		try {

			String sql = "DELETE FROM '" + TABLE_NAME + "';";

			db.execSQL(sql);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			db.endTransaction();
		}
	}

	/**
	 *
	 * @param db DB
	 * @throws Exception e
	 */
	public void DropTable(SQLiteDatabase db) throws Exception {
		// 内部にテーブルを作成する
		db.beginTransaction();
		try {

			String sql = "DROP TABLE '" + TABLE_NAME + "';";

			db.execSQL(sql);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			db.endTransaction();
		}
	}
}
