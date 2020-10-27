/**
 *
 */
package jp.co.ctc.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.annotation.Resource;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.ctc.servlet.CreateResponse;
import jp.co.ctc.servlet.LogBody;
import jp.co.ctc.servlet.LogEditdate;
import jp.co.ctc.servlet.LogSps;
import jp.co.ctc.servlet.LogUploadAssembly;
import jp.co.ctc.servlet.LogUploadLogistics;
import jp.co.ctc.servlet.LogUserid;
import jp.co.ctc.servlet.Supplier;
import jp.co.ctc.util.ServletUtil;
import jp.co.ctc.util.ServletUtil.Attribute;
import jp.co.ctc.util.ServletUtil.IdList;

import org.seasar.framework.log.Logger;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.annotation.Required;
import org.seasar.struts.util.ResponseUtil;

/**
 * @author CJ01786
 *
 */
public class PdaAction {

	/**
	 * HTTPリクエスト
	 */
	@Resource
	private HttpServletResponse response;

	/**
	 * HTTPレスポンス
	 */
	@Resource
	private HttpServletRequest request;

	/**
	 * XMLを読み取るストリーム
	 */
	private ByteArrayInputStream inputStream;

	/**
	 * ログ出力用のLoggerインスタンス
	 */
	private Logger logger = Logger.getLogger(PdaAction.class);

	/**
	 * idパラメータを格納するid列挙体
	 */
	private IdList paramId;

	/**
	 * パラメータ：id
	 */
	@Required
	public String id = "";

	/**
	 * パラメータ：group
	 */
	@Required
	public String group = "";

	/**
	 * パラメータ：sps
	 */
	@Required
	public String sps = "";

	/**
	 * パラメータ：EditDate
	 */
	@Required
	public String editDate = "";

	/**
	 * 検査用の実行メソッド
	 * @return null
	 */
	@Execute(validator = false)
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public String inspection() {
		//あくまで窓口としておき、処理はメインメソッドに投げる。
		this.mainMethod(Attribute.inspection);
		return null;
	}

	/**
	 * 物流組立用の実行メソッド
	 * @return null
	 */
	@Execute(validator = false)
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public String logistics() {
		//あくまで窓口としておき、処理はメインメソッドに投げる。
		this.mainMethod(Attribute.logistics);
		return null;
	}

	/**
	 * 初期化処理を行う。
	 * @throws IOException IOException
	 */
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void initialize() throws IOException {
		//リクエストとレスポンスの取得

		//エンコーディングの設定
		this.response.setCharacterEncoding(ServletUtil.ENCODING);

		//パラメータに関するログ出力
		StringBuilder paramLog = new StringBuilder();
		paramLog.append("RequestParameter");
		if (!this.id.isEmpty()) {
			paramLog.append(" id:" + this.id);
		}
		if (!this.group.isEmpty()) {
			paramLog.append(" group:" + this.group);
		}
		if (!this.sps.isEmpty()) {
			paramLog.append(" sps:" + this.sps);
		}
		if (!this.editDate.isEmpty()) {
			paramLog.append(" EditDate:" + this.editDate);
		}
		this.logger.info(paramLog.toString());

		//InputStreamを読み込みログ出力
		String xml = ServletUtil.inputStreamToString(this.request.getInputStream());
		this.logger.info("Get XML from PDA " + ServletUtil.LINE_SEPARATOR + xml);

		//読み込んだXMLを再度InputStreamに変換する。
		this.inputStream = new ByteArrayInputStream(xml.getBytes());

		this.paramId = ServletUtil.parseIdList(this.id);
	}

	/**
	 * 実際に処理を行う
	 * @param attribute システム属性（検査／物流組立）
	 */
	@TransactionAttribute(TransactionAttributeType.NEVER)
	private void mainMethod(Attribute attribute) {
		String responseValue = "";	//PDAに送信するレスポンス
		CreateResponse createRes = null;	//レスポンスを生成するインスタンス
		try {
			this.initialize();
			//システム属性に応じて処理を選択させる。
			switch (attribute) {
			case logistics:
				createRes = this.selectLogisticsMethod();
				break;
			default:
				break;
			}
			if (createRes == null) {
				//値が設定されなかった場合、パラメータなしか無効なパラメータかを判断
				switch (this.paramId) {
				case novalue:
					responseValue = ServletUtil.RESPONSE_OK;
					break;

				default:
					responseValue = ServletUtil.RESPONSE_NG;
					break;

				}
			} else {
				//値が設定された場合、クライアントへ返すレスポンスを取得する。
				responseValue = createRes.getResponse(this.inputStream);
			}

			//ストリームとログへのレスポンスの書き出し
			ResponseUtil.write(responseValue);
			this.logger.info("Send XML to PDA" + ServletUtil.LINE_SEPARATOR  + responseValue);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}


	/**
	 * IDパラメータの値に応じて、物流組立向けに処理を選択する。
	 * @return レスポンスを返すための処理のインスタンス
	 */
	@TransactionAttribute(TransactionAttributeType.NEVER)
	private CreateResponse selectLogisticsMethod() {
		CreateResponse createRes = null;
		switch (this.paramId) {
		case body:
			createRes = new LogBody();
			break;

		case sps:
			createRes = new LogSps();
			boolean checkLock = false;
			if (this.sps == null || this.sps.isEmpty()) {
				checkLock = true;
			}
			createRes.setLockCheckFlg(checkLock);
			break;

		case upload_assembly:
			createRes = new LogUploadAssembly();
			break;

		case upload_logistics:
			createRes = new LogUploadLogistics();
			break;

		case supplier:
			createRes = new Supplier();
			break;

		case userId:
			createRes = new LogUserid();
			break;

		case EditDate:
			createRes = new LogEditdate();
			createRes.setEditDate(this.editDate);
			break;

		default:
			break;

		}
		return createRes;
	}
}
