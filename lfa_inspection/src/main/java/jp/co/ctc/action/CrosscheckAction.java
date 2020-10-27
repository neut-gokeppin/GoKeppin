/**
 *
 */
package jp.co.ctc.action;

import java.io.File;
import java.io.FileOutputStream;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import jp.co.ctc.service.MItemService;
import jp.co.ctc.service.MVehicleService;
import jp.co.ctc.service.VBcfmtService;
import jp.co.ctc.service.VConvertRuleDataService;
import jp.co.ctc.util.UniqUtils;
import jp.co.ctc.util.Utils;

import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.upload.S2MultipartRequestHandler;
import org.seasar.struts.util.ActionMessagesUtil;
import org.seasar.struts.util.ResponseUtil;

/**
 * スペックシート×ＰＤＡ　クロスチェック。
 * http://localhost:8400/lfa_inspection/crosscheck
 *
 * @author CJ01615
 *
 */
public class CrosscheckAction {

	/**
	 * 検査項目ファイル
	 */
	@Binding(bindingType = BindingType.NONE)
	public FormFile formFile;

	/**
	 * スペックシートマスタ種類（仮本区分）
	 */
	public String tlcodeSpec = "";

	/**
	 * PDAマスタ種類（仮本区分）
	 */
	public String tlcodePda = "";

	/**
	 * Submitボタン
	 */
	public String submit = "";

	/**
	 * VConvertRuleDataService
	 */
	@Resource
	public VConvertRuleDataService vConvertRuleDataService;

	/**
	 * VBcfmtService
	 */
	@Resource
	public VBcfmtService vBcfmtService;

	/**
	 * MVehicleService
	 */
	@Resource
	public MVehicleService mVehicleService;

	/**
	 * MItemService
	 */
	@Resource
	public MItemService mItemService;

	@Resource
	public HttpServletRequest request;

	@Resource
	public ServletContext application;

	/**
	 * Excelパスワード
	 */
	@Binding("excelPassword")
	public String excelPassword;

	/**
	 * Excelシート名
	 */
	@Binding("excelSheetName")
	public String excelSheetName;

	/**
	 * http://localhost:8400/lfa_inspection/crosscheck
	 * @return
	 */
	@Execute(validator = false)
	public String index() {
		if (UniqUtils.getFlg() == 1) {
			return inspectionTmc();
		}
		else if (UniqUtils.getFlg() == 2) {
			return inspectionHn();
		}
		return null;
	}

	/**
	 * クロスチェック処理（TMC）
	 * @return
	 */
	private String inspectionTmc() {

		if (StringUtils.isEmpty(submit)) {
			return UniqUtils.CROSSCHECK_JSP;
		}

		String csv = "";
		String filename = "";
		if (submit.startsWith("検査項目")) {
			csv = vBcfmtService.crossCheckCsv(tlcodeSpec, tlcodePda);
			filename = "bcfmt_item_crosscheck";
		}
		else if (submit.startsWith("検査指示")) {
			csv = vConvertRuleDataService.crossCheckCsv(tlcodeSpec, tlcodePda);
			filename = "bcconv_bcsign_crosscheck";
		}

		ResponseUtil.download(filename + ".csv", Utils.getBytes(csv, "Windows-31J"));

		return null;
	}

	/**
	 * クロスチェック処理（HN）
	 * @return
	 */
	private String inspectionHn() {

		//アップロードファイルサイズでエラーが発生した場合、例外がスローされているのでチェックする
		SizeLimitExceededException sle = (SizeLimitExceededException) request.getAttribute(S2MultipartRequestHandler.SIZE_EXCEPTION_KEY);
		if (sle != null) {
			ActionMessages errors = new ActionMessages();
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.upload.size"));
			ActionMessagesUtil.addErrors(request, errors);
		}

		if (StringUtils.isEmpty(submit)) {
			return UniqUtils.CROSSCHECK_JSP;
		}

		String path = "";
		try {
			String csv = "";
			String filename = "file_item_crosscheck";

			//デバッグ用に保存する
			try {
				//アップロードしたファイルを一時保存
				path = upload(formFile);
			}
			catch (Exception e) {
			}

			UniqUtils uu = new UniqUtils();
			csv = uu.crossCheckCsv(formFile.getInputStream(), formFile.getFileName(), excelPassword, excelSheetName, tlcodePda, mVehicleService, mItemService);
			if (StringUtils.isBlank(csv)) {
				String result = uu.getErrorContent();
				ActionMessages messages = new ActionMessages();
				messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(result));
				ActionMessagesUtil.addErrors(request, messages);
				return UniqUtils.CROSSCHECK_JSP;
			}

			//デバッグ用に保存する
			try {
				String workpath = application.getRealPath("/work");
				mkDir(workpath);
				String workfile = workpath + "/" + filename + ".csv";
				Utils.outputFile(workfile, csv);
			}
			catch (Exception e) {
			}

			ResponseUtil.download(filename + ".csv", Utils.getBytes(csv, "Windows-31J"));

			return null;
		}
		catch (Exception e) {
			ActionMessages messages = new ActionMessages();
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.crosscheck"));
			if (e.getCause() == null) {
				messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.toString(), false));
			}
			else {
				messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getCause().toString(), false));
			}
			ActionMessagesUtil.addErrors(request, messages);
			e.printStackTrace();
		}
		finally {
			//アップロードした一時ファイルを削除
			if (StringUtils.isNotBlank(path)) {
				try {
					File file = new File(path);
					file.delete();
				}
				catch (Exception e) {
				}
			}
		}

		return UniqUtils.CROSSCHECK_JSP;
	}

	/**
	 * ファイルのアップロード
	 * @param file アップロードファイル
	 * @return アップロードファイルを保存したファイル名
	 */
	private String upload(FormFile file) {

		if (file == null || file.getFileSize() == 0) {
			return "";
		}

		String workpath = application.getRealPath("/work");
		mkDir(workpath);
		String workname = workpath + "/" + file.getFileName();

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(workname);
			fos.write(file.getFileData());
		}
		catch (Exception e) {
			return "";
		}
		finally {
			IOUtils.closeQuietly(fos);
		}

		return workname;
	}

	/**
	 * ディレクトリ作成
	 */
	private void mkDir(String path) {
		try {
			File file = new File(path);
			if (file.exists() == false) {
				file.mkdirs();
			}
		}
		catch (Exception e) {
		}
	}
}
