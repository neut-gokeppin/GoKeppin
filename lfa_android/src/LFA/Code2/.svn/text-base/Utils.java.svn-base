package LFA.Code2;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Xml;
import android.view.WindowManager;

/**
 *
 * @author cj01779
 *
 */
public final class Utils {

	/**
	 * ユーティリティクラスのため、デフォルトコンストラクタは隠す
	 */
	private Utils() {
	}

	/**
	 * メッセージ格納用変数
	 */
	public static String strFileMsg = "";

	/**
	 * プロパティー格納
	 */
	public static final  String BR = System.getProperty("line.separator");

	/**
	 *  指定されたURLからデータを取得します。
	 *  @param location URL
	 *  @return 取得したデータ
	 */
	public static byte[] getByteArrayFromURL(String location) {
		HttpURLConnection con = null;
		InputStream in = null;
		byte[] byteArray = null;

		try {
			// 指定した URL の作成
			URL url = new URL(location);
			// HTTP通信の初期化
			con = (HttpURLConnection) url.openConnection();
			// HTTP通信のメソッド指定(今回はダウンロードのみなので GET を指定)
			con.setRequestMethod("GET");
			con.setConnectTimeout(30000);
			con.setReadTimeout(30000);
			// HTTP通信開始
			con.connect();
			// HTTP通信でデータを取得
			in = con.getInputStream();

			// 取得したデータをbyteへ変換
			byteArray = getBytes(in);
			// データ有無チェック
			if (byteArray == null) {
				Log.e(LFA.TAG, "画像ファイルのダウンロードに失敗しました");
			}

		} catch (FileNotFoundException ex) {
			Utils.log("Utils.getByteArrayFromURL()\n" + ex.toString());
			Log.w(TAG, ex);
			Common.STR_FILEMSG = "noFile";
		} catch (Exception e) {
			Utils.log("Utils.getByteArrayFromURL()\n" + e.toString());
			e.printStackTrace();
			Common.STR_FILEMSG = "badConnect";
		} finally {
			// HTTP 通信の後始末
			try {
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				con.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return byteArray;
	}

	/**
	 * 指定されたURLからPOSTでデータを取得します。
	 * @param strUrl 通信を行うURL
	 * @param strPrma パラメータ
	 * @param timeout タイムアウト時間（ミリ秒）
	 * @return 通信結果
	 */
	public static byte[] getByteArrayFromURL(String strUrl, String strPrma, int timeout) {
		byte[] result = null;
		HttpURLConnection con = null;
		InputStream in = null;

		try {
			// HTTP接続のオープン
			URL url = new URL(strUrl);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setConnectTimeout(timeout);
			con.setReadTimeout(timeout);
			con.setUseCaches(false);
			con.setRequestProperty("Content-type", "text/xml; charset=UTF-8");
			con.setDoOutput(true);

			OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
			wr.write(strPrma);
			wr.flush();
			in = con.getInputStream();
			// wr.flush();
			wr.close();

			// バイト配列の読み込み
			result = getBytes(in);

		} catch (FileNotFoundException ex) {
			Utils.log("Utils.postByteArrayFromURL()\n" + ex.toString());
			Log.w(TAG, ex);
			result = "connect".getBytes();
		} catch (Exception e) {
			Utils.log("Utils.postByteArrayFromURL()\n" + e.toString());
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {
					con.disconnect();
				}
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;

	}

	/**
	 * 指定されたURLからPOSTでデータを取得します。
	 * @param strUrl 通信を行うURL
	 * @param strPrma パラメータ
	 * @return 通信結果
	 */
	public static byte[] getByteArrayFromURL(String strUrl, String strPrma) {
		// 標準のタイムアウト時間＝5分
		final int timeout = 300000;
		return getByteArrayFromURL(strUrl, strPrma, timeout);
	}

	/**
	 * タグ名の設定
	 */
	private static final String TAG = "ImageManager";

	/**
	 *
	 * @param context コンテキスト
	 * @param filename 画像ファイル名
	 * @param source bitmap
	 * @param strDate 更新日
	 */
	@SuppressLint("SimpleDateFormat")
	public static void addImage(Context context, String filename,
			byte[] source, String strDate) {

		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		boolean isCreateNew = false;
		String filePath = filename;
		long lngTimeIn = 0;

		try {

			try {
				lngTimeIn = df.parse(strDate).getTime();
			} catch (Exception e) {
				// サーバーの時間が正しくない場合
				lngTimeIn = System.currentTimeMillis();
			}

			File fileTemp = getFile(context, filePath);

			try {
				if (source != null) {
					if ((fileTemp).exists()) {

						long lngModif = fileTemp.lastModified();
						// 2016/02/24 DA upd start
						if (lngTimeIn != lngModif) {
						//if (lngTimeIn > lngModif) {
						// 2016/02/24 DA upd end
							if (!fileTemp.delete()) {
								Log.w(TAG, "削除失敗");
							}
							isCreateNew = true;
						} else {
							isCreateNew = false;
						}
					} else {
						isCreateNew = true;
					}
				} else {
					isCreateNew = false;
				}

			} catch (Exception e) {
				Log.w(TAG, e);
			}

			if (isCreateNew && source != null) {
				save(context, filename, source);
				// 2016/02/01 DA ins start
				fileTemp = getFile(context, filename);
				if ((fileTemp).exists()) {
					fileTemp.setLastModified(lngTimeIn);
				}
				// 2016/02/01 DA ins end
			}

		} catch (FileNotFoundException ex) {
			Log.w(TAG, ex);
		} catch (IOException ex) {
			Log.w(TAG, ex);
		}
	}

	/**
	 *
	 * @param url 接続URL
	 * @param targetTags ターゲットタグ
	 * @param strPrma パラメータ
	 * @return 結果
	 */
	public static ArrayList<String> getXmlTags(String url, String[] targetTags,
			String strPrma) {
		byte[] byteArray = Utils.getByteArrayFromURL(url, strPrma);
		ArrayList<String> result = new ArrayList<String>();
		if (byteArray == null) {
			Log.i("getXmlTags", "URLの取得に失敗");
			return result;
		}
		String data = new String(byteArray);

		try {
			final XmlPullParser xpp = Xml.newPullParser();
			xpp.setInput(new StringReader(data));
			while (true) {
				int e = xpp.getEventType();

				// EventがEND_DOCUMENTなら終了
				if (e == XmlPullParser.END_DOCUMENT) {
					break;
				}

				e = xpp.next();
				switch (e) {
				case XmlPullParser.START_TAG:
					String tag = xpp.getName();
					for (String target : targetTags) {
						if (target.equals(tag)) {
							result.add(xpp.nextText());
							break;
						}
					}
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * XML分析
	 * @param url URL
	 * @param targetTags ターゲットのタグ
	 * @param strPrma パラメータ
	 * @return 結果
	 */
	public static ArrayList<HashMap<String, String>> getXmlTagsForMap(
			String url, String[] targetTags, String strPrma) {
		byte[] byteArray = Utils.getByteArrayFromURL(url, strPrma);
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		if (byteArray == null) {
			Log.i("getXmlTagsForMap", "URLの取得に失敗 " + url);
			return result;
		}
		HashMap<String, String> xmlMap;
		String data = new String(byteArray);
		int iLoop = 0;
		try {
			final XmlPullParser xpp = Xml.newPullParser();
			xpp.setInput(new StringReader(data));
			xmlMap = new HashMap<String, String>();
			while (true) {

				int e = xpp.getEventType();

				// EventがEND_DOCUMENTなら終了
				if (e == XmlPullParser.END_DOCUMENT) {
					break;
				}

				e = xpp.next();

				switch (e) {
				case XmlPullParser.START_TAG:
					String tag = xpp.getName();
					for (String target : targetTags) {
						if (target.equals(tag)) {
							iLoop++;
							xmlMap.put(tag, xpp.nextText());
							break;
						}
					}
					break;
				}

				if (iLoop == targetTags.length && xmlMap.size() > 0) {
					result.add(xmlMap);
					xmlMap = new HashMap<String, String>();
					iLoop = 0;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}


	/**
	 * @param data 解析データ
	 * @param targetTags XML解析タグ
	 * @return 結果
	 */
	public static ArrayList<HashMap<String, String>> getOrdersingXmlTagsFromURL(
			String data, String[] targetTags) {

		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> xmlMap;
		HashMap<String, String> xmlBodyMap;
		HashMap<String, String> xmlTitleMap;
		try {
			final XmlPullParser xpp = Xml.newPullParser();
			data = data.trim();
			xpp.setInput(new StringReader(data));
			xmlMap = new HashMap<String, String>();
			xmlBodyMap = new  HashMap<String, String>();
			xmlTitleMap = new  HashMap<String, String>();
			String tag = "";
			while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
				switch (xpp.getEventType()) {
				case XmlPullParser.START_TAG:
					tag = xpp.getName();
					for (String target : targetTags) {
						if (tag.equals("InspecItem")){
							break;
						}
						if (target.equals(tag)) {
							xmlMap.put(tag, xpp.nextText());
							break;
						}
					}

					if (tag.equals("InspecItem") && xmlMap.size() > 0) {
						if (xmlTitleMap.size() == 0) {
							xmlTitleMap.putAll(xmlMap);
							xmlMap = new HashMap<String, String>();
						} else {
							xmlBodyMap.putAll(xmlTitleMap);
							xmlBodyMap.putAll(xmlMap);
							result.add(xmlBodyMap);
							xmlBodyMap = new HashMap<String, String>();
							xmlMap = new HashMap<String, String>();
						}
					}
				}
				xpp.next();
			}
			xmlBodyMap.putAll(xmlTitleMap);
			xmlBodyMap.putAll(xmlMap);
			result.add(xmlBodyMap);
			xmlBodyMap = new HashMap<String, String>();
			xmlMap = new HashMap<String, String>();

		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, e.toString());
		}
		return result;

	}

	/**
	 *
	 * @param data 解析データ
	 * @param targetTags XML解析タグ
	 * @return 結果
	 */
	public static ArrayList<HashMap<String, String>> getXmlTagsFromURL(
			String data, String[] targetTags) {

		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();

		HashMap<String, String> xmlMap;
		int iLoop = 0;
		try {
			final XmlPullParser xpp = Xml.newPullParser();
			xpp.setInput(new StringReader(data));
			xmlMap = new HashMap<String, String>();
			while (true) {

				int e = xpp.getEventType();

				// EventがEND_DOCUMENTなら終了
				if (e == XmlPullParser.END_DOCUMENT) {
					break;
				}

				e = xpp.next();

				switch (e) {
				case XmlPullParser.START_TAG:
					String tag = xpp.getName();
					for (String target : targetTags) {
						if (target.equals(tag)) {
							iLoop++;
							xmlMap.put(tag, xpp.nextText());
							break;
						}
					}
					break;
				}

				if (iLoop == targetTags.length && xmlMap.size() > 0) {
					result.add(xmlMap);
					xmlMap = new HashMap<String, String>();
					iLoop = 0;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, e.toString());
		}
		return result;
	}



	/**
	 *
	 * @param context コンテキスト
	 * @param filename ファイル名
	 * @param strURL 接続URL
	 * @param strDate データ
	 * @return 結果
	 */
	@SuppressLint("SimpleDateFormat")
	public static boolean addAppliByDate(Context context, String filename,
			String strURL, String strDate) {

		boolean blnRtn = false;
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		long lngTimeIn = 0;

		File fileTemp = getFile(context, filename);

		try {
			if ((fileTemp).exists()) {
				fileTemp.delete();
			}
		} catch (SecurityException e) {
			Log.w(TAG, e);
		}

		try {
			lngTimeIn = df.parse(strDate).getTime();
		} catch (Exception e) {
			// サーバーの時間が正しくない場合
			lngTimeIn = System.currentTimeMillis();
		}

		try {
			// 指定したURLからバイト配列を読み込む
			byte[] byteArray = getByteArrayFromURL(strURL + filename);

			File file = getFile(context, filename);
			if (byteArray != null) {
				if (file.createNewFile()) {
					save(context, filename, byteArray);
					blnRtn = true;
				}
				if (!file.setLastModified(lngTimeIn)) {
					Log.w(TAG, "更新日書き換え失敗");
				}
			} else {
				blnRtn = false;
			}

		} catch (FileNotFoundException ex) {
			Log.w(TAG, ex);
		} catch (IOException ex) {
			Log.w(TAG, ex);
		}

		return blnRtn;
	}

	/**
	 * InputStreamからbyte配列を作成します。
	 *
	 * @param in 入力
	 * @return バイト配列
	 * @throws IOException IO例外
	 */
	public static byte[] getBytes(InputStream in) throws IOException {
		final int size = 1024;
		byte[] buf = new byte[size];
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		try {
			int len = 0;
			while ((len = in.read(buf)) >= 0) {
				os.write(buf, 0, len);
			}

		} finally {
			if (os != null) {
				os.close();
			}
		}

		return os.toByteArray();
	}

	/**
	 * バイト配列をファイルに保存します。
	 *
	 * @param context コンテキスト
	 * @param filename 保存ファイル名
	 * @param filebody ファイル本体
	 * @exception IOException IO例外
	 */
	public static void save(Context context, String filename, byte[] filebody) throws IOException {
		OutputStream os = null;
		try {
			os = getFileOutputStream(context, filename);
			os.write(filebody);
		} finally {
			if (os != null) {
				os.close();
			}
		}
	}

	/**
	 * ファイルオブジェクトを取得します。
	 * @param context コンテキスト
	 * @param filename ファイル名
	 * @return ファイルオブジェクト
	 */
	public static File getFile(Context context, String filename) {
		if (Common.USE_SDCARD) {
			return new File(Common.SD_AP_DIR + filename);
		} else {
			return context.getFileStreamPath(filename);
		}
	}

	/**
	 * ファイル出力ストリームを取得します。
	 * @param context コンテキスト
	 * @param filename ファイル名
	 * @return ファイル出力ストリーム
	 * @throws FileNotFoundException ファイルが見つからないとき
	 */
	public static FileOutputStream getFileOutputStream(Context context, String filename) throws FileNotFoundException {
		if (Common.USE_SDCARD) {
			return new FileOutputStream(Common.SD_AP_DIR + filename);
		} else {
			return context.openFileOutput(filename, Context.MODE_PRIVATE);
		}
	}

	/**
	 * SDカードをクリアします
	 */
	public static void clearSdcard() {
		// アプリインストールに使用したファイルを消す
		File file = new File(Common.SD_DIR + LFA.APK_FILENAME);
		delete(file);

		// アプリ実行に使用したファイル／ディレクトリを消す
		File dir = new File(Common.SD_AP_DIR);
		delete(dir);
	}

	/**
	 * SDカードの準備を行います。
	 * lfa_inspectionフォルダを作ります。
	 */
	public static void readySdcard() {
		File dir = new File(Common.SD_AP_DIR);
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				Log.w(TAG, "フォルダ作成失敗");
			}
		}
	}

	/**
	 * ファイルまたはディレクトリを削除します。
	 * @param f 削除対象のファイルまたはディレクトリ
	 */
	private static void delete(File f) {
	    if (!f.exists()) {
	        return;
	    }

	    // ディレクトリは空にしておかないと削除できないため、
	    // 対象がディレクトリの場合、配下のファイルを全て削除する。
	    if (f.isDirectory()) {
	        File[] files = f.listFiles();
	        for (File file : files) {
				delete(file);
			}
	    }

	    // 削除
	    if (!f.delete()) {
			Log.w(TAG, "failed to delete file: " + f.getAbsolutePath());
	    }
	}

	/**
	 * ステータスバー高さ(低解像度)
	 *
	 * @author CJ01915
	 * @since 1.0.0.0
	 * @version 1.0.0.0
	 */
	private static final float LOW_DPI_STATUS_BAR_HEIGHT = 19f;

	/**
	 * ステータスバー高さ(中解像度)
	 *
	 * @author CJ01915
	 * @since 1.0.0.0
	 * @version 1.0.0.0
	 */
	private static final float MEDIUM_DPI_STATUS_BAR_HEIGHT = 25f;

	/**
	 * ステータスバー高さ(高解像度)
	 *
	 * @author CJ01915
	 * @since 1.0.0.0
	 * @version 1.0.0.0
	 */
	private static final float HIGH_DPI_STATUS_BAR_HEIGHT = 38f;

	/**
	 * ステータスバーの高さを取得する
	 *
	 * @author CJ01915
	 * @since 1.0.0.0
	 * @version 1.0.0.0
	 * @param context Activity
	 * @return ステータスバー高さ
	 */
	public static float getStatusBarHeight(Context context) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
		float statusBarHeight;
		switch (displayMetrics.densityDpi) {
			case DisplayMetrics.DENSITY_HIGH:
				statusBarHeight = HIGH_DPI_STATUS_BAR_HEIGHT;
				break;
			case DisplayMetrics.DENSITY_MEDIUM:
				statusBarHeight = MEDIUM_DPI_STATUS_BAR_HEIGHT;
				break;
			case DisplayMetrics.DENSITY_LOW:
				statusBarHeight = LOW_DPI_STATUS_BAR_HEIGHT;
				break;
			default:
				statusBarHeight = MEDIUM_DPI_STATUS_BAR_HEIGHT;
			}
		return statusBarHeight;
	}


	/**
	 * 検査データリクエスト用URLを取得。
	 * 選択されているモードによってURLを切り替える
	 * @param idno アイデントNo
	 * @param loDate ラインオフ計画日
	 * @param groupCode 工程コード
	 * @return URL文字列
	 */
	public static String getKensaUrl(String idno, String loDate, String groupCode) {
		String url;

		// 2016/02/24 DA upd start
		if (LFA.mode == LFA.ModeList.practiceKari0) {
			url = Common.KENSA_KARI_URL + "&idno=" + idno + "&lo_date=" + loDate + "&group_code=" + groupCode + "&selectmst=0";
		}
		// 2017/03/02 CT del start
		//else if (LFA.mode == LFA.ModeList.practiceKari1) {
		//	url = Common.KENSA_KARI_URL + "&idno=" + idno + "&lo_date=" + loDate + "&group_code=" + groupCode + "&selectmst=-1";
		//}
		// 2017/03/02 CT del end
		else {
			url = Common.KENSA_URL + idno + "_" + loDate + "_" + groupCode + ".xml";
		}
		//if (LFA.mode == LFA.ModeList.practiceKari) {
		//	url = Common.KENSA_KARI_URL + "&idno=" + idno + "&lo_date=" + loDate + "&group_code=" + groupCode;
		//} else {
		//	url = Common.KENSA_URL + idno + "_" + loDate + "_" + groupCode + ".xml";
		//}
		// 2016/02/24 DA upd end

		return url;
	}


	/**
	 * 指定された文字コードにて、文字列をURLエンコードします。
	 * Exception 処理を共通化するために作成しました。
	 *
	 * @param str 文字列
	 * @param encoding 文字コード
	 * @return エンコードした文字列
	 */
	public static String urlEncode(String str, String encoding) {
		try {
			return URLEncoder.encode(str, encoding);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 最小から最大の範囲内のランダムな数値を返す。
	 * @param min 最小
	 * @param max 最大
	 * @return ランダム値
	 */
	public static int getRandom(int min , int max) {
		return (int) (Math.random() * (max - min + 1)) + min;
	}

	/**
	 * 検査項目の合計を取得する。
	 * @param groupCode 工程コード
	 * @param idno アイデントNo
	 * @param loDate ラインオフ計画日
	 * @return 検査項目の合計
	 */
	public static int getOrdersingCount(String groupCode, String idno, String loDate) {

		int count = 0;
		String sql = "SELECT COUNT(*) FROM P_ordersing"
				+ " WHERE"
				+ " groupCode ='" + groupCode + "'"
				+ " AND idno ='" + idno + "'"
				+ " AND loDate ='" + loDate + "'";

		Cursor c = LFA.getmDb().rawQuery(sql, null);
		if (c.moveToFirst()) {
			String strCount = c.getString(0);
			if (strCount != null) {
				count = Integer.parseInt(strCount);
			}
		}
		return count;
	}


	/**
	 * スリープ
	 * @param milliseconds ミリ秒
	 */
	public static void sleep(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	/**
	 * ログ格納用
	 */
	private static StringBuilder log;

	/**
	 * ログ格納用オブジェクトを準備する
	 * @return ログ格納用オブジェクト
	 */
	private static synchronized StringBuilder openLog() {
		if (log == null) {
			log = new StringBuilder();
		}
		return log;
	}

	/**
	 * ログを書き込む
	 * @param text ログに書き込むテキスト
	 */
	public static void log(String text) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS ", Locale.JAPAN);
		String now = dateFormat.format(new Date());
		StringBuilder out = openLog();
		out.append(now + text + "\n");
	}

	/**
	 * ログをPOSTデータとしてサーバーへ送信する
	 */
	public static synchronized void flushLog() {
		// ログ内容があれば送信
		if (log != null && log.length() > 0) {
			final int timeout = 10000;
			byte[] res = Utils.getByteArrayFromURL(Common.REQ_URL, log.toString(), timeout);
			if (res != null) {
				// 送信成功したらログ内容をクリア
				log = new StringBuilder();
			}
		}
	}
}