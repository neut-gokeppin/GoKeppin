package jp.co.ctc.servlet;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.util.List;

import jp.co.ctc.dto.LgMBcsignDTO;
import jp.co.ctc.entity.FBcdata;
import jp.co.ctc.entity.LgFTakeresult;
import jp.co.ctc.entity.LgMOrder;
import jp.co.ctc.entity.LgMSps;
import jp.co.ctc.entity.RSps;
import jp.co.ctc.entity.TSf;
import jp.co.ctc.service.FBcdataService;
import jp.co.ctc.service.LgFTakeresultService;
import jp.co.ctc.service.LgMSpsService;
import jp.co.ctc.service.LockManagerService;
import jp.co.ctc.service.RSpsService;
import jp.co.ctc.service.TSfService;
import jp.co.ctc.util.ServletUtil;
import jp.co.ctc.util.ServletUtil.Attribute;
import jp.co.ctc.util.ServletUtil.LockState;
import jp.co.ctc.util.Utils;

import org.seasar.framework.container.SingletonS2Container;



/**
 * 物流・組立システムでのsps要求に対する処理を行うクラス
 * @author CJ01786
 *
 */
public class LogSps extends Download {

	/**
	 * 対象となるサーブレットの種類
	 */
	private static final Attribute SERVLET_TYPE = Attribute.logistics;

	/**
	 * 指定したダウンロード要求に対しロックをかける
	 * @param recvSps 受信したXMLのエンティティリスト
	 * @exception Exception 処理例外
	 */
	protected void createLock(Object recvSps) throws Exception {
		LockManagerService srvLock = SingletonS2Container.getComponent(LockManagerService.class);
		RSps rSps = (RSps) this.getTypeCheckedObject(recvSps, RSps.class);

		//ロックをかけていなければ、排他テーブルにレコードを追加する。
		LockState lockState = srvLock.getLockState(SERVLET_TYPE, rSps.bodyNo, rSps.recvDay, rSps.spsCode, rSps.userCode);
		switch (lockState) {
		case nothing:
			srvLock.simpleCreateLock(SERVLET_TYPE, rSps.bodyNo, rSps.recvDay, rSps.spsCode, rSps.userCode);
		default:
			break;
		}
	}

	/**
	 * XMLで指定された値からBCデータを取得する。
	 * @param recvSps 処理中の要求
	 * @return 取得したインスタンス
	 * @throws Exception 処理例外
	 */
	protected FBcdata getBcdata(Object recvSps) throws Exception {
		FBcdataService srvBcdata = SingletonS2Container.getComponent(FBcdataService.class);
		RSps rSps = (RSps) this.getTypeCheckedObject(recvSps, RSps.class);

		List<FBcdata> resBcdata = srvBcdata.getLogFBcdata(rSps.bodyNo, rSps.recvDay);
		if (!Utils.isEmpty(resBcdata)) {
			return resBcdata.get(0);
		} else {
			throw new Exception(rSps.bodyNo + "に該当するBCデータがありません。");
		}
	}

	/**
	 * XMLで指定されたSPS台車コードから、該当するデータを取得する。
	 * @param bcdata BCデータ
	 * @param recvSps 処理中の要求
	 * @return 取得したインスタンス
	 * @exception Exception 処理例外
	 */
	protected Object getGroup(FBcdata bcdata, Object recvSps) throws Exception {
		LgMSpsService srvSps = SingletonS2Container.getComponent(LgMSpsService.class);
		RSps rSps = (RSps) this.getTypeCheckedObject(recvSps, RSps.class);

		List<LgMSps> spsList = srvSps.getLgMSpsByName(ServletUtil.SELECT_MST, rSps.spsCode, bcdata.lgmstVer);

		if (!Utils.isEmpty(spsList)) {
			return spsList.get(0);
		} else {
			throw new Exception(rSps.spsCode.toString() + "に該当するグループデータがありません。");
		}
	}

	/**
	 * 受信したXMLの値全体のロック状態をチェックする。
	 * @param recvSps 受信したXMLをインスタンス化したリスト
	 * @return ロック状態
	 */
	protected LockState getLockState(List<?> recvSps) {
		LockManagerService srvLock = SingletonS2Container.getComponent(LockManagerService.class);

		LockState lState = LockState.nothing;	//return用のLockState変数。初期値はロックなし。
		try {
			for (Object object : recvSps) {
				RSps rSps = (RSps) this.getTypeCheckedObject(object, RSps.class);

				//排他テーブルを読み、ロックの有無を取得する。
				LockState lockState = srvLock.getLockState(SERVLET_TYPE, rSps.bodyNo, rSps.recvDay, rSps.spsCode, rSps.userCode);

				if (lockState == LockState.anotherLock) {
					//ほかのユーザがロックしていれば、NGを返す。
					return LockState.anotherLock;
				}
				if (lockState == LockState.userLock) {
					//自分がロックしていれば、返答用のステータスを「LOCK」に変更する。
					lState = LockState.userLock;
				}
			}
			return lState;
		} catch (Exception e) {
			return LockState.anotherLock;
		}
	}

	/**
	 * 順番を指定するマスタからデータを所得する。
	 * @param sps 順番を取得したいSPS台車のインスタンス
	 * @return 取得したインスタンス
	 * @throws Exception 処理例外
	 */
	protected List<?> getOrderList(Object sps) throws Exception {
		LgMSps mSps = (LgMSps) this.getTypeCheckedObject(sps, LgMSps.class);
		return mSps.mOrderList;
	}
	/**
	 * ストリームからXMLを読み出し、エンティティに格納してリスト化する。
	 * @param inputStream XMLを取得するためのストリーム
	 * @return 生成されたインスタンスのリスト
	 */
	protected List<?> getRecvEntity(ByteArrayInputStream inputStream) {
		RSpsService srvSps = SingletonS2Container.getComponent(RSpsService.class);
		return srvSps.readXml(inputStream);
	}

	/**
	 * 指定したダウンロード要求に対してかけたロックを解除する
	 * @param recvSps 受信したXMLのエンティティリスト
	 * @exception Exception 処理例外
	 */
	protected void removeLock(Object recvSps) throws Exception {
		LockManagerService srvLock = SingletonS2Container.getComponent(LockManagerService.class);
		RSps rSps = (RSps) this.getTypeCheckedObject(recvSps, RSps.class);

		srvLock.removeLock(SERVLET_TYPE, rSps.bodyNo, rSps.recvDay, rSps.spsCode, rSps.userCode);
	}

	/**
	 * 取出し順のエンティティからXMLを設定する。
	 * @param bcdata BCデータ
	 * @param sps SPS台車エンティティ
	 * @param order 取出し順エンティティ
	 * @param recvSps 受信したXMLのエンティティ
	 * @return XMLが生成されたか
	 * @exception Exception 処理例外
	 */
	@Override
	protected boolean setGroupXmlByOrder(FBcdata bcdata, Object sps, Object order, Object recvSps) throws Exception {

		//サービスクラス定義
		TSfService srvSf = SingletonS2Container.getComponent(TSfService.class);

		//引数をキャストする。
		LgMSps mSps = (LgMSps) this.getTypeCheckedObject(sps, LgMSps.class);
		LgMOrder mOrder = (LgMOrder) this.getTypeCheckedObject(order, LgMOrder.class);
		RSps rSps = (RSps) this.getTypeCheckedObject(recvSps, RSps.class);

		//新しいテーブルの生成
		this.srvXmlWriter.ceateTable();
		this.srvXmlWriter.addData("spsCode", rSps.spsCode);
		this.srvXmlWriter.addData("spsName", mSps.spsName);
		this.srvXmlWriter.addData("spsNo", mSps.spsNo);
		this.srvXmlWriter.addData("bodyNo", rSps.bodyNo);
		this.srvXmlWriter.addData("recvDay", rSps.recvDay);

		//取出し結果を設定
		this.setXmlResultflg(rSps.bodyNo, rSps.recvDay, mOrder.partCode);

		this.srvXmlWriter.addData("partsOrder", mOrder.takeOrder);

		//bcsignを取得し、その値からXMLを設定する
		Object mBcsign = ServletUtil.getMsgSign(SERVLET_TYPE, bcdata, mOrder.mPart);

		if (!this.setGroupXmlByItem(mBcsign)) {
			//結果を送らない場合、XMLをロールバックしてfalseを返す。
			this.srvXmlWriter.rollbackTable();
			return false;
		}

		//SF基本テーブルからデータを取得する。
		TSf tSf = srvSf.selectById(bcdata.idno, bcdata.loDate);
		this.srvXmlWriter.addData("productNo", tSf.comment1);

		return true;
	}

	/**
	 * 部品および指示記号からなるDTOインスタンスからXMLを設定する。
	 * @param part 項目（部品）
	 * @return XMLが生成されたか
	 * @throws Exception 処理例外
	 */
	@Override
	protected boolean setGroupXmlByItem(Object part) throws Exception {
		LgMBcsignDTO bcsign = (LgMBcsignDTO) this.getTypeCheckedObject(part, LgMBcsignDTO.class);

		if (bcsign.notuseFlag) {
			//取出し不要フラグが設定されている場合XMLを送らない。
			return false;
		}

		//画像ファイル更新日取得
		String imgRecvDay = this.getFileUpdateTime(Utils.LG_IMAGE_DIR, bcsign.fileName);
		String fileName;
		if (bcsign.fileName != null) {
			fileName = URLEncoder.encode(bcsign.fileName, "UTF-8");
		} else {
			fileName = "";
		}

		Integer checkFlag = 0;
		if (bcsign.checkFlag != null && bcsign.checkFlag) {
			checkFlag = 1;
		}

		this.srvXmlWriter.addData("partsCode", bcsign.partCode);
		this.srvXmlWriter.addData("partsName", bcsign.partName);
		this.srvXmlWriter.addData("partsContents", bcsign.identName);
		this.srvXmlWriter.addData("fileName", fileName);
		this.srvXmlWriter.addData("imgRecvDay", imgRecvDay);
		this.srvXmlWriter.addData("partsMaker", bcsign.supplierName);
		this.srvXmlWriter.addData("partsBackNum", bcsign.backNo);
		this.srvXmlWriter.addData("shelfCheck", checkFlag);
		this.srvXmlWriter.addData("shelfAddress", bcsign.rackAddress);
		this.srvXmlWriter.addData("partsNo", bcsign.partNo);
		return true;
	}

	/**
	 * BCデータにマスタバージョンが設定されていない場合設定する。
	 * @param bcdata BCデータ
	 * @param sps SPS台車のインスタンス
	 * @throws Exception 処理例外
	 */
	protected void setMstVer(FBcdata bcdata, Object sps) throws Exception {
		LgMSps mSps = (LgMSps) this.getTypeCheckedObject(sps, LgMSps.class);
		FBcdataService srvBcdata = SingletonS2Container.getComponent(FBcdataService.class);

		if (bcdata.lgmstVer == null || bcdata.lgmstVer == 0) {
			//BCデータのマスタバージョンが『0』の場合

			//BCデータのマスタバージョンを更新する。
			bcdata.lgmstVer = mSps.mstVer;
			srvBcdata.update(bcdata);
		}
	}

	/**
	 * ボデーNo、受信日、部品コードから取出し結果を設定
	 * @param bodyNo ボデーNo
	 * @param recvDay 受信日
	 * @param partCode 部品コード
	 * @return 取出結果がOKかどうか
	 */
	@Override
	protected boolean setXmlResultflg(String bodyNo, String recvDay, Integer partCode) {
		LgFTakeresultService srvResult = SingletonS2Container.getComponent(LgFTakeresultService.class);
		List<LgFTakeresult> resResult;
		String rackResult = "";		//棚照合結果
		String partResult = "";		//部品チェック結果。棚NGなら空値が設定される。
		resResult = srvResult.getFTakeresultByKeys(bodyNo, recvDay, partCode);
		if (!Utils.isEmpty(resResult)) {
			//結果があれば処理を行う。

			rackResult = resResult.get(0).rackResult;
			if (rackResult == null || rackResult.trim().equals("0")) {
				//棚照合結果が『null(棚照合なし)』もしくは『0(OK)』の場合部品の結果も確認

				partResult = resResult.get(0).partResult;
				if (partResult != null && partResult.trim().equals("0")) {
					//取出結果が『0（OK）』の場合trueを返す
					this.srvXmlWriter.addData("resultFlg", partResult);
					return true;
				}
			}
		}
		this.srvXmlWriter.addData("resultFlg", partResult);
		return false;
	}
}
