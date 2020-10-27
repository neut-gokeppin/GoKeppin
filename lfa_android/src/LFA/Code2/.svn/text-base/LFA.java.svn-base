package LFA.Code2;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import LFA.Code2.R;
import LFA.Code2.Common.Aspect;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;

/**
 *
 * @author cj01779
 *
 */
public class LFA extends BaseActivity implements OnClickListener {

	/**
	 * モード { 訓練-本番マスタ, 訓練-仮マスタ, 本番 }
	 */
	// 2016/02/24 DA upd start
	// 2017/03/02 CT upd start
	// enum ModeList { practiceHon, practiceKari0, practiceKari1, production };
	enum ModeList {
		practiceHon, practiceKari0, production
	};

	// 2017/03/02 CT upd end
	// enum ModeList { practiceHon, practiceKari, production };
	// 2016/02/24 DA upd end

	/**
	 * 現在選択されているモードを管理する変数
	 */
	public static ModeList mode = ModeList.production;

	/**
	 * 訓練モード用従業員コード ※サーバー側で訓練モードの判断に使用しています。 もし変更する場合は、サーバー側にも変更が必要です。
	 */
	public static final String PRACTICE_JYUGYOIN = "PRACTICE";

	/**
	 * アスペクト比3:2の比率（％）
	 */
	private static final int ASPECT_RATIO_3_2 = 3 * 100 / 2;

	/**
	 * アスペクト比5:3の比率（％）
	 */
	private static final int ASPECT_RATIO_5_3 = 5 * 100 / 3;

	/**
	 * アスペクト比16:9の比率（％）
	 */
	private static final int ASPECT_RATIO_16_9 = 16 * 100 / 9;

	public static Aspect aspect;

	/**
	 * ボタンの登録
	 */
	static final int[] BUTTONS = { R.id.OK, R.id.Cansel, R.id.PracticeMode, };

	/**
	 * データベースのファイル名
	 */
	static final String DB = "P_paramlist.db";

	/**
	 * テーブルの名前
	 */
	static final String TABLE = "P_paramlist";

	/**
	 * メモカラムの名前
	 */
	static final String NAME = "name";

	/**
	 * 値
	 */
	static final String VALUE = "value";

	/**
	 * apkファイル名
	 */
	static final String APK_FILENAME = "lfa_android.apk";

	/**
	 * 再読み込み確認用テーブル
	 */
	static final String FILE_READ_CHECK_TABLE = "file_read_check_table";

	// static final Integer READ_CHECK_ID = 1;

	/**
	 * チェック用テーブル作成SQL
	 */
	static final String CREATE_CHECK_TABLE = "create table "
			+ FILE_READ_CHECK_TABLE + " (_id integer primary key, "
			+ "file_read_flg boolean" + ");";

	/**
	 * db_bodyLisｔ生成
	 */
	public static DB_BODY_LIST dbbodyList = new DB_BODY_LIST();

	/**
	 * dbordersign生成
	 */
	public static DB_ORDER_SING dbordersign = new DB_ORDER_SING();

	// 2016/02/24 DA ins start
	/**
	 * dbordersingHistory生成
	 */
	public static DB_ORDERSIGN_HISTORY dbordersignHistory = new DB_ORDERSIGN_HISTORY();
	// 2016/02/24 DA ins end

	/**
	 * dbuserlist生成
	 */
	public static DB_USER_LIST dbuserlist = new DB_USER_LIST();

	// 2020/08/25 NEUT ins start
	/**
	 * tiremakerlist生成
	 */
	public static DB_TIREMAKER_LIST dbtireMakerList = new DB_TIREMAKER_LIST();
	// 2020/08/25 NEUT ins end
	/**
	 * Handler定義
	 */
	private final Handler handler = new Handler(); // ハンドラ

	/**
	 * メニューからのパラメータ格納
	 */
	public String strParma = "";

	/**
	 * ダイアログ定義
	 */
	ProgressDialog progressDialog;

	/**
	 * ユーザーダイアログ定義
	 */
	ProgressDialog progressDialogUserList;

	/**
	 * エラーメッセージ格納変数
	 */
	private String strErrmsg = "";

	/**
	 * データベースのバージョン
	 */
	static final int DB_VERSION = 1;

	/**
	 * パラメータテーブル
	 */
	static final String P_PARAMLIST = "create table P_paramlist ("
			+ "_id integer primary key autoincrement not null," + "name text,"
			+ "value text)";

	/**
	 * 定義
	 */
	static final String TAG = "LFACode";

	/**
	 * DB定義
	 */
	private static SQLiteDatabase mDb;

	/**
	 * 他画面からmDbを取得
	 *
	 * @return mDb
	 */
	public static SQLiteDatabase getmDb() {
		return mDb;
	}

	/**
	 * mDbをセットする
	 *
	 * @param mDbObj
	 *            mDb
	 */
	private static void setmDb(SQLiteDatabase mDbObj) {
		LFA.mDb = mDbObj;
	}

	/**
	 * EditText定義
	 */
	private EditText mEdit;

	/**
	 * 格納変数
	 */
	private String strMenu = "";

	/**
	 * データベースオープン用のヘルパークラス
	 *
	 * @author cj01779
	 *
	 */
	private static class MemoOpenHelper extends SQLiteOpenHelper {

		/**
		 * データベースオープン
		 *
		 * @param c
		 *            a
		 */
		public MemoOpenHelper(Context c) {
			// データベースのファイル名とバージョンを指定
			super(c, DB, null, DB_VERSION);
		}

		/**
		 * データベースを新規に作成した後呼ばれる
		 *
		 * @param db
		 *            db
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			// 内部にテーブルを作る
			// db.execSQL(CREATE_TABLE);
			db.beginTransaction();
			try {
				db.execSQL(CREATE_CHECK_TABLE);
				db.execSQL(P_PARAMLIST);
				// db.execSQL(P_ordersing);
				dbordersign.CreateTableSSS(db);
				// 2016/02/24 DA del start
				dbordersignHistory.CreateTableSSS(db);
				// 2016/02/24 DA del end
				dbbodyList.CreateTableSSS(db);
				dbuserlist.CreateTableSSS(db);
				// 2020/08/25 NEUT ins start
				dbtireMakerList.CreateTableSSS(db);
				// 2020/08/25 NEUT ins end


				SQLiteStatement stmt = db.compileStatement("insert into "
						+ TABLE + " values (NULL,?,NULL);");
				stmt.bindString(1, "jyugyouinNo");
				// stmt.bindString(2,null);
				stmt.executeInsert();
				stmt.bindString(1, "aspect");
				// stmt.bindString(2,null);
				stmt.executeInsert();

				db.setTransactionSuccessful();
			} catch (Exception e) {
				e.toString();
			} finally {
				db.endTransaction();
			}
		}

		/**
		 * 存在するデータベースと定義しているバージョンが異なるとき
		 *
		 * @param db
		 *            db
		 * @param oldVersion
		 *            oldVersion
		 * @param newVersion
		 *            newVersion
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Version mismatch :" + oldVersion + " to " + newVersion);
			// ここでは、テーブルを削除して新規に作成している
			// 通常は、テーブル内のデータの変換を行う
			// db.execSQL(DROP_TABLE);
			onCreate(db);
		}
	}

	/**
	 * Called when the activity is first created.
	 *
	 * @param savedInstanceState
	 *            savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 2017/12/01 DA ins start
		getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		// 2017/12/01 DA ins end
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.syain_code);

		strMenu = getIntent().getStringExtra(Common.INTENT_EX_MENU);
		strParma = getIntent().getStringExtra(Common.INTENT_EX_MENU);
		mEdit = (EditText) findViewById(R.id.SyainNoInput);

		strErrmsg = "";
		if (strMenu == null || strMenu.equals("")) {
			// 新規起動のとき、検査途中の情報をリセットする。
			// 必ず工程選択しアップロードさせるため
			Menyu.setStrGroupName(null);
			Menyu.setStrBodyNo(null);
			Menyu.setStrGroupCode(null);

			/**
			 * データベースを書き込み用にオープンする
			 */
			MemoOpenHelper h = new MemoOpenHelper(getApplicationContext());
			try {
				// mDb = h.getWritableDatabase();
				setmDb(h.getWritableDatabase());
			} catch (SQLiteException e) {
				// ディスクフルなどでデータが書き込めない場合
				Log.e(TAG, e.toString());
				// mDb = h.getReadableDatabase();
				setmDb(h.getReadableDatabase());
			}
		}

		// ボタンを登録
		Button button;
		for (int buttonId : BUTTONS) {
			button = (Button) findViewById(buttonId);
			button.setOnClickListener(this);
		}

		// 画面サイズを取得する
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);

		// 画面アスペクト比設定
		int high = Math.max(metrics.widthPixels, metrics.heightPixels);
		int low = Math.min(metrics.widthPixels, metrics.heightPixels);

		int aspectRatio = high * 100 / low;

		if (aspectRatio <= ASPECT_RATIO_3_2) {
			aspect = Aspect.ASPECT_3_2;
		} else {
			if (aspectRatio <= ASPECT_RATIO_5_3) {
				aspect = Aspect.ASPECT_5_3;
			} else {
				if (aspectRatio <= ASPECT_RATIO_16_9) {
					aspect = Aspect.ASPECT_16_9;
				} else {
					aspect = Aspect.OTHER;
				}
			}
		}

		// SDカードを使うかどうか
		if (Common.USE_SDCARD) {
			// SDカードを使う場合、使えるよう準備する
			Utils.readySdcard();
		} else {
			// SDカードを使わない場合、SDカードをクリア
			Utils.clearSdcard();
		}
	}

	/**
	 * onStartメソッド
	 */
	protected void onStart() {
		super.onStart();

		try {

			if (strMenu != null && !strMenu.equals("")) {

			} else {
				// アプリバージョンチェック
				loadVersionCheck();

			}
			// 2016/02/24 DA del start
			// setInit();
			// 2016/02/24 DA del end
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	/**
	 * レジューム時に実行する処理
	 *
	 * @author CJ01615
	 * @since 1.0.0.0
	 * @version 1.0.0.0
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		// 2016/02/24 DA ins start
		mEdit.setText("");
		// 2016/02/24 DA ins end

		// OK・NG音準備
		SoundPoolPlayer.getInstance().prepare(this);
	}

	/**
	 * アプリバージョンアップメソッド
	 */
	public void updateApp() {

		String fileName = APK_FILENAME;
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(Utils.getFile(this, fileName)),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}

	/**
	 * ユーザーリストダウンロードメソッド
	 */
	public void getUserIDList() {
		// ユーザーリストダウンとロード使用例
		boolean blnflg = false;

		blnflg = InputCheck.setJyugyouin();

		if (!blnflg) {
			showErrorUserDialog(Common.MSG_USERLIST_CHECK);
		}
	}

	/**
	 * ボタンクリックイベント
	 *
	 * @param v
	 *            ボタンイベント
	 */
	public void onClick(View v) {

		if (v.getId() == R.id.OK) {

			// タイヤメーカーのデータを呼び出す
			setTireMaker();

			// 入力されたテキストを取得
			String inputData = mEdit.getText().toString();
			// 英語小文字は大文字に変換
			String inputDataCase = inputData.toUpperCase();

			// 必須チェック
			boolean check = InputCheck.checkRequired(inputData);
			if (!check) {
				showErrorDialog(Common.MSG_JYUGYOIN_HISU);
				return;
			}

			// 2014/10/27 DA del start
			// // 文字チェック
			// check = InputCheck.checkLength(inputDataCase, 7, 7);
			// if (!check) {
			// showErrorDialog(Common.MSG_JYUGYOIN_INPUT);
			// return;
			// }
			// 2014/10/27 DA del end

			// 内容チェック
			check = InputCheck.checkNumber(inputDataCase);
			if (!check) {
				showErrorDialog(Common.MSG_JYUGYOIN_INPUT);
				return;
			}

			// 従業員チェック
			check = InputCheck.isRightUserId(inputDataCase);
			if (!check) {
				showErrorDialog(Common.MSG_USERID_CHECK);
				return;
			}

			// 本番モードにセット
			mode = ModeList.production;

			// 2017/12/01 DA ins start
			// 撮影画像の全削除
			sidown.deleteShotimage(getApplicationContext());
			// 2017/12/01 DA ins end

			// 入力された従業員コード保存、画面遷移
			setJyugyoinAndOpenNextActivity(inputDataCase);

		} else if (v.getId() == R.id.Cansel) {
			finish();

		} else if (v.getId() == R.id.PracticeMode) {
			Builder builder = new Builder(this);
			builder.setTitle("訓練用マスタを選択してください");
			// 2016/02/24 DA upd start
			// 2017/03/02 CT upd start
			// builder.setItems(new String[]{"本番マスタ（号口工程）", "仮0マスタ（新工程）",
			// "仮1マスタ（新工程）", },
			builder.setItems(new String[] { "本番マスタ（号口工程）", "仮0マスタ（新工程）", },
			// 2017/03/02 CT upd end
			// builder.setItems(new String[]{"本番マスタ（号口工程）", "仮マスタ（新工程）", },
			// 2016/02/24 DA upd end
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// 訓練モード選択後の処理
							onClickPracticeMode(which);
						}
					});
			builder.create().show();
		}
	}

	// 2020/08/25 NEUT ins start
	/**
	 * タイヤメーカーリスト取得
	 *
	 * @return true or false
	 */
	private boolean setTireMaker() {
		boolean blnFlg = false;
		Cursor c = null;
		// データの登録
		try {
			ArrayList<HashMap<String, String>> list;
			String url = Common.REQ_URL + Common.KEY_TIREMAKERID;

			// タイヤメーカーを取得する
			String[] targets = {
					DB_TIREMAKER_LIST.Columns[DB_TIREMAKER_LIST.INDEX_TIREABBREVIATION],
					DB_TIREMAKER_LIST.Columns[DB_TIREMAKER_LIST.INDEX_TIRENAME] };

			list = Utils.getXmlTagsForMap(url, targets, "");

			if (list.size() > 0) {
				updateTimeMaker(list);
				blnFlg = true;
			} else {
				blnFlg = false;
			}

			String sql = " SELECT * " + " FROM P_tiremaker ";


			// Cursor c = LFA.mDb.rawQuery(sql, null);
			c = LFA.getmDb().rawQuery(sql, null);

			System.out.println(c.getCount());

		} catch (Exception e) {
			Log.e(LFA.TAG, e.toString());
		}finally {
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
	 * タイヤメーカーをSQLiteに登録
	 *
	 * @param list
	 *            タイヤメーカーリスト
	 */
	public void updateTimeMaker(final ArrayList<HashMap<String, String>> list) {

		HashMap<String, String> map;
		String strTemp = "";

		LFA.getmDb().beginTransaction();

		try {

			if (list.size() > 0) {

				for (int i = 0; i < list.size(); i++) {
					map = (HashMap<String, String>) list.get(i);
					ContentValues values = new ContentValues();

					// タイヤメーカー略称
					strTemp = map
							.get(DB_TIREMAKER_LIST.Columns[DB_TIREMAKER_LIST.INDEX_TIREABBREVIATION]);

					if (strTemp != null) {
						values.put(
								DB_TIREMAKER_LIST.Columns[DB_TIREMAKER_LIST.INDEX_TIREABBREVIATION],
								strTemp.trim());
					}

					// タイヤメーカー名称
					strTemp = map
							.get(DB_TIREMAKER_LIST.Columns[DB_TIREMAKER_LIST.INDEX_TIRENAME]);

					if (strTemp != null) {
						values.put(
								DB_TIREMAKER_LIST.Columns[DB_TIREMAKER_LIST.INDEX_TIRENAME],
								strTemp.trim());
					}

					LFA.getmDb().insert("P_tiremaker", null, values);

				}

				LFA.getmDb().setTransactionSuccessful();
			}

		} catch (Exception e) {
			Log.e(LFA.TAG, e.toString());
			showErrorDialog(Common.MSG_DB_ERR);
		} finally {
			LFA.getmDb().endTransaction();
		}

	}

	// 2020/08/25 NEUT ins end

	/**
	 * 従業員コードを保存し、次の画面に遷移する
	 *
	 * @param jyugyoin
	 *            従業員コード
	 */
	private void setJyugyoinAndOpenNextActivity(String jyugyoin) {
		mDb.beginTransaction();
		try {
			// 従業員ｺｰﾄﾞアップデート
			SQLiteStatement stmt = mDb.compileStatement("update " + TABLE
					+ " set " + VALUE + "='" + jyugyoin + "'" + " where "
					+ NAME + " ='jyugyouinNo'");
			stmt.execute();
			mDb.setTransactionSuccessful();

		} catch (Exception e) {
			// showDialog(this,"",e.toString());
			e.toString();
		} finally {
			mDb.endTransaction();
		}

		if (strParma != null && !strParma.equals("")) {
			// 起動パラメータあり → 前画面があるので前画面に戻る
			finish();

		} else {
			// 起動パラメータなし → 新規起動のためメニューを開く
			Intent intent = new Intent(getApplicationContext(), Menyu.class);
			intent.putExtra(Common.INTENT_EX_JYUUGYOIN, jyugyoin);
			startActivityForResult(intent, R.layout.menyu);
		}
	}

	/**
	 * 訓練モード選択時の処理
	 *
	 * @param which
	 *            選択されたモード
	 */
	protected void onClickPracticeMode(int which) {
		// 選択されたモードを共通変数にセット
		mode = ModeList.values()[which];

		// 訓練用従業員セット、画面遷移
		setJyugyoinAndOpenNextActivity(PRACTICE_JYUGYOIN);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		} else {
			strMenu = "menu";
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	/**
	 * エラーダイアログ
	 *
	 * @param str
	 *            エラー内容
	 */
	private void showErrorDialog(String str) {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("エラー");
		b.setMessage(str);
		b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			// ボタンが押されたらダイアログを閉じる
			public void onClick(DialogInterface dialog, int which) {
				mEdit.requestFocus();
				dialog.dismiss();

			}
		});
		b.show();
	}

	/**
	 * エラーダイアログ
	 *
	 * @param str
	 *            エラー内容
	 */
	private void showErrorUserDialog(String str) {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("エラー");
		b.setMessage(str);
		b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			// ボタンが押されたらダイアログを閉じる
			public void onClick(DialogInterface dialog, int which) {
				showUserDialog();
				dialog.dismiss();

			}
		});
		b.show();
	}

	/**
	 * はい・いいえダイアログ
	 */
	private void showDouituDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("確認");
		builder.setMessage(Common.MSG_APIUPDATE_QUS);
		builder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				updateApp();
				dialog.dismiss();
				finish();
			}
		});
		builder.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// SDcardのapkファイルを削除する
				deleteApkByCancel();
				// ユーザーリストを取得する
				loadUserList();
				dialog.dismiss();
			}
		});
		builder.show();
	}

	/**
	 * ユーザーリストダウンロード確認ダイアログ
	 */
	private void showUserDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("ユーザーリストを再ダウンロードしますか？");
		builder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				loadUserList();
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
			}
		});
		builder.show();
	}

	/**
	 * バージョンチェック
	 */
	public void loadVersionCheck() {
		try {
			progressDialog = new ProgressDialog(this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Loading...");
			progressDialog.show();
			new Thread(new Runnable() {
				public void run() {

					boolean blnFlg = false;

					// blnFlg = checkUpdateBydate();

					if (blnFlg) {
						handler.post(new Runnable() {
							public void run() {
								progressDialog.dismiss();
								showDouituDialog();
							}
						});
					} else {

						if (strErrmsg != null && !strErrmsg.equals("")) {
							handler.post(new Runnable() {
								public void run() {
									progressDialog.dismiss();
									showErrorDialog(strErrmsg);
								}
							});

						} else {

							blnFlg = false;
							blnFlg = InputCheck.setJyugyouin();

							progressDialog.dismiss();

							if (!blnFlg) {
								handler.post(new Runnable() {
									public void run() {
										showErrorUserDialog(Common.MSG_USERLIST_CHECK);
									}
								});
							}
						}
					}
				}
			}).start();
		} catch (Exception e) {
			Log.e(LFA.TAG, e.toString());
		}
	}

	/**
	 * ユーザーリスト取得
	 */
	public void loadUserList() {
		try {
			progressDialogUserList = new ProgressDialog(this);
			progressDialogUserList
					.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialogUserList.setMessage("Loading...");
			progressDialogUserList.show();
			new Thread(new Runnable() {
				public void run() {

					boolean blnFlg = false;
					blnFlg = InputCheck.setJyugyouin();

					progressDialogUserList.dismiss();

					if (!blnFlg) {
						handler.post(new Runnable() {
							public void run() {
								showErrorUserDialog(Common.MSG_USERLIST_CHECK);
							}
						});
					}
				}
			}).start();
		} catch (Exception e) {
			Log.e(LFA.TAG, e.toString());
		}
	}

	/**
	 * SDカードapkファイル削除
	 */
	private void deleteApkByCancel() {

		File fileTemp = Utils.getFile(this, APK_FILENAME);

		try {
			if ((fileTemp).exists()) {
				fileTemp.delete();
			} else {

			}
		} catch (Exception e) {
			Log.w(TAG, e);
		}

	}

	/**
	 * サーバーよりapkファイル取得
	 *
	 * @return 0
	 */
	private boolean checkUpdateBydate() {

		boolean blnRtn = false;

		File fileTemp = Utils.getFile(this, APK_FILENAME);
		Date dNow;
		String strDate = "";
		try {

			if ((fileTemp).exists()) {

				long lngModif = fileTemp.lastModified();
				dNow = new Date(lngModif);
				SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				strDate = f.format(dNow);
			} else {
				strDate = "";
			}
		} catch (Exception e) {
			Log.w(TAG, e);
		}
		String strURL = Common.REQ_URL + "?id=EditDate&EditDate="
				+ strDate.replace(" ", "%20");
		// 接続テスト
		byte[] byteTemp = Utils.getByteArrayFromURL(Common.REQ_URL, "");

		if (byteTemp == null) {
			strErrmsg = Common.MSG_URL_CONN_ERR;
		} else {
			byte[] byteArray = Utils.getByteArrayFromURL(strURL, "");

			if (byteArray == null) {
				strErrmsg = Common.MSG_URL_CONN_ERR;
				blnRtn = false;
			} else {

				String data = new String(byteArray);
				if (data.trim().equals(Common.KEY_REQUEST_OK)) {
					// 更新ない場合
					blnRtn = false;
				} else if (data.trim().equals("connect")) {
					blnRtn = false;
				} else {
					blnRtn = Utils.addAppliByDate(this, APK_FILENAME,
							Common.APK_URL, data);
				}
			}
		}

		return blnRtn;
	}

	// 2016/02/24 DA del start
	// /**
	// * 従業員コードセット
	// */
	// private void setInit() {
	// // SQLiteチェック
	// String sql = "select " + VALUE + " from " + TABLE + " where " + NAME
	// + "='jyugyouinNo'";
	//
	// Cursor c = null;
	//
	// try {
	// c = mDb.rawQuery(sql, null);
	//
	// if (c.moveToFirst()) {
	// String str = c.getString(0);
	// if (str != null && !str.equals(PRACTICE_JYUGYOIN)) {
	// mEdit.setText("");
	// } else {
	// mEdit.setText("");
	// }
	// } else {
	// mEdit.setText("");
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// try {
	// if (c != null) {
	// c.close();
	// c = null;
	// }
	// } catch (Exception e) {
	// Log.e(LFA.TAG, e.toString());
	// e.printStackTrace();
	// } finally {
	// c = null;
	// }
	// }
	//
	// mEdit.selectAll();
	// }
	// 2016/02/24 DA del end

	/**
	 * 戻るボタン無効
	 *
	 * @param keyCode
	 *            キーコード
	 * @param event
	 *            キーイベント
	 * @return false
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

		}

		return false;

	}

	/**
	 * 破棄時メソッド<br/>
	 * 音再生インスタンスを破棄する。 DBをクローズする。
	 *
	 * @author CJ01915
	 * @version labo(2012/03/19)
	 * @since labo(2012/03/19)
	 */
	@Override
	protected void onDestroy() {
		// アプリ全体の終了のときのみ処理する
		// （メニューから呼び出されたときは処理しない）
		if (strParma == null || strParma.equals("")) {
			SoundPoolPlayer.getInstance().destroy();
			getmDb().close();
		}
		super.onDestroy();
	}
}