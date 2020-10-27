package LFA.Code2;

import LFA.Code2.R;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

/**
 * 音再生インスタンス(シングルトンクラス)
 *
 * @author CJ01915
 * @version labo(2012/03/19)
 * @since labo(2012/03/19)
 */
public class SoundPoolPlayer {
	/**
	 * コンテキスト
	 *
	 * @author CJ01915
	 * @version labo(2012/03/19)
	 * @since labo(2012/03/19)
	 */
	private Context parentContext = null;

	/**
	 * 自身のインスタンス
	 *
	 * @author CJ01915
	 * @version labo(2012/03/19)
	 * @since labo(2012/03/19)
	 */
	private static SoundPoolPlayer instance = null;

	/**
	 * サウンドプール
	 *
	 * @author CJ01915
	 * @since 1.0.0.0
	 * @version 1.0.0.0
	 */
	private SoundPool soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);

	/**
	 * OKサウンド再生
	 *
	 * @author CJ01915
	 * @version labo(2012/03/19)
	 * @since labo(2012/03/19)
	 */
	private int okId = -1;

	/**
	 * NGサウンド再生
	 *
	 * @author CJ01915
	 * @version labo(2012/03/19)
	 * @since labo(2012/03/19)
	 */
	private int ngId = -1;

	/**
	 * シングルトンインスタンス取得
	 *
	 * @author CJ01915
	 * @version labo(2012/03/19)
	 * @since labo(2012/03/19)
	 * @return インスタンス
	 */
	public static synchronized SoundPoolPlayer getInstance() {
		if (SoundPoolPlayer.instance == null) {
			SoundPoolPlayer.instance = new SoundPoolPlayer();
		}

		if (SoundPoolPlayer.instance.soundPool == null) {
			// 初期化
			SoundPoolPlayer.instance.soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
			SoundPoolPlayer.instance.okId = -1;
			SoundPoolPlayer.instance.ngId = -1;
		}

		return SoundPoolPlayer.instance;
	}

	/**
	 * サウンド破棄
	 *
	 * @author CJ01915
	 * @version labo(2012/03/19)
	 * @since labo(2012/03/19)
	 */
	public void destroy() {
		if (this.soundPool != null) {
			this.soundPool.release();
			this.soundPool = null;
		}
	}

	/**
	 * サウンド再生準備
	 *
	 * @author CJ01915
	 * @version labo(2012/03/19)
	 * @since labo(2012/03/19)
	 * @param context 画面クラス
	 */
	public void prepare(Context context) {
		this.parentContext = context;
		if (this.okId < 0) {
			try {
				this.okId = this.soundPool.load(context, R.raw.good, 1);
			} catch (Exception e) {
				Log.e(LFA.TAG, "Can not load OK Sound File.[" + e.getLocalizedMessage() + "]");
			}
		}
		if (this.ngId < 0) {
			try {
				this.ngId = this.soundPool.load(context, R.raw.bad, 1);
			} catch (Exception e) {
				Log.e(LFA.TAG, "Can not load NG Sound File.[" + e.getLocalizedMessage() + "]");
			}
		}
	}

	/**
	 * サウンド再生実行
	 *
	 * @author CJ01915
	 * @version labo(2012/03/19)
	 * @since labo(2012/03/19)
	 * @param context 画面クラス
	 * @param isOK OKフラグ
	 */
	public void play(Context context, Boolean isOK) {
		// コンテキストが破棄されていたら再設定
		if (this.parentContext == null) {
			Log.d(LFA.TAG, "Reprepare Sound.");
			this.parentContext = context;
			this.prepare(context);
		}
		int playId = (isOK ? this.okId : this.ngId);
		this.soundPool.play(playId, 1.0F, 1.0F, 0, 0, 1.0F);
	}

}
