package LFA.Code2;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import LFA.Code2.R;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 検査入力画面　選択方式
 * <ul>
 * <li>2012/02/21・・・仕様検査オフライントライ#2対応
 * <ul>
 * <li>画面レイアウト変更</li>
 * <li>サイドキー押下によるOK入力対応</li>
 * <li>OK入力時の振動対応</li>
 * <li>カーソルクローズ忘れ対応</li>
 * </ul>
 * </li>
 * <li>2012/02/28・・・MediaPlayer解放漏れ対応</li>
 * <li>2012/03/19・・・MediaPlayer -> SoundPoolへ音の再生を変更(シングルトン化してインスタンス生成コストも削減)</li>
 * </ul>
 *
 * @author cj01779
 * @since unknown
 * @version labo(2012/02/28)
 */
public class Kensa2 extends BaseActivity implements OnTouchListener, OnClickListener {

	/**
	 * 画面上部に表示する情報のフォントサイズ
	 */
	private static final int SIZE_FONT_HEAD = 14;

	/**
	 * 検査情報のフォントサイズ。
	 */
	private static final int SIZE_FONT_DATA_MIDDLE = 22;

	/**
	 * 検査情報項目のフォントサイズ。
	 */
	private static final int SIZE_FONT_DATA_LARGE = 24;

	/**
	 * 画面下部に表示するボデー情報のフォントサイズ
	 */
	private static final int SIZE_FONT_FOOT_BODY = 14;

	// 2014/04/07 DA ins start
	/**
	 * テキストビューで使用する大フォントサイズ
	 */
	private static final double SIZE_FONT_LARGE = 54;

	/**
	 * テキストビューで使用する中フォントサイズ
	 */
	private static final double SIZE_FONT_MIDDLE = 40;

	/**
	 * テキストビューで使用する小フォントサイズ
	 */
	private static final double SIZE_FONT_SMALL = 24;

	/**
	 * 大フォントサイズを使用するテキストビューの文字数（以下）
	 */
	private static final double SIZE_LENGTH_LARGE = 5;

	/**
	 * 中フォントサイズを使用するテキストビューの文字数（以上以下）
	 */
	private static final double SIZE_LENGTH_MIDDLE = 10;

	/**
	 * 小フォントサイズを使用するテキストビューの文字数（以上）
	 */
	private static final double SIZE_LENGTH_SMALL = 11;
	// 2014/04/07 DA ins end

	/**
	 * アスペクト比3:2の時の文字サイズ倍率
	 */
	private static final double SCALE_3_2 = 1.0;

	/**
	 * アスペクト比16:9の時の文字サイズ倍率
	 */
	private static final double SCALE_16_9 = 1.5;

	/**
	 * アスペクト比5:3の時の文字サイズ倍率
	 */
	private static final double SCALE_5_3 = 1.3;

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
	 * カウント定義
	 */
	static int iCnt = 0;

	/**
	 * ボタンの定義
	 */
	static final int[] BUTTONS = { R.id.Menu_Button, R.id.Mae_Button,
			R.id.Tsugi_Button, R.id.ImageButton01, R.id.ImageButton02,
			R.id.OtherButton };

	// 2014/04/07 DA ins start
	/**
	 * クリック対象のテキストビューの定義
	 */
	static final int[] TEXTVIEW = {
			R.id.tvImageKensaNaiyo1, R.id.tvImageKensaNaiyo2,
			};
	// 2014/04/07 DA ins end

	/**
	 * TextView tvGroupName定義
	 */
	TextView tvGroupName;

	/**
	 * TextView tvKensaJun定義
	 */
	TextView tvKensaJun;

	/**
	 * TextView tvKensaItem定義
	 */
	TextView tvKensaItem;

	/**
	 * TextView tvBodyNo定義
	 */
	TextView tvBodyNo;

	/**
	 * TextView tvJyugyoin定義
	 */
	TextView tvJyugyoin;

	/**
	 * グループ名格納変数
	 */
	private String strGroupName;

	/**
	 * グループCode格納変数
	 */
	private String strGroupCode;

	/**
	 * 従業員NO格納変数
	 */
	private String strJyugyoinNo;

	/**
	 * 検査項目格納配列
	 */
	private String[] mkensaItems;

	/**
	 * 検査順格納変数
	 */
	private int intItemNo = 0;

	/**
	 * ボデーNO格納変数
	 */
	private String strBodyNo;

	/**
	 * 検査順格納変数
	 */
	private String strCheckItem;

	/**
	 * 検査内容格納変数
	 */
	private String strCheckNaiyo;

	/**
	 * 検査内容格納変数（ダミー）
	 */
	private String strCheckNaiyo2;

	/**
	 * チェック順格納変数
	 */
	private String strCheckJun;

	/**
	 * 検査履歴格納変数
	 */
	private String strordersingFlg;

	/**
	 * 従業員名格納変数
	 */
	private String strJyugyoinName = "";

	/**
	 * 表示時にボタンを一時非活性にするため使用
	 */
	private boolean blnOnload;

	/**
	 * 検査区分格納変数
	 */
	private String strimageFlg = "";

	/**
	 * 上画像ボタンの定義
	 */
	ImageButton btSel1;

	/**
	 * 下画像ボタンの定義
	 */
	ImageButton btSel2;

	/**
	 * 正解ボタンの定義
	 */
	ImageButton btOK;

	/**
	 * ダミーボタン（NG）の定義
	 */
	ImageButton btNG;

	// 2014/04/07 DA ins start
	/**
	 * 上検査画像内容の定義
	 */
	TextView tvImageKensaNaiyo1;

	/**
	 * 下検査画像内容の定義
	 */
	TextView tvImageKensaNaiyo2;

	/**
	 * 正解テキストの定義
	 */
	TextView tvOK;

	/**
	 * ダミーテキスト（NG）の定義
	 */
	TextView tvNG;

	/**
	 * 検査内容の正解テキストの定義
	 */
	TextView tvKensaNaiyoOK;

	/**
	 * 検査内容のダミーテキスト（NG）の定義
	 */
	TextView tvKensaNaiyoNG;
	// 2014/04/07 DA ins end

	/**
	 * その他ボタンの定義
	 */
	Button btOther;

	/**
	 * ライナーレイアウトの定義
	 */
	LinearLayout layout;

	/**
	 * フレームレイアウトの定義
	 */
	FrameLayout framelayout;

	/**
	 * 画像ファイル名格納
	 */
	private String strimageName = "";

	/**
	 * 画像ファイル名格納変数（ダミー）
	 */
	private String strimageName2;

	/**
     *　OK/NG検査区分
	 */
	private String strOkNgDiv = "";

	/**
	 *　タイヤメーカー検査区分
	 */
	private String strTireDiv = "";

	/**
	　*　LO日
	 */
	private String strlodate = "";

	/**
	 *　受信日表示用
	 */
	private String outdataTime = "";

	/**
	 *　測定値
	 */
	private String strInputData = "";

	// 2016/02/24 DA ins start
	/**
	 *　項目Code
	 */
	private String strItemCode = "";
	// 2016/02/24 DA ins start

	/**
	 *　指示記号
	 */
	private String strBcSign = "";

	/**
	 *　ダミー指示記号
	 */
	private String strBcSign2 = "";

	/**
	 * NG内容
	 */
	private String strNgCheck = "";

	/**
	 * NG内容(漢字)
	 */
	private String strNgContent = "";

	/**
	 * アイデントNo
	 */
	private String strIdno = "";

	/**
	 * ラインオフ計画日
	 */
	private String strLoDate = "";

	/**
	 * 結果フラグ
	 */
	private String strResultFlg = "";

	/**
	 * メニューアイテムID
	 */
	private static final int MENU_ITEM0 = 0;

	/**
	 * 本体振動時間(ミリ秒)
	 *
	 * @author CJ01915
	 * @version labo
	 * @since labo
	 */
	private static final int VIBLATE_TIME = 75;

	/**
	 * 初回ボタン有効化時間(ミリ秒)
	 *
	 * @author CJ01915
	 * @version labo
	 * @since labo
	 */
	private static final int ENABLED_TIME_FIRST = 200;

	/**
	 * ページ遷移座標差制限
	 *
	 * @author CJ01915
	 * @version labo
	 * @since labo
	 */
	private static final int LIMIT_DIFF = 150;

	/**
	 * タッチ座標管理（X座標）
	 *
	 * @author CJ01915
	 * @version labo
	 * @since labo
	 */
	private float posX = 0;

	/**
	 * タッチ座標管理（Y座標）
	 *
	 * @author CJ01915
	 * @version labo
	 * @since labo
	 */
	private float posY = 0;

	/**
	 * 自動OKにする検査内容値
	 *
	 * @param savedInstanceState {@inheritDoc}
	 * @author CJ01915
	 * @version labo
	 * @since labo
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			// レイアウトの生成
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.kensa2);
			// ステータスバー削除
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

			strBodyNo = getIntent().getStringExtra(Common.INTENT_EX_BODYNO);
			strGroupCode = getIntent().getStringExtra(Common.INTENT_EX_GROUPNO);
			mkensaItems = getIntent().getStringArrayExtra(Common.INTENT_EX_CHECKITEMS);
			intItemNo = getIntent().getIntExtra(Common.INTENT_EX_GROUPCOUNT, 0);
			strIdno = getIntent().getStringExtra(Common.INTENT_EX_IDNO);
			strLoDate = getIntent().getStringExtra(Common.INTENT_EX_LO_DATE);
			tvGroupName = (TextView) findViewById(R.id.KS_TV_GROUPNAME);
			tvKensaJun = (TextView) findViewById(R.id.KS_TV_KENSAJUN);
			tvKensaItem = (TextView) findViewById(R.id.KS_TV_KENSAITEM);
			tvBodyNo = (TextView) findViewById(R.id.KS_TV_BODYNO);
			btSel1 = (ImageButton) findViewById(R.id.ImageButton01);
			btSel2 = (ImageButton) findViewById(R.id.ImageButton02);
			btOther = (Button) findViewById(R.id.OtherButton);
			layout = (LinearLayout) findViewById(R.id.Layout2);
			framelayout = (FrameLayout) findViewById(R.id.FrameLayout02);
			// 2014/04/07 DA ins start
			tvImageKensaNaiyo1 = (TextView) findViewById(R.id.tvImageKensaNaiyo1);
			tvImageKensaNaiyo2 = (TextView) findViewById(R.id.tvImageKensaNaiyo2);
			// 2014/04/07 DA ins end

			Cursor c = null, d = null;
			try {
				String sqlite = "";
				sqlite = "SELECT value FROM P_paramlist where name "
						+ "='jyugyouinNo'";
				d = LFA.getmDb().rawQuery(sqlite, null);
				if (d.moveToFirst()) {
					strJyugyoinNo = d.getString(0);
					if (strJyugyoinNo != null) {
						strJyugyoinName = InputCheck
								.getRightUserId(strJyugyoinNo);
					} else {
						strJyugyoinName = "";
					}
				} else {
					strJyugyoinName = "";
				}
				String sql = "";
				sql = "SELECT * FROM P_ordersing " + " WHERE groupCode ='"
						+ strGroupCode + "'" + " AND ordersignNo ='"
						+ mkensaItems[intItemNo].toString() + "'"
						+ " AND idno ='" + strIdno + "'"
						+ " AND loDate ='" + strLoDate + "'";

				c = LFA.getmDb().rawQuery(sql, null);
				if (c.moveToFirst()) {
					strCheckItem = c.getString(DB_ORDER_SING.INDEX_ORDERSINGNAME);
					strCheckJun = c.getString(DB_ORDER_SING.INDEX_ORDERSINGNO);
					strimageFlg = c.getString(DB_ORDER_SING.INDEX_IMAGEFLG);
					strGroupName = c.getString(DB_ORDER_SING.INDEX_GROUPNAME);
					strNgCheck = c.getString(DB_ORDER_SING.INDEX_NGCONTENTS);
					strResultFlg = c.getString(DB_ORDER_SING.INDEX_RESULTFLG);
					strlodate = c.getString(DB_ORDER_SING.INDEX_LODATE);
					strOkNgDiv = c.getString(DB_ORDER_SING.INDEX_OKNGFLG);
					strTireDiv = c.getString(DB_ORDER_SING.INDEX_TIREFLG);
					strInputData = c.getString(DB_ORDER_SING.INDEX_INPUTDATA);
					// 2016/02/24 DA ins start
					strItemCode = c.getString(DB_ORDER_SING.INDEX_ITEMCODE);
					// 2016/02/24 DA ins start

					// ダミーチェック項目かどうかでOK/NGの画像、指示内容、記号を入れ替える
					if (strOkNgDiv.equals("2")) {
						strCheckNaiyo = c.getString(DB_ORDER_SING.INDEX_ORDERSINGCONTENTS2);
						strCheckNaiyo2 = c.getString(DB_ORDER_SING.INDEX_ORDERSINGCONTENTS);
						strimageName = c.getString(DB_ORDER_SING.INDEX_FILENAME2);
						strimageName2 = c.getString(DB_ORDER_SING.INDEX_FILENAME);
						strBcSign = c.getString(DB_ORDER_SING.INDEX_BCSIGN2);
						strBcSign2 = c.getString(DB_ORDER_SING.INDEX_BCSIGN);
					} else {
						strCheckNaiyo = c.getString(DB_ORDER_SING.INDEX_ORDERSINGCONTENTS);
						strCheckNaiyo2 = c.getString(DB_ORDER_SING.INDEX_ORDERSINGCONTENTS2);
						strimageName = c.getString(DB_ORDER_SING.INDEX_FILENAME);
						strimageName2 = c.getString(DB_ORDER_SING.INDEX_FILENAME2);
						strBcSign = c.getString(DB_ORDER_SING.INDEX_BCSIGN);
						strBcSign2 = c.getString(DB_ORDER_SING.INDEX_BCSIGN2);
					}

					if (strlodate != null && !strlodate.equals("")) {
						outdataTime = strlodate.substring(0, 4) + "/"
								+ strlodate.substring(4, 6) + "/"
								+ strlodate.substring(6, 8);
					}
					if (strNgCheck.equals("0")) {
						strNgContent = "誤品";
					} else if (strNgCheck.equals("1")) {
						strNgContent = "欠品";
					} else if (strNgCheck.equals("2")) {
						strNgContent = "不要";
					} else if (strNgCheck.equals("3")) {
						strNgContent = "その他";
					}

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

					int txtSize;	//テキストサイズ
					//画面上部のテキストを設定する。
					txtSize = (int) Math.floor((SIZE_FONT_HEAD) /* * noimageScale */ * aspectScale);

					if (strGroupName != null) {
						tvGroupName.setText(strGroupName);
						tvGroupName.setTextSize(txtSize);
					}
					if (strCheckJun != null) {
						tvKensaJun.setText(String.valueOf(Integer.parseInt(strCheckJun.trim())) + "/" + mkensaItems.length);
						tvKensaJun.setTextSize(txtSize);
					}

					txtSize = (int) Math.floor((SIZE_FONT_DATA_LARGE)
							* aspectScale);

					// 画面下部ボデー情報のテキスト設定
					txtSize = (int) Math.floor(SIZE_FONT_FOOT_BODY
							* aspectScaleFoot);
					if (strBodyNo != null) {
						tvBodyNo.setText("BNO." + strBodyNo + " "
								+ strJyugyoinName + " " + outdataTime
								+ " "
								+ strLoDate);
						tvBodyNo.setTextSize(txtSize);
					}

					// 2014/04/07 DA ins start
					boolean isExist = false;
					// 2014/04/07 DA ins end
					TextView tv1 = (TextView) findViewById(R.id.TextView01);
					TextView tv2 = (TextView) findViewById(R.id.TextView02);

					if (strOkNgDiv.equals("1") || strCheckNaiyo2.equals("")) {
						// 一択式フラグがON、もしくはダミー画像無しの場合、
						// 上ボタンを正解、下ボタンは無効化する
						btOK = btSel1;
						btNG = btSel2;
						// 2014/04/07 DA ins start
						tvOK = tvImageKensaNaiyo1;
						tvNG = tvImageKensaNaiyo2;
						tvKensaNaiyoOK = tv1;
						tvKensaNaiyoNG = tv2;
						// 2014/04/07 DA ins end
						tv1.setText(strCheckNaiyo);
						framelayout.removeView(tv2);

						// ボタンに画像を表示
						// 2014/04/07 DA ins start
						if(strimageName != null && !strimageName.equals("")) {
						// 2014/04/07 DA ins end
							setImageToButton(btOK, strimageName, true);
						// 2014/04/07 DA ins start
							isExist = true;
						}
						else {
							isExist = false;
						}
						if(isExist) {
							tvOK.setVisibility(View.INVISIBLE);
							tvKensaNaiyoOK.setVisibility(View.VISIBLE);
						}
						else {
							tvOK.setVisibility(View.VISIBLE);
							tvKensaNaiyoOK.setVisibility(View.INVISIBLE);
						}
						// 2014/04/07 DA ins end
						setImageToButton(btNG, null, false);
						// 2014/04/07 DA ins start
						tvNG.setVisibility(View.INVISIBLE);
						// 2014/04/07 DA ins end

					} else {
						// 上下ランダムに画像表示する
						final double sikii = 0.5;
						if (Math.random() < sikii) {
							btOK = btSel1;
							btNG = btSel2;
							// 2014/04/07 DA ins start
							tvOK = tvImageKensaNaiyo1;
							tvNG = tvImageKensaNaiyo2;
							tvKensaNaiyoOK = tv1;
							tvKensaNaiyoNG = tv2;
							// 2014/04/07 DA ins end
							// 2014/04/07 DA del start
							//tv1.setText(strCheckNaiyo);
							//tv2.setText(strCheckNaiyo2);
							// 2014/04/07 DA del end
						} else {
							btOK = btSel2;
							btNG = btSel1;
							// 2014/04/07 DA ins start
							tvOK = tvImageKensaNaiyo2;
							tvNG = tvImageKensaNaiyo1;
							tvKensaNaiyoOK = tv2;
							tvKensaNaiyoNG = tv1;
							// 2014/04/07 DA ins end
							// 2014/04/07 DA del start
							//tv1.setText(strCheckNaiyo2);
							//tv2.setText(strCheckNaiyo);
							// 2014/04/07 DA del end
						}

						// 2014/04/07 DA ins start
						// 検査内容を表示
						tvKensaNaiyoOK.setText(strCheckNaiyo);
						tvKensaNaiyoNG.setText(strCheckNaiyo2);
						// 2014/04/07 DA ins end

						// ボタンに画像を表示
						// 2014/04/07 DA ins start
						if(strimageName != null && !strimageName.equals("")) {
						// 2014/04/07 DA ins end
							setImageToButton(btOK, strimageName, true);
						// 2014/04/07 DA ins start
							isExist = true;
						}
						else {
							isExist = false;
						}
						if(isExist) {
							tvOK.setVisibility(View.INVISIBLE);
							tvKensaNaiyoOK.setVisibility(View.VISIBLE);
						}
						else {
							tvOK.setVisibility(View.VISIBLE);
							tvKensaNaiyoOK.setVisibility(View.INVISIBLE);
						}
						if(strimageName2 != null && !strimageName2.equals("")) {
							setImageToButton(btNG, strimageName2, false);
							isExist = true;
						}
						else {
							isExist = false;
						}
						if(isExist) {
							tvNG.setVisibility(View.INVISIBLE);
							tvKensaNaiyoNG.setVisibility(View.VISIBLE);
						}
						else {
							tvNG.setVisibility(View.VISIBLE);
							tvKensaNaiyoNG.setVisibility(View.INVISIBLE);
						}
						// 2014/04/07 DA ins end
					}

					// 検査画像内容を表示
					// 2016/02/24 DA upd start
					if ((tvOK.getVisibility() == View.VISIBLE) && (tvNG.getVisibility() == View.VISIBLE)) {
						int maxlen = Math.max(strCheckNaiyo.length(), strCheckNaiyo2.length());
						setTextView(tvOK, strCheckNaiyo, aspectScale, maxlen);
						setTextView(tvNG, strCheckNaiyo2, aspectScale, maxlen);

					}
					else {
						setTextView(tvOK, strCheckNaiyo,  aspectScale);
						setTextView(tvNG, strCheckNaiyo2, aspectScale);
					}
					// setTextView(tvOK, strCheckNaiyo,  aspectScale);
					// setTextView(tvNG, strCheckNaiyo2, aspectScale);
					// 2016/02/24 DA upd end

					// 検査項目文字サイズ設定
					txtSize = (int) Math.floor((SIZE_FONT_DATA_MIDDLE) * aspectScale);
					tvKensaItem.setTextSize(txtSize);

					// 検査項目テキスト設定
					tvKensaItem.setText(strCheckItem);

					// 本番モードのときは、前回検査結果によって検査項目名およびボタン色を変える。
					// 訓練モードでは変えない
					if (LFA.mode == LFA.ModeList.production) {
						if (strInputData.equals(strBcSign) && !strInputData.equals("")) {
							tvKensaItem.setTextColor(getResources().getColor(R.color.GREEN));
							btOK.setBackgroundDrawable(getResources().getDrawable(R.drawable.green));

						} else if (strResultFlg.equals("0") && strInputData.equals("")) {
							//検査OKで、測定値がブランク（指示記号なし）の場合
							tvKensaItem.setTextColor(getResources().getColor(R.color.GREEN));
							btOK.setBackgroundDrawable(getResources().getDrawable(R.drawable.green));

						} else if (strInputData.equals(strBcSign2) && !strInputData.equals("")) {
							tvKensaItem.setText(strCheckItem + "(" + strNgContent + ")");
							tvKensaItem.setTextColor(getResources().getColor(R.color.RED));
							btNG.setBackgroundDrawable(getResources().getDrawable(R.drawable.red));

						} else if (strInputData.equals("Others")) {
							tvKensaItem.setText(strCheckItem + "(" + strNgContent + ")");
							tvKensaItem.setTextColor(getResources().getColor(R.color.RED));
							btOther.setBackgroundDrawable(getResources().getDrawable(R.drawable.red));
						}
					}

					strordersingFlg = c.getString(DB_ORDER_SING.INDEX_ORDERSINGFLG);

					if (strordersingFlg.equals("1")) {
						// 背景色を赤色
						layout.setBackgroundColor(Color.RED);
					}
					Button btmae = (Button) findViewById(R.id.Mae_Button);
					btmae.setEnabled(false);
					Button bttsugi = (Button) findViewById(R.id.Tsugi_Button);
					bttsugi.setEnabled(false);

					// ボタン無効化
					btSel1.setEnabled(false);
					btSel2.setEnabled(false);
					// 2014/04/07 DA ins start
					tvImageKensaNaiyo1.setEnabled(false);
					tvImageKensaNaiyo2.setEnabled(false);
					// 2014/04/07 DA ins end
					btOther.setEnabled(false);

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

			// ボタンを登録
			for (int buttonId : BUTTONS) {
				View button = (View) findViewById(buttonId);
				// button.setSoundEffectsEnabled(true);
				/*
				 * クリック -> タッチイベント割り当てによる操作高速化
				 *
				 * @author CJ01915
				 *
				 * @version labo(2012/02/21)
				 *
				 * @since labo(2012/02/21)
				 */
				button.setOnClickListener(this);
				button.setOnTouchListener(this);
			}

			// 2014/04/07 DA ins start
			// テキストビューにイベントを登録
			for (int textviewid : TEXTVIEW) {
				View textview = (View) findViewById(textviewid);
				textview.setOnClickListener(this);
				textview.setOnTouchListener(this);
			}
			// 2014/04/07 DA ins end

			// ジェスチャで前後の項目に移動できるようにする
			//this.layout.setOnTouchListener(this);

			Timer timer = new Timer(true);
			final android.os.Handler handler = new android.os.Handler();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					handler.post(new Runnable() {
						public void run() {
							setButton();
						}
					});
				}
			}, Kensa2.ENABLED_TIME_FIRST);
		} catch (RuntimeException e) {
			Log.e(LFA.TAG, e.getMessage(), e);
		}
	}

	/**
	 * 検査画像を読み込み、ボタンにセットする
	 *
	 * @param button
	 *            画像をセットするボタン
	 * @param imageFile
	 *            ボタンの上に表示する画像のファイル名
	 * @param isOK
	 *            正解ボタンかどうか。NGボタンで画像なしの場合、ボタン無効化する
	 */
	private void setImageToButton(ImageButton button, String imageFile,
			boolean isOK) {

		// ファイル名が空白の場合、ボタン上の画像を消して
		// NGボタンは無効化し、終了
		if (imageFile == null || imageFile.equals("")) {
			button.setImageBitmap(null);
			return;
		}

		try {
			File file = Utils.getFile(this, imageFile);
			if (file.exists()) {
				Bitmap bmp = ResizedImageCache.getImage(file.getAbsolutePath(), file.lastModified());
				button.setImageBitmap(bmp);
			} else {
				Log.e(LFA.TAG,
						"[WARINIG]NO IMAGE FILE FOUND! - "
								+ file.getAbsolutePath());
			}
		} catch (Error e) {
			Log.e(LFA.TAG, e.toString(), e);
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Log.e(LFA.TAG, e.toString(), e);
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
		}
	}

// 2016/02/24 DA upd start
	/**
	 * テキストを文字数により文字サイズを変更して表示する
	 * @param object テキストビューオブジェクト
	 * @param text 表示する文字列
	 * @param aspectScale 文字サイズ倍率
	 * @author DA 2014/04/07
	 */
	private void setTextView(TextView object, String text, double aspectScale)
	{
		setTextView(object, text, aspectScale, text.length());
	}

	/**
	 * テキストを文字数により文字サイズを変更して表示する
	 * @param object テキストビューオブジェクト
	 * @param text 表示する文字列
	 * @param len 文字列長
	 * @param aspectScale 文字サイズ倍率
	 * @author DA 2015/02/24
	 */
	private void setTextView(TextView object, String text, double aspectScale, int len)
	{
		try {
			object.setText(text);

			int txtSize = 0;
			if(len <= SIZE_LENGTH_LARGE) {
				txtSize = (int) Math.floor((SIZE_FONT_LARGE) * aspectScale);
			}
			else if(len <= SIZE_LENGTH_MIDDLE) {
				txtSize = (int) Math.floor((SIZE_FONT_MIDDLE) * aspectScale);
			}
			else if(len >= SIZE_LENGTH_SMALL) {
				txtSize = (int) Math.floor((SIZE_FONT_SMALL) * aspectScale);
			}
			object.setTextSize(txtSize);
		}
		catch(Error e) {
			Log.e(LFA.TAG, e.toString(), e);
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
		}
		catch(Exception e) {
			Log.e(LFA.TAG, e.toString(), e);
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
		}
	}

//	/**
//	 * テキストを文字数により文字サイズを変更して表示する
//	 * @param object テキストビューオブジェクト
//	 * @param text 表示する文字列
//	 * @param len 文字列長
//	 * @param aspectScale 文字サイズ倍率
//	 * @author DA 2014/04/07
//	 */
//	 private void setTextView(TextView object, String text, double aspectScale)
//	{
//		try {
//			object.setText(text);
//
//			int len = object.getText().length();
//
//			int txtSize = 0;
//			if(len <= SIZE_LENGTH_LARGE) {
//				txtSize = (int) Math.floor((SIZE_FONT_LARGE) * aspectScale);
//			}
//			else if(len <= SIZE_LENGTH_MIDDLE) {
//				txtSize = (int) Math.floor((SIZE_FONT_MIDDLE) * aspectScale);
//			}
//			else if(len >= SIZE_LENGTH_SMALL) {
//				txtSize = (int) Math.floor((SIZE_FONT_SMALL) * aspectScale);
//			}
//			object.setTextSize(txtSize);
//		}
//		catch(Error e) {
//			Log.e(LFA.TAG, e.toString(), e);
//			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
//		}
//		catch(Exception e) {
//			Log.e(LFA.TAG, e.toString(), e);
//			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
//		}
//	}
// 2016/02/24 DA upd end

	@Override
	protected void onStart() {
		super.onStart();
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
			intent.putExtra(Common.INTENT_EX_GROUPNO, strGroupCode);
			intent.putExtra(Common.INTENT_EX_GROUPNAME, strGroupName);

			startActivityForResult(intent, R.layout.menyu);
			return true;
		default:
			return true;
		}
	}

	/**
	 * ボタンクリックイベント
	 *
	 * @param v
	 *            キーイベント発生元
	 * @param isHardware
	 *            ハードウェアキー押下
	 */
	public void onClickAny(View v, Boolean isHardware) {

		// 二度押し対策
		if (!startProcess()) {
			return;
		}

		String strDate = "";
		java.util.Date dNow = new java.util.Date();
		/*
		 * 検査時間計測厳密化のためミリ秒まで記録するようフォーマット変更
		 *
		 * @author CJ01915
		 *
		 * @version labo(2012/04/19)
		 *
		 * @since labo(2012/04/19)
		 */
		SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.S");
		strDate = f.format(dNow);

		boolean isFinish = false;

		// 押されたボタンにより、処理を分岐
		// 2014/04/07 DA upd start
		//if (v == btOK) {
		if (v == btOK || v == tvOK) {
		// 2014/04/07 DA upd end
			// 正解の場合
			/*
			 * 本体の振動実行<br/> 画面上のOK押下時のみ振動させる
			 *
			 * @author CJ01915
			 *
			 * @since labo(2012/02/21)
			 *
			 * @version labo(2012/03/15)
			 */
			// if (!isHardware) {
			this.vibrateBody();
			// }
			String ngContents = "";

			// 検査結果　0:OK, 1:NG, 2:ﾀﾞﾐｰ検出, 3:ﾀﾞﾐｰ見逃し
			String resultFlg = strOkNgDiv.equals("2") ? "3" : "0";

			LFA.getmDb().beginTransaction();
			try {
				ContentValues values = new ContentValues();
				// 検査結果更新
				values.put(
						DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_RESULTFLG],
						resultFlg);
				values.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_USERID],
						strJyugyoinNo);
				values.put(
						DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_ORDERTIME],
						strDate);
				values.put(
						DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_NGCONTENTS],
						ngContents);
				values.put(
						DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_INPUTDATA],
						strBcSign);
				String where = "groupCode = '" + strGroupCode + "'"
						+ " AND ordersignNo ='"
						+ mkensaItems[intItemNo].toString() + "'"
						+ " AND idno ='" + strIdno + "'"
						+ " AND loDate ='" + strLoDate + "'";
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

				// 検査履歴登録
				values = new ContentValues();
				values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_IDNO], strIdno);
				values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_LODATE], strLoDate);
				values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_ITEMCODE], strItemCode);
				values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_SELECT_NUMBER], selectNumber);
				values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_RESULTFLG], resultFlg);
				values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_INPUTDATA], strBcSign);
				values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_NGCONTENTS], ngContents);
				values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_USERID], strJyugyoinNo);
				values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_ORDERTIME], strDate);
				LFA.getmDb().insert("P_ordersignHistory", null, values);
				// 2016/02/24 DA ins end

				LFA.getmDb().setTransactionSuccessful();
				SoundPoolPlayer.getInstance().play(this, true);

			} catch (Exception e) {
				Log.e(LFA.TAG, e.toString(), e);
			} finally {
				LFA.getmDb().endTransaction();
			}

			if (mkensaItems.length != intItemNo + 1) {
				// 次の検査項目がある場合
				Cursor c = null;
				try {
					if (strimageFlg.equals("1")) {

						String sql = "";

						sql = "SELECT * FROM P_ordersing "
								+ " WHERE groupCode ='" + strGroupCode + "'"
								+ " AND ordersignNo ='"
								+ mkensaItems[intItemNo + 1].toString() + "'"
								+ " AND idno ='" + strIdno + "'"
								+ " AND loDate ='" + strLoDate + "'";
						c = LFA.getmDb().rawQuery(sql, null);
						if (c.moveToFirst()) {
							strTireDiv = c.getString(DB_ORDER_SING.INDEX_TIREFLG);
						}

						if (strTireDiv.equals("1")) {
							Intent intent = new Intent(getApplicationContext(),
									KensaTire.class);
							intent.putExtra(Common.INTENT_EX_GROUPCOUNT, intItemNo + 1);
							intent.putExtra(Common.INTENT_EX_CHECKITEMS, mkensaItems);
							intent.putExtra(Common.INTENT_EX_BODYNO, strBodyNo);
							intent.putExtra(Common.INTENT_EX_GROUPNO, strGroupCode);
							intent.putExtra(Common.INTENT_EX_IDNO, strIdno);
							intent.putExtra(Common.INTENT_EX_LO_DATE, strLoDate);
							startActivityForResult(intent, R.layout.kensa_tire);
						} else {
							Intent intent = new Intent(getApplicationContext(),
									Kensa2.class);
							intent.putExtra(Common.INTENT_EX_GROUPCOUNT, intItemNo + 1);
							intent.putExtra(Common.INTENT_EX_CHECKITEMS, mkensaItems);
							intent.putExtra(Common.INTENT_EX_BODYNO, strBodyNo);
							intent.putExtra(Common.INTENT_EX_GROUPNO, strGroupCode);
							intent.putExtra(Common.INTENT_EX_IDNO, strIdno);
							intent.putExtra(Common.INTENT_EX_LO_DATE, strLoDate);
							startActivityForResult(intent, R.layout.kensa2);
						}
					}
					setResult(RESULT_OK);
					isFinish = true;
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
			} else if (mkensaItems.length == intItemNo + 1) {
				Intent intent = new Intent(getApplicationContext(),
						UpLoad.class);
				intent.putExtra(Common.INTENT_EX_BODYNO, strBodyNo);
				intent.putExtra(Common.INTENT_EX_GROUPNO, strGroupCode);
				intent.putExtra(Common.INTENT_EX_CHECKITEMS, mkensaItems);
				intent.putExtra(Common.INTENT_EX_GROUPCOUNT, intItemNo);
				intent.putExtra(Common.INTENT_EX_IDNO, strIdno);
				intent.putExtra(Common.INTENT_EX_LO_DATE, strLoDate);
				intent.putExtra(Common.INTENT_EX_CHECKKARAMODORU, "kensa2");
				startActivityForResult(intent, R.layout.upload);
				setResult(RESULT_OK);
				isFinish = true;
			}

		// 2014/04/07 DA upd start
		//} else if (v == btNG || v.getId() == R.id.OtherButton) {
		} else if (v == btNG || v == tvNG || v.getId() == R.id.OtherButton) {
		// 2014/04/07 DA upd end
			// 不正解の場合

			// NG音
			SoundPoolPlayer.getInstance().play(this, false);

			// 再確認画面を表示
			if (strimageFlg.equals("1")) {
				Intent intent = new Intent(getApplicationContext(), Kensa.class);
				intent.putExtra(Common.INTENT_EX_GROUPCOUNT, intItemNo);
				intent.putExtra(Common.INTENT_EX_CHECKITEMS, mkensaItems);
				intent.putExtra(Common.INTENT_EX_BODYNO, strBodyNo);
				intent.putExtra(Common.INTENT_EX_GROUPNO, strGroupCode);
				intent.putExtra(Common.INTENT_EX_IDNO, strIdno);
				intent.putExtra(Common.INTENT_EX_LO_DATE, strLoDate);
				// 2014/04/07 DA upd start
				//if (v == btNG) {
				if (v == btNG || v == tvNG) {
				// 2014/04/07 DA upd end
					intent.putExtra(Common.INTENT_EX_INPUTDATA, strBcSign2);
				} else {
					intent.putExtra(Common.INTENT_EX_INPUTDATA, "Others");
				}

				startActivityForResult(intent, R.layout.kensa);
			}
			setResult(RESULT_OK);
			isFinish = true;

		} else if (v.getId() == R.id.Mae_Button
				|| v.getId() == R.id.Tsugi_Button) {
			// 前ボタン、次ボタン押下時
			Cursor c = null;
			try {
				if (strimageFlg.equals("1")) {
					// 遷移先の項目番号
					int afterItemNo = v.getId() == R.id.Mae_Button ? intItemNo - 1
							: intItemNo + 1;
					String sql = "";
					sql = "SELECT * FROM P_ordersing " + " WHERE groupCode ='"
							+ strGroupCode + "'" + " AND ordersignNo ='"
							+ mkensaItems[afterItemNo].toString() + "'"
							+ " AND idno ='" + strIdno + "'"
							+ " AND loDate ='" + strLoDate + "'";

					c = LFA.getmDb().rawQuery(sql, null);
					if (c.moveToFirst()) {
						strTireDiv = c.getString(DB_ORDER_SING.INDEX_TIREFLG);
					}
					if (strTireDiv.equals("1")) {
						Intent intent = new Intent(getApplicationContext(),
								KensaTire.class);
						intent.putExtra(Common.INTENT_EX_GROUPCOUNT, afterItemNo);
						intent.putExtra(Common.INTENT_EX_CHECKITEMS, mkensaItems);
						intent.putExtra(Common.INTENT_EX_BODYNO, strBodyNo);
						intent.putExtra(Common.INTENT_EX_GROUPNO, strGroupCode);
						intent.putExtra(Common.INTENT_EX_IDNO, strIdno);
						intent.putExtra(Common.INTENT_EX_LO_DATE, strLoDate);
						startActivityForResult(intent, R.layout.kensa_tire);
					} else {
						Intent intent = new Intent(getApplicationContext(),
								Kensa2.class);
						intent.putExtra(Common.INTENT_EX_GROUPCOUNT, afterItemNo);
						intent.putExtra(Common.INTENT_EX_CHECKITEMS, mkensaItems);
						intent.putExtra(Common.INTENT_EX_BODYNO, strBodyNo);
						intent.putExtra(Common.INTENT_EX_GROUPNO, strGroupCode);
						intent.putExtra(Common.INTENT_EX_IDNO, strIdno);
						intent.putExtra(Common.INTENT_EX_LO_DATE, strLoDate);
						startActivityForResult(intent, R.layout.kensa2);
					}

					/*
					 * Android 2.0以降であればページ遷移時のアニメーションを指定可能
					 *
					 * @author CJ01915
					 *
					 * @since labo(2012/02/21)
					 */
					// overridePendingTransition(R.anim.activity_close_enter,
					// R.anim.activity_close_enter);
				}
				setResult(RESULT_OK);
				isFinish = true;
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

		} else if (v.getId() == R.id.Menu_Button) {
			// メニューボタン押下時
			Intent intent = new Intent();
			intent.setClassName("LFA.Code2", "LFA.Code2.Menyu");
			intent.putExtra(Common.INTENT_EX_BODYNO, strBodyNo);
			intent.putExtra(Common.INTENT_EX_GROUPNO, strGroupCode);
			intent.putExtra(Common.INTENT_EX_GROUPNAME, strGroupName);
			startActivityForResult(intent, R.layout.menyu);
		}

		// 終了フラグOFFなら終了しない
		if (!isFinish) {
			endProcess();
			return;
		}
		finish();
	}

	/**
	 * ページ遷移ボタン状態設定
	 *
	 * @author unknown
	 * @since unknown
	 * @version unknown
	 */
	private void setButton() {
		// 前ボタン,次ボタン非活性
		if (mkensaItems.length != 1) {
			if (intItemNo != 0) {
				Button btmae = (Button) findViewById(R.id.Mae_Button);
				btmae.setEnabled(true);
			}
			if (mkensaItems.length != intItemNo + 1) {
				Button bttsugi = (Button) findViewById(R.id.Tsugi_Button);
				bttsugi.setEnabled(true);
			}
		}

		// ボタンとテキストビューの有効・無効判定。
		// 画像あり　　　　　：ボタンを有効にする
		// 画像なし、文字あり：テキストビューを有効にする
		// 画像なし、文字なし：すべて無効にする
		if(((BitmapDrawable) btSel1.getDrawable()).getBitmap() != null) {
			this.btSel1.setEnabled(true);
		}
		// 2014/04/07 DA ins start
		if(!this.tvImageKensaNaiyo1.getText().equals("")) {
			this.tvImageKensaNaiyo1.setEnabled(true);
		}
		// 2014/04/07 DA ins end
		if(((BitmapDrawable) btSel2.getDrawable()).getBitmap() != null) {
			this.btSel2.setEnabled(true);
		}
		// 2014/04/07 DA ins start
		if(!this.tvImageKensaNaiyo2.getText().equals("")) {
			this.tvImageKensaNaiyo2.setEnabled(true);
		}
		// 2014/04/07 DA ins end

		// 他ボタンの有効
		this.btOther.setEnabled(true);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		}
	}

	/**
	 * キーイベントハンドラ<br/>
	 * ※ 戻り値でtrueを返すとハードウェア制御機能が実行されない(電源ボタンを除く)
	 *
	 * @param keyCode
	 *            キーコード
	 * @param event
	 *            キーイベント
	 * @since unkown
	 * @version labo(2012/02/21)
	 * @author unknown
	 * @return false
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean resValue = false;
		switch (keyCode) {
		// メニューは何もしない
		case KeyEvent.KEYCODE_MENU:
			Log.d(LFA.TAG, "ignored key event...");
			break;
		// VolUp, VolDownキー押下でOKボタン押下イベントを実行
		case KeyEvent.KEYCODE_VOLUME_UP:
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			// キーの押しっぱなしを許可しない
			if (event.getRepeatCount() == 0 && this.btSel1.isEnabled()
					&& this.blnOnload) {
				this.onClickAny(this.btSel1, true);
			}
			resValue = true;
			break;
		default:
			Log.d(LFA.TAG, "ignored key event...");
			break;
		}
		return resValue;
	}

	/**
	 * 本体を振動させる
	 *
	 * @author CJ01915
	 * @version labo(2012/02/21)
	 * @since labo(2012/02/21)
	 */
	private void vibrateBody() {
		try {
			Vibrator vib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
			vib.vibrate(Kensa2.VIBLATE_TIME);
		} catch (SecurityException e) {
			Log.e(LFA.TAG, e.toString());
		}
	}

	/**
	 * タッチイベントハンドラ<br/>
	 * クリック時の処理をそのまま実行
	 *
	 * @param view
	 *            押下対象
	 * @param event
	 *            イベント種別
	 * @return false
	 * @author CJ01915
	 * @version labo(2012/02/21)
	 * @since labo(2012/02/21)
	 */
	public boolean onTouch(View view, MotionEvent event) {
		boolean resValue = event.getAction() == MotionEvent.ACTION_DOWN;

		// 戻り値を格納する変数。
		//   true:イベント中止 → onClickが処理されない
		//   false:イベント続行 → onClickが処理される
		boolean ret = false;

		/*
		 * ボタン以外をタッチした場合はゼスチャーに応じて前後ページ遷移実行
		 */
		if (view == this.layout) {
			// タッチ開始時のX座標を記憶
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				this.posX = event.getRawX();
				// タッチ終了時にページ遷移実行
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				float posDiff = event.getX() - this.posX;
				int buttonId = (posDiff > 0 ? R.id.Mae_Button
						: R.id.Tsugi_Button);
				Button targetButton = (Button) findViewById(buttonId);
				Log.d(LFA.TAG, "[Move length]" + String.valueOf(posDiff));
				if (targetButton.isEnabled()
						&& Math.abs(posDiff) > Kensa2.LIMIT_DIFF) {
					this.onClickAny(targetButton, false);
				}
			}
		} else {
			// 押し込んだときにクリック時処理実行
			if (resValue) {
				this.posY = event.getRawY();
				if (view == btOK) {
					int btOkWidth = btOK.getMeasuredHeight();
					int[] locationOK = new int[2];
					btOK.getLocationInWindow(locationOK);
					int btOkStart = locationOK[1];
					int btOkRangeS = btOkStart + btOkWidth / 4;
					int btOkRangeE = btOkStart + btOkWidth / 4 * 3;
					// タッチ位置がボタン有効範囲外かどうか判定
					ret = posY < btOkRangeS || posY > btOkRangeE;
				} else if (view == btNG) {
					int btNgWidth = btNG.getMeasuredHeight();
					int[] locationNG = new int[2];
					btNG.getLocationInWindow(locationNG);
					int btNgStart = locationNG[1];
					int btNgRangeS = btNgStart + btNgWidth / 4;
					int btNgRangeE = btNgStart + btNgWidth / 4 * 3;
					// タッチ位置がボタン有効範囲外かどうか判定
					ret = posY < btNgRangeS || posY > btNgRangeE;
				}
				// 2014/04/07 DA ins start
				else if(view == tvOK ||
						view == tvNG) {
					ret = isImageClickOutOfRange(view, event.getRawY());
				}
				// 2014/04/07 DA ins end
			}
		}

		return ret;
	}

	/**
	 * クリック位置が有効範囲外かどうか判定
	 * @param view オブジェクト
	 * @param posY クリック位置
	 * @return 判定結果（true：範囲外、false：範囲内）
	 * @author DA 2014/04/07
	 */
	private boolean isImageClickOutOfRange(View view, float posY)
	{
		boolean isResult = true;

		int btWidth = view.getMeasuredHeight();
		int[] location = new int[2];
		view.getLocationInWindow(location);
		int btStart = location[1];
		int btRangeS = btStart + btWidth / 4;
		int btRangeE = btStart + btWidth / 4 * 3;

		// タッチ位置がボタン有効範囲外かどうか判定
		isResult = posY < btRangeS || posY > btRangeE;

		return isResult;
	}

	/**
	 * アクティビティ破棄時の処理
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// 画像解放する
		btSel1.setImageBitmap(null);
		btSel2.setImageBitmap(null);

		// 2017/12/01 DA ins start
		ImageView iview = (ImageView) findViewById(R.id.shotimage);
		if (iview != null) {
			iview.setImageBitmap(null);
		}
		// 2017/12/01 DA ins end

		super.onDestroy();
	}


	/**
	 * ボタンクリックイベント
	 *
	 * @param view キーイベント発生元
	 */
	public void onClick(View view) {
		onClickAny(view, false);
	}

	// 2017/12/01 DA ins start
	/**
	 * ハンドラ定義
	 */
	private final Handler handler = new Handler();

	/**
	 * 経過時間タイマー
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