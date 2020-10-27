/**
 *
 */
package jp.co.ctc.action;

import java.io.File;
import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;

import jp.co.ctc.service.CsvService;
import jp.co.ctc.service.FBcdataService;
import jp.co.ctc.service.FResultsumService;
import jp.co.ctc.service.LgFTakeresultsumService;

import org.seasar.framework.util.FileUtil;
import org.seasar.framework.util.ResourceUtil;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.util.ResponseUtil;

/**
 * テスト用。 http://localhost:8400/lfa_inspection/fileDownload/index?name=def
 *
 * @author CJ01615
 *
 */
public class FileDownloadAction {

	// @Resource
	// @ActionForm
	// protected FileDownloadForm fileDownloadForm;

	/**
	 * FBcdataServiceクラスを利用します
	 */
	@Resource
	protected FBcdataService fBcdataService;

	/**
	 * CsvServiceクラスを利用します
	 */
	@Resource
	protected CsvService csvService;

	/**
	 * FResultsumServiceクラスを利用します
	 */
	@Resource
	protected FResultsumService fResultsumService;

	/**
	 * LgFTakeresultsumServiceクラスを利用します
	 */
	@Resource
	protected LgFTakeresultsumService lgFTakeresultsumService;

	/** リクエストパラメータで上書き */
	public String name = "abc";

	/**
	 * CSVダウンロード
	 * @return null
	 */
	@Execute(validator = false)
	public String index() {
		String filename = "LFA_MASTER_yyyyMMddHHmmss.csv";

		String csv = this.csvService.exportCsv(0, "CJ00001");

		try {
			byte[] bytes = csv.getBytes("Windows-31J");
			ResponseUtil.download(filename, bytes);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * テスト用
	 * @return null
	 */
	@Execute(validator = false)
	public String index2() {
		System.out.println("name=" + this.name);

		String filename = "sample.png";
		File imgFile = ResourceUtil.getResourceAsFile("const.png");
		ResponseUtil.download(filename, FileUtil.getBytes(imgFile));
		return null;
	}

	/**
	 * テスト用
	 * @return null
	 */
	@Execute(validator = false)
	public String index3() {
		System.out.println("name=" + this.name);

		ResponseUtil.write("test");

		return null;
	}

	/**
	 * F_RESULTSUMテーブルのデータを全件作成しなおす
	 * http://localhost:8400/lfa_inspection/fileDownload/refreshFResultsum
	 * @return null
	 */
	@Execute(validator = false)
	public String refreshFResultsum() {
		fResultsumService.refreshAll();

		ResponseUtil.write("OK");

		return null;
	}

	/**
	 * LG_F_TAKERESULTSUMテーブルのデータを全件作成しなおす
	 * http://localhost:8400/lfa_inspection/fileDownload/refreshLgFTakeresultsum
	 * @return null
	 */
	@Execute(validator = false)
	public String refreshLgFTakeresultsum() {
		lgFTakeresultsumService.refreshAll();

		ResponseUtil.write("OK");

		return null;
	}

	/**
	 * テスト用検査データを作成する
	 * http://localhost:6400/lfa_inspection/fileDownload/createKensaData?name=ﾎﾞﾃﾞｰNO
	 * @return null
	 */
	@Execute(validator = false)
	public String createKensaData() {
		fBcdataService.createKensaDataTest(name);

		ResponseUtil.write("OK");

		return null;
	}
}
