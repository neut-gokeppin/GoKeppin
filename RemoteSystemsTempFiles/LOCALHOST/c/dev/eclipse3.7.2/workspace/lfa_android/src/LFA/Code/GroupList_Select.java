package LFA.Code;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 工程選択画面
 * @author cj01779
 *
 */
public class GroupList_Select extends BaseActivity implements View.OnClickListener {

	/**
	 * ライナーレイアウト定義
	 */
	LinearLayout linearLayout;

	/**
	 * ボタン定義
	 */
	static final int[] BUTTONS = { R.id.BL_BT_OK, R.id.BL_BT_CANCEL };

	/**
	 * 工程格納
	 */
	private String[] strKouteiNo;

	/**
	 * 工程情報格納
	 */
	private String[][] strKouteiInfo;

	/**
	 * ラインコード
	 */
	@SuppressWarnings("unused")
	private String strLineCode = "1";

	/**
	 * 選択されたItem格納
	 */
	private int intSelectItem = -1;

	/**
	 * メッセージ格納用変数
	 */
	private String strMsg = "";

	/**
	 * ボタンカウント格納
	 */
	Integer buttonCnt;

	/**
	 * リストビュー定義
	 */
	ListView blList;

	/**
	 * ダイアログ定義
	 */
	ProgressDialog progressDialog;

	/**
	 * リストビュー表示
	 */
	private static final int FP = ViewGroup.LayoutParams.FILL_PARENT;

	/**
	 * ordersingテーブル作成
	 */
	public static DB_ORDER_SING db_order_sign = new DB_ORDER_SING();

	/**
	 * ハンドラ定義
	 */
	private final Handler handler = new Handler();

	/**
	 * HashMap定義
	 */
	ArrayList<HashMap<String, String>> list;

	/**
	 * 工程名格納用
	 */
	private String groupName;

	/**
	 * 工程名URL格納用
	 */
	private String urlGroupName;

	/**
	 * 職制・確認工程
	 */
	private String strShokuSei = "職制・確認工程";

	/**
	 * アイデントNo
	 */
	private String strIdno = "";

	/**
	 * ラインオフ計画日
	 */
	private String strLoDate = "";

	/**
	 * 工程コード定義
	 */
	private String strGroupCode = "";

	/**
	 * ボデーNO格納変数
	 */
	private String strBodyNo = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// レイアウトの生成
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.group_list_celect);

		linearLayout = (LinearLayout) findViewById(R.id.LinearLayout03);

		// ボタンを登録
		for (int buttonId : BUTTONS) {
			Button button = (Button) findViewById(buttonId);
			button.setOnClickListener(this);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		linearLayout.removeAllViews();
		intSelectItem = -1;

		try {

			String url = Common.REQ_URL + Common.KEY_BODY;

			// 2016/02/24 DA upd start
			if (LFA.mode == LFA.ModeList.practiceKari0) {
				url += "&selectmst=0";
			}
			else if (LFA.mode == LFA.ModeList.practiceKari1) {
				url += "&selectmst=-1";
			}
			//if (LFA.mode == LFA.ModeList.practiceKari) {
			//	url += "&selectmst=0";
			//}
			// 2016/02/24 DA upd end

			String[] targets = { "groupCode", "groupNo", "groupName", "bcType" };

			byte[] byteArray = Utils.getByteArrayFromURL(url, "");
			if (byteArray == null) {
				strMsg = Common.MSG_URL_FAITH;
			} else {
				String data = new String(byteArray);
				list = Utils.getXmlTagsFromURL(data, targets);
			}

			HashMap<String, String> map;

			if (list.size() > 0) {
				buttonCnt = list.size();
				strKouteiNo = new String[buttonCnt + 1];
				strKouteiInfo = new String[buttonCnt][4];

				for (int i = 0; i < list.size(); i++) {
					map = (HashMap<String, String>) list.get(i);
					String group_code = map.get("groupCode");
					String group_no = map.get("groupNo");
					String group_name = map.get("groupName");
					String bctype = map.get("bcType");
					if (group_code != null && !group_code.equals("")) {
						strKouteiInfo[i][0] = group_code.trim();
					}
					if (group_no != null && !group_no.equals("")) {
						strKouteiInfo[i][1] = group_no.trim();
					}
					if (group_name != null && !group_name.equals("")) {
						strKouteiInfo[i][2] = group_name.trim();
						strKouteiNo[i] = group_name.trim();
					}
					if (bctype != null && !bctype.equals("")) {
						strKouteiInfo[i][3] = bctype.trim();
					}
				}
					strKouteiNo[buttonCnt] = strShokuSei;

				blList = new ListView(this);
				linearLayout.addView(blList, createParam(FP, FP));

				ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
						getApplicationContext(), R.layout.row_test3, strKouteiNo);

				blList.setAdapter(arrayAdapter);
				blList.setItemsCanFocus(true);
				blList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

				blList.setOnItemClickListener(new OnItemClickListener() {

					public void onItemClick(AdapterView<?> a, View v,
							int position, long id) {
						intSelectItem = position;
						// 選択済み工程表示する
						if (((ListView) a).getTag() != null) {
						    ((View) ((ListView) a).getTag()).setBackgroundDrawable(null);
						}
						((ListView) a).setTag(v);
						v.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
					}
				});
			} else {
				TextView tv = new TextView(this);
				tv.setId(1);
				tv.setText("データがありません");
				tv.setTextSize(16.0f);
				tv.setTextColor(getResources().getColor(R.color.WHITE));
				linearLayout.addView(tv);
			}
		} catch (Exception e) {
			Log.e(LFA.TAG, e.toString());
		}
	}

	/**
	 *
	 * @param w
	 *            w
	 * @param h
	 *            h
	 * @return レイアウト
	 */
	private LinearLayout.LayoutParams createParam(int w, int h) {
		return new LinearLayout.LayoutParams(w, h);
	}

	/**
	 * @param id
	 *            id
	 * @return dialog
	 */
	protected Dialog onCreateDialog(int id) {
		View view = LayoutInflater.from(GroupList_Select.this).inflate(
				R.layout.row_test3, null);
		return new AlertDialog.Builder(GroupList_Select.this).setView(view)
				.setTitle("Share").create();
	}

	/**
	 * ボタンクリックイベント
	 *
	 * @param v
	 *            ボタンIｄ
	 */
	public void onClick(View v) {

		// 二度押し対策
		if (!startProcess()) {
			return;
		}

		int id = v.getId();
		if (id == R.id.BL_BT_CANCEL) {
			finish();
		} else if (id == R.id.BL_BT_OK) {

			if (intSelectItem < 0) {
				showErrorDialog(Common.MSG_GROUPNO_HISU);
				endProcess();

			} else if (intSelectItem == buttonCnt) {
				Intent intent = new Intent(getApplicationContext(),
						BodyNo.class);
				intent.putExtra(Common.INTENT_EX_GROUPNAME, strShokuSei);
				startActivityForResult(intent, R.layout.bodyno_input);
				setResult(RESULT_OK);
				finish();

			} else {
				groupName = strKouteiInfo[intSelectItem][2];
				urlGroupName = Utils.urlEncode(groupName, "UTF-8");

				getKensaData();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		}
	}

	/**
	 * エラーダイアログ
	 *
	 * @param str
	 *            メッセージ
	 */
	private void showErrorDialog(String str) {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("エラー");
		b.setMessage(str);
		b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			// ボタンが押されたらダイアログを閉じる
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		b.show();
	}

	/**
	 * エラーダイアログ
	 *
	 * @param str
	 *            メッセージ
	 */
	private void intentErrorDialog(String str) {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("エラー");
		b.setMessage(str);
		b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			// ボタンが押されたらダイアログを閉じる
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent intent = new Intent(getApplicationContext(),
						BodyNo.class);
				intent.putExtra(Common.INTENT_EX_GROUPNAME, groupName);
				intent.putExtra(Common.INTENT_EX_BODYNO, strBodyNo);
				startActivityForResult(intent, R.layout.bodyno_input);
				setResult(RESULT_OK);
				finish();
			}
		});
		b.show();
	}

	/**
	 * 戻るボタン無効
	 *
	 * @param keyCode
	 *            ボタン認識
	 * @param event
	 *            イベント
	 * @return false
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

		}
		return false;

	}

	/**
	 * 検査データ取得
	 *
	 */
	private void getKensaData() {
		try {
			progressDialog = new ProgressDialog(this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Loading...");
			progressDialog.show();
			Thread th =
			new Thread(new Runnable() {
				public void run() {

					boolean isDialogDeterrence = false;

					strMsg = "";
					// 既存の検査結果をアップロードおよびクリアする
					upload();

					if (strMsg.equals("")) {

						String strTargetFrame = getTargetFrame();
						if (strTargetFrame != null
								&& !strTargetFrame.equals("")) {
							if (strMsg.equals("")) {
								String[] strFrame = strTargetFrame.split(",");

								for (int j = 0; j < strFrame.length; j++) {

									String frame[] = strFrame[j].split(":");

									if (j == 0) {
										strIdno = frame[1];
										strLoDate = frame[2];
										strGroupCode = frame[3];
										// 2016/02/24 DA ins start
										strBodyNo = frame[4];
										// 2016/02/24 DA ins end
									}

									Cursor c = null;
									try {
										String sql = "";
										sql = "SELECT * FROM P_ordersing "
												+ " WHERE idno ='"
												+ frame[1] + "'"
												+ " AND loDate ='" + frame[2]
												+ "'" + " AND groupCode ='"
												+ frame[3] + "'";
										c = LFA.getmDb().rawQuery(sql, null);
										if (c.moveToFirst()) {
											continue;
										}
									}
									catch (Exception e) {
										Log.e(LFA.TAG, e.toString(), e);
									}
									finally {
										try {
											if (c != null) {
												c.close();
												c = null;
											}
										}
										catch (Exception e) {
											Log.e(LFA.TAG, e.toString(), e);
										}
										finally {
											c = null;
										}
									}

									String url = Utils.getKensaUrl(frame[1], frame[2], frame[3]);

									byte[] byteArray = downloadKensaData(url);

									if (byteArray != null
											&& byteArray.length > 0) {
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
										getImgFromURL(frame[1], frame[2],
												frame[3]);
									}
									else {
										// 2016/02/24 DA ins start
										if ((LFA.mode == LFA.ModeList.production) || (LFA.mode == LFA.ModeList.practiceHon)) {
											// 遷移先のボディー画面にてエラー表示する
											isDialogDeterrence = true;
										}
										// 2016/02/24 DA ins end
										if (strMsg.equals("")) {
											strMsg = Common.MSG_NO_DATA;
										}
									}
								}
							}
						}
						else {
							if (strMsg.equals("")) {
								strMsg = Common.MSG_NO_DATA;
							}
							isDialogDeterrence = true;
						}
					}

					progressDialog.dismiss();

					//検査対象データが取得できない場合は、エラーダイアログを表示させないようにエラー内容を強制クリアする。(2014/11仕様変更)
					if (isDialogDeterrence) {
						strMsg = "";
					}

					Cursor c = null;
					try {
						String sql = "";
						sql = "SELECT * FROM P_ordersingItem "
								+ " WHERE idno ='" + strIdno + "'"
								+ " AND loDate ='" + strLoDate + "'"
								+ " AND groupCode ='" + strGroupCode + "'";
						c = LFA.getmDb().rawQuery(sql, null);
						if (c.moveToFirst()) {
							strBodyNo = c.getString(DB_BODY_LIST.INDEX_BODYNO);
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
							Log.e(LFA.TAG, e.toString(), e);
						} finally {
							c = null;
						}
					}

					if (!strMsg.equals("")) {
						handler.post(new Runnable() {
							public void run() {
								intentErrorDialog(strMsg);
							}
						});
					} else {
						Intent intent = new Intent(getApplicationContext(),
								BodyNo.class);
						intent.putExtra(Common.INTENT_EX_GROUPNAME, groupName);
						intent.putExtra(Common.INTENT_EX_BODYNO, strBodyNo);
						startActivityForResult(intent, R.layout.bodyno_input);
						setResult(RESULT_OK);
						finish();
					}
				}
			});
			th.start();
			// No.29 Del Itage Watanabe Start
			//th.join();
			// No.29 Del Itage Watanabe End
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
		String url = Common.REQ_URL + Common.KEY_GROUP + "&Koutei=" + urlGroupName;
		// 2016/02/24 DA upd start
		String[] targets = { "orderNo", "idno", "loDate", "groupCode", "bodyNo"};
		// String[] targets = { "orderNo", "idno", "loDate", "groupCode"};
		// 2016/02/24 DA upd end
		String result = "";
		byte[] byteArray = Utils.getByteArrayFromURL(url, "");
		if (byteArray == null) {
			strMsg = Common.MSG_NO_DATA;
			list.clear();
		} else {

			String data = new String(byteArray);
			list = Utils.getXmlTagsFromURL(data, targets);
		}
		HashMap<String, String> map;
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				map = (HashMap<String, String>) list.get(i);
				String strOrderNo = map.get("orderNo");
				String strIdno = map.get("idno");
				String strLoDate = map.get("loDate");
				String strGroupCode = map.get("groupCode");
				// 2016/02/24 DA ins start
				String strBodyNo = map.get("bodyNo");
				if (strBodyNo == null) {
					strBodyNo = "";
				}
				else {
					strBodyNo.trim();
				}
				// 2016/02/24 DA ins end
				if ((strOrderNo != null && !strOrderNo.equals(""))
						&& (strIdno != null && !strIdno.equals(""))
						&& (strLoDate != null && !strLoDate.equals(""))
						&& (strGroupCode != null && !strGroupCode.equals(""))) {
					result = result + strOrderNo.trim() + ":"
							+ strIdno.trim() + ":" + strLoDate.trim()
							+ ":" + strGroupCode.trim() +
							// 2016/02/24 DA ins start
							":" + strBodyNo
							// 2016/02/24 DA ins end
							+ ",";
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
	 * ボデー一覧データを取得
	 *
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
					if (groupName != null) {
						values.put(
								DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_GROUPNAME],
								groupName.trim());
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
					if (groupName != null) {
						values.put(
								DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_GROUPNAME],
								groupName.trim());
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
				// 2014/04/07 DA upd start
				//if (!data.trim().equals(Common.KEY_REQUEST_OK)) {
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
				// 2014/04/07 DA upd end
					strMsg = Common.MSG_UPLOAD_FAITH;
				} else {
					// LFA.mDb.beginTransaction();
					LFA.getmDb().beginTransaction();
					try {
						LFA.getmDb().delete("P_ordersignHistory", null, null);
						LFA.getmDb().delete("P_ordersing", null, null);
						LFA.getmDb().delete("P_ordersingItem", null, null);
						LFA.getmDb().setTransactionSuccessful();
					} catch (Exception e) {
						Log.e(LFA.TAG, e.toString());
					} finally {
						// LFA.mDb.endTransaction();
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

			// 2016/02/24 DA upd start
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
				    + " ON oh.idno = o.idno AND oh.loDate = o.loDate AND oh.itemCode = o.itemCode";

			// sql = "SELECT * FROM P_ordersing";
			// 2016/02/24 DA upd end

			// Cursor c = LFA.mDb.rawQuery(sql, null);
			c = LFA.getmDb().rawQuery(sql, null);

			if (c.moveToFirst()) {

				do {
					map = new HashMap<String, String>();

					// 2016/02/24 DA upd start
					for (int i = 0; i < c.getColumnCount(); i++) {
						map.put(c.getColumnName(i), c.getString(i));
					}
//					// 検索結果フラグ
//					map.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_RESULTFLG],
//							c.getString(DB_ORDER_SING.INDEX_RESULTFLG));
//					// 従業員コード
//					map.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_USERID],
//							c.getString(DB_ORDER_SING.INDEX_USERID));
//					// 測定値
//					map.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_INPUTDATA],
//							c.getString(DB_ORDER_SING.INDEX_INPUTDATA));
//					// 検査時間
//					map.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_ORDERTIME],
//							c.getString(DB_ORDER_SING.INDEX_ORDERTIME));
//					// NG内容
//					map.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_NGCONTENTS],
//							c.getString(DB_ORDER_SING.INDEX_NGCONTENTS));
//					// ﾎﾞデーNo
//					map.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_BODYNO],
//							c.getString(DB_ORDER_SING.INDEX_BODYNO));
//					// 受信日
//					map.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_RECVDAY],
//							c.getString(DB_ORDER_SING.INDEX_RECVDAY));
//					// 項目Code
//					map.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_ITEMCODE],
//							c.getString(DB_ORDER_SING.INDEX_ITEMCODE));
//					// グループCode
//					map.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_GROUPCODE],
//							c.getString(DB_ORDER_SING.INDEX_GROUPCODE));
//					// アイデントNo
//					map.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_IDNO],
//							c.getString(DB_ORDER_SING.INDEX_IDNO));
//					// ラインオフ計画日
//					map.put(DB_ORDER_SING.Columns[DB_ORDER_SING.INDEX_LODATE],
//							c.getString(DB_ORDER_SING.INDEX_LODATE));
					// 2016/02/24 DA upd end

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
}