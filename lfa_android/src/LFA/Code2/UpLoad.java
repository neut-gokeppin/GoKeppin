package LFA.Code2;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import LFA.Code2.R;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import jp.co.ctc.util.LfaCommon;
/**
 *
 * @author cj01779
 *
 */
public class UpLoad extends BaseActivity implements OnClickListener, OnItemClickListener {

	/**
	 * ボタン定義
	 */
	static final int[] BUTTONS = { R.id.NEXT, R.id.Cansel };

	/**
	 * ダイアログ定義
	 */
	ProgressDialog progressDialog;

	/**
	 * ハンドラー定義
	 */
	private final Handler handler = new Handler();

	/**
	 * テーブルの名前
	 */
	static final String TABLE = "P_ordersingItem";

	/**
	 * ボタンカウント用変数
	 */
	Integer buttonCnt = 0;

	/**
	 * TextView定義
	 */
	TextView resultFlg;

	/**
	 * メッセージ格納用変数
	 */
	private String strMsg = "";

	/**
	 * 検査内容確認用変数
	 */
	private String strMenuFlg = "";

	/**
	 * 検査結果格納用List
	 */
	ArrayList<HashMap<String, String>> list;

	/**
	 * 検査データ一式
	 */
	private String[] strItems = null;

	/**
	 * ボデーNo定義
	 */
	private String strBodyNo;

	/**
	 * TempボデーNo定義
	 */
	private String strTempBodyNo;

	/**
	 * NextボデーNo定義
	 */
	private String strNextBodyNo;

	/**
	 * 工程コード定義
	 */
	private String strGroupCode;

	/**
	 * 工程名称定義
	 */
	private String strGroupName;

	/**
	 * 工程名URL格納用
	 */
	private String urlGroupName;

	/**
	 * TextView　車両名定義
	 */
	TextView tvSyasyuName;

	/**
	 * TextView　ボデーNo定義
	 */
	TextView tvBodyNo;

	/**
	 * NG未検査リスト
	 */
	ArrayList<HashMap<String, String>> lvKensaList;

	/**
	 * NG内容(漢字)
	 */
	private String strNgContent;

	/**
	 * NG内容
	 */
	private String strNgCheck;

	/**
	 * LO日
	 */
	private String strlodate = "";

	/**
	 * 受信日表示用
	 */
	private String outdataTime = "";

	/**
	 * リストビュー表示
	 */
	private static final int FP = ViewGroup.LayoutParams.FILL_PARENT;

	/**
	 * ライナーレイアウト定義
	 */
	LinearLayout linearLayout;

	/**
	 * リストビュー定義
	 */
	ListView blList;

	/**
	 * 検査順格納変数
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
	 * 車種名
	 */
	private String strSyasyuName = "";

	/**
	 * NextアイデントNo
	 */
	private String strNextIdno = "";

	/**
	 * Nextラインオフ計画日
	 */
	private String strNextLoDate = "";

	/**
	 * Next工程コード
	 */
	private String strNextGroupCode = "";

	// 2014/04/07 DA ins start
	/**
	 * 判定結果
	 */
	private String strResult = "";

	/**
	 * 判定リスト
	 */
	private String strJudgmentList = "";
	// 2014/04/07 DA ins end

	/**
	 * 次に検査する車両の情報を格納する変数。
	 * 別スレッドに渡すため、クラス変数とする。
	 */
	private String strTargetFrame = "";

	/**
	 * Called when the activity is first created.
	 *
	 * @param savedInstanceState
	 *            引数
	 * */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.upload);

		list = new ArrayList<HashMap<String, String>>();
		lvKensaList = new ArrayList<HashMap<String, String>>();
		strBodyNo = getIntent().getStringExtra(Common.INTENT_EX_BODYNO);
		strGroupCode = getIntent().getStringExtra(Common.INTENT_EX_GROUPNO);
		strItems = getIntent().getStringArrayExtra(Common.INTENT_EX_CHECKITEMS);
		intItemNo = getIntent().getIntExtra(Common.INTENT_EX_GROUPCOUNT, 0);
		strIdno = getIntent().getStringExtra(Common.INTENT_EX_IDNO);
		strLoDate = getIntent().getStringExtra(Common.INTENT_EX_LO_DATE);
		strMenuFlg = getIntent().getStringExtra(Common.INTENT_EX_CHECKKARAMODORU);

		String sql = "SELECT * FROM P_ordersing"
				+ " WHERE bodyNo = '" + strBodyNo
				+ "' AND groupCode = '" + strGroupCode + "'"
				+ " ORDER BY ordersignNo";

		Cursor c = null;
		try {
			c = LFA.getmDb().rawQuery(sql, null);
			if (c.moveToFirst()) {
				strlodate = c.getString(DB_ORDER_SING.INDEX_LODATE);
				if (strlodate != null && !strlodate.equals("")) {
					outdataTime = strlodate.substring(0, 4) + "/"
							+ strlodate.substring(4, 6) + "/"
							+ strlodate.substring(6, 8);
				}
				strSyasyuName = c.getString(DB_ORDER_SING.INDEX_VEHICLENAME);
				strGroupName = c.getString(DB_ORDER_SING.INDEX_GROUPNAME);
				try {
					urlGroupName = URLEncoder.encode(strGroupName, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					Log.e(LFA.TAG, e.toString());
				}

				strIdno = c.getString(DB_ORDER_SING.INDEX_IDNO);
				strLoDate = c.getString(DB_ORDER_SING.INDEX_LODATE);

				// ordersingNo格納用List
				List<String> ordersignNoList = new ArrayList<String>();

				// 全検査項目のordersingNoを格納
				do {
					String ordersignNo = c.getString(DB_ORDER_SING.INDEX_ORDERSINGNO);
					ordersignNoList.add(ordersignNo);
				} while (c.moveToNext());

				// Listを配列に変換
				strItems = ordersignNoList.toArray(new String[0]);
			}
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

		sql = "SELECT * FROM P_ordersing" + " WHERE bodyNo = '" + strBodyNo
				+ "' AND groupCode = '" + strGroupCode
				+ "' AND resultFlg IN ('', '1', '2')"
				+ " ORDER BY ordersignNo";
		try {

			c = LFA.getmDb().rawQuery(sql, null);

			if (c.moveToFirst()) {
				setContentView(R.layout.upload2);
				linearLayout = (LinearLayout) findViewById(R.id.LinearLayout01);
				do {
					strNgCheck = c.getString(DB_ORDER_SING.INDEX_NGCONTENTS);

					HashMap<String, String> ngMap = new HashMap<String, String>();
					if (strNgCheck.equals("")) {
						strNgContent = "未検";
					} else {
						if (strNgCheck.equals("0")) {
							strNgContent = "誤品";
						} else if (strNgCheck.equals("1")) {
							strNgContent = "欠品";
						} else if (strNgCheck.equals("2")) {
							strNgContent = "不要";
						} else if (strNgCheck.equals("3")) {
							strNgContent = "その他";
						}
					}

					ngMap.put("name", c.getString(DB_ORDER_SING.INDEX_ORDERSINGNAME));
					ngMap.put("info", strNgContent);
					ngMap.put("orderSingNo", c.getString(DB_ORDER_SING.INDEX_ORDERSINGNO));
					ngMap.put("tireFlg", c.getString(DB_ORDER_SING.INDEX_TIREFLG));
					lvKensaList.add(ngMap);
				} while (c.moveToNext());

				blList = new ListView(this);
				linearLayout.addView(blList, createParam(FP, FP));

				SimpleAdapter arrayAdapter = new SimpleAdapter(
						getApplicationContext(),
						lvKensaList, R.layout.row_test4,
						new String[] {"name", "info"},
						new int[] {R.id.name, R.id.info});

				blList.setAdapter(arrayAdapter);
				blList.setItemsCanFocus(true);
				blList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				blList.setOnItemClickListener(this);
			}

			tvSyasyuName = (TextView) findViewById(R.id.KS_TV_SYASYUNAME);
			tvBodyNo = (TextView) findViewById(R.id.KS_TV_BODYNO);

			if (strSyasyuName != null) {
				tvSyasyuName.setText(strSyasyuName);
			}
			if (strBodyNo != null && strlodate != null) {
				tvBodyNo.setText("BNO." + strBodyNo + " " + outdataTime);
			}

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

		// ボタンを登録
		for (int buttonId : BUTTONS) {
			Button button = (Button) findViewById(buttonId);
			button.setOnClickListener(this);
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	/**
	 * ボタンクリックイベント
	 *
	 * @param v button引数
	 */
	public void onClick(final View v) {

		// 二度押し対策
		if (!startProcess()) {
			return;
		}

		if (v.getId() == R.id.NEXT) {
			// 2014/04/07 DA upd start
			progressDialog = new ProgressDialog(this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Loading...");
			progressDialog.setCancelable(false);
			progressDialog.show();
			Thread th =
			new Thread(new Runnable() {
				public void run() {
					nextButtonMain();
				}
			});
			th.start();
			// 2014/04/07 DA upd end

		} else if (v.getId() == R.id.Cansel) {
			if (strMenuFlg != null && strMenuFlg.equals("kensa")) {
				Intent intent = new Intent(getApplicationContext(), Kensa.class);
				intent.putExtra(Common.INTENT_EX_GROUPCOUNT, intItemNo);
				intent.putExtra(Common.INTENT_EX_CHECKITEMS, strItems);
				intent.putExtra(Common.INTENT_EX_BODYNO, strBodyNo);
				intent.putExtra(Common.INTENT_EX_GROUPNO, strGroupCode);
				intent.putExtra(Common.INTENT_EX_IDNO, strIdno);
				intent.putExtra(Common.INTENT_EX_LO_DATE, strLoDate);
				startActivityForResult(intent, R.layout.kensa);
				setResult(RESULT_OK);
			}
			if (strMenuFlg != null && strMenuFlg.equals("kensa2")) {
				Intent intent = new Intent(getApplicationContext(),
						Kensa2.class);
				intent.putExtra(Common.INTENT_EX_GROUPCOUNT, intItemNo);
				intent.putExtra(Common.INTENT_EX_CHECKITEMS, strItems);
				intent.putExtra(Common.INTENT_EX_BODYNO, strBodyNo);
				intent.putExtra(Common.INTENT_EX_GROUPNO, strGroupCode);
				intent.putExtra(Common.INTENT_EX_IDNO, strIdno);
				intent.putExtra(Common.INTENT_EX_LO_DATE, strLoDate);
				startActivityForResult(intent, R.layout.kensa2);
				setResult(RESULT_OK);
			}
			if (strMenuFlg != null && strMenuFlg.equals("kensaTire")) {
				Intent intent = new Intent(getApplicationContext(),
						KensaTire.class);
				intent.putExtra(Common.INTENT_EX_GROUPCOUNT, intItemNo);
				intent.putExtra(Common.INTENT_EX_CHECKITEMS, strItems);
				intent.putExtra(Common.INTENT_EX_BODYNO, strBodyNo);
				intent.putExtra(Common.INTENT_EX_GROUPNO, strGroupCode);
				intent.putExtra(Common.INTENT_EX_IDNO, strIdno);
				intent.putExtra(Common.INTENT_EX_LO_DATE, strLoDate);
				startActivityForResult(intent, R.layout.kensa_tire);
				setResult(RESULT_OK);
			}

			finish();
		}
	}

	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		if (resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		}
	}

	/**
	 * 次へボタン押下時の画面遷移処理
	 * @author DA 2014/04/07
	 */
	public void nextButtonScreenTransition()
	{
		if(strResult.equals("TIRE-NG")) {
			Intent intent = new Intent(getApplicationContext(), ResultTire.class);
			intent.putExtra(Common.INTENT_EX_GROUPNAME, strGroupName);
			intent.putExtra(Common.INTENT_EX_BODYNO, strBodyNo);
			intent.putExtra(Common.INTENT_EX_VEHICLENAME, strSyasyuName);
			intent.putExtra(Common.INTENT_EX_LODATE, strlodate);
			intent.putExtra(Common.INTENT_EX_JUDGMENTLIST, strJudgmentList);
			intent.putExtra(Common.INTENT_EX_NEXT_BODYNO, strNextBodyNo);
			intent.putExtra(Common.INTENT_EX_ERROR_MESSAGE, strMsg);
			startActivityForResult(intent, R.layout.result_tire);
		}
		else {
			Intent intent = new Intent(getApplicationContext(), BodyNo.class);
			intent.putExtra(Common.INTENT_EX_GROUPNAME, strGroupName);
			intent.putExtra(Common.INTENT_EX_BODYNO, strNextBodyNo);
			startActivityForResult(intent, R.layout.bodyno_input);
		}

		// 通信できないときにアプリが止まらないよう、別スレッドにする
		Thread th = new Thread(new Runnable() {
			public void run() {
				// PDA内ログをサーバーへ送信
				Utils.flushLog();
			}
		});
		th.start();

		setResult(RESULT_OK);
		finish();
	}

	/**
	 * 次へボタン押下時の処理
	 * @author DA 2014/04/07
	 */
	public void nextButtonMain()
	{
		strMsg = "";

		Cursor c = null;
		String sql = "SELECT * FROM P_ordersingItem" + " WHERE groupName = '" + strGroupName
				// 2016/08/22 DA upd start
				//+ "' ORDER BY tp, _id";
				+ "' ORDER BY _id";
				// 2016/08/22 DA upd end
		try {
			c = LFA.getmDb().rawQuery(sql, null);
			if(c.moveToFirst()) {
				do {
					strTempBodyNo = c.getString(DB_BODY_LIST.INDEX_BODYNO);
					if(strTempBodyNo.equals(strBodyNo)) {
						if(c.moveToNext()) {
							strNextBodyNo = c.getString(DB_BODY_LIST.INDEX_BODYNO);
							strNextIdno = c.getString(DB_BODY_LIST.INDEX_IDNO);
							strNextLoDate = c.getString(DB_BODY_LIST.INDEX_LODATE);
							strNextGroupCode = c.getString(DB_BODY_LIST.INDEX_GROUPCODE);
						}
					}
				}
				while(c.moveToNext());
			}
		}
		catch(Exception e) {
			Log.e(LFA.TAG, e.toString());
		}
		finally {
			try {
				if(c != null) {
					c.close();
					c = null;
				}
			}
			catch(Exception e) {
				Log.e(LFA.TAG, e.toString());
				e.printStackTrace();
			}
			finally {
				c = null;
			}
		}

		// 次に検査する車両をサーバーから取得し、
		// ローカルDBから取得した次車両と一致するか確認する。
		// 一致しなかった場合、次車両をサーバーから取得した
		// ものになるようにする
		strTargetFrame = getTargetFrame();
		if(strTargetFrame != null
				&& !strTargetFrame.equals("")) {
			if(strMsg.equals("")) {
				String[] strFrame = strTargetFrame
						.split(",");
				String[] frame = strFrame[0].split(":");

				if(strNextIdno == null || !strNextIdno.equals(frame[1])
						|| strNextLoDate == null || !strNextLoDate.equals(frame[2])
						|| strNextGroupCode == null || !strNextGroupCode.equals(frame[3])) {
					// 2016/02/24 DA ins start
					if ((LFA.mode == LFA.ModeList.production) || (LFA.mode == LFA.ModeList.practiceHon)) {
						strNextBodyNo = frame[4];
						// 遷移先のボディー画面にてエラー表示する
					}
					else {
						strNextBodyNo = "";
					}
					// 2016/02/24 DA ins end
					strNextIdno = frame[1];
					strNextLoDate = frame[2];
					strNextGroupCode = frame[3];
				}
			}
		}

		// 異常が無ければアップロード処理を行う
		if (strMsg.equals("")) {
			updateGroup();
		}

		progressDialog.dismiss();

		if(strResult.equals("TIRE-NG")) {
			//タイヤメーカー判定画面で表示させる
			nextButtonScreenTransition();
		}
		else {
			if(!strMsg.equals("")) {
				handler.post(new Runnable()
				{
					public void run()
					{
						nextButtonErrorDialog(strMsg);
					}
				});
			}
			else {
				nextButtonScreenTransition();
			}
		}
	}

	/**
	 * 検査結果アップロード
	 */
	public void updateGroup() {
		try {
			Thread th =
			new Thread(new Runnable() {
				public void run() {

					boolean isDialogDeterrence = false;

					list.clear();
					getUpLoadDate();

					if (list.size() > 0) {

						// 2016/02/24 DA upd start
						list.clear();
						getUpLoadInspectionDate();

						if (list.size() > 0) {
							// 検査対象の検査済データが存在する場合は、検査対象データをアップロードして削除する
							upLoadDateByXml();
						}
						else {
							// 検査対象の検査済データが存在しない場合は、検査対象データのみを削除する
							deleteKensaData(strBodyNo, strGroupCode);
						}
						// 2016/02/24 DA upd end

						if (strMsg.equals("")) {

							if (strTargetFrame != null
									&& !strTargetFrame.equals("")) {
								if (strMsg.equals("")) {

									String[] strFrame = strTargetFrame
											.split(",");

									for (int j = 0; j < strFrame.length; j++) {

										String frame[] = strFrame[j].split(":");

										Cursor c = null;
										try {
											String sql = "";
											sql = "SELECT * FROM P_ordersing "
													+ " WHERE idno ='"
													+ frame[1] + "'"
													+ " AND loDate ='"
													+ frame[2] + "'"
													+ " AND groupCode ='"
													+ frame[3] + "'";
											c = LFA.getmDb()
													.rawQuery(sql, null);
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
											list.clear();
											setBodyTable(byteArray);
											updateBodyData(list);
											list.clear();
											setGroupTable(byteArray);
											updateGroupData(list);

											// 画像を取得する
											getImgFromURL(frame[1], frame[2], frame[3]);
										}
										else {
											if (strMsg.equals("")) {
												strMsg = Common.MSG_NO_DATA;
											}
											isDialogDeterrence = true;
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
					}
					else {
						// 検査対象データが存在しない場合、アップロード失敗（存在しないことは、ありえないはず）
						strMsg = Common.MSG_DATA_CHCEK;
					}

					//検査対象データが取得できない場合は、エラーダイアログを表示させないようにエラー内容を強制クリアする。(2014/11仕様変更)
					if (isDialogDeterrence) {
						strMsg = "";
					}

					// 2014/04/07 DA del start
//					if (!strMsg.equals("")) {
//						handler.post(new Runnable() {
//							public void run() {
//								showErrorDialog(strMsg);
//							}
//						});
//					}
					// 2014/04/07 DA del end
				}
			});
			th.start();
			// 2014/04/07 DA ins start
			th.join();
			// 2014/04/07 DA ins end

			// 2017/12/01 DA ins start
			// 撮影画像の取得
			boolean isJudgment = false;
			boolean isLandscape = isLandscapeOrientation();
			if (isLandscape) {
				boolean isShotimage = LfaCommon.isJudgmentShotimage(strGroupName);
				if (isShotimage) {
					isJudgment = true;
				}
			}
			if (isJudgment) {
				sidown.downloadShotimageBg(getApplicationContext(), strGroupName);
			}
			// 2017/12/01 DA ins end

			// 2014/04/07 DA ins start
			if(strTargetFrame == null || strTargetFrame.equals("")) {
				// サーバーから取得できない場合は次車両は無し
				strNextBodyNo = "";
			}
			else {
			// 2014/04/07 DA ins end
				if (strNextBodyNo == null || strNextBodyNo.equals("")) {
					// 2014/04/07 DA del start
					//th.join();
					// 2014/04/07 DA del end

					Cursor c = null;
					try {
						String sql = "";
						sql = "SELECT * FROM P_ordersingItem "
								+ " WHERE idno ='" + strNextIdno + "'"
								+ " AND loDate ='" + strNextLoDate + "'"
								+ " AND groupCode ='" + strNextGroupCode + "'";
						c = LFA.getmDb().rawQuery(sql, null);
						if (c.moveToFirst()) {
							strNextBodyNo = c.getString(DB_BODY_LIST.INDEX_BODYNO);
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
				}
			// 2014/04/07 DA ins start
			}
			// 2014/04/07 DA ins end
		} catch (Exception e) {
			Log.e(LFA.TAG, e.toString());
		}
	}

	/**
	 * サーバー接続
	 */
	public void upLoadDateByXml() {

		String url = Common.REQ_URL + Common.KEY_UPLOAD;
		String strPrma = "";
		strPrma = getPrmaForGroup(list);

		byte[] byteArray = Utils.getByteArrayFromURL(url, strPrma);

		if (byteArray == null) {
			strMsg = Common.MSG_URL_FAITH;
		} else {
			String data = new String(byteArray);
			// 2014/04/07 DA upd start
			//if (!data.trim().equals(Common.KEY_REQUEST_OK)) {
			//	strMsg = Common.MSG_UPLOAD_FAITH;
			//} else {
			// 結果取得
			strResult = "NG";
			strJudgmentList = "";

			boolean isTire = false;
			boolean isDelete = false;
			String[] targets;
			ArrayList<HashMap<String, String>> list;
			HashMap<String, String> map;

			targets = new String[] { "result" };
			list = Utils.getXmlTagsFromURL(data, targets);
			if(list.size() > 0) {
				map = (HashMap<String, String>) list.get(0);
				strResult = map.get("result");
			}

			// 結果判定
			if(strResult.equals("OK")) {
				isDelete = true;
			}
			else if(strResult.equals("TIRE-NG")) {
				isTire = true;
				isDelete = true;
			}
			else if(strResult.equals("NG")) {
				strMsg = Common.MSG_UPLOAD_FAITH;
			}
			else {
				isDelete = true;
			}

			// タイヤメーカー判定結果画面に渡す情報の設定
			if(isTire)
			{
				targets = new String[] { "orderNo", "itemName", "inputData", "inspecResult", "ngReason" };
				list = Utils.getXmlTagsFromURL(data, targets);
				for(int i = 0; i < list.size(); i++) {
					map = (HashMap<String, String>) list.get(i);
					String strOrderNo = map.get("orderNo");
					String strItemName = map.get("itemName");
					String strInputData = map.get("inputData");
					String strInspecResult = map.get("inspecResult");
					String strNgReason = map.get("ngReason");

					if(strOrderNo == null || strItemName == null || strInputData == null || strInspecResult == null || strNgReason == null) {
						continue;
					}

					strJudgmentList = strJudgmentList + strOrderNo.trim()
							+ ":" + strItemName.trim() + ":" + strInputData.trim()
							+ ":" + strInspecResult.trim() + ":" + strNgReason.trim() + ",";
				}
				// 最後のカンマを削除
				if(strJudgmentList.length() > 0) {
					strJudgmentList = strJudgmentList.substring(0, strJudgmentList.length() - 1);
				}
			}

			// DB削除
			if(isDelete) {
			// 2014/04/07 DA upd end
				// 2016/02/24 DA upd start
				deleteKensaData(strBodyNo, strGroupCode);
//				// LFA.mDb.beginTransaction();
//				LFA.getmDb().beginTransaction();
//				try {
//					String where = "bodyNo = '" + strBodyNo
//							+ "' AND groupCode = '" + strGroupCode + "'";
//					LFA.getmDb().delete("P_ordersing", where, null);
//					// No.39 Edit Itage Watanabe start
//					LFA.getmDb().delete("P_ordersingItem", where, null);
//					// No.39 Edit Itage Watanabe end
//					LFA.getmDb().setTransactionSuccessful();
//				} catch (Exception e) {
//					Log.e(LFA.TAG, e.toString());
//				} finally {
//					// LFA.mDb.endTransaction();
//					LFA.getmDb().endTransaction();
//				}
				// 2016/02/24 DA upd end
			}
		}
	}

	// 2016/02/24 DA ins start
	/**
	 * アップロードする検査対象データを取得する
	 */
	public void getUpLoadDate()
	{
		Cursor c = null;
		try {
			HashMap<String, String> map;
			String sql = "";
			sql = "SELECT * FROM P_ordersing WHERE bodyNo = '" + strBodyNo
					+ "' AND groupCode = '" + strGroupCode + "'";

			c = LFA.getmDb().rawQuery(sql, null);
			if (c.moveToFirst()) {
				do {
					map = new HashMap<String, String>();

					for (int i = 0; i < c.getColumnCount(); i++) {
						map.put(c.getColumnName(i), c.getString(i));
					}

					list.add(map);
				}
				while (c.moveToNext());
			}
		}
		catch (Exception e) {
			Log.e(LFA.TAG, e.toString());
			list.clear();
		}
		finally {
			try {
				if (c != null) {
					c.close();
					c = null;
				}
			}
			catch (Exception e) {
				Log.e(LFA.TAG, e.toString());
				e.printStackTrace();
			}
			finally {
				c = null;
			}
		}
	}
	// 2016/02/24 DA ins end

	/**
	 * アップロードする検査対象の検査済データを取得する
	 */
	public void getUpLoadInspectionDate() {
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
				    + " ON oh.idno = o.idno AND oh.loDate = o.loDate AND oh.itemCode = o.itemCode"
					+ " WHERE o.bodyNo = '" + strBodyNo
					+ "' AND o.groupCode = '" + strGroupCode + "'";

			// sql = "SELECT * FROM P_ordersing WHERE bodyNo = '" + strBodyNo
			//		+ "' AND groupCode = '" + strGroupCode + "'";
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
//					// 検査結果フラグ
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

					list.add(map);
				} while (c.moveToNext());
			}
		} catch (Exception e) {
			Log.e(LFA.TAG, e.toString());
			list.clear();
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

	/**
	 * エラーダイアログ
	 *
	 * @param str
	 *            メッセージ内容
	 */
	@SuppressWarnings("unused")
	private void showErrorDialog(final String str) {
		Intent intent = new Intent();
		intent.putExtra(Common.INTENT_EX_MSG, str);
		intent.setAction(Common.INTENT_BROADCAST);
		sendOrderedBroadcast(intent, null);
	}

	/**
	 * 次へボタン押下時のエラーダイアログ処理
	 * @param str エラー内容
	 * @author DA 2014/04/07
	 */
	private void nextButtonErrorDialog(String str)
	{
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setCancelable(false);
		b.setTitle("エラー");
		b.setMessage(str);
		b.setPositiveButton("OK", new DialogInterface.OnClickListener()
		{
			// ボタンが押されたらダイアログを閉じる
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				nextButtonScreenTransition();
			}
		});
		b.show();
	}

	/**
	 * 戻るボタン無効
	 *
	 * @param keyCode
	 *            keyCode
	 * @param event
	 *            キーイベント
	 * @return false
	 */
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

		}
		return false;

	}

	/**
	 * アイデントNoとラインオフ計画日を取得する
	 *
	 * @return 取得したデータ
	 */
	private String getTargetFrame() {

		// 接続テスト
		byte[] bytetest = getByteArrayFromURLRetry(Common.REQ_URL);

		if (bytetest == null) {
			// 通信失敗 ⇒ WiFi入れなおす
			wifiRestart();

			// 再度、接続テスト
			bytetest = getByteArrayFromURLRetry(Common.REQ_URL);
			if (bytetest == null) {
				// 通信失敗 ⇒ あきらめる
				strMsg = Common.MSG_URL_FAITH;
				return null;
			}

			// 通信成功 ⇒ WiFi接続状態ログ出力
			Utils.log(getClass() + ".getTargetFrame()" + getWifiInfo());
		}

		String url = Common.REQ_URL + Common.KEY_GROUP + "&Koutei=" + urlGroupName + "&body=" + strBodyNo;
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
			buttonCnt = list.size();

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

				result = result + strOrderNo.trim() + ":" + strIdno.trim()
						+ ":" + strLoDate.trim() + ":" + strGroupCode +
						// 2016/02/24 DA ins start
						":" + strBodyNo
						// 2016/02/24 DA ins end
						+ ",";
			}
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}

	/**
	 * WiFiをOFF→ONする
	 */
	private void wifiRestart() {
		// WiFi接続状態ログ出力
		Utils.log(getClass() + ".wifiRestart()" + getWifiInfo());

		// WiFi入れなおす
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		wifiManager.setWifiEnabled(false);
		wifiManager.setWifiEnabled(true);

		// WiFi入れなおした後、通信できるまで少し時間がかかるので待つ
		final int sleep = 5000;
		Utils.sleep(sleep);
	}

	/**
	 * Webサーバーからデータを取得する。通信失敗時、5回までリトライする
	 * @param url URL
	 * @return 取得したデータ
	 */
	private byte[] getByteArrayFromURLRetry(String url) {
		// 通信のタイムアウト時間
		final int timeout = 5000;
		// 通信失敗時、次のリクエストを発行するまでの待ち時間
		final int sleep = 1000;
		// リトライ回数
		final int retryMax = 5;
		// 接続
		for (int i = 0; i < retryMax; i++) {
			byte[] bytetest = Utils.getByteArrayFromURL(url + (i > 0 ? "?retrycount=" + i : ""), "", timeout);
			if (bytetest != null) {
				// 通信成功
				return bytetest;
			} else if (i < retryMax - 1) {
				// 通信失敗。4回目まで ⇒ 1秒待つ
				Utils.sleep(sleep);
			}
		}
		return null;
	}


	/**
	 * Wi-Fi情報を取得する。
	 * @return Wi-Fi情報
	 */
	private String getWifiInfo() {
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		String log = "\r\nSSID:" + wifiInfo.getSSID()
				+ "\r\nBSSID:" + wifiInfo.getBSSID()
				+ "\r\nIP Address:" + wifiInfo.getIpAddress()
				+ "\r\nMac Address:" + wifiInfo.getMacAddress()
				+ "\r\nNetwork ID:" + wifiInfo.getNetworkId()
				+ "\r\nLink Speed:" + wifiInfo.getLinkSpeed()
				+ "\r\nRSSI:" + wifiInfo.getRssi()
				+ "\r\nSupplicant State:" + wifiInfo.getSupplicantState().toString();

		return log;
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
					//DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_TP],			// 2016/08/22 DA del
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

					// 2016/08/22 DA del start
					//// TP通過時刻
					//strTemp = map
					//		.get(DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_TP]);
					//
					//if (strTemp != null) {
					//	values.put(
					//			DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_TP],
					//			strTemp.trim());
					//}
					// 2016/08/22 DA del end

					// 組立連番（H0のBC連番）
					strTemp = map
							.get(DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_BCNO_H0]);

					if (strTemp != null) {
						values.put(
								DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_BCNO_H0],
								strTemp.trim());
					}

					// 2017/12/01 DA ins start
					// 撮影画像状態
					values.put(DB_BODY_LIST.Columns[DB_BODY_LIST.INDEX_SHOTIMAGESTATE], 0);
					// 2017/12/01 DA ins end

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
				Log.e(LFA.TAG, e.toString());
				e.printStackTrace();
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
	 *
	 * @param w w
	 * @param h h
	 * @return レイアウト
	 */
	private LinearLayout.LayoutParams createParam(int w, int h) {
		return new LinearLayout.LayoutParams(w, h);
	}


	/**
	 * 不具合項目をクリックしたときの処理
	 *
	 * @param parent {@inheritDoc}
	 * @param view {@inheritDoc}
	 * @param position {@inheritDoc}
	 * @param id {@inheritDoc}
	 */
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		// タッチされた行のデータを取得
		Map<String, String> item = lvKensaList.get(position);

		// タッチされた行が何番目の検査項目なのかを検索
		int groupcount = Arrays.asList(strItems).indexOf(item.get("orderSingNo"));

		// 選択された検査項目の検査画面に遷移
		Intent intent = new Intent();
		intent.putExtra(Common.INTENT_EX_GROUPCOUNT, groupcount);
		intent.putExtra(Common.INTENT_EX_CHECKITEMS, strItems);
		intent.putExtra(Common.INTENT_EX_BODYNO, strBodyNo);
		intent.putExtra(Common.INTENT_EX_GROUPNO, strGroupCode);
		intent.putExtra(Common.INTENT_EX_IDNO, strIdno);
		intent.putExtra(Common.INTENT_EX_LO_DATE, strLoDate);
		if (item.get("tireFlg").equals("1")) {
			intent.setClass(getApplicationContext(), KensaTire.class);
			startActivityForResult(intent, R.layout.kensa_tire);
		} else {
			intent.setClass(getApplicationContext(), Kensa2.class);
			startActivityForResult(intent, R.layout.kensa2);
		}

		// 画面終了
		setResult(RESULT_OK);
		finish();

	}

	// 2016/02/24 DA ins start
	/**
	 * 検査データ削除
	 * @param strBodyNo ボデーNO
	 * @param strGroupCode 工程コード
	 */
	private void deleteKensaData(String strBodyNo, String strGroupCode)
	{
		LFA.getmDb().beginTransaction();
		try {
			// ボデーNOを持っていないP_ordersignHistoryを先に削除
			String sql = "SELECT * FROM P_ordersing WHERE bodyNo = '" + strBodyNo
					+ "' AND groupCode = '" + strGroupCode + "'";
			Cursor c = LFA.getmDb().rawQuery(sql, null);
			if (c.moveToFirst()) {
				do {
					String strIdno = c.getString(DB_ORDER_SING.INDEX_IDNO);
					String strLoDate = c.getString(DB_ORDER_SING.INDEX_LODATE);
					String where = "idno = '" + strIdno
							+ "' AND loDate = '" + strLoDate + "'";
					LFA.getmDb().delete("P_ordersignHistory", where, null);
				}
				while (c.moveToNext());
			}

			String where = "bodyNo = '" + strBodyNo
					+ "' AND groupCode = '" + strGroupCode + "'";
			LFA.getmDb().delete("P_ordersing", where, null);
			LFA.getmDb().delete("P_ordersingItem", where, null);
			LFA.getmDb().setTransactionSuccessful();
		}
		catch (Exception e) {
			Log.e(LFA.TAG, e.toString());
		}
		finally {
			LFA.getmDb().endTransaction();
		}
	}
	// 2016/02/24 DA ins end
}