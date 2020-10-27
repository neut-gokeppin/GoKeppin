package LFA.Code2;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import LFA.Code2.LFA.ModeList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/**
 * 撮影画像ファイルのダウンロード処理（バックグラウンド処理）
 *
 * @author DA 2017/12/01
 *
 */
public class ShotimageDownload
{
	// 対象情報リストを格納するHashMapのkey
	/** ボデーNO */
	private static final String INDEX_BODYNO = "bodyNo";
	/** アイデントNo */
	private static final String INDEX_IDNO = "idno";
	/** ラインオフ計画日 */
	private static final String INDEX_LODATE = "loDate";

	// 撮影画像リスト取得の処理結果
	/** 異常 */
	public static final int NG = 0;
	/** 正常 */
	public static final int OK = 1;
	/** タイムアウト */
	public static final int TIMEOUT = 2;
	/** 正常（データ無し） */
	public static final int NONE = 3;

	/** タクトタイム */
	private long tacttime = -1;

	/**
	 * コンストラクタ
	 */
	public ShotimageDownload()
	{
	}

	/**
	 * タクトタイム取得
	 * @return タクトタイム
	 */
	public long getTacttime()
	{
		return tacttime;
	}

	/**
	 * タクトタイム設定
	 * @param data タクトタイムの秒数
	 */
	public void setTacttime(long data)
	{
		tacttime = data;
	}

	/**
	 * 撮影画像ファイルをバックグラウンドでダウンロード開始する。
	 * @param context コンテキスト
	 * @param groupName 工程名称
	 */
	public void downloadShotimageBg(final Context context, final String groupName)
	{
		getShotimageBackground(context, groupName, null);
	}

	/**
	 * 撮影画像ファイルをバックグラウンドでダウンロード開始する。
	 * @param context コンテキスト
	 * @param groupName 工程名称
	 * @param bodyNo ボデーNO
	 */
	public void downloadShotimageBg(final Context context, final String groupName, final String bodyNo)
	{
		getShotimageBackground(context, groupName, bodyNo);
	}

	/**
	 * 撮影画像ファイルをバックグラウンドでダウンロード開始する。ダウンロードが開始するまで待機する。
	 * @param context コンテキスト
	 * @param groupName 工程名称
	 * @param bodyNo ボデーNO
	 * @param mode モード
	 * @return 処理結果（0:異常、1:正常、2:タイムアウト、3:正常（データ無し））
	 */
	public int downloadShotimage(final Context context, final String groupName, final String bodyNo, final ModeList mode)
	{
		int ret = getShotimage(context, groupName, bodyNo, mode);
		return ret;
	}

	/**
	 * 撮影画像ファイルをバックグラウンドでダウンロード開始する。
	 * @param context コンテキスト
	 * @param groupName 工程名称
	 * @param bodyNo ボデーNO
	 */
	private void getShotimageBackground(final Context context, final String groupName, final String bodyNo)
	{
		Thread th = new Thread(new Runnable()
		{
			public void run()
			{
				List<HashMap<String, String>> list = getDownloadList(groupName, bodyNo, "0");

				HashMap<String, String> map;
				for (int i = 0; i < list.size(); i++) {
					map = (HashMap<String, String>) list.get(i);
					String idnoRow = map.get(INDEX_IDNO);
					String loDateRow = map.get(INDEX_LODATE);

					int ret = getDownloadShotimageList(context, groupName, idnoRow, loDateRow, "0");
					if (ret == OK) {
						getDownloadShotimage(context, groupName, idnoRow, loDateRow);
						updateShotimageState(groupName, idnoRow, loDateRow);
					}
				}
			}
		});
		th.start();
	}

	/**
	 * 撮影画像ファイルをバックグラウンドでダウンロード開始する。ダウンロードが開始するまで待機する。
	 * @param context コンテキスト
	 * @param groupName 工程名称
	 * @param bodyNo ボデーNO
	 * @param mode モード
	 * @return 処理結果（0:異常、1:正常、2:タイムアウト、3:正常（データ無し））
	 */
	private int getShotimage(final Context context, final String groupName, final String bodyNo, final ModeList mode)
	{
		final List<HashMap<String, String>> list = getDownloadList(groupName, bodyNo, "1");
		if (list.size() == 0) {
			return NG;
		}

		// 本番の場合、取得できるまで繰り返す
		String flg = "0";
		if (mode == LFA.ModeList.production) {
			flg = "1";
		}

		HashMap<String, String> map;
		for (int i = 0; i < list.size(); i++) {
			map = (HashMap<String, String>) list.get(i);
			String idnoRow = map.get(INDEX_IDNO);
			String loDateRow = map.get(INDEX_LODATE);

			int ret = getDownloadShotimageList(context, groupName, idnoRow, loDateRow, flg);
			if (ret != OK) {
				return ret;
			}
		}

		Thread th = new Thread(new Runnable()
		{
			public void run()
			{
				HashMap<String, String> map;
				for (int i = 0; i < list.size(); i++) {
					map = (HashMap<String, String>) list.get(i);
					String idnoRow = map.get(INDEX_IDNO);
					String loDateRow = map.get(INDEX_LODATE);

					getDownloadShotimage(context, groupName, idnoRow, loDateRow);
					updateShotimageState(groupName, idnoRow, loDateRow);
				}
			}
		});
		th.start();

		return OK;
	}

	/**
	 * 検査項目から撮影画像を取得する対象情報を取得する。
	 * @param groupName 工程名称
	 * @param bodyNo ボデーNO
	 * @param flg 撮影画像状態（0:未取得のみ取得対象、1:すべて取得対象）
	 * @return 対象情報リスト
	 */
	private List<HashMap<String, String>> getDownloadList(String groupName, String bodyNo, String flg)
	{
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map;
		Cursor c = null;

		String sqlBodyNo = "";
		if (bodyNo != null && bodyNo.equals("") == false) {
			sqlBodyNo = " AND bodyNo = '" + bodyNo + "'";
		}

		String sqlShotimageState = "";
		if (flg != null && flg.equals("0")) {
			sqlShotimageState = " AND (shotimageState IS NULL OR shotimageState = 0)";
		}

		String sql = "SELECT * FROM P_ordersingItem" + " WHERE"
				+ " groupName = '" + groupName + "'"
				+ sqlBodyNo
				+ sqlShotimageState
				+ " ORDER BY _id";

		try {
			c = LFA.getmDb().rawQuery(sql, null);
			if (c.moveToFirst()) {
				do {
					String bodyNoRow = c.getString(DB_BODY_LIST.INDEX_BODYNO);
					String idnoRow = c.getString(DB_BODY_LIST.INDEX_IDNO);
					String loDateRow = c.getString(DB_BODY_LIST.INDEX_LODATE);

					map = new HashMap<String, String>();
					map.put(INDEX_BODYNO, bodyNoRow);
					map.put(INDEX_IDNO, idnoRow);
					map.put(INDEX_LODATE, loDateRow);
					list.add(map);
				}
				while (c.moveToNext());
			}
		}
		catch (Exception e) {
			Log.e(LFA.TAG, e.toString());
		}
		finally {
			try {
				if (c != null) {
					c.close();
					c = null;
				}
			}
			catch (Exception e) {
				Log.e(LFA.TAG, e.toString());
				e.printStackTrace();
			}
			finally {
				c = null;
			}
		}

		return list;
	}

	/**
	 * 撮影画像リストを取得する。
	 * @param context コンテキスト
	 * @param groupName 工程名称
	 * @param idno アイデントNo
	 * @param loDate ラインオフ計画日
	 * @param flg 繰り返しフラグ（0:繰り返さない、1:繰り返す）
	 * @return 処理結果（0:異常、1:正常、2:タイムアウト、3:正常（データ無し））
	 */
	private int getDownloadShotimageList(Context context, String groupName, String idno, String loDate, String flg)
	{
		int result = NG;

		String urlGroupName = Utils.urlEncode(groupName, "UTF-8");
		String url = Common.REQ_URL + Common.KEY_IMAGELIST + "&Koutei=" + urlGroupName + "&idno=" + idno + "&loDate=" + loDate + "&flg=" + flg;
		byte[] byteArray = Utils.getByteArrayFromURL(url, "");
		if (byteArray == null) {
			result = TIMEOUT;
		}
		else {
			String data = new String(byteArray);

			String strResult = "";
			String strTacttime = "";
			String[] targets;
			ArrayList<HashMap<String, String>> list;
			HashMap<String, String> map;

			// 結果
			targets = new String[] { "result", "tacttime" };
			list = Utils.getXmlTagsFromURL(data, targets);
			if (list.size() > 0) {
				map = (HashMap<String, String>) list.get(0);
				strResult = map.get("result");
				strTacttime = map.get("tacttime");
			}

			if (strResult.equals("OK")) {
				result = OK;
			}
			else if (strResult.equals("TIMEOUT")) {
				result = TIMEOUT;
			}
			else if (strResult.equals("NONE")) {
				result = NONE;
			}
			else if (strResult.equals("NG")) {
				result = NG;
			}
			else {
				result = NG;
			}

			// 正常の場合、DB更新する
			if (result == OK) {
				targets = new String[] { "fileName", "fileSize", "fileDate", "itemCode" };
				list = Utils.getXmlTagsFromURL(data, targets);
				for (int i = 0; i < list.size(); i++) {
					map = (HashMap<String, String>) list.get(i);
					String fileName = map.get("fileName");
					String fileSize = map.get("fileSize");
					String fileDate = map.get("fileDate");
					String itemCode = map.get("itemCode");

					boolean ret = updateShotimage(fileName, fileSize, fileDate, idno, loDate, itemCode);
					if (ret == false) {
						Log.e(LFA.TAG, "Acquisition of " + fileName + " failed (No item code)");
					}
				}
			}

			// タクトタイム
			try {
				tacttime = Long.parseLong(strTacttime);
			}
			catch (Exception e) {
				tacttime = -1;
			}
		}

		return result;
	}

	/**
	 * 撮影画像ファイルをダウンロードする。
	 * @param context コンテキスト
	 * @param groupName 工程名称
	 * @param idno アイデントNo
	 * @param loDate ラインオフ計画日
	 */
	private void getDownloadShotimage(Context context, String groupName, String idno, String loDate)
	{
		Cursor c = null;

		String sql = "SELECT * FROM P_ordersing" + " WHERE"
				+ " groupName = '" + groupName + "'"
				+ " AND idno ='" + idno + "'"
				+ " AND loDate ='" + loDate + "'"
				+ " ORDER BY _id";

		try {
			c = LFA.getmDb().rawQuery(sql, null);
			if (c.moveToFirst()) {
				do {
					try {
						String fileName = c.getString(DB_ORDER_SING.INDEX_SHOTIMAGEFILENAME);
						String fileSize = c.getString(DB_ORDER_SING.INDEX_SHOTIMAGEFILESIZE);
						String fileDate = c.getString(DB_ORDER_SING.INDEX_SHOTIMAGEFILEDATE);

						boolean ret = getShotimage(context, fileName, fileSize, fileDate);
						if (ret == false) {
							// フィルの取得に失敗しました
							Log.e(LFA.TAG, "Acquisition of " + fileName + " failed (No file name)");
						}
					}
					catch (Exception e) {
						//画像処理に失敗しても処理は続行する。
						Log.e(LFA.TAG, e.toString(), e);
					}
				}
				while (c.moveToNext());
			}
		}
		catch (Exception e) {
			Log.e(LFA.TAG, e.toString());
		}
		finally {
			try {
				if (c != null) {
					c.close();
					c = null;
				}
			}
			catch (Exception e) {
				Log.e(LFA.TAG, e.toString());
				e.printStackTrace();
			}
			finally {
				c = null;
			}
		}
	}

	/**
	 * 撮影画像状態を取得済に更新する。
	 * @param groupName 工程名称
	 * @param idno アイデントNo
	 * @param loDate ラインオフ計画日
	 */
	private void updateShotimageState(String groupName, String idno, String loDate)
	{
		LFA.getmDb().beginTransaction();
		try {
			ContentValues values = new ContentValues();
			values.put(DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_SHOTIMAGESTATE], 1);

			String where = ""
					+ " groupName = '" + groupName + "'"
					+ " AND idno ='" + idno + "'"
					+ " AND loDate ='" + loDate + "'";

			LFA.getmDb().update("P_ordersingItem", values, where, null);
			LFA.getmDb().setTransactionSuccessful();
		}
		catch (Exception e) {
			Log.e(LFA.TAG, e.toString(), e);
		}
		finally {
			LFA.getmDb().endTransaction();
		}
	}

	/**
	 * 撮影画像の情報を検査テーブルに更新する。
	 * @param fileName 撮影画像ファイル名
	 * @param fileSize 撮影画像ファイルサイズ
	 * @param fileDate 撮影画像ファイル日
	 * @param idno アイデントNo
	 * @param loDate ラインオフ計画日
	 * @param itemCode 項目Code
	 * @return 処理結果（true:正常、false:異常）
	 */
	private boolean updateShotimage(String fileName, String fileSize, String fileDate, String idno, String loDate, String itemCode)
	{
		// 入力パラメータチェック
		if (idno == null || idno.equals("")) {
			return false;
		}
		if (loDate == null || loDate.equals("")) {
			return false;
		}
		if (itemCode == null || itemCode.equals("")) {
			return false;
		}

		LFA.getmDb().beginTransaction();
		try {
			ContentValues values = new ContentValues();
			values.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_SHOTIMAGEFILENAME], fileName);
			values.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_SHOTIMAGEFILESIZE], fileSize);
			values.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_SHOTIMAGEFILEDATE], fileDate);

			String where = ""
					+ "itemCode like '%-" + itemCode + "'"
					+ " AND idno ='" + idno + "'"
					+ " AND loDate ='" + loDate + "'";

			LFA.getmDb().update("P_ordersing", values, where, null);
			LFA.getmDb().setTransactionSuccessful();
		}
		catch (Exception e) {
			Log.e(LFA.TAG, e.toString(), e);
		}
		finally {
			LFA.getmDb().endTransaction();
		}

		return true;
	}

	/**
	 * サーバーファイルの日付フォーマット
	 */
	private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	/**
	 * 撮影画像ファイルをダウンロードする。
	 * @param context コンテキスト
	 * @param fileName 撮影画像ファイル名
	 * @param fileSize 撮影画像ファイルサイズ
	 * @param fileDate 撮影画像ファイル日
	 * @return 処理結果（true:正常、false:異常）
	 * @throws Exception
	 */
	private boolean getShotimage(Context context, String fileName, String fileSize, String fileDate) throws Exception
	{
		// 入力パラメータチェック
		if (fileName == null || fileName.equals("")) {
			return false;
		}
		if (fileSize == null || fileSize.equals("")) {
			return false;
		}
		if (fileDate == null || fileDate.equals("")) {
			return false;
		}

		Boolean isNewImg = false;

		//ファイルが無い場合は取得する
		//ファイルが古い場合は取得する
		File dir = Utils.getFile(context, fileName);
		if (dir.exists()) {
			Calendar clientFile = Calendar.getInstance();
			Date clientFileDate = new Date(dir.lastModified());
			clientFile.setTime(clientFileDate);

			Calendar serverFile = Calendar.getInstance();
			Date serverFileDate = simpleDateFormat.parse(fileDate);
			serverFile.setTime(serverFileDate);

			int diff = clientFile.compareTo(serverFile);
			if (diff < 0) {
				isNewImg = true;
			}
		}
		else {
			isNewImg = true;
		}

		//ファイルのサイズが足りない場合は取得する
		if (isNewImg == false && dir.exists()) {

			long lenBe = dir.length();
			long lenAf = Long.valueOf(fileSize);
			if (lenBe < lenAf) {
				isNewImg = true;
			}
		}

		// 画像ファイルの取得が必要なければ処理を抜ける
		if (isNewImg == false) {
			return true;
		}

		// 1回1回の処理判定でよいので、ダウンロード前にクリアする。
		Common.STR_FILEMSG = "";

		// 画像ファイルを取得
		String url = Common.SHOT_IMG_URL + fileName;
		byte[] bmp1 = Utils.getByteArrayFromURL(url);

		// 画像ファイル存在しない場合
		if (!Common.STR_FILEMSG.equals("")) {
			if (Common.STR_FILEMSG.equals("noFile")) {
				Log.e(LFA.TAG, fileName + " is not found on the server.");
			}
			else {
				Log.e(LFA.TAG, fileName + " connection is refused.");
			}
			Common.STR_FILEMSG = "";
			return false;
		}

		// SDカードへ書き込む
		Utils.addImage(context, fileName, bmp1, fileDate);

		return true;
	}

	/**
	 * 撮影画像ファイルをすべて削除する。
	 * @param context コンテキスト
	 */
	public void deleteShotimage(Context context)
	{
		try {
			String[] fileList = context.fileList();

			for (String file : fileList) {
				if (file.startsWith("img_") && file.endsWith(".jpg")) {
					context.deleteFile(file);
				}
			}
		}
		catch (Exception e) {
			Log.e(LFA.TAG, e.toString(), e);
		}
		finally {
		}
	}

	// 2018/09/25 DA ins start
	/**
	 * 端末に存在しない車両データの撮影画像ファイルを削除する。
	 * @param context コンテキスト
	 */
	public void deleteUnnecessaryShotimage(Context context)
	{
		// 内部保持しているボデーNOを取得
		List<String[]> filematchList = new ArrayList<String[]>();
		Cursor c = null;
		String sql = "SELECT * FROM P_ordersingItem ORDER BY bodyNo";
		try {
			c = LFA.getmDb().rawQuery(sql, null);
			if (c.moveToFirst()) {
				do {
					String bodyNoRow = c.getString(DB_BODY_LIST.INDEX_BODYNO);
					String groupCodeRow = c.getString(DB_BODY_LIST.INDEX_GROUPCODE);
					String[] filematch = { bodyNoRow, String.format("%5s", groupCodeRow).replace(" ", "0") };
					filematchList.add(filematch);
				}
				while (c.moveToNext());
			}
		}
		catch (Exception e) {
			Log.e(LFA.TAG, e.toString());
		}
		finally {
			try {
				if (c != null) {
					c.close();
					c = null;
				}
			}
			catch (Exception e) {
				Log.e(LFA.TAG, e.toString());
				e.printStackTrace();
			}
			finally {
				c = null;
			}
		}

		try {
			// ファイル一覧を取得
			String[] fiList = context.fileList();
			List<String> fileList = new ArrayList<String>(Arrays.asList(fiList));

			// 削除しないファイルを除外する
			for (String[] filematch : filematchList) {

				Iterator<String> ite = fileList.iterator();
				while (ite.hasNext()) {
					String file = ite.next();
					String[] filesplit = file.split("_");
					if (filesplit.length >= 7) {
						if (filesplit[0].equals("img") &&
								filesplit[1].equals(filematch[0]) &&
								filesplit[3].equals(filematch[1]) &&
								filesplit[6].endsWith(".jpg")) {
							// 内部保持している撮影画像ファイル
							ite.remove();
						}
						else if (filesplit[0].equals("img") == false) {
							// 撮影画像ファイル以外
							ite.remove();
						}
					}
					else {
						// 撮影画像ファイル以外
						ite.remove();
					}
				}
			}

			// ファイル削除
			for (String file : fileList) {
				boolean isRet = context.deleteFile(file);
				if (isRet == false) {
					Log.e(LFA.TAG, file + " Failed to delete the file.");
				}
			}
		}
		catch (Exception e) {
			Log.e(LFA.TAG, e.toString(), e);
		}
		finally {
		}
	}
	// 2018/09/25 DA ins end
}
