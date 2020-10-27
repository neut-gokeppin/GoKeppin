package jp.co.ctc.service;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.extension.jdbc.where.SimpleWhere;
import org.seasar.extension.timer.TimeoutManager;
import org.seasar.extension.timer.TimeoutTarget;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.log.Logger;
import org.seasar.framework.util.ResourceUtil;

import jp.co.ctc.entity.MBcsign;
import jp.co.ctc.entity.MGroup;
import jp.co.ctc.entity.MItem;
import jp.co.ctc.entity.MOrder;
import jp.co.ctc.entity.MReserve;
import jp.co.ctc.entity.MVehicle;
import jp.co.ctc.util.Utils;

/**
 * 予約処理を行う。
 *
 * @author DA 2016/02/24
 */
public class MstRegistService implements TimeoutTarget
{
	/**
	 * JdbcManagerを使います。
	 */
	public JdbcManager jdbcManager;

	/**
	 * StoredFunctionService
	 */
	@Resource
	public StoredFunctionService storedFunctionService;

	/**
	 * ログ出力用
	 */
	private Logger logger = Logger.getLogger(MstRegistService.class);

	/**
	 * 本番予約マスタを扱うサービス
	 */
	@Resource
	public MReserveService mReserveService;

	/**
	 * 指示記号マスタを扱うサービス
	 */
	@Resource
	public MBcsignService mBcsignService;

	/**
	 * 予約状態
	 */
	public enum ReservationState
	{
		/** 予約中 */
		RESERVATION,

		/** 実行中 */
		EXECUTION,

		/** 予約中のみ */
		RESERV_ONLY
	};

	/**
	 * 予約処理を実行するためのタイマーを起動します。 このメソッドは、S2Container起動時に自動的に呼び出すよう、
	 * app.diconにて設定しています。
	 *
	 * @param permanent
	 *            タイマーを永続的に実行するか。true:永続実行、false:1回だけ実行
	 */
	public void importStart(boolean permanent)
	{
		// タイマー実行間隔
		int interval = (Integer) SingletonS2ContainerFactory.getContainer().getComponent("reserveMonitoring");
		if (interval < 0) {
			return;
		}

		// タイマー起動
		TimeoutManager timeoutManager = TimeoutManager.getInstance();
		timeoutManager.addTimeoutTarget(this, interval, permanent);
	}

	/**
	 * テスト用
	 */
	@SuppressWarnings("unused")
	public void test()
	{
		try {
			ArrayList<String> itemCodeList = new ArrayList<String>();
			Collections.addAll(itemCodeList, "17529", "17530");
			ArrayList<String> a = getReservationInformationMBcsign(0, "L", itemCodeList);

			Timestamp dateTime;
			dateTime = new Timestamp(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2016/2/10 1:0:0").getTime());

			//int ret1 = storedFunctionService.tempMstRegist(0, "L", "3", "2148,2149", dateTime, false);
			//int ret2 = storedFunctionService.tempMstRegist(0, "L", "4", "17529,17530", dateTime, false);
			int ret3 = storedFunctionService.tempMstRegist(0, "L", "5", null, dateTime, false);

		}
		catch (ParseException e) {
			e.printStackTrace();
		}

		throw new RuntimeException("error");
	}

	/**
	 * トランザクション
	 */
	@Resource
	private UserTransaction tx;

	/**
	 * 定期的に実行する処理。 このメソッドは、importStartメソッドにて指定したタイミングで 定期的に実行されます。
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void expired()
	{
		// DEBUG用
		//logger.info("予約処理開始");

		ClsVehicle errClsVehicle = null;

		try {
			// トランザクションの開始
			tx.begin();

			Timestamp now = Utils.nowts();

			// 予約中車種取得
			List<ClsVehicle> mVehicleList = getVehicleList(now);
			// 車種がない場合処理を終了する
			if (mVehicleList.size() < 1) {
				return;
			}

			// 予約処理
			for (ClsVehicle clsVehicle : mVehicleList) {

				errClsVehicle = clsVehicle;

				// DEBUG用
				//this.logger.info(clsVehicle.mstType + ", "
				//		+ (clsVehicle.mstVer == 0 ? " " : "") + clsVehicle.mstVer + ", "
				//		+ clsVehicle.bctype + ", "
				//		+ clsVehicle.reserveDate + ", "
				//		+ clsVehicle.registrationMethod);

				updateReserveFlag(clsVehicle, "2", "SYSTEM", now);

				// 指示記号を削除をする
				// ※削除した指示記号が本番登録できるように本番削除フラグは落とさない。
				List<MBcsign> mBcsignList = new ArrayList<MBcsign>();
				SimpleWhere sWhere = null;
				if (clsVehicle.mstType == 1 && clsVehicle.registrationMethod.equals("0")) {
					sWhere = new SimpleWhere()
							.eq("mstVer", clsVehicle.mstVer)
							.eq("sopDeleteFlag", "1")
							.eq("mItemList.bctype", clsVehicle.bctype);
				}
				else if (clsVehicle.mstType == 2) {
					sWhere = new SimpleWhere()
							.eq("mstVer", clsVehicle.mstVer)
							.eq("sopDeleteFlag", "1")
							.eq("mItemList.bctype", clsVehicle.bctype)
							.le("reserveDate", clsVehicle.reserveDate);
				}
				if (sWhere != null) {
					mBcsignList = jdbcManager.from(MBcsign.class).innerJoin("mItemList")
							.where(sWhere)
							.getResultList();

					for (MBcsign mBcsign : mBcsignList) {
						// 本番削除フラグを落とすと、この後のストアドで仮マスタで削除した指示記号が本番マスタから削除されないため、本番削除フラグを落とさないように対応
						mBcsign.deleteFlag = "1";
						jdbcManager.update(mBcsign).execute();
					}
				}

				// 仮マスタ本番登録処理（ストアドファンクション）を実行する
				if (clsVehicle.mstType == 1) {

					int ret = storedFunctionService.tempMstRegist(clsVehicle.mstVer, clsVehicle.bctype, clsVehicle.registrationMethod, null, null, false);
					if (ret == -1) {
						tx.setRollbackOnly();
						break;
					}
				}
				else if (clsVehicle.mstType == 2) {

					int ret = storedFunctionService.tempMstRegist(clsVehicle.mstVer, clsVehicle.bctype, "5", null, clsVehicle.reserveDate, false);
					if (ret == -1) {
						tx.setRollbackOnly();
						break;
					}
				}

				// 指示記号を削除をする
				// ※本番登録が終わったので本番削除フラグを落とす。
				for (MBcsign mBcsign : mBcsignList) {

					mBcsign.sopDeleteFlag = "0";

					// すでに削除フラグが立っていることで、この後で行う予約フラグの更新がされないので、ここで予約フラグの更新も行う。
					if (mBcsign.reserveFlag.equals("2")) {
						mBcsign.reserveFlag = "3";
					}

					mBcsign.updateUser = "SYSTEM";
					mBcsign.updateDate = now;
					jdbcManager.update(mBcsign).execute();
				}

				updateReserveFlag(clsVehicle, "3", "SYSTEM", now);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			try {
				// トランザクションをロールバック
				tx.setRollbackOnly();
			}
			catch (IllegalStateException e1) {
				e1.printStackTrace();
			}
			catch (SystemException e2) {
				e2.printStackTrace();
			}
		}
		finally {
			try {
				if (tx.getStatus() == Status.STATUS_ACTIVE) {
					// コミット
					tx.commit();
				}
				else {
					// トランザクションをロールバック
					tx.rollback();
					sendNotifyMail(errClsVehicle);
				}
			}
			catch (Exception e3) {
				e3.printStackTrace();
			}
		}
	}

	/**
	 * 予約フラグを更新する
	 *
	 * @param clsVehicle
	 *            車種情報
	 * @param reserveFlag
	 *            予約フラグ（"2"(実行中)または"3"(実行完了)）
	 * @param updateUser
	 *            更新者
	 * @param updateDate
	 *            更新日
	 */
	public void updateReserveFlag(ClsVehicle clsVehicle, String reserveFlag, String updateUser, Timestamp updateDate) throws Exception
	{
		// 更新対象の予約フラグを決める
		String beforeReserveFlag = null;
		if (reserveFlag.equals("2")) {
			beforeReserveFlag = "1";
		}
		else if (reserveFlag.equals("3")) {
			beforeReserveFlag = "2";
		}
		else {
			return;
		}

		String sql = "";
		if (clsVehicle.mstType == 1) {
			// 車種
			sql += " UPDATE m_reserve";
			sql += " SET reserve_flag = ?, update_user = ?, update_date = ?";
			sql += " WHERE (mst_ver, bctype) IN ";
			sql += " (select T1_.mst_ver, T1_.bctype";
			sql += " from M_RESERVE T1_ inner join M_VEHICLE T2_ on T2_.bctype = T1_.bctype";
			sql += " where (T2_.end_of_prod = false AND T1_.reserve_flag = ? AND T1_.mst_ver = ? AND T1_.bctype = ?))";

			jdbcManager.updateBySql(sql, String.class, String.class, Timestamp.class, String.class, Integer.class, String.class)
					.params(reserveFlag, updateUser, updateDate, beforeReserveFlag, clsVehicle.mstVer, clsVehicle.bctype)
					.execute();

			// 指示記号
			if (clsVehicle.registrationMethod.equals("0")) {
				sql = " UPDATE m_bcsign";
				sql += " SET reserve_flag = ?, update_user = ?, update_date = ?";
				sql += " WHERE (mst_ver, sign_code) IN ";
				sql += " (select T1_.mst_ver, T1_.sign_code";
				sql += " from M_BCSIGN T1_ inner join M_ITEM T2_ on T2_.mst_ver = T1_.mst_ver and T2_.item_code = T1_.item_code";
				sql += " where (T1_.reserve_flag = ? AND T1_.delete_flag = '0' AND T2_.delete_flag = '0' AND T1_.mst_ver = ? AND T2_.bctype = ?))";

				jdbcManager.updateBySql(sql, String.class, String.class, Timestamp.class, String.class, Integer.class, String.class)
						.params(reserveFlag, updateUser, updateDate, beforeReserveFlag, clsVehicle.mstVer, clsVehicle.bctype)
						.execute();
			}
		}
		else if (clsVehicle.mstType == 2) {
			// 指示記号
			sql += " UPDATE m_bcsign";
			sql += " SET reserve_flag = ?, update_user = ?, update_date = ?";
			sql += " WHERE (mst_ver, sign_code) IN ";
			sql += " (select T1_.mst_ver, T1_.sign_code";
			sql += " from M_BCSIGN T1_ inner join M_ITEM T2_ on T2_.mst_ver = T1_.mst_ver and T2_.item_code = T1_.item_code";
			sql += " where (T1_.reserve_flag = ? AND T1_.delete_flag = '0' AND T2_.delete_flag = '0' AND T1_.mst_ver = ? AND T2_.bctype = ? AND T1_.reserve_date <= ?))";

			jdbcManager.updateBySql(sql, String.class, String.class, Timestamp.class, String.class, Integer.class, String.class, Timestamp.class)
					.params(reserveFlag, updateUser, updateDate, beforeReserveFlag, clsVehicle.mstVer, clsVehicle.bctype, clsVehicle.reserveDate)
					.execute();
		}
	}

	/**
	 * 本番予約マスタ取得する 本番予約データを取得する際、車種マスタ（m_vehicleテーブル）を結合します。 指示記号マスタ取得する
	 * 指示記号データを取得する際、項目マスタ（m_itemテーブル）を結合します。
	 *
	 * @param now 処理日時
	 * @return 車種一覧データのList
	 */
	public List<ClsVehicle> getVehicleList(Timestamp now)
	{
		List<ClsVehicle> clsVehicleList = new ArrayList<ClsVehicle>();

		// 車種
		List<MReserve> mReserveList = jdbcManager.from(MReserve.class)
				.innerJoin("mVehicle")
				.where(new SimpleWhere()
						.eq("reserveFlag", "1")
						.le("reserveDate", now)
						.eq("mVehicle.endOfProd", false))
				.orderBy("reserveDate, mstVer, bctype ASC")
				.getResultList();

		for (MReserve mReserve : mReserveList) {
			ClsVehicle clsVehicle = new ClsVehicle();
			clsVehicle.mstVer = mReserve.mstVer;
			clsVehicle.bctype = mReserve.bctype;
			clsVehicle.reserveDate = mReserve.reserveDate;
			clsVehicle.registrationMethod = mReserve.registrationMethod;
			clsVehicle.mstType = 1;
			clsVehicleList.add(clsVehicle);
		}

		// 指示記号
		List<MBcsign> mBcsignList = jdbcManager.from(MBcsign.class)
				.innerJoin("mItemList")
				.where(new SimpleWhere()
						.eq("reserveFlag", "1")
						.le("reserveDate", now)
						.eq("deleteFlag", "0")
						.eq("mItemList.deleteFlag", "0"))
				.orderBy("mItemList.bctype, reserveDate DESC, mstVer DESC")
				.getResultList();

		for (MBcsign mBcsign : mBcsignList) {
			ClsVehicle clsVehicle = new ClsVehicle();
			clsVehicle.mstVer = mBcsign.mstVer;
			clsVehicle.bctype = mBcsign.mItemList.get(0).bctype;
			clsVehicle.reserveDate = mBcsign.reserveDate;
			clsVehicle.registrationMethod = null;
			clsVehicle.mstType = 2;
			clsVehicleList.add(clsVehicle);
		}

		// ソート（車種・予約日時）
		Collections.sort(clsVehicleList, new ClsVehicleMergeComparator());

		List<ClsVehicle> clsVehicleMergeList = new ArrayList<ClsVehicle>();
		Integer mstVer = 0;
		String bctype = null;
		Integer mstType = 0;
		for (ClsVehicle clsVehicle : clsVehicleList) {
			if (clsVehicle.mstVer.equals(mstVer) == false
					|| StringUtils.equals(clsVehicle.bctype, bctype) == false
					|| clsVehicle.mstType.equals(mstType) == false) {

				clsVehicleMergeList.add(clsVehicle);

				mstVer = clsVehicle.mstVer;
				bctype = clsVehicle.bctype;
				mstType = clsVehicle.mstType;
			}
		}

		// ソート（予約日時）
		Collections.sort(clsVehicleMergeList, new ClsVehicleComparator());

		return clsVehicleMergeList;
	}

	/**
	 * 予約中であるか判定をする。（すべての車種または車種に紐付く指示記号が予約中の場合）
	 *
	 * @param selectMst
	 *            対象の仮マスタ
	 * @return 判定結果（true:予約中、false:予約中なし）
	 */
	public Boolean isReserved(final Integer selectMst)
	{
		Boolean result = false;

		result = this.mReserveService.getReserveFlag(selectMst, null, ReservationState.RESERVATION);
		if (result) {
			return result;
		}

		result = this.mBcsignService.getReserveFlag(selectMst, null, null, ReservationState.RESERVATION);
		if (result) {
			return result;
		}

		return result;
	}

	/**
	 * 予約中であるか判定をする。（車種または車種に紐付く指示記号が予約中の場合）
	 *
	 * @param bctypeList
	 *            対象のBC車種区分
	 * @return 判定結果（true:予約中、false:予約中なし）
	 */
	public Boolean isReservedVehicle(final ArrayList<String> bctypeList)
	{
		Boolean result = false;

		List<Integer> codeList = MstSelectService.getTempMasterCodeList();
		for (String bctype : bctypeList) {
			for (Integer selectMst : codeList) {

				result = this.mReserveService.getReserveFlag(selectMst, bctype, ReservationState.RESERVATION);
				if (result) {
					return result;
				}

				result = this.mBcsignService.getReserveFlag(selectMst, bctype, null, ReservationState.RESERVATION);
				if (result) {
					return result;
				}
			}
		}

		return result;
	}

	/**
	 * 予約中であるか判定をする。（車種または車種に紐付く指示記号が予約中の場合）
	 *
	 * @param selectMst
	 *            対象の仮マスタ
	 * @param bctype
	 *            対象のBC車種区分
	 * @return 判定結果（true:予約中、false:予約中なし）
	 */
	public Boolean isReservedVehicle(final Integer selectMst, final String bctype)
	{
		Boolean result = false;

		result = this.mReserveService.getReserveFlag(selectMst, bctype, ReservationState.RESERVATION);
		if (result) {
			return result;
		}

		result = this.mBcsignService.getReserveFlag(selectMst, bctype, null, ReservationState.RESERVATION);
		if (result) {
			return result;
		}

		return result;
	}

	/**
	 * 予約中であるか判定をする。（車種または指示記号が予約中の場合）
	 *
	 * @param selectMst
	 *            対象の仮マスタ
	 * @param bctype
	 *            対象のBC車種区分
	 * @param signCodeList
	 *            対象の指示記号Codeの一覧
	 * @return 判定結果（true:予約中、false:予約中なし）
	 */
	public Boolean isReservedVehicleSign(final Integer selectMst, final String bctype, final ArrayList<String> signCodeList)
	{
		Boolean result = false;

		result = this.mReserveService.getReserveFlag(selectMst, bctype, ReservationState.RESERVATION);
		if (result) {
			return result;
		}

		result = this.mBcsignService.getReserveFlagSign(selectMst, bctype, signCodeList, ReservationState.RESERVATION);
		if (result) {
			return result;
		}

		return result;
	}

	/**
	 * 予約中であるか判定をする。（車種または項目コードに紐付く指示記号が予約中の場合）
	 *
	 * @param selectMst
	 *            対象の仮マスタ
	 * @param bctype
	 *            対象のBC車種区分
	 * @param itemCodeList
	 *            対象の項目Codeの一覧
	 * @return 判定結果（true:予約中、false:予約中なし）
	 */
	public Boolean isReservedVehicleItem(final Integer selectMst, final String bctype, final ArrayList<String> itemCodeList)
	{
		Boolean result = false;

		result = this.mReserveService.getReserveFlag(selectMst, bctype, ReservationState.RESERVATION);
		if (result) {
			return result;
		}

		result = this.mBcsignService.getReserveFlag(selectMst, bctype, itemCodeList, ReservationState.RESERVATION);
		if (result) {
			return result;
		}

		return result;
	}

	/**
	 * 予約中であるか判定をする。（車種または工程コードに紐付く指示記号が予約中の場合）
	 *
	 * @param selectMst
	 *            対象の仮マスタ
	 * @param bctype
	 *            対象のBC車種区分
	 * @param groupCode
	 *            対象の工程コード
	 * @return 判定結果（true:予約中、false:予約中なし）
	 */
	public Boolean isReservedVehicleGroup(final Integer selectMst, final String bctype, final String groupCode)
	{
		Boolean result = false;

		result = this.mReserveService.getReserveFlag(selectMst, bctype, ReservationState.RESERVATION);
		if (result) {
			return result;
		}

		result = this.mBcsignService.getReserveFlagGroup(selectMst, bctype, groupCode, ReservationState.RESERVATION);
		if (result) {
			return result;
		}

		return result;
	}

	/**
	 * 本番予約が実行中であるか判定をする。（車種または車種に紐付く指示記号が予約中の場合）
	 *
	 * @param selectMst
	 *            対象の仮マスタ
	 * @param bctype
	 *            対象のBC車種区分
	 * @return 判定結果（true:予約実行中、false:予約未実行）
	 */
	public Boolean isReservedExecutionVehicle(final Integer selectMst, final String bctype)
	{
		Boolean result = false;

		result = this.mReserveService.getReserveFlag(selectMst, bctype, ReservationState.EXECUTION);
		if (result) {
			return result;
		}

		result = this.mBcsignService.getReserveFlag(selectMst, bctype, null, ReservationState.EXECUTION);
		if (result) {
			return result;
		}

		return result;
	}

	/**
	 * 本番予約が実行中であるか判定をする。（車種または指示記号が実行中の場合）
	 *
	 * @param selectMst
	 *            対象の仮マスタ
	 * @param bctype
	 *            対象のBC車種区分
	 * @param signCodeList
	 *            対象の指示記号Codeの一覧
	 * @return 判定結果（true:予約実行中、false:予約未実行）
	 */
	public Boolean isReservedExecutionVehicleSign(final Integer selectMst, final String bctype, final ArrayList<String> signCodeList)
	{
		Boolean result = false;

		result = this.mReserveService.getReserveFlag(selectMst, bctype, ReservationState.EXECUTION);
		if (result) {
			return result;
		}

		result = this.mBcsignService.getReserveFlagSign(selectMst, bctype, signCodeList, ReservationState.EXECUTION);
		if (result) {
			return result;
		}

		return result;
	}

	/**
	 * 本番予約が実行中であるか判定をする。（車種または項目コードに紐付く指示記号が実行中の場合）
	 *
	 * @param selectMst
	 *            対象の仮マスタ
	 * @param bctype
	 *            対象のBC車種区分
	 * @param itemCodeList
	 *            対象の項目Codeの一覧
	 * @return 判定結果（true:予約実行中、false:予約未実行）
	 */
	public Boolean isReservedExecutionVehicleItem(final Integer selectMst, final String bctype, final ArrayList<String> itemCodeList)
	{
		Boolean result = false;

		result = this.mReserveService.getReserveFlag(selectMst, bctype, ReservationState.EXECUTION);
		if (result) {
			return result;
		}

		result = this.mBcsignService.getReserveFlag(selectMst, bctype, itemCodeList, ReservationState.EXECUTION);
		if (result) {
			return result;
		}

		return result;
	}

	/**
	 * 予約中の従業員を取得する。（車種または車種に紐付く指示記号が予約中の場合）
	 *
	 * @param selectMst
	 *            対象の仮マスタ
	 * @param bctype
	 *            対象のBC車種区分
	 * @return 従業員一覧（null以外:予約中の従業員一覧、null:予約中なし）
	 */
	public ArrayList<String> getReservationUserVehicle(final Integer selectMst, final String bctype)
	{
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> workList = null;

		workList = this.mReserveService.getReservationUserVehicle(selectMst, bctype, ReservationState.RESERVATION);
		list.addAll(workList);

		workList = this.mBcsignService.getReservationUserVehicle(selectMst, bctype, null, ReservationState.RESERVATION);
		for (String workData : workList) {
			if (list.contains(workData) == false) {
				list.add(workData);
			}
		}

		if (list == null || list.size() == 0) {
			return null;
		}

		return list;
	}

	/**
	 * 予約中の従業員を取得する。（車種または指示記号が予約中の場合）
	 *
	 * @param selectMst
	 *            対象の仮マスタ
	 * @param bctype
	 *            対象のBC車種区分
	 * @param signCodeList
	 *            対象の指示記号Codeの一覧
	 * @return 従業員一覧（null以外:予約中の従業員一覧、null:予約中なし）
	 */
	public ArrayList<String> getReservationUserVehicleSign(final Integer selectMst, final String bctype, final ArrayList<String> signCodeList)
	{
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> workList = null;

		workList = this.mReserveService.getReservationUserVehicle(selectMst, bctype, ReservationState.RESERVATION);
		list.addAll(workList);

		workList = this.mBcsignService.getReservationUserVehicleSign(selectMst, bctype, signCodeList, ReservationState.RESERVATION);
		for (String workData : workList) {
			if (list.contains(workData) == false) {
				list.add(workData);
			}
		}

		if (list == null || list.size() == 0) {
			return null;
		}

		return list;
	}

	/**
	 * 予約中の従業員を取得する。（車種または項目コードに紐付く指示記号が予約中の場合）
	 *
	 * @param selectMst
	 *            対象の仮マスタ
	 * @param bctype
	 *            対象のBC車種区分
	 * @param itemCodeList
	 *            対象の項目Codeの一覧
	 * @return 従業員一覧（null以外:予約中の従業員一覧、null:予約中なし）
	 */
	public ArrayList<String> getReservationUserVehicleItem(final Integer selectMst, final String bctype, final ArrayList<String> itemCodeList)
	{
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> workList = null;

		workList = this.mReserveService.getReservationUserVehicle(selectMst, bctype, ReservationState.RESERVATION);
		list.addAll(workList);

		workList = this.mBcsignService.getReservationUserVehicle(selectMst, bctype, itemCodeList, ReservationState.RESERVATION);
		for (String workData : workList) {
			if (list.contains(workData) == false) {
				list.add(workData);
			}
		}

		if (list == null || list.size() == 0) {
			return null;
		}

		return list;
	}

	/**
	 * 車種が予約中であるか判定をする。（車種が予約中の場合）
	 *
	 * @param selectMst
	 *            対象の仮マスタ
	 * @param bctype
	 *            対象のBC車種区分
	 * @return 判定結果（true:予約中、false:予約中なし）
	 */
	public Boolean isReservedMVehicle(final Integer selectMst, final String bctype)
	{
		Boolean result = false;

		result = this.mReserveService.getReserveFlag(selectMst, bctype, ReservationState.RESERVATION);
		if (result) {
			return result;
		}

		return result;
	}

	/**
	 * 指示記号が予約中であるか判定をする。（車種に紐付く指示記号が予約中の場合）
	 *
	 * @param selectMst
	 *            対象の仮マスタ
	 * @param bctype
	 *            対象のBC車種区分
	 * @return 判定結果（true:予約中、false:予約中なし）
	 */
	public Boolean isReservedMBcsign(final Integer selectMst, final String bctype)
	{
		Boolean result = false;

		result = this.mBcsignService.getReserveFlag(selectMst, bctype, null, ReservationState.RESERVATION);
		if (result) {
			return result;
		}

		return result;
	}

	/**
	 * 指示記号が予約中であるか判定をする。（指示記号が予約中の場合）
	 *
	 * @param selectMst
	 *            対象の仮マスタ
	 * @param bctype
	 *            対象のBC車種区分
	 * @param signCodeList
	 *            対象の指示記号Codeの一覧
	 * @return 判定結果（true:予約中、false:予約中なし）
	 */
	public Boolean isReservedMBcsignSign(final Integer selectMst, final String bctype, final ArrayList<String> signCodeList)
	{
		Boolean result = false;

		result = this.mBcsignService.getReserveFlagSign(selectMst, bctype, signCodeList, ReservationState.RESERVATION);
		if (result) {
			return result;
		}

		return result;
	}

	/**
	 * 指示記号が予約中であるか判定をする。（指示記号が予約中のみの場合）
	 *
	 * @param selectMst
	 *            対象の仮マスタ
	 * @param bctype
	 *            対象のBC車種区分
	 * @param signCodeList
	 *            対象の指示記号Codeの一覧
	 * @return 判定結果（true:予約中、false:予約中なし）
	 */
	public Boolean isReservedMBcsignSignOnly(final Integer selectMst, final String bctype, final ArrayList<String> signCodeList)
	{
		Boolean result = false;

		result = this.mBcsignService.getReserveFlagSign(selectMst, bctype, signCodeList, ReservationState.RESERV_ONLY);
		if (result) {
			return result;
		}

		return result;
	}

	/**
	 * 車種が予約中の予約情報を取得する。（車種が予約中のみの場合）
	 *
	 * @param selectMst
	 *            対象の仮マスタ
	 * @param bctype
	 *            対象のBC車種区分
	 * @return 予約情報（null以外:予約中の予約情報、null:予約中なし）
	 */
	public ArrayList<String> getReservationInformationMVehicle(final Integer selectMst, final String bctype)
	{
		ArrayList<String> list = new ArrayList<String>();

		list = this.mReserveService.getReservationInformationMVehicle(selectMst, bctype, ReservationState.RESERV_ONLY);
		if (list == null || list.size() == 0) {
			return null;
		}

		return list;
	}

	/**
	 * 指示記号が予約中の予約情報を取得する。（指示記号が予約中の場合）
	 *
	 * @param selectMst
	 *            対象の仮マスタ
	 * @param bctype
	 *            対象のBC車種区分
	 * @param signCodeList
	 *            対象の指示記号Codeの一覧
	 * @return 予約情報（null以外:予約中の予約情報、null:予約中なし）
	 */
	public ArrayList<String> getReservationInformationMBcsignSign(final Integer selectMst, final String bctype, final ArrayList<String> signCodeList)
	{
		ArrayList<String> list = new ArrayList<String>();

		list = this.mBcsignService.getReservationInformationMVehicleSign(selectMst, bctype, signCodeList, ReservationState.RESERVATION);
		if (list == null || list.size() == 0) {
			return null;
		}

		return list;
	}

	/**
	 * 指示記号が予約中の予約情報を取得する。（項目コードに紐付く指示記号が予約中の場合）
	 *
	 * @param selectMst
	 *            対象の仮マスタ
	 * @param bctype
	 *            対象のBC車種区分
	 * @param itemCodeList
	 *            対象の項目Codeの一覧
	 * @return 予約情報（null以外:予約中の予約情報、null:予約中なし）
	 */
	public ArrayList<String> getReservationInformationMBcsign(final Integer selectMst, final String bctype, final ArrayList<String> itemCodeList)
	{
		ArrayList<String> list = new ArrayList<String>();

		list = this.mBcsignService.getReservationInformationMVehicle(selectMst, bctype, itemCodeList, ReservationState.RESERVATION);
		if (list == null || list.size() == 0) {
			return null;
		}

		return list;
	}

	/**
	 * エラーメール送信
	 * @param clsVehicle
	 *            処理エラーとなった車種情報
	 */
	private void sendNotifyMail(ClsVehicle clsVehicle)
	{
		if (clsVehicle == null) {
			return;
		}

		// メール送信コマンド
		String scriptFile = (String) SingletonS2ContainerFactory.getContainer().getComponent("reserveErrorNotifyMail");
		File notify = ResourceUtil.getResourceAsFile(scriptFile);
		// コマンド実行
		try {
			ProcessBuilder pb = new ProcessBuilder();
			pb.command("cmd", "/c", notify.getAbsolutePath(), clsVehicle.mstVer.toString(), clsVehicle.bctype, clsVehicle.reserveDate.toString()).start();
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 予約バッチ処理用クラス
	 *
	 */
	private class ClsVehicle
	{
		// マスタ選択（マスターバージョン）
		public Integer mstVer;
		// 車種
		public String bctype;
		// 予約日時
		public Timestamp reserveDate;
		// 本番登録（0:全体、1:検査順のみ、null:指示記号の場合）
		public String registrationMethod;
		// マスタ区分（1:仮マスタ本番、2:指示記号）
		public Integer mstType;
	}

	/**
	 * マージ用に予約クラス並び替え処理
	 */
	private static final class ClsVehicleMergeComparator implements Comparator<ClsVehicle>
	{
		@Override
		public int compare(ClsVehicle o1, ClsVehicle o2)
		{
			int ret = 0;

			// 車種で昇順
			if (o1.bctype == null && o2.bctype == null) {
				ret = 0;
			}
			else if (o1.bctype == null) {
				ret = -1;
			}
			else if (o2.bctype == null) {
				ret = 1;
			}
			else {
				ret = o1.bctype.compareTo(o2.bctype);
			}

			// 予約日時で降順
			if (ret == 0) {
				if (o1.reserveDate == null && o2.reserveDate == null) {
					ret = 0;
				}
				else if (o1.reserveDate == null) {
					ret = 1;
				}
				else if (o2.reserveDate == null) {
					ret = -1;
				}
				else {
					ret = o2.reserveDate.compareTo(o1.reserveDate);
				}
			}

			// マスタ選択で昇順（仮1→仮0の順に並べるため）
			if (ret == 0) {
				if (o1.mstVer == null && o2.mstVer == null) {
					ret = 0;
				}
				else if (o1.mstVer == null) {
					ret = -1;
				}
				else if (o2.mstVer == null) {
					ret = 1;
				}
				else {
					ret = o1.mstVer.compareTo(o2.mstVer);
				}
			}

			return ret;
		}
	}

	/**
	 * 予約クラス並び替え処理
	 */
	private static final class ClsVehicleComparator implements Comparator<ClsVehicle>
	{
		@Override
		public int compare(ClsVehicle o1, ClsVehicle o2)
		{
			int ret = 0;

			// 予約日時で昇順
			if (o1.reserveDate == null && o2.reserveDate == null) {
				ret = 0;
			}
			else if (o1.reserveDate == null) {
				ret = -1;
			}
			else if (o2.reserveDate == null) {
				ret = 1;
			}
			else {
				ret = o1.reserveDate.compareTo(o2.reserveDate);
			}

			// マスタ選択で降順（仮0→仮1の順に並べるため）
			if (ret == 0) {
				if (o1.mstVer == null && o2.mstVer == null) {
					ret = 0;
				}
				else if (o1.mstVer == null) {
					ret = 1;
				}
				else if (o2.mstVer == null) {
					ret = -1;
				}
				else {
					ret = o2.mstVer.compareTo(o1.mstVer);
				}
			}

			return ret;
		}
	}

	/**
	 * 仮マスタ本番登録から予約中状態を取得する
	 *
	 * @param mstVer
	 *            マスタバージョン
	 * @param bctype
	 *            BC車種区分コード
	 * @return 判定結果（1:予約実行中、2:予約中の従業員あり、0:上記以外）
	 */
	public Object getReserveStatus(final Integer selectMst, final String bctype)
	{
		if (isReservedExecutionVehicle(selectMst, bctype)) {
			// 予約実行中
			return "1";
		}
		else {
			ArrayList<String> list = getReservationUserVehicle(selectMst, bctype);
			if (list != null && list.size() > 0) {
				// 予約中の従業員あり
				return list;
			}
			else {
				return "0";
			}
		}
	}

	/**
	 * 仮マスタ本番メンテ画面より、予約日時を設定する。
	 *
	 * @param selectMst
	 *            対象の仮マスタ
	 * @param bctype
	 *            対象のBC車種区分
	 * @param userCode
	 *            更新ユーザーID
	 * @param reserveDate
	 *            予約日時
	 * @param registType
	 *            本番登録方法
	 *
	 * @return 処理結果（1:本番予約が実行中、list:本番予約中の従業員一覧、0：成功）
	 */
	public Object registReserveDateFromTempMst(final int selectMst, final String bctype, final String userCode, final String reserveDate, final String registType)
	{
		// 本番予約の実行中チェック
		if (isReservedExecutionVehicle(selectMst, bctype)) {
			// 予約実行中
			return "1";
		}

		ArrayList<String> list = getReservationUserVehicle(selectMst, bctype);
		if (list != null && list.size() > 0) {
			// 予約中の従業員あり
			return list;
		}

		// マスタ存在チェック
		if (isTemporaryNoMaster(selectMst, bctype)) {
			return "4";
		}
		if (StringUtils.equals(registType, "1")) {
			// 検査順は本番マスタもチェックする
			if (isRealNoMaster(bctype)) {
				return "4";
			}
		}

		updateMReserve(selectMst, bctype, userCode, reserveDate, registType);

		return "0";
	}

	/**
	 * 本番予約マスタ登録・更新
	 *
	 * @param selectMst
	 *            対象の仮マスタ
	 * @param bctype
	 *            対象のBC車種区分
	 * @param userCode
	 *            更新ユーザーID
	 * @param reserveDate
	 *            予約日時
	 * @param registType
	 *            本番登録方法
	 *
	 */
	@TransactionAttribute(TransactionAttributeType.NEVER)
	private void updateMReserve(int selectMst, String bctype, String userCode, String reserveDate, String registrationMethod)
	{
		try {
			// トランザクションの開始
            tx.begin();

			MReserve mReserve = jdbcManager.from(MReserve.class).where(new SimpleWhere()
					.eq("mstVer", selectMst)
					.eq("bctype", bctype))
					.getSingleResult();
			if (mReserve == null) {
				mReserve = new MReserve();
				// マスタバージョン
				mReserve.mstVer = selectMst;
				// BC車種区分コード
				mReserve.bctype = bctype;
				// 予約フラグ
				mReserve.reserveFlag = "1";
				// 予約者
				mReserve.reserveUser = userCode;
				// 予約日
				long lngReserveDate = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(reserveDate).getTime();
				mReserve.reserveDate = new Timestamp(lngReserveDate);
				// 本番登録方法
				mReserve.registrationMethod = registrationMethod;
				// 作成者
				mReserve.insertUser = userCode;
				// 更新者
				mReserve.updateUser = null;
				// 作成日
				mReserve.insertDate = Utils.nowts();
				// 更新日
				mReserve.updateDate = null;
				jdbcManager.insert(mReserve).execute();
			}
			else {
				// 予約フラグ
				mReserve.reserveFlag = "1";
				// 予約者
				mReserve.reserveUser = userCode;
				// 予約日
				long lngReserveDate = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(reserveDate).getTime();
				mReserve.reserveDate = new Timestamp(lngReserveDate);
				// 本番登録方法
				mReserve.registrationMethod = registrationMethod;
				// 更新者
				mReserve.updateUser = userCode;
				// 更新日
				mReserve.updateDate = Utils.nowts();
				jdbcManager.update(mReserve).execute();
			}

			// 車種マスタの更新者を更新する
			MVehicle mVehicle = jdbcManager.from(MVehicle.class)
					.where(new SimpleWhere()
							.eq("bctype", bctype))
					.getSingleResult();
			if (mVehicle != null) {
				mVehicle.updateUser = userCode;
				mVehicle.updateDate = Utils.nowts();
				jdbcManager.update(mVehicle).execute();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			try {
				// トランザクションをロールバック
				tx.setRollbackOnly();
			}
			catch (IllegalStateException e1) {
				e1.printStackTrace();
			}
			catch (SystemException e2) {
				e2.printStackTrace();
			}
		}
		finally {
			try {
				if (tx.getStatus() == Status.STATUS_ACTIVE) {
					// コミット
					tx.commit();
				}
				else {
					// トランザクションをロールバック
					tx.rollback();
				}
			}
			catch (Exception e3) {
				e3.printStackTrace();
			}
		}
	}

	/**
	 * 指示記号メンテ画面より、予約日時を設定する。
	 *
	 * @param selectMst
	 *            対象の仮マスタ
	 * @param bctype
	 *            対象のBC車種区分
	 * @param signCodeList
	 *            対象の指示記号Codeの一覧
	 * @param userCode
	 *            更新ユーザーID
	 * @param reserveDate
	 *            予約日時
	 * @param registType
	 *            本番登録方法
	 *
	 * @return 処理結果（1:本番予約が実行中、list:本番予約中の従業員一覧、0：成功）
	 */
	public Object registReserveDateFromSign(final int selectMst, final String bctype, final ArrayList<String> signCodeList, final String userCode, final String reserveDate)
	{
		// 本番予約の実行中チェック
		// 2016/09/09 DA upd start
//		if (isReservedExecutionVehicleItem(selectMst, bctype, itemCodeList)) {
//			// 予約実行中
//			return "1";
//		}
//
//		ArrayList<String> list = getReservationUserVehicleItem(selectMst, bctype, itemCodeList);
//		if (list != null && list.size() > 0) {
//			// 予約中の従業員あり
//			return list;
//		}
		if (isReservedExecutionVehicleSign(selectMst, bctype, signCodeList)) {
			// 予約実行中
			return "1";
		}

		ArrayList<String> list = getReservationUserVehicleSign(selectMst, bctype, signCodeList);
		if (list != null && list.size() > 0) {
			// 予約中の従業員あり
			return list;
		}
		// 2016/09/09 DA upd end

		// マスタ存在チェック
		if (isTemporaryNoMaster(selectMst, bctype)) {
			return "4";
		}
		if (isRealNoMaster(bctype)) {
			return "4";
		}

		updateMbcsign(selectMst, bctype, signCodeList, userCode, reserveDate);

		return "0";
	}

	/**
	 * 指示記号マスタ更新
	 *
	 * @param selectMst
	 *            対象の仮マスタ
	 * @param bctype
	 *            対象のBC車種区分
	 * @param signCodeList
	 *            対象の指示記号Codeの一覧
	 * @param userCode
	 *            更新ユーザーID
	 * @param reserveDate
	 *            予約日時
	 *
	 */
	private void updateMbcsign(int selectMst, String bctype, ArrayList<String> signCodeList, String userCode, String reserveDate)
	{
		try {

			List<MBcsign> list = jdbcManager.from(MBcsign.class)
					.where(new SimpleWhere()
							.eq("mstVer", selectMst)
							.in("signCode", signCodeList)
							.eq("deleteFlag", 0))
					.getResultList();

			for (MBcsign mBcsign : list) {
				mBcsign.reserveFlag = "1";
				mBcsign.reserveUser = userCode;
				long lngReserveDate = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(reserveDate).getTime();
				mBcsign.reserveDate = new Timestamp(lngReserveDate);
				mBcsign.updateUser = userCode;
				mBcsign.updateDate = Utils.nowts();
				jdbcManager.update(mBcsign).execute();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 予約を解除する
	 *
	 * @param loginUser
	 *            ログインID
	 * @param mstVer
	 *            マスタバージョン
	 * @param bctype
	 *            BC車種区分コード
	 * @return 判定結果（1:解除失敗、0:解除成功）
	 */
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public String updateTempMstReserveCancel(final String loginUser, final Integer selectMst, final String bctype)
	{
		ArrayList<String> list = getReservationInformationMVehicle(selectMst, bctype);
		if (list == null || list.size() < 1) {
			return "1";
		}

		try {
			// トランザクションの開始
			tx.begin();

			// 本番予約のデータを取得
			MReserve mReserve = jdbcManager.from(MReserve.class)
					.where(new SimpleWhere()
							.eq("mstVer", selectMst)
							.eq("bctype", bctype))
					.getSingleResult();

			mReserve.reserveFlag = "0";
			mReserve.reserveUser = null;
			mReserve.reserveDate = null;
			mReserve.updateUser = loginUser;
			mReserve.updateDate = Utils.nowts();

			jdbcManager.update(mReserve).execute();

			// 車種マスタの更新者を更新する
			MVehicle mVehicle = jdbcManager.from(MVehicle.class)
					.where(new SimpleWhere()
							.eq("bctype", bctype))
					.getSingleResult();
			if (mVehicle != null) {
				mVehicle.updateUser = loginUser;
				mVehicle.updateDate = Utils.nowts();
				jdbcManager.update(mVehicle).execute();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			try {
				// トランザクションをロールバック
				tx.setRollbackOnly();
			}
			catch (IllegalStateException e1) {
				e1.printStackTrace();
			}
			catch (SystemException e2) {
				e2.printStackTrace();
			}
		}
		finally {
			try {
				if (tx.getStatus() == Status.STATUS_ACTIVE) {
					// コミット
					tx.commit();
				}
				else {
					// トランザクションをロールバック
					tx.rollback();
				}
			}
			catch (Exception e3) {
				e3.printStackTrace();
			}
		}

		Properties props = ResourceUtil.getProperties("application_ja.properties");
		this.logger.info(MessageFormat.format(props.getProperty("svr0000001"), loginUser, bctype, list.get(0)));

		return "0";
	}

	/**
	 * 指示記号マスタメンテナンスから予約中状態を取得する
	 *
	 * @param mstVer
	 *            マスタバージョン
	 * @param bctype
	 *            BC車種区分コード
	 * @param signCodeList
	 *            対象の指示記号Codeの一覧
	 *
	 * @return 判定結果（1:予約実行中、2:予約中の従業員あり、0:上記以外）
	 */
	public Object getBcsignReserveStatus(final Integer selectMst, final String bctype, final ArrayList<String> signCodeList)
	{
		if (isReservedExecutionVehicleSign(selectMst, bctype, signCodeList)) {
			// 予約実行中
			return "1";
		}
		else {
			ArrayList<String> list = getReservationUserVehicleSign(selectMst, bctype, signCodeList);
			if (list != null && list.size() > 0) {
				// 予約中の従業員あり
				return list;
			}
			else {
				return "0";
			}
		}
	}

	/**
	 * 仮マスタ本番登録処理する
	 * ※指示記号マスタメンテナンス画面
	 * @param loginUser
	 *            ログインID
	 * @param mstVer
	 *            マスタバージョン
	 * @param bctype
	 *            BC車種区分コード
	 * @param signCodeList
	 *            対象の指示記号Codeの一覧
	 *
	 * @return 判定結果（1:予約実行中、2:ストアド異常、0:上記以外）
	 */
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public Object registBcsignRealReg(final String loginUser, final Integer selectMst, final String bctype, final ArrayList<String> signCodeList)
	{
		if (isReservedMVehicle(selectMst, bctype)) {
			// 予約実行中
			return "1";
		}

		if (isReservedMBcsignSign(selectMst, bctype, signCodeList)) {
			// 予約実行中
			return "1";
		}

		// マスタ存在チェック
		if (isTemporaryNoMaster(selectMst, bctype)) {
			return "4";
		}
		if (isRealNoMaster(bctype)) {
			return "4";
		}

		try {
			// トランザクションの開始
			tx.begin();

			// 本番削除をする
			// 指示記号
			List<MBcsign> mBcsignList = jdbcManager.from(MBcsign.class).innerJoin("mItemList")
					.where(new SimpleWhere()
							.eq("mstVer", selectMst)
							.eq("sopDeleteFlag", "1")
							.eq("mItemList.bctype", bctype)
							.in("signCode", signCodeList))
					.getResultList();

			for (MBcsign mBcsign : mBcsignList) {
				mBcsign.sopDeleteFlag = "0";
				mBcsign.deleteFlag = "1";
				mBcsign.updateUser = loginUser;
				mBcsign.updateDate = Utils.nowts();
				jdbcManager.update(mBcsign).execute();
			}

			String strSignCode = signCodeList.toString();
			if (strSignCode.length() > 0) {
				strSignCode = strSignCode.replace("[", "").replace("]", "");
			}

			// 仮マスタ本番登録処理（ストアドファンクション）を実行する
			// 登録種類 4:指示記号
			int ret = storedFunctionService.tempMstRegist(selectMst, bctype, "4", strSignCode, null, false);
			if (ret == -1) {
				tx.setRollbackOnly();
				return "2";
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			try {
				// トランザクションをロールバック
				tx.setRollbackOnly();
				return "2";
			}
			catch (IllegalStateException e1) {
				e1.printStackTrace();
				return "2";
			}
			catch (SystemException e2) {
				e2.printStackTrace();
				return "2";
			}
		}
		finally {
			try {
				//
				if (tx.getStatus() == Status.STATUS_ACTIVE) {
					// コミット
					tx.commit();
				}
				else {
					// トランザクションをロールバック
					tx.rollback();
					return "2";
				}
			}
			catch (Exception e3) {
				e3.printStackTrace();
				return "2";
			}
		}
		return "0";
	}

	/**
	 * 予約を解除する
	 *
	 * @param loginUser
	 *            ログインID
	 * @param mstVer
	 *            マスタバージョン
	 * @param bctype
	 *            BC車種区分コード
	 * @param signCodeList
	 *            対象の指示記号Codeの一覧
	 *
	 * @return 判定結果（1:解除失敗、0:解除成功）
	 */
	public String updateBcsignReserveCancel(final String loginUser, final Integer selectMst, final String bctype, final ArrayList<String> signCodeList)
	{

		ArrayList<String> list = getReservationInformationMBcsignSign(selectMst, bctype, signCodeList);
		if (list == null || list.size() < 1) {
			return "1";
		}

		// 予約解除をする
		// 指示記号
		List<MBcsign> mBcsignList = jdbcManager.from(MBcsign.class).innerJoin("mItemList")
				.where(new SimpleWhere()
						.eq("mstVer", selectMst)
						.eq("mItemList.bctype", bctype)
						.in("signCode", signCodeList))
				.getResultList();

		for (MBcsign mBcsign : mBcsignList) {
			mBcsign.reserveFlag = "0";
			mBcsign.reserveUser = null;
			mBcsign.reserveDate = null;
			mBcsign.updateUser = loginUser;
			mBcsign.updateDate = Utils.nowts();
			jdbcManager.update(mBcsign).execute();
		}

		Properties props = ResourceUtil.getProperties("application_ja.properties");
		this.logger.info(MessageFormat.format(props.getProperty("svr0000003"), loginUser, mBcsignList.size(), list.get(0)));

		return "0";
	}

	/**
	 * 存在していないマスタがあるか判定をする。
	 *
	 * @param selectMst 本番マスタ(1)か仮マスタ(0以下)かの指定
	 * @param mstVersion マスタバージョン
	 * @param bctype BC車種区分（nullの場合、全車種分を返す）
	 * @return 判定結果（true:存在しないマスタあり、false:なし）
	 */
	private Boolean isNoMaster(final Integer selectMst, final Integer mstVersion, final String bctype) {

		// マスタの選択
		String sopFlag = null;
		Integer mstVer = null;
		if (MstSelectService.isReal(selectMst)) {
			if (mstVersion == null || MstSelectService.isTemporary(mstVersion)) {
				sopFlag = "1";
			}
			else {
				mstVer = mstVersion;
			}
		}
		else {
			mstVer = selectMst;
		}

		// 工程
		long group_cnt = jdbcManager.from(MGroup.class)
				.where(new SimpleWhere()
						.eq("mstVer", mstVer)
						.eq("sopFlag", sopFlag)
						.eq("deleteFlag", "0")
						.eq("bctype", bctype))
				.getCount();
		if (group_cnt <= 0) {
			return true;
		}

		// 検査項目
		long item_cnt = jdbcManager.from(MItem.class)
				.where(new SimpleWhere()
						.eq("mstVer", mstVer)
						.eq("sopFlag", sopFlag)
						.eq("deleteFlag", "0")
						.eq("bctype", bctype))
				.getCount();
		if (item_cnt <= 0) {
			return true;
		}

		// 検査順
		long order_cnt = jdbcManager.from(MOrder.class)
				.innerJoin("mGroup")
				.where(new SimpleWhere()
						.eq("mstVer", mstVer)
						.eq("sopFlag", sopFlag)
						.eq("mGroup.bctype", bctype))
				.getCount();
		if (order_cnt <= 0) {
			return true;
		}

		// 指示記号
		long bcsign_cnt = jdbcManager.from(MBcsign.class)
				.innerJoin("mItem")
				.where(new SimpleWhere()
						.eq("mstVer", mstVer)
						.eq("sopFlag", sopFlag)
						.eq("deleteFlag", "0")
						.eq("mItem.bctype", bctype))
				.getCount();
		if (bcsign_cnt <= 0) {
			return true;
		}

		return false;
	}

	/**
	 * 存在していない仮マスタがあるか判定をする。
	 *
	 * @param selectMst
	 *            対象の仮マスタ
	 * @param bctype
	 *            対象のBC車種区分
	 * @return 判定結果（true:存在しないマスタあり、false:なし）
	 */
	public Boolean isTemporaryNoMaster(final Integer selectMst, final String bctype)
	{
		return isNoMaster(selectMst, null, bctype);
	}

	/**
	 * 存在していない本番マスタがあるか判定をする。
	 *
	 * @param bctype
	 *            対象のBC車種区分
	 * @return 判定結果（true:存在しないマスタあり、false:なし）
	 */
	public Boolean isRealNoMaster(final String bctype)
	{
		return isNoMaster(1, null, bctype);
	}
}
