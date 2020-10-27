package LFA.Code2;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import LFA.Code2.R;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import jp.co.ctc.util.LfaCommon;
import jp.co.ctc.util.LfaCommon.SELECTSTATUSLIST;

/**
 * タイヤメーカー検査画面
 * @author CTC
 *
 */
public class KensaTire extends BaseActivity implements OnClickListener {

	/**
	 * ボタンの定義
	 */
	static final int[] BUTTONS = { R.id.btMenu, R.id.btMae, R.id.btTsugi,
			R.id.Button01, R.id.Button02, R.id.Button03, R.id.Button04,
			R.id.Button05, R.id.Button06, R.id.Button07, R.id.Button08,
			R.id.btOther };

	/**
	 * 検査順
	 */
	TextView tvKensaJun;
	/**
	 * 工程名
	 */
	TextView tvGroupName;
	/**
	 * 検査項目名
	 */
	TextView tvKensaItem;
	/**
	 * ボディーNo
	 */
	TextView tvBodyNo;
	/**
	 * 作業者
	 */
	TextView tvJyugyouinNo;
	/**
	 * メニュー
	 */
	Button btMenu;
	/**
	 * 前
	 */
	Button btMae;
	/**
	 * 次
	 */
	Button btTsugi;
	/**
	 * 他
	 */
	Button btOther;
	/**
	 * ライナーレイアウトの定義
	 */
	LinearLayout layout;
	/**
	 * タイヤボタン
	 */
	Button[] btButton = new Button[8];
	/**
	 * 検査内容
	 */
	private String strCheckNaiyo;
	/**
	 * チェック順
	 */
	private String strCheckJun;
	/**
	 * 検査区分
	 */
	private String strimageFlg;
	/**
	 * グループ名
	 */
	private String strGroupName;
	/**
	 * 従業員NO
	 */
	private String strJyugyoinNo;
	/**
	 * 従業員名
	 */
	private String strJyugyoinName;
	/**
	 * ボデーNO
	 */
	private String strBodyNo;
	/**
	 * 工程コード
	 */
	private String strGroupNo;
	/**
	 * 工程検索名
	 */
	private String strCheckItem;
	/**
	 * 検査データ一式
	 */
	private String[] mkensaItems;
	/**
	 * グループ数
	 */
	private int intItemNo = 0;
	/**
	 * アイデントNo
	 */
	private String strIdno = "";
	/**
	 * ラインオフ計画日
	 */
	private String strLoDate = "";
	/**
	 * 測定値
	 */
	private String strInputData = "";
	// 2016/02/24 DA ins start
	/**
	 *　項目Code
	 */
	private String strItemCode = "";
	// 2016/02/24 DA ins start
	/**
	 * NG内容
	 */
	private String strNGContents;
	/**
	 * 結果フラグ
	 */
	private String strResultFlg = "";
	/**
	 * 定数OK
	 */
	private static final String OK = "0";
	/**
	 * 定数NG
	 */
	private static final String NG = "1";
	/**
	 * 定数Others
	 */
	private static final String Others = "Others";
	// 2014/04/07 DA del start
	///**
	// * 定数誤品
	// */
	//private static final String Gohin = "0";
	// 2014/04/07 DA del end
	/**
	 * メニューアイテムID
	 */
	private static final int MENU_ITEM0 = 0;
	/**
	 * アスペクト比3:2の時の文字サイズ倍率
	 */
	private static final double SCALE_3_2_FOOT = 1.0;
	/**
	 * アスペクト比16:9の時の文字サイズ倍率
	 */
	private static final double SCALE_16_9_FOOT = 1.2;
	/**
	 * アスペクト比5:3の時の文字サイズ倍率
	 */
	private static final double SCALE_5_3_FOOT = 1.1;
	/**
	 * 該当するアスペクト比がない時の文字サイズ倍率
	 */
	private static final double SCALE_OTHER = 1.0;
	/**
	 * アスペクト比3:2の時の文字サイズ倍率
	 */
	private static final double SCALE_3_2 = 1.0;
	/**
	 * アスペクト比5:3の時の文字サイズ倍率
	 */
	private static final double SCALE_5_3 = 1.3;
	/**
	 * アスペクト比16:9の時の文字サイズ倍率
	 */
	private static final double SCALE_16_9 = 1.5;
	/**
	 * 画面上部に表示する情報のフォントサイズ
	 */
	private static final int SIZE_FONT_HEAD = 14;
	/**
	 * 検査情報のフォントサイズ。
	 */
	private static final int SIZE_FONT_DATA_MIDDLE = 22;
	/**
	 * 画面下部に表示するボデー情報のフォントサイズ
	 */
	private static final int SIZE_FONT_FOOT_BODY = 14;
	/**
	 * タイヤフラグ
	 */
	private String strTireFlg;



	/**
	 * タイヤメーカー名
	 */
	// 2017/03/02 CT upd start
	// private enum enumMakerName { BRIDGESTONE, DUNLOP, TOYO, YOKOHAMA,
	// GOODYEAR, MICHELIN, PIRELLI, OHTSU, CONTINENTAL, FIRESTONE; }
	// private enum enumMakerName {
	// BRIDGESTONE, DUNLOP, TOYO, YOKOHAMA, GOODYEAR, MICHELIN, PIRELLI, OHTSU,
	// CONTINENTAL, FIRESTONE, HANKOOK;
	// }

	// 2017/03/02 CT upd end

	/**
	 * タイヤメーカー省略
	 */
	// 2017/03/02 CT upd start
	// private enum enumMakerCode { BS, DU, TY, TO, YH, GY, MI, PL, OH, CT, FS,
	// DMY, Other; }
	// private enum enumMakerCode {
	// BS, DU, TY, YH, GY, MI, PL, OH, CT, FS, HK, DMY, Other;
	// }

	// 2017/03/02 CT upd end

	// 2020/08/25 NEUT add start
	/**
	 * タイヤメーカーmap
	 */
	private Map<String, String> tireMakers = null;

	/**
	 * タイヤメーカーがその他の場合
	 */
	private static final String TIREMAKER_OTHER = "Other";

	/**
	 * タイヤメーカーがDMYの場合
	 */
	private static final String TIREMAKER_DMY ="DMY";


	String makerCode = null;
	// 2020/08/25 NEUT add end

	/**
	 * NG内容コード
	 */
	private Map<String, String> NGContentsCodeMap = new HashMap<String, String>();

	/**
	 * 検査内容のレコード
	 */
	private class KensaRecord {

		/**
		 * ボタンウィジェット（表示先のボタンが無ければNULL）
		 */
		Button button;

		/**
		 * マスタの検査内容
		 */
		public String kensaNaiyo;

		/**
		 * 表示内容の検査内容
		 */
		public String checkNaiyo;

		/**
		 * 表示内容
		 */
		public String hyojiNaiyo;
	}

	/**
	 * 検査内容
	 */
	private List<KensaRecord> mKensaList = new ArrayList<KensaRecord>();


	// 2020/08/25 NEUT upd start
	/**
	 * タイヤメーカー省略の変換
	 * @param kensaNaiyo タイヤメーカー省略の文字列
	 * @return タイヤメーカーの略称。Other:変換できない
	 */
	// private enumMakerCode convertMakerCode(String kensaNaiyo) {
	private String convertMakerCode(String kensaNaiyo) {
		try {
			// return enumMakerCode.valueOf(kensaNaiyo);
			String res = tireMakers.get(kensaNaiyo) ;
			if (res == null) {
				res = TIREMAKER_OTHER;
			}
			return res;
		} catch (Exception e) {
			// 該当のタイヤが存在しない場合
			return TIREMAKER_OTHER;
		}
	}
//	2020/08/25 NEUT upd end

	/**
	 * 検査内容の一覧情報を作成
	 *
	 * @param strCheckNaiyoAry
	 *            検査内容のタイヤ一覧
	 */
	@SuppressLint("NewApi")
	// 2020/08/25 NEUT del start
	// private void createKensaRecord(String[] strCheckNaiyoAry) {
	//
	//
	// // List<enumMakerCode> dummyNaiyoList = new ArrayList<enumMakerCode>();
	// //ダミータイヤ候補一覧
	// // List<enumMakerCode> checkNaiyoList = new ArrayList<enumMakerCode>();
	// //ダミータイヤ候補一覧作成用のタイヤ一覧
	//
	// List<String> dummyNaiyoList = new ArrayList<String>(); //ダミータイヤ候補一覧
	// List<String> checkNaiyoList = new
	// ArrayList<String>();//ダミータイヤ候補一覧作成用のタイヤ一
	//
	// //検査内容一覧の作成
	// for (int i = 0; i < strCheckNaiyoAry.length; i++) {
	//
	// KensaRecord kensaData = new KensaRecord();
	//
	// boolean isDirectly = false;
	// String kensaNaiyo = strCheckNaiyoAry[i].trim();
	//
	// /* enumMakerCode makerCode = convertMakerCode(kensaNaiyo);
	// if(makerCode == enumMakerCode.Other){
	// // タイヤが間違っているなど、無い場合は、そのまま使用する
	// isDirectly = true;
	// }
	// */
	// //存在チェック
	// enumMakerCode makerCode = convertMakerCode(kensaNaiyo);
	// if(makerCode == enumMakerCode.Other){
	// // タイヤが間違っているなど、無い場合は、そのまま使用する
	// isDirectly = true;
	// }
	//
	// // 2017/03/02 CT del start
	// //"TO"は"TY"に変換する
	// // if (makerCode == enumMakerCode.TO) {
	// // makerCode = enumMakerCode.TY;
	// // kensaNaiyo = makerCode.name();
	// // }
	// // 2017/03/02 CT del end
	//
	// kensaData.kensaNaiyo = kensaNaiyo;
	// kensaData.checkNaiyo = kensaNaiyo;
	// if (isDirectly) {
	// kensaData.hyojiNaiyo = kensaNaiyo;
	// }
	// else {
	// // kensaData.hyojiNaiyo = getTireMakerName(makerCode);
	// kensaData.hyojiNaiyo = getTireMakerName(kensaNaiyo);
	// }
	// mKensaList.add(kensaData);
	//
	// //タイヤ一覧の作成
	// checkNaiyoList.add(kensaNaiyo);
	// }
	//
	// //ダミータイヤ一覧の作成
	// for (enumMakerCode emc : enumMakerCode.values()) {
	// // 2017/03/02 CT upd start
	// //タイヤメーカー省略からダミーに不要な"TO"と"Other"を除く
	// // if (emc == enumMakerCode.TO || emc == enumMakerCode.Other) {
	// if (emc == enumMakerCode.Other) {
	// // 2017/03/02 CT upd end
	// continue;
	// }
	//
	// int checkListIndex = checkNaiyoList.indexOf(emc);
	// if (checkListIndex == -1) {
	// dummyNaiyoList.add(emc);
	// }
	// }
	// ArrayList<enumMakerCode> dummyNaiyoListBak = new
	// ArrayList<enumMakerCode>(dummyNaiyoList);
	//
	// //ダミータイヤにタイヤを設定
	// int isDummyCount = 0;
	// boolean isFirstDummy = true; //ダミーが選択されている場合は最初のDMYに設定する
	// if (strInputData != null && strInputData.isEmpty()) {
	// isFirstDummy = false;
	// }
	// for (KensaRecord kensaData : mKensaList) {
	//
	// if (kensaData.kensaNaiyo.equals(enumMakerCode.DMY.name())) {
	//
	// isDummyCount++;
	//
	// if (dummyNaiyoListBak.size() == 0) {
	// //ダミーにするタイヤが無い場合は、そのまま使用する
	// kensaData.hyojiNaiyo = enumMakerCode.DMY.name();
	//
	// continue;
	// }
	// else {
	// if (dummyNaiyoList.size() == 0) {
	// //ダミーにするタイヤがなくなれば、次巡目に突入する
	// dummyNaiyoList = new ArrayList<enumMakerCode>(dummyNaiyoListBak);
	// }
	// }
	//
	// if (isFirstDummy) {
	// isFirstDummy = false;
	//
	// enumMakerCode makerCode = convertMakerCode(strInputData);
	// if (makerCode != enumMakerCode.Other) {
	// int checkListIndex = dummyNaiyoList.indexOf(makerCode);
	// if (checkListIndex != -1) {
	//
	// kensaData.checkNaiyo = strInputData;
	// kensaData.hyojiNaiyo =
	// getTireMakerName(dummyNaiyoList.get(checkListIndex));
	//
	// //ダミーに同じタイヤを設定させないために候補一覧から削除する
	// dummyNaiyoList.remove(checkListIndex);
	//
	// continue;
	// }
	// }
	// }
	//
	// int dummyIndex = Utils.getRandom(0, dummyNaiyoList.size() - 1);
	// kensaData.checkNaiyo = dummyNaiyoList.get(dummyIndex).name();
	// kensaData.hyojiNaiyo = getTireMakerName(dummyNaiyoList.get(dummyIndex));
	//
	//
	// //ダミーに同じタイヤを設定させないために候補一覧から削除する
	// dummyNaiyoList.remove(dummyIndex);
	// }
	// }
	//
	// //使用するボタンウィジェットを設定
	// if (isDummyCount != 0) {
	// //ダミーがある場合はランダムにする
	// List<Button> buttonList = new ArrayList<Button>();
	// for (int i = 0; i < mKensaList.size(); i++) {
	// if (i < btButton.length) {
	// buttonList.add(btButton[i]);
	// }
	// else {
	// buttonList.add(null);
	// }
	// }
	//
	// Collections.shuffle(buttonList);
	//
	// for (int i = 0; i < mKensaList.size(); i++) {
	// mKensaList.get(i).button = buttonList.get(i);
	// }
	// }
	// else {
	// //ダミーでない場合は上から順にする
	// for (int i = 0; i < mKensaList.size(); i++) {
	// if (i < btButton.length) {
	// mKensaList.get(i).button = btButton[i];
	// }
	// else {
	// mKensaList.get(i).button = null;
	// }
	// }
	// }
	// }
	// 2020/08/25 NEUT del end

	// 2020/08/25 NEUT upd start
	private void createKensaRecord(String[] strCheckNaiyoAry) {

		List<String> dummyNaiyoList = new ArrayList<String>(); // ダミータイヤ候補一覧
		List<String> checkNaiyoList = new ArrayList<String>(); // ダミータイヤ候補一覧作成用のタイヤ一覧

		tireMakers = new HashMap<String, String>();

//		String strTireMakerCode = getTireMakerKey(tireMakers, strButtonName);

		ArrayList<HashMap<String, String>> list;

		String url = Common.REQ_URL + Common.KEY_TIREMAKERID;
		// タイヤメーカーを取得する
		String[] targets = {
				DB_TIREMAKER_LIST.Columns[DB_TIREMAKER_LIST.INDEX_TIREABBREVIATION],
				DB_TIREMAKER_LIST.Columns[DB_TIREMAKER_LIST.INDEX_TIRENAME] };

		list = Utils.getXmlTagsForMap(url, targets, "");



		for (int i = 0; i < list.size(); i++) {
			HashMap<String, String> map = list.get(i);

			// Set s =m.keySet();
			//Entry<String, String> entry = map.entrySet();

			tireMakers.put(list.get(i).get("tireMakerAbbreviation"), list.get(i).get("tireMakerName"));

		}
		// DMYをmasterに追加
		tireMakers.put(TIREMAKER_DMY,"");

		// 検査内容一覧の作成
		for (int i = 0; i < strCheckNaiyoAry.length; i++) {

			KensaRecord kensaData = new KensaRecord();

			boolean isDirectly = false;
			String kensaNaiyo = strCheckNaiyoAry[i].trim();

			if (tireMakers.get(kensaNaiyo) == null) {

				// タイヤが間違っているなど、無い場合は、そのまま使用する
				isDirectly = true;
			}

			// 2017/03/02 CT del start
			// "TO"は"TY"に変換する
			// if (makerCode == enumMakerCode.TO) {
			// makerCode = enumMakerCode.TY;
			// kensaNaiyo = makerCode.name();
			// }
			// 2017/03/02 CT del end

			kensaData.kensaNaiyo = kensaNaiyo;
			kensaData.checkNaiyo = kensaNaiyo;
			if (isDirectly) {
				kensaData.hyojiNaiyo = kensaNaiyo;
			} else {
				kensaData.hyojiNaiyo = tireMakers.get(kensaNaiyo);
			}
			mKensaList.add(kensaData);

			// タイヤ一覧の作成
			checkNaiyoList.add(kensaNaiyo);
		}

		// ダミータイヤ一覧の作成
		for (String emc : tireMakers.keySet()) {

			int checkListIndex = checkNaiyoList.indexOf(emc);
			if (checkListIndex == -1) {
				dummyNaiyoList.add(emc);
			}
		}
		ArrayList<String> dummyNaiyoListBak = new ArrayList<String>(dummyNaiyoList);

		// ダミータイヤにタイヤを設定
		int isDummyCount = 0;
		boolean isFirstDummy = true;		//ダミーが選択されている場合は最初のDMYに設定する
		if (strInputData != null && strInputData.isEmpty()) {
			isFirstDummy = false;
		}
		boolean hasDummy = false;
		for (KensaRecord kensaData : mKensaList) {
			if (kensaData.kensaNaiyo.equals(TIREMAKER_DMY)) {
				hasDummy = true;
			}
		}
		for (KensaRecord kensaData : mKensaList) {
			if (kensaData.kensaNaiyo.equals(TIREMAKER_DMY)) {

				isDummyCount++;

				if (dummyNaiyoListBak.size() == 0) {
					// ダミーにするタイヤが無い場合は、そのまま使用する
					kensaData.hyojiNaiyo = TIREMAKER_DMY;

					continue;
				} else if (dummyNaiyoList.size() == 0) {
					// ダミーにするタイヤがなくなれば、次巡目に突入する
					dummyNaiyoList = new ArrayList<String>(dummyNaiyoListBak);
				}
			}


			if (isFirstDummy) {
				isFirstDummy = false;

				makerCode = convertMakerCode(strInputData);
				if (makerCode != TIREMAKER_OTHER) {
					int checkListIndex = dummyNaiyoList.indexOf(makerCode);
					if (checkListIndex != -1) {

						kensaData.checkNaiyo = strInputData;
						kensaData.hyojiNaiyo = getTireMakerValue(tireMakers,
								makerCode);

						// ダミーに同じタイヤを設定させないために候補一覧から削除する
						dummyNaiyoList.remove(checkListIndex);

						continue;
					}
				}
			}

			int dummyIndex = Utils.getRandom(0, dummyNaiyoList.size() - 1);
			if (dummyNaiyoList.size() > 0 && hasDummy && kensaData.kensaNaiyo.equals(TIREMAKER_DMY)) {
				kensaData.checkNaiyo = dummyNaiyoList.get(dummyIndex);
				kensaData.hyojiNaiyo = getTireMakerValue(tireMakers,
						kensaData.checkNaiyo);
				// ダミーに同じタイヤを設定させないために候補一覧から削除する
				dummyNaiyoList.remove(dummyIndex);
			}

		}

		//使用するボタンウィジェットを設定
		if (isDummyCount != 0) {
			//ダミーがある場合はランダムにする
			List<Button> buttonList = new ArrayList<Button>();
			for (int i = 0; i < mKensaList.size(); i++) {
				if (i < btButton.length) {
					buttonList.add(btButton[i]);
				}
				else {
					buttonList.add(null);
				}
			}

			Collections.shuffle(buttonList);

			for (int i = 0; i < mKensaList.size(); i++) {
				mKensaList.get(i).button = buttonList.get(i);
			}
		}
		else {
			//ダミーでない場合は上から順にする
			for (int i = 0; i < mKensaList.size(); i++) {
				if (i < btButton.length) {
					mKensaList.get(i).button = btButton[i];
				}
				else {
					mKensaList.get(i).button = null;
				}
			}
		}
	}
	// 2020/08/25 NEUT upd end
	// 2020/08/25 NEUT add start
	/**
	 * タイヤメーカーの名称から略称を取得
	 *
	 * @param map
	 *            タイヤメーカーのmap(key:略称	value:名称)
	 *         @param value
	 *         		タイヤメーカーの名称
	 */
	private <K, V> K getTireMakerValue(Map<K, V> map, V value) {
		for (Entry<K, V> entry : map.entrySet()) {
			if (entry.getKey().equals(value)) {
				return (K) entry.getValue();
			}
		}
		return null;
	}

	/**
	 * タイヤメーカーの名称から略称を取得
	 *
	 * @param map
	 *            タイヤメーカーのmap(key:略称 value:名称)
	 * @param value
	 *            タイヤメーカーの名称
	 */
	private <K, V> K getTireMakerKey(Map<K, V> map, V value) {
		for (Entry<K, V> entry : map.entrySet()) {
			if (entry.getValue().equals(value)) {
				return (K) entry.getKey();
			}
		}
		return null;
	}

	// 2020/08/25 NEUT add end

	/**
	 * Called when the activity is first created.
	 *
	 * @param savedInstanceState
	 *            savedInstanceState
	 * */
	@SuppressLint({ "NewApi", "ResourceAsColor" })
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		try {
			// レイアウトの生成
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.kensa_tire);
			// ステータスバー削除
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

			// ボディNo
			strBodyNo = getIntent().getStringExtra(Common.INTENT_EX_BODYNO);
			// アイデントNo
			strIdno = getIntent().getStringExtra(Common.INTENT_EX_IDNO);
			// ラインオフ計画日
			strLoDate = getIntent().getStringExtra(Common.INTENT_EX_LO_DATE);
			// 検査順
			intItemNo = getIntent().getIntExtra(Common.INTENT_EX_GROUPCOUNT, 0);
			// 工程コード
			strGroupNo = getIntent().getStringExtra(Common.INTENT_EX_GROUPNO);
			// 検査データ一式
			mkensaItems = getIntent().getStringArrayExtra(Common.INTENT_EX_CHECKITEMS);

			tvKensaJun = (TextView) findViewById(R.id.KS_TV_KENSAJUN);
			tvGroupName = (TextView) findViewById(R.id.KS_TV_GROUPNAME);
			tvKensaItem = (TextView) findViewById(R.id.KS_TV_KENSAITEM);
			tvBodyNo = (TextView) findViewById(R.id.KS_TV_BODYNO);
			tvJyugyouinNo = (TextView) findViewById(R.id.KS_TV_JYUGYOIN);
			btMenu = (Button) findViewById(R.id.Menu_Button);
			btMae = (Button) findViewById(R.id.Mae_Button);
			btTsugi = (Button) findViewById(R.id.Tsugi_Button);

			for (int i = 0; i < btButton.length; i++) {
				btButton[i] = (Button) findViewById(BUTTONS[i + 3]);

			}

			btOther = (Button) findViewById(R.id.btOther);

			// NG内容コード初期化
			NGContentsCodeMap.put("0", "誤品");
			NGContentsCodeMap.put("1", "欠品");
			NGContentsCodeMap.put("2", "不要");
			NGContentsCodeMap.put("3", "その他");

			Cursor c = null, d = null;
			try {
				String sqlite = "";
				sqlite = "SELECT value FROM P_paramlist where name = 'jyugyouinNo'";

				d = LFA.getmDb().rawQuery(sqlite, null);
				if (d.moveToFirst()) {
					strJyugyoinNo = d.getString(0);
					if (strJyugyoinNo != null) {
						strJyugyoinName = InputCheck.getRightUserId(strJyugyoinNo);
					} else {
						strJyugyoinName = "";
					}
				} else {
					strJyugyoinName = "";
				}

				// 2014/12/02 DA del start
				// OKNG判定処理
				//checkOKNG();
				// 2014/12/02 DA del end

				String sql = "";
				sql = "SELECT * FROM P_ordersing " + " WHERE idno = '"
						+ strIdno + "'" + " AND loDate ='" + strLoDate
						+ "'" + " AND groupCode ='" + strGroupNo + "'"
						+ " AND ordersignNo ='" + mkensaItems[intItemNo] + "'";

				c = LFA.getmDb().rawQuery(sql, null);
				if (c.moveToFirst()) {
					strCheckItem = c.getString(DB_ORDER_SING.INDEX_ORDERSINGNAME);
					strCheckNaiyo = c.getString(DB_ORDER_SING.INDEX_ORDERSINGCONTENTS);
					strInputData = c.getString(DB_ORDER_SING.INDEX_INPUTDATA);
					// 2016/02/24 DA ins start
					strItemCode = c.getString(DB_ORDER_SING.INDEX_ITEMCODE);
					// 2016/02/24 DA ins start
					strNGContents = c.getString(DB_ORDER_SING.INDEX_NGCONTENTS);
					strCheckJun = c.getString(DB_ORDER_SING.INDEX_ORDERSINGNO);
					strResultFlg = c.getString(DB_ORDER_SING.INDEX_RESULTFLG);
					strGroupName = c.getString(DB_ORDER_SING.INDEX_GROUPNAME);
					strTireFlg = c.getString(DB_ORDER_SING.INDEX_TIREFLG);
					strimageFlg = c.getString(DB_ORDER_SING.INDEX_IMAGEFLG);

					// アスペクト比に応じた文字サイズ倍率を設定する
					double aspectScale;
					double aspectScaleFoot;
					switch (LFA.aspect) {
					case ASPECT_3_2:
						aspectScale = SCALE_3_2;
						aspectScaleFoot = SCALE_3_2_FOOT;
						break;
					case ASPECT_16_9:
						aspectScale = SCALE_16_9;
						aspectScaleFoot = SCALE_16_9_FOOT;
						break;
					case ASPECT_5_3:
						aspectScale = SCALE_5_3;
						aspectScaleFoot = SCALE_5_3_FOOT;
						break;
					default:
						aspectScale = SCALE_OTHER;
						aspectScaleFoot = SCALE_OTHER;
						break;
					}

					int txtSize; // テキストサイズ
					// 画面上部のテキストを設定する。
					txtSize = (int) Math.floor((SIZE_FONT_HEAD) /* * noimageScale */ * aspectScale);
					// 検査順
					if (strCheckJun != null) {
						tvKensaJun.setText(String.valueOf(Integer.parseInt(strCheckJun.trim())) + "/" + mkensaItems.length);
						tvKensaJun.setTextSize(txtSize);
					}
					// 工程名
					if (strGroupName != null) {
						tvGroupName.setText(strGroupName);
						tvGroupName.setTextSize(txtSize);
					}
					txtSize = (int) Math.floor((SIZE_FONT_DATA_MIDDLE) * aspectScale);
					// 検査項目名
					if (strCheckItem != null) {
						tvKensaItem.setText(strCheckItem);
						tvKensaItem.setTextSize(txtSize);
					}
					// 画面下部ボデー情報のテキスト設定
					txtSize = (int) Math.floor(SIZE_FONT_FOOT_BODY * aspectScaleFoot);
					if (strBodyNo != null) {
						tvBodyNo.setText("BNO." + strBodyNo);
						tvBodyNo.setTextSize(txtSize);
					}
					if (strJyugyoinName != null) {
						tvJyugyouinNo.setText(strJyugyoinName);
						tvJyugyouinNo.setTextSize(txtSize);
					}
					// 検査内容を"/"で分割する
					String[] strCheckNaiyoAry = strCheckNaiyo.split("/");

					// メーカーボタン間隔の調整
					if (strCheckNaiyoAry.length < 6) {
						for (int i = 0; i < strCheckNaiyoAry.length; i++) {
							LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
							layoutParams.topMargin = 70;
							btButton[i].setLayoutParams(layoutParams);
						}
					}

					// 2014/12/02 DA upd start
					createKensaRecord(strCheckNaiyoAry);

					//ボタンウィジェットに表示する
					boolean isFirst = true;		//最初の項目のみ状態を変える
					for (KensaRecord kensaData : mKensaList) {

						if (kensaData.button == null) {
							continue;
						}

						// 本番モードのときは、前回検査結果によって検査項目名およびボタン色を変える。
						// 訓練モードでは変えない
						if (strInputData != null && strInputData.equals(kensaData.checkNaiyo) && isFirst) {
							if (LFA.mode == LFA.ModeList.production) {
								if (strResultFlg == null) {
									// xml不正
								}
								else if (strResultFlg.equals(OK)) {
									kensaData.button.setBackgroundDrawable(getResources().getDrawable(R.drawable.green));
									tvKensaItem.setTextColor(getResources().getColor(R.color.GREEN));
								}
								else if (strResultFlg.equals(NG)) {
									kensaData.button.setBackgroundDrawable(getResources().getDrawable(R.drawable.red));
									tvKensaItem.setText(tvKensaItem.getText()
											+ "("
											+ NGContentsCodeMap.get(strNGContents)
											+ ")");
									tvKensaItem.setTextColor(getResources().getColor(R.color.RED));
								}
							}
							isFirst = false;
						}

						// タイヤメーカー名称をボタンにセット
						kensaData.button.setText(kensaData.hyojiNaiyo);
					}
					for (int i = mKensaList.size(); i < btButton.length; i++) {
						//使わないボタンを消す
						btButton[i].setVisibility(View.INVISIBLE);
					}

//					// 表示内容リスト作成
//					String[] strHyojiNaiyoAry = new String[strCheckNaiyoAry.length];
//
//					// 初期化
//					enumMakerCode enumTireMakerCoder = enumMakerCode.Other;
//					for (int i = 0; i < strCheckNaiyoAry.length; i++) {
//						try {
//							enumTireMakerCoder = enumMakerCode.valueOf(strCheckNaiyoAry[i].trim());
//						} catch (Exception e) {
//							// 何もしない
//						}
//						// 表示名がない場合、そのまま表示する
//						if (getTireMakerName(enumTireMakerCoder).equals("")) {
//							strHyojiNaiyoAry[i] = strCheckNaiyoAry[i].trim();
//						} else {
//							strHyojiNaiyoAry[i] = getTireMakerName(enumTireMakerCoder);
//						}
//					}
//
//					// 検査内容がNULLの場合、ボタンが０個表示
//					if (strCheckNaiyo != null && strCheckNaiyo.isEmpty()) {
//						for (int j = 0; j < btButton.length; j++) {
//							btButton[j].setVisibility(View.INVISIBLE);
//						}
//					} else {
//						// ループで、取得タイヤメーカーを設定する
//						for (int i = 1; i <= btButton.length; i++) {
//
//							if (i > strHyojiNaiyoAry.length) {
//								btButton[i - 1].setVisibility(View.INVISIBLE);
//							} else {
//								if (strInputData != null && strInputData.equals(strCheckNaiyoAry[i - 1].trim())) {
//									// 本番モードのときは、前回検査結果によって検査項目名およびボタン色を変える。
//									// 訓練モードでは変えない
//									if (LFA.mode == LFA.ModeList.production) {
//										if (strResultFlg == null) {
//											// xml不正
//										} else if (strResultFlg.equals(OK)) {
//											btButton[i - 1].setBackgroundDrawable(getResources().getDrawable(R.drawable.green));
//											tvKensaItem.setTextColor(getResources().getColor(R.color.GREEN));
//										} else if (strResultFlg.equals(NG)) {
//											btButton[i - 1].setBackgroundDrawable(getResources().getDrawable(R.drawable.red));
//											tvKensaItem.setText(tvKensaItem.getText()
//													+ "("
//													+ NGContentsCodeMap.get(strNGContents)
//													+ ")");
//											tvKensaItem.setTextColor(getResources().getColor(R.color.RED));
//										}
//									}
//									// タイヤメーカー名称をボタンにセット
//									btButton[i - 1].setText(strHyojiNaiyoAry[i - 1]);
//								// No.37 Add Itage Watanabe start
//								//検査内容がTOはあるが、TYがない場合の対応
//								} else if (strInputData != null && strInputData.equals("TY") && strCheckNaiyoAry[i - 1].trim().equals("TO")) {
//									// 本番モードのときは、前回検査結果によって検査項目名およびボタン色を変える。
//									// 訓練モードでは変えない
//									if (LFA.mode == LFA.ModeList.production) {
//										if (strResultFlg == null) {
//											// xml不正
//										} else if (strResultFlg.equals(OK)) {
//											btButton[i - 1].setBackgroundDrawable(getResources().getDrawable(R.drawable.green));
//											tvKensaItem.setTextColor(getResources().getColor(R.color.GREEN));
//										} else if (strResultFlg.equals(NG)) {
//											btButton[i - 1].setBackgroundDrawable(getResources().getDrawable(R.drawable.red));
//											tvKensaItem.setText(tvKensaItem.getText()
//													+ "("
//													+ NGContentsCodeMap.get(strNGContents)
//													+ ")");
//											tvKensaItem.setTextColor(getResources().getColor(R.color.RED));
//										}
//									}
//									// タイヤメーカー名称をボタンにセット
//									btButton[i - 1].setText(strHyojiNaiyoAry[i - 1]);
//								// No.37 Add Itage Watanabe end
//								} else {
//									btButton[i - 1].setText(strHyojiNaiyoAry[i - 1]);
//								}
//							}
//						}
//					}
					// 2014/12/02 DA upd end

					// 他ボタンの場合
					// 本番モードのときは、前回検査結果によって検査項目名およびボタン色を変える。
					// 訓練モードでは変えない
					if (LFA.mode == LFA.ModeList.production) {
						if (strResultFlg != null && strResultFlg.equals(NG)
								&& strInputData.equals(Others)) {
							btOther.setBackgroundDrawable(getResources().getDrawable(R.drawable.red));
							tvKensaItem.setText(tvKensaItem.getText()
									+ "("
									+ NGContentsCodeMap.get(strNGContents)
									+ ")");
							tvKensaItem.setTextColor(getResources().getColor(R.color.RED));
						}
					}

					// 2017/12/01 DA ins start
					// 撮影画像
					String fileName = c.getString(DB_ORDER_SING.INDEX_SHOTIMAGEFILENAME);
					showShotimage(fileName);
					String fileDate = c.getString(DB_ORDER_SING.INDEX_SHOTIMAGEFILEDATE);
					String vehicleName = c.getString(DB_ORDER_SING.INDEX_VEHICLENAME);
					String itemName = c.getString(DB_ORDER_SING.INDEX_ORDERSINGNAME);
					showShotimageInfo(fileDate, vehicleName, strBodyNo, itemName);
					// 2017/12/01 DA ins end
				}
			} catch (Exception e) {
				Log.e(LFA.TAG, e.toString(), e);
			} finally {
				try {
					if (d != null) {
						d.close();
						d = null;
					}
					if (c != null) {
						c.close();
						c = null;
					}
				} catch (Exception e) {
					Log.e(LFA.TAG, e.toString(), e);
				} finally {
					c = null;
					d = null;
				}
			}

			// ボタン初期非活性に設定
			Button btmae = (Button) findViewById(R.id.btMae);
			btmae.setEnabled(false);
			Button bttsugi = (Button) findViewById(R.id.btTsugi);
			bttsugi.setEnabled(false);

			// ボタンを登録
			Button button;
			for (int buttonId : BUTTONS) {
				button = (Button) findViewById(buttonId);
				button.setOnClickListener(this);
			}

//			//　マスタから取得したボタン
//			for(Button item : btButton) {
//				item.setOnClickListener(this);
//			}
//			// 他ボタン
//			button = (Button) findViewById(R.id.btOther);
//			button.setOnClickListener(this);
			setButton();
		} catch (RuntimeException e) {
			Log.e(LFA.TAG, e.getMessage(), e);
		}
	}

	/**
	 * ボタンクリックイベント
	 *
	 * @param v
	 *            ボタンイベント
	 */
	@SuppressLint({ "SimpleDateFormat", "NewApi" })
	public void onClick(View v) {

		// 二度押し対策
		if (!startProcess()) {
			return;
		}

		boolean isFinish = false;
		if (v.getId() == R.id.btMenu) {
			try {
				Intent intent = new Intent();
				intent.setClassName("LFA.Code2", "LFA.Code2.Menyu");
				intent.putExtra(Common.INTENT_EX_BODYNO, strBodyNo);
				intent.putExtra(Common.INTENT_EX_GROUPNO, strGroupNo);
				intent.putExtra(Common.INTENT_EX_GROUPNAME, strGroupName);
				startActivityForResult(intent, R.layout.menyu);

			} catch (Exception e) {
				Log.e(LFA.TAG, e.toString(), e);
			}
		} else if (v.getId() == R.id.btMae || v.getId() == R.id.btTsugi) {
			// 前ボタン、次ボタン押下時
			Cursor c = null;
			try {
				if (strimageFlg.equals("1")) {
					// 遷移先の項目番号
					int afterItemNo = v.getId() == R.id.btMae ? intItemNo - 1
							: intItemNo + 1;

					String sql = "";
					sql = "SELECT * FROM P_ordersing " + " WHERE idno = '"
							+ strIdno + "'" + " AND loDate ='"
							+ strLoDate + "'" + " AND groupCode ='"
							+ strGroupNo + "'" + " AND ordersignNo ='"
							+ mkensaItems[afterItemNo] + "'";

					c = LFA.getmDb().rawQuery(sql, null);
					if (c.moveToFirst()) {
						// タイヤフラグ
						strTireFlg = c.getString(DB_ORDER_SING.INDEX_TIREFLG);
						if (strTireFlg.equals("0")) {
							Intent intent = new Intent(getApplicationContext(), Kensa2.class);
							intent.putExtra(Common.INTENT_EX_GROUPCOUNT, afterItemNo);
							intent.putExtra(Common.INTENT_EX_CHECKITEMS, mkensaItems);
							intent.putExtra(Common.INTENT_EX_BODYNO, strBodyNo);
							intent.putExtra(Common.INTENT_EX_GROUPNO, strGroupNo);
							intent.putExtra(Common.INTENT_EX_IDNO, strIdno);
							intent.putExtra(Common.INTENT_EX_LO_DATE, strLoDate);
							startActivityForResult(intent, R.layout.kensa2);

							setResult(RESULT_OK);
							isFinish = true;
						} else if (strTireFlg.equals("1")) {
							Intent intent = new Intent(getApplicationContext(), KensaTire.class);
							intent.putExtra(Common.INTENT_EX_GROUPCOUNT, afterItemNo);
							intent.putExtra(Common.INTENT_EX_CHECKITEMS, mkensaItems);
							intent.putExtra(Common.INTENT_EX_BODYNO, strBodyNo);
							intent.putExtra(Common.INTENT_EX_GROUPNO, strGroupNo);
							intent.putExtra(Common.INTENT_EX_IDNO, strIdno);
							intent.putExtra(Common.INTENT_EX_LO_DATE, strLoDate);
							startActivityForResult(intent, R.layout.kensa_tire);

							setResult(RESULT_OK);
							isFinish = true;
						}
					}
				}
			} catch (Exception e) {
				Log.e(LFA.TAG, e.toString(), e);
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

		} else if (isMakerButton(v)) {
			// 押下されたボタンのタイヤメーカーの省略コード取得
			String strTireMakerCode = "";
			Button mbutton = (Button) v;
			String strButtonName = mbutton.getText().toString();

			if (!strButtonName.isEmpty()) {
				try {
					// 2020/08/25 NEUT upd start
					// enumMakerName enumTireMakerName = enumMakerName
					// .valueOf(strButtonName);


					strTireMakerCode = getTireMakerKey(tireMakers, strButtonName);

					ArrayList<HashMap<String, String>> list;

					String url = Common.REQ_URL + Common.KEY_TIREMAKERID;

					// タイヤメーカーを取得する
					String[] targets = {
							DB_TIREMAKER_LIST.Columns[DB_TIREMAKER_LIST.INDEX_TIREABBREVIATION],
							DB_TIREMAKER_LIST.Columns[DB_TIREMAKER_LIST.INDEX_TIRENAME] };

					list = Utils.getXmlTagsForMap(url, targets, "");



					for (int i = 0; i < list.size(); i++) {
						HashMap<String, String> map = list.get(i);
						// Set s =m.keySet();
						Entry<String, String> entry = map.entrySet().iterator()
								.next();

						tireMakers.put(entry.getKey(), entry.getValue());

					}
					// DMYをmasterに追加
					tireMakers.put(TIREMAKER_DMY,"");

					// 2020/08/25 NEUT upd end

				} catch (Exception e) {
					// 無い場合は、そのまま使用する
					strTireMakerCode = strButtonName;
				}
			}
			LFA.getmDb().beginTransaction();
			try {
				ContentValues values = new ContentValues();
				// 2014/04/07 DA ins start
				// 日付取得
				String strDate = "";
				SimpleDateFormat fomatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.S");
				Date dNow = new Date(System.currentTimeMillis());
				strDate = fomatter.format(dNow);
				String ngContents = "";

				values.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_RESULTFLG], OK);
				values.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_USERID], strJyugyoinNo);
				values.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_ORDERTIME], strDate);
				values.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_NGCONTENTS], ngContents);
				// 2014/04/07 DA ins end
				// 測定値を押下されたメーカーコードに更新する
				values.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_INPUTDATA], strTireMakerCode);

				String where = "idno = '" + strIdno + "'"
				+ " AND loDate = '" + strLoDate + "'"
				+ " AND groupCode = '" + strGroupNo + "'"
				+ " AND ordersignNo = '" + mkensaItems[intItemNo] + "'";

				LFA.getmDb().update("P_ordersing", values, where, null);

				// 2016/02/24 DA ins start
				// 検査履歴
				Cursor c = null;
				String sql = "";
				sql = "SELECT MAX(CAST(selectNumber as INT)) FROM P_ordersignHistory "
						+ " WHERE idno ='" + strIdno + "'"
						+ " AND loDate ='" + strLoDate + "'"
						+ " AND itemCode ='" + strItemCode + "'";
				c = LFA.getmDb().rawQuery(sql, null);

				// 選択回数
				int selectNumber = 0;
				if (c.moveToFirst()) {
					selectNumber = c.getInt(0);
				}
				++ selectNumber;

				// 検査履歴仮登録
				values = new ContentValues();
				values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_IDNO], strIdno);
				values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_LODATE], strLoDate);
				values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_ITEMCODE], strItemCode);
				values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_SELECT_NUMBER], selectNumber);
				values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_RESULTFLG], OK);
				values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_INPUTDATA], strTireMakerCode);
				values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_NGCONTENTS], ngContents);
				values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_USERID], strJyugyoinNo);
				values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_ORDERTIME], strDate);
				LFA.getmDb().insert("P_ordersignHistory", null, values);
				// 2016/02/24 DA ins end

				LFA.getmDb().setTransactionSuccessful();
			} catch (Exception e) {
				Log.e(LFA.TAG, e.toString(), e);
			} finally {
				LFA.getmDb().endTransaction();
			}

			// OKNG判定処理
			checkOKNG(strIdno, strLoDate, strGroupNo, strJyugyoinNo);

			// 次の検査がある且つ検査結果区分が1
			if (mkensaItems.length != intItemNo + 1 && strimageFlg != null && strimageFlg.equals("1")) {
				Cursor c = null;

				String sql = "";
				sql = "SELECT * FROM P_ordersing " + " WHERE idno = '"
						+ strIdno + "'" + " AND loDate ='"
						+ strLoDate + "'" + " AND groupCode ='"
						+ strGroupNo + "'" + " AND ordersignNo ='"
						+ mkensaItems[intItemNo + 1] + "'";

				try {
					c = LFA.getmDb().rawQuery(sql, null);
					if (c.moveToFirst()) {
						strTireFlg = c.getString(DB_ORDER_SING.INDEX_TIREFLG);
						if (strTireFlg.equals("0")) {
							Intent intent = new Intent(getApplicationContext(), Kensa2.class);
							intent.putExtra(Common.INTENT_EX_GROUPCOUNT, intItemNo + 1);
							intent.putExtra(Common.INTENT_EX_CHECKITEMS, mkensaItems);
							intent.putExtra(Common.INTENT_EX_BODYNO, strBodyNo);
							intent.putExtra(Common.INTENT_EX_GROUPNO, strGroupNo);
							intent.putExtra(Common.INTENT_EX_IDNO, strIdno);
							intent.putExtra(Common.INTENT_EX_LO_DATE, strLoDate);
							startActivityForResult(intent, R.layout.kensa2);

							setResult(RESULT_OK);
							isFinish = true;
						} else if (strTireFlg.equals("1")) {

							Intent intent = new Intent(getApplicationContext(), KensaTire.class);
							intent.putExtra(Common.INTENT_EX_GROUPCOUNT, intItemNo + 1);
							intent.putExtra(Common.INTENT_EX_CHECKITEMS, mkensaItems);
							intent.putExtra(Common.INTENT_EX_BODYNO, strBodyNo);
							intent.putExtra(Common.INTENT_EX_GROUPNO, strGroupNo);
							intent.putExtra(Common.INTENT_EX_IDNO, strIdno);
							intent.putExtra(Common.INTENT_EX_LO_DATE, strLoDate);
							startActivityForResult(intent, R.layout.kensa_tire);

							setResult(RESULT_OK);
							isFinish = true;
						}
					}
				} catch (Exception e) {
					Log.e(LFA.TAG, e.toString(), e);
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
			} else if (mkensaItems.length == intItemNo + 1) {
				// 次の検査項目がない場合
				Intent intent = new Intent(getApplicationContext(), UpLoad.class);
				intent.putExtra(Common.INTENT_EX_GROUPCOUNT, intItemNo);
				intent.putExtra(Common.INTENT_EX_BODYNO, strBodyNo);
				intent.putExtra(Common.INTENT_EX_GROUPNO, strGroupNo);
				intent.putExtra(Common.INTENT_EX_CHECKITEMS, mkensaItems);
				intent.putExtra(Common.INTENT_EX_IDNO, strIdno);
				intent.putExtra(Common.INTENT_EX_LO_DATE, strLoDate);
				intent.putExtra(Common.INTENT_EX_CHECKKARAMODORU, "kensaTire");
				startActivityForResult(intent, R.layout.upload);

				setResult(RESULT_OK);
				isFinish = true;
			}

		} else if (isDummyMakerButton(v)) {

			KensaRecord kensa = getKensaData(v);
			if (kensa != null) {
				LFA.getmDb().beginTransaction();
				try {
					ContentValues values = new ContentValues();
					values.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_INPUTDATA], kensa.checkNaiyo);
					String where = "idno = '" + strIdno + "'"
							+ " AND loDate = '" + strLoDate + "'"
							+ " AND groupCode = '" + strGroupNo + "'"
							+ " AND ordersignNo = '" + mkensaItems[intItemNo] + "'";

					LFA.getmDb().update("P_ordersing", values, where, null);
					LFA.getmDb().setTransactionSuccessful();
				}
				catch (Exception e) {
					Log.e(LFA.TAG, e.toString(), e);
				}
				finally {
					LFA.getmDb().endTransaction();
				}

				// 2014/12/02 DA del start
				// OKNG更新
				//checkOKNG();
				// 2014/12/02 DA del end

				// NG音
				SoundPoolPlayer.getInstance().play(this, false);

				Intent intent = new Intent(getApplicationContext(), Kensa.class);
				intent.putExtra(Common.INTENT_EX_GROUPCOUNT, intItemNo);
				intent.putExtra(Common.INTENT_EX_CHECKITEMS, mkensaItems);
				intent.putExtra(Common.INTENT_EX_BODYNO, strBodyNo);
				intent.putExtra(Common.INTENT_EX_GROUPNO, strGroupNo);
				intent.putExtra(Common.INTENT_EX_IDNO, strIdno);
				intent.putExtra(Common.INTENT_EX_LO_DATE, strLoDate);
				// 2016/02/24 DA upd start
				intent.putExtra(Common.INTENT_EX_INPUTDATA, kensa.checkNaiyo);
				// intent.putExtra(Common.INTENT_EX_INPUTDATA, strInputData);
				// 2016/02/24 DA upd end
				startActivityForResult(intent, R.layout.kensa);

				setResult(RESULT_OK);
				isFinish = true;
			}

		} else if (v == btOther) {
			LFA.getmDb().beginTransaction();
			try {
				ContentValues values = new ContentValues();
				// 測定値をOthersに更新する
				values.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_INPUTDATA], Others);
				String where = "idno = '" + strIdno + "'"
						+ " AND loDate = '" + strLoDate + "'"
						+ " AND groupCode = '" + strGroupNo + "'"
						+ " AND ordersignNo = '" + mkensaItems[intItemNo] + "'";

				LFA.getmDb().update("P_ordersing", values, where, null);
				LFA.getmDb().setTransactionSuccessful();
			} catch (Exception e) {
				Log.e(LFA.TAG, e.toString(), e);
			} finally {
				LFA.getmDb().endTransaction();
			}

			// 2014/12/02 DA del start
			// OKNG更新
			//checkOKNG();
			// 2014/12/02 DA del end

			// NG音
			SoundPoolPlayer.getInstance().play(this, false);

			Intent intent = new Intent(getApplicationContext(), Kensa.class);
			intent.putExtra(Common.INTENT_EX_GROUPCOUNT, intItemNo);
			intent.putExtra(Common.INTENT_EX_CHECKITEMS, mkensaItems);
			intent.putExtra(Common.INTENT_EX_BODYNO, strBodyNo);
			intent.putExtra(Common.INTENT_EX_GROUPNO, strGroupNo);
			intent.putExtra(Common.INTENT_EX_IDNO, strIdno);
			intent.putExtra(Common.INTENT_EX_LO_DATE, strLoDate);
			// 2016/02/24 DA upd start
			intent.putExtra(Common.INTENT_EX_INPUTDATA, "Others");
			// intent.putExtra(Common.INTENT_EX_INPUTDATA, strInputData);
			// 2016/02/24 DA upd end
			startActivityForResult(intent, R.layout.kensa);

			setResult(RESULT_OK);
			isFinish = true;
		} else {
			// 何もしない
		}

		// 終了フラグOFFなら終了しない
		if (!isFinish) {
			endProcess();
			return;
		} else {
			finish();
		}
	}

	/**
	 * タイヤメーカーボタンかどうかを判定
	 * @param v 判定対象のボタン
	 * @return true:タイヤメーカーボタン、false:タイヤメーカー以外
	 */
	private boolean isMakerButton(View v) {
		// 2014/12/02 DA upd start
//		for (Button b : btButton) {
//			if (v == b) {
//				return true;
//			}
//		}
		for (KensaRecord kensaData : mKensaList) {
			if (kensaData.button != null && kensaData.button == v) {
				if (kensaData.kensaNaiyo.equals(TIREMAKER_DMY) == false) {
					return true;
				}
			}
		}
		// 2014/12/02 DA upd end
		return false;
	}

	/**
	 * ダミータイヤメーカーボタンかどうかを判定
	 * @param v 判定対象のボタン
	 * @return true:タイヤメーカーボタン、false:タイヤメーカー以外
	 */
	private boolean isDummyMakerButton(View v) {
		for (KensaRecord kensaData : mKensaList) {
			if (kensaData.button != null && kensaData.button == v) {
				if (kensaData.kensaNaiyo.equals(TIREMAKER_DMY)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * タイヤメーカーボタンの情報を取得する
	 * @param v 対象のボタン
	 * @return タイヤメーカー情報、null:該当タイヤメーカー情報なし
	 */
	private KensaRecord getKensaData(View v) {
		for (KensaRecord kensaData : mKensaList) {
			if (kensaData.button != null && kensaData.button == v) {
				return kensaData;
			}
		}
		return null;
	}

	/**
	 * ページ遷移ボタン状態設定
	 */
	private void setButton() {
		// 前ボタン活性,次ボタン非活性
		if (mkensaItems.length != 1) {
			if (intItemNo != 0) {
				Button btmae = (Button) findViewById(R.id.btMae);
				btmae.setEnabled(true);
			}
			if (mkensaItems.length != intItemNo + 1) {
				Button bttsugi = (Button) findViewById(R.id.btTsugi);
				bttsugi.setEnabled(true);
			}
		}
	}

	/**
	 * onStartメソッド
	 */
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		if (resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		}
	}

	// オプションメニューの生成(1)
	/**
	 * @param menu
	 *            menu
	 * @return true
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		// メニューアイテム0の追加(2)
		menu.add(0, 0, 0, "メニュー");
		return true;
	}

	// メニューアイテム選択イベントの処理(3)
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ITEM0:
			Intent intent = new Intent();
			intent.setClassName("LFA.Code2", "LFA.Code2.Menyu");
			intent.putExtra(Common.INTENT_EX_BODYNO, strBodyNo);
			intent.putExtra(Common.INTENT_EX_GROUPNO, strGroupNo);
			intent.putExtra(Common.INTENT_EX_GROUPNAME, strGroupName);
			startActivityForResult(intent, R.layout.menyu);
			return true;
		default:
			return true;
		}
	}

	// 2020/08/25 NEUT add start
	/**
	 * タイヤメーカーリスト取得
	 *
	 * @return true or false
	 */
	public static boolean setTireMaker() {
		boolean blnFlg = false;

		// データの登録
		try {
			ArrayList<HashMap<String, String>> list;
			String url = Common.REQ_URL + Common.KEY_TIREMAKERID;

			// タイヤメーカーデータを取得する
			String[] targets = {// DB_USER_LIST.Columns[DB_USER_LIST.INDEX_ID],
			DB_USER_LIST.Columns[DB_USER_LIST.INDEX_USERID],
					DB_USER_LIST.Columns[DB_USER_LIST.INDEX_USERNAME] };

			list = Utils.getXmlTagsForMap(url, targets, "");

			if (list.size() > 0) {
				// updateUserData(list);
				blnFlg = true;
			} else {
				blnFlg = false;
			}

		} catch (Exception e) {
			Log.e(LFA.TAG, e.toString());
		}

		return blnFlg;
	}
	// 2020/08/25 NEUT add end

	// 2020/08/25 NEUT del start
	/**
	 * タイヤメーカー名全称を取得する
	 *
	 * @param enumTireMakerCoder
	 *            タイヤメーカー略称
	 * @return タイヤメーカー名全称
	 */
	// private String getTireMakerName(enumMakerCode enumTireMakerCoder) {
	// String value = "";
	// switch (enumTireMakerCoder) {
	// case BS:
	// value = "BRIDGESTONE";
	// break;
	// case DU:
	// value = "DUNLOP";
	// break;
	// case TY:
	// value = "TOYO";
	// break;
	// // 2017/03/02 CT del start
	// // case TO:
	// // value = "TOYO";
	// // break;
	// // 2017/03/02 CT del end
	// case YH:
	// value = "YOKOHAMA";
	// break;
	// case GY:
	// value = "GOODYEAR";
	// break;
	// case MI:
	// value = "MICHELIN";
	// break;
	// case PL:
	// value = "PIRELLI";
	// break;
	// case OH:
	// value = "OHTSU";
	// break;
	// case CT:
	// value = "CONTINENTAL";
	// break;
	// case FS:
	// value = "FIRESTONE";
	// break;
	// // 2017/03/02 CT ins start
	// case HK:
	// value = "HANKOOK";
	// break;
	// // 2017/03/02 CT ins end
	// default:
	// value = "";
	// }
	// return value;
	// }
	//
	// /**
	// * タイヤメーカー省略を取得する
	// *
	// * @param enumTireMakerName
	// * タイヤメーカー名全称
	// * @return タイヤメーカー名略称
	// */
	// private String getTireMakerCode(enumMakerName enumTireMakerName) {
	// String value = "";
	// switch (enumTireMakerName) {
	// case BRIDGESTONE:
	// value = "BS";
	// break;
	// case DUNLOP:
	// value = "DU";
	// break;
	// case TOYO:
	// value = "TY";
	// break;
	// case YOKOHAMA:
	// value = "YH";
	// break;
	// case GOODYEAR:
	// value = "GY";
	// break;
	// case MICHELIN:
	// value = "MI";
	// break;
	// case PIRELLI:
	// value = "PL";
	// break;
	// case OHTSU:
	// value = "OH";
	// break;
	// case CONTINENTAL:
	// value = "CT";
	// break;
	// case FIRESTONE:
	// value = "FS";
	// break;
	// // 2017/03/02 CT ins start
	// case HANKOOK:
	// value = "HK";
	// break;
	// // 2017/03/02 CT ins end
	// default:
	// value = "";
	// }
	// return value;
	// }
	// 2020/08/25 NEUT del end

	/**
	 * OKNG判断処理
	 */
	@SuppressLint("SimpleDateFormat")
	//private void checkOKNG() {
	public static void checkOKNG(String strIdno, String strLoDate, String strGroupNo, String strJyugyoinNo) {
		Cursor c = null;
		try {
			// 日付取得
			String strDate = "";
			SimpleDateFormat fomatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.S");
			Date dNow = new Date(System.currentTimeMillis());
			strDate = fomatter.format(dNow);

			String sql = "";
			// 2014/04/07 DA ins start
			// 検査項目を取得
			sql = "SELECT * FROM P_ordersing "
					+ " WHERE idno = '" + strIdno + "'"
					+ " AND loDate ='" + strLoDate + "'" + " AND groupCode ='"
					+ strGroupNo + "'" + " AND tireDiv ='" + 1
					+ "'";
			c = LFA.getmDb().rawQuery(sql, null);
			int ordersignCount = c.getCount();
			// 2014/04/07 DA ins end

			sql = "SELECT * FROM P_ordersing "
					+ " WHERE idno = '" + strIdno + "'"
					+ " AND loDate ='" + strLoDate + "'" + " AND groupCode ='"
					+ strGroupNo + "'" + " AND tireDiv ='" + 1
					+ "' AND inputData != ''";
			// 検査結果を取得
			c = LFA.getmDb().rawQuery(sql, null);

			// 2016/02/24 DA upd start 可読性が悪いためコメント行を削除
			// 測定値リスト
			List<List<String>> measurementList = new ArrayList<List<String>>();

			// 工程の全てのタイヤをチェック完了
			if (c.getCount() == ordersignCount) {
				sql = "SELECT inputdata , groupNo, ordersignNo, ordersignContents FROM P_ordersing "
						+ " WHERE idno = '" + strIdno + "'"
						+ " AND loDate ='" + strLoDate + "'" + " AND groupCode ='"
						+ strGroupNo + "'" + " AND tireDiv ='" + 1
						+ "' AND (inputData != 'Others' AND inputData != '')"
						+ " Order by ordersignNo";
				// 検索データ取得
				c = LFA.getmDb().rawQuery(sql, null);
				// 測定値をセットする
				while (c.moveToNext()) {
					String strInputdata = c.getString(c.getColumnIndex("inputData"));
					String strSignContents = c.getString(c.getColumnIndex("ordersignContents"));
					// 他ボタン押下の場合を排除する
					int selectStatus = LfaCommon.getTireSelectStatus(strInputdata, strSignContents);
					if (selectStatus == SELECTSTATUSLIST.CORRECTANSWER) {
						List<String> arrInputdata = new ArrayList<String>();
						arrInputdata.add(strInputdata);
						arrInputdata.add(c.getString(c.getColumnIndex("groupNo")));
						arrInputdata.add(c.getString(c.getColumnIndex("ordersignNo")));
						measurementList.add(arrInputdata);
					}
				}
				// 判定リスト構成:
				// [[測定値,回数,結果フラグ,NG内容],[測定値,回数,結果フラグ,NG内容],[...]...]
				// 測定値、回数を判定リストにセットする
				List<List<String>> hanteiList = LfaCommon.getTireJudgment(measurementList);

				// DBへ判定結果更新
				for (int i = 0; i < hanteiList.size(); i++) {
					ContentValues values = new ContentValues();
					LFA.getmDb().beginTransaction();
					try {
						String strResultFlg = hanteiList.get(i).get(LfaCommon.JUDGMENTLIST.RESULTFLG).toString();
						String strNGcontents = hanteiList.get(i).get(LfaCommon.JUDGMENTLIST.NGCONTENTS).toString();
						values.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_RESULTFLG], strResultFlg);
						values.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_NGCONTENTS], strNGcontents);
						String where = "idno = '" + strIdno + "'"
								+ " AND loDate = '" + strLoDate + "'"
								+ " AND groupCode = '" + strGroupNo + "'"
								+ " AND tireDiv ='" + 1 + "'"
								+ " AND inputData = '"
								+ hanteiList.get(i).get(LfaCommon.JUDGMENTLIST.INPUTDATA).toString() + "'";

						LFA.getmDb().update("P_ordersing", values, where, null);

						// 2016/02/24 DA ins start
						// 検査履歴
						values = new ContentValues();
						values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_RESULTFLG], strResultFlg);
						values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_NGCONTENTS], strNGcontents);
						where = "idno = '" + strIdno + "'"
								+ " AND loDate ='" + strLoDate + "'"
								+ " AND inputData = '"
								+ hanteiList.get(i).get(LfaCommon.JUDGMENTLIST.INPUTDATA).toString() + "'"
								+ " AND itemCode IN "
								+ " (SELECT itemCode FROM P_ordersing WHERE idno = '" + strIdno + "'"
								+ " AND loDate = '" + strLoDate + "'"
								+ " AND groupCode = '" + strGroupNo + "'"
								+ " AND tireDiv ='1')";

						LFA.getmDb().update("P_ordersignHistory", values, where, null);
						// 2016/02/24 DA ins end

						LFA.getmDb().setTransactionSuccessful();
					} catch (Exception e) {
						Log.e(LFA.TAG, e.toString(), e);
					} finally {
						LFA.getmDb().endTransaction();
					}
				}
			} else {
				// 何もしない
			}
		} catch (Exception e) {
			Log.e(LFA.TAG, e.toString(), e);
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
	}

//	2016/02/24 DA upd  可読性が悪いためコメント行を削除
//			// 2014/04/07 DA upd start
//			//List<String> arrInputdata = new ArrayList<String>();
//			List<List<String>> measurementList = new ArrayList<List<String>>();
//			//// 4本チェック完了した場合
//			//if (c.getCount() == 4) {
//			if (c.getCount() == ordersignCount) {
//			// 2014/04/07 DA upd end
//				sql = "SELECT inputdata , groupNo, ordersignNo, ordersignContents FROM P_ordersing "
//						+ " WHERE idno = '" + strIdno + "'"
//						+ " AND loDate ='" + strLoDate + "'" + " AND groupCode ='"
//						+ strGroupNo + "'" + " AND tireDiv ='" + 1
//						// 2014/04/07 DA upd start
//						//+ "' AND inputData != 'Others'" + " Order by ordersignNo";
//						+ "' AND (inputData != 'Others' AND inputData != '')"
//						+ " Order by ordersignNo";
//						// 2014/04/07 DA upd end
//				// 検索データ取得
//				c = LFA.getmDb().rawQuery(sql, null);
//				// 測定値をセットする
//				while (c.moveToNext()) {
//					String strInputdata = c.getString(c.getColumnIndex("inputData"));
//					String strSignContents = c.getString(c.getColumnIndex("ordersignContents"));
//					// 他ボタン押下の場合を排除する
//					// 2014/12/02 DA upd start
//					int selectStatus = LfaCommon.getTireSelectStatus(strInputdata, strSignContents);
//					if (selectStatus == SELECTSTATUSLIST.CORRECTANSWER) {
//					//if (strInputdata != null && !strInputdata.equals(Others)) {
//					// 2014/12/02 DA upd end
//						// 2014/04/07 DA ins start
//						List<String> arrInputdata = new ArrayList<String>();
//						// 2014/04/07 DA ins end
//						arrInputdata.add(strInputdata);
//						// 2014/04/07 DA ins start
//						arrInputdata.add(c.getString(c.getColumnIndex("groupNo")));
//						arrInputdata.add(c.getString(c.getColumnIndex("ordersignNo")));
//						measurementList.add(arrInputdata);
//						// 2014/04/07 DA ins end
//					}
//				}
//				// 2014/04/07 DA upd start
////				// 押下されたメーカーの回数をカウントする
////				int countA = 0;
////				int countB = 0;
////				int countC = 0;
////				int countD = 0;
////				List<String> arrInputdata2 = new ArrayList<String>();
////				arrInputdata2.addAll(arrInputdata);
////				for (int i = 0; i < arrInputdata.size(); i++) {
////					String inputdata = arrInputdata.get(i);
////					if (!inputdata.equals("")) {
////						for (int j = 0; j < arrInputdata.size(); j++) {
////							if (arrInputdata.get(j).equals(inputdata)) {
////								arrInputdata.set(j, "");
////								switch (i) {
////								case 0:
////									countA++;
////									break;
////								case 1:
////									countB++;
////									break;
////								case 2:
////									countC++;
////									break;
////								case 3:
////									countD++;
////									break;
////								}
////							}
////						}
////					}
////				}
////
//				// 判定リスト構成:
//				// [[測定値,回数,結果フラグ,NG内容],[測定値,回数,結果フラグ,NG内容],[...]...]
//				// 測定値、回数を判定リストにセットする
//				List<List<String>> hanteiList = LfaCommon.getTireJudgment(measurementList);
////				List<List> listHantei = new ArrayList<List>();
////				List<String> listIteam;
////				if (countA != 0) {
////					listIteam = new ArrayList<String>();
////					listIteam.add(arrInputdata2.get(0));
////					listIteam.add(String.valueOf(countA));
////					listHantei.add(listIteam);
////				}
////				if (countB != 0) {
////					listIteam = new ArrayList<String>();
////					listIteam.add(arrInputdata2.get(1));
////					listIteam.add(String.valueOf(countB));
////					listHantei.add(listIteam);
////				}
////				if (countC != 0) {
////					listIteam = new ArrayList<String>();
////					listIteam.add(arrInputdata2.get(2));
////					listIteam.add(String.valueOf(countC));
////					listHantei.add(listIteam);
////				}
////				if (countD != 0) {
////					listIteam = new ArrayList<String>();
////					listIteam.add(arrInputdata2.get(3));
////					listIteam.add(String.valueOf(countD));
////					listHantei.add(listIteam);
////				}
////
////				// 降順でソート
////				Collections.sort(listHantei, new Comparator<Object>() {
////					public int compare(Object lhs, Object rhs) {
////						int v1 = Integer.parseInt(((List) lhs).get(1).toString());
////						int v2 = Integer.parseInt(((List) rhs).get(1).toString());
////						return v2 - v1;
////					}
////				});
////
////				// 結果フラグ、NG内容を判定リストにセットする
////				for (int i = 0; i < listHantei.size(); i++) {
////					if (i == 0) {
////						listHantei.get(i).add(OK);
////						listHantei.get(i).add("");
////					} else {
////						listHantei.get(i).add(NG);
////						listHantei.get(i).add(Gohin);
////					}
////				}
//				// 2014/04/07 DA upd end
//
//				// DBへ判定結果更新
//				for (int i = 0; i < hanteiList.size(); i++) {
//					ContentValues values = new ContentValues();
//					// 2014/04/07 DA del start
////					// 四つボタン違ってメーカー押下された場合
////					if (listHantei.size() == 4) {
////						LFA.getmDb().beginTransaction();
////						try {
////							// 検索順が１のがＯＫ
////							if (i == 0) {
////								values.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_RESULTFLG], OK);
////								values.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_NGCONTENTS], "");
////							} else {
////								values.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_RESULTFLG], NG);
////								values.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_NGCONTENTS], Gohin);
////							}
////							values.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_USERID], strJyugyoinNo);
////							values.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_ORDERTIME], strDate);
////							String where = "frameCode = '" + strFrameDiv + "'"
////									+ " AND frameSeq = '" + strFrameNo + "'"
////									+ " AND groupCode = '" + strGroupNo + "'"
////									+ " AND tireDiv ='" + 1 + "'"
////									+ " AND inputData = '"
////									+ listHantei.get(i).get(0).toString() + "'";
////
////							LFA.getmDb().update("P_ordersing", values, where,null);
////							LFA.getmDb().setTransactionSuccessful();
////						} catch (Exception e) {
////							Log.e(LFA.TAG, e.toString(), e);
////						} finally {
////							LFA.getmDb().endTransaction();
////						}
////					} else {
////						// 以外の場合は測定値より、更新する
//					// 2014/04/07 DA del end
//						LFA.getmDb().beginTransaction();
//						try {
//							String strResultFlg = hanteiList.get(i).get(LfaCommon.JUDGMENTLIST.RESULTFLG).toString();
//							String strNGcontents = hanteiList.get(i).get(LfaCommon.JUDGMENTLIST.NGCONTENTS).toString();
//							values.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_RESULTFLG], strResultFlg);
//							values.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_NGCONTENTS], strNGcontents);
//							// 2014/12/02 DA del start
//							//values.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_USERID], strJyugyoinNo);
//							//values.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_ORDERTIME], strDate);
//							// 2014/12/02 DA del end
//							String where = "idno = '" + strIdno + "'"
//									+ " AND loDate = '" + strLoDate + "'"
//									+ " AND groupCode = '" + strGroupNo + "'"
//									+ " AND tireDiv ='" + 1 + "'"
//									+ " AND inputData = '"
//									+ hanteiList.get(i).get(LfaCommon.JUDGMENTLIST.INPUTDATA).toString() + "'";
//
//							LFA.getmDb().update("P_ordersing", values, where, null);
//							LFA.getmDb().setTransactionSuccessful();
//						} catch (Exception e) {
//							Log.e(LFA.TAG, e.toString(), e);
//						} finally {
//							LFA.getmDb().endTransaction();
//						}
//					// 2014/04/07 DA del start
////					}
//					// 2014/04/07 DA del end
//				}
//			} else {
//				// 何もしない
//			}
//		} catch (Exception e) {
//			Log.e(LFA.TAG, e.toString(), e);
//		} finally {
//			try {
//				if (c != null) {
//					c.close();
//					c = null;
//				}
//			} catch (Exception e) {
//				Log.e(LFA.TAG, e.toString());
//				e.printStackTrace();
//			} finally {
//				c = null;
//			}
//		}
//	}
//	2016/02/24 DA upd end 可読性が悪いためコメント行を削除


	/**
	 * ハードキーイベント処理
	 * @param keyCode ボタン認識
	 * @param event イベント
	 * @return false
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

	// 2017/12/01 DA ins start
	/**
	 * アクティビティ破棄時の処理
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// 画像解放する
		ImageView iview = (ImageView) findViewById(R.id.shotimage);
		if (iview != null) {
			iview.setImageBitmap(null);
		}

		super.onDestroy();
	}

	/**
	 * ハンドラ定義
	 */
	private final Handler handler = new Handler();

	/**
	 * タイマー
	 */
	private Timer timer = null;

	/**
	 * アクティビティを一時停止する
	 */
	@Override
	protected void onPause() {
		super.onPause();

		// 経過時間タイマーの終了
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	};

	/**
	 * アクティビティを再開する
	 */
	@Override
	protected void onResume() {
		super.onResume();

		// 経過時間タイマーの開始
		final TextView timerText = (TextView) findViewById(R.id.tacttime);
		final TextView timerLabel = (TextView) findViewById(R.id.tacttimeLabel);

		if (isLandscapeOrientation() && timer == null) {
			timer = new Timer(false);
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					handler.post(new Runnable() {
						public void run() {
							long sa = getTacttime();
							long mm = sa / 60;
							long ss = sa % 60;
							if (mm >= 100) {
								mm = 99;
								ss = 99;
							}

							// 描画処理
							if (timerText != null) {
								timerText.setText(String.format("%1$01d:%2$02d", mm, ss));
								if (sa > sidown.getTacttime() && sidown.getTacttime() != -1) {
									timerText.setTextColor(Color.RED);
									timerLabel.setTextColor(Color.RED);
								}
								else {
									timerText.setTextColor(Color.WHITE);
									timerLabel.setTextColor(Color.WHITE);
								}
							}
						}
					});
				}
			}, 0, 1000);
		}
	}

	/**
	 * 車両情報の表示
	 *
	 * @param fileDate 撮影日時
	 * @param vehicleName 車種名
	 * @param bodyNo ボデーNO
	 * @param itemName 項目名称
	 */
	private void showShotimageInfo(String fileDate, String vehicleName, String bodyNo, String itemName)
	{
		TextView tview;
		tview = (TextView) findViewById(R.id.shotimageFileDate);
		if (tview != null) {
			tview.setText(fileDate);
		}

		tview = (TextView) findViewById(R.id.vehicleName);
		if (tview != null) {
			tview.setText(vehicleName);
		}

		tview = (TextView) findViewById(R.id.bodyNo);
		if (tview != null) {
			tview.setText(bodyNo);
		}

		tview = (TextView) findViewById(R.id.itemName);
		if (tview != null) {
			tview.setText(itemName);
		}
	}

	/**
	 * 画像の表示
	 *
	 * @param imageFile 撮影画像ファイル
	 */
	private void showShotimage(String imageFile)
	{
		String ret = null;

		ImageView iview = (ImageView) findViewById(R.id.shotimage);
		if (iview == null) {
			return;
		}

		Bitmap bmp = null;
		iview.setImageBitmap(null);
		if (imageFile == null || imageFile.equals("")) {
			ret = getString(R.string.shotimageUnregistered); // 撮影画像未登録
		}
		else {
			try {
				File file = Utils.getFile(this, imageFile);
				if (file.exists()) {
					boolean isImage = ResizedShotimageCache.isImage(file.getAbsolutePath());
					if (isImage) {
						bmp = ResizedShotimageCache.getImage(file.getAbsolutePath(), file.lastModified());
					}
					else {
						ret = getString(R.string.shotimageError); // 撮影画像ファイルの読み取りに失敗しました
					}
				}
				else {
					ret = getString(R.string.shotimageNofile); // 撮影画像ダウンロード中。または、撮影画像未登録
				}
			}
			catch (Error e) {
				// 画像ファイルでない場合
				ret = getString(R.string.shotimageError); // 撮影画像ファイルの読み取りに失敗しました
				Log.e(LFA.TAG, e.toString(), e);
			}
			catch (Exception e) {
				ret = getString(R.string.shotimageError); // 撮影画像ファイルの読み取りに失敗しました
				Log.e(LFA.TAG, e.toString(), e);
			}
		}
		iview.setImageBitmap(bmp);

		TextView tview = (TextView) findViewById(R.id.shotimageMsg);
		if (tview != null) {
			if (bmp == null) {
				tview.setText(ret);
				tview.setVisibility(View.VISIBLE);
				iview.setVisibility(View.GONE);
			}
			else {
				tview.setVisibility(View.GONE);
				iview.setVisibility(View.VISIBLE);
			}
		}
	}
	// 2017/12/01 DA ins end
}