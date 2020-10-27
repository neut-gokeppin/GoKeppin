package jp.co.ctc.service;

import java.sql.Timestamp;
import java.util.List;

import org.seasar.extension.jdbc.service.S2AbstractService;
import org.seasar.extension.jdbc.where.SimpleWhere;

import jp.co.ctc.entity.FShotimage;
import jp.co.ctc.util.Utils;

/**
 * 撮影画像テーブルを扱うサービス
 */
public class FShotimageService extends S2AbstractService<FShotimage>
{
	/**
	 * 撮影画像データを検索します。
	 *
	 * @param groupCode 工程コード
	 * @param idno アイデントNo
	 * @param loDate ラインオフ計画日
	 * @return 撮影画像情報のリスト
	 */
	public List<FShotimage> selectByData(Integer groupCode, String idno, String loDate)
	{
		List<FShotimage> fResultsumList = select()
				.leftOuterJoin("fResultList")
				.innerJoin("fResultsum")
				.innerJoin("fResultsum.mGroup", "fResultsum.mGroup.deleteFlag <> '1'")
				.innerJoin("fBcdata")
				.leftOuterJoin("mOrderList", "mOrderList.mstVer = fResultsum.mstVer AND mOrderList.ptnDiv = fBcdata.ptnDiv")
				.where(new SimpleWhere()
						.eq("idno", idno)
						.eq("loDate", loDate)
						.eq("groupCode", groupCode)
						.eq("completionFlag", "1"))
				.orderBy("idno, loDate, fResultsum.mGroup.groupNo, mOrderList.inspecOrder, itemCode, fResultList.inspecNo DESC, fResultList.selectNumber DESC")
				.getResultList();

		return fResultsumList;
	}

	/**
	 * 撮影画像ファイル名を取得する
	 *
	 * @param bodyNo ボデーNO
	 * @param groupCode 工程コード
	 * @param itemCode 項目コード
	 * @return 撮影画像ファイル名
	 */
	public String getShotimage(String bodyNo, Integer groupCode, Integer itemCode)
	{
		return null;
	}

	/**
	 * 撮影画像ファイル名を取得する
	 *
	 * @param idno アイデントNo
	 * @param loDate ラインオフ計画日
	 * @param groupCode 工程コード
	 * @param itemCode 項目コード
	 * @return 撮影画像ファイル名
	 */
	public String getShotimage(String idno, String loDate, Integer groupCode, Integer itemCode)
	{
		FShotimage fShotimage = select()
				.where(new SimpleWhere()
						.eq("idno", idno)
						.eq("loDate", loDate)
						.eq("groupCode", groupCode)
						.eq("itemCode", itemCode))
				.orderBy("shotDate DESC")
				.limit(1)
				.getSingleResult();

		if (fShotimage == null) {
			return null;
		}

		return fShotimage.shotImage;
	}

	/**
	 * 撮影画像の登録
	 * ※データがなければ追加、あれば更新
	 *
	 * @param fShotimage 撮影画像
	 */
	public void createFShotimage(FShotimage fShotimage)
	{
		FShotimage fShotimageSelect = select()
				.where(new SimpleWhere()
						.eq("idno", fShotimage.idno)
						.eq("loDate", fShotimage.loDate)
						.eq("groupCode", fShotimage.groupCode)
						.eq("itemCode", fShotimage.itemCode))
				.getSingleResult();

		if (fShotimageSelect == null) {
			jdbcManager.insert(fShotimage)
					.excludes("updateUser", "updateDate")
					.execute();
		}
		else {
			boolean isUpdate = true;

			// 更新する撮影日時が古い場合は更新しない
			if (fShotimageSelect.shotDate.compareTo(fShotimage.shotDate) >= 0) {
				isUpdate = false;
			}

			if (isUpdate) {
				String sql = ""
						+ " UPDATE f_shotimage"
						+ " SET"
						+ " shot_date = ?, production_sign = ?, shot_image = ?, update_user = ?, update_date = ?"
						+ " WHERE"
						+ " idno = ? AND lo_date = ? AND bctype = ? AND group_code = ? AND item_code = ?";

				jdbcManager.updateBySql(sql,
										String.class, String.class, String.class, String.class, Timestamp.class,
										String.class, String.class, String.class, Integer.class, Integer.class)
						.params(fShotimage.shotDate, fShotimage.productionSign, fShotimage.shotImage, fShotimage.updateUser, fShotimage.updateDate,
								fShotimage.idno, fShotimage.loDate, fShotimage.bctype, fShotimage.groupCode, fShotimage.itemCode)
						.execute();
			}
		}
	}

	/**
	 * 撮影完了フラグを完了に更新する
	 *
	 * @param fShotimage 撮影画像
	 */
	public void updateCompletionFlag(FShotimage fShotimage)
	{
		String sql = "UPDATE f_shotimage"
				+ " SET"
				+ " completion_flag = ? , update_user = ? , update_date = ?"
				+ " WHERE"
				+ " idno = ? AND lo_date = ? AND bctype = ? AND group_code = ?";

		String completionFlag = "1";
		String updateUser = "SYSTEM";
		Timestamp updateDate = Utils.nowts();
		String idno = fShotimage.idno;
		String loDate = fShotimage.loDate;
		String bctype = fShotimage.bctype;
		Integer groupCode = fShotimage.groupCode;

		jdbcManager.updateBySql(sql,
								String.class, String.class, Timestamp.class,
								String.class, String.class, String.class, Integer.class)
				.params(completionFlag, updateUser, updateDate,
						idno, loDate, bctype, groupCode)
				.execute();
	}
}
