/**
 *
 */
package jp.co.ctc.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.util.ResponseUtil;

import jp.co.ctc.entity.FResult;
import jp.co.ctc.entity.MBcsign;
import jp.co.ctc.service.CsvService;
import jp.co.ctc.service.FResultService;
import jp.co.ctc.util.Utils;

/**
 * 以下のURLで接続する。 http://localhost:8400/lfa_inspection/FResultInspect/export
 *
 * @author CJ01615
 *
 */
public class FResultInspectAction {

	/**
	 * CsvServiceクラスを利用します。
	 */
	@Resource
	protected CsvService csvService;
	@Resource
	protected FResultService fresultService;

	public String frameSeq;
	public String bodyNo;
	public String loDateFrom;
	public String loDateTo;
	public String groupName;
	public String inspecResult;
	public String userCode;
	public String inspecDateFrom;
	public String inspecDateTo;

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

		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {
			Date dInspecDateFrom = format.parse(inspecDateFrom);
			Date dInspecDateTo = format.parse(inspecDateTo);

			groupName = groupName.equals("null") ? "" : groupName;
			inspecResult = inspecResult.equals("undefined") ? "" : inspecResult;

			// 検査結果履歴一覧
			List<FResult> list = fresultService.selectFResult(
					frameSeq
					,bodyNo
					,loDateFrom
					,loDateTo
					,groupName
					,inspecResult
					,userCode
					,dInspecDateFrom
					,dInspecDateTo);

			SingletonS2ContainerFactory.init();
			S2Container container = SingletonS2ContainerFactory.getContainer();
			int iInspectMax = (Integer)container.getComponent("inspectMax");
			String CsvFile = (String)container.getComponent("inspectCsvFile");
			String ZeroHD = (String)container.getComponent("inspectCsvFileZeroHD");
			String OverHD = (String)container.getComponent("inspectCsvFileOverHD");

			java.util.Date now = new java.util.Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			String strDate = df.format(now);
			String fileName = CsvFile;
			String csv = "";

			// 件数が最大件数を超えるファイル名
			if (list == null) {
				fileName = OverHD + CsvFile;
				list = new ArrayList<FResult>();

			// 件数が０件のファイル名
			} else if (list.size() == 0) {
				fileName = ZeroHD + CsvFile;
			}
//			// 件数が最大件数を超えるファイル名
//			if (list.size() > iInspectMax) {
//				fileName = OverHD + CsvFile;
//			}

			// ファイル名の日時の置き換え
			fileName = fileName.replaceAll("YYYYMMDDHHMMSS", strDate);

			// CSVデータを返す
			csv = exportCsv(list);
			fileName = new String(fileName.getBytes("Windows-31J"),"ISO-8859-1");
			byte[] bytes = Utils.getBytes(csv, "Windows-31J");
			ResponseUtil.download(fileName, bytes);

			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * CSVデータを返します.
	 * @param lists
	 * @return
	 */
	private String exportCsv(List<FResult> lists) {
		final String crlf = "\r\n";
		final String comma = ",";

		// CSVデータを格納するためのStringBuilder
		StringBuilder sb = new StringBuilder();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String nowStr = sdf.format(new Date());
		sb.append(addDbQu("出力日時") + comma + addDbQu(nowStr) + crlf);

		// 検索条件 start----------------------------------------
		sb.append(addDbQu("検索条件") + crlf);
		sb.append(addDbQu("フレームNo") + comma + addDbQu(frameSeq) + crlf);
		sb.append(addDbQu("ボデーNo") + comma + addDbQu(bodyNo) + crlf);
		sb.append(addDbQu("LO計画日FROM") + comma
				+ addDbQu(loDateFrom.equals("19000101") ? "" : loDateFrom)
				+ crlf);
		sb.append(addDbQu("LO計画日TO") + comma
				+ addDbQu(loDateTo.equals("99991231") ? "" : loDateTo) + crlf);
		sb.append(addDbQu("工程") + comma + addDbQu(groupName) + crlf);
		sb.append(addDbQu("検査結果") + comma + addDbQu(inspecResult) + crlf);
		sb.append(addDbQu("従業員コード") + comma + addDbQu(userCode) + crlf);

		String sInspecDateFrom = inspecDateFrom.substring(0, 16);
		sInspecDateFrom = sInspecDateFrom.equals("1900/01/01 00:00") ? ""
				: sInspecDateFrom;
		sInspecDateFrom = sInspecDateFrom.replaceAll("/", "");
		sInspecDateFrom = sInspecDateFrom.replaceAll(":", "");
		sInspecDateFrom = sInspecDateFrom.replaceAll(" ", "");
		sb.append(addDbQu("検査日時FROM") + comma + addDbQu(sInspecDateFrom) + crlf);

		String sInspecDateTo = inspecDateTo.substring(0, 16);
		sInspecDateTo = sInspecDateTo.equals("9999/12/31 23:59") ? ""
				: sInspecDateTo;
		sInspecDateTo = sInspecDateTo.replaceAll("/", "");
		sInspecDateTo = sInspecDateTo.replaceAll(":", "");
		sInspecDateTo = sInspecDateTo.replaceAll(" ", "");
		sb.append(addDbQu("検査日時TO") + comma + addDbQu(sInspecDateTo) + crlf);
		// 検索条件 end----------------------------------------

		sb.append(addDbQu("検査結果") + crlf);
		String[] strAry = { "フレームNo", "ボデーNo", "LO計画日", "組立連番", "車種区分", "検査工程",
				"検査順序No", "項目CODE", "項目名", "検査回数", "選択回数","正解記号", "マスタ設定値", "入力記号",
				"入力値", "検査結果", "NG理由", "撮影画像", "検査員名", "検査日時" };
		sb.append(Utils.arrayToCsv(strAry));

		// グループマスタのデータ
		for (FResult fresult : lists) {
			sb.append(addDbQu(fresult.fBcdata.frameSeq) + comma);
			sb.append(addDbQu(fresult.bodyNo) + comma);
			sb.append(addDbQu(fresult.loDate) + comma);
			sb.append(addDbQu(fresult.fBcdata.bcnoH0) + comma);
			sb.append(addDbQu(fresult.fBcdata.bctype) + comma);
			sb.append(addDbQu(fresult.mOrder.mGroup.groupName) + comma);
			sb.append(addDbQu(String.valueOf(fresult.mOrder.inspecOrder))
					+ comma);
			sb.append(addDbQu(String.valueOf(fresult.mItem.itemCode)) + comma);
			sb.append(addDbQu(fresult.mItem.itemName) + comma);
			sb.append(addDbQu(String.valueOf(fresult.inspecNo)) + comma);
			// 2016/02/24 DA ins start
			sb.append(addDbQu(String.valueOf(fresult.selectNumber)) + comma);
			// 2016/02/24 DA ins end
			sb.append(addDbQu(fresult.bcSign) + comma);

			String signContents1 = "";
			for (MBcsign mbcsign : fresult.mItem.mBcsignList) {
				if (fresult.bcSign.equals(mbcsign.bcSign)) {
					signContents1 = mbcsign.signContents;
					break;
				}
			}
			sb.append(addDbQu(signContents1) + comma);

			sb.append(addDbQu(fresult.inputData) + comma);

			String signContents2 = "";
			for (MBcsign mbcsign : fresult.mItem.mBcsignList) {
				if (fresult.inputData.equals(mbcsign.bcSign)) {
					signContents2 = mbcsign.signContents;
					break;
				}
			}
			sb.append(addDbQu(signContents2) + comma);

			// 検査結果
			String inspecResultName = "";
			if ("0".equals(fresult.inspecResult)) {
				inspecResultName = "OK";
			} else if ("1".equals(fresult.inspecResult)) {
				inspecResultName = "NG";
			} else if ("2".equals(fresult.inspecResult)) {
				inspecResultName = "ﾀﾞﾐｰ検出";
			} else if ("3".equals(fresult.inspecResult)) {
				inspecResultName = "ﾀﾞﾐｰ見逃し";
			// 2016/02/24 DA ins start
			} else if ("4".equals(fresult.inspecResult)) {
				inspecResultName = "-";
			// 2016/02/24 DA ins end
			}
			sb.append(addDbQu(inspecResultName) + comma);

			// NG理由
			String ngReasonName = "";
			if ("0".equals(fresult.ngReason)) {
				ngReasonName = "誤品";
			} else if ("1".equals(fresult.ngReason)) {
				ngReasonName = "欠品";
			} else if ("2".equals(fresult.ngReason)) {
				ngReasonName = "不要";
			} else if ("3".equals(fresult.ngReason)) {
				ngReasonName = "その他";
			} else if ("5".equals(fresult.ngReason)) {
				ngReasonName = "特設OK";
			}
			sb.append(addDbQu(ngReasonName) + comma);

			// 2017/12/01 DA ins start
			String shotImage = fresult.shotImage;
			if (fresult.shotImage == null) {
				shotImage = "";
			}
			sb.append(addDbQu(shotImage) + comma);
			// 2017/12/01 DA ins end
			sb.append(addDbQu(fresult.mUser.userName) + comma);
			sb.append(addDbQu(sdf.format(fresult.inspecDate)) + crlf);

		}

		return sb.toString();
	}

	/**
	 * 文字列をダブルクォートで囲む
	 * @param s
	 * @return
	 */
	private String addDbQu(String s) {
		final String dbqu = "\"";

		return dbqu + s + dbqu;
	}
}
