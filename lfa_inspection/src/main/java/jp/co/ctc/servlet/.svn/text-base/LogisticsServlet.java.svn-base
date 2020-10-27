/**
 *
 */
package jp.co.ctc.servlet;

import java.util.List;

import jp.co.ctc.entity.LgMSupplier;
import jp.co.ctc.service.LgMSupplierService;
import jp.co.ctc.util.ServletUtil.Attribute;
import jp.co.ctc.util.ServletUtil.IdList;

import org.seasar.framework.container.SingletonS2Container;

/**
 * @author CJ01786
 *
 */
public class LogisticsServlet extends SuperServlet {


	/**
	 * シリアライズ用
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * アプリケーションファイル格納ディレクトリ名
	 */
	protected static final String APK_DIR = "apk";

	/**
	 * アプリケーションファイル名
	 */
	protected static final String APK_NAME = "ToyotaLFA_UpdateEX.CAB";

	/**
	 * サーブレットの種類
	 */
	protected static final Attribute SERVLET_TYPE = Attribute.logistics;

	/**
	 * コンストラクタ
	 */
	public LogisticsServlet() {
		super();
		this.apkDir = APK_DIR;
		this.apkName = APK_NAME;
		this.servletType = SERVLET_TYPE;
	}

	/**
	 * パラメータによって処理するメソッドを選択し、実行する。
	 * @param id パラメータ
	 * @return 処理によって生成された返信用の値
	 */
	@Override
	protected String selectMethod(IdList id) {
		switch (id) {
		case sps:
			//idがspsの場合

			//sps用のデータ検索・構成処理へ
			return this.responseDownload();

		case upload_assembly:
		case upload_logistics:
			//idがupload_logisticsの場合
			//idがupload_assemblyの場合

			//upload_assembly・upload_logistics用のデータ検索・構築処理へ
			return this.responseUpload();

		case supplier:
			//idがpartsMakerの場合

			//partsMaker用のデータ検索・構築処理へ
			return this.responseMaker();

		default:
			//それ以外のIDの場合

			//親クラスでの判断を行う
			return super.selectMethod(id);

		}
	}

	/* (非 Javadoc)
	 * @see jp.co.ctc.servlet.SuperServlet#responseBody()
	 */
	@Override
	protected String responseBody() {
		LogBody body = new LogBody();
		return body.getResponse(this.inputStream);
	}


	/* (非 Javadoc)
	 * @see jp.co.ctc.servlet.SuperServlet#responseDownload()
	 */
	@Override
	protected String responseDownload() {
		LogSps download = new LogSps();
		boolean checkLock = false;
		if (this.paramGroup == null || this.paramGroup.equals("")) {
			checkLock = true;
		}
		return download.getResponse(this.inputStream, checkLock);
	}

	/* (非 Javadoc)
	 * @see jp.co.ctc.servlet.SuperServlet#responseUpload()
	 */
	@Override
	protected String responseUpload() {

		switch (this.paramId) {
			case upload_assembly:
				LogUploadAssembly uploadAss = new LogUploadAssembly();
				return uploadAss.getResponse(this.inputStream);
			case upload_logistics:
				LogUploadLogistics uploadLog = new LogUploadLogistics();
				return uploadLog.getResponse(this.inputStream);
			default:
				return "NG";
		}
	}


	/**
	 * partsMaker要求に対するレスポンスを生成する。
	 * @return PDAに送るレスポンス
	 */
	protected String responseMaker() {
		LgMSupplierService srvSupplier = SingletonS2Container.getComponent(LgMSupplierService.class);
		List<LgMSupplier> resSupplier = srvSupplier.getMSuppliers();

		this.srvXmlWriter.createDataset();

		for (LgMSupplier mSupplier : resSupplier) {
			this.srvXmlWriter.ceateTable();
			this.srvXmlWriter.addData("supplierCode", mSupplier.supplierCode);
			this.srvXmlWriter.addData("supplierName", mSupplier.supplierName);
		}

		return this.srvXmlWriter.getXMLData();
	}
}