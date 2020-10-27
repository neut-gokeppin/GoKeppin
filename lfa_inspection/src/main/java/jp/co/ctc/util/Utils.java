package jp.co.ctc.util;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.commons.lang.time.DateUtils;
import org.seasar.framework.exception.IORuntimeException;
import org.seasar.framework.exception.IllegalAccessRuntimeException;
import org.seasar.framework.util.URLUtil;
import org.seasar.struts.util.RequestUtil;

import jp.co.ctc.entity.MBcsign;


/**
 * 共通のユーティリティクラス。
 * 中身が増えてきたら分割してください。
 *
 * @author CJ01615
 *
 */
public final class Utils {

	/**
	 * デフォルトコンストラクタ。
	 * インスタンス化禁止のためprivate宣言
	 */
	private Utils() {
	}

	/**
	 * 検査用画像格納ディレクトリ名を定義
	 */
	public static final String IMAGE_DIR = "/images/";

	/**
	 * 物流用画像格納ディレクトリ名を定義
	 */
	public static final String LG_IMAGE_DIR = "/lg_images/";

	/**
	 * XMLエスケープ用の変換文字定義
	 */
	private static final String[][] ESCAPE_XML_LIST = {{"&", "<", ">"}, {"&amp;", "&lt;", "&gt;"}};

	/**
	 * XMLエスケープ。
	 * 　"&" ⇒ "&amp;"
	 * 　"<" ⇒ "&lt;"
	 * 　">" ⇒ "&gt;"
	 * @param str 変換対象の文字列
	 * @return XMLエスケープ後の文字列
	 */
	public static String escapeXml(String str) {
		return StringUtils.replaceEach(str, ESCAPE_XML_LIST[0], ESCAPE_XML_LIST[1]);
	}

	/**
	 * ArrayをCSV形式に変換します。
	 *
	 * @param array
	 *            配列
	 * @return String
	 */
	public static String arrayToCsv(final Object[] array) {
		return "\"" + StringUtils.join(array, "\",\"") + "\"\r\n";
	}

	/**
	 * CSV形式の文章を配列に変換します。
	 *
	 * @param line
	 *            一行分の文章
	 * @return 配列
	 */
	public static Object[] csvToArray(final String line) {

		// 項目ごとに分ける
		Object[] item = line.split("\",\"");

		// 一番初めと最後の項目に付いた不要な「”」を取る
		String firstItem = item[0].toString();
		String lastItem = item[item.length - 1].toString();

		item[0] = firstItem.substring(1, firstItem.length());
		item[item.length - 1] = lastItem.substring(0, lastItem.length() - 1);

		return item;
	}

	/**
	 * EXCELのシート名の制約事項に適合する文字列を返します。
	 * 下記ルールで変換します。
	 * 　・禁止文字（:\/?*[]）→削除
	 *
	 * 半角31文字以内の制限については、問題が起きないので対応せずに様子見とします。
	 * 文字数制限の必要があった場合、formatSheetName2を有効にして使用してください。
	 *
	 * @param name 変換前の文字列
	 * @return EXCELのシート名制約に適合する文字列
	 */
	public static String formatSheetName(String name) {
		return StringUtils.replaceChars(name, ":/?*[]\\", null);
	}

	/**
	 * EXCELのシート名の制約事項に適合する文字列を返します。
	 * 下記ルールで変換します。
	 * 　・禁止文字（:\/?*[]）→削除
	 * 　・半角31文字以内→後で"_NG"を付けるため、29文字目以降を切り捨て
	 *
	 * @param name 変換前の文字列
	 * @return EXCELのシート名制約に適合する文字列
	 * @deprecated formatSheetNameを使用してください
	 */
	@Deprecated
	public static String formatSheetName2(String name) {
		final int maxLength = 28;
		int len = 0;
		StringBuilder newName = new StringBuilder();
		for (char c : name.toCharArray()) {
			// 禁止文字は除外
			if (StringUtils.contains(":/?*[]\\", c)) {
				continue;
			}
			// 29文字目以降は除外
			// ※暫定的に、半角カタカナは2文字でカウントしています
			len += CharUtils.isAscii(c) ? 1 : 2;
			if (len > maxLength) {
				break;
			}
			newName.append(c);
		}
		return newName.toString();
	}

	/**
	 * yyyy/MM/dd形式の文字をDate型に直して返します
	 * @param strDate yyyy/MM/dd形式の文字
	 * @return Date Dateオブジェクト。strDateが日付形式ではなかった場合、null
	 */
	public static Date itemCastDate(String strDate) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		try {
			return df.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Listがnullまたは空かどうかチェックします。
	 * @param list チェック対象
	 * @return true;nullまたは空 / false:それ以外
	 */
	public static boolean isEmpty(List<?> list) {
		return list == null || list.isEmpty();
	}

	/**
	 * サーバーの現在時刻を取得する
	 * @return サーバーの現在時刻
	 */
	public static Date now() {
		return new Date();
	}

	/**
	 * サーバーの現在時刻をTimestamp形式で取得する
	 * @return サーバーの現在時刻
	 */
	public static Timestamp nowts() {
		return new Timestamp(now().getTime());
	}

	/**
	 * サーバーの現在日をTimestamp形式で取得する
	 * @return サーバーの現在日
	 */
	public static Timestamp nowtsDate() {
		return new Timestamp(DateUtils.truncate(now(), Calendar.DAY_OF_MONTH).getTime());
	}

	/**
	 * URLエンコードを行います。
	 * nullの場合も例外発生させません。（nullを返す）
	 *
	 * @param s 文字列
	 * @param enc エンコーディング名
	 * @return エンコード後の文字列
	 */
	public static String urlEncode(String s, String enc) {

		if (s == null) {
			return null;
		}

		String s2 = URLUtil.encode(s, enc);

		// URLEncoder.encodeでは"*"がエンコードされず
		// エンコード後文字列をファイル名として使ったときに
		// 保存ができなくなるため、"*"もエンコードする。
		// *=%2A はUTF-8, SJIS, EUCで利用可なことを確認。
		return StringUtils.replace(s2, "*", "%2A");
	}

	/**
	 * 2つのStringをトリムしてから比較します。
	 * @param s1 比較する文字列
	 * @param s2 比較する文字列
	 * @return 比較結果
	 */
	public static boolean trimEqual(String s1, String s2) {
		return StringUtils.trimToEmpty(s1).equals(StringUtils.trimToEmpty(s2));
	}

	// 2016/02/24 DA ins start
	/**
	 * DBに設定する文字列をトリムする
	 * @param data 変換対象の文字列
	 * @return 変換後の文字列 ※変換対象の文字列がNULLの場合はNULLを返す。
	 */
	public static String trimDbSetting(String data)
	{
		if (data == null) {
			return null;
		}
		return Utils.trimRight(data);
	}

	/**
	 * 画面に表示する文字列をトリムする
	 * @param data 変換対象の文字列
	 * @return 変換後の文字列 ※変換対象の文字列がNULLの場合は空文字を返す。
	 */
	public static String trimDisplay(String data)
	{
		if (data == null) {
			return "";
		}
		return Utils.trimRightToEmpty(data);
	}

	/**
	 * 文字列の先頭から半角スペースを削除する
	 * @param data 変換対象の文字列
	 * @return 変換後の文字列 ※変換対象の文字列がNULLの場合はNULLを返す。
	 */
	public static String trimLeft(String data)
	{
		if (data == null) {
			return null;
		}
		return data.replaceAll("^[ ]+", "");
	}

	/**
	 * 文字列の先頭から半角スペースを削除する
	 * @param data 変換対象の文字列
	 * @return 変換後の文字列 ※変換対象の文字列がNULLの場合は空文字を返す。
	 */
	public static String trimLeftToEmpty(String data)
	{
		if (data == null) {
			return "";
		}
		return data.replaceAll("^[ ]+", "");
	}

	/**
	 * 文字列の末尾から半角スペースを削除する
	 * @param data 変換対象の文字列
	 * @return 変換後の文字列 ※変換対象の文字列がNULLの場合はNULLを返す。
	 */
	public static String trimRight(String data)
	{
		if (data == null) {
			return null;
		}
		return data.replaceAll("[ ]+$", "");
	}

	/**
	 * 文字列の末尾から半角スペースを削除する
	 * @param data 変換対象の文字列
	 * @return 変換後の文字列 ※変換対象の文字列がNULLの場合は空文字を返す。
	 */
	public static String trimRightToEmpty(String data)
	{
		if (data == null) {
			return "";
		}
		return data.replaceAll("[ ]+$", "");
	}
	// 2016/02/24 DA ins end

	/**
	 * 指定された文字コードにて、文字列をバイト配列に変換します。
	 * String.getBytes の Exception 処理を共通化するために作成しました。
	 * @param s 文字列
	 * @param encoding 文字コード
	 * @return バイト配列
	 */
	public static byte[] getBytes(String s, String encoding) {
		try {
			return s.getBytes(encoding);
		} catch (UnsupportedEncodingException e) {
			throw new IORuntimeException(e);
		}
	}

	/**
	 * MBcsignのリストから指示記号の一致するエンティティを取得
	 *
	 * @param mBcsignList MBcsignのリスト
	 * @param msgSign 検索する指示記号
	 * @return MBcsignエンティティ
	 */
	public static MBcsign findBcsign(List<MBcsign> mBcsignList, String msgSign) {
		for (MBcsign mBcsign : mBcsignList) {
			if (trimEqual(msgSign, mBcsign.bcSign)) {
				return mBcsign;
			}
		}

		return null;
	}

	/**
	 * targetオブジェクトのfieldフィールドの値を取得します。
	 * Exception 処理を共通化するために作成しました。
	 * @param target 取得対象オブジェクト
	 * @param field 取得対象フィールド名
	 * @return 取得された値
	 * @see FieldUtils#readDeclaredField(Object, String)
	 */
	public static Object readDeclaredField(Object target, String field) {
		try {
			return FieldUtils.readDeclaredField(target, field);
		} catch (IllegalAccessException e) {
			throw new IllegalAccessRuntimeException(target.getClass(), e);
		}
	}

	/**
	 * 工場
	 * @author Z127884
	 */
	public static enum Plant {
		/** 堤 */
		TUTUMI,
		/** 高岡 */
		TAKAOKA,
		/** 田原 */
		TAHARA,
		/** 元町 */
		MOTOMACHI
	}

	/**
	 * リクエストURLより、現在の工場を判定して返す。
	 * @return 現在の工場
	 */
	public static Plant getPlant() {
		String localAddr = RequestUtil.getRequest().getLocalAddr();

		if (localAddr.equals("172.20.115.121")) {
			return Plant.TAKAOKA;
		} else if (localAddr.equals("172.19.97.105")) {
			return Plant.MOTOMACHI;
		} else if (localAddr.equals("172.21.54.83")) {
			return Plant.TUTUMI;
		} else if (localAddr.equals("172.22.137.216")) {
			return Plant.TAHARA;
		} else if (localAddr.equals("161.95.25.71")) {
			return Plant.TUTUMI;
		} else if (localAddr.equals("161.95.4.67")) {
			return Plant.MOTOMACHI;
		} else if (localAddr.equals("172.19.98.74")) {
			return Plant.MOTOMACHI;
		} else if (localAddr.equals("161.95.4.147")) {
			return Plant.TAKAOKA;
		} else {
			throw new RuntimeException("Cannot know in which factry the server is placed.");
		}
	}

	/**
	 * 配列からnullまたは空文字を除いた配列を返します。
	 * @param array 処理対象の配列
	 * @return arrayからnullと空文字を除いた新たな配列
	 */
	public static String[] removeEmpty(String[] array) {
		if (array == null) {
			return array;
		}

		List<String> list = new ArrayList<String>();
		for (String e : array) {
			if (StringUtils.isNotEmpty(e)) {
				list.add(e);
			}
		}

		return list.isEmpty() ? new String[0] : list.toArray(array);
	}

	/**
	 * ファイルに出力する
	 * @param filename ファイル名
	 * @param data 出力データ
	 */
	public static void outputFile(String filename, String data) {

		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		try {
			fos = new FileOutputStream(filename);
			osw = new OutputStreamWriter(fos, "Windows-31J");
			bw = new BufferedWriter(osw);

			bw.write(data);
		}
		catch (Exception e) {
		}
		finally {
			IOUtils.closeQuietly(bw);
			IOUtils.closeQuietly(osw);
			IOUtils.closeQuietly(fos);
		}
	}
}
