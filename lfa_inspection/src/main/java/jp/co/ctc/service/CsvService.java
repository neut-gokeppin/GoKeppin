package jp.co.ctc.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.framework.util.FileUtil;
import org.seasar.struts.util.ServletContextUtil;

import jp.co.ctc.entity.LgMBcsign;
import jp.co.ctc.entity.LgMBcsignNoGenId;
import jp.co.ctc.entity.LgMOrder;
import jp.co.ctc.entity.LgMPart;
import jp.co.ctc.entity.LgMPartNoGenId;
import jp.co.ctc.entity.LgMSps;
import jp.co.ctc.entity.LgMSpsNoGenId;
import jp.co.ctc.entity.MBcsign;
import jp.co.ctc.entity.MBcsignNoGenId;
import jp.co.ctc.entity.MGroup;
import jp.co.ctc.entity.MGroupNoGenId;
import jp.co.ctc.entity.MItem;
import jp.co.ctc.entity.MItemNoGenId;
import jp.co.ctc.entity.MOrder;
import jp.co.ctc.util.Utils;

/**
 * CSVを扱うサービスです。
 *
 * @author kaidu
 *
 */
public class CsvService {

	/**
	 * JDBCマネージャです。
	 */
	public JdbcManager jdbcManager;

	/**
	 * MstRegistService
	 */
	@Resource
	public MstRegistService mstRegistService;

	/**
	 * CSVデータを返します。
	 *
	 * @param selectMst
	 *            仮マスタor本番マスタ
	 * @param loginUser
	 *            実行したユーザー
	 * @return CSVデータ
	 */
	public String exportCsv(int selectMst, String loginUser) {

		List<MGroup> groups;
		List<MItem> items;
		List<MBcsign> signs;
		List<MOrder> orders;

		// 仮マスタ選択
		// 2016/02/24 DA upd start
		//if (selectMst == 0) {
		if (MstSelectService.isTemporary(selectMst)) {
			// グループマスタ
			groups = jdbcManager.from(MGroup.class).where("mstVer = " + selectMst)
					.getResultList();

			// 項目マスタ
			items = jdbcManager.from(MItem.class).where("mstVer = " + selectMst)
					.getResultList();

			// 指示記号マスタ
			signs = jdbcManager.from(MBcsign.class).where("mstVer = " + selectMst)
					.getResultList();

			// 検査順マスタ
			orders = jdbcManager.from(MOrder.class).where("mstVer = " + selectMst)
					.getResultList();
		// 2016/02/24 DA upd end
			// 本番マスタ選択
		} else {
			// グループマスタ
			groups = jdbcManager.from(MGroup.class).where("sop_flag = '1'")
					.getResultList();

			// 項目マスタ
			items = jdbcManager.from(MItem.class).where("sop_flag = '1' ")
					.getResultList();

			// 指示記号マスタ
			signs = jdbcManager.from(MBcsign.class).where("sop_flag = '1' ")
					.getResultList();

			// 検査順マスタ
			orders = jdbcManager.from(MOrder.class).where("sop_flag = '1' ")
					.getResultList();
		}

		// CSVデータを格納するためのStringBuilder
		StringBuilder sb = new StringBuilder();

		// 1行目
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String nowStr = sdf.format(new Date());
		sb.append("\"" + loginUser + "\",\"" + nowStr + "\"\r\n");
		sb.append("※このファイルを編集した場合、取り込みはできなくなります。\r\n");

		// *******************グループマスタの書込**********************
		sb.append("\r\n");
		sb.append("\"C\",\"工程マスタ\"\r\n");
		String[] strAry = { "H", "マスタバージョン", "工程Code", "BC車種区分", "工程No.", "工程名称",
				// 2014/04/07 DA ins start
				"ライン", "エリア",
				// 2014/04/07 DA ins end
				// 2016/02/24 DA ins start
				"PDA非表示フラグ",
				// 2016/02/24 DA ins end
				"削除フラグ", "号口フラグ", "作成者", "更新者", "作成日", "更新日" };
		sb.append(Utils.arrayToCsv(strAry));

		// グループマスタのデータ
		for (MGroup group : groups) {
			sb.append("\"G\",");
			sb.append(Utils.arrayToCsv(group.toArray()));
		}

		// ********************検査項目マスタの書込*********************
		sb.append("\r\n");
		sb.append("\"C\",\"検査項目マスタ\"\r\n");
		String[] strAry2 = { "H", "マスタバージョン", "項目Code", "BC車種区分", "項目名称",
				"結果区分", "メッセージ区分", "メッセージNo.", "開始位置", "桁数", "タイヤメーカー", "一択式",
				 "基準上限値", "基準下限値", "削除フラグ", "号口フラグ", "備考",
				 "作成者", "更新者", "作成日", "更新日" };
		sb.append(Utils.arrayToCsv(strAry2));

		// 検査項目マスタのデータ
		for (MItem item : items) {
			sb.append("\"I\",");
			sb.append(Utils.arrayToCsv(item.toArray()));
		}

		// **********************指示記号項目マスタの書込*********************
		sb.append("\r\n");
		sb.append("\"C\",\"指示記号項目マスタ\"\r\n");
		String[] strAry3 = { "H", "マスタバージョン", "指示記号Code", "項目Code", "指示記号",
				"内容", "ダミー記号", "表示順", "画像データファイル名", "基準上限度", "基準下限度",
				// 2016/02/24 DA ins start
				"予約フラグ", "予約者", "予約日", "本番削除フラグ",
				// 2016/02/24 DA ins end
				"削除フラグ", "号口フラグ", "備考", "作成者", "更新者", "作成日", "更新日" };
		sb.append(Utils.arrayToCsv(strAry3));

		// 指示記号項目マスタのデータ
		for (MBcsign sign : signs) {
			// 2016/02/24 DA ins start
			sign.reserveFlag = "0";
			sign.reserveUser = null;
			sign.reserveDate = null;
			// 2016/02/24 DA ins end
			sb.append("\"S\",");
			sb.append(Utils.arrayToCsv(sign.toArray()));
		}

		// **********************検査順マスタの書込****************************
		sb.append("\r\n");
		sb.append("\"C\",\"検査順マスタ\"\r\n");
		String[] strAry4 = { "H", "マスタバージョン", "グループCode", "パターン区分", "検査順",
				"項目Code", "号口フラグ", "作成者", "更新者", "作成日", "更新日" };
		sb.append(Utils.arrayToCsv(strAry4));

		// 検査順マスタのデータ
		for (MOrder order : orders) {
			sb.append("\"O\",");
			sb.append(Utils.arrayToCsv(order.toArray()));
		}
		return sb.toString();
	}

	/**
	 * アップロードされたファイルを受信します。
	 *
	 * @param selectMst
	 *            対象の仮マスタ
	 * @param fileBody
	 *            画像ファイル本体
	 * @return 処理結果（0:正常、以外:異常）
	 * @throws IOException
	 *             IOExceptionを投げる可能性があります
	 */
	public String importCsv(int selectMst, final byte[] fileBody) throws IOException {

		// 2016/02/24 DA ins start
		// 予約中チェック
		if (mstRegistService.isReserved(selectMst)) {
			return "1";
		}
		// 2016/02/24 DA ins end

		// ファイル名設定
		String fileName = "PDA_MASTER_" + fileNameBuild();

		// ファイルの作成
		fileBuild(fileName, fileBody);

		// １行分のデータ(List)を保持するList
		List<MGroupNoGenId> allGroup = new ArrayList<MGroupNoGenId>();
		List<MItemNoGenId> allItem = new ArrayList<MItemNoGenId>();
		List<MBcsignNoGenId> allBcsign = new ArrayList<MBcsignNoGenId>();
		List<MOrder> allOrder = new ArrayList<MOrder>();

		String str = new String(fileBody, "Windows-31J");

		// 改行で分割
		String[] lines = str.split("\r\n");

		// ******************CSVの行数分ループ*************************
		theLoop: for (int i = 0; i < lines.length; i++) {
			String line = lines[i];

			// 改行の行なら以下の処理をスキップする
			if (line.equals("")) {
				continue theLoop;
			}

			// CSV１行を配列に変換
			Object[] item = Utils.csvToArray(line);

			// １つ目の項目で登録先を判断
			// マスタバージョンと号口フラグを０に設定
			// M_Groupへの登録
			if (item[0].toString().equals("G")) {
				MGroupNoGenId gp = new MGroupNoGenId();
				gp = gp.getClass(item);
				// 2016/02/24 DA upd start
				gp.mstVer = selectMst;
				// 2016/02/24 DA upd end
				gp.sopFlag = "0";
				allGroup.add(gp);

				// M_Itemへの登録
			} else if (item[0].toString().equals("I")) {
				MItemNoGenId mi = new MItemNoGenId();
				mi = mi.getClass(item);
				// 2016/02/24 DA upd start
				mi.mstVer = selectMst;
				// 2016/02/24 DA upd end
				mi.sopFlag = "0";
				allItem.add(mi);

				// M_bcSignへの登録
			} else if (item[0].toString().equals("S")) {
				MBcsignNoGenId bs = new MBcsignNoGenId();
				bs = bs.getClass(item);
				// 2016/02/24 DA upd start
				bs.mstVer = selectMst;
				// 2016/02/24 DA upd end
				bs.sopFlag = "0";
				allBcsign.add(bs);

				// M_orderへの登録
			} else if (item[0].toString().equals("O")) {
				MOrder od = new MOrder();
				od = od.getClass(item);
				// 2016/02/24 DA upd start
				od.mstVer = selectMst;
				// 2016/02/24 DA upd end
				od.sopFlag = "0";
				allOrder.add(od);
			}
		}

		// DB検索(DB削除用に取得)
		// グループマスタ
		// 2016/02/24 DA upd start
		List<MGroup> groups = jdbcManager.from(MGroup.class).where(
				"mstVer = " + selectMst).getResultList();
		// 項目マスタ
		List<MItem> items = jdbcManager.from(MItem.class).where(
				"mstVer = " + selectMst).getResultList();
		// 指示記号マスタ
		List<MBcsign> signs = jdbcManager.from(MBcsign.class).where(
				"mstVer = " + selectMst).getResultList();
		// 検査順マスタ
		List<MOrder> orders = jdbcManager.from(MOrder.class).where(
				"mstVer = " + selectMst).getResultList();
		// 2016/02/24 DA upd end

		// 仮テーブルの削除
		deleteBatchList(groups, items, signs, orders);

		// リストに登録
		incertBatchList(allGroup, allItem, allBcsign, allOrder);

		// 2016/02/24 DA ins start
		return "0";
		// 2016/02/24 DA ins end
	}

	/**
	 * アップロードされたファイルを受信します。
	 *
	 * @param fileBody
	 *            画像ファイル本体
	 * @throws IOException
	 *             IOExceptionを投げる可能性があります
	 */
	public void lgImportCsv(final byte[] fileBody) throws IOException {

		// ファイル名設定
		String fileName = "LFA_LGMASTER_" + fileNameBuild();
		// 取込データを保存します。
		fileBuild(fileName, fileBody);

		// １行分のデータ(List)を保持するList
		List<LgMPartNoGenId> allPart = new ArrayList<LgMPartNoGenId>();
		List<LgMBcsignNoGenId> allSign = new ArrayList<LgMBcsignNoGenId>();
		List<LgMOrder> allOrder = new ArrayList<LgMOrder>();
		List<LgMSpsNoGenId> allSps = new ArrayList<LgMSpsNoGenId>();

		String str = new String(fileBody, "Windows-31J");

		// 改行で分割
		String[] lines = str.split("\r\n");

		// ******************CSVの行数分ループ*************************
		theLoop: for (int i = 0; i < lines.length; i++) {
			String line = lines[i];

			// 改行の行なら以下の処理をスキップする
			if (line.equals("")) {
				continue theLoop;
			}

			// CSV１行を配列に変換
			Object[] item = Utils.csvToArray(line);

			// １つ目の項目で登録先を判断
			// マスタバージョンと号口フラグを０に設定
			// 部品マスタへの登録
			if (item[0].toString().equals("P")) {
				LgMPartNoGenId pt = new LgMPartNoGenId();
				pt = pt.getClass(item);
				pt.mstVer = 0;
				pt.sopFlag = "0";
				allPart.add(pt);

				// 指示マスタへの登録
			} else if (item[0].toString().equals("B")) {
				LgMBcsignNoGenId sn = new LgMBcsignNoGenId();
				sn = sn.getClass(item);
				sn.mstVer = 0;
				sn.sopFlag = "0";
				allSign.add(sn);

				// 取出順マスタへの登録
			} else if (item[0].toString().equals("O")) {
				LgMOrder od = new LgMOrder();
				od = od.getClass(item);
				od.mstVer = 0;
				od.sopFlag = "0";
				allOrder.add(od);

				// SPS台車マスタへの登録
			} else if (item[0].toString().equals("S")) {
				LgMSpsNoGenId sp = new LgMSpsNoGenId();
				sp = sp.getClass(item);
				sp.mstVer = 0;
				sp.sopFlag = "0";
				allSps.add(sp);
			}
		}

		// DB検索(DB削除用に取得)
		// 部品マスタ
		List<LgMPart> parts = jdbcManager.from(LgMPart.class).where(
				"mstVer = 0 ").getResultList();
		// 指示マスタ
		List<LgMBcsign> signs = jdbcManager.from(LgMBcsign.class).where(
				"mstVer = 0 ").getResultList();
		// 取出順マスタ
		List<LgMOrder> orders = jdbcManager.from(LgMOrder.class).where(
				"mstVer = 0 ").getResultList();
		// SPS台車マスタ
		List<LgMSps> spses = jdbcManager.from(LgMSps.class)
				.where("mstVer = 0 ").getResultList();

		// 仮テーブルの削除
		deleteBatchList(parts, signs, orders, spses);
		// リストを登録
		incertBatchList(allPart, allSign, allOrder, allSps);

	}

	/**
	 * 仮テーブルを削除します
	 * @param listA 削除対象データ
	 * @param listB 削除対象データ
	 * @param listC 削除対象データ
	 * @param listD 削除対象データ
	 */
	public void deleteBatchList(List<?> listA, List<?> listB, List<?> listC,
			List<?> listD) {

		if (listA.size() > 0) {
			jdbcManager.deleteBatch(listA).execute();
		}
		if (listB.size() > 0){
			jdbcManager.deleteBatch(listB).execute();
		}
		if (listC.size() > 0) {
			jdbcManager.deleteBatch(listC).execute();
		}
		if (listD.size() > 0) {
			jdbcManager.deleteBatch(listD).execute();
		}
	}

	/**
	 * リストを登録します(左：LFA検査、右：LFA物流)
	 *
	 * @param listA
	 *            allGroup or allPart
	 * @param listB
	 *            allItem or allSign
	 * @param listC
	 *            allBcsign or allOrder
	 * @param listD
	 *            allOrder or allSps
	 */
	private void incertBatchList(List<?> listA, List<?> listB, List<?> listC,
			List<?> listD) {

		jdbcManager.insertBatch(listA).execute();
		jdbcManager.insertBatch(listB).execute();
		jdbcManager.insertBatch(listC).execute();
		jdbcManager.insertBatch(listD).execute();
	}

	/**
	 * ファイル名を返します
	 *
	 * @return 日付.csv
	 */
	public String fileNameBuild() {

		java.util.Date now = new java.util.Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
		String dateStr = df.format(now);
		String fileName = dateStr + ".csv";
		return fileName;
	}

	/**
	 * 取込ファイルを保存します
	 *
	 * @param fileName
	 *            ファイル名
	 * @param fileBody
	 *            保存ファイル
	 */
	public void fileBuild(String fileName, final byte[] fileBody) {
		// 格納先パスを決定
		String path = "/images/";
		String fullPath = ServletContextUtil.getServletContext().getRealPath(
				path + fileName);

		// ファイル保存
		FileUtil.write(fullPath, fileBody);
	}

	/**
	 * 物流システムのCSVデータを返します。
	 *
	 * @param selectMst
	 *            仮マスタor本番マスタ
	 * @param loginUser
	 *            実行したユーザー
	 * @return CSVデータ
	 */
	public String lgExportCsv(int selectMst, String loginUser) {

		List<LgMPart> parts;
		List<LgMBcsign> signs;
		List<LgMOrder> orders;
		List<LgMSps> spses;

		// 仮マスタ選択
		if (selectMst == 0) {
			// 部品マスタ
			parts = jdbcManager.from(LgMPart.class).where("mstVer = 0 ")
					.getResultList();

			// 指示マスタ
			signs = jdbcManager.from(LgMBcsign.class).where("mstVer = 0 ")
					.getResultList();

			// 取出順マスタ
			orders = jdbcManager.from(LgMOrder.class).where("mstVer = 0 ")
					.getResultList();

			// Sps台車マスタ
			spses = jdbcManager.from(LgMSps.class).where("mstVer = 0 ")
					.getResultList();
			// 本番マスタ選択
		} else {
			// 部品マスタ
			parts = jdbcManager.from(LgMPart.class).where("sop_flag = '1'")
					.getResultList();

			// 指示マスタ
			signs = jdbcManager.from(LgMBcsign.class).where("sop_flag = '1'")
					.getResultList();

			// 取出順マスタ
			orders = jdbcManager.from(LgMOrder.class).where("sop_flag = '1' ")
					.getResultList();

			// Sps台車マスタ
			spses = jdbcManager.from(LgMSps.class).where("sop_flag = '1' ")
					.getResultList();
		}

		// CSVデータを格納するためのStringBuilder
		StringBuilder sb = new StringBuilder();

		// 1行目
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String nowStr = sdf.format(new Date());
		sb.append("\"" + loginUser + "\",\"" + nowStr + "\"\r\n");

		// *******************部品マスタの書込**********************
		sb.append("\r\n");
		sb.append("\"C\",\"部品マスタ\"\r\n");
//		String[] strAry = { "H", "マスタバージョン", "部品Code", "部品名称", "メッセージ区分",
//				"メッセージNo.", "開始位置", "桁数", "棚照合フラグ", "所番地", "削除フラグ", "号口フラグ",
//				"作成者", "更新者", "作成日", "更新日" };
// "棚照合フラグ", "所番地"を指示マスタへ移動
		String[] strAry = { "H", "マスタバージョン", "部品Code", "部品名称", "メッセージ区分",
		"メッセージNo.", "開始位置", "桁数", "削除フラグ", "号口フラグ",
		"作成者", "更新者", "作成日", "更新日" };
		sb.append(Utils.arrayToCsv(strAry));

		// 部品マスタのデータ
		for (LgMPart part : parts) {
			sb.append("\"P\",");
			sb.append(Utils.arrayToCsv(part.toArray()));
		}

		// ********************指示マスタの書込*********************
		sb.append("\r\n");
		sb.append("\"C\",\"指示マスタ\"\r\n");
		String[] strAry2 = { "H", "マスタバージョン", "指示記号Code", "部品Code", "指示記号",
				"仕入先", "背番号", "品番", "識別", "画像データファイル名", "取出不要フラグ", "削除フラグ", "号口フラグ",
				"作成者", "更新者", "作成日", "更新日", "棚照合フラグ", "所番地",  };
		sb.append(Utils.arrayToCsv(strAry2));

		// 検査項目マスタのデータ
		for (LgMBcsign sign : signs) {
			sb.append("\"B\",");
			sb.append(Utils.arrayToCsv(sign.toArray()));
		}

		// **********************取出順マスタの書込*********************
		sb.append("\r\n");
		sb.append("\"C\",\"取出順マスタ\"\r\n");
		String[] strAry3 = { "H", "マスタバージョン", "SPS台車Code", "取出順", "部品Code",
				"号口フラグ", "作成者", "更新者", "作成日", "更新日" };
		sb.append(Utils.arrayToCsv(strAry3));

		// 取出順マスタのデータ
		for (LgMOrder order : orders) {
			sb.append("\"O\",");
			sb.append(Utils.arrayToCsv(order.toArray()));
		}

		// **********************SPS台車マスタの書込****************************
		sb.append("\r\n");
		sb.append("\"C\",\"SPS台車マスタ\"\r\n");
		String[] strAry4 = { "H", "マスタバージョン", "SPS台車Code", "SPS台車No.",
				"SPS台車名称", "削除フラグ", "号口フラグ", "作成者", "更新者", "作成日", "更新日" };
		sb.append(Utils.arrayToCsv(strAry4));

		// SPS台車マスタのデータ
		for (LgMSps sps : spses) {
			sb.append("\"S\",");
			sb.append(Utils.arrayToCsv(sps.toArray()));
		}
		return sb.toString();
	}

	/**
	 * オブジェクトをStringにキャストします。 値がなければNullを返します
	 *
	 * @param item キャストするオブジェクト
	 * @return 変換後の文字列。値がなければnull。
	 */
	public static String itemCastString(Object item) {
		if (!item.toString().equals("")) {
			return item.toString();
		}
		return null;
	}

	/**
	 * オブジェクトをIntegerにキャストします。 値がなければNullを返します
	 *
	 * @param item キャストするオブジェクト
	 * @return 変換後の数値。値がなければnull
	 */
	public static Integer itemCastInteger(Object item) {
		if (!item.toString().equals("")) {
			return Integer.parseInt(item.toString());
			// return (Integer)item;
		}
		return null;
	}

	/**
	 * オブジェクトをBooleanにキャストします。 値がなければNullを返します
	 *
	 * @param item キャストするオブジェクト
	 * @return 変換後の真理値。値がなければnull
	 */
	public static Boolean itemCastBoolean(Object item) {
		Boolean bool = null;
		if (!item.toString().equals("")) {
			bool = Boolean.parseBoolean(item.toString());
			// return (Boolean)item;
		}
		return bool;
	}

	/**
	 * オブジェクトをTimestampにキャストします。 値がなければNullを返します。
	 *
	 * StringからDate型への変換に、以前はSimpleDateFormat.parseを使っていま
	 * したが、ミリ秒を正しく取り込めない問題(*)があったため、
	 * Timestamp.valueOfを利用するようにしています。
	 *
	 * (*).5秒をSimpleDateFormat.parseを使ってDate型に変換すると、
	 *    .005秒となってしまう。正しくは.500秒
	 *
	 * @param item キャストするオブジェクト。yyyy-mm-dd hh:mm:ss[.f...] 形式のタイムスタンプ。小数点以下の秒数は省略されることがある
	 * @return 変換後のタイムスタンプ。値がなければnull
	 * @see Timestamp#valueOf(String)
	 */
	public static Timestamp itemCastTimestamp(Object item) {

		if (StringUtils.isEmpty(item.toString())) {
			return null;
		}

		return Timestamp.valueOf(item.toString());

	}

}