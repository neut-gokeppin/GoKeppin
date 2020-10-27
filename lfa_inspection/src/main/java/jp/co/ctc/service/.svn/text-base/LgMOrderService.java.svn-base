package jp.co.ctc.service;

import java.sql.Timestamp;
import java.util.List;

import org.seasar.extension.jdbc.JdbcManager;

import jp.co.ctc.entity.LgMOrder;

/**
 * 取出順序を扱うサービスです。
 *
 * @author kato
 *
 */
public class LgMOrderService {

	/**
	 * JDBCマネージャです。
	 */
	public JdbcManager jdbcManager;

	/**
	 * 検査順(仮テーブル)のリストを返します。
	 *
	 * @param selectMst
	 *            セレクトマスタ
	 * @param getSps
	 *            グループ
	 * @return 取出順のリスト
	 */
	public List<LgMOrder> getLgMOrders(final Integer selectMst, final Integer getSps) {

		switch (selectMst) {
		case 1:
			//-----------------------------------------
			// 本番マスタ取得
			//-----------------------------------------

			// 最新バージョンのマスタのみを取得する。
			return jdbcManager.from(LgMOrder.class)
			.innerJoin("mPart", "mPart.sopFlag = '1'")
			.where("sopFlag = '1' AND spsCode = ? ",
					getSps
					)
			.orderBy("takeOrder")
			.getResultList();
//			break;
		case 0:
			//-----------------------------------------
			// 仮マスタ取得
			//-----------------------------------------

			return jdbcManager.from(LgMOrder.class)
			.innerJoin("mPart", "mPart.sopFlag ='0'")
			.where("mstVer = 0 AND spsCode = ? ",
					getSps
					)
			.orderBy("takeOrder")
			.getResultList();






//			break;
		default:
			return null;
		}
	}

	/**
	 * 指定したマスタバージョンの取出順のリストを返します。
	 *
	 * @param selectMst セレクトマスタ
	 * @param getSps SPS台車
	 * @param mstVersion マスタバージョン
	 * @return 取出順のリスト
	 */
	public List<LgMOrder> getLgMOrders(final Integer selectMst,
			final Integer getSps, final Integer mstVersion) {
		String conVersion = "";
		// バージョン指定が0の場合号口レコードを返す。
		if (mstVersion == null || mstVersion == 0) {
			conVersion = "sopFlag = '1'";
		} else {
			conVersion = "mstVer = " + mstVersion;
		}
		switch (selectMst) {
		case 1:
			//-----------------------------------------
			// 本番マスタ取得
			//-----------------------------------------

			// 最新バージョンのマスタのみを取得する。
			return jdbcManager.from(LgMOrder.class)
			.innerJoin("LgMPart", "LgMPart." + conVersion)
			.where("spsCode = ? " + conVersion,
					getSps
					)
			.orderBy("takeOrder")
			.getResultList();
//			break;
		case 0:
			//-----------------------------------------
			// 仮マスタ取得
			//-----------------------------------------

			return jdbcManager.from(LgMOrder.class)
			.innerJoin("LgMPart", "LgMPart =0")
			.where("mstVer = 0 AND spsCode = ? ",
					getSps
					)
			.orderBy("takeOrder")
			.getResultList();
//			break;
		default:
			return null;
		}
	}

	/**
	 * 指定したマスタバージョンの取出順のリストを返します。
	 * @param selectMst 本番マスタ(1)か仮マスタ(0)かの指定
	 * @param partCode 部品Code
	 * @param mstVersion マスタバージョン
	 * @return 取出順のリスト
	 * @author sugihara
	 */
	public List<LgMOrder> getLgMOrdersByItem(Integer selectMst, Integer partCode,
			Integer mstVersion) {
		String conVersion = "";
		// バージョン指定が0の場合号口レコードを返す。
		if (mstVersion == null || mstVersion == 0) {
			conVersion = "sopFlag = '1'";
		} else {
			conVersion = "mstVer = " + mstVersion;
		}
		switch (selectMst) {
		case 1:
			//-----------------------------------------
			// 本番マスタ取得
			//-----------------------------------------

			// 最新バージョンのマスタのみを取得する。
			return jdbcManager.from(LgMOrder.class)
			.innerJoin("LgMPart", "LgMPart" + conVersion)
			.where("partCode = ? AND " + conVersion,  partCode)
			.orderBy("takeOrder")
			.getResultList();
//			break;
		case 0:
			//-----------------------------------------
			// 仮マスタ取得
			//-----------------------------------------

			return jdbcManager.from(LgMOrder.class)
			.innerJoin("LgMPart", "LgMPart =0")
			.where("mstVer = 0 AND partCode = ?", partCode)
			.orderBy("takeOrder")
			.getResultList();
//			break;
		default:
			return null;
		}
	}


	/**
	 * 検査順情報を更新します。
	 *
	 * @param spsCode 	SPS台車CODE
	 * @param list 		取出順リスト
	 * @param loginUser ログインユーザーのユーザーコード
	 *
	 */
	public void updateAll(int spsCode, List<LgMOrder> list, String loginUser) {
		// 更新レコードにセットするタイムスタンプ
		Timestamp timestamp = new Timestamp(new java.util.Date().getTime());

		System.out.println("updateAll Start");

		// 一旦、仮マスタを全て削除する。
//		jdbcManager.updateBySql("DELETE FROM M_ORDER WHERE MST_VER=0 AND GROUP_CODE = ?", Integer.class)
//					.params(groupCode)
//					.execute();

		for (LgMOrder order : list) {
			order.updateDate = timestamp;
			order.updateUser = loginUser;
			jdbcManager.update(order).execute();
		}

	}


	/**
	 * 取出順情報を削除します。
	 *
	 * @param deleteList 削除対象リスト
	 *
	 * @return
	 */
	public void delete(List<LgMOrder> deleteList) {

		System.out.println("delete Start");

		if (deleteList == null) {
			return;
		}

		for (LgMOrder order : deleteList) {
			jdbcManager.delete(order).execute();
		}
	}

	/**
	 * SPS台車Codeをキーに取出順情報を削除します。
	 *
	 * @param spsCode SPS台車Code
	 *
	 * @return
	 */
	public void deleteByGroupCode(int spsCode) {

		System.out.println("delete Start");

		jdbcManager
				.updateBySql(
						"DELETE FROM LG_M_ORDER "
								+ "WHERE MST_VER = 0 AND SPS_CODE = ?",
						Integer.class).params(spsCode).execute();

		return;
	}

}
