package LFA.Code2;

import LFA.Code2.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.view.ViewGroup;


/**
 * Activityの基本クラス
 */
public class BaseActivity extends Activity {

	/**
	 * 処理中フラグ
	 */
	private boolean processing = false;

	/**
	 * ブロードキャストされたインテントを受け取る
	 */
	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			AlertDialog.Builder b = new AlertDialog.Builder(BaseActivity.this);
			String strErrorMsg = intent.getStringExtra(Common.INTENT_EX_MSG);
			b.setTitle("エラー");
			b.setMessage(strErrorMsg);
			b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				// ボタンが押されたらダイアログを閉じる
				public void onClick(final DialogInterface dialog,
						final int which) {
					dialog.dismiss();
				}
			});
			b.show();
			abortBroadcast();
		}
	};

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	};

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(receiver, new IntentFilter(Common.INTENT_BROADCAST));

		// モードによって背景色を変更
		setBackgroundColorByMode();
	}


	/**
	 * 選択されたモードによって背景色を変更
	 */
	protected void setBackgroundColorByMode() {

		// 最上位のViewを取得
		ViewGroup contentRoot = (ViewGroup) findViewById(android.R.id.content);

		// モードによって色を切替
		switch (LFA.mode) {
		case practiceHon:
		// 2016/02/24 DA upd start
		case practiceKari0:
		// 2017/03/02 CT del start
		//case practiceKari1:
		// 2017/03/02 CT del end
		// case practiceKari:
		// 2016/02/24 DA upd end
			contentRoot.setBackgroundColor(getResources().getColor(R.color.DARKBLUE));
			break;
		default:
			contentRoot.setBackgroundColor(getResources().getColor(R.color.BLACK));
			break;
		}
	}


	/**
	 * 処理を開始する。
	 * onClickが2回呼ばれてしまう問題の対策として、
	 * 処理開始時にフラグを立てるようにする。
	 *
	 * @return true:処理中フラグ立て成功、false:処理中フラグ立て失敗
	 */
	protected synchronized boolean startProcess() {
		if (processing) {
			return false;
		}

		processing = true;
		return true;
	}


	/**
	 * 処理中フラグを落とす
	 */
	protected void endProcess() {
		processing = false;
	}

	// 2017/12/01 DA ins start
	/**
	 * 端末の向きが横向きか判定をする。
	 * @return 判定結果（true:横向き、false:縦向き）
	 */
	protected boolean isLandscapeOrientation()
	{
		boolean isResult = false;

		int orientation = getResources().getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			isResult = true;
		}

		return isResult;
	}

	/**
	 * タクトタイム
	 */
	private static long startTime = 0;

	/**
	 * 検査を開始する
	 */
	public static void startTacttime()
	{
		startTime = System.currentTimeMillis();
	}

	/**
	 * 検査の経過時間の取得（秒）
	 */
	public static long getTacttime()
	{
		long currentTime = System.currentTimeMillis();
		long sa = (currentTime - startTime) / 1000;
		return sa;
	}

	/**
	 * 撮影画像をバックグラウンドで取得する
	 */
	protected static ShotimageDownload sidown = new ShotimageDownload();
	// 2017/12/01 DA ins end
}
