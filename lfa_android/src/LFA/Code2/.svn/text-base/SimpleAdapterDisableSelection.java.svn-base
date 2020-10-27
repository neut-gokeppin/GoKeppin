package LFA.Code2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import LFA.Code2.R;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * SimpleAdapterのリスト選択を無効にする
 *
 * @author DA 2014/04/07
 */
public class SimpleAdapterDisableSelection extends SimpleAdapter
{
	/**
	 * コンストラクタ
	 * @param context 表示するView。
	 * @param data マップリスト。Viewに表示するデータ。
	 * @param resource ウィジェットが定義されているレイアウトのリソースID。
	 * @param from マップリストの列名。
	 * @param to Viewに表示するウィジェットのリソースID。formと対。
	 */
	public SimpleAdapterDisableSelection(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to)
	{
		super(context, data, resource, from, to);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View v = super.getView(position, convertView, parent);

		Integer textColor = null;
		Integer backgroundColor = null;

		// NG内容により色を変更する
		Object item = getItem(position);
		if(item instanceof HashMap) {
			HashMap<String, String> hm = (HashMap) item;
			String wk = hm.get("info");
			if(wk.equals("OK")) {
				textColor = Color.BLACK;
				backgroundColor = Color.GREEN;
			}
			else {
				textColor = null; // 今の色をそのまま使用
				backgroundColor = Color.RED;
			}
		}

		setColor(v, null, backgroundColor);

		// 検査項目
		setColor(v.findViewById(R.id.name), textColor, backgroundColor);

		// 測定値
		setColor(v.findViewById(R.id.makerName), textColor, backgroundColor);

		// NG内容
		setColor(v.findViewById(R.id.info), textColor, backgroundColor);

		return v;
	}

	@Override
	public boolean isEnabled(int position)
	{
		// リスト選択を無効
		return false;
	}

	/**
	 * 文字色と背景色を設定
	 * @param v ウィジェット
	 * @param textColor 文字色（null：設定しない）
	 * @param backgroundColor 背景色（null：設定しない）
	 */
	private void setColor(View v, Integer textColor, Integer backgroundColor)
	{
		if(v == null) {
			return;
		}

		// 背景色
		if(backgroundColor != null) {
			v.setBackgroundColor(backgroundColor);
		}

		// 文字色
		if(textColor != null) {
			if(v instanceof TextView) {
				((TextView) v).setTextColor(textColor);
			}
		}
	}
}
