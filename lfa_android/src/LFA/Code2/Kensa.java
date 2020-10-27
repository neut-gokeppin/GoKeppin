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
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 検査入力画面<ul>
 * <li>2012/02/21・・・仕様検査オフライントライ#2対応<ul>
 * <li>画面レイアウト変更</li>
 * <li>サイドキー押下によるOK入力対応</li>
 * <li>OK入力時の振動対応</li>
 * <li>カーソルクローズ忘れ対応</li></ul></li>
 * <li>2012/02/28・・・MediaPlayer解放漏れ対応</li>
 * <li>2012/03/19・・・MediaPlayer -> SoundPoolへ音の再生を変更(シングルトン化してインスタンス生成コストも削減)</li></ul>
 *
 * @author cj01779
 * @since unknown
 * @version labo(2012/02/28)
 */
public class Kensa extends BaseActivity implements View.OnClickListener {

	/**
	 * 画面上部に表示する情報のフォントサイズ
	 */
	private static final int SIZE_FONT_HEAD = 16;

	/**
	 * 検査情報のフォントサイズ。
	 */
	private static final int SIZE_FONT_DATA_MIDDLE = 20;

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
	 * 画像がない時の文字サイズ倍率
	 */
	private static final double SCALE_NO_IMAGE = 1.5;

	/**
	 * カウント定義
	 */
	static int iCnt = 0;

	/**
	 * ボタンの定義
	 */
	static final int[] BUTTONS = { R.id.Gohin, R.id.Keppin, R.id.Fuyou, R.id.Sonota, R.id.back};

	// 2014/04/07 DA ins start
	/**
	 * クリック対象のテキストビューの定義
	 */
	static final int[] TEXTVIEW = {
			R.id.tvImageKensaNaiyo
			};
	// 2014/04/07 DA ins end

	/**
	 *  テーブルの名前
	 */
	static final String TABLE = "P_ordersing";

	/**
	 * NG内容定義
	 */
	String[] checkItems = { "誤品", "欠品", "不要", "その他", "", "特設OK" };

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
	 * TextView tvKensaNaiyo定義
	 */
	TextView tvKensaNaiyo;

	/**
	 * TextView tvBodyNo定義
	 */
	TextView tvBodyNo;

	// 2014/04/07 DA ins start
	/**
	 * 検査画像内容の定義
	 */
	TextView tvImageKensaNaiyo;
	// 2014/04/07 DA ins end

	/**
	 * spinner定義
	 */
	Spinner spinner;

	/**
	 * ImageView定義
	 */
	ImageView image;

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
	 *検査項目格納配列
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
	 * チェック順格納変数
	 */
	private String strCheckJun;

	/**
	 * 検査結果格納変数
	 */
	@SuppressWarnings("unused")
	private String strresultFlg;

	/**
	 * 検査履歴格納変数
	 */
	private String strordersingFlg;

	/**
	 * NG内容格納変数
	 */
	private String ngCheack;

	/**
	 * 従業員名格納変数
	 */
	private String strJyugyoinName = "";

	/**
	 * 検査区分格納変数
	 */
	private String strimageFlg = "";

	/**
	 * NGボタンの定義
	 */
	Button btBack;

	/**
	*　誤品
	*/
	Button btGohin;

	/**
	*　欠品
	*/
	Button btKeppin;

	/**
	*　不要
	*/
	Button btFuyou;

	/**
	*　その他
	*/
	Button btSonota;

	/**
	 * ライナーレイアウトの定義
	 */
	LinearLayout layout;

	/**
	 * 画像ファイル名格納
	 */
	private String strimageName = "";

	/**
	 * 検査内容格納変数
	 */
	private String strCheckNaiyo;

	/**
	 * 画像ファイル名格納変数
	 */
	private String strImgFileName;

	/**
	 * LO日
	 */
	private String strlodate;

	// 2016/02/24 DA ins start
	/**
	 *　項目Code
	 */
	private String strItemCode = "";
	// 2016/02/24 DA ins start

	/**
	 * 　受信日表示用
	 */
	private String outdataTime = "";

	/**
     *　OK/NG検査区分
	 */
	private String strOkNgDiv = "";

	/**
	 * タイヤフラグ
	 */
	private String strTireFlg;


	/**
	 *  メニューアイテムID
	 */
	private static final int MENU_ITEM0 = 0;

	/**
	 * 本体振動時間(ミリ秒)
	 *
	 * @author CJ01915
	 * @version labo
	 * @since labo
	 */
	private static final int VIBLATE_TIME = 100;

	/**
	 * 自動OKにする検査内容値
	 *
	 * @author CJ01915
	 * @version labo
	 * @since labo
	private static final String AUTO_OK = "-";
	 */

	/**
	 * アイデントNo
	 */
	private String strIdno = "";

	/**
	 * ラインオフ計画日
	 */
	private String strLoDate = "";

	/**
	*　測定値
	*/
	private String strInputData = "";

	/* (非 Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			// レイアウトの生成
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.kensa);
			// ステータスバー削除
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			strBodyNo = getIntent().getStringExtra(Common.INTENT_EX_BODYNO);
			strGroupCode = getIntent().getStringExtra(Common.INTENT_EX_GROUPNO);
			mkensaItems = getIntent().getStringArrayExtra(Common.INTENT_EX_CHECKITEMS);
			intItemNo = getIntent().getIntExtra(Common.INTENT_EX_GROUPCOUNT, 0);
			strIdno = getIntent().getStringExtra(Common.INTENT_EX_IDNO);
			strLoDate = getIntent().getStringExtra(Common.INTENT_EX_LO_DATE);
			strInputData = getIntent().getStringExtra(Common.INTENT_EX_INPUTDATA);
			tvGroupName = (TextView) findViewById(R.id.KS_TV_GROUPNAME);
			tvKensaJun = (TextView) findViewById(R.id.KS_TV_KENSAJUN);
			tvKensaItem = (TextView) findViewById(R.id.KS_TV_KENSAITEM);
			tvKensaNaiyo = (TextView) findViewById(R.id.KS_TV_KENSANAIYO);
			tvBodyNo = (TextView) findViewById(R.id.KS_TV_BODYNO);
			// 2014/04/07 DA ins start
			tvImageKensaNaiyo = (TextView) findViewById(R.id.tvImageKensaNaiyo);
			// 2014/04/07 DA ins end

			// 2014/12/02 DA del start
//			// アープロード画面戻す対応
//			if (strInputData == null) {
//				strInputData = "Others";
//			}
			// 2014/12/02 DA del end

			// 画像の表示
			image = (ImageView) findViewById(R.id.ImageView01);
			btBack = (Button) findViewById(R.id.back);
			btGohin = (Button) findViewById(R.id.Gohin);
			btKeppin = (Button) findViewById(R.id.Keppin);
			btFuyou = (Button) findViewById(R.id.Fuyou);
			btSonota = (Button) findViewById(R.id.Sonota);
			layout = (LinearLayout) findViewById(R.id.Layout);
			Cursor c = null, d = null;
			try {
				String sqlite = "";
				sqlite = "SELECT value FROM P_paramlist where name " + "='jyugyouinNo'";

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
				String sql = "";
				sql = "SELECT * FROM P_ordersing "
					+ " WHERE bodyNo = '" + strBodyNo + "'"
					+ " AND groupCode ='" + strGroupCode + "'"
					+ " AND ordersignNo ='"
					+ mkensaItems[intItemNo].toString() + "'"
					+ " AND idno ='" + strIdno + "'"
					+ " AND loDate ='" + strLoDate + "'";

				c = LFA.getmDb().rawQuery(sql, null);
				if (c.moveToFirst()) {
					strCheckItem = c.getString(DB_ORDER_SING.INDEX_ORDERSINGNAME);
					strCheckJun = c.getString(DB_ORDER_SING.INDEX_ORDERSINGNO);
					strimageFlg = c.getString(DB_ORDER_SING.INDEX_IMAGEFLG);
					strGroupName = c.getString(DB_ORDER_SING.INDEX_GROUPNAME);
					strOkNgDiv = c.getString(DB_ORDER_SING.INDEX_OKNGFLG);
					strTireFlg = c.getString(DB_ORDER_SING.INDEX_TIREFLG);
					strlodate = c.getString(DB_ORDER_SING.INDEX_LODATE);
					// 2016/02/24 DA ins start
					strItemCode = c.getString(DB_ORDER_SING.INDEX_ITEMCODE);
					// 2016/02/24 DA ins start


					// 2014/12/02 DA ins start
					// アープロード画面戻す対応
					// アープロード画面からくる場合のみNULLとなる。以外は選択値やブランク
					if (strInputData == null) {
						strInputData = c.getString(DB_ORDER_SING.INDEX_INPUTDATA);
					}
					// 2014/12/02 DA ins end

					// ダミーチェック項目かどうかで画像、指示内容を入れ替える
					if (strOkNgDiv.equals("2")) {
						strCheckNaiyo = c.getString(DB_ORDER_SING.INDEX_ORDERSINGCONTENTS2);
						strImgFileName = c.getString(DB_ORDER_SING.INDEX_FILENAME2);
					} else {
						strCheckNaiyo = c.getString(DB_ORDER_SING.INDEX_ORDERSINGCONTENTS);
						strImgFileName = c.getString(DB_ORDER_SING.INDEX_FILENAME);
					}

					// 2014/12/02 DA ins start
					if (strTireFlg.equals("1")) {
						StringBuilder sb = new StringBuilder();
						String[] strCheckNaiyoAry = strCheckNaiyo.split("/");
						String slash = "";
						for (String str : strCheckNaiyoAry) {
							if (str.trim().equals("DMY") == false) {
								sb.append(slash);
								sb.append(str);
								slash = "/";
							}
						}
						strCheckNaiyo = sb.toString();
					}
					// 2014/12/02 DA ins end

					if (strlodate != null && !strlodate.equals("")) {
						outdataTime = strlodate.substring(0, 4) + "/"
								+ strlodate.substring(4, 6) + "/"
								+ strlodate.substring(6, 8);
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
					//画像の有無による文字サイズ倍率を設定する
					double noimageScale = 1;
					if (strimageName == null || strimageName.equals("")) {
						noimageScale = SCALE_NO_IMAGE;
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
					txtSize = (int) Math.floor((SIZE_FONT_DATA_MIDDLE) * noimageScale * aspectScale);
					if (strCheckItem != null) {
						tvKensaItem.setText(strCheckItem);
						tvKensaItem.setTextSize(txtSize);
					}
					if (strCheckNaiyo != null) {
						tvKensaNaiyo.setText("正しい仕様は「" + strCheckNaiyo + "」です。");
					}
					// 画面下部ボデー情報のテキスト設定
					txtSize = (int) Math.floor(SIZE_FONT_FOOT_BODY
							* aspectScaleFoot);
					if (strBodyNo != null) {
						tvBodyNo.setText("BNO." + strBodyNo + " "
								+ strJyugyoinName + " " + outdataTime);
						tvBodyNo.setTextSize(txtSize);
					}

					// 2014/04/07 DA ins start
					boolean isExist = false;
					// 2014/04/07 DA ins end

					// 検査画像読み込み
					if (strImgFileName != null && !strImgFileName.equals("")) {
						// 2014/04/07 DA ins start
						isExist = true;
						// 2014/04/07 DA ins end
						try {
							File file = Utils.getFile(this, strImgFileName);
							if (file.exists()) {
								Bitmap bmp = ResizedImageCache.getImage(file.getAbsolutePath(), file.lastModified());
								image.setImageBitmap(bmp);
							} else {
								Log.e(LFA.TAG, "[WARINIG]NO IMAGE FILE FOUND! - " + file.getAbsolutePath());
							}
						} catch (OutOfMemoryError e) {
							Log.e(LFA.TAG, e.toString());
						} catch (Exception e) {
							Log.e(LFA.TAG, e.toString());
						}
					}
					// 2014/04/07 DA ins start
					if(isExist) {
						image.setVisibility(View.VISIBLE);
						tvImageKensaNaiyo.setVisibility(View.INVISIBLE);
					}
					else {
						image.setImageBitmap(null);
						image.setVisibility(View.INVISIBLE);
						tvImageKensaNaiyo.setVisibility(View.VISIBLE);
					}

					// 検査画像内容を表示
					setTextView(tvImageKensaNaiyo, strCheckNaiyo, aspectScale);
					// 2014/04/07 DA ins end

					strresultFlg = c.getString(DB_ORDER_SING.INDEX_RESULTFLG);
					strordersingFlg = c.getString(DB_ORDER_SING.INDEX_ORDERSINGFLG);
					ngCheack = c.getString(DB_ORDER_SING.INDEX_NGCONTENTS);
					if (ngCheack.equals("0")) {
						btGohin.setTextColor(getResources().getColor(R.color.RED));
					} else if (ngCheack.equals("1")) {
						btKeppin.setTextColor(getResources().getColor(R.color.RED));
					} else if (ngCheack.equals("2")) {
						btFuyou.setTextColor(getResources().getColor(R.color.RED));
					} else if (ngCheack.equals("3")) {
						btSonota.setTextColor(getResources().getColor(R.color.RED));
					} else {
						ngCheack = ngCheack.trim();
					}

					if (strordersingFlg.equals("1")) {
						// 背景色を赤色
						layout.setBackgroundColor(Color.RED);
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
				Log.e(LFA.TAG, e.toString());
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
					Log.e(LFA.TAG, e.toString());
					e.printStackTrace();
				} finally {
					c = null;
					d = null;
				}
			}

			// ボタンを登録
			for (int buttonId : BUTTONS) {
				View button = (View) findViewById(buttonId);
				button.setOnClickListener(this);
			}

		} catch (RuntimeException e) {
			Log.e(LFA.TAG, e.toString());
		}
	}

	/**
	 * テキストを文字数により文字サイズを変更して表示する
	 * @param object テキストビューオブジェクト
	 * @param text 表示する文字列
	 * @param aspectScale 文字サイズ倍率
	 * @author DA 2014/04/07
	 */
	private void setTextView(TextView object, String text, double aspectScale)
	{
		try {
			object.setText(text);

			int len = object.getText().length();

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

	@Override
	protected void onStart() {
		super.onStart();
	}

	// オプションメニューの生成(1)
	/**
	 * @param menu menu
	 * @return true
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		// 2014/12/02 DA del start
		// メニューアイテム0の追加(2)
		//menu.add(0, 0, 0, "メニュー");
		// 2014/12/02 DA del end

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
	 * @param view キーイベント発生元
	 */
	public void onClick(View view) {
		onClickAny(view, false);
	}

	/**
	 * ボタンクリックイベント
	 * @param v キーイベント発生元
	 * @param isHardware ハードウェアキー押下
	 */
	public void onClickAny(View v, Boolean isHardware) {

		// 二度押し対策
		if (!startProcess()) {
			return;
		}

		String strDate = "";
		java.util.Date dNow;
		dNow = new java.util.Date();
		/*
		 * 検査時間計測厳密化のためミリ秒まで記録するようフォーマット変更
		 *
		 * @author CJ01915
		 * @version labo(2012/04/19)
		 * @since labo(2012/04/19)
		 */
		SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.S");
		strDate = f.format(dNow);
		boolean isFinish = false;

		// 2016/02/24 DA ins start
		// 検査履歴
		Cursor c = null;
		String sql = "";
		// 選択回数
		int selectNumber = 0;
		try {
			sql = "SELECT MAX(CAST(selectNumber as INT)) FROM P_ordersignHistory "
					+ " WHERE idno ='" + strIdno + "'"
					+ " AND loDate ='" + strLoDate + "'"
					+ " AND itemCode ='" + strItemCode + "'";
			c = LFA.getmDb().rawQuery(sql, null);
			if (c.moveToFirst()) {
				selectNumber = c.getInt(0);
			}
			++ selectNumber;
		} catch (Exception e) {
			Log.e(LFA.TAG, e.toString(), e);
		}
		// 2016/02/24 DA ins end

		if (v.getId() == R.id.back) {
			// 戻るボタンの処理
			// 2016/02/24 DA ins start
			// 検査履歴

			// 検査履歴登録
			ContentValues values = new ContentValues();
			values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_IDNO], strIdno);
			values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_LODATE], strLoDate);
			values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_ITEMCODE], strItemCode);
			values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_SELECT_NUMBER], selectNumber);
			values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_RESULTFLG], "4");
			values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_INPUTDATA], strInputData);
			values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_NGCONTENTS], "");
			values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_USERID], strJyugyoinNo);
			values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_ORDERTIME], strDate);
			LFA.getmDb().insert("P_ordersignHistory", null, values);
			// 2016/02/24 DA ins end

			// 画像選択画面に戻る
			if (strimageFlg.equals("1") && strTireFlg.equals("0")) {
				Intent intent = new Intent(getApplicationContext(),
						Kensa2.class);
				intent.putExtra(Common.INTENT_EX_GROUPCOUNT, intItemNo);
				intent.putExtra(Common.INTENT_EX_CHECKITEMS, mkensaItems);
				intent.putExtra(Common.INTENT_EX_BODYNO, strBodyNo);
				intent.putExtra(Common.INTENT_EX_GROUPNO, strGroupCode);
				intent.putExtra(Common.INTENT_EX_IDNO, strIdno);
				intent.putExtra(Common.INTENT_EX_LO_DATE, strLoDate);
				startActivityForResult(intent, R.layout.kensa2);

			} else if (strimageFlg.equals("1") && strTireFlg.equals("1")) {

				LFA.getmDb().beginTransaction();
				try {
					values = new ContentValues();
					values.put(
							DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_INPUTDATA],
							strInputData);
					String where = "groupCode = '" + strGroupCode + "'"
							+ " AND ordersignNo ='"
							+ mkensaItems[intItemNo].toString() + "'"
							+ " AND idno ='" + strIdno + "'"
							+ " AND loDate ='" + strLoDate + "'";
					LFA.getmDb().update("P_ordersing", values, where, null);
					LFA.getmDb().setTransactionSuccessful();
				} catch (Exception e) {
					Log.e(LFA.TAG, e.toString(), e);
				} finally {
					LFA.getmDb().endTransaction();
				}

				// 2014/12/02 DA del start
//				if (strInputData == null || strInputData.equals("")) {
//
//					LFA.getmDb().beginTransaction();
//					try {
//						ContentValues values = new ContentValues();
//						values.put(
//								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_RESULTFLG],
//								"");
//						values.put(
//								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_USERID],
//								"");
//						values.put(
//								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_NGCONTENTS],
//								"");
//						values.put(
//								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_ORDERTIME],
//								"");
//						String where = "groupCode = '" + strGroupCode + "'"
//								+ " AND tireDiv = '1'"
//								+ " AND idno ='" + strIdno + "'"
//								+ " AND loDate ='" + strLoDate + "'"
//								+ " AND inputData <> 'Others'";
//						LFA.getmDb().update("P_ordersing", values, where, null);
//						LFA.getmDb().setTransactionSuccessful();
//					} catch (Exception e) {
//						Log.e(LFA.TAG, e.toString(), e);
//					} finally {
//						LFA.getmDb().endTransaction();
//					}
//				}
				// 2014/12/02 DA del end

				Intent intent = new Intent(getApplicationContext(),
						KensaTire.class);
				intent.putExtra(Common.INTENT_EX_GROUPCOUNT, intItemNo);
				intent.putExtra(Common.INTENT_EX_CHECKITEMS, mkensaItems);
				intent.putExtra(Common.INTENT_EX_BODYNO, strBodyNo);
				intent.putExtra(Common.INTENT_EX_GROUPNO, strGroupCode);
				intent.putExtra(Common.INTENT_EX_IDNO, strIdno);
				intent.putExtra(Common.INTENT_EX_LO_DATE, strLoDate);
				startActivityForResult(intent, R.layout.kensa_tire);
			}
			setResult(RESULT_OK);
			isFinish = true;
		} else if (v.getId() == R.id.Gohin || v.getId() == R.id.Keppin
				|| v.getId() == R.id.Fuyou || v.getId() == R.id.Sonota) {

			if (v.getId() == R.id.Gohin) {
				ngCheack = "0";
			}

			if (v.getId() == R.id.Keppin) {
				ngCheack = "1";
			}

			if (v.getId() == R.id.Fuyou) {
				ngCheack = "2";
			}

			if (v.getId() == R.id.Sonota) {
				ngCheack = "3";
			}

			// 検査結果　0:OK, 1:NG, 2:ﾀﾞﾐｰ検出, 3:ﾀﾞﾐｰ見逃し
			String resultFlg = strOkNgDiv.equals("2") ? "2" : "1";

			LFA.getmDb().beginTransaction();
			try {
				// 検査結果・NG理由アップロード
				ContentValues values = new ContentValues();
				values.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_RESULTFLG], resultFlg);
				values.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_USERID], strJyugyoinNo);
				values.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_NGCONTENTS], ngCheack);
				values.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_ORDERTIME], strDate);
				if (strTireFlg.equals("1")) {
					// 2014/12/02 DA del start
					//前画面（タイヤメーカー画面）で更新しているため、ここでは不必要
					//values.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_INPUTDATA], "Others");
					// 2014/12/02 DA del end
				} else {
					values.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_INPUTDATA], strInputData);
				}
				String where = "groupCode = '" + strGroupCode + "'"
					+ " AND ordersignNo ='" + mkensaItems[intItemNo].toString() + "'"
					+ " AND idno ='" + strIdno + "'"
					+ " AND loDate ='" + strLoDate + "'";
				LFA.getmDb().update("P_ordersing", values, where, null);

				// 2016/02/24 DA ins start
				// 検査履歴登録
				values = new ContentValues();
				values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_IDNO], strIdno);
				values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_LODATE], strLoDate);
				values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_ITEMCODE], strItemCode);
				values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_SELECT_NUMBER], selectNumber);
				values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_RESULTFLG], resultFlg);
				values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_INPUTDATA], strInputData);
				values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_NGCONTENTS], ngCheack);
				values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_USERID], strJyugyoinNo);
				values.put(DB_ORDERSIGN_HISTORY.Columns[DB_ORDERSIGN_HISTORY.INDEX_ORDERTIME], strDate);
				LFA.getmDb().insert("P_ordersignHistory", null, values);
				// 2016/02/24 DA ins end

				LFA.getmDb().setTransactionSuccessful();
			} catch (Exception e) {
				Log.e(LFA.TAG, e.toString());
			} finally {
				//LFA.mDb.endTransaction();
				LFA.getmDb().endTransaction();
			}

			// 2014/12/02 DA ins start
			KensaTire.checkOKNG(strIdno, strLoDate, strGroupCode, strJyugyoinNo);
			// 2014/12/02 DA ins end

			if (mkensaItems.length != intItemNo + 1) {
				// 次の検査項目がある場合
				c = null;
				try {
					if (strimageFlg.equals("1")) {
						sql = "SELECT * FROM P_ordersing "
								+ " WHERE groupCode ='" + strGroupCode + "'"
								+ " AND ordersignNo ='"
								+ mkensaItems[intItemNo + 1].toString() + "'"
								+ " AND idno ='" + strIdno + "'"
								+ " AND loDate ='" + strLoDate + "'";
						c = LFA.getmDb().rawQuery(sql, null);
						if (c.moveToFirst()) {
							strTireFlg = c.getString(DB_ORDER_SING.INDEX_TIREFLG);
						}

						if (strTireFlg.equals("1")) {
							Intent intent = new Intent(getApplicationContext(), KensaTire.class);
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
				intent.putExtra(Common.INTENT_EX_CHECKKARAMODORU, "kensa");
				startActivityForResult(intent, R.layout.upload);
				setResult(RESULT_OK);
				isFinish = true;
			}
		}

		// 終了フラグOFFなら終了しない
		if (!isFinish) {
			endProcess();
			return;
		}
		finish();
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
	 * @param keyCode キーコード
	 * @param event キーイベント
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
//				if (event.getRepeatCount() == 0 && this.btYes.isEnabled() && this.blnOnload) {
//					this.onClickAny(this.btYes, true);
//				}
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
	@SuppressWarnings("unused")
	private void vibrateBody() {
		try {
			Vibrator vib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
			vib.vibrate(Kensa.VIBLATE_TIME);
		} catch (SecurityException e) {
			Log.e(LFA.TAG, e.toString());
		}
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