/**
 *
 */
package jp.co.ctc.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;

import jp.co.ctc.service.CsvService;
import jp.co.ctc.service.LgVSresultService;
import jp.co.ctc.service.MstSelectService;
import jp.co.ctc.util.Utils;

import org.seasar.struts.annotation.Execute;
import org.seasar.struts.util.ResponseUtil;

/**
 * 以下のURLで接続する。 http://localhost:8400/lfa_inspection/csv/export
 *
 * @author CJ01615
 *
 */
public class CsvAction {

	/**
	 * CsvServiceクラスを利用します。
	 */
	@Resource
	protected CsvService csvService;

	/**
	 * LgFStoreresultServiceクラスを利用します(搬入結果出力用)
	 */
	@Resource
	protected LgVSresultService lgVSresultService;

	/**
	 * マスタメニューからセレクトマスタを受け取ります
	 */
	public int selectMst;
	/**
	 * マスタメニューからloginUserを受け取ります
	 */
	public String loginUser;

	/**
	 * 搬入結果画面から検索対象開始日を受け取ります(搬入結果出力用)
	 */
	public String fromDate;

	/**
	 * 搬入結果画面から検索対象終了日を受け取ります(搬入結果出力用)
	 */
	public String toDate;

	/**
	 * 遷移元のページ名を受け取ります
	 */
	public String fromPage;

	/**
	 * CSVデータを作成し、ダウンロードします。
	 *
	 * @return null
	 */
	@Execute(validator = false)
	public String export() {

		java.util.Date now = new java.util.Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String strDate = df.format(now);
		String fileName = "";
		String csv = "";
		
		// 2016/02/24 DA ins start
		String masterName = MstSelectService.getMasterName(this.selectMst);
		// 2016/02/24 DA ins end

		//LFA検査システムのマスタCSV出力
		if (fromPage.equals("MasterMenu")) {
			// 2016/02/24 DA upd start
			fileName = "PDA_MASTER_" + masterName + "_" + strDate + ".csv";
			// 2016/02/24 DA upd end
			csv = this.csvService.exportCsv(selectMst, loginUser);

		//LFA物流システムのマスタCSV出力
		} else if (fromPage.equals("LgMasterMenu")) {
			fileName = "LFA_LGMASTER_" + strDate + ".csv";
			csv = this.csvService.lgExportCsv(selectMst, loginUser);

		//LFA物流システムの搬入結果CSV出力
		} else if (fromPage.equals("LgResultStore")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			Date stDate = null;
			Date endDate = null;

			try {
				//検索開始日の入力があればDate作成（なければ全件表示用のDate作成）
				if (fromDate != null) {
					stDate = sdf.parse(fromDate);
				} else {
					stDate = sdf.parse("1900/1/1");
				}

				//検索終了日の入力があればDate作成（なければ全件表示用のDate作成）
				if (toDate != null) {
					endDate = sdf.parse(toDate);
				} else {
					endDate = sdf.parse("9999/1/1");
				}

				fileName = "LFA_STORERESULT_" + strDate + ".csv";
				csv = this.lgVSresultService.exportCsv(stDate, endDate);

			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		byte[] bytes = Utils.getBytes(csv, "Windows-31J");
		// 2016/02/24 DA ins start
		fileName = Utils.urlEncode(fileName, "UTF-8");
		// 2016/02/24 DA ins end
		ResponseUtil.download(fileName, bytes);

		return null;
	}
}
