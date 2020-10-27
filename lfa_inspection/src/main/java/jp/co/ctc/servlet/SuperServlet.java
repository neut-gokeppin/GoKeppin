/**
 *
 */
package jp.co.ctc.servlet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.ctc.entity.LgMTireMaker;
import jp.co.ctc.entity.LgMUser;
import jp.co.ctc.entity.MTiremaker;
import jp.co.ctc.entity.MUser;
import jp.co.ctc.service.LgMTireMakerService;
import jp.co.ctc.service.LgMUserService;
import jp.co.ctc.service.MTiremakerService;
import jp.co.ctc.service.MUserService;
import jp.co.ctc.service.XMLWriteService;
import jp.co.ctc.util.ServletUtil;
import jp.co.ctc.util.ServletUtil.Attribute;
import jp.co.ctc.util.ServletUtil.IdList;

import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.log.Logger;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.FieldUtil;
import org.seasar.framework.util.MethodUtil;

/**
 * @author CJ01786
 *
 */
public abstract class SuperServlet extends HttpServlet {

	/**
	 * シリアライズ用
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * アプリケーション格納先ディレクトリ名
	 */
	protected String apkDir;

	/**
	 * アプリケーションファイル名
	 */
	protected String apkName;

	/**
	 * XMLを読み取るストリーム
	 */
	protected ByteArrayInputStream inputStream;

	/**
	 * idパラメータを格納するid列挙体
	 */
	protected IdList paramId;

	/**
	 * Edittimeパラメータを格納するString変数
	 */
	protected String paramEditdate;

	/**
	 * group/spsパラメータを格納するString変数
	 */
	protected String paramGroup;

	/**
	 * サーブレットの種類を示すAttribute変数
	 */
	protected Attribute servletType;

	/**
	 * XMLを生成するサービスクラスのインスタンス
	 */
	protected XMLWriteService srvXmlWriter;

	/**
	 * ログ出力用のLoggerインスタンス
	 */
	protected Logger logger = Logger.getLogger(SuperServlet.class);

	/**
	 * コンストラクタ
	 */
	public SuperServlet() {
		this.logger = Logger.getLogger(this.getClass());
	}

	/**
	 * 初期化処理。
	 * @throws ServletException サーブレット例外
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		SingletonS2ContainerFactory.init();
	}

	/**
	 * GETメソッドでの処理
	 * @param request HTTPリクエスト
	 * @param response HTTPレスポンス
	 * @throws ServletException サーブレット例外
	 * @throws IOException 入出力例外
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//GETで要求がきた場合、POSTの処理へ投げる。
		this.doPost(request, response);
	}

	/**
	 * POSTメソッドでの処理
	 * @param request HTTPリクエスト
	 * @param response HTTPレスポンス
	 * @throws ServletException サーブレット例外
	 * @throws IOException 入出力例外
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//応答用の設定を行う。
		response.setContentType("text/plain; charset=" + ServletUtil.ENCODING);
		response.setCharacterEncoding(ServletUtil.ENCODING);
		PrintWriter out = response.getWriter();

		//InputStreamの生成
		ServletInputStream sInputStream = request.getInputStream();

		//StreamからXMLを読み込みログ出力し、パーサで使用するストリームを生成する。
		String xml = ServletUtil.inputStreamToString(sInputStream);
		this.logger.info("Get XML from PDA \n" + xml);

		this.inputStream = new ByteArrayInputStream(xml.getBytes());


		this.srvXmlWriter = new XMLWriteService();

		//パラメータを取得する。
		this.paramId = IdList.novalue;	//idパラメータを格納するid列挙体
		this.paramEditdate = "";		//Edittimeパラメータを格納するString変数
		this.paramGroup = "";			//groupパラメータを格納するString変数
		try {
			//受信したidの値を元にid列挙体の設定を行う
			this.paramId  = IdList.valueOf(request.getParameter(ServletUtil.PARAMETER_ID));

			//EditDateを設定する。
			this.paramEditdate = request.getParameter(ServletUtil.PARAMETER_EDITDATE);

			switch (this.servletType) {
				case inspection:
					//groupを設定する。
					this.paramGroup = request.getParameter(ServletUtil.PARAMETER_GROUP);
					break;
				case logistics:
					//spsを設定する。
					this.paramGroup = request.getParameter(ServletUtil.PARAMETER_SPS);
					break;
				default:
					//値なしとする
					this.paramGroup = "";
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			//設定時例外が発生したら値なしとみなす。
			this.paramId  = IdList.novalue;
			this.paramEditdate = "";
			this.paramGroup = "";
		}

		String resString;
		try {
			resString = this.selectMethod(this.paramId);
			out.print(resString);
			this.logger.info("Send XML to PDA \n" + resString);
		} catch (Exception e) {
			e.printStackTrace();

			System.out.println(e.getMessage());
		} finally {
			out.flush();
			out.close();
			inputStream.close();
		}

	}

	/**
	 * パラメータによって処理するメソッドを選択し、実行する。
	 * @param id パラメータ
	 * @return 処理によって生成された返信用の値
	 */
	protected String selectMethod(IdList id) {
		switch (id) {
		case body:
			//idがbodyの場合

			//body用のデータ検索・構成処理へ
			return this.responseBody();

		case userId:
			//idがuserIdの場合

			//userId用のデータ検索・構築処理へ
			return this.responseUserid();

		case EditDate:
			//idがEditDateの場合

			//EditDate用のデータ検索・構築処理へ
			return this.responseEditdate();

		// 2020/08/19 NEUT Start
		case TireMaker:
			//idがTireMakerの場合
			//TireMaker用のデータ検索・構築処理へ
			return this.responseTireMaker();
		// 2020/08/19 NEUT End

		default:
			//それ以外のIDの場合

			return "NG";			//NGを返す。

		}
	}

	/**
	 * body要求に対するレスポンスを生成する。
	 *
	 * @return PDAに送るレスポンス
	 */
	protected abstract String responseBody();

	/**
	 * group・sps要求に対するレスポンスを生成する。
	 *
	 * @return PDAに送るレスポンス
	 */
	protected abstract String responseDownload();

	/**
	 * uplpoad要求に対するレスポンスを生成する。
	 *
	 * @return PDAに送るレスポンス
	 */
	protected abstract String responseUpload();

	// 2020/08/19 NEUT Start
	/**
	 * TireMaker要求に対するレスポンスを生成する。
	 *
	 * @return PDAに送るレスポンス
	 */
	protected String responseTireMaker() {

		Class<?> TireMakerServiceClass;
		Class<?> TireMakerEntityClass;
		switch (this.servletType) {
		case inspection:
			TireMakerServiceClass = MTiremakerService.class;
			TireMakerEntityClass = MTiremaker.class;
			break;

		case logistics:
			TireMakerServiceClass = LgMTireMakerService.class;
			TireMakerEntityClass = LgMTireMaker.class;
			break;

		default:
			return "NG";
		}

		Object srvTireMaker = SingletonS2Container.getComponent(TireMakerServiceClass);
		Method method = ClassUtil.getMethod(TireMakerServiceClass, "getMTireMakers", null);

		List<?> TireMakerList = (List<?>) MethodUtil.invoke(method, srvTireMaker, null);

		this.srvXmlWriter.createDataset();

		for (Object mTireMaker : TireMakerList) {
			this.srvXmlWriter.ceateTable();

			Field field;
			field = ClassUtil.getField(TireMakerEntityClass, "tireMakerAbbreviation");
			String TireMakerAbbreviation = FieldUtil.getString(field, mTireMaker);

			field = ClassUtil.getField(TireMakerEntityClass, "tireMakerName");
			String TireMakerName = FieldUtil.getString(field, mTireMaker);

			this.srvXmlWriter.addData("tireMakerAbbreviation", TireMakerAbbreviation.toUpperCase());
			this.srvXmlWriter.addData("tireMakerName", TireMakerName);
		}

		return this.srvXmlWriter.getXMLData();
	}
	// 2020/08/19 NEUT End			user

	/**
	 * userId要求に対するレスポンスを生成する。
	 *
	 * @return PDAに送るレスポンス
	 */
	protected String responseUserid() {

		Class<?> userServiceClass;
		Class<?> userEntityClass;
		switch (this.servletType) {
		case inspection:
			userServiceClass = MUserService.class;
			userEntityClass = MUser.class;
			break;

		case logistics:
			userServiceClass = LgMUserService.class;
			userEntityClass = LgMUser.class;
			break;

		default:
			return "NG";
		}

		Object srvUser = SingletonS2Container.getComponent(userServiceClass);
		Method method = ClassUtil.getMethod(userServiceClass, "getMUsers", null);

		List<?> userList = (List<?>) MethodUtil.invoke(method, srvUser, null);

		this.srvXmlWriter.createDataset();

		for (Object mUser : userList) {
			this.srvXmlWriter.ceateTable();

			Field field;
			field = ClassUtil.getField(userEntityClass, "userCode");
			String userCode = FieldUtil.getString(field, mUser);

			field = ClassUtil.getField(userEntityClass, "userName");
			String userName = FieldUtil.getString(field, mUser);

			this.srvXmlWriter.addData("userID", userCode.toUpperCase());
			this.srvXmlWriter.addData("userName", userName);
		}

		return this.srvXmlWriter.getXMLData();
	}


	/**
	 * EditDate要求に対するレスポンスを生成する。
	 *
	 * @return PDAに送るレスポンス
	 */
	protected String responseEditdate() {
		String response = "";

		String updateString = this.getFileUpdateTime(this.apkDir, this.apkName);	//返信用の日付文字列
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");	//フォーマットの設定

		try {
			//アプリケーションファイルの更新日時を取得する。
			java.util.Date updateTime = df.parse(updateString);	//基準となる更新日
			java.util.Date minTime = new java.util.Date(updateTime.getTime() - ServletUtil.RANGE_EDITDATE);	//誤差
			java.util.Date maxTime = new java.util.Date(updateTime.getTime() + ServletUtil.RANGE_EDITDATE);	//誤差

			if (this.paramEditdate == null || this.paramEditdate.equals("")) {
				response = updateString;
			} else {
				java.util.Date rcvEditdate = df.parse(this.paramEditdate);	//受信した更新日

				//受信した更新日時とファイルの更新日時を比較する。
				if (minTime.before(rcvEditdate) && maxTime.after(rcvEditdate)) {
					//一致する場合OKを返す
					response = "OK";
				} else {
					//一致しない場合updateStringを返す
					response = updateString;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			response = updateString;
		}

		return response;
	}

	/**
	 * ファイル名からファイルの更新日を取得する。
	 * @param directory ファイルの格納されたディレクトリ名
	 * @param fileName ファイル名
	 * @return ファイルの更新日の文字列
	 */
	protected String getFileUpdateTime(String directory, String fileName) {
		String imgPath = "";
		imgPath += getServletContext().getRealPath(directory);
		imgPath += "/";
		imgPath += fileName;
		File imgFile = new File(imgPath);
		java.util.Date updateTime = new java.util.Date(imgFile.lastModified());
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

		return df.format(updateTime);
	}

}
