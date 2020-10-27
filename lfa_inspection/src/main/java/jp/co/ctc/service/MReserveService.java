package jp.co.ctc.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.seasar.extension.jdbc.AutoSelect;
import org.seasar.extension.jdbc.where.ComplexWhere;

import jp.co.ctc.entity.MReserve;
import jp.co.ctc.service.MstRegistService.ReservationState;

/**
 * 本番予約マスタを扱うサービスです.
 *
 * @author DA 2016/02/24
 */
public class MReserveService extends UpdateService
{
	/**
	 * 本番予約マスタの予約情報を取得するSELECT文
	 * @param selectMst 取得する仮マスタ。nullの場合はすべて対象。
	 * @param bctype 取得する対象のBC車種区分
	 * @param flag 予約状態
	 * @return SELECT文
	 */
	protected AutoSelect<MReserve> selectReserveFlag(final Integer selectMst, final String bctype, final ReservationState flag)
	{
		ComplexWhere sWhere = new ComplexWhere();

		if (selectMst == null) {
			// すべて
			sWhere.in("mstVer", MstSelectService.getTempMasterCodeList());
		}
		else {
			// 指定
			sWhere.eq("mstVer", selectMst);
		}

		sWhere.eq("bctype", bctype);
		if (flag != null && flag.equals(ReservationState.RESERVATION)) {
			sWhere.in("reserveFlag", "1", "2");
		}
		else if (flag != null && flag.equals(ReservationState.RESERV_ONLY)) {
			sWhere.in("reserveFlag", "1");
		}
		else {
			sWhere.in("reserveFlag", "2");
		}
		sWhere.eq("mVehicle.endOfProd", false);

		return this.jdbcManager.from(MReserve.class)
				.innerJoin("mVehicle")
				.leftOuterJoin("reserveMUser")
				.where(sWhere);
	}

	/**
	 * 車種の予約状況を取得
	 * @param selectMst 取得する仮マスタ
	 * @param bctype 取得する対象のBC車種区分
	 * @param flag 予約状態
	 * @return 判定結果（true:予約中、false:予約中なし）
	 */
	public Boolean getReserveFlag(final Integer selectMst, final String bctype, final ReservationState flag)
	{
		Boolean result = false;

		// 本番マスタ選択の場合、本番は存在しないので必ず予約中はなし。
		if (MstSelectService.isReal(selectMst)) {
			return result;
		}

		// チェックする仮マスタを設定する
		Integer mst = null;
		mst = selectMst;

		long count = selectReserveFlag(mst, bctype, flag).getCount();
		if (count == 0) {
			result = false;
		}
		else {
			result = true;
		}

		return result;
	}

	/**
	 * 車種の予約者を取得
	 * @param selectMst 取得する仮マスタ
	 * @param bctype 取得する対象のBC車種区分
	 * @param flag 予約状態
	 * @return 予約者一覧
	 */
	public ArrayList<String> getReservationUserVehicle(final Integer selectMst, final String bctype, final ReservationState flag)
	{
		ArrayList<String> list = new ArrayList<String>();

		// 本番マスタ選択の場合、本番は存在しないので必ず予約中はなし。
		if (MstSelectService.isReal(selectMst)) {
			return list;
		}

		// チェックする仮マスタを設定する
		Integer mst = null;
		mst = selectMst;

		List<MReserve> items = selectReserveFlag(mst, bctype, flag).getResultList();

		// 予約者で集約する。
		for (MReserve item : items) {

			String reserveName = "";
			if (item.reserveMUser == null || item.reserveMUser.userName == null || item.reserveMUser.userName.trim().equals("")) {
				reserveName = item.reserveUser;
			}
			else {
				reserveName = item.reserveMUser.userName;
			}

			if (list.contains(reserveName) == false) {
				list.add(reserveName);
			}
		}

		return list;
	}

	/**
	 * 車種の予約情報を取得
	 * @param selectMst 取得する仮マスタ
	 * @param bctype 取得する対象のBC車種区分
	 * @param flag 予約状態
	 * @return 予約情報一覧
	 */
	public ArrayList<String> getReservationInformationMVehicle(final Integer selectMst, final String bctype, final ReservationState flag)
	{
		ArrayList<String> list = new ArrayList<String>();

		// 本番マスタ選択の場合、本番は存在しないので必ず予約中はなし。
		if (MstSelectService.isReal(selectMst)) {
			return list;
		}

		// チェックする仮マスタを設定する
		Integer mst = null;
		mst = selectMst;

		List<MReserve> items = selectReserveFlag(mst, bctype, flag).getResultList();

		// 予約日時と予約者で集約する。
		for (MReserve item : items) {

			String reserveDate = "";
			if (item.reserveDate != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				reserveDate = sdf.format(item.reserveDate);
			}

			String reserveName = "";
			if (item.reserveMUser == null || item.reserveMUser.userName == null || item.reserveMUser.userName.trim().equals("")) {
				reserveName = item.reserveUser;
			}
			else {
				reserveName = item.reserveMUser.userName;
			}

			String reserveInfo = reserveDate + ", " + reserveName;

			if (list.contains(reserveInfo) == false) {
				list.add(reserveInfo);
			}
		}

		return list;
	}
}
