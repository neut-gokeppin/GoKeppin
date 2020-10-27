package jp.co.ctc.service;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityExistsException;

import jp.co.ctc.entity.FBcdata;
import jp.co.ctc.entity.FBcdataDummy;
import jp.co.ctc.entity.MBcsign;
import jp.co.ctc.entity.MItem;
import jp.co.ctc.entity.MOrder;
import jp.co.ctc.entity.TMsgno;
import jp.co.ctc.util.Utils;

import org.seasar.extension.jdbc.service.S2AbstractService;
import org.seasar.extension.jdbc.where.SimpleWhere;




/**
 * ダミーチェック設定データを扱うサービス
 *
 * @author CJ01615
 *
 */
public class FBcdataDummyService extends S2AbstractService<FBcdataDummy> {

	/**
	 * FBcdataService
	 */
	@Resource
	public FBcdataService fBcdataService;

	/**
	 * TSfbcService
	 */
	@Resource
	public TSfbcService tSfbcService;

	/**
	 * MOrderService
	 */
	@Resource
	public MOrderService mOrderService;


	/**
	 * ダミー設定データ取得
	 * TODO 件数増えても遅くならないよう絞込み
	 *
	 * @return ダミー設定データ
	 */
	public List<FBcdataDummy> selectAll() {

		List<FBcdataDummy> fBcdataDummyList = select()
				.innerJoin("fBcdata")
				.innerJoin("fBcdata.mVehicle")
				.innerJoin("mItem")
				.innerJoin("mItem.tMsgno")
				.innerJoin("mItem.mBcsignList")
				.innerJoin("updateMUser")
				.orderBy("fBcdata.tpN0 DESC, mItem.msgNo")
				.getResultList();


		for (FBcdataDummy fBcdataDummy : fBcdataDummyList) {
			TMsgno tMsgno = fBcdataDummy.mItem.tMsgno;
			String bcSign = tSfbcService.getTSfbcValue(fBcdataDummy.idno, fBcdataDummy.loDate, tMsgno.tblname, tMsgno.colname);
			fBcdataDummy.specifiedSign = bcSign;

			// 正解の検査内容を取得
			MBcsign okBcsign = Utils.findBcsign(fBcdataDummy.mItem.mBcsignList, bcSign);
			if (okBcsign != null) {
				fBcdataDummy.specifiedContents = okBcsign.signContents;
			}

			// ダミーの検査内容を取得
			MBcsign dummyBcsign = Utils.findBcsign(fBcdataDummy.mItem.mBcsignList, fBcdataDummy.dummySign);
			if (dummyBcsign != null) {
				fBcdataDummy.dummyContents = dummyBcsign.signContents;
			}
		}

		return fBcdataDummyList;
	}


	/**
	 * ダミー設定データ取得
	 * @param idno アイデントNo
	 * @param loDate ラインオフ計画日
	 * @return ダミー設定データ
	 */
	public List<FBcdataDummy> selectByBody(String idno, String loDate) {
		return select()
				.where(new SimpleWhere()
						.eq("idno", idno)
						.eq("loDate", loDate))
				.getResultList();
	}


	/**
	 * ダミー設定対象の車両リストを取得
	 * @param itemCode 検査項目コード
	 * @param specifiedSign 正解記号
	 * @return 指定された検査項目と正解記号に合致する車両リスト
	 */
	public List<FBcdata> targetBodies(Integer itemCode, String specifiedSign) {

		// 最後尾から12台目以降の車両はダミー設定可能とするために、
		// 最後尾から11台目の車両を取得。
		//   11台＝先読み分4台＋設定中に通過5台＋安全係数2台
		final int offset = 11;
		FBcdata startBody = fBcdataService.getLastInspectedBodyOffset(itemCode, offset);

		// 11台以上の車両が無い場合
		if (startBody == null) {
			return null;
		}

		// 検査項目のデータを取得
		MItem item = jdbcManager.from(MItem.class)
				.innerJoin("tMsgno")
				.where(new SimpleWhere()
						.eq("itemCode", itemCode)
						.eq("sopFlag", "1"))
				.getSingleResult();

		return tSfbcService.getSpecifiedBodies(item, specifiedSign, startBody);
	}


	/**
	 * ダミー設定を登録する。
	 * @param entity 登録対象のダミー設定
	 */
	public void updateEntity(FBcdataDummy entity) {
		// 検査順データ取得
		MOrder order = mOrderService.selectByBodyItem(entity.fBcdata.idno, entity.fBcdata.loDate, entity.itemCode);

		// 検索結果存在チェック
		if (order == null) {
			throw new RuntimeException("指定された項目Noと車両のデータがありません。");
		}

		// 車種区分チェック
		if (!entity.fBcdata.bctype.equals(order.mItem.bctype)) {
			throw new RuntimeException("指定された項目Noと車両との車種が違います。");
		}

		// タイヤフラグチェック
		if (order.mItem.tireDiv) {
			throw new RuntimeException("指定された検査項目はタイヤメーカー検査です。\nタイヤメーカー検査はダミーチェックできません。");
		}

		// ダミー記号の存在チェック
		MBcsign dummyMBcsign = Utils.findBcsign(order.mItem.mBcsignList, entity.dummySign);
		if (dummyMBcsign == null) {
			throw new RuntimeException("指定されたダミー記号は、指示記号マスタに登録されていません。");
		}

		// 車両位置チェック・・・最後尾から6台目以降であること
		//   6台＝先読み分4台＋安全係数2台
		final int offset = 5;
		FBcdata startBody = fBcdataService.getLastInspectedBodyOffset(entity.itemCode, offset);

		// 検査済み最後尾から5台以内はNG
		if (startBody == null || startBody.tpN0.after(entity.fBcdata.tpN0)) {
			throw new RuntimeException("指定された車両は検査直前まで進んだため、ダミーチェックを設定できません。別の車両を指定してください");
		}

		// DBにデータ投入
		entity.idno = entity.fBcdata.idno;
		entity.loDate = entity.fBcdata.loDate;
		entity.mstVer = order.mstVer;
		entity.updateDate = Utils.nowts();

		try {
			insert(entity);
		} catch (EntityExistsException e) {
			throw new EntityExistsException("同一の検査項目No、車両にてダミーチェック設定済みです。", e);
		}

		// XML再生成する
		fBcdataService.createKensaData(entity.idno, entity.loDate, order.groupCode, 1);
	}


	/**
	 * ダミー設定を解除する。
	 * @param entity 解除対象のダミー設定
	 */
	public void deleteEntity(FBcdataDummy entity) {
		// 車両位置チェック・・・最後尾から6台目以降であること
		//   6台＝先読み分4台＋安全係数2台
		final int offset = 5;
		FBcdata startBody = fBcdataService.getLastInspectedBodyOffset(entity.itemCode, offset);

		// 検査済み最後尾から8台以内はNG
		if (startBody == null || startBody.tpN0.after(entity.fBcdata.tpN0)) {
			throw new RuntimeException("指定された車両は検査直前まで進んだため、ダミーチェックを解除できません。");
		}

		// DBデータ削除
		delete(entity);

		// XML再生成するための工程コードを取得する
		MOrder order = mOrderService.selectByBodyItem(entity.idno, entity.loDate, entity.itemCode);

		// XML再生成する
		fBcdataService.createKensaData(entity.idno, entity.loDate, order.groupCode, 1);
	}

}
