package jp.co.ctc.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 共通機能
 *<br>　サーバと端末の共通で使用される機能
 *<br>
 * @author DA 2014/04/07
 */
public class LfaCommon
{
	/**
	 * モード
	 * { 訓練-本番マスタ, 訓練-仮マスタ, 本番 }
	 */
	public enum ModeList {
		// 2016/08/12 DA upd start
//		practiceHon, practiceKari, production
		// 2017/03/02 CT upd start
		//practiceHon, practiceKari0, practiceKari1, production
		practiceHon, practiceKari0, production
		// 2017/03/02 CT upd end
		// 2016/08/12 DA upd end
	};

	/** エリア */
	public enum AreaList {
		novalue, area01, area02, area03
	};

	/** 検査結果フラグ */
	public static class RESULTFLG
	{
		/** OK */
		public static final String OK = "0";
		/** NG */
		public static final String NG = "1";
		/** 未検査 */
		public static final String MIKENSA = "";
	}

	/** NG内容 */
	public static class NGCONTENTS
	{
		/** NGでない */
		public static final String NON_NG = "";
		/** 誤品 */
		public static final String GOHIN = "0";
		/** 欠品 */
		public static final String KEPPIN = "1";
		/** 不要 */
		public static final String FUYOU = "2";
		/** その他 */
		public static final String SONOTA = "3";
	}

	/** 測定値リストの各項目の位置 */
	public static class MEASUREMENTLIST
	{
		/** 測定値 */
		public static final int INPUTDATA = 0;
		/** 工程No */
		public static final int GROUPNO = 1;
		/** 検査順 */
		public static final int ORDERSIGNNO = 2;
	}

	/** 測定値集約リストの各項目の位置 */
	public static class AGGREGATELIST
	{
		/** 測定値 */
		public static final int INPUTDATA = 0;
		/** 回数 */
		public static final int COUNT = 1;
		/** 工程No */
		public static final int GROUPNO = 2;
		/** 検査順 */
		public static final int ORDERSIGNNO = 3;
	}

	/** 判定リストの各項目の位置 */
	public static class JUDGMENTLIST
	{
		/** 測定値 */
		public static final int INPUTDATA = 0;
		/** 回数 */
		public static final int COUNT = 1;
		/** 測定結果 */
		public static final int RESULTFLG = 2;
		/** NG内容 */
		public static final int NGCONTENTS = 3;
	}

	/** 選択状態 */
	public static class SELECTSTATUSLIST
	{
		/** 未検査 */
		public static final int UNINSPECTED = 0;
		/** 他 */
		public static final int OTHER = 1;
		/** ダミー選択（不正解） */
		public static final int INCORRECTANSWER = 2;
		/** 選択（正解） */
		public static final int CORRECTANSWER = 3;
	}


	// 2020/01/22 DA ins start
	/**
	 * 検査判定リストの各項目の位置
	 */
	public static class JUDGMENT_TIRE_LIST
	{
		/** 工程No */
		public static final int GROUPNO = 0;
		/** 検査順 */
		public static final int ORDERSIGNNO = 1;
		/** 検査項目 */
		public static final int ITEMNAME = 2;
		/** 測定値 */
		public static final int INPUTDATA = 3;
		/** 回数 */
		public static final int INSPECNO = 4;
		/** 判定結果 */
		public static final int INSPECRESULT = 5;
		/** NG内容 */
		public static final int NGREASON = 6;
		/** 選択状態 */
		public static final int SELECTSTATUSLIST = 7;
	}

	/** タイヤメーカーの判定パターン */
	public static class TIREMAKER_JUDGMENT
	{
		/** 全工程内で、一番多いタイヤメーカーをOK、以外のタイヤメーカーをNGにするパターン */
		public static final int PATTERN1 = 1;
		/** 同じ工程内で、全て同じタイヤメーカーの場合は全てOK、複数タイヤメーカーの場合は全てNGにするパターン */
		public static final int PATTERN2 = 2;
	}
	// 2020/01/22 DA ins end

	/**
	 * タイヤメーカーの選択状態を取得
	 * @param inputData 測定値
	 * @param signContents 検査内容
	 * @return 選択状態（未検査、他、ダミー選択、選択）
	 * @author DA 2014/12/02
	 */
	public static int getTireSelectStatus(String inputData, String signContents) {

		// 選択
		int	selectStatus = SELECTSTATUSLIST.CORRECTANSWER;

		if (inputData == null || inputData.equals("")) {
			// 未検査
			selectStatus = SELECTSTATUSLIST.UNINSPECTED;
		}
		else if (inputData.equals("Others")) {
			// 他
			selectStatus = SELECTSTATUSLIST.OTHER;
		}
		else {
			if (inputData.equals("DMY")) {
				// ダミー選択
				selectStatus = SELECTSTATUSLIST.INCORRECTANSWER;
			}
			else if (signContents != null && signContents.equals("") == false) {
				// 測定値が検査内容に無い時はダミーとする
				String wk = signContents.replace(" ", "");
				wk = "/" + wk + "/";
				if (wk.indexOf("/" + inputData + "/") == -1) {
					// ダミー選択
					selectStatus = SELECTSTATUSLIST.INCORRECTANSWER;
				}
			}
		}

		return selectStatus;
	}

	/**
	 * タイヤメーカーの判定をする
	 * @param measurementList 測定値リスト
	 * @return 判定リスト
	 * @author DA 2014/04/07
	 */
	public static List<List<String>> getTireJudgment(List<List<String>> measurementList)
    {
		return getTireJudgment(measurementList, null, TIREMAKER_JUDGMENT.PATTERN1);
    }

    /**
	 * タイヤメーカーの判定をする
	 * @param measurementList 測定値リスト
	 * @param othersList 選択（正解）以外の検査判定リスト
	 * @param tiremakerJudgment タイヤメーカー判定パターン
	 * @return 判定リスト
	 * @author DA 2020/01/22
	 */
	public static List<List<String>> getTireJudgment(List<List<String>> measurementList, List<List<String>> othersList, int tiremakerJudgment)
	{
		List<List<String>> judgmentList = new ArrayList<List<String>>();

		// 測定値を集約
		List<List<String>> aggregateList = new ArrayList<List<String>>();
		for (List<String> measurementData : measurementList) {

			// 測定値の存在チェック
			int index = -1;
			for (int i = 0; i < aggregateList.size(); i++) {
				List<String> aggregateData = aggregateList.get(i);
				if (aggregateData.get(AGGREGATELIST.INPUTDATA)
						.equals(measurementData.get(MEASUREMENTLIST.INPUTDATA))) {
					index = i;
					break;
				}
			}

			if (index == -1) {
				// 存在しない場合は追加
				List<String> itemList = new ArrayList<String>();
				itemList.add(measurementData.get(MEASUREMENTLIST.INPUTDATA));
				itemList.add("1"); // 回数の初期値
				itemList.add(measurementData.get(MEASUREMENTLIST.GROUPNO));
				itemList.add(measurementData.get(MEASUREMENTLIST.ORDERSIGNNO));
				aggregateList.add(itemList);
			}
			else {
				// 存在する場合はカウントアップ
				List<String> aggregateData = aggregateList.get(index);
				int count = Integer.parseInt(aggregateData.get(AGGREGATELIST.COUNT));
				aggregateData.set(AGGREGATELIST.COUNT, String.valueOf(count + 1));
			}
		}

		// ソート
		Collections.sort(aggregateList, new Comparator<List<String>>()
		{
			public int compare(List<String> o1, List<String> o2)
			{
				// 回数の降順
				int v1 = Integer.parseInt(o1.get(AGGREGATELIST.COUNT));
				int v2 = Integer.parseInt(o2.get(AGGREGATELIST.COUNT));
				int ret = v2 - v1;
				if (ret != 0) {
					return ret;
				}

				// 工程Noの昇順
				v1 = Integer.parseInt(o1.get(AGGREGATELIST.GROUPNO));
				v2 = Integer.parseInt(o2.get(AGGREGATELIST.GROUPNO));
				ret = v1 - v2;
				if (ret != 0) {
					return ret;
				}

				// 検査順の昇順
				v1 = Integer.parseInt(o1.get(AGGREGATELIST.ORDERSIGNNO));
				v2 = Integer.parseInt(o2.get(AGGREGATELIST.ORDERSIGNNO));
				ret = v1 - v2;
				if (ret != 0) {
					return ret;
				}

				return 0;
			}
		});

		// 判定結果の設定
		// 2020/01/22 DA upd start
		if (tiremakerJudgment == TIREMAKER_JUDGMENT.PATTERN1) {

			for (int i = 0; i < aggregateList.size(); i++) {
				List<String> aggregateData = aggregateList.get(i);

				List<String> itemList = new ArrayList<String>();
				itemList.add(aggregateData.get(AGGREGATELIST.INPUTDATA));
				itemList.add(aggregateData.get(AGGREGATELIST.COUNT));
				if (i == 0) {
					// 最初のデータがOK
					itemList.add(RESULTFLG.OK);
					itemList.add(NGCONTENTS.NON_NG);
				}
				else {
					// 2件目以降のデータはNG
					itemList.add(RESULTFLG.NG);
					itemList.add(NGCONTENTS.GOHIN);
				}
				judgmentList.add(itemList);
			}
		}
		else if (tiremakerJudgment == TIREMAKER_JUDGMENT.PATTERN2) {

			for (int i = 0; i < aggregateList.size(); i++) {
				List<String> aggregateData = aggregateList.get(i);

				List<String> itemList = new ArrayList<String>();
				itemList.add(aggregateData.get(AGGREGATELIST.INPUTDATA));
				itemList.add(aggregateData.get(AGGREGATELIST.COUNT));
				if (aggregateList.size() == 1) {

					int cnt = 0;
					for (List<String> othersData : othersList) {
						int selectStatus = Integer.parseInt(othersData.get(JUDGMENT_TIRE_LIST.SELECTSTATUSLIST));
						if (selectStatus != SELECTSTATUSLIST.UNINSPECTED) {
							cnt++;
						}
					}

					// 1件はOK
					if (cnt == 0) {
						itemList.add(RESULTFLG.OK);
						itemList.add(NGCONTENTS.NON_NG);
					}
					else {
						// でもダミーや他があればNG
						itemList.add(RESULTFLG.NG);
						itemList.add(NGCONTENTS.GOHIN);
					}
				}
				else {
					// 複数件はNG
					itemList.add(RESULTFLG.NG);
					itemList.add(NGCONTENTS.GOHIN);
				}
				judgmentList.add(itemList);
			}
		}
		// 2020/01/22 DA upd end

		return judgmentList;
	}

	/**
	 * 文字列からリストにデータ変換する。
	 * <br>文字列データ構成：レコードはカンマ区切り、項目はコロン区切り
	 * <pre>
	 * 参考）
	 *   文字列データ
	 *     aaa:bbb:ccc:ddd,eee:fff:ggg:hhh,...
	 *                ↓変換
	 *   リストデータ
	 * 　　　　　　　 │項目1│項目2│項目3│項目4│
	 * 　　1レコード目│ aaa │ bbb │ ccc │ ddd │
	 * 　　2レコード目│ eee │ fff │ ggg │ hhh │
	 * 　　　　:　　　│  :  │  :  │  :  │  :  │
	 * </pre>
	 * @param data 文字列データ
	 * @return リストデータ
	 * @author DA 2014/04/07
	 */
	public static List<List<String>> convertStringToList(String data)
	{
		List<List<String>> list = new ArrayList<List<String>>();

		// レコード単位分割
		String[] recordArray = data.split(",");
		for (String recordData : recordArray) {
			// 項目単位に分割
			String[] itemArray = recordData.split(":", -1);

			List<String> itemList = new ArrayList<String>();
			for (String itemData : itemArray) {
				itemList.add(itemData);
			}
			list.add(itemList);
		}

		return list;
	}

	/**
	 * 次のボデーNo取得の有無判定をする。
	 * @param groupName 工程名称
	 * @return 判定結果（true:取得する、false:取得しない）
	 * @author DA 2014/10/27
	 */
	public static boolean isJudgmentNextBodyNo(String groupName)
	{
		boolean isResult;

		//デフォルトは取得する
		isResult = true;

		//通過日時記録工程判定
		boolean isJudgment = isJudgmentGroupPassDatetime(groupName);
		if (isJudgment) {
			isResult = false;
		}

		return isResult;
	}

	/**
	 * 通過日時を記録する工程か判定をする。
	 * @param groupName 工程名称
	 * @return 判定結果（true:工程である、false:工程でない）
	 * @author DA 2014/10/27
	 */
	public static boolean isJudgmentGroupPassDatetime(String groupName)
	{
		boolean isResult = false;

		//うさぎ追いでない場合
		if (groupName.endsWith("T__")) {
			isResult = true;
		}
		else {
			//うさぎ追いの場合
			Pattern p = Pattern.compile("TU[0-9]$");
			Matcher m = p.matcher(groupName);
			if (m.find()) {
				isResult = true;
			}
			else {
				//通過日時記録工程ではない
				isResult = false;
			}
		}

		return isResult;
	}

	// 2017/12/01 DA ins start
	/**
	 * 撮影画像の工程か判定をする。
	 * @param groupName 工程名称
	 * @return 判定結果（true:工程である、false:工程でない）
	 * @author DA 2017/12/01
	 */
	public static boolean isJudgmentShotimage(String groupName)
	{
		//工程ではない
		boolean isResult = false;

		//うさぎ追いでない場合
		if (groupName.endsWith("G__")) {
			isResult = true;
		}

		//うさぎ追いの場合
		if (isResult == false) {
			Pattern p = Pattern.compile("GU[0-9]$");
			Matcher m = p.matcher(groupName);
			if (m.find()) {
				isResult = true;
			}
		}

		// 2020/01/22 DA ins start
		// タイヤメーカー検査結果連携の場合
		if (isResult == false) {
			Pattern p = Pattern.compile("GT[0-9]$");
			Matcher m = p.matcher(groupName);
			if (m.find()) {
				isResult = true;
			}
		}
		// 2020/01/22 DA ins end

		return isResult;
	}
	// 2017/12/01 DA ins end

	// 2020/01/22 DA ins start
	/**
	 * タイヤメーカー検査結果連携の工程か判定をする。
	 * @param groupName 工程名称
	 * @return 判定結果（true:工程である、false:工程でない）
	 * @author DA 2020/01/22
	 */
	public static boolean isJudgmentShotimageTiremaker(String groupName)
	{
		boolean isResult = false;

		Pattern p = Pattern.compile("GT[0-9]$");
		Matcher m = p.matcher(groupName);
		if (m.find()) {
			isResult = true;
		}
		else {
			//工程ではない
			isResult = false;
		}

		return isResult;
	}
	// 2020/01/22 DA ins end

	/**
	 * エリアから対象のエリアを取得する。
	 * @param area エリア
	 * @return エリア
	 * @author DA 2014/10/27
	 */
	public static AreaList getArea(String area)
	{
		if (area.startsWith("01")) {
			//艤装
			return AreaList.area01;
		}
		else if (area.startsWith("02")) {
			//セールス
			return AreaList.area02;
		}
		else if (area.startsWith("03")) {
			//拡張
			return AreaList.area03;
		}
		return AreaList.novalue;
	}
}
