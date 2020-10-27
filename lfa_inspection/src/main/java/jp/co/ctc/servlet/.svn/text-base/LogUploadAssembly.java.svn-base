package jp.co.ctc.servlet;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jp.co.ctc.entity.LgFTakeresult;
import jp.co.ctc.entity.LgFTakeresultsum;
import jp.co.ctc.entity.RUploadAssembly;
import jp.co.ctc.entity.RUploadUnlock;
import jp.co.ctc.service.LgFTakeresultService;
import jp.co.ctc.service.LgFTakeresultsumService;
import jp.co.ctc.service.LockManagerService;
import jp.co.ctc.service.RUploadAssemblyService;
import jp.co.ctc.service.RUploadUnlockService;
import jp.co.ctc.util.ServletUtil;
import jp.co.ctc.util.ServletUtil.Attribute;

import org.seasar.framework.container.SingletonS2Container;

/**
 * 物流・組立システムでのupload_assembly要求に対する処理を行うクラス
 * @author CJ01786
 *
 */
public class LogUploadAssembly extends Upload {

	/**
	 * 対象となるサーブレットの種類
	 */
	private static final Attribute SERVLET_TYPE = Attribute.logistics;

	/**
	 * ロック解除用の受信XMLインスタンスを生成するためのストリーム
	 */
	private InputStream unlockStream;

	/**
	 * ストリームからXMLを読み出し、エンティティに格納してリスト化する。
	 * @param inputStream XMLを取得するためのストリーム
	 * @return 生成されたインスタンスのリスト
	 */
	@Override
	protected List<?> getRecvEntity(ByteArrayInputStream inputStream) {
		RUploadAssemblyService srvUpload = SingletonS2Container.getComponent(RUploadAssemblyService.class);

		//結果挿入用と排他制御解除用の２つのエンティティに分解するため、ストリームを２つ生成する
		String xml = ServletUtil.inputStreamToString(inputStream);
		InputStream uploladStream = new ByteArrayInputStream(xml.getBytes());
		this.unlockStream = new ByteArrayInputStream(xml.getBytes());

		//生成したうち一方から結果挿入用のエンティティを生成して返す。
		return srvUpload.readXml(uploladStream);
	}

	@Override
	protected void setResult(List<?> rcvUpload) throws Exception {
		//ロックを解除する。
		this.removeLock(rcvUpload);

		//結果サマリテーブルに登録するデータの器を用意
		List<LgFTakeresultsum> sumResultList = new ArrayList<LgFTakeresultsum>();

		//XMLから結果を読み取り、DBに登録する
		for (Object rUpload : rcvUpload) {
			//結果をDBに登録する。
			this.setResultByRUpload(rUpload, sumResultList);
		}

		//サマリをDBに登録する。
		this.setResultByRUploadsum(sumResultList);
	}

	/**
	 * 受信したXMLのインスタンスから結果を登録する。
	 * @param rUpload XMLから生成したインスタンス
	 * @param sumResultList 結果登録したボデー、部品のリスト
	 * @exception Exception 処理例外
	 */
	protected void setResultByRUpload(Object rUpload, List<LgFTakeresultsum> sumResultList) throws Exception {
		LgFTakeresultService srvResult = SingletonS2Container.getComponent(LgFTakeresultService.class);
		RUploadAssembly upload = (RUploadAssembly) this.getTypeCheckedObject(rUpload, RUploadAssembly.class);

		//取出回数を設定
		Integer takeNo = this.getUploadTimes(upload.bodyNo, upload.recvDay, upload.partsCode);

		String rackResult = null;
		Timestamp rackTime = null;
		if (!(upload.shelfResultFlg == null || upload.shelfResultFlg.trim().equals(""))
				&& !(upload.shelfResultTime == null || upload.shelfResultTime.trim().equals(""))) {
			//棚照合の結果が設定されていない場合棚照合なしとしてnullのままとする。

			rackResult = upload.shelfResultFlg;
			rackTime = ServletUtil.parseTimestamp(upload.shelfResultTime);
		}

		String partResult = null;
		Timestamp partTime = null;

		//部品チェックの結果を判断
		if (!(upload.partsResultFlg == null || upload.partsResultFlg.trim().equals(""))
				&& !(upload.partsResultTime == null || upload.partsResultTime.trim().equals(""))) {
			//部品チェックの結果が設定されていない場合はnullとなるようにする。

			partResult = upload.partsResultFlg;
			partTime = ServletUtil.parseTimestamp(upload.partsResultTime);
		}

		if (rackTime != null || partTime != null) {
			//棚か部品どちらかがチェックされていれば登録する

			//検査結果の値を設定。
			LgFTakeresult insResult = new LgFTakeresult();
			insResult.bodyNo = upload.bodyNo;
			insResult.recvDay = upload.recvDay;
			insResult.partCode = upload.partsCode;
			insResult.takeNo = takeNo;
			insResult.rackResult = rackResult;
			insResult.partResult = partResult;
			insResult.takeUser = upload.userCode;
			insResult.partDate = partTime;
			insResult.rackDate = rackTime;
			insResult.rackQrcode = upload.shelfQrrcode;
			insResult.partQrcode = upload.partsQrcode;
			//結果を追加する。
			if (!srvResult.create(insResult)) {
				throw new Exception("検査結果の登録に失敗しました。");
			}

			//XMLのデータをサマリ用リストに登録する。
			//bodyNo,recvDay,spsCodeが同一のデータを複数作成しない。
			boolean existData = false;
			for (LgFTakeresultsum sumResult : sumResultList) {
				if (upload.bodyNo.equals(sumResult.bodyNo)
						&& upload.recvDay.equals(sumResult.recvDay)
						&& upload.spsCode.equals(sumResult.spsCode)) {
					sumResult.insertUser = upload.userCode;
					sumResult.updateUser = upload.userCode;
					existData = true;
					break;
				}
			}
			//一致するものがなければ新規登録
			if (!existData) {
				LgFTakeresultsum sumResult = new LgFTakeresultsum();
				sumResult.bodyNo = upload.bodyNo;
				sumResult.recvDay = upload.recvDay;
				sumResult.spsCode = upload.spsCode;
				sumResult.insertUser = upload.userCode;
				sumResult.updateUser = upload.userCode;
				sumResultList.add(sumResult);
			}
		}
	}

	/**
	 * サマリのリスト登録
	 * @param sumResultList サマリ登録するボデー、部品のリスト
	 * @author CJ01729
	 */
	protected void setResultByRUploadsum(List<LgFTakeresultsum> sumResultList) {
		//台車ごとのサマリデータをListから登録
		LgFTakeresultsumService srvResultSum = SingletonS2Container.getComponent(LgFTakeresultsumService.class);
		for (LgFTakeresultsum sumResult : sumResultList) {
			srvResultSum.setResultsum(sumResult);
		}
	}

	/**
	 * 現在のアップロード処理で取出回数が何回目となるか
	 * @param bodyNo ボデーNo
	 * @param recvDay 受信日
	 * @param partCode 部品コード
	 * @return 取出回数
	 */
	protected Integer getUploadTimes(String bodyNo, String recvDay, Integer partCode) {
		LgFTakeresultService srvResult = SingletonS2Container.getComponent(LgFTakeresultService.class);
		List<LgFTakeresult> resResult;

		resResult = srvResult.getFTakeresultByKeys(bodyNo, recvDay, partCode);
		if (resResult != null && resResult.size() > 0) {
			//過去に検査が行われていた場合、最新の検査回数に『1』加算する。
			return resResult.get(0).takeNo + 1;
		} else {
			//検査結果がない場合は1回目とみなす。
			return 1;
		}
	}

	/**
	 * ロックを解除する。
	 * @param rUpload XMLから生成したインスタンスのリスト
	 * @exception Exception 処理例外
	 */
	protected void removeLock(List<?> rUpload) throws Exception {
		//XML変更後はこっちを採用
		RUploadUnlockService srvUnlock = SingletonS2Container.getComponent(RUploadUnlockService.class);
		LockManagerService srvLock = SingletonS2Container.getComponent(LockManagerService.class);
		List<RUploadUnlock> unlockList = srvUnlock.readXml(this.unlockStream);

		for (RUploadUnlock unlock : unlockList) {
			if (this.saveCode == null || !this.saveCode.equals(unlock.spsCode)) {
				this.saveCode = unlock.spsCode;
				srvLock.simpleRemoveLock(SERVLET_TYPE, unlock.bodyNo, unlock.recvDay, this.saveCode, null);
			}
		}

	}
}
