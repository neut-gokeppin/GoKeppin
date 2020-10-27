package LFA.Code2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.co.ctc.util.LfaCommon;
import LFA.Code2.R;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * タイヤメーカー判定結果画面
 *
 * @author DA 2014/04/07
 */
public class ResultTire extends BaseActivity implements OnClickListener
{
	/**
	 * ボタン定義
	 */
	static final int[] BUTTONS = { R.id.NEXT };

	/**
	 * 工程名称定義
	 */
	private String strGroupName;

	/**
	 * NextボデーNo定義
	 */
	private String strNextBodyNo;

	/**
	 * エラーメッセージ
	 */
	private String strErrorMessage;

	/**
	 * リストビュー表示
	 */
	private static final int FP = ViewGroup.LayoutParams.FILL_PARENT;

	/**
	 *  クリックイベント制御
	 */
	ClickEventControl cec = new ClickEventControl();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.result_tire);

		ArrayList<HashMap<String, String>> lvKensaList = new ArrayList<HashMap<String, String>>();

		strGroupName = getIntent().getStringExtra(Common.INTENT_EX_GROUPNAME);
		String strBodyNo = getIntent().getStringExtra(Common.INTENT_EX_BODYNO);
		String strVehicleName = getIntent().getStringExtra(Common.INTENT_EX_VEHICLENAME);
		String strLoDate = getIntent().getStringExtra(Common.INTENT_EX_LODATE);
		String strJudgmentList = getIntent().getStringExtra(Common.INTENT_EX_JUDGMENTLIST);
		strNextBodyNo = getIntent().getStringExtra(Common.INTENT_EX_NEXT_BODYNO);
		strErrorMessage = getIntent().getStringExtra(Common.INTENT_EX_ERROR_MESSAGE);

		try {
			// 車種名
			if(strVehicleName != null) {
				TextView tvSyasyuName = (TextView) findViewById(R.id.KS_TV_SYASYUNAME);
				tvSyasyuName.setText(strVehicleName);
			}

			// ボデーNo
			if(strBodyNo != null && strLoDate != null) {
				TextView tvBodyNo = (TextView) findViewById(R.id.KS_TV_BODYNO);
				String outdataTime = "";
				if(strLoDate != null && !strLoDate.equals("")) {
					outdataTime = strLoDate.substring(0, 4) + "/"
							+ strLoDate.substring(4, 6) + "/"
							+ strLoDate.substring(6, 8);
				}
				tvBodyNo.setText("BNO." + strBodyNo + " " + outdataTime);
			}

			// NGリスト
			List<List<String>> list = LfaCommon.convertStringToList(strJudgmentList);

			LinearLayout linearLayout = (LinearLayout) findViewById(R.id.LinearLayout01);
			for(List<String> data : list) {

				//データが無い場合は処理をしない
				if(data.size() < 2) {
					continue;
				}

				HashMap<String, String> ngMap = new HashMap<String, String>();
				String strNgContent = "";

				String strInputData = data.get(Common.INTENT_EX_JUDGMENTLIST_INDEX.INPUTDATA);
				if(strInputData.equals("Others")) {
					strInputData = "";
				}

				String strInspecResult = data.get(Common.INTENT_EX_JUDGMENTLIST_INDEX.INSPECRESULT);
				if(strInspecResult.equals("0")) {
					strNgContent = "OK";
				}
				else {
					String strNgCheck = data.get(Common.INTENT_EX_JUDGMENTLIST_INDEX.NGREASON);
					if(strNgCheck.equals("")) {
						strNgContent = "未検";
					}
					else {
						if(strNgCheck.equals("0")) {
							strNgContent = "誤品";
						}
						else if(strNgCheck.equals("1")) {
							strNgContent = "欠品";
						}
						else if(strNgCheck.equals("2")) {
							strNgContent = "不要";
						}
						else if(strNgCheck.equals("3")) {
							strNgContent = "その他";
						}
					}
				}

				ngMap.put("name", data.get(Common.INTENT_EX_JUDGMENTLIST_INDEX.ITEMNAME));
				ngMap.put("makerName", strInputData);
				ngMap.put("info", strNgContent);
				lvKensaList.add(ngMap);
			}

			SimpleAdapterDisableSelection arrayAdapter = new SimpleAdapterDisableSelection(
					getApplicationContext(), lvKensaList, R.layout.row_test5,
					new String[] { "name", "makerName", "info" },
					new int[] { R.id.name, R.id.makerName, R.id.info });

			ListView blList = new ListView(this);
			blList.setAdapter(arrayAdapter);
			blList.setItemsCanFocus(true);
			blList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			linearLayout.addView(blList, new LinearLayout.LayoutParams(FP, FP));
		}
		catch(Exception e) {
			Log.e(LFA.TAG, e.toString());
		}

		// ボタンを登録
		for(int buttonId : BUTTONS) {
			Button button = (Button) findViewById(buttonId);
			button.setOnClickListener(this);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		}
	}

	@Override
	public void onBackPressed()
	{
		// BACKキー無効
		//super.onBackPressed();
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		cec.setAllow();
	}

	/**
	 * ボタンクリックイベント
	 *
	 * @param v
	 *            ボタンイベント
	 */
	public void onClick(View v)
	{
		if(v.getId() == R.id.NEXT) {

			if(cec.isClickBan()) {
				return;
			}
			cec.setBan();

			if(strErrorMessage.equals("")) {
				nextButtonScreenTransition();
			}
			else {
				Handler handler = new Handler();
				handler.post(new Runnable()
				{
					public void run()
					{
						nextButtonErrorDialog(strErrorMessage);
					}
				});
			}
		}
	}

	/**
	 * 次へボタン押下時のエラーダイアログ処理
	 * @param str エラー内容
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
	 * 次へボタン押下時の画面遷移処理
	 */
	public void nextButtonScreenTransition()
	{
		Intent intent = new Intent(getApplicationContext(), BodyNo.class);
		intent.putExtra(Common.INTENT_EX_GROUPNAME, strGroupName);
		intent.putExtra(Common.INTENT_EX_BODYNO, strNextBodyNo);
		startActivityForResult(intent, R.layout.bodyno_input);
	}

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
