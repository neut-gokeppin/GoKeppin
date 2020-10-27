package LFA.Code2;

import java.util.ArrayList;

import LFA.Code2.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * 検査項目一覧画面
 *
 * TODO NGの項目に色を付ける
 *
 * @author cj01779
 */
public class KensaList extends BaseActivity implements View.OnClickListener {

	/**
	 * ライナーレイアウト定義
	 */
	LinearLayout linearLayout;

	/**
	 * リストビュー用空白定義
	 */
	private static final String STR_SPACE = " ";

	/**
	 * ボタン定義
	 */
	static final int[] BUTTONS = { R.id.BL_BT_OK, R.id.BL_BT_CANCEL };

	/**
	 * 検査項目コード格納
	 */
	private String[] strItemCodes;

	/**
	 * 検査NO格納
	 */
	private String[] strOrdersignNos;

	/**
	 * ボデーNo定義
	 */
	private String strBodyNo;

	/**
	 * 工程コード定義
	 */
	private String strGroupCode;

	/**
	 * 選択されたItem格納
	 */
	private int intSelectItem = -1;

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


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// レイアウトの生成
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.kensa_list);

		strBodyNo = getIntent().getStringExtra(Common.INTENT_EX_BODYNO);
		strGroupCode = getIntent().getStringExtra(Common.INTENT_EX_GROUPNO);
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
		int iPosition = -1;

		// リストに表示する値の格納用
		ArrayList<String> arr = new ArrayList<String>();

		String sql = "SELECT * FROM P_ordersing"
				+ " WHERE bodyNo='" + strBodyNo + "'"
				+ " AND groupCode='" + strGroupCode + "'"
				+ " ORDER BY ordersignNo";

		Cursor c = null;
		try {

			c = LFA.getmDb().rawQuery(sql, null);

			if (c.moveToFirst()) {
				// 検索結果を順番に取り出しリストに追加
				buttonCnt = c.getCount();
				strItemCodes = new String[buttonCnt];
				strOrdersignNos = new String[buttonCnt];
				String btnText;
				String strJyoutai = "";

				// タイトル部に工程名とボデーNOを表示する
				String strGroupName = c.getString(DB_ORDER_SING.INDEX_GROUPNAME);
				TextView tvTitle = (TextView) findViewById(R.id.TextView02);
				tvTitle.setText(getString(R.string.kensaList) + " BNO." + strBodyNo + " " + strGroupName);


				for (int i = 0; i < buttonCnt; i++) {

					String ordersignNo = c.getString(DB_ORDER_SING.INDEX_ORDERSINGNO);
					String ordersignName = c.getString(DB_ORDER_SING.INDEX_ORDERSINGNAME);
					String resultFlg = c.getString(DB_ORDER_SING.INDEX_RESULTFLG);
					String itemCode = c.getString(DB_ORDER_SING.INDEX_ITEMCODE);

					if (resultFlg == null || resultFlg.equals("")) {
						strJyoutai = "未検";
					} else {
						switch (resultFlg.toCharArray()[0]) {
						case '0':
							strJyoutai = "OK";
							break;
						case '1':
							strJyoutai = "NG";
							if (iPosition == -1) {
								iPosition = i;
								intSelectItem = i;
							}
							break;
						default:
							// 何もしない
						}
					}

					strItemCodes[i] = itemCode.trim();
					strOrdersignNos[i] = ordersignNo.trim();

					btnText = setBtnText(ordersignNo.trim().substring(2) + ". " + ordersignName.trim(), strJyoutai);
					arr.add(btnText);

					c.moveToNext();
				}

				blList = new ListView(this);
				linearLayout.addView(blList, createParam(FP, FP));

				ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
						getApplicationContext(), R.layout.row_test1, arr);

				blList.setAdapter(arrayAdapter);
				blList.setItemsCanFocus(false);
				blList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				blList.setItemChecked(iPosition, true);
				blList.setOnItemClickListener(new OnItemClickListener() {

					public void onItemClick(AdapterView<?> a, View v,
							int position, long id) {

						intSelectItem = position;
					}
				});

			} else {
				TextView tv = new TextView(this);
				//tv = new TextView(this);
				tv.setId(1);
				tv.setText("データがありません");
				tv.setTextSize(16.0f);
				tv.setTextColor(getResources().getColor(R.color.BLACK));
				linearLayout.addView(tv);
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
	 *
	 * @param group グループ
	 * @param checkedId チェックID
	 */
	public void onCheckedChanged(RadioGroup group, int checkedId) {

	}

	/**
	 * @param id id
	 * @return dialog
	 */
	protected Dialog onCreateDialog(int id) {
		View view = LayoutInflater.from(KensaList.this).inflate(
				R.layout.row_test1, null);
		return new AlertDialog.Builder(KensaList.this).setView(view)
				.setTitle("Share").create();
	}

	/**
	 * ボタンクリックイベント
	 * @param v ボタンIｄ
	 */
	public void onClick(View v) {

		// 二度押し対策
		if (!startProcess()) {
			return;
		}

		int id = v.getId();

		if (id == R.id.BL_BT_CANCEL) {
			// キャンセルボタン
			// 画面終了
			finish();

		} else if (id == R.id.BL_BT_OK) {
			// OKボタン

			if (intSelectItem < 0) {
				// 検査項目未選択の場合、メッセージ表示
				showErrorDialog(Common.MSG_ITEMNAME_HISU);
				endProcess();
			} else {

				String strIdno = "";
				String strLoDate = "";
				String strTireDiv = "";

				// 選択された検査項目の情報を取得
				String sql = "SELECT * FROM P_ordersing "
						+ " WHERE bodyNo='" + strBodyNo + "'"
						+ " AND groupCode='" + strGroupCode + "'"
						+ " AND itemCode='" + strItemCodes[intSelectItem] + "'"
						+ " ORDER BY ordersignNo ";

				Cursor c = null;
				try {
					c = LFA.getmDb().rawQuery(sql, null);

					if (c.moveToFirst()) {
						strIdno = c.getString(DB_ORDER_SING.INDEX_IDNO);
						strLoDate = c.getString(DB_ORDER_SING.INDEX_LODATE);
						strTireDiv = c.getString(DB_ORDER_SING.INDEX_TIREFLG);
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

				// 選択された検査項目の検査画面に遷移
				Intent intent = new Intent();
				intent.putExtra(Common.INTENT_EX_GROUPCOUNT, intSelectItem);
				intent.putExtra(Common.INTENT_EX_CHECKITEMS, strOrdersignNos);
				intent.putExtra(Common.INTENT_EX_BODYNO, strBodyNo);
				intent.putExtra(Common.INTENT_EX_GROUPNO, strGroupCode);
				intent.putExtra(Common.INTENT_EX_IDNO, strIdno);
				intent.putExtra(Common.INTENT_EX_LO_DATE, strLoDate);
				if (strTireDiv.equals("1")) {
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
	 *
	 * @param strName グループ
	 * @param strFlg グループ状態
	 * @return RETURN
	 */
	public String setBtnText(String strName, String strFlg) {
		String strRtn = "";

		// byte [] bytesData = strKokyakuName.getBytes("Shift_JIS");
		try {

			if (strName.getBytes("Shift_JIS").length > 24) {
				strRtn = strRtn + InputCheck.splitBytes(strName, 24, "Shift_JIS");
			} else {
				strRtn = strRtn + InputCheck.padRight(strName, 24, STR_SPACE);
			}

			strRtn = strRtn + InputCheck.padRight("", 2, STR_SPACE);

			if (strFlg.length() > 0) {
				strRtn = strRtn + strFlg;
			} else {
				strRtn = strRtn + InputCheck.padRight("", 4, STR_SPACE);
			}

			//strRtn = strRtn + InputCheck.padRight("", 4, STR_SPACE);

		} catch (Exception e) {
			Log.e(LFA.TAG, e.toString());
		}

		return strRtn;
	}

	/**
	 *  エラーダイアログ
	 * @param str メッセージ
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
	 * 戻るボタン無効
	 * @param keyCode ボタン認識
	 * @param event イベント
	 * @return false
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}
}
