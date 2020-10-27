package jp.co.ctc.util;

import java.io.InputStream;

import jp.co.ctc.service.MItemService;
import jp.co.ctc.service.MVehicleService;

import org.seasar.extension.jdbc.where.SimpleWhere;

/**
 * 個別処理を実装するクラス　TMC用.
 *
 * @author DA
 *
 */
public class UniqUtils {


	/**
	 * 次処理車両取得の追加条件を返す
	 * @param sWhere 条件
	 * @return 追加条件
	 */
	public SimpleWhere getNextFBcdataWithResultsumOption(SimpleWhere sWhere) {

		return sWhere;
	}

	//=========================================================================
	//クロスチェック関係
	//=========================================================================

	/** TMC用の識別コード */
	public static final int CROSSCHECK_FLG = 1;

	/** TMC用のJSPファイル */
	public static final String CROSSCHECK_JSP = "index.jsp";

	/**
	 * 識別コードを取得する
	 * @return 識別コード
	 */
	public static int getFlg() {
		return CROSSCHECK_FLG;
	}

	/**
	 * エラーメッセージを取得する。
	 * @return エラーメッセージ。エラーがない場合はブランク。
	 */
	public String getErrorContent() {
		return "";
	}

	/**
	 * クロスチェックを実行する。
	 * @param inputStream ファイル
	 * @param filename ファイル名
	 * @param pass パスワード
	 * @param sheetName シート名
	 * @param tlcodePda PDAマスタ種類
	 * @param mItemService 項目マスタサービス
	 * @return チェック結果データ
	 * @throws Exception
	 */
	public String crossCheckCsv(InputStream inputStream, String filename, String pass, String sheetName, String tlcodePda, MItemService mItemService) throws Exception {
		return "";
	}

	/**
	 * クロスチェックを実行する。
	 * @param inputStream ファイル
	 * @param filename ファイル名
	 * @param pass パスワード
	 * @param sheetName シート名
	 * @param tlcodePda PDAマスタ種類
	 * @param mVehicleService 車種マスタサービス
	 * @param mItemService 項目マスタサービス
	 * @return チェック結果データ
	 * @throws Exception
	 */
	public String crossCheckCsv(InputStream inputStream, String filename, String pass, String sheetName, String tlcodePda, MVehicleService mVehicleService, MItemService mItemService) throws Exception {
		return "";
	}
}
