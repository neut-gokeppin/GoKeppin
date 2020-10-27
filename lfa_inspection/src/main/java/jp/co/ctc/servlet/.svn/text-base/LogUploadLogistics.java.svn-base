/**
 *
 */
package jp.co.ctc.servlet;

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.List;

import jp.co.ctc.entity.LgFStoreresult;
import jp.co.ctc.entity.RUploadLogistics;
import jp.co.ctc.service.LgFStoreresultService;
import jp.co.ctc.service.RUploadLogisticsService;
import jp.co.ctc.util.ServletUtil;

import org.seasar.framework.container.SingletonS2Container;


/**
 * @author CJ01786
 *
 */
public class LogUploadLogistics extends Upload {

	/**
	 * ストリームからXMLを読み出し、エンティティに格納してリスト化する。
	 * @param inputStream XMLを取得するためのストリーム
	 * @return 生成されたインスタンスのリスト
	 */
	@Override
	protected List<?> getRecvEntity(ByteArrayInputStream inputStream) {
		RUploadLogisticsService srvUpload = SingletonS2Container.getComponent(RUploadLogisticsService.class);
		return srvUpload.readXml(inputStream);
	}

	@Override
	protected void setResult(List<?> rcvUpload) throws Exception {
		//XMLから結果を読み取り、DBに登録する
		for (Object rUpload : rcvUpload) {
			//結果をDBに登録する。
			this.setResultByRUpload(rUpload);
		}
	}

	/**
	 * 受信したXMLのインスタンスから結果を登録する。
	 * @param rUpload XMLから生成したインスタンス
	 * @exception Exception 処理例外
	 */
	protected void setResultByRUpload(Object rUpload) throws Exception {
		LgFStoreresultService srvResult = SingletonS2Container.getComponent(LgFStoreresultService.class);
		RUploadLogistics upload = (RUploadLogistics) this.getTypeCheckedObject(rUpload, RUploadLogistics.class);

		Timestamp rackTime = ServletUtil.parseTimestamp(upload.shelfReadTime);
		Timestamp partTime = ServletUtil.parseTimestamp(upload.partsReadTime);

		LgFStoreresult insResult = new LgFStoreresult();

		insResult.rackQrcode = upload.shelfQrcode;
		insResult.partQrcode = upload.partsQrcode;
		insResult.resultDiv = upload.shelfResultFlg;
		insResult.storeUser = upload.userCode;
		insResult.partReaddate = partTime;
		insResult.rackReaddate = rackTime;

		if (!srvResult.create(insResult)) {
			throw new Exception("検査結果の登録に失敗しました。");
		}
	}
}
