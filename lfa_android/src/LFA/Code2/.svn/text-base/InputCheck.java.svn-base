package LFA.Code2;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

/**
 *
 * @author cj01779
 *
 */
public class InputCheck {

	/**
	 *  桁数チェック
	 * @param val 値
	 * @param min 最小値
	 * @param max 最大値
	 * @return 値
	 */
	public static boolean checkLength(String val, int min, int max) {

		int strlen = 0;
		try {
			if (val == null) {
				return false;
			}

			strlen = val.getBytes("Shift_JIS").length;

			if (strlen < min || strlen > max) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 *  必須チェック
	 * @param input 値
	 * @return true or false
	 */
	public static boolean checkRequired(String input) {
		if (input == null || input.trim().length() == 0) {
			return false;
		}
		return true;
	}

	/**
	 *  必須チェック
	 * @param input 値
	 * @return true or false
	 */
	public static boolean checkDigitRequired(String input) {
		if (input == null || input.trim().length() == 0) {
			return false;
		} else {
			if (checkNumber(input)) {
				if (Long.parseLong(input) == 0) {
					return false;
				}
			}
		}
		return true;
	}


	/**
	 * 半角全角チェック
	 * @param str 値
	 * @return true or false
	 */
	public static boolean checkFullSize(String str) {

		try {
			byte[] bytes;
			bytes = str.getBytes("Shift_JIS");

			// lengthを２倍する
			int beams = str.length() * 2;

			// lengthの２倍とバイト数が異なる場合は半角が含まれている
			if (beams != bytes.length) {
				return false;
			}

		} catch (Exception e) {
			return false;
		}

		return true;
	}

	/**
	 *  半角入力チェック
	 * @param str 値
	 * @return true or false
	 */
	public static boolean checkHalfSize(String str) {
		try {
			byte[] bytes;
			bytes = str.getBytes("Shift_JIS");

			int beams = str.length();

			// lengthとバイト数が異なる場合は全角が含まれている
			if (beams != bytes.length) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 *  半角英数字チェック
	 * @param input 値
	 * @return true or false
	 */
	public static boolean checkDigitAlphabet(String input) {
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if ((c < '0' || c > '9') && // 数字
					(c < 'a' || c > 'z') && // 小文字英字
					(c < 'A' || c > 'Z')) { // 大文字英字
				return false;
			}
		}

		return true;
	}

	/**
	 *  半角カナチェック
	 * @param input 値
	 * @return true or false
	 */
	public static boolean checkHankakuKana(String input) {

		final String HANKAKU_KANA = "ｱ ｲ ｳ ｴ ｵ ｶ ｷ ｸ ｹ ｺ ﾊ ﾋ ﾌ ﾍ ﾎ ﾏ ﾐ ﾑ ﾒ ﾓ ﾅ ﾆ ﾇ ﾈ ﾉ ｻ ｼ ｽ ｾ ｿ "
				+ "ﾀ ﾁ ﾂ ﾃ ﾄ ﾗ ﾘ ﾙ ﾚ ﾛ ﾔ ﾕ ﾖ ﾜ ｦ ｶﾞ ｷﾞ ｸﾞ ｹﾞ ｺﾞ ｻﾞ ｼﾞ ｽﾞ ｾﾞ ｿﾞ "
				+ "ﾀﾞ ﾁﾞ ﾂﾞ ﾃﾞ ﾄﾞ ﾊﾞ ﾋﾞ ﾌﾞ ﾍﾞ ﾎﾞ ﾊﾟ ﾋﾟ ﾌﾟ ﾍﾟ ﾎﾟ ﾝ ｬ ｭ ｮ ｯ ｰﾞﾟ､｡｢｣･";

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);

			if (-1 == HANKAKU_KANA.indexOf(c)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 英数字チェック
	 * @param input 値
	 * @return true or falseS
	 */
	public static boolean checkNumber(String input) {
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);

			if ((c < '0' || c > '9') && (c < 'a' || c > 'z')
					&& (c < 'A' || c > 'Z')) {
				// 英数字以外文字がある時
				return false;
			}
		}

		return true;
	}

	/**
	 *  左側文字埋め
	 * @param mStr 値
	 * @param intLen length
	 * @param mpad a
	 * @return 値
	 * @throws UnsupportedEncodingException e
	 */
	public static String padLeft(String mStr, int intLen, String mpad)
			throws UnsupportedEncodingException {

		String strRtn = "";

		if (mStr.getBytes("Shift_JIS").length >= intLen) {
			strRtn = mStr;
		} else {

			for (int i = 0;
			i < intLen - mStr.getBytes("Shift_JIS").length; ++i) {
				strRtn = mpad + strRtn;
			}
			strRtn = strRtn + mStr;
		}

		return strRtn;
	}

	/**
	 *  右側文字埋め
	 * @param mStr 値
	 * @param intLen length
	 * @param mpad a
	 * @return 値
	 * @throws UnsupportedEncodingException e
	 */
	public static String padRight(String mStr, int intLen, String mpad)
			throws UnsupportedEncodingException {
		String strRtn = "";

		if (mStr.getBytes("Shift_JIS").length >= intLen) {
			strRtn = mStr;
		} else {

			StringBuffer buf = new StringBuffer();

			for (int i = 0;
			i < intLen - mStr.getBytes("Shift_JIS").length; ++i) {
				buf.append(mpad);
			}
			strRtn = mStr + buf.toString();
		}

		return strRtn;
	}

	/**
	 *
	 * @param s s
	 * @param l l
	 * @param e e
	 * @return string
	 * @throws Exception e
	 */
	public static String splitBytes(String s, int l, String e) throws Exception {

		StringBuffer t = new StringBuffer();
		for (int i = 0; i < s.length(); ++i) {
			String u = s.substring(i, i + 1);
			if (t.toString().getBytes(e).length + u.getBytes(e).length > l) {
				break;
			}
			t.append(u);
		}

		if (t.toString().getBytes().length < l) {
			return padRight(t.toString(), l, " ");
		} else {
			return t.toString();
		}
	}

	/**
	 * Long型に変換
	 * @param input 変換する値
	 * @return true or false
	 */
	public static boolean isNumericSSS(String input) {
		boolean ret = false;
		try {
			Long.parseLong(input);
			ret = true;
		} catch (Exception e) {
			ret = false;
		}
		return ret;
	}


	/**
	 * ユーザーIDを検索
	 * @param id ユーザーID
	 * @return true or false
	 */
	public static boolean isRightUserId(String id) {

		boolean blnFlg = false;

		Cursor c = null;
		try {
			String sql = "";
			int iCount = 0;

			sql = " SELECT * " + " FROM P_userlist " + " WHERE userID ='" + id
					+ "'";

			//Cursor c = LFA.mDb.rawQuery(sql, null);
			c = LFA.getmDb().rawQuery(sql, null);

			if (c.moveToFirst()) {
				iCount = c.getCount();

				if (iCount > 0) {
					blnFlg = true;
				}
			}
		} catch (Exception e) {
			Log.e(LFA.TAG, e.toString());
		} finally {
			try {
				if (c != null) {
					c.close();
					c = null;
				}
			} catch (Exception e) {
				Log.e(LFA.TAG, e.toString());
				e.printStackTrace();
			} finally {
				c = null;
			}
		}

		return blnFlg;
	}

	/**
	 * 従業員コードから従業員名を取得
	 * @param id 従業員コード
	 * @return 従業員名
	 */
	public static String getRightUserId(String id) {

		String strRtn = "";
		Cursor c = null;

		try {
			String sql = "";
			int iCount = 0;

			sql = " SELECT * " + " FROM P_userlist " + " WHERE userID ='" + id
					+ "'";

			//Cursor c = LFA.mDb.rawQuery(sql, null);
			c = LFA.getmDb().rawQuery(sql, null);

			if (c.moveToFirst()) {
				iCount = c.getCount();

				if (iCount > 0) {
					strRtn = c.getString(2);
					if (strRtn != null) {
						strRtn = strRtn.trim();
					}
				}
			}
			c.close();
		} catch (Exception e) {
			Log.e(LFA.TAG, e.toString());
		} finally {
			try {
				if (c != null) {
					c.close();
					c = null;
				}
			} catch (Exception e) {
				Log.e(LFA.TAG, e.toString());
				e.printStackTrace();
			} finally {
				c = null;
			}
		}

		return strRtn;
	}


	/**
	 * ユーザーリスト取得
	 * @return true or false
	 */
	public static boolean setJyugyouin() {
		boolean blnFlg = false;

		// データの登録
		try {
			ArrayList<HashMap<String, String>> list;
			String url = Common.REQ_URL + Common.KEY_USERID;

			// ユーザーデータを取得する
			String[] targets = {// DB_USER_LIST.Columns[DB_USER_LIST.INDEX_ID],
			DB_USER_LIST.Columns[DB_USER_LIST.INDEX_USERID],
					DB_USER_LIST.Columns[DB_USER_LIST.INDEX_USERNAME] };

			list = Utils.getXmlTagsForMap(url, targets, "");

			if (list.size() > 0) {
				updateUserData(list);
				blnFlg = true;
			} else {
				blnFlg = false;
			}

		} catch (Exception e) {
			Log.e(LFA.TAG, e.toString());
		}

		return blnFlg;
	}

	/**
	 * ユーザーリストアップデート
	 * @param list リスト
	 */
	private static void updateUserData(
			ArrayList<HashMap<String, String>> list) {

		try {

			HashMap<String, String> map;
			String strTemp = "";

			//LFA.mDb.beginTransaction();
			LFA.getmDb().beginTransaction();

			Cursor c = null;
			try {

				String sql = "";

				sql = "SELECT * FROM P_userlist";

				//Cursor c = LFA.mDb.rawQuery(sql, null);
				c = LFA.getmDb().rawQuery(sql, null);

				if (c.moveToFirst()) {

					//LFA.mDb.delete("P_userlist", null, null);
					LFA.getmDb().delete("P_userlist", null, null);
				}

				if (list.size() > 0) {

					for (int i = 0; i < list.size(); i++) {
						map = (HashMap<String, String>) list.get(i);
						ContentValues values = new ContentValues();

						strTemp = map
						.get(DB_USER_LIST.Columns[DB_USER_LIST.INDEX_USERID]);

						if (strTemp != null) {
							values
						.put(
							DB_USER_LIST.Columns[DB_USER_LIST.INDEX_USERID],
											strTemp.trim());
						}

						strTemp = map
						.get(DB_USER_LIST.Columns[DB_USER_LIST.INDEX_USERNAME]);

						if (strTemp != null) {
							values
							.put(
							DB_USER_LIST.Columns[DB_USER_LIST.INDEX_USERNAME],
							strTemp.trim());
						}

						//LFA.mDb.insert("P_userlist", null, values);
						LFA.getmDb().insert("P_userlist", null, values);

					}

					// 訓練モード用ユーザー追加
					ContentValues values = new ContentValues();
					values.put(DB_USER_LIST.Columns[DB_USER_LIST.INDEX_USERID], LFA.PRACTICE_JYUGYOIN);
					values.put(DB_USER_LIST.Columns[DB_USER_LIST.INDEX_USERNAME], "訓練モード");
					LFA.getmDb().insert("P_userlist", null, values);


					//LFA.mDb.setTransactionSuccessful();
					LFA.getmDb().setTransactionSuccessful();
				}

			} catch (Exception e) {
				Log.e(LFA.TAG, e.toString());
			} finally {
				try {
					if (c != null) {
						c.close();
						c = null;
					}
				} catch (Exception e) {
					Log.e(LFA.TAG, e.toString());
					e.printStackTrace();
				} finally {
					c = null;
				}
				//LFA.mDb.endTransaction();
				LFA.getmDb().endTransaction();
			}

		} catch (Exception e) {
			Log.e(LFA.TAG, e.toString());
		}
	}

	/**
	 *
	 * @param DATE1 1
	 * @param DATE2 1
	 * @param strFormat 1
	 * @return 1
	 */
	public static boolean compareDate(String DATE1, String DATE2,
			String strFormat) {

		// strFormat :yyyy/MM/dd HH:mm:ss; yyyy-MM-dd ;
		DateFormat df = new SimpleDateFormat(strFormat);

		boolean blnRtn = false;
		try {
			Date dt1 = df.parse(DATE1);
			Date dt2 = df.parse(DATE2);
			if (dt1.getTime() > dt2.getTime()) {
				blnRtn = true;
			} else if (dt1.getTime() < dt2.getTime()) {
				blnRtn = false;
			} else {
				blnRtn = true;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return blnRtn;
	}

}
