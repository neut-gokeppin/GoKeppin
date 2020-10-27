package jp.co.ctc.service;

import java.sql.Timestamp;
import java.util.List;

import jp.co.ctc.entity.FBcdata;
import jp.co.ctc.entity.MOrder;

import org.seasar.extension.jdbc.service.S2AbstractService;
import org.seasar.extension.jdbc.where.SimpleWhere;

/**
 * 検査順序を扱うサービスです。
 *
 * @author kato
 *
 */
public class MOrderService extends S2AbstractService<MOrder> {

	/**
	 * 指定したマスタバージョンの検査順(仮テーブル)のリストを返します。
	 *
	 * @param selectMst セレクトマスタ
	 * @param mstVersion マスタバージョン
	 * @param bctype BC車種区分
	 * @param groupName 工程名
	 * @param ptnDiv 区分
	 * @return 検査順のリスト
	 */
	public List<MOrder> getMOrders(final Integer selectMst,
			final Integer mstVersion, final String bctype,
			final String groupName, final String ptnDiv) {

		String sopFlag = null;
		Integer mstVer = null;

		// 2016/02/24 DA upd start
//		switch (selectMst) {
//		case 1:
//			//-----------------------------------------
//			// 本番マスタ取得
//			//-----------------------------------------
//			// バージョン指定が0の場合号口レコードを返す。
//			if (mstVersion == null || mstVersion == 0) {
//				sopFlag = selectMst.toString();
//			} else {
//				mstVer = mstVersion;
//			}
//			break;
//
//		case 0:
//			//-----------------------------------------
//			// 仮マスタ取得
//			//-----------------------------------------
//			mstVer = 0;
//			break;
//
//		default:
//			return null;
//		}
		if (MstSelectService.isReal(selectMst)) {
			if (mstVersion == null || MstSelectService.isTemporary(mstVersion)) {
				sopFlag = selectMst.toString();
			}
			else {
				mstVer = mstVersion;
			}
		}
		else {
			mstVer = selectMst;
		}
		// 2016/02/24 DA upd end

		return select()
				.innerJoin("mGroup")
				.innerJoin("mItem")
				.leftOuterJoin("updateMUser")
				.where(new SimpleWhere()
						.eq("mGroup.bctype", bctype)
						.eq("mGroup.groupName", groupName)
						.eq("ptnDiv", ptnDiv)
						.eq("sopFlag", sopFlag)
						.eq("mstVer", mstVer))
				.orderBy("mGroup.groupNo, inspecOrder")
				.getResultList();
	}


	/**
	 * 検査順情報を更新します。
	 *
	 * @param listL 	左ハンドル用のリスト
	 * @param listR 	右ハンドル用のリスト
	 * @param loginUser ログインユーザーのユーザーコード
	 *
	 */
	public void updateAll(List<MOrder> listL, List<MOrder> listR, String loginUser) {
		// 更新レコードにセットするタイムスタンプ
		Timestamp timestamp = new Timestamp(new java.util.Date().getTime());

		for (MOrder order : listL) {
			order.updateDate = timestamp;
			order.updateUser = loginUser;
			jdbcManager.update(order).execute();
		}

		for (MOrder order : listR) {
			order.updateDate = timestamp;
			order.updateUser = loginUser;
			jdbcManager.update(order).execute();
		}
	}


	/**
	 * 検査順情報を削除します。
	 *
	 * @param deleteList 削除対象リスト
	 *
	 * @return
	 */
	public void delete(List<MOrder> deleteList) {

		System.out.println("delete Start");

		if (deleteList == null) {
			return;
		}

		for (MOrder order : deleteList) {
			jdbcManager.delete(order).execute();
		}
	}

	/**
	 * グループCodeをキーに検査順情報を削除します。
	 *
	 * @param groupCode グループCode
	 *
	 * @return
	 */
	public void deleteByGroupCode(int groupCode) {

		System.out.println("delete Start");

		jdbcManager
				.updateBySql(
						"DELETE FROM M_ORDER "
								+ "WHERE MST_VER = 0 AND GROUP_CODE = ?",
						Integer.class).params(groupCode).execute();

		return;
	}


	/**
	 * 車両と検査項目をキーに、検査順データを取得します。
	 * @param idno アイデントNo
	 * @param loDate ラインオフ計画日
	 * @param itemCode 検査項目コード
	 * @return 検査順データ
	 */
	public MOrder selectByBodyItem(String idno, String loDate, Integer itemCode) {
		return select()
				.innerJoin("mItem")
				.innerJoin("mItem.mBcsignList")
				.innerJoin("fResultsum")
				.innerJoin("fResultsum.fBcdata", "fResultsum.fBcdata.ptnDiv = ptnDiv")
				.where(new SimpleWhere()
						.eq("itemCode", itemCode)
						.eq("fResultsum.idno", idno)
						.eq("fResultsum.loDate", loDate))
				.getSingleResult();
	}


	/**
	 * 指定された検査項目を含む検査順レコードを取得。
	 * @param itemCode 検査項目コード
	 * @return 検査順レコード
	 */
	public List<MOrder> selectByItem(Integer itemCode) {
		return select()
				.innerJoin("mGroup")
				.where(new SimpleWhere()
						.eq("sopFlag", "1")
						.eq("itemcode", itemCode))
				.getResultList();
	}


	/**
	 * 検査結果付きの検査順を取得します。
	 * @param fBcdata 対象の車両
	 * @param groupCode 対象の工程
	 * @param mstVer 対象のマスタバージョン
	 * @return 検査結果付きの検査順リスト
	 */
	public List<MOrder> getMOrderList(FBcdata fBcdata, Integer groupCode, Integer mstVer) {
		List<MOrder> mOrderList = select()
				.leftOuterJoin("fResultList", new SimpleWhere()
						.eq("fResultList.idno", fBcdata.idno)
						.eq("fResultList.loDate", fBcdata.loDate))
				.where(new SimpleWhere()
						.eq("mstVer", mstVer)
						.eq("groupCode", groupCode)
						.eq("ptnDiv", fBcdata.ptnDiv))
				.orderBy("fResultList.inspecNo DESC")
				.getResultList();
		return mOrderList;
	}
}
