package jp.co.ctc.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.seasar.framework.log.Logger;

/**
 * マスタバージョンを管理する
 *
 * @author DA 2016/02/24
 */
public class MstSelectService
{
	/**
	 * ログ出力用
	 */
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(MstSelectService.class);

	/**
	 * 仮0のマスタバージョン
	 */
	public static final int MSTVER_TEMPORARY0 = 0;

	/**
	 * 仮1のマスタバージョン
	 */
	// 2017/03/02 CT del start
	//public static final int MSTVER_TEMPORARY1 = -1;
	// 2017/03/02 CT del end

	/**
	 * 本番のマスタバージョン
	 * ※実際の本番マスターバージョンは、マスタバージョンのMAX値となります。
	 */
	public static final int MSTVER_REAL = 1;

	/**
	 * マスタバージョン一覧
	 */
	private static final Map<Integer, String> mstList = new HashMap<Integer, String>()
	{
		{
			put(MSTVER_TEMPORARY0, "仮０");
			// 2017/03/02 CT del start
			//put(MSTVER_TEMPORARY1, "仮１");
			// 2017/03/02 CT del end
			put(MSTVER_REAL, "本番");
		}
	};

	/**
	 * コンストラクタ
	 */
	public MstSelectService()
	{
	}

	/**
	 * マスタバージョンの一覧を取得する
	 * @return マスタバージョン一覧
	 */
	public static List<Integer> getMasterCodeList()
	{
		List<Integer> list = new ArrayList<Integer>();
		for (Map.Entry<Integer, String> entry : mstList.entrySet()) {
			Integer key = entry.getKey();
			list.add(key);
		}
		return list;
	}

	/**
	 * マスタバージョンの名称一覧を取得する
	 * @return 名称一覧
	 */
	public static List<String> getMasterList()
	{
		List<String> list = new ArrayList<String>();
		for (Map.Entry<Integer, String> entry : mstList.entrySet()) {
			String val = entry.getValue();
			list.add(val);
		}
		return list;
	}

	/**
	 * 仮マスタバージョン一覧を取得する
	 * @return マスタバージョン一覧
	 */
	public static List<Integer> getTempMasterCodeList()
	{
		List<Integer> list = new ArrayList<Integer>();
		for (Map.Entry<Integer, String> entry : mstList.entrySet()) {
			Integer key = entry.getKey();
			if (key != MSTVER_REAL) {
				list.add(key);
			}
		}
		return list;
	}

	/**
	 * 仮マスタバージョン名称一覧を取得する
	 * @return 名称一覧
	 */
	public static List<String> getTempMasterNameList()
	{
		List<String> list = new ArrayList<String>();
		for (Map.Entry<Integer, String> entry : mstList.entrySet()) {
			Integer key = entry.getKey();
			if (key != MSTVER_REAL) {
				String val = entry.getValue();
				list.add(val);
			}
		}
		return list;
	}

	/**
	 * 名称からマスタバージョンを取得する
	 * @param name マスタバージョン名称
	 * @return マスタバージョン
	 */
	public static Integer getMasterCode(String name)
	{
		Integer key = 0;
		for (Map.Entry<Integer, String> entry : mstList.entrySet()) {
			String val = entry.getValue();
			if (val.equals(name)) {
				key = entry.getKey();
				break;
			}
		}
		return key;
	}

	/**
	 * マスタバージョンから名称を取得する
	 * @param code マスタバージョン
	 * @return マスタバージョン名称
	 */
	public static String getMasterName(Integer code)
	{
		String val = "";
		for (Map.Entry<Integer, String> entry : mstList.entrySet()) {
			Integer key = entry.getKey();
			if (key == code) {
				val = entry.getValue();
				break;
			}
		}
		return val;
	}

	/**
	 * マスタバージョンから名称を取得する
	 * @param code マスタバージョン
	 * @return マスタバージョン名称
	 */
	public static String getMasterVersionName(Integer code)
	{
		String val = "";
		for (Map.Entry<Integer, String> entry : mstList.entrySet()) {
			Integer key = entry.getKey();
			if (key.equals(code)) {
				val = entry.getValue();
				break;
			}
		}
		return val;
	}

	/**
	 * マスタバージョンが仮マスタかどうか判定する
	 * @param obj マスタバージョン or マスタバージョン名称
	 * @return true:仮マスタ、false:仮マスタでない
	 */
	public static Boolean isTemporary(Object obj)
	{
		Integer code = 0;

		if (obj instanceof Integer) {
			code = (Integer) obj;
		}
		else if (obj instanceof String) {
			code = getMasterCode(obj.toString());
		}

		if (code <= 0) {
			return true;
		}
		return false;
	}

	/**
	 * マスタバージョンが本番マスタかどうか判定する
	 * @param obj マスタバージョン or マスタバージョン名称
	 * @return true:本番マスタ、false:本番マスタでない
	 */
	public static Boolean isReal(Object obj)
	{
		Integer code = 0;

		if (obj instanceof Integer) {
			code = (Integer) obj;
		}
		else if (obj instanceof String) {
			code = getMasterCode(obj.toString());
		}

		if (code <= 0) {
			return false;
		}
		return true;
	}
}
