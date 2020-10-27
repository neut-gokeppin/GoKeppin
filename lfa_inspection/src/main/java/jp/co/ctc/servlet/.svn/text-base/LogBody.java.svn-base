/**
 *
 */
package jp.co.ctc.servlet;

import java.util.List;

import jp.co.ctc.entity.FBcdata;
import jp.co.ctc.entity.LgFLock;
import jp.co.ctc.entity.LgMSps;
import jp.co.ctc.service.FBcdataService;
import jp.co.ctc.service.LgFLockService;
import jp.co.ctc.service.LgMSpsService;
import jp.co.ctc.util.ServletUtil;
import jp.co.ctc.util.ServletUtil.LockState;
import jp.co.ctc.util.ServletUtil.State;

import org.seasar.framework.container.SingletonS2Container;

/**
 * @author CJ01786
 *
 */
public class LogBody extends Body {

	/**
	 * SPS台車状態を設定する。
	 * @param sps 設定対象となるSPS台車
	 * @param state 設定する状態
	 * @return 設定したあとのSPS台車
	 * @throws Exception 型の不一致による例外
	 */
	@Override
	protected Object setGroupState(Object sps, State state) throws Exception {
		LgMSps mSps = (LgMSps) this.getTypeCheckedObject(sps, LgMSps.class);
		mSps.spsState = state;
		return mSps;
	}

	/**
	 * SPS台車状態を取得する。
	 * @param sps SPS台車
	 * @return SPS台車状態
	 * @throws Exception 型の不一致による例外
	 */
	@Override
	protected State getGroupState(Object sps) throws Exception {
		LgMSps mSps = (LgMSps) this.getTypeCheckedObject(sps, LgMSps.class);
		return mSps.spsState;
	}

	/**
	 * BCデータからSPS台車の一覧を取得する。
	 * @param bcdata BCデータ
	 * @return 取得したSPS台車の一覧
	 */
	@Override
	public List<?> getGroupList(FBcdata bcdata) {
		int mstVer = this.getMstver(bcdata);

		LgMSpsService srvGroup = SingletonS2Container.getComponent(LgMSpsService.class);
		return srvGroup.getLgMSpss(ServletUtil.SELECT_MST, mstVer);
	}

	/**
	 * 	BCデータとグループ（SPS台車）からロックの状態を取得する。
	 * @param bcdata BCデータ
	 * @param sps SPS台車
	 * @return ロック状態
	 * @exception Exception 処理例外
	 */
	@Override
	protected LockState getLockState(FBcdata bcdata, Object sps) throws Exception {
		LgMSps mSps = (LgMSps) this.getTypeCheckedObject(sps, LgMSps.class);

		for (Object objLock : this.lockList) {
			LgFLock lock = (LgFLock) this.getTypeCheckedObject(objLock, LgFLock.class);
			if (bcdata.bodyNo.equals(lock.bodyNo)) {
				//ボデーNoが一致し
				if (bcdata.recvDay.equals(lock.recvDay)) {
					//受信日が一致し
					if (mSps.spsCode.toString().equals(lock.spsCode.trim())) {
						//SPSコードが一致する場合ロックされているとみなす。
						//また、処理効率のためリストから削除しておく。
						this.lockList.remove(lock);

						return LockState.anotherLock;
					}
				}
			}
		}

		return LockState.nothing;

	}

	/**
	 * 現在のボデー状態とグループ・SPS台車状態からボデー状態を取得する。
	 * @param bodyState 現在のボデー状態
	 * @param groupState グループ・SPS台車状態
	 * @param lockState ロック状態
	 * @return 新たなボデー状態
	 */
	protected State getBodyState(State bodyState, State groupState, LockState lockState) {
		State bState = bodyState;
		if (groupState == State.noCheck) {
			//SPS台車状態が未着手の場合、ボデー状態を未着手とする。
			bState = State.noCheck;
		}
		if (groupState == State.reCheck && bState != State.noCheck) {
			//SPS台車状態がNGありであり、現時点のボデー状態が未着手でないなら、ボデー状態をNGありとする。
			bState = State.reCheck;
		}
		if (groupState == State.yetCheck && bState == State.compleat) {
			//SPS台車状態が未確認ありであり、現時点のボデー状態が完了（未着手・NGありではない）の場合未確認ありとする。
			bState = State.yetCheck;
		}
		return bState;
	}

	/**
	 * XML文を生成する。
	 * @param bcdata BCデータ
	 * @param sps SPS台車
	 * @param bodyState ボデー状態
	 * @return 成功可否
	 * @throws Exception 型の不一致による例外
	 */
	@Override
	protected boolean setXml(FBcdata bcdata, Object sps, State bodyState) throws Exception {
		//TSfService srvSf = SingletonS2Container.getComponent(TSfService.class);
		LgMSps mSps = (LgMSps) this.getTypeCheckedObject(sps, LgMSps.class);

		//ハンドル区分名称を取得
		String ptnDivName;
		try {
			int ptnDivInt = Integer.parseInt(bcdata.ptnDiv);
			if (ptnDivInt < ServletUtil.NAME_HANDLE.length) {
				ptnDivName = ServletUtil.NAME_HANDLE[ptnDivInt];
			} else {
				ptnDivName = "";
			}
		} catch (Exception e) {
			ptnDivName = "";
		}

		this.srvXmlWriter.ceateTable();
		this.srvXmlWriter.addData("bodyNo", bcdata.bodyNo);
		this.srvXmlWriter.addData("spsCode", mSps.spsCode);
		this.srvXmlWriter.addData("spsName", mSps.spsName);
		this.srvXmlWriter.addData("spsNo", mSps.spsNo);
		this.srvXmlWriter.addData("bodyState", bodyState.ordinal());
		this.srvXmlWriter.addData("spsState", mSps.spsState.ordinal());
		this.srvXmlWriter.addData("recvDay", bcdata.recvDay);
		this.srvXmlWriter.addData("ptnDivName", ptnDivName);

		if (bcdata.tSf == null) {
			this.srvXmlWriter.addData("productNo", "");
			this.srvXmlWriter.addData("dest", "");
			this.srvXmlWriter.addData("frSeq", "");
			this.srvXmlWriter.addData("extCode", "");
			this.srvXmlWriter.addData("intCode", "");
		} else {
			this.srvXmlWriter.addData("productNo", bcdata.tSf.comment1);
			this.srvXmlWriter.addData("dest", bcdata.tSf.dest);
			this.srvXmlWriter.addData("frSeq", bcdata.tSf.frSeq);
			this.srvXmlWriter.addData("extCode", bcdata.tSf.extCode);
			this.srvXmlWriter.addData("intCode", bcdata.tSf.intCode);
		}

		return true;
	}

	/**
	 * BCデータからマスタバージョンを取得する。
	 * @param bcdata マスタバージョンを取得するBCデータ
	 * @return 取得したマスタバージョン
	 */
	@Override
	protected int getMstver(FBcdata bcdata) {
		if (bcdata.lgmstVer == null) {
			return 0;
		}
		return bcdata.lgmstVer;
	}

	/**
	 * 排他制御のリストを取得する。
	 * @return 取得した排他情報のリスト
	 */
	@Override
	protected List<?> getLockList() {
		LgFLockService srvLock = SingletonS2Container.getComponent(LgFLockService.class);
		return srvLock.getLgFLock();
	}

	/**
	 * BCデータを取得する。
	 * @return 取得したBCデータのリスト
	 */
	@Override
	protected List<FBcdata> getFBcdataList() {
		FBcdataService srvBcdata = SingletonS2Container.getComponent(FBcdataService.class);
		return srvBcdata.getLogFBcdataWithResultsum();
	}
}
