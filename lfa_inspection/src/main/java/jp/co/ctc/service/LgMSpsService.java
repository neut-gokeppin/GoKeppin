package jp.co.ctc.service;

import java.sql.Timestamp;
import java.util.List;

import org.seasar.extension.jdbc.AutoSelect;
//import org.seasar.extension.jdbc.JdbcManager;

import jp.co.ctc.entity.LgMSps;

/**
 * SPS台車を扱うサービスです。
 *
 * @author kaidu
 *
 */
public class LgMSpsService extends UpdateService {

	/**
	 * JDBCマネージャです。
	 */
	//public JdbcManager jdbcManager;

	/**
	 * 検査グループ(本番テーブル)のリストを返します。
	 *
	 * @return 検査グループのリスト
	 */
	public List<LgMSps> getLgMSpss() {

		// 最新バージョンのマスタのみを取得する。
		return getLgMSpss(1);
	}

	/**
	 * SPS台車(本番テーブル)のリストを返します。
	 *@param selectMst 本番マスタ(1)か仮マスタ(0)かの指定
	 * @return SPS台車のリスト
	 */
	public List<LgMSps> getLgMSpss(final Integer selectMst) {

		return getLgMSpss(selectMst, null);
	}
	/**
	 * 指定したマスタバージョンの検査グループ(本番テーブル)のリストを返します。
	 * @param selectMst 本番マスタ(1)か仮マスタ(0)かの指定
	 * @param mstVersion マスタバージョン
	 * @return SPS台車のリスト
	 * @author sugihara
	 */
	public List<LgMSps> getLgMSpss(final Integer selectMst, final Integer mstVersion) {
		return this.getLgMSpss(selectMst, mstVersion, false);
	}

	/**
	 * 指定したマスタバージョンの検査グループ(本番テーブル)のリストを返します。
	 * @param selectMst 本番マスタ(1)か仮マスタ(0)かの指定
	 * @param mstVersion マスタバージョン
	 * @param relFlg リレーションをすべて取得するかどうかのフラグ
	 * @return SPS台車のリスト
	 * @author sugihara
	 */
	public List<LgMSps> getLgMSpss(final Integer selectMst, final Integer mstVersion, final boolean relFlg) {
		String conVersion = "";
		//バージョン指定が0の場合号口レコードを返す。
		if (mstVersion == null || mstVersion.equals(0)) {
			conVersion = "sopFlag = '1'";
		} else {
			conVersion = "mstVer = " + mstVersion;
		}
		AutoSelect<LgMSps> asMSps;
		switch (selectMst) {
		case 1:
			//-----------------------------------------
			// 本番マスタ取得
			//-----------------------------------------

			// 最新バージョンのマスタのみを取得する。
			asMSps = jdbcManager.from(LgMSps.class)
				.where("deleteFlag <> '1' AND " + conVersion)
				.leftOuterJoin("mOrderList")
				.orderBy("spsNo");
			break;

		case 0:
			//-----------------------------------------
			// 仮マスタ取得
			//-----------------------------------------

			asMSps = jdbcManager.from(LgMSps.class)
				.where("mstVer = 0 AND deleteFlag <> '1'")
				.leftOuterJoin("mOrderList")
				.orderBy("spsNo");
			break;

		default:
			return null;
		}
		if (relFlg) {
			return asMSps.innerJoin("mOrderList.mPart", "mOrderList.mPart.deleteFlag <> '1' AND mOrderList.mPart." + conVersion)
				.leftOuterJoin("mOrderList.mPart.tMsgno")
				.leftOuterJoin("mOrderList.mPart.mBcsignList", "mOrderList.mPart.mBcsignList.deleteFlag <> '1'")
				.orderBy("spsNo, mOrderList.takeOrder")
				.getResultList();
		} else {
			return asMSps.getResultList();
		}
	}


	/**
	 * SPSCodeで検索してSPS台車・部品のリストを返します。
	 * mstVersionに「0」を指定すると最新のバージョンを取得する。
	 * @param selectMst 本番マスタ(1)か仮マスタ(0)かの指定
	 * @param spsCode SPSCode
	 * @param mstVersion 使用するマスタバージョン
	 * @return SPS台車のリスト
	 * @author sugihara
	 */
	public List<LgMSps> getLgMSpsByName(Integer selectMst, Integer spsCode, Integer mstVersion) {
		AutoSelect<LgMSps> autoSelect;
		if (selectMst == 1) {
			if (mstVersion == null || mstVersion.equals(0)) {
				autoSelect = jdbcManager.from(LgMSps.class)
					.where("spsCode = ? AND sopFlag = '1' AND deleteFlag <> '1'", spsCode);
			} else {
				autoSelect = jdbcManager.from(LgMSps.class)
					.where("spsCode = ? AND mstVer = ? AND deleteFlag <> '1'", spsCode, mstVersion);
			}
		} else {
			autoSelect = jdbcManager.from(LgMSps.class)
				.where("spsCode = ? AND mstVer = '0' AND deleteFlag <> '1'", spsCode);
		}
		return autoSelect.leftOuterJoin("mOrderList")
			.innerJoin("mOrderList.mPart", "mOrderList.mPart.deleteFlag <> '1'")
			.leftOuterJoin("mOrderList.mPart.tMsgno")
			.leftOuterJoin("mOrderList.mPart.mBcsignList", "mOrderList.mPart.mBcsignList.deleteFlag <> '1'")
			.orderBy("spsNo, mOrderList.takeOrder")
			.getResultList();
	}

	/**
	 * getLgMSps for body.
	 * 号車選択画面用のSPS台車のリストを返します。
	 * @return 検査グループのリスト
	 */
	public List<LgMSps> getLgMSps4Body() {
		return jdbcManager.from(LgMSps.class)
			.where("deleteFlag<>'1' and mstVer>0 and exists(select 1 from lg_m_order where sps_code=spsCode AND mst_ver=mstVer)")
			.orderBy("mstVer, spsNo")
			.getResultList();
	}

	/**
	 * SPS台車をテーブルに挿入します。
	 *
	 * @param sps SPS台車
	 * @return 識別子が設定された後のSPS台車
	 */
	public LgMSps create(LgMSps sps) {
		jdbcManager.insert(sps)
		.excludes("sopFlag", "deleteFlag")
		.execute();
		return sps;
	}

	/**
	 * SPS台車を更新します。
	 *
	 * @param sps SPS台車
	 * @return 更新した行数
	 *//*
	public int update(LgMSps sps) {
		return jdbcManager.update(sps)
		.excludes("sopFlag")
		.execute();
	}*/

	/**
	 * SPS台車を更新します。
	 *
	 * @param updateSpss 追加／更新レコードのリスト
	 * @param removeSpss 削除レコードのリスト
	 *
	 */
	public void updateAll(List<LgMSps> updateSpss, List<LgMSps> removeSpss) {
		// 更新レコードにセットするタイムスタンプ
		Timestamp timestamp = new Timestamp(new java.util.Date().getTime());

		System.out.println("updateAll Start");

		// 追加 or 更新
		for (LgMSps sps : updateSpss) {

			System.out.println("Edit Div = " + sps.editDiv);

			if (sps.editDiv == null) {
				continue;
			}

			if (sps.editDiv.equals("I")) {
				// 新規追加
				sps.insertDate = timestamp;
				create(sps);
				System.out.println("Insert");

			} else if (sps.editDiv.equals("U") || sps.editDiv.equals("M")) {
				// 更新
				sps.updateDate = timestamp;
				updateEntity(sps);
				System.out.println("Update");
			}

		}

//		MOrderService orderSrv = new MOrderService();

		// 削除
		for (LgMSps sps : removeSpss) {

			// 物理削除はしない。deleteFlagのONのみ
			sps.updateDate = timestamp;
			//sps.deleteFlag = "1";

			//update(sps);
			deleteEntity(sps);

			// 削除対象のグループ内の検査順情報を削除する。
//			orderSrv.deleteByGroupCode(sps.spsCode);

			jdbcManager.updateBySql("DELETE FROM LG_M_ORDER WHERE MST_VER = 0 AND SPS_CODE = ?", Integer.class)
			.params(sps.spsCode).execute();

		}

	}
}
