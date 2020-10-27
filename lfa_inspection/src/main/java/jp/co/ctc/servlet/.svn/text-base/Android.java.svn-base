package jp.co.ctc.servlet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.log.Logger;
import org.seasar.struts.util.ServletContextUtil;

import jp.co.ctc.entity.FBcdata;
import jp.co.ctc.entity.FResult;
import jp.co.ctc.entity.FShotimage;
import jp.co.ctc.entity.MGroup;
import jp.co.ctc.entity.MTiremaker;
import jp.co.ctc.entity.MUser;
import jp.co.ctc.entity.RUpload;
import jp.co.ctc.service.FBcdataService;
import jp.co.ctc.service.FBcdataService.JUDGMENT_TIRE_LIST;
import jp.co.ctc.service.FResultService;
import jp.co.ctc.service.FResultsumService;
import jp.co.ctc.service.FShotimageService;
import jp.co.ctc.service.FileMonitoringService;
import jp.co.ctc.service.FileMonitoringService.FILESPLIT;
import jp.co.ctc.service.MGroupService;
import jp.co.ctc.service.MTiremakerService;
import jp.co.ctc.service.MUserService;
import jp.co.ctc.service.RUploadService;
import jp.co.ctc.service.TransactionService;
import jp.co.ctc.service.XMLWriteService;
import jp.co.ctc.util.LfaCommon;
import jp.co.ctc.util.LfaCommon.ModeList;
import jp.co.ctc.util.LfaCommon.TIREMAKER_JUDGMENT;

/**
 * PDAとデータの通信を行うサーブレット
 * @author 12-0214
 *
 */
public class Android extends HttpServlet {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * PDAから受け取るid値の列挙体。
	 *
	 * @author CJ01786
	 */
	// 2020/08/19 NEUT Start
/*	// 2017/12/01 DA upd start
	enum IdList { novalue, userId, body, group, bodyId , upload, kensa, updateTp, check, imageList };
	//// 2016/02/24 DA upd start
	// enum IdList { novalue, userId, body, group, bodyId , upload, kensa, updateTp, check };
	//// enum IdList { novalue, userId, body, group, bodyId , upload, kensa, updateTp };
	//// 2016/02/24 DA upd start
	// 2017/12/01 DA upd end
*/
	enum IdList { novalue, userId, body, group, bodyId , upload, kensa, updateTp, check, imageList, tireMakerId};
	// 2020/08/19 NEUT End
	/**
	 * 使用する文字コード
	 */
	private static final String ENCODING = "UTF-8";

	/**
	 * ログ出力用のLoggerインスタンス
	 */
	private static Logger logger = Logger.getLogger(Android.class);

	/**
	 * ストリームから1度に読み込むバイト数
	 */
	protected static final int READ_BYTES = 128;

	// 2014/04/07 DA ins start
	/**
	 * レスポンスの退避
	 */
	private String xmlResponseThread = "";

	// 2018/08/23 DA del start
	///**
	// * ストリームの退避
	// */
	//private ByteArrayInputStream bArrayInputStreamThread = null;
	// 2018/08/23 DA del end

	// 2014/04/07 DA ins end

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
	public void doGet(
			HttpServletRequest request,
			HttpServletResponse response
			) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * POSTメソッドでの処理
	 * @param request HTTPリクエスト
	 * @param response HTTPレスポンス
	 * @throws ServletException サーブレット例外
	 * @throws IOException 入出力例外
	 */
	public void doPost(
			HttpServletRequest request,
			HttpServletResponse response
			) throws ServletException, IOException {

		//応答用の設定を行う。
		response.setContentType("text/plain; charset=" + ENCODING);
		response.setCharacterEncoding(ENCODING);
		PrintWriter out = response.getWriter();

		// リクエストデータ取得
		String input = IOUtils.toString(request.getInputStream(), "UTF-8");
		logger.info("Get XML from PDA \r\n" + request.getQueryString() + "\r\n" + input);

		// No.41 Edit Itage Watanabe start
		//ByteArrayInputStream bArrayInputStream = new ByteArrayInputStream(strBuf.toString().getBytes());
		final ByteArrayInputStream bArrayInputStream = new ByteArrayInputStream(input.getBytes("UTF-8"));
		// No.41 Edit Itage Watanabe end

		//パラメータを取得する。
		IdList id = IdList.novalue;	//idパラメータを格納するid列挙体
		String koutei = "";
		String body = "";
		String idno = "";
		String loDate = "";
		String flg = "";
		ModeList mode = ModeList.production;

		try {
			//受信したidの値を元にid列挙体の設定を行う
			id = IdList.valueOf(request.getParameter("id"));

			//工程ネームを設定する。
			koutei = request.getParameter("Koutei");
			//ボデーNoを設定する。
			body = request.getParameter("body");
			// 2017/12/01 DA ins start
			//アイデントNoを設定する。
			idno = request.getParameter("idno");
			//ラインオフ計画日を設定する。
			loDate = request.getParameter("loDate");
			//フラグを設定する。
			flg = request.getParameter("flg");
			// 2017/12/01 DA ins end
			//モードを設定する。
			String strMode = request.getParameter("mode");
			if (strMode != null) {
				try {
					mode = ModeList.valueOf(request.getParameter("mode"));
				}
				catch (Exception e) {
					//本番とみなす
					mode = ModeList.production;
				}
			}

		}
		catch (Exception e) {
			//e.printStackTrace();		//許容された例外なのでStacTraceの出力はしない。
			//設定時例外が発生したら値なしとみなす。
			id = IdList.novalue;
			koutei = "";
			body = "";
			idno = "";
			loDate = "";
			flg = "";
			mode = ModeList.production;
		}

		//レスポンスXMLの生成
		try {
			String xmlResponse = "";		//PDAに送るXML文

			switch (id) {
			case userId:
				//idがuserIdの場合

				//userId用のデータ検索・構成処理へ
				xmlResponse = this.responseUserid();
				break;

			case body:
				//idがbodyの場合

				//body用のデータ検索・構成処理へ
				xmlResponse = this.responseBody(request);
				break;

			case group:
				//idがgroupの場合

				//group用のデータ検索・構築処理へ
				xmlResponse = this.responseGroup(koutei, body);
				//Koutei, position
				break;

			case bodyId:

				xmlResponse = this.responseBodyId(koutei, body, mode);

				break;

			case upload:
				//idがuploadのときの処理

				// 2014/04/07 DA upd start
//					//XMLからRGroupエンティティのインスタンスの配列を生成する。
//					RUploadService srvRUpload =
//						new RUploadService();		//配列生成のためのサービスインスタンスを生成
//
//					ArrayList<RUpload> rcvUpload =
//						srvRUpload.readXml(bArrayInputStream);	//配列生成処理を行う。
//
//					//upload用のデータ検索処理・構築処理へ
//					xmlResponse = this.responseUpload(rcvUpload);

				// 2018/08/23 DA del start
				//bArrayInputStreamThread = bArrayInputStream;
				// 2018/08/23 DA del end

				Thread th =
						new Thread(new Runnable()
						{
							public void run()
							{
								//XMLからRGroupエンティティのインスタンスの配列を生成する。
								RUploadService srvRUpload =
										new RUploadService(); //配列生成のためのサービスインスタンスを生成

								ArrayList<RUpload> rcvUpload =
										// 2018/08/23 DA upd start
										//srvRUpload.readXml(bArrayInputStreamThread); //配列生成処理を行う。
										srvRUpload.readXml(bArrayInputStream); //配列生成処理を行う。
										// 2018/08/23 DA upd end

								//upload用のデータ検索処理・構築処理へ
								xmlResponseThread = responseUpload(rcvUpload);
							}
						});
				th.start();
				th.join();
				xmlResponse = xmlResponseThread;
				// 2014/04/07 DA upd end

				break;

			case kensa:
				// idがkensaのときの処理
				xmlResponse = this.responseKensa(request);
				break;

			case updateTp:
				// idがupdatetpのときの処理
				xmlResponse = this.responseUpdateTp(koutei, body);
				break;

			// 2016/02/24 DA ins start
			case check:
				xmlResponse = this.responseCheck(koutei, body);
				break;
			// 2016/02/24 DA ins end

			// 2017/12/01 DA ins start
			case imageList:
				xmlResponse = this.responseImageList(koutei, idno, loDate, flg);
				break;
			// 2017/12/01 DA ins end

			// 2020/08/19 NEUT Start
			case tireMakerId:
				// idがTireMakerのときの処理

				//TireMaker用のデータ検索・構成処理へ
				xmlResponse = this.responseTireMaker();
				break;
			// 2020/08/19 NEUT End
			default:
				//それ以外のIDの場合
				xmlResponse = "NG";			//NGを返す。

				break;
			}

			//レスポンス用のXMLを書き込む
			out.println(xmlResponse);
			//System.out.println(xmlResponse);
			logger.info("Send XML to PDA \r\n" + xmlResponse);

		} catch (Exception e) {
			logger.log(e);
		} finally {
			out.flush();
			out.close();
		}

	}

	/**
	 * 仮マスタ検査用XMLの取得
	 *
	 * http://localhost:6400/lfa_inspection/android?id=kensa&frame_code=186&frame_seq=6034850&group_code=232
	 * http://localhost:6400/lfa_inspection/android?id=kensa&idno=1234567890&lo_date=20141027&group_code=232
	 *
	 * @param request HTTPリクエスト
	 * @return 検査データXML
	 */
	private String responseKensa(HttpServletRequest request) {

		// リクエストパラメータ取得
		String idno = request.getParameter("idno");
		String loDate = request.getParameter("lo_date");
		String groupCode = request.getParameter("group_code");
		// 2016/02/24 DA ins start
		String selectMst = request.getParameter("selectmst");
		// 2016/02/24 DA ins end

		// XML生成処理呼び出し
		FBcdataService srvFbcdata = SingletonS2Container.getComponent(FBcdataService.class);
		// 2016/02/24 DA upd start
		XMLWriteService srvXML = srvFbcdata.createKensaDataKari(idno, loDate, Integer.parseInt(groupCode), Integer.parseInt(selectMst));
		// XMLWriteService srvXML = srvFbcdata.createKensaDataKari(idno, loDate, Integer.parseInt(groupCode));
		// 2016/02/24 DA upd end

		return srvXML.getXMLData();
	}
	// 2020/08/19 NEUT Start
	/**
	 * TireMaker要求に対するレスポンスを生成する。
	 *
	 * @return PDAに送るレスポンス
	 */
	private String responseTireMaker() {

		MTiremakerService srvTireMaker =
				SingletonS2Container.getComponent(MTiremakerService.class);
		List<MTiremaker> resTireMaker;

		resTireMaker = srvTireMaker.getMTiremakers();

		XMLWriteService srvXML = new XMLWriteService();
		srvXML.createDataset();

		for (MTiremaker mTireMaker : resTireMaker) {
			srvXML.ceateTable();
			// tire_maker_abbreviation
			srvXML.addData("tireMakerAbbreviation", mTireMaker.TireMakerAbbreviation.toUpperCase());
			// tire_maker_name
			srvXML.addData("tireMakerName", mTireMaker.TireMakerName);
		}

		return srvXML.getXMLData();

	}
	// 2020/08/19 NEUT End

	/**
	 * userId要求に対するレスポンスを生成する。
	 *
	 * @return PDAに送るレスポンス
	 */
	private String responseUserid() {

		MUserService srvUser =
				SingletonS2Container.getComponent(MUserService.class);
		List<MUser> resUser;

		resUser = srvUser.getMUsers();

		XMLWriteService srvXML = new XMLWriteService();
		srvXML.createDataset();

		for (MUser mUser : resUser) {
			srvXML.ceateTable();

			srvXML.addData("userID", mUser.userCode.toUpperCase());
			srvXML.addData("userName", mUser.userName);
		}

		return srvXML.getXMLData();
	}

	/**
	 * 工程一覧の取得
	 * @param request HTTPリクエスト
	 * @return 工程一覧取得の処理結果
	 */
	private String responseBody(HttpServletRequest request) {

		MGroupService srvGroup =
				SingletonS2Container.getComponent(MGroupService.class);

		XMLWriteService srvXML = new XMLWriteService();
		srvXML.createDataset();

		// 仮マスタか本番マスタかをリクエストパラメータから取得。
		// 指定が無ければ本番マスタ
		String selectMstStr = request.getParameter("selectmst");
		int selectMst = 1;
		if (StringUtils.isNotEmpty(selectMstStr)) {
			selectMst = Integer.parseInt(selectMstStr);
		}
		// 2016/02/24 DA upd start
		LinkedHashSet<String> resMGroup = srvGroup.getMGroupNamesDisplay(selectMst);
		// LinkedHashSet<String> resMGroup = srvGroup.getMGroupNames(selectMst);
		// 2016/02/24 DA upd start

		for (String groupName : resMGroup) {

			srvXML.ceateTable();

			srvXML.addData("groupCode", "");
			srvXML.addData("bcType", "");
			srvXML.addData("groupNo", "");
			srvXML.addData("groupName", groupName);
		}

		return srvXML.getXMLData();
	}

	/**
	 *次処理車両データ取得
	 * @param koutei 工程コード
	 * @param body ボデーNO
	 * @return PDAに送るレスポンス
	 */
	private String responseGroup(String koutei, String body) {

		FBcdataService srvFbcdata =
				SingletonS2Container.getComponent(FBcdataService.class);

		XMLWriteService srvXML = new XMLWriteService();
		srvXML.createDataset();

		List<String[]> nextBodies = srvFbcdata.getNextFBcdataWithResultsum(koutei, body);

		int lenth = 0;
		for (String[] nextBody : nextBodies) {
			int i = 0;
			srvXML.ceateTable();

			srvXML.addData("orderNo", ++lenth);
			srvXML.addData("idno", nextBody[i++].toUpperCase());
			srvXML.addData("loDate", nextBody[i++].toUpperCase());
			srvXML.addData("groupCode", nextBody[i++]);
			// 2016/02/24 DA ins start
			srvXML.addData("bodyNo", nextBody[i++]);
			// 2016/02/24 DA ins end
		}

		return srvXML.getXMLData();
	}

	/**
	 *_車両情報取得
	 * @param koutei 工程コード
	 * @param body ボデーNo
	 * @param mode モード（訓練-本番マスタ, 訓練-仮マスタ, 本番）
	 * @return PDAに送るレスポンス
	 */
	private String responseBodyId(String koutei, String body, ModeList mode) {

		FBcdataService srvFbcdata =
				SingletonS2Container.getComponent(FBcdataService.class);

		XMLWriteService srvXML = new XMLWriteService();
		srvXML.createDataset();

		String[] res = srvFbcdata.getBodyResultsum(koutei, body, mode);
		int i = 0;
		srvXML.ceateTable();
		srvXML.addData("orderNo", 1);
		srvXML.addData("idno", res[i++].toUpperCase());
		srvXML.addData("loDate", res[i++].toUpperCase());
		srvXML.addData("groupCode", res[i++]);

		return srvXML.getXMLData();
	}

	/**
	 * upload要求に対するレスポンスを生成する。
	 * @param rcvUpload 受信したXMLを格納したエンティティのリスト
	 * @return PDAに送るレスポンス
	 */
	// 2014/04/07 DA upd start
	//private String responseUpload(ArrayList<RUpload> rcvUpload) {
	private synchronized String responseUpload(ArrayList<RUpload> rcvUpload) {
		// 2014/04/07 DA upd end
		//String xmlResponse = "OK";	//あらかじめ『OK』を格納しておき、エラー時に『NG』を設定する。

		FBcdataService srvBcdata =
				SingletonS2Container.getComponent(FBcdataService.class);
		FResultService srvResult =
				SingletonS2Container.getComponent(FResultService.class);
		FResultsumService srvResultsum =
				SingletonS2Container.getComponent(FResultsumService.class);
		TransactionService srvTran =
				SingletonS2Container.getComponent(TransactionService.class);
		FShotimageService srvShotimage =
				SingletonS2Container.getComponent(FShotimageService.class);
		MGroupService srvGroup =
				SingletonS2Container.getComponent(MGroupService.class);

		List<FResult> resResult;
		// 2016/02/24 DA ins start
		HashMap<String,Integer> inspecNoMap = new HashMap<String,Integer>();
		// 2016/02/24 DA ins start
		//List<MOrder> resOrder;
		//List<FBcdata> resBcdata;

		boolean ngFlg = false;	//エラー等でNGを返す状態であるか。
		// 2014/04/07 DA ins start
		boolean isUpdate = false;
		List<List<String>> list = new ArrayList<List<String>>();
		// 2014/04/07 DA ins end

		//トランザクションを生成する。
		if (srvTran.startTransaction()) {
			try {
				/*
				 * 検査時間計測厳密化のためミリ秒まで記録するようフォーマット変更
				 *
				 * @author CJ01915
				 * @version labo(2012/04/19)
				 * @since labo(2012/04/19)
				 */
				// SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.S");

				// サマリテーブル（F_RESULTSUM）に登録するグループを保持するためのリスト
				Map<String, RUpload> resultsumMap = new HashMap<String, RUpload>();

				// 2014/04/07 DA ins start
				Integer groupCode = 0;
				String idno = "";
				String loDate = "";
				// 2014/04/07 DA ins end
				String bodyNo = "";

				// 2014/10/27 DA ins start
				//検査日時にシステム日付を使用する
				Date now = new Date();
				Timestamp timestamp = new Timestamp(now.getTime());
				// 2014/10/27 DA ins end

				// f_result.input_dataの最大桁数
				final int inputDataMaxLength = 10;

				// XMLレコードごとに処理
				for (RUpload rUpload : rcvUpload) {
					// 2014/04/07 DA ins start
					groupCode = rUpload.groupCode;
					idno = rUpload.idno;
					loDate = rUpload.loDate;
					// 2014/04/07 DA ins end
					bodyNo = rUpload.bodyNo;

					// 2016/02/24 DA upd start
					if (!rUpload.ordertime.trim().equals("")
							&& !rUpload.userID.trim().equals("PRACTICE")) {
						// 訓練モード（PRACTICE）の検査結果は登録しない

						String inspecNoKey = rUpload.idno + "-" + rUpload.loDate + "-" + rUpload.itemCode;
						Integer inspecNo = inspecNoMap.get(inspecNoKey);
						// 初回
						if (inspecNo == null) {
							// 検査結果取得
							resResult = srvResult.getFResultByKeys(rUpload.idno, rUpload.loDate, rUpload.itemCode);
							if (resResult != null && resResult.size() > 0) {
								// 過去に検査が行われていた場合、最新の検査回数に『1』加算する。
								inspecNo = resResult.get(0).inspecNo + 1;
							}
							else {
								// 検査回数の初期値は未検査時の『1』とする。
								inspecNo = 1;
							}
							inspecNoMap.put(inspecNoKey, inspecNo);
						}
						// if (!rUpload.resultFlg.trim().equals("")
						//		&& !rUpload.ordertime.trim().equals("")
						//		&& !rUpload.userID.trim().equals("PRACTICE")) {
						//	// 検査結果が設定されている場合だけ検査結果を登録する、
						//	// 訓練モード（PRACTICE）の検査結果は登録しない
						// Integer inspecNo = 1; //検査回数の初期値は未検査時の『1』とする。
						// resResult = srvResult.getFResultByKeys(rUpload.idno, rUpload.loDate, rUpload.itemCode);
						// if (resResult != null && resResult.size() > 0) {
						// //過去に検査が行われていた場合、最新の検査回数に『1』加算する。
						// inspecNo = resResult.get(0).inspecNo + 1;
						// }
						// 2016/02/24 DA upd end

						// 2014/10/27 DA del start
						////検査時間をDate型に変換し、さらにtimestamp型に変換する。
						//long time = df.parse(rUpload.ordertime).getTime();
						//Timestamp timestamp = new Timestamp(time);
						// 2014/10/27 DA del end

						//検査結果の値を設定。
						FResult insResult = new FResult();
						insResult.idno = rUpload.idno;
						insResult.loDate = rUpload.loDate;
						insResult.bodyNo = rUpload.bodyNo;
						insResult.recvDay = rUpload.recvDay;
						insResult.mstVer = rUpload.mstVer;
						insResult.itemCode = rUpload.itemCode;
						insResult.inspecNo = inspecNo;
						// 2016/02/24 DA ins start
						insResult.selectNumber = rUpload.selectNumber;
						// 2016/02/24 DA ins end
						insResult.inspecResult = rUpload.resultFlg;
						insResult.ngReason = rUpload.ngContents;
						insResult.inputData = StringUtils.left(rUpload.inputData, inputDataMaxLength);
						insResult.inspecDate = timestamp;
						insResult.inspecUser = rUpload.userID.toUpperCase();
						// 2017/12/01 DA ins start
						insResult.shotImage = srvShotimage.getShotimage(rUpload.idno, rUpload.loDate, rUpload.groupCode, rUpload.itemCode);
						List<String> ifn = srvBcdata.getKensaDataBcSign(rUpload.idno, rUpload.loDate, rUpload.groupCode, rUpload.itemCode);
						insResult.okBcSign = ifn.get(0);
						insResult.ngBcSign = ifn.get(1);
						// 2017/12/01 DA ins end

						//結果を追加する。
						srvResult.create(insResult);

						// 後で検査結果サマリを作成するために検査結果サマリのキーと値を記憶
						String resultsumKey = rUpload.idno + "-" + rUpload.loDate + "-" + rUpload.groupCode.toString();
						resultsumMap.put(resultsumKey, rUpload);
					}
				}

				// 2020/01/22 DA ins start
				// 工程名を取得
				String groupName = "";
				String line = "";
				MGroup group = srvGroup.getMGroupByCode(1, null, groupCode);
				if (group != null) {
					groupName = group.groupName;
					line = group.line;
				}

				boolean isSiTm = LfaCommon.isJudgmentShotimageTiremaker(groupName);

				// タイヤメーカーの判定パターンを決定する
				int tiremakerJudgment = TIREMAKER_JUDGMENT.PATTERN1;
				if (isSiTm) {
					tiremakerJudgment = TIREMAKER_JUDGMENT.PATTERN2;
				}
				// 2020/01/22 DA ins end

				// 2014/04/07 DA ins start
				list = srvBcdata.getJudgmentTireInfo(groupCode, idno, loDate, tiremakerJudgment, false);
				if (list == null) {
					ngFlg = true;
				}
				// 2014/04/07 DA ins end

				// 2020/01/22 DA ins start
				if (isSiTm && ngFlg == false) {
					boolean isCreate = srvBcdata.createTireMakerCooperation(groupCode, idno, loDate, tiremakerJudgment, timestamp, groupName, line, bodyNo);
					if (isCreate == false) {
						ngFlg = true;
					}
				}
				// 2020/01/22 DA ins end

				// サマリレコードごとに処理
				for (RUpload rUpload : resultsumMap.values()) {
					//グループ状態を算出し、サマリテーブル（F_RESULTSUM）に登録
					srvResultsum.setResultsum(rUpload);
					// 検査データ再生成
					srvBcdata.createKensaData(rUpload.idno, rUpload.loDate, rUpload.groupCode, 1);
				}

				if (ngFlg) {
					//NGを返す状況なら、ロールバックしてNGを返す。
					srvTran.rollback();
					// 2014/04/07 DA upd start
					isUpdate = false;
					//return "NG";
					// 2014/04/07 DA upd end
				} else {
					//NGでないならばコミットする。
					if (srvTran.commit()) {
						//コミットに成功すればOKを返す
						// 2014/04/07 DA upd start
						isUpdate = true;
						//return "OK";
						// 2014/04/07 DA upd end
					} else {
						//コミットに失敗すればNGを返す。
						// 2014/04/07 DA upd start
						isUpdate = false;
						//return "NG";
						// 2014/04/07 DA upd end
					}
				}
			} catch (Exception e) {
				logger.log(e);
				srvTran.rollback();
				// 2014/04/07 DA upd start
				isUpdate = false;
				//return "NG";
				// 2014/04/07 DA upd end
			}
		} else {
			//トランザクション生成に失敗したらNGを返す。
			// 2014/04/07 DA upd start
			isUpdate = false;
			//return "NG";
			// 2014/04/07 DA upd end
		}

		// 2014/04/07 DA ins start
		XMLWriteService srvXML = new XMLWriteService();
		srvXML.createDataset();

		srvXML.ceateTable();
		if (isUpdate) {
			if (list.size() == 0) {
				srvXML.addData("result", "OK");
			}
			else {
				int cnt = 0;
				for (List<String> data : list) {
					if (data.get(JUDGMENT_TIRE_LIST.INSPECRESULT).equals("1")) {
						cnt++;
					}
				}
				if (cnt == 0) {
					srvXML.addData("result", "OK");
				}
				else {
					srvXML.addData("result", "TIRE-NG");

					for (int i = 0; i < list.size(); i++) {
						srvXML.ceateTable();
						srvXML.addData("orderNo", i);
						srvXML.addData("itemName", list.get(i).get(JUDGMENT_TIRE_LIST.ITEMNAME));
						srvXML.addData("inputData", list.get(i).get(JUDGMENT_TIRE_LIST.INPUTDATA));
						srvXML.addData("inspecResult", list.get(i).get(JUDGMENT_TIRE_LIST.INSPECRESULT));
						srvXML.addData("ngReason", list.get(i).get(JUDGMENT_TIRE_LIST.NGREASON));
					}
				}
			}
		}
		else {
			srvXML.addData("result", "NG");
		}

		return srvXML.getXMLData();
		// 2014/04/07 DA ins end
	}

	/**
	 * 処理対象のボデーNoの通過日時を更新する。
	 * @param koutei 工程名
	 * @param body ボデーNO
	 * @return PDAに送るレスポンス
	 */
	private String responseUpdateTp(String koutei, String body) {

		FBcdataService srvFbcdata =
				SingletonS2Container.getComponent(FBcdataService.class);

		XMLWriteService srvXML = new XMLWriteService();
		srvXML.createDataset();

		boolean isResult = srvFbcdata.updateTp(koutei, body);

		srvXML.ceateTable();
		if (isResult) {
			srvXML.addData("result", "OK");
		}
		else {
			srvXML.addData("result", "NG");
		}

		return srvXML.getXMLData();
	}

	/**
	 * 処理対象のボデーNoの車両チェックをする。
	 * @param koutei 工程名
	 * @param body ボデーNO
	 * @return PDAに送るレスポンス
	 */
	private String responseCheck(String koutei, String body) {

		// 検査データ有効日数
		SingletonS2ContainerFactory.init();
		S2Container container = SingletonS2ContainerFactory.getContainer();
		int inspectionExpireDays = (Integer)container.getComponent("inspectionExpireDays");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE , - inspectionExpireDays);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String strInspectionExpire = sdf.format(cal.getTime());


		// ラインオフ計画日
		String strLoDate = "";
		// 検査結果有無
		String strExistResult = "";
		// 2016/07/15 DA ins start
		// 検査データのファイルなし（"":未チェック、"true":ファイルなし、"false":ファイルあり）
		String strNoXmlFile = "";
		// 未通過の検査（"":未チェック、"true":未通過、"false":通過済）
		String strNotPassing = "";
		// 有効期限切れの検査（"":未チェック、"true":期限切れ、"false":期限内）
		String strExpired = "";
		// 2016/07/15 DA ins end

		// 車両情報を取得
		FBcdataService srvFbcdata = SingletonS2Container.getComponent(FBcdataService.class);
		FBcdata bcdata = srvFbcdata.selectByBodyNoOrBcno(body, null);

		if (bcdata != null) {

			strLoDate = bcdata.loDate;

			// 工程を取得
			MGroupService mGroupService = SingletonS2Container.getComponent(MGroupService.class);
			MGroup group = mGroupService.getMGroupByName(bcdata.bctype, koutei);

			// 2016/07/15 DA ins start
			// 検査データのXMLファイルチェック
			if (group != null) {
				boolean isRet = srvFbcdata.isKensaData(bcdata, group);
				if (isRet) {
					strNoXmlFile = "false";
				}
				else {
					strNoXmlFile = "true";
				}
			}
			else {
				strNoXmlFile = "true";
			}

			// 塗装吊上げ通過時刻チェック
			if (bcdata.tpN0 == null) {
				strNotPassing = "true";
			}
			else {
				strNotPassing = "false";
			}
			// 2016/07/15 DA ins end

			// 検査結果を取得
			if (group != null) {
				FResultService fResultService = SingletonS2Container.getComponent(FResultService.class);
				List<FResult> fResultList = fResultService.selectByBcdata(bcdata, group);
				if (fResultList.size() != 0) {
					strExistResult = "true";
				}
				else {
					strExistResult = "false";
				}
			}
			else {
				// マスタがない場合は検査結果もないので、検査結果なしとしておく
				strExistResult = "false";
			}

			// 2016/07/15 DA ins start
			// 検査データ有効日数チェック
			if (Integer.valueOf(strLoDate) < Integer.valueOf(strInspectionExpire)) {
				strExpired = "true";

				// 有効期限切れでも検査結果が存在する場合は検査可能にする
				if (strExistResult.equals("true")) {
					strExpired = "false";
				}
			}
			else {
				strExpired = "false";
			}
			// 2016/07/15 DA ins end
		}

		XMLWriteService srvXML = new XMLWriteService();
		srvXML.createDataset();
		srvXML.ceateTable();
		srvXML.addData("lastLoDate", strLoDate);
		srvXML.addData("inspectionExpire", strInspectionExpire);
		srvXML.addData("existResult", strExistResult);
		// 2016/07/15 DA ins start
		srvXML.addData("noXmlFile", strNoXmlFile);
		srvXML.addData("notPassing", strNotPassing);
		srvXML.addData("expired", strExpired);
		// 2016/07/15 DA ins end

		return srvXML.getXMLData();
	}

	// 2017/12/01 DA ins start
	/**
	 * 処理対象の撮影画像ファイルのリストを取得する。
	 * @param koutei 工程名
	 * @param idno アイデントNo
	 * @param loDate ラインオフ計画日
	 * @param flg 繰り返しフラグ（0:繰り返さない、1:繰り返す）
	 * @return PDAに送るレスポンス
	 */
	private String responseImageList(String koutei, String idno, String loDate, String flg)
	{
		// 車両情報を取得
		FBcdataService srvFbcdata = SingletonS2Container.getComponent(FBcdataService.class);
		FBcdata bcdata = srvFbcdata.getSimpleFBcdata(idno, loDate);

		// 工程情報を取得
		MGroup group = null;
		if (bcdata != null) {
			MGroupService mGroupService = SingletonS2Container.getComponent(MGroupService.class);
			group = mGroupService.getMGroupByName(bcdata.bctype, koutei);
		}

		List<FShotimage> siList = null;
		boolean isData = false;
		boolean isLoop = false;

		if (group != null) {

			FShotimageService srvFshotimage = SingletonS2Container.getComponent(FShotimageService.class);

			long startTime = System.currentTimeMillis();
			int sleepMaxTime = 0;

			// 繰り返しの設定
			if (flg.equals("1")) {
				isLoop = true;

				// 監視時間を取得
				int repetitionTime = (Integer) SingletonS2ContainerFactory.getContainer().getComponent("shotimageMonitoring");
				sleepMaxTime = repetitionTime * 1000;
			}

			while (true) {

				siList = srvFshotimage.selectByData(group.groupCode, bcdata.idno, bcdata.loDate);
				if (siList != null && siList.size() != 0) {
					isData = true;
					break;
				}

				if (isLoop) {
					// 繰り返す
					try {
						Thread.sleep(100); // 直ぐ処理に行かないで、気持ち待ってみる。

						long currentTime = System.currentTimeMillis();
						long saTime = currentTime - startTime;
						if (saTime > sleepMaxTime) {
							break;
						}
					}
					catch (InterruptedException e) {
						logger.error("sleep中に例外発生", e);
						break;
					}
				}
				else {
					// 直ぐに終了する
					break;
				}
			}
		}

		XMLWriteService srvXML = new XMLWriteService();
		srvXML.createDataset();

		srvXML.ceateTable();
		if (group == null) {
			// 車両情報が取得できない場合は、異常を返す（ありえないはず）
			srvXML.addData("result", "NG");
		}
		else {
			if (isData) {
				// 取得できた場合は、正常を返す
				srvXML.addData("result", "OK");

				// 撮影画像情報
				for (int i = 0; i < siList.size(); i++) {

					FShotimage siRow = siList.get(i);

					String shotImage = "";
					String shotDate = "";
					if (siRow.fResultList.size() == 0) {
						shotImage = siRow.shotImage;
						shotDate = siRow.shotDate;
					}
					else {
						shotImage = siRow.fResultList.get(0).shotImage;
						if (shotImage == null) {
							shotImage = siRow.shotImage;
							shotDate = siRow.shotDate;
						}
						else {
							String[] siArray = FileMonitoringService.getFileSplit(shotImage);
							if (siArray != null) {
								shotDate = siArray[FILESPLIT.INDEX_SHOT_DATE]; // 撮影日時
							}
						}
					}

					srvXML.ceateTable();

					srvXML.addData("fileName", shotImage);

					long size = 0;
					String fullPath = ServletContextUtil.getServletContext().getRealPath("shotimages/" + shotImage);
					File file = new File(fullPath);
					if (file.exists()) {
						size = file.length();
					}
					srvXML.addData("fileSize", size);

					// 取込時に正しい日付として登録するため日付チェックは不要
					String strdate = "";
					if (shotDate.length() == 14) {
						strdate = shotDate.substring(0, 4) + "/"
								+ shotDate.substring(4, 6) + "/"
								+ shotDate.substring(6, 8) + " "
								+ shotDate.substring(8, 10) + ":"
								+ shotDate.substring(10, 12) + ":"
								+ shotDate.substring(12, 14);
					}
					srvXML.addData("fileDate", strdate);

					srvXML.addData("itemCode", siRow.itemCode);
				}
			}
			else if (isLoop == false) {
				// 取得の繰り返しをしないで、データがない場合は、データ無しの正常を返す
				srvXML.addData("result", "NONE");
			}
			else {
				// 取得の繰り返しをしたが、それでもデータがない場合は、タイムアウトを返す
				srvXML.addData("result", "TIMEOUT");
			}
		}

		// タクトタイム
		int tacttime = (Integer) SingletonS2ContainerFactory.getContainer().getComponent("tacttime");
		srvXML.addData("tacttime", tacttime);

		return srvXML.getXMLData();
	}
	// 2017/12/01 DA ins end
}
