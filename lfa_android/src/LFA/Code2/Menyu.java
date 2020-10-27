package LFA.Code2;

import LFA.Code2.R;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 *
 * @author cj01779
 *
 */
public class Menyu extends BaseActivity implements OnClickListener {
	/**
	 * ボタン定義
	 */
	static final int[] BUTTONS = { R.id.jyuugyouinCode, R.id.KouteiSelect,
			R.id.bodynoInput, R.id.kensaList, R.id.kensaResume, R.id.Cansel, R.id.end, };

	/**
	 * ボタンカウント格納用変数
	 */
	Integer buttonCnt;

	/**
	 * BNO直接入力ボタン定義
	 */
	Button bodynoInput;

	/**
	 * 検査項目一覧ボタン定義
	 */
	Button kensaList;

	/**
	 * 検査中断ボタン定義
	 */
	Button kensaResume;

	/**
	 * カウント定義
	 */
	Boolean count;

	/**
	 * 車両情報の取得有無
	 */
	Boolean IsExistSharyoInfo;

	/**
	 * 検査中、検査完了の有無
	 */
	Boolean IsCheck;

	/**
	 * エラーダイアログ
	 */
	ProgressDialog progressDialog;

	/**
	 *  データベースのファイル名
	 */
	static final String DB = "P_paramlist.db";

	/**
	 * テーブルの名前
	 */
	static final String TABLE = "P_paramlist";

	/**
	 * メモカラムの名前
	 * name
	 */
	static final String NAME = "name";

	/**
	 * メモカラムの値
	 * value
	 */
	static final String VALUE = "value";

	/**
	 * ボデーNO。
	 * どの画面からメニューを表示しても同じ値となるよう、static変数とする
	 */
	private static String strBodyNo = "";

	/**
	 * 工程コード。
	 * どの画面からメニューを表示しても同じ値となるよう、static変数とする
	 */
	private static String strGroupCode = "";

	/**
	 * 工程名称。
	 * どの画面からメニューを表示しても同じ値となるよう、static変数とする
	 */
	private static String strGroupName = "";

	/**
	 * @param strBodyNo セットする strBodyNo
	 */
	public static void setStrBodyNo(String strBodyNo) {
		Menyu.strBodyNo = strBodyNo;
	}

	/**
	 * @param strGroupCode セットする strGroupCode
	 */
	public static void setStrGroupCode(String strGroupCode) {
		Menyu.strGroupCode = strGroupCode;
	}

	/**
	 * @param strGroupName セットする strGroupName
	 */
	public static void setStrGroupName(String strGroupName) {
		Menyu.strGroupName = strGroupName;
	}


	/** Called when the activity is first created.
	 *  @param savedInstanceState 引数
	 * */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.menyu);

		//---------------------------------------------------------------------
		// メニューから従業員変更画面に行って従業員変更を行うと、工程名・ボデー
		// NO、工程コードの情報が失われてしまうため、static変数に格納する。
		//---------------------------------------------------------------------
		// 工程名
		String groupName = getIntent().getStringExtra(Common.INTENT_EX_GROUPNAME);
		if (groupName != null) {
			setStrGroupName(groupName);
		}

		// ボデーNO
		String bodyNo = getIntent().getStringExtra(Common.INTENT_EX_BODYNO);
		if (bodyNo != null) {
			setStrBodyNo(bodyNo);
		}

		// 工程コード
		String groupCode = getIntent().getStringExtra(Common.INTENT_EX_GROUPNO);
		if (groupCode != null) {
			setStrGroupCode(groupCode);
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

		bodynoInput = (Button) findViewById(R.id.bodynoInput);
		kensaList = (Button) findViewById(R.id.kensaList);
		kensaResume = (Button) findViewById(R.id.kensaResume);
		TextView kensaNo = (TextView) findViewById(R.id.kensaNo);
		String strKensu = "";
		strKensu = getIntent().getStringExtra(Common.INTENT_EX_NO);

		if (strKensu != null && !strKensu.equals("")) {
			kensaNo.setText(strKensu);
		} else {
			kensaNo.setText("");
		}

		// ボタン活性・非活性
		if (strGroupName == null || strGroupName.equals("")) {
			bodynoInput.setEnabled(false);
		} else {
			bodynoInput.setEnabled(true);
		}

		if (strGroupCode == null || strGroupCode.equals("")) {
			kensaList.setEnabled(false);
			kensaResume.setEnabled(false);
		} else {
			kensaList.setEnabled(true);
			kensaResume.setEnabled(true);
		}
	}

	/**
	 * ボタンクリックイベント
	 * @param v ボタンイベント
	 */
	public void onClick(final View v) {

		// 二度押し対策
		if (!startProcess()) {
			return;
		}

		// 従業員ｺｰﾄﾞ変更ボタン
		if (v.getId() == R.id.jyuugyouinCode) {
			//---------------------------------------------------------------
			// アプリ継続利用するとパフォーマンス劣化する問題について、
			// 暫定対策として従業員コード変更時にアプリ終了しメモリクリア
			// するようにする
			//---------------------------------------------------------------
			// Intent intent = new Intent(getApplicationContext(), LFA.class);
			// intent.putExtra(Common.INTENT_EX_MENU, "0");
			// startActivityForResult(intent, R.layout.syain_code);
			setResult(RESULT_OK);
			finish();

			// 工程選択ボタン
		} else if (v.getId() == R.id.KouteiSelect) {
			Intent intent = new Intent(getApplicationContext(),
					GroupList_Select.class);
			startActivityForResult(intent, R.layout.group_list_celect);

			// ボデーNO直接入力ボタン
		} else if (v.getId() == R.id.bodynoInput) {
			Intent intent = new Intent(getApplicationContext(),
					BodyNo.class);
			intent.putExtra(Common.INTENT_EX_GROUPNAME, strGroupName);
			intent.putExtra(Common.INTENT_EX_BODYNO, strBodyNo);
			intent.putExtra(Common.INTENT_EX_GROUPNO, strGroupCode);
			startActivityForResult(intent, R.layout.bodyno_input);

			// 検査項目一覧ボタン
		} else if (v.getId() == R.id.kensaList) {
			Intent intent = new Intent(getApplicationContext(),
					KensaList.class);
			intent.putExtra(Common.INTENT_EX_BODYNO, strBodyNo);
			intent.putExtra(Common.INTENT_EX_GROUPNO, strGroupCode);
			intent.putExtra(Common.INTENT_EX_CHECKKARAMODORU, "menu");
			startActivityForResult(intent, R.layout.kensa_list);

			// 検査中断ボタン
		} else if (v.getId() == R.id.kensaResume) {
			Intent intent = new Intent(getApplicationContext(), UpLoad.class);
			intent.putExtra(Common.INTENT_EX_BODYNO, strBodyNo);
			intent.putExtra(Common.INTENT_EX_GROUPNO, strGroupCode);
			intent.putExtra(Common.INTENT_EX_CHECKKARAMODORU, "menu");
			startActivityForResult(intent, R.layout.upload);

			// ｷｬﾝｾﾙボタン
		} else if (v.getId() == R.id.Cansel) {
			finish();

			// 終了ボタン
		} else if (v.getId() == R.id.end) {
			showKakuninDialog(Common.MSG_END_CHECK);
		}

		endProcess();
	}

	/**
	 *  エラーダイアログ
	 * @param str メッセージ内容
	 */
	private void showKakuninDialog(final String str) {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("確認");
		b.setMessage(str);
		b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog,
					final int whichButton) {
				setResult(RESULT_OK);
				finish();
			}
		});
		b.setNegativeButton("No", new DialogInterface.OnClickListener() {

			public void onClick(final DialogInterface dialog,
					final int whichButton) {
				return;
			}
		});
		b.show();
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
	 * 戻るボタン無効
	 * @param keyCode kyeCode
	 * @param event キーイベント
	 * @return false
	 */
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

		}
		return false;

	}
}