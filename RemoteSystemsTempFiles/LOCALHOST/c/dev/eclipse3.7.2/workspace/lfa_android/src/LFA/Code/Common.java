package LFA.Code;


import android.os.Environment;

/**
 *
 * @author cj01779
 *
 */
public final class Common {
	/**
	 * ユーティリティクラスのため、デフォルトコンストラクタは隠す
	 */
	private Common() {
	}

	/**
	 * SDカードを使うか否か。
	 *   true: SDカードでデータを読み書きする
	 *   false: 本体でデータを読み書きする
	 */
	public static final boolean USE_SDCARD = false;

	/**
	 * アスペクト比の種類
	 * 3:2、5:3、16:9、それ以外の4種類
	 * @author CJ01786
	 *
	 */
	public enum Aspect { OTHER, ASPECT_3_2, ASPECT_5_3, ASPECT_16_9 }

	/**
	 * BroadcastAction格納
	 */
	public static final String INTENT_BROADCAST = "LFA";

	/**
	 * エラーメッセージ格納
	 */
	public static final String INTENT_EX_MSG = "intent_ex_msg";

	/**
	 * intentNo格納
	 */
	public static final String INTENT_EX_NO = "intent_ex_no";

	/**
	 * menu格納
	 */
	public static final String INTENT_EX_MENU = "intent_ex_menu";

	/**
	 * ボデーNO格納
	 */
	public static final String INTENT_EX_BODYNO = "intent_ex_bodyno";

	/**
	 * グループNO格納
	 */
	public static final String INTENT_EX_GROUPNO = "intent_ex_groupno";

	/**
	 * 従業員NO格納
	 */
	public static final String INTENT_EX_JYUUGYOIN = "intent_ex_jyuugyoin";

	/**
	 * 画像ファイル名格納
	 */
	public static final String INTENT_EX_IMGFILENAME = "intent_ex_imgfilename";

	/**
	 * グループ数格納
	 */
	public static final String INTENT_EX_GROUPCOUNT = "intent_ex_groupcount";

//	/**
//	 * チェック数格納
//	 */
//	public static final String INTENT_EX_CHECKCOUNT = "intent_ex_checkcount";

	/**
	 * 選択グループ格納
	 */
	public static final String INTENT_EX_CHECKITEMS = "intent_ex_checkitems";

	/**
	 * 画面終了ボタン押下時処理格納
	 */
	public static final String INTENT_EX_CHECKKARAMODORU = "intent_ex_checkkaramodoru";

	/**
	 * menuを格納
	 */
	public static final String INTENT_EX_MENUKARAMODORU = "intent_ex_menukaramodoru";

	/**
	 * 検査NG
	 */
	public static final String INTENT_EX_KENSANO = "intent_ex_kensano";

	/**
	 * アイデントNo
	 */
	public static final String INTENT_EX_IDNO = "intent_ex_idno";

	/**
	 * ラインオフ計画日
	 */
	public static final String INTENT_EX_LO_DATE = "intent_ex_lo_date";

//	/**
//	 * フレーム区分
//	 */
//	public static final String INTENT_EX_FRAME_DIV = "intent_ex_frame_div";
//
//	/**
//	 * フレーム連番
//	 */
//	public static final String INTENT_EX_FRAME_NO = "intent_ex_frame_no";

	/**
	 * 正解タイヤ
	 */
	public static final String INTENT_EX_TRUETIRE = "intent_ex_truetire";

	/**
	 * 選択された工程名
	 */
	public static final String INTENT_EX_GROUPNAME = "intent_ex_groupname";

	/**
	 * 測定値
	 */
	public static final String INTENT_EX_INPUTDATA = "intent_ex_inputdata";

	// 2014/04/07 DA ins start
	/**
	 * 車種名
	 */
	public static final String INTENT_EX_VEHICLENAME = "intent_ex_vehiclename";

	/**
	 * LO日
	 */
	public static final String INTENT_EX_LODATE = "intent_ex_lodate";

	/**
	 * 判定リスト
	 */
	public static final String INTENT_EX_JUDGMENTLIST = "intent_ex_judgmentlist";

	/**
	 * 判定リストの各項目の位置
	 */
	public static class INTENT_EX_JUDGMENTLIST_INDEX
	{
		/** インデックス */
		public static final int INDEX = 0;
		/** 検査項目 */
		public static final int ITEMNAME = 1;
		/** 測定値 */
		public static final int INPUTDATA = 2;
		/** 判定結果 */
		public static final int INSPECRESULT = 3;
		/** NG内容 */
		public static final int NGREASON = 4;
	}

	/**
	 * 次データのボデーNO
	 */
	public static final String INTENT_EX_NEXT_BODYNO = "intent_ex_next_bodyno";

	/**
	 * エラーメッセージ
	 */
	public static final String INTENT_EX_ERROR_MESSAGE = "intent_ex_error_message";
	// 2014/04/07 DA ins end

	/**
	 * ボデーNO入力画面
	 * ボデーNO非選択時に使用
	 */
	public static final String MSG_BODYNO_HISU = "ボデーNoを入力してください。";

	/**
	 * ボデーNO入力画面
	 * 内容チェックで使用
	 */
	public static final String MSG_BODYNO_INPUT = "ボデーNoが正しくありません。";

	/**
	 * ボデーNO入力画面
	 * 内容チェックで使用
	 */
	public static final String MSG_BODYNO_NUMBER = "指定されたﾎﾞﾃﾞｰNOの車両は存在しません。";

	// 2016/02/24 DA ins start
	/**
	 * ボデーNO入力画面
	 * 塗装吊上げ通過時刻チェックで使用
	 */
	public static final String MSG_BODYNO_TP = "指定されたﾎﾞﾃﾞｰNOは検査できる状態の車両ではありません。";

	/**
	 * ボデーNO入力画面
	 * 塗装吊上げ通過時刻チェックで使用
	 */
	public static final String MSG_BODYNO_REGIST = "ボデーNO．%sは、本番マスタに登録が無いため検査できません。";

	/**
	 * ボデーNO入力画面
	 * ラインオフ計画日期限切れ
	 */
	public static final String MSG_LODATE_EXPIRE = "LO計画日を一定期間過ぎているため表示できません";
	// 2016/02/24 DA ins end

	/**
	 * ダウンロード画面
	 * グループ非選択時に使用
	 */
	public static final String MSG_GROUPNO_HISU = "工程を選択してください。";

	/**
	 * 検査項目一覧画面
	 * 検査項目非選択時に使用
	 */
	public static final String MSG_ITEMNAME_HISU = "検査項目を選択してください。";

	/**
	 *従業員コード入力画面
	 *未入力チェックで使用
	 */
	public static final String MSG_JYUGYOIN_HISU = "従業員ｺｰﾄﾞを入力してください。";

	/**
	 * 従業員コード入力画面
	 * 内容チェックで使用
	 */
	public static final String MSG_JYUGYOIN_INPUT = "従業員ｺｰﾄﾞが正しくありません。";

	/**
	 * 終了ボタン押下時に使用
	 */
	public static final String MSG_END_CHECK = "アプリを終了します。" + '\n' + "よろしいですか？";

	/**
	 * 終了ボタン押下時、アップロードされていない場合に使用
	 */
	public static final String MSG_CLOSE_CHECK = "検査データがアップロードされていません。" + '\n'
			+ "アップロードしてください。";

	/**
	 * 従業員コード入力画面
	 * ユーザーリスト取得失敗時に使用
	 */
	public static final String MSG_USERLIST_CHECK = "ユーザーリストダウンロードに失敗しました。";

	/**
	 * 従業員コード入力画面
	 * 該当データがない場合に使用
	 */
	public static final String MSG_USERID_CHECK = "該当するユーザーIDが存在しません。";

	/**
	 * アプリバージョン時に使用
	 */
	public static final String MSG_APIUPDATE_QUS = "アプリが更新されています。" + '\n'
			+ "更新しますか？";

	/**
	 * 不使用
	 */
	public static final String MSG_REDOWNLOAD_QUS = "再ダウンロードしますか？";

	/**
	 * 画像取得をする際、ダウンロードに失敗した場合に使用
	 */
	public static final String MSG_IMGDOWNLOAD_CHECK = "画像を取得する際に、エラーが発生しました。";

	/**
	 * DBエラーが発生した際に使用
	 */
	public static final String MSG_DB_ERR = "DBエラーが発生しました。";

	/**
	 * サーバーに接続する際、タイムアウトになった際使用
	 */
	public static final String MSG_URL_CONN_ERR = "接続が中断されました。再度操作をしてください。";

	/**
	 * サーバーに接続できなかった際に使用
	 */
	public static final String MSG_URL_FAITH = "サーバーに接続できませんでした。再度操作してください。";

	/**
	 * 別の従業員が検査中のグループを選択した際に使用
	 */
	public static final String MSG_USERLOCK_CHECK = "別の従業員が検査中です。";

	/**
	 * 検査データが取得できない場合に使用
	 */
	public static final String MSG_DATA_CHCEK = "該当データが存在しません。";

	/**
	 * 車両情報が取得できない場合に使用
	 */
	public static final String MSG_NO_DATA = "車両情報の取得に失敗しました。ﾎﾞﾃﾞｰNOを入力して検査してください。";

	/**
	 * アップロード失敗時に使用
	 */
	public static final String MSG_UPLOAD_FAITH = "アップロード失敗しました。";

	/**
	 * 通過日時更新に失敗時に使用
	 */
	public static final String MSG_UPDATETP_ERROR = "通過日時の更新に失敗しました。";

	/**
	 * 受信日の格納に使用
	 */
	public static final String INTENT_EX_JYUSINBI = "intent_ex_jyusinbi";

	/**
	 * Msg格納
	 */
	public static  String  STR_FILEMSG = "";

	/**
	 * 自身がロック中のグループを選択した際に使用
	 */
	public static final String MSG_USERLOCK_CHECK_BYUSER = "自身が既にダウンロードしています。"
			+ '\n' + "再ダウンロードしますか？";

	/**
	 * URLの共通部分
	 */
	public static final String URL_BASE = "http://172.19.97.105:8400/lfa_inspection/"; //元町
	//public static final String URL_BASE = "http://172.19.97.105:6400/lfa_inspection/"; //元町テスト
	//public static final String URL_BASE = "http://172.21.54.83:8400/lfa_inspection/"; //堤
	//public static final String URL_BASE = "http://172.20.115.121:8400/lfa_inspection/"; //高岡
	//public static final String URL_BASE = "http://172.22.137.216:6400/lfa_inspection/"; //田原 ※キーボード変更すること！
	//public static final String URL_BASE = "http://161.95.25.71:8400/lfa_inspection/"; //車品生開発環境
	//public static final String URL_BASE = "http://192.168.1.152:8400/lfa_inspection/"; //DA

	/**
	 * サーバー接続用URL
	 */
	public static final String REQ_URL = URL_BASE + "android";

	/**
	 * 検査データダウンロード用URL
	 */
	public static final String KENSA_URL = URL_BASE + "xml/";

	/**
	 * 検査データダウンロード用URL（仮マスタ用）
	 */
	public static final String KENSA_KARI_URL = REQ_URL + "?id=kensa";

	/**
	 * 画像ファイル取得用URL
	 */
	public static final String IMG_URL = URL_BASE + "images/";

	/**
	 * apkファイル取得用URL
	 */
	public static final String APK_URL = URL_BASE + "apk/";

	/**
	 * SDカード格納先ディレクトリ名
	 */
	public static final String SD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";

	/**
	 * SDカード格納先ディレクトリ名
	 */
	public static final String SD_AP_DIR = SD_DIR + "lfa_inspection/";

	/**
	 * ボデーリスト取得の際に使用
	 */
	public static final String KEY_BODY = "?id=body";

	/**
	 * ボデーリスト取得の際に使用
	 */
	public static final String KEY_BODYID = "?id=bodyId";

	/**
	 * ユーザーID
	 */
	public static final String KEY_USERID = "?id=userId";

	/**
	 * 検査内容取得の際に使用
	 */
	public static final String KEY_GROUP = "?id=group";

	/**
	 * アップロードの際に使用
	 */
	public static final String KEY_UPLOAD = "?id=upload";

	/**
	 * 検査データダウンロードの際に使用
	 */
	public static final String KEY_GROUP_DL = "?koutei";

	/**
	 * 通過日時更新の際に使用
	 */
	public static final String KEY_UPDATETP = "?id=updateTp";

	/**
	 * ラインオフ計画日超過の確認に使用
	 */
	public static final String KEY_CHECK = "?id=check";

	/**
	 * OK
	 */
	public static final String KEY_REQUEST_OK = "OK";

	/**
	 * NG
	 */
	public static final String KEY_REQUEST_NG = "NG";

	/**
	 * LOCK
	 */
	public static final String KEY_REQUEST_LOCK = "LOCK";
}
