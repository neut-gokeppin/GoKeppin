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
public final class ResizedShotimageCache {
	/**
	 * ディスプレイ幅(画像縮小計算用)
	 *
	 * @author CJ01915
	 * @version labo(2012/02/21)
	 * @since labo(2012/02/21)
	 */
	private static final int DISPLAY_WIDTH = 1280;

	/**
	 * ディスプレイ高さ(画像縮小計算用)
	 *
	 * @author CJ01915
	 * @version labo(2012/02/21)
	 * @since labo(2012/02/21)
	 */
	private static final int DISPLAY_HEIGHT = 960;

	/**
	 * コンストラクタ不要
	 *
	 * @author CJ01915
	 * @version labo(2012/02/21)
	 * @since labo(2012/02/21)
	 */
	private ResizedShotimageCache() {
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
	public static Bitmap getImage(String fileName, long lastModified) {
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
				if (resImage == null || resImage.isRecycled()) {
					Log.d(LFA.TAG, "\tCache is cleard![" + key + "]");
					imageCache.remove(key);
					resImage = getImage(fileName, lastModified);
				}
			}
		// 未取得画像は新たに読み込み追加
		} else {
			resImage = ResizedShotimageCache.loadNewImage(fileName);
			ResizedShotimageCache.setImage(key, resImage);
			resImage = getImage(fileName, lastModified);
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
	 * キャッシュクリア
	 */
	public static void clearImage() {

		// 画像をクリア
		for (HashMap.Entry<String, SoftReference<Bitmap>> entry : imageCache.entrySet()) {

			SoftReference<Bitmap> imageRef = entry.getValue();
			if (imageRef != null) {
				Bitmap resImage = imageRef.get();
				if (resImage != null) {
					resImage.recycle();
				}
			}
		}

		//領域をクリア
		imageCache.clear();
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
		int scaleW = options.outWidth / ResizedShotimageCache.DISPLAY_WIDTH + 1;
		int scaleH = options.outHeight / ResizedShotimageCache.DISPLAY_HEIGHT + 1;
		int scale = Math.max(scaleW, scaleH);
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
}
