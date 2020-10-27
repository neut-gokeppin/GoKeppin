package jp.co.ctc.service;

import java.util.Date;
import java.util.List;

import jp.co.ctc.entity.LgVSresult;
import jp.co.ctc.util.Utils;

import org.seasar.extension.jdbc.JdbcManager;

/**
 * 搬入結果(View)に対して操作を行います。
 * @author CJ01785
 *
 */
public class LgVSresultService {
	/**
	 * JdbcManagerを使います
	 */
	public JdbcManager jdbcManager;

	/**
	 * 搬入結果を取得します。
	 *
	 * @param fromDate
	 *            検索開始日
	 * @param toDate
	 *            検索終了日
	 * @return 検索期間に行われた搬入データ
	 */

	public List<LgVSresult> getVSResult(Date fromDate, Date toDate) {

		Date endDate = new Date();
		// 検索対象終了日に１日加える(そのままではその日の"00:00:00"であるため)
		endDate.setTime(toDate.getTime() + 86400000L);

		return jdbcManager.from(LgVSresult.class).where(
						"partReaddate > ? AND partReaddate < ?", fromDate,
						endDate).orderBy("partReaddate DESC, rackReaddate DESC").getResultList();

	}

	/**
	 * CSV形式のStringを返します。
	 *
	 * @param fromDate
	 *            検索対象開始日
	 * @param toDate
	 *            検索対象終了日
	 * @return CSV形式のString
	 */
	public String exportCsv(Date fromDate, Date toDate) {

		List<LgVSresult> storeList = this.getVSResult(fromDate, toDate);
		// CSVデータを格納するためのStringBuilder
		StringBuilder sb = new StringBuilder();
		sb.append("\r\n");
		String[] strAry = { "品番", "仕入先コード", "仕入先名", "背番号", "所番地",
				"収容数", "搬入者", "かんばん読込日時", "棚読込日時", "仕入先管理事業体", "出荷場", "前工程",
				"納入先", "納入先工区", "受入", "自工程", "支給元", "発注区分", "個別かんばん区分", "納入日",
				"納入便", "MROS", "オーダーNO", "納番", "打切り・設変区分", "納入区分", "箱種", "枝番",
				"ダミー", "バージョンNO" };

		sb.append(Utils.arrayToCsv(strAry));

		for (int i = 0; i < storeList.size(); i++) {
			LgVSresult sr = storeList.get(i);

			String[] partArray = qrCodetoArray(sr.partQrcode,
					sr.supplierName, sr.userName,
					sr.partReaddate, sr.rackReaddate);
			sb.append(Utils.arrayToCsv(partArray));
			}

		return sb.toString();
	}

	/**
	 * QRコードとCSV出力に必要な項目の値を渡し、 分解して配列で返します。
	 *
	 * @param partQRcode
	 *            かんばんQRコード
	 * @param supliName
	 *            仕入先名
	 * @param userName
	 *            搬入者
	 * @param partReaddate
	 *            かんばん読込日時
	 * @param rackReaddate
	 *            棚読込日時
	 * @return CSV出力をおこなう値の配列
	 */
	public String[] qrCodetoArray(String partQRcode, String supliName,
			String userName, Date partReaddate, Date rackReaddate) {

		String[] partArray = {

				partQRcode.substring(64, 69) + "-"
						+ partQRcode.substring(69, 74) + "-"
						+ partQRcode.substring(74, 76), 	// 品番
				partQRcode.substring(2, 6)
				+ partQRcode.substring(6, 7), 				// 仕入先コード
				supliName, 									// 仕入先名
				partQRcode.substring(60, 64), 				// 背番号
				partQRcode.substring(95, 105), 				// 所番地
				partQRcode.substring(76, 81), 				// 収納数
				userName, 									// 搬入者
				partReaddate.toString(), 					// かんばん読込日時
				rackReaddate.toString(), 					// 棚読込日時
				partQRcode.substring(0, 2), 				// 仕入先管理団体
				partQRcode.substring(7, 10), 				// 出荷場
				partQRcode.substring(10, 16), 				// 前工程
				partQRcode.substring(16, 20), 				// 納入先
				partQRcode.substring(20, 21), 				// 納入先工区
				partQRcode.substring(21, 23), 				// 受入
				partQRcode.substring(23, 29), 				// 自工程
				partQRcode.substring(29, 33), 				// 支給元
				partQRcode.substring(33, 34), 				// 発注区分
				partQRcode.substring(34, 35), 				// 個別かんばん区分
				partQRcode.substring(35, 39),				// 納入日
				partQRcode.substring(39, 41), 				// 納入便
				partQRcode.substring(41, 43), 				// MROS
				partQRcode.substring(43, 55), 				// オーダーNO
				partQRcode.substring(55, 60), 				// 納番
				partQRcode.substring(81, 82), 				// 打切・設変区分
				partQRcode.substring(82, 83), 				// 納入区分
				partQRcode.substring(83, 91), 				// 箱種
				partQRcode.substring(91, 95), 				// 枝番
				partQRcode.substring(105, 151), 			// ダミー
				partQRcode.substring(151, 152) 				// バージョンNo
		};
		return partArray;
	}

}
