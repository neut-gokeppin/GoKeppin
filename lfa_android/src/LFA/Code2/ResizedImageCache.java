package LFA.Code2;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * イメージをAP側でキャッシュするクラス<br/>
 * メモリ節約のため解像度を落として保持する。
 *
 * @author CJ01915
 * @version labo(2012/02/21)
 * @since labo(2012/02/21)
 */
public final class ResizedImageCache {
	/**
	 * ディスプレイ幅(画像縮小計算用)
	 *
	 * @author CJ01915
	 * @version labo(2012/02/21)
	 * @since labo(2012/02/21)
	 */
	private static final int DISPLAY_WIDTH = 320;
	//private static final int DISPLAY_WIDTH = 720;

	/**
	 * ディスプレイ高さ(画像縮小計算用)
	 *
	 * @author CJ01915
	 * @version labo(2012/02/21)
	 * @since labo(2012/02/21)
	 */
	private static final int DISPLAY_HEIGHT = 480;
	//private static final int DISPLAY_HEIGHT = 1280;

	/**
	 * コンストラクタ不要
	 *
	 * @author CJ01915
	 * @version labo(2012/02/21)
	 * @since labo(2012/02/21)
	 */
	private ResizedImageCache() {
	}

	/**
	 * キャッシュ保持バッファ
	 *
	 * @author CJ01915
	 * @version labo(2012/02/21)
	 * @since labo(2012/02/21)
	 */
	private static HashMap<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();

	/**
	 * イメージ取得
	 *
	 * @param key イメージ取得キー(パス使用)
	 * @return イメージ
	 * @author CJ01915
	 * @version labo(2012/02/21)
	 * @since labo(2012/02/21)
	 */
	// 2016/02/24 DA upd start
	public static Bitmap getImage(String fileName, long lastModified) {
	// public static Bitmap getImage(String key) {
	// 2016/02/24 DA upd end
		String key = fileName + "-" + lastModified;
		Bitmap resImage = null;
		SoftReference<Bitmap> imageRef = null;
		// 取得済み画像はキャッシュから返す
		if (imageCache.containsKey(key)) {
			imageRef = imageCache.get(key);
			if (imageRef != null) {
				resImage = imageRef.get();
				Log.d(LFA.TAG, "Got an image from cache[" + key + "]");
				// キャッシュがガベージされていたら再取得(再起実行)
				if (resImage == null) {
					Log.d(LFA.TAG, "\tCache is cleard![" + key + "]");
					imageCache.remove(key);
					// 2016/02/24 DA upd start
					resImage = getImage(fileName, lastModified);
					// resImage = getImage(key);
					// 2016/02/24 DA upd end
				}
			}
		// 未取得画像は新たに読み込み追加
		} else {
			// 2016/02/24 DA upd start
			resImage = ResizedImageCache.loadNewImage(fileName);
			ResizedImageCache.setImage(key, resImage);
			resImage = getImage(fileName, lastModified);
			// resImage = ResizedImageCache.loadNewImage(key);
			// ResizedImageCache.setImage(key, resImage);
			// resImage = getImage(key);
			// 2016/02/24 DA upd end
		}
		return resImage;
	}

	/**
	 * キャッシュへイメージ追加
	 *
	 * @param key イメージ設定キー(パス使用)
	 * @param image 設定イメージ
	 * @author CJ01915
	 * @version labo(2012/02/21)
	 * @since labo(2012/02/21)
	 */
	public static void setImage(String key, Bitmap image) {
		imageCache.put(key, new SoftReference<Bitmap>(image));
	}

	/**
	 * キャッシュ内イメージ有無
	 *
	 * @param key イメージ取得キー(パス使用)
	 * @return イメージ有無
	 * @author CJ01915
	 * @version labo(2012/02/21)
	 * @since labo(2012/02/21)
	 */
	public static boolean hasImage(String key) {
		return imageCache.containsKey(key);
	}

	/**
	 * キャッシュクリア。
	 *
	 * OutOfMemory発生時に当メソッドを使うことで解消するか検証しましたが、
	 * 逆にOutOfMemory発生頻度が高くなってしまいました。
	 * 従って、OutOfMemory対策としては使用できません。
	 *
	 * @author CJ01915
	 * @version labo(2012/02/21)
	 * @since labo(2012/02/21)
	 */
	public static void clear() {
		imageCache.clear();
	}

	/**
	 * 新規イメージ取得
	 *
	 * @param key イメージ取得キー(パス使用)
	 * @return イメージ
	 * @author CJ01915
	 * @version labo(2012/02/21)
	 * @since labo(2012/02/21)
	 */
	public static Bitmap loadNewImage(String key) {
		// 画像実体の大きさを取得して縮尺を計算
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(key, options);
		int scaleW = options.outWidth / ResizedImageCache.DISPLAY_WIDTH + 1;
		int scaleH = options.outHeight / ResizedImageCache.DISPLAY_HEIGHT + 1;
		int scale = Math.max(scaleW, scaleH);

		// TMC ADD S 2019/01/18 縮尺の比率を2の累乗に切り下げ
		for (int i = 0 ; i < 32 ; i++) {
			// scaleが2のi乗と2のi+1乗の間にあるかチェック
			if ((scale >= Math.pow(2, i) ) && (scale < Math.pow(2, i + 1))) {
			// ある場合scaleにi乗を代入
				scale = (int) Math.pow(2, i);
				// ループを終了
				break;
			}
		}
		// TMC ADD E 2019/01/18

		// 縮尺を適用して画像読み込み実行
		options.inJustDecodeBounds = false;
		options.inSampleSize = scale;
		Bitmap resImage = BitmapFactory.decodeFile(key, options);
		Log.d(LFA.TAG, "Loading a new image[" + key + "]");
		return resImage;
	}

	/**
	 * キャッシュに格納された画像の件数を返します。
	 */
	public static int size() {
		return imageCache.size();
	}

	// 2017/12/01 DA ins start
	/**
	 * 画像かどうか判断します。
	 *
	 * @param fileName ファイル名
	 * @return 判定結果（true:画像、false:画像でない）
	 */
	public static boolean isImage(String fileName) {

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(fileName, options);

		if (options.outWidth == -1 || options.outHeight == -1) {
			// 画像の情報が取得できない場合
			return false;
		}

		return true;
	}
	// 2017/12/01 DA ins end
}
