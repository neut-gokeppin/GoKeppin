package LFA.Code2;

/**
 * クリックイベントの制御をする
 *
 * @author DA 2014/04/07
 */
public class ClickEventControl
{
	/** クリックイベントフラグ（true:クリック許可、false:クリック禁止） */
	private boolean isAllow;

	/** クリック制御間隔時間（ミリ秒）*/
	private long interval;

	/** 前回のクリックイベント時間 */
	private long lastTime;

	/**
	 * コンストラクタ
	 */
	ClickEventControl()
	{
		isAllow = true;
		interval = 1000;
		lastTime = 0;
	}

	/**
	 * クリックイベントが禁止かどうか判定する。
	 * @return 判定結果（true:禁止、false:許可）
	 */
	public boolean isClickBan()
	{
		for(long i = 0; i < 100000; i++) {
		}

		boolean isTime = isClickEventTime();
		boolean isFlg = isClickEventFlg();
		if(isTime && isFlg) {
			return false;
		}

		//System.out.println("連続クリックは無視する");
		return true;
	}

	/**
	 * クリックを許可する
	 */
	public void setAllow()
	{
		isAllow = true;
	}

	/**
	 * クリックを禁止する
	 */
	public void setBan()
	{
		isAllow = false;
	}

	/**
	 * クリックイベントが可能かどうか判定する。（フラグ管理）
	 * @return 判定結果（true:可能、false:不可能）
	 */
	private boolean isClickEventFlg()
	{
		return isAllow;
	}

	/**
	 * クリックイベントが可能かどうか判定する。（時間管理）
	 * @return 判定結果（true:可能、false:不可能）
	 */
	private boolean isClickEventTime()
	{
		// 現在時間を取得する
		long time = System.currentTimeMillis();

		// 一定時間経過していない場合はクリックイベントを禁止する
		if(time - lastTime < interval) {
			return false;
		}

		// 一定時間経過したらクリックイベントを許可する
		lastTime = time;

		return true;
	}
}
