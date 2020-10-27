package LFA.Code;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import jp.co.ctc.util.LfaCommon;

/**
 * ボデーNO確認画面
 * @author CTC
 */
public class BodyNo extends BaseActivity implements View.OnClickListener {

	/**
	 * EditText定義
	 */
	private EditText edBodyNo;

	/**
	 * 工程名称定義
	 */
	private String strGroupName;

	/**
	 * 工程名URL格納用
	 */
	private String urlGroupName;

	/**
	 * ボタンの登録
	 */
	static final int[] BUTTONS = { R.id.BL_BT_OK, R.id.BL_BT_CANCEL, };

	/**
	 * BodyNo格納変数
	 */
	private String inputDataCase = "";

	/**
	 * 検査データ一式
	 */
	private String[] strItems = null;

	/**
	 * 工程コード
	 */
	private String strGroupCode = "";

	/**
	 * アイデントNo
	 */
	private String strIdno = "";

	/**
	 * ラインオフ計画日
	 */
	private String strLoDate = "";

	/**
	 * タイヤフラグ
	 */
	private String strTireFlg = "";

	/**
	*　ボデーNO
	*/
	private String strBodyNo = "";

	/**
	 * ダイアログ定義
	 */
	ProgressDialog progressDialog;

	/**
	 * メッセージ格納用変数
	 */
	private String strMsg = "";

	/**
	 * HashMap定義
	 */
	ArrayList<HashMap<String, String>> list;

	/**
	 * ハンドラ定義
	 */
	private final Handler handler = new Handler();

	/**
	 * Called when the activity is first created.
	 *
	 * @param savedInstanceState
	 *            savedInstanceState
	 * */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.bodyno_input);
		strGroupName = getIntent().getStringExtra(Common.INTENT_EX_GROUPNAME);
		strBodyNo = getIntent().getStringExtra(Common.INTENT_EX_BODYNO);
		strGroupCode = getIntent().getStringExtra(Common.INTENT_EX_GROUPNO);

		urlGroupName = Utils.urlEncode(strGroupName, "UTF-8");

		//通過日時記録工程の場合は、次のボデーNOは使用しない。常に入力させる。
		boolean isJudgment = LfaCommon.isJudgmentGroupPassDatetime(strGroupName);
		if (isJudgment) {
			strBodyNo = "";
		}

		edBodyNo = (EditText) findViewById(R.id.KS_ED_BODYNO);

		if (strBodyNo != null && !strBodyNo.equals("")) {
			// 連番をDBから取得して表示する
			Cursor c = null;
			try {
				String sql = "SELECT * FROM P_ordersingItem WHERE bodyNo='" + strBodyNo + "'";
				c = LFA.getmDb().rawQuery(sql, null);
				if (c.moveToFirst()) {
					String bcnoH0 = c.getString(DB_BODY_LIST.INDEX_BCNO_H0);
					EditText edBcnoH0 = (EditText) findViewById(R.id.KS_ED_BCNO_H0);
					edBcnoH0.setText(bcnoH0);
					// 2016/02/24 DA upd start
					edBodyNo.setText(strBodyNo);
					// 2016/02/24 DA upd end
				}
				// 2016/02/24 DA ins start
				else {
					if ((LFA.mode == LFA.ModeList.production) || (LFA.mode == LFA.ModeList.practiceHon)) {
						showErrorDialog(String.format(Common.MSG_BODYNO_REGIST, strBodyNo));

					}
				}
				// 2016/02/24 DA ins end

			} catch (Exception e) {
				Log.e(LFA.TAG, e.toString(), e);
			} finally {
				try {
					if (c != null) {
						c.close();
						c = null;
					}
				} catch (Exception e) {
					Log.e(LFA.TAG, e.toString(), e);
				} finally {
					c = null;
				}
			}
		}


		// ボタンを登録
		for (int buttonId : BUTTONS) {
			Button button = (Button) findViewById(buttonId);
			button.setOnClickListener(this);
		}
	}

	/**
	 * onStartメソッド
	 */
	@Override
	protected void onStart() {
		super.onStart();

	}

	/**
	 * ボタンクリックイベント
	 *
	 * @param v
	 *            ボタンイベント
	 */
	public void onClick(View v) {

		// 二度押し対策
		if (!startProcess()) {
			return;
		}

		if (v.getId() == R.id.BL_BT_OK) {
			// 2016/02/24 DA upd start
			progressDialog = new ProgressDialog(this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Loading...");
			progressDialog.setCancelable(false);
			progressDialog.show();
			Thread th =
			new Thread(new Runnable() {
				public void run() {
					strMsg = "";
					okButtonMain();
					progressDialog.dismiss();
					if (!strMsg.equals("")) {
						handler.post(new Runnable() {
							public void run() {
								showErrorDialog(strMsg);
							}
						});
					}
				}
			});
			th.start();
//			// 入力されたテキストを取得
//			inputDataCase = edBodyNo.getText().toString();
//
//			// 必須チェック
//			boolean check = InputCheck.checkRequired(inputDataCase);
//			if (!check) {
//				showErrorDialog(Common.MSG_BODYNO_HISU);
//				endProcess();
//				return;
//			}
//
//			// 文字チェック
//			check = InputCheck.checkLength(inputDataCase, 5, 5);
//			if (!check) {
//				showErrorDialog(Common.MSG_BODYNO_INPUT);
//				endProcess();
//				return;
//			}
//
//			// 内容チェック
//			check = InputCheck.checkNumber(inputDataCase);
//			if (!check) {
//				showErrorDialog(Common.MSG_BODYNO_INPUT);
//				endProcess();
//				return;
//			}
//			Cursor c = null;

//				String sql = "SELECT * FROM P_ordersing WHERE bodyNo = '"
//						+ inputDataCase + "' AND groupName = '" + strGroupName
//						+ "' ORDER BY idno , loDate , groupCode, ordersignNo";
//				try {
//					c = LFA.getmDb().rawQuery(sql, null);
//					if (c.moveToFirst()) {
//
//						if (LFA.mode == LFA.ModeList.production) {
//							boolean isJudgment = LfaCommon.isJudgmentGroupPassDatetime(strGroupName);
//							if (isJudgment) {
//								updateTp(urlGroupName, inputDataCase);
//								if (strMsg.equals("") == false) {
//									endProcess();
//									return;
//								}
//							}
//						}
//
//						strTireFlg = c.getString(DB_ORDER_SING.INDEX_TIREFLG);
//						strGroupCode = c.getString(DB_ORDER_SING.INDEX_GROUPCODE);
//						strIdno = c.getString(DB_ORDER_SING.INDEX_IDNO);
//						strLoDate = c.getString(DB_ORDER_SING.INDEX_LODATE);
//						strItems = new String[c.getCount()];
//						int iLoop = 0;
//						do {
//							strItems[iLoop] = c.getString(DB_ORDER_SING.INDEX_ORDERSINGNO);
//							iLoop++;
//						} while (c.moveToNext());
//
//						if (strTireFlg.equals("1")) {
//							Intent intent = new Intent(getApplicationContext(), KensaTire.class);
//							intent.putExtra(Common.INTENT_EX_CHECKITEMS, strItems);
//							intent.putExtra(Common.INTENT_EX_GROUPNO, strGroupCode);
//							intent.putExtra(Common.INTENT_EX_BODYNO, inputDataCase);
//							intent.putExtra(Common.INTENT_EX_IDNO, strIdno);
//							intent.putExtra(Common.INTENT_EX_LO_DATE, strLoDate);
//							intent.putExtra(Common.INTENT_EX_TRUETIRE, "");
//							startActivityForResult(intent, R.layout.kensa_tire);
//						} else {
//							Intent intent = new Intent(getApplicationContext(), Kensa2.class);
//							intent.putExtra(Common.INTENT_EX_CHECKITEMS, strItems);
//							intent.putExtra(Common.INTENT_EX_GROUPNO, strGroupCode);
//							intent.putExtra(Common.INTENT_EX_BODYNO, inputDataCase);
//							intent.putExtra(Common.INTENT_EX_IDNO, strIdno);
//							intent.putExtra(Common.INTENT_EX_LO_DATE, strLoDate);
//							startActivityForResult(intent, R.layout.kensa2);
//						}
//					} else {
//
//						getKensaData();
//						if (strMsg.equals("")) {
//							sql = "SELECT * FROM P_ordersing WHERE bodyNo = '"
//									+ inputDataCase + "' AND groupName = '" + strGroupName
//									+ "' ORDER BY idno , loDate , groupCode, ordersignNo";
//							c = LFA.getmDb().rawQuery(sql, null);
//							if (c.moveToFirst()) {
//								strTireFlg = c
//										.getString(DB_ORDER_SING.INDEX_TIREFLG);
//								strGroupCode = c
//										.getString(DB_ORDER_SING.INDEX_GROUPCODE);
//								strIdno = c
//										.getString(DB_ORDER_SING.INDEX_IDNO);
//								strLoDate = c
//										.getString(DB_ORDER_SING.INDEX_LODATE);
//								strItems = new String[c.getCount()];
//								int iLoop = 0;
//								do {
//									strItems[iLoop] = c
//											.getString(DB_ORDER_SING.INDEX_ORDERSINGNO);
//									iLoop++;
//								} while (c.moveToNext());
//
//								if (strTireFlg.equals("1")) {
//									Intent intent = new Intent(
//											getApplicationContext(),
//											KensaTire.class);
//									intent.putExtra(Common.INTENT_EX_CHECKITEMS,
//											strItems);
//									intent.putExtra(Common.INTENT_EX_GROUPNO,
//											strGroupCode);
//									intent.putExtra(Common.INTENT_EX_BODYNO,
//											inputDataCase);
//									intent.putExtra(Common.INTENT_EX_IDNO,
//											strIdno);
//									intent.putExtra(Common.INTENT_EX_LO_DATE,
//											strLoDate);
//									startActivityForResult(intent,
//											R.layout.kensa_tire);
//								} else {
//									Intent intent = new Intent(
//											getApplicationContext(), Kensa2.class);
//									intent.putExtra(Common.INTENT_EX_CHECKITEMS,
//											strItems);
//									intent.putExtra(Common.INTENT_EX_GROUPNO,
//											strGroupCode);
//									intent.putExtra(Common.INTENT_EX_BODYNO,
//											inputDataCase);
//									intent.putExtra(Common.INTENT_EX_IDNO,
//											strIdno);
//									intent.putExtra(Common.INTENT_EX_LO_DATE,
//											strLoDate);
//									startActivityForResult(intent, R.layout.kensa2);
//								}
//							} else {
//								showErrorDialog(Common.MSG_BODYNO_NUMBER);
//								endProcess();
//								return;
//							}
//						} else {
//							endProcess();
//							return;
//						}
//					}
//				setResult(RESULT_OK);
//				finish();
//			} catch (Exception e) {
//				endProcess();
//				Log.e(LFA.TAG, e.toString());
//			} finally {
//				try {
//					if (c != null) {
//						c.close();
//						c = null;
//					}
//				} catch (Exception e) {
//					Log.e(LFA.TAG, e.toString(), e);
//				} finally {
//					c = null;
//				}
//			}
		// 2016/02/24 DA upd end

		} else if (v.getId() == R.id.BL_BT_CANCEL) {
			Intent intent = new Intent();
			intent.setClassName("LFA.Code", "LFA.Code.Menyu");
			intent.putExtra(Common.INTENT_EX_BODYNO, strBodyNo);
			intent.putExtra(Common.INTENT_EX_GROUPNO, strGroupCode == null ? "" : strGroupCode);
			intent.putExtra(Common.INTENT_EX_GROUPNAME, strGroupName);
			startActivityForResult(intent, R.layout.menyu);

			endProcess();
		}
	}

	/**
	 * OKボタン押下時の処理
	 * @author DA 2016/02/24
	 */
	public void okButtonMain()
	{
		// 入力されたテキストを取得
		inputDataCase = edBodyNo.getText().toString();

		// 必須チェック
		boolean check = InputCheck.checkRequired(inputDataCase);
		if (!check) {
			strMsg = Common.MSG_BODYNO_HISU;
			endProcess();
			return;
		}

		// 文字チェック
		check = InputCheck.checkLength(inputDataCase, 5, 5);
		if (!check) {
			strMsg = Common.MSG_BODYNO_INPUT;
			endProcess();
			return;
		}

		// 内容チェック
		check = InputCheck.checkNumber(inputDataCase);
		if (!check) {
			strMsg = Common.MSG_BODYNO_INPUT;
			endProcess();
			return;
		}
		Cursor c = null;
		Cursor oic = null;
		String sql = "";
		try {
			ArrayList<HashMap<String, String>> checkList = null;
			// 最新ラインオフ計画日
			String strLastLoDate = "";
			// 検査データ有効日数
			String strInspectionExpire = "";
			// 検査結果有無
			Boolean existResult = false;
			// 再検索フラグ
			Boolean searchFlg = false;

			// サーバの最新データ確認
			String url = Common.REQ_URL + Common.KEY_CHECK + "&Koutei=" + urlGroupName + "&body=" + inputDataCase;
			String[] targets = { "lastLoDate", "inspectionExpire", "existResult"};

			byte[] byteArray = Utils.getByteArrayFromURL(url, "");
			if (byteArray == null) {
				strMsg = Common.MSG_URL_FAITH;
				endProcess();
				return;
			}
			else {
				String data = new String(byteArray);
				checkList = Utils.getXmlTagsFromURL(data, targets);
			}
			if ((checkList !=null) && (checkList.size() > 0)) {
				HashMap<String, String> map = (HashMap<String, String>) checkList.get(0);
				strLastLoDate = map.get("lastLoDate");
				strInspectionExpire = map.get("inspectionExpire");
				existResult = Boolean.valueOf(map.get("existResult"));
			}
			// 最新ラインオフ計画日取得失敗
			if (strLastLoDate.equals("")) {
				strMsg = Common.MSG_BODYNO_NUMBER;
				endProcess();
				return;
			}

			sql = "SELECT * FROM P_ordersing WHERE bodyNo = '"
					+ inputDataCase + "' AND groupName = '" + strGroupName
					+ "' ORDER BY idno , loDate , groupCode, ordersignNo";
			c = LFA.getmDb().rawQuery(sql, null);

			// ローカルデータ有
			if (c.moveToFirst()) {
				strLoDate = c.getString(DB_ORDER_SING.INDEX_LODATE);
				strGroupCode = c.getString(DB_ORDER_SING.INDEX_GROUPCODE);
				if (strLoDate != null && !strLoDate.equals("")) {
					// サーバの方が新しい
					if (Integer.valueOf(strLoDate) < Integer.valueOf(strLastLoDate)) {
						// 検査結果のアップロード
						upload();
						if (!strMsg.equals("")) {
							endProcess();
							return;
						}

						// 検査データ削除
						deleteKensaData();

						// 検査データ取得
						getKensaData();
						if (!strMsg.equals("")) {
							endProcess();
							return;
						}
						searchFlg = true;
					}
					else {
						// 通過日時を記録する工程か判定
						if (LFA.mode == LFA.ModeList.production) {
							boolean isJudgment = LfaCommon.isJudgmentGroupPassDatetime(strGroupName);
							if (isJudgment) {
								updateTp(urlGroupName, inputDataCase);
								if (!strMsg.equals("")) {
									endProcess();
									return;
								}
							}
						}
					}
				}

				// ラインオフ計画日不正
				else {
					strMsg = Common.MSG_BODYNO_TP;
					endProcess();
					return;
				}
			}
			// ローカルデータ無し
			else {
				// 検査データ取得
				getKensaData();
				if (!strMsg.equals("")) {
					endProcess();
					return;
				}
				searchFlg = true;
			}

			// 検査データ取得は再検索
			if (searchFlg) {
				c = LFA.getmDb().rawQuery(sql, null);
			}

			if (c.moveToFirst()) {
				// 本番モード
				if (LFA.mode == LFA.ModeList.production) {
					// 塗装吊上げ通過時刻チェック
					sql = "SELECT * FROM P_ordersingItem WHERE bodyNo='" + inputDataCase + "'";
					oic = LFA.getmDb().rawQuery(sql, null);
					String strTp = null;
					if (oic.moveToFirst()) {
						strTp = oic.getString(DB_BODY_LIST.INDEX_TP);
						if ((strTp == null) || strTp.equals("")) {
							strMsg = Common.MSG_BODYNO_TP;
							endProcess();
							return;
						}
					}
					// 検査データ有効日数
					strLoDate = c.getString(DB_ORDER_SING.INDEX_LODATE);
					if (strLoDate != null && !strLoDate.equals("") && !strInspectionExpire.equals("")) {
						// 有効期限切れ
						if (Integer.valueOf(strLoDate) < Integer.valueOf(strInspectionExpire)) {
							// 検査結果が存在しない
							if (!existResult) {
								strMsg = Common.MSG_LODATE_EXPIRE;
								endProcess();
								return;
							}
						}
					}
				}

				strTireFlg = c.getString(DB_ORDER_SING.INDEX_TIREFLG);
				strGroupCode = c.getString(DB_ORDER_SING.INDEX_GROUPCODE);
				strIdno = c.getString(DB_ORDER_SING.INDEX_IDNO);
				strLoDate = c.getString(DB_ORDER_SING.INDEX_LODATE);
				strItems = new String[c.getCount()];
				int iLoop = 0;
				do {
					strItems[iLoop] = c.getString(DB_ORDER_SING.INDEX_ORDERSINGNO);
					iLoop++;
				} while (c.moveToNext());

				if (strTireFlg.equals("1")) {
					Intent intent = new Intent(getApplicationContext(), KensaTire.class);
					intent.putExtra(Common.INTENT_EX_CHECKITEMS, strItems);
					intent.putExtra(Common.INTENT_EX_GROUPNO, strGroupCode);
					intent.putExtra(Common.INTENT_EX_BODYNO, inputDataCase);
					intent.putExtra(Common.INTENT_EX_IDNO, strIdno);
					intent.putExtra(Common.INTENT_EX_LO_DATE, strLoDate);
					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivityForResult(intent, R.layout.kensa_tire);
				} else {
					Intent intent = new Intent(getApplicationContext(), Kensa2.class);
					intent.putExtra(Common.INTENT_EX_CHECKITEMS, strItems);
					intent.putExtra(Common.INTENT_EX_GROUPNO, strGroupCode);
					intent.putExtra(Common.INTENT_EX_BODYNO, inputDataCase);
					intent.putExtra(Common.INTENT_EX_IDNO, strIdno);
					intent.putExtra(Common.INTENT_EX_LO_DATE, strLoDate);
					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivityForResult(intent, R.layout.kensa2);
				}
			}
			else {
				strMsg = Common.MSG_BODYNO_NUMBER;
				endProcess();
				return;
			}
			setResult(RESULT_OK);
			finish();
		} catch (Exception e) {
			endProcess();
			Log.e(LFA.TAG, e.toString());
		} finally {
			try {
				if (c != null) {
					c.close();
					c = null;
				}
			} catch (Exception e) {
				Log.e(LFA.TAG, e.toString(), e);
			} finally {
				c = null;
			}
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
				edBodyNo.requestFocus();
				dialog.dismiss();

			}
		});
		b.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		}
	}

	// 2016/02/24 DA ins start
	/**
	 * 検査データ削除
	 *
	 */
	private void deleteKensaData() {
		LFA.getmDb().beginTransaction();
		try {
			String where = "bodyNo = '" + inputDataCase
					+ "' AND groupCode = '" + strGroupCode + "'";
			LFA.getmDb().delete("P_ordersing", where, null);
			LFA.getmDb().delete("P_ordersingItem", where, null);
			LFA.getmDb().setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(LFA.TAG, e.toString());
		} finally {
			LFA.getmDb().endTransaction();
		}
	}
	// 2016/02/24 DA ins end

	/**
	 * 検査データ取得
	 *
	 */
	private void getKensaData() {
		try {
			// 2016/02/24 DA del start
//			progressDialog = new ProgressDialog(this);
//			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//			progressDialog.setMessage("Loading...");
//			progressDialog.show();
//			Thread th =
//					new Thread(new Runnable() {
//						public void run() {
			// 2016/02/24 DA del end
			strMsg = "";
			String strTargetFrame = getTargetFrame();
			if (strTargetFrame != null && !strTargetFrame.equals("")) {
				if (strMsg.equals("")) {
					String[] strFrame = strTargetFrame.split(",");
					for (int j = 0; j < strFrame.length; j++) {

						String frame[] = strFrame[j].split(":");

						String url = Utils.getKensaUrl(frame[1], frame[2], frame[3]);

						byte[] byteArray = downloadKensaData(url);

						if (byteArray != null && byteArray.length > 0) {
							// DB_BODY_LISTを取得する
							setBodyTable(byteArray);
							// BodyGroupをSQLiteに登録
							updateBodyData(list);
							list.clear();
							// DB_ORDER_SINGを取得する
							setGroupTable(byteArray);
							// BodyGroupとInspecItemをSQLiteに登録
							updateGroupData(list);

							// 画像を取得する
							getImgFromURL(frame[1], frame[2], frame[3]);
						} else {
							// 2016/02/24 DA ins start
							if ((LFA.mode == LFA.ModeList.production) || (LFA.mode == LFA.ModeList.practiceHon)) {
								strMsg = String.format(Common.MSG_BODYNO_REGIST, inputDataCase);
							}
							// 2016/02/24 DA ins end

							if (strMsg.equals("")) {
								//strMsg = Common.MSG_NO_DATA;			// No.38 Edit Itage Watanabe
								strMsg = Common.MSG_BODYNO_NUMBER;		// No.38 Edit Itage Watanabe
							}
						}
					}
				}
			} else {
				if (strMsg.equals("")) {
					//strMsg = Common.MSG_NO_DATA;			// No.38 Edit Itage Watanabe
					strMsg = Common.MSG_BODYNO_NUMBER;		// No.38 Edit Itage Watanabe
				}
			}

			// 2016/02/24 DA del start
//			progressDialog.dismiss();
//
//			if (!strMsg.equals("")) {
//				handler.post(new Runnable() {
//					public void run() {
//						showErrorDialog(strMsg);
//					}
//				});
//			}
//						}
//					});
//			th.start();
//			th.join();
			// 2016/02/24 DA del end
		} catch (Exception e) {
			Log.e(LFA.TAG, e.toString());
		}
	}

	/**
	 * アイデントNoとラインオフ計画日を取得する
	 *
	 * @return 取得したデータ
	 */
	private String getTargetFrame() {
		String url = Common.REQ_URL + Common.KEY_BODYID + "&Koutei=" + urlGroupName + "&body=" + inputDataCase + "&mode=" + LFA.mode;
		String[] targets = { "orderNo", "idno", "loDate", "groupCode" };
		String result = "";
		byte[] byteArray = Utils.getByteArrayFromURL(url, "");
		if (byteArray == null) {
			strMsg = Common.MSG_URL_FAITH;
		} else {

			String data = new String(byteArray);
			list = Utils.getXmlTagsFromURL(data, targets);
		}
		HashMap<String, String> map;
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				map = (HashMap<String, String>) list.get(i);
				String strOrderNo = map.get("orderNo");
				String strIdno = map.get("idno");
				String strLoDate = map.get("loDate");
				String strGroupCode = map.get("groupCode");
				if ((strOrderNo != null && !strOrderNo.equals(""))
						&& (strIdno != null && !strIdno.equals(""))
						&& (strLoDate != null && !strLoDate.equals(""))
						&& (strGroupCode != null && !strGroupCode.equals(""))) {
					result = result + strOrderNo.trim() + ":"
							+ strIdno.trim() + ":" + strLoDate.trim()
							+ ":" + strGroupCode.trim() + ",";
				}
			}
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}

	/**
	 * 検査データダウンロード
	 *
	 * @param url
	 *            パラメータ
	 * @return 取得したデータ
	 */
	private byte[] downloadKensaData(String url) {

		byte[] byteArray = Utils.getByteArrayFromURL(url);
		if (byteArray == null) {
			strMsg = Common.MSG_NO_DATA;
		}
		return byteArray;
	}

	/**
	 * ボデーNO、グループリストを取得
	 * @param byteArray XMLデータ
	 */
	private void setBodyTable(byte[] byteArray) {

		// データの登録
		try {

			// ボデー一覧データを取得する
			String[] targets = {// DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_ID],
			DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_IDNO],
					DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_LODATE],
					DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_FRAMECODE],
					DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_FRAMESEQ],
					DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_BODYNO],
					DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_RECVDAY],
					DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_VEHICLENAME],
					DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_GROUPNAME],
					DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_GROUPSTATE],
					DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_GROUPNO],
					DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_GROUPCODE],
					DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_TP],
					DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_BCNO_H0] };
			String data = new String(byteArray);
			list = Utils.getXmlTagsFromURL(data, targets);
		} catch (Exception e) {
			Log.e(LFA.TAG, e.toString());
		}
	}

	/**
	 * ボデーNO、グループをSQLiteに登録
	 *
	 * @param list
	 *            ボデー、グループリスト
	 */
	public void updateBodyData(final ArrayList<HashMap<String, String>> list) {

		HashMap<String, String> map;
		String strTemp = "";

		LFA.getmDb().beginTransaction();

		try {

			if (list.size() > 0) {

				for (int i = 0; i < list.size(); i++) {
					map = (HashMap<String, String>) list.get(i);
					ContentValues values = new ContentValues();

					// アイデントNo
					strTemp = map
							.get(DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_IDNO]);

					if (strTemp != null) {
						values.put(
								DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_IDNO],
								strTemp.trim());
					}

					// ラインオフ計画日
					strTemp = map
							.get(DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_LODATE]);

					if (strTemp != null) {
						values.put(
								DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_LODATE],
								strTemp.trim());
					}

					// フレーム区分
					strTemp = map
							.get(DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_FRAMECODE]);

					if (strTemp != null) {
						values.put(
								DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_FRAMECODE],
								strTemp.trim());
					}

					// フレーム連番
					strTemp = map
							.get(DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_FRAMESEQ]);

					if (strTemp != null) {
						values.put(
								DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_FRAMESEQ],
								strTemp.trim());
					}

					// ボデーNo
					strTemp = map
							.get(DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_BODYNO]);

					if (strTemp != null) {
						values.put(
								DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_BODYNO],
								strTemp.trim());
					}

					// 受信日
					strTemp = map
							.get(DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_RECVDAY]);

					if (strTemp != null) {
						values.put(
								DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_RECVDAY],
								strTemp.trim());
					}

					// 車種名
					strTemp = map
							.get(DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_VEHICLENAME]);

					if (strTemp != null) {
						values.put(
								DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_VEHICLENAME],
								strTemp.trim());
					}

					// 工程名称
					if (strGroupName != null) {
						values.put(
								DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_GROUPNAME],
								strGroupName.trim());
					}

					// グループ状態
					strTemp = map
							.get(DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_GROUPSTATE]);

					if (strTemp != null) {
						values.put(
								DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_GROUPSTATE],
								strTemp.trim());
					}

					// グループ順
					strTemp = map
							.get(DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_GROUPNO]);

					if (strTemp != null) {
						values.put(
								DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_GROUPNO],
								strTemp.trim());
					}

					// 工程コード
					strTemp = map
							.get(DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_GROUPCODE]);

					if (strTemp != null) {
						values.put(
								DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_GROUPCODE],
								strTemp.trim());
					}

					// TP通過時刻
					strTemp = map
							.get(DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_TP]);

					if (strTemp != null) {
						values.put(
								DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_TP],
								strTemp.trim());
					}

					// 組立連番（H0のBC連番）
					strTemp = map
							.get(DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_BCNO_H0]);

					if (strTemp != null) {
						values.put(
								DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_BCNO_H0],
								strTemp.trim());
					}

					LFA.getmDb().insert("P_ordersingItem", null, values);

				}

				LFA.getmDb().setTransactionSuccessful();
			}

		} catch (Exception e) {
			Log.e(LFA.TAG, e.toString());
			strMsg = Common.MSG_DB_ERR;
		} finally {
			LFA.getmDb().endTransaction();
		}

	}

	/**
	 * ボデー一覧データを取得
	 * @param byteArray XMLデータ
	 */
	private void setGroupTable(byte[] byteArray) {

		// データの登録
		try {

			// ボデー一覧データを取得する
			String[] targets = {
					DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_IDNO], // アイデントNo
					DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_LODATE], // ラインオフ計画日
					DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_BODYNO], // ボデーNo
					DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_RECVDAY], // 受信日
					DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_VEHICLENAME], // 車種名
					DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_GROUPNAME], // 工程名称
					DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_GROUPSTATE], // グループ状態
					DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_GROUPNO], // グループ順
					DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_GROUPCODE], // 工程コード
					DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_ORDERSINGNO], // 検査順
					DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_ORDERSINGNAME], // 検査項目名
					DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_ORDERSINGCONTENTS], // 検査内容
					DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_FILENAME], // 画像ファイル名
					DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_IMAGEFLG], // 結果区分
					// 検査画像確認画面：1　検査数値確認画面：2
					DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_RESULTFLG], // 検査結果フラグ
					// OK：0　NG：1　未検査：NULL
					DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_ORDERSINGFLG], // 検査履歴フラグ
					// 未検査：0　再検査：1
					DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_INPUTDATA], // 測定値
					DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_NGCONTENTS], // NG内容
					// 誤品：0  欠品：１　不要：２　その他３
					DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_ITEMCODE], // 項目
					DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_INSPECNO], // 検査回数
					DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_IMGRECVDAY], // 画像更新日
					DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_TIREFLG], // タイヤフラグ
					DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_OKNGFLG], // ＯＫＮＧフラグ
					DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_SIGNORDER], // 検索画像表示順
					DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_BCSIGN], // 指示記号
					DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_ORDERSINGCONTENTS2], // ダミー検査内容
					DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_FILENAME2], // ダミー画像ファイル名
					DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_IMGRECVDAY2], //ダミー 画像更新日
					DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_SIGNORDER2], // ダミー検索画像表示順
					DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_BCSIGN2], // ダミー指示記号

			};

			String data = new String(byteArray);
			list = Utils.getOrdersingXmlTagsFromURL(data, targets);

		} catch (Exception e) {
			Log.e(LFA.TAG, e.toString(), e);
		}
	}

	/**
	 * 検査内容取得
	 *
	 * @param list
	 *            list
	 */
	public void updateGroupData(ArrayList<HashMap<String, String>> list) {
		HashMap<String, String> map;
		String strTemp = "";
		LFA.getmDb().beginTransaction();
		try {

			if (list.size() > 0) {
				String strSaveContents = "";
				for (int i = 0; i < list.size(); i++) {
					map = (HashMap<String, String>) list.get(i);
					ContentValues values = new ContentValues();

					if (strSaveContents == null || strSaveContents.equals("")) {
						if (map.get(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_TIREFLG]).equals("1")) {
							strSaveContents = map.get(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_ORDERSINGCONTENTS]);
						}
					}

					// アイデントNo
					strTemp = map
							.get(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_IDNO]);

					if (strTemp != null) {
						values.put(
								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_IDNO],
								strTemp.trim());
					}

					// ラインオフ計画日
					strTemp = map
							.get(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_LODATE]);

					if (strTemp != null) {
						values.put(
								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_LODATE],
								strTemp.trim());
					}

					// ボデーNo
					strTemp = map
							.get(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_BODYNO]);

					if (strTemp != null) {
						values.put(
								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_BODYNO],
								strTemp.trim());
					}

					// 受信日
					strTemp = map
							.get(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_RECVDAY]);

					if (strTemp != null) {
						values.put(
								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_RECVDAY],
								strTemp.trim());
					}

					// 車種名
					strTemp = map
							.get(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_VEHICLENAME]);

					if (strTemp != null) {
						values.put(
								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_VEHICLENAME],
								strTemp.trim());
					}

					// 工程名称
					if (strGroupName != null) {
						values.put(
								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_GROUPNAME],
								strGroupName.trim());
					}

					// グループ状態
					strTemp = map
							.get(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_GROUPSTATE]);

					if (strTemp != null) {
						values.put(
								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_GROUPSTATE],
								strTemp.trim());
					}

					// グループ順
					strTemp = map
							.get(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_GROUPNO]);

					if (strTemp != null) {
						values.put(
								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_GROUPNO],
								strTemp.trim());
					}

					// 工程コード
					strTemp = map
							.get(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_GROUPCODE]);

					if (strTemp != null) {
						values.put(
								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_GROUPCODE],
								strTemp.trim());
					}

					// 検査順
					strTemp = map
							.get(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_ORDERSINGNO]);

					if (strTemp != null) {
						values.put(
								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_ORDERSINGNO],
								InputCheck.padLeft(strTemp.trim(), 4, "0"));
					}

					// 検査項目名
					strTemp = map
							.get(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_ORDERSINGNAME]);

					if (strTemp != null) {
						values.put(
								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_ORDERSINGNAME],
								strTemp.trim());
					}

					// 検査内容
					if (map.get(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_TIREFLG]).trim().equals("1")) {

						values.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_ORDERSINGCONTENTS], strSaveContents);

					} else {

						strTemp = map
								.get(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_ORDERSINGCONTENTS]);

						if (strTemp != null) {
							values.put(
									DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_ORDERSINGCONTENTS],
									strTemp.trim());
						}
					}

					// 画像ファイル名
					strTemp = map
							.get(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_FILENAME]);

					if (strTemp != null) {
						values.put(
								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_FILENAME],
								strTemp.trim());
					}
					// 結果区分 検査画像確認画面：1　検査数値確認画面：2
					strTemp = map
							.get(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_IMAGEFLG]);

					if (strTemp != null) {
						values.put(
								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_IMAGEFLG],
								strTemp.trim());
					}
					// 検査結果フラグ OK：0　NG：1　未検査：NULL
					strTemp = map
							.get(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_RESULTFLG]);

					if (strTemp != null) {
						values.put(
								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_RESULTFLG],
								strTemp.trim());
					}

					// 検査履歴フラグ 未検査：0　再検査：1
					strTemp = map
							.get(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_ORDERSINGFLG]);

					if (strTemp != null) {
						values.put(
								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_ORDERSINGFLG],
								strTemp.trim());
					}

					// 測定値
					strTemp = map
							.get(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_INPUTDATA]);

					if (strTemp != null) {
						values.put(
								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_INPUTDATA],
								strTemp.trim());
					}

					// NG内容 誤品：0 欠品：１　不要：２　その他３
					strTemp = map
							.get(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_NGCONTENTS]);

					if (strTemp != null) {
						values.put(
								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_NGCONTENTS],
								strTemp.trim());
					}

					// 項目
					strTemp = map
							.get(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_ITEMCODE]);

					if (strTemp != null) {
						values.put(
								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_ITEMCODE],
								strTemp.trim());
					}
					// 検査回数
					strTemp = map
							.get(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_INSPECNO]);

					if (strTemp != null) {
						values.put(
								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_INSPECNO],
								strTemp.trim());
					}

					// 画像更新日
					strTemp = map
							.get(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_IMGRECVDAY]);

					if (strTemp != null) {
						values.put(
								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_IMGRECVDAY],
								strTemp.trim());
					}

					// タイヤフラグ
					strTemp = map
							.get(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_TIREFLG]);

					if (strTemp != null) {
						values.put(
								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_TIREFLG],
								strTemp.trim());
					}

					// ＯＫＮＧフラグ
					strTemp = map
							.get(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_OKNGFLG]);

					if (strTemp != null) {
						values.put(
								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_OKNGFLG],
								strTemp.trim());
					}

					// 検索画像表示順
					strTemp = map
							.get(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_SIGNORDER]);

					if (strTemp != null) {
						values.put(
								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_SIGNORDER],
								strTemp.trim());
					}

					// 指示記号
					strTemp = map
							.get(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_BCSIGN]);

					if (strTemp != null) {
						values.put(
								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_BCSIGN],
								strTemp.trim());
					}

					// ダミー検査内容
					strTemp = map
							.get(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_ORDERSINGCONTENTS2]);
					if (strTemp != null) {
						values.put(
								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_ORDERSINGCONTENTS2],
								strTemp.trim());
					}

					// ダミー画像ファイル名
					strTemp = map
							.get(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_FILENAME2]);
					if (strTemp != null) {
						values.put(
								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_FILENAME2],
								strTemp.trim());
					}

					// ダミー画像ファイル更新日
					strTemp = map
							.get(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_IMGRECVDAY2]);
					if (strTemp != null) {
						values.put(
								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_IMGRECVDAY2],
								strTemp.trim());
					}

					// ダミー 検索画像表示順
					strTemp = map
							.get(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_SIGNORDER2]);
					if (strTemp != null) {
						values.put(
								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_SIGNORDER2],
								strTemp.trim());
					}

					// ダミー指示記号
					strTemp = map
							.get(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_BCSIGN2]);

					if (strTemp != null) {
						values.put(
								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_BCSIGN2],
								strTemp.trim());
					}

					LFA.getmDb().insert("P_ordersing", null, values);
				}

				LFA.getmDb().setTransactionSuccessful();
			}

		} catch (Exception e) {
			Log.e(LFA.TAG, e.toString());
			strMsg = Common.MSG_DB_ERR;
		} finally {
			LFA.getmDb().endTransaction();
		}

	}

	/**
	 * 検査画像取得
	 * @param idno アイデントNo
	 * @param loDate ラインオフ計画日
	 * @param groupCode 工程コード
	 */
	public void getImgFromURL(String idno, String loDate, String groupCode) {

		String sql = "SELECT fileName, imgRecvDay, fileName2, imgRecvDay2 FROM P_ordersing"
				+ " WHERE idno = '" + idno + "'"
				+ " AND   loDate = '" + loDate + "'"
				+ " AND   groupCode = '" + groupCode + "'";

		Cursor c = null;
		try {
			c = LFA.getmDb().rawQuery(sql, null);

			if (c.moveToFirst()) {

				do {
					// 正解画像ファイル
					getImg(c.getString(0), c.getString(1));
					// ダミー画像ファイル
					getImg(c.getString(2), c.getString(3));

				} while (c.moveToNext());
			}

		} catch (Exception e) {
			Log.e(LFA.TAG, e.toString(), e);
			strMsg = Common.MSG_IMGDOWNLOAD_CHECK;
		} finally {
			try {
				if (c != null) {
					c.close();
					c = null;
				}
			} catch (Exception e) {
				Log.e(LFA.TAG, e.toString(), e);
			} finally {
				c = null;
			}
		}

	}

	/**
	 * getImgメソッドで使用するSimpleDateFormat。
	 * getImgメソッドは呼び出される回数が多く、SimpleDateFormatを
	 * 毎回newするとオブジェクト生成コストが積み上がるため、
	 * オブジェクト生成が1度で済むようにクラス変数として定義する。
	 *
	 * 【効果】
	 *     工程選択～ﾎﾞﾃﾞｰNO確認画面表示までの時間（エミュレータで計測）
	 *         修正前7.5秒程度 → 修正後6.4秒程度
	 *         （約15%の改善）
	 */
	private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	/**
	 * 引数で指定されたファイルをサーバーから取得します。
	 *
	 * @param fileName
	 *            取得するファイル名
	 * @param imgRecvDay
	 *            取得するファイルの更新日
	 * @throws Exception 例外
	 */
	private void getImg(String fileName, String imgRecvDay) throws Exception {
		// 入力パラメータチェック
		if (fileName == null || fileName.equals("")) {
			return;
		}

		String strFileName = fileName.trim();
		Boolean isNewImg = false;

		// ローカルに画像ファイルが存在するかチェック。
		// ファイルが存在して最新版の場合、何もしない。
		// ファイルが存在するがサーバー上で更新されている場合、
		// もしくは存在しない場合、サーバーに最新ファイルを取りに行く
		File dir = Utils.getFile(this, strFileName);
		if (dir.exists()) {
			Date updateTime = new Date(dir.lastModified());
			Date rcvEditdate = simpleDateFormat.parse(imgRecvDay);

			// 2016/02/24 DA upd start
			if (rcvEditdate.equals(updateTime) == false) {
			//if (rcvEditdate.after(updateTime)) {
			// 2016/02/24 DA upd end
				isNewImg = true;
			}
		} else {
			isNewImg = true;
		}

		if (!isNewImg) {
			return;
		}

		// 画像ファイルを取得
		String url = Common.IMG_URL + strFileName;
		byte[] bmp1 = Utils.getByteArrayFromURL(url);

		// 画像ファイル存在しない場合
		if (!Common.STR_FILEMSG.equals("")) {

			if (Common.STR_FILEMSG.equals("noFile")) {
				Log.e(LFA.TAG, strFileName + " is not found on the server.");
			} else {
				strMsg = Common.MSG_URL_CONN_ERR;
				throw new Exception("connection is refused.");
			}

			Common.STR_FILEMSG = "";
		}

		// SDカードへ書き込む
		Utils.addImage(this, strFileName, bmp1, imgRecvDay);
	}

	/**
	 * 通過日時更新
	 * @param groupName 工程名
	 * @param bodyNo ボデーNO
	 */
	private void updateTp(final String groupName, final String bodyNo)
	{
		//訓練モードは更新しない
		if (LFA.mode != LFA.ModeList.production) {
			return;
		}

		try {
//			progressDialog = new ProgressDialog(this);
//			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//			progressDialog.setMessage("Loading...");
//			progressDialog.show();

			Thread th = new Thread(new Runnable() {
				public void run() {
					strMsg = "";

					String url = Common.REQ_URL + Common.KEY_UPDATETP + "&Koutei=" + groupName + "&body=" + bodyNo;
					byte[] byteArray = Utils.getByteArrayFromURL(url, "");
					if (byteArray == null) {
						strMsg = Common.MSG_URL_FAITH;
					}
					else {
						String data = new String(byteArray);
						String[] targets = { "result" };
						ArrayList<HashMap<String, String>> list = Utils.getXmlTagsFromURL(data, targets);

						if (list.size() == 0) {
							strMsg = Common.MSG_UPDATETP_ERROR;
						}
						else {
							// 結果判定
							HashMap<String, String> map = (HashMap<String, String>) list.get(0);
							String strResult = map.get("result");
							if (strResult.equals("NG")) {
								strMsg = Common.MSG_UPDATETP_ERROR;
							}
						}
					}

					// 2016/02/24 DA del start
//					progressDialog.dismiss();
//
//					if (!strMsg.equals("")) {
//						handler.post(new Runnable() {
//							public void run() {
//								showErrorDialog(strMsg);
//							}
//						});
//					}
					// 2016/02/24 DA del end
				}
			});
			th.start();
			th.join();
		}
		catch (Exception e) {
			Log.e(LFA.TAG, e.toString());
		}
	}

	// 2016/02/24 DA ins start
	/**
	 * 検査結果アップロード
	 */
	public void upload() {

		// アップロード対象データ取得
		ArrayList<HashMap<String, String>> uplist = getUpLoadDate();

		if (uplist.size() > 0) {
			// データが存在した場合、アップロード実施
			upLoadDateByXml(uplist);
		}
	}


	/**
	 * サーバー接続
	 * @param uplist アップロードする検査結果データ
	 */
	public void upLoadDateByXml(ArrayList<HashMap<String, String>> uplist) {

		String url = Common.REQ_URL + Common.KEY_UPLOAD;
		String strPrma = "";
		strPrma = getPrmaForGroup(uplist);

		// 接続テスト
		byte[] bytetest = Utils.getByteArrayFromURL(Common.REQ_URL, "");

		if (bytetest == null) {
			strMsg = Common.MSG_URL_FAITH;
		} else {
			byte[] byteArray = Utils.getByteArrayFromURL(url, strPrma);

			if (byteArray == null) {
				strMsg = Common.MSG_URL_FAITH;
			} else {
				String data = new String(byteArray);

				// 結果取得
				String strResult = "NG";
				String[] targets;
				ArrayList<HashMap<String, String>> list;
				HashMap<String, String> map;

				targets = new String[] { "result" };
				list = Utils.getXmlTagsFromURL(data, targets);
				if(list.size() > 0) {
					map = (HashMap<String, String>) list.get(0);
					strResult = map.get("result");
				}

				if(strResult.equals("NG")) {

					strMsg = Common.MSG_UPLOAD_FAITH;
				} else {
					LFA.getmDb().beginTransaction();
					try {
						// 2016/02/24 DA ins start
						String sql = "SELECT * FROM P_ordersing WHERE bodyNo = '" + strBodyNo
								+ "' AND groupCode = '" + strGroupCode + "'";
						Cursor c = LFA.getmDb().rawQuery(sql, null);
						if (c.moveToFirst()) {
							do {
								// アイデントNo
								String strIdno = c.getString(DB_ORDER_SING.INDEX_IDNO);
								// ラインオフ計画日
								String strLoDate = c.getString(DB_ORDER_SING.INDEX_LODATE);
								String where = "idno = '" + strIdno
										+ "' AND loDate = '" + strLoDate + "'";
								LFA.getmDb().delete("P_ordersignHistory", where, null);
							} while (c.moveToNext());
						}
						// 2016/02/24 DA ins end

						String where = "bodyNo = '" + inputDataCase
								+ "' AND groupCode = '" + strGroupCode + "'";
						LFA.getmDb().delete("P_ordersing", where, null);
						LFA.getmDb().delete("P_ordersingItem", where, null);
						LFA.getmDb().setTransactionSuccessful();
					} catch (Exception e) {
						Log.e(LFA.TAG, e.toString());
					} finally {
						LFA.getmDb().endTransaction();
					}
				}
			}
		}
	}

	/**
	 * 検査内容アップロード
	 * @return アップロードするデータ
	 */
	public ArrayList<HashMap<String, String>> getUpLoadDate() {

		// アップロードするデータ
		ArrayList<HashMap<String, String>> uplist = new ArrayList<HashMap<String, String>>();

		Cursor c = null;
		try {
			HashMap<String, String> map;
			String sql = "";

			sql = "SELECT "
					+ " oh.idno, "			// アイデントNo
					+ " oh.loDate, "		// ラインオフ計画日
					+ " oh.itemCode, "		// 項目Code
					+ " oh.selectNumber, "	// 選択回数
					+ " oh.resultFlg, "		// 検査結果フラグ
					+ " oh.inputData, "		// 測定値
					+ " oh.ngContents, "	// NG内容
					+ " oh.userID, "		// 従業員ｺｰﾄﾞ
					+ " oh.orderTime,"		// 検査時間

				    + " o.bodyNo, "			// ボデーNo
				    + " o.recvDay, "		// 受信日
				    + " o.groupCode "		// 工程コード
				    + " FROM P_ordersignHistory AS oh INNER JOIN P_ordersing AS o "
				    + " ON oh.idno = o.idno AND oh.loDate = o.loDate AND oh.itemCode = o.itemCode"
					+ " WHERE o.bodyNo = '" + strBodyNo
					+ "' AND o.groupCode = '" + strGroupCode + "'";
			c = LFA.getmDb().rawQuery(sql, null);

			if (c.moveToFirst()) {

				do {
					map = new HashMap<String, String>();

					for (int i = 0; i < c.getColumnCount(); i++) {
						map.put(c.getColumnName(i), c.getString(i));
					}


					uplist.add(map);
				} while (c.moveToNext());
			}
		} catch (Exception e) {
			Log.e(LFA.TAG, e.toString());
			uplist.clear();
		} finally {
			try {
				if (c != null) {
					c.close();
					c = null;
				}
			} catch (Exception e) {
				Log.e(LFA.TAG, e.toString(), e);
			} finally {
				c = null;
			}
		}
		return uplist;
	}


	/**
	 * XML作成
	 *
	 * @param m_List
	 *            検査結果
	 * @return XML
	 */
	private String getPrmaForGroup(
			final ArrayList<HashMap<String, String>> m_List) {
		String strRtn = "";
		StringBuilder sb = new StringBuilder();
		HashMap<String, String> mMap;
		String strTemp = "";

		if (m_List.size() > 0) {

			sb.append("<NewDataSet>");
			sb.append("\n");
			try {
				for (int i = 0; i < m_List.size(); i++) {
					sb.append("<Table>");
					sb.append("\n");
					mMap = (HashMap<String, String>) m_List.get(i);

					for (int j = 0; j < mMap.size(); j++) {
						sb.append("<" + mMap.keySet().toArray()[j].toString()
								+ ">");
						strTemp = mMap.get(mMap.keySet().toArray()[j]);
						if (strTemp != null) {
							sb.append(strTemp.trim());
						} else {
							sb.append("");
						}
						sb.append("</" + mMap.keySet().toArray()[j].toString()
								+ ">");
					}
					sb.append("\n");
					sb.append("</Table>");
					sb.append("\n");
				}

				sb.append("</NewDataSet>");
			} catch (Exception e) {
				Log.e(LFA.TAG, e.toString());
			}

		}

		strRtn = sb.toString();

		return strRtn;
	}
	// 2016/02/24 DA ins end
	/**
	 * ハードキーイベント処理
	 * @param keyCode ボタン認識
	 * @param event イベント
	 * @return false
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}
}
