package jp.co.ctc.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.log.Logger;
import org.seasar.framework.util.ResourceUtil;
import org.seasar.struts.util.RequestUtil;

import jp.co.ctc.entity.MGroup;
import jp.co.ctc.entity.MItem;
import jp.co.ctc.entity.MOrder;
import jp.co.ctc.util.Utils;

/**
 * 検査項目を扱うサービスです。
 *
 * @author kaidu
 *
 */
public class MItemService extends UpdateService {

	// 2017/12/01 DA ins start
	/**
	 * ログ出力用
	 */
	private Logger logger = Logger.getLogger(this.getClass());
	// 2017/12/01 DA ins end

	/**
	 * MGroupService
	 */
	@Resource
	public MGroupService mGroupService;

	/**
	 * 指定したマスタバージョンの検査項目のリストを返します。
	 *
	 * @param selectMst 1:本番マスタ 0:仮マスタ
	 * @param mstVersion 使用するマスタバージョン。nullの場合は最新のものを取得。
	 * @param bctype BC車種区分。nullの場合は全車種のデータを取得
	 * @param groupName 工程名。nullの場合は全工程のデータを取得
	 * @return 検査項目のリスト
	 */
	public List<MItem> getMItems(Integer selectMst, Integer mstVersion, String bctype, String groupName) {

		//マスターのバージョンを決定する。
		String conVersion = "";
		// 2016/02/24 DA upd start
//		if (selectMst == 1) {
		if (MstSelectService.isReal(selectMst)) {
//			if (mstVersion == null || mstVersion.equals(0)) {
			if (mstVersion == null || MstSelectService.isTemporary(mstVersion)) {
		// 2016/02/24 DA upd end
				//本番・仮選択が『1』で、バージョンが指定されていない場合
				//本番マスタの最新（号口）データを指定する。
				conVersion = " AND sopFlag = '1'";
			} else {
				//本番・仮選択が『1』で、バージョンが指定されている場合
				//本番マスタの指定バージョンのデータを指定する。
				conVersion = " AND mstVer = " + mstVersion;
			}
		} else {
			//本番・仮選択が『0』の場合
			//仮マスタのデータを指定する。
			// 2016/02/24 DA upd start
//			conVersion = " AND mstVer = 0";
			conVersion = " AND mstVer = " + selectMst;
			// 2016/02/24 DA upd end
		}

		// 車種区分の条件分を記述
		String conBctype = "";
		if (StringUtils.isNotEmpty(bctype)) {
			conBctype = " AND bctype = '" + bctype + "'";
		}

		// 工程名称の条件文を記述
		String conGroup = "";
		if (StringUtils.equals(groupName, ALL_GROUPS)) {
			conGroup = "";
		} else if (StringUtils.equals(groupName, NO_GROUPS)) {
			conGroup = " AND (mOrderL.mGroup.groupName IS NULL"
					+ " OR mOrderR.mGroup.groupName IS NULL)";
		} else {
			conGroup = " AND (mOrderL.mGroup.groupName = '" + groupName + "'"
					+ " OR mOrderR.mGroup.groupName = '" + groupName + "')";
		}

		// 最新バージョンのマスタのみを取得する。
		// 2016/02/24 DA upd start
//		return jdbcManager.from(MItem.class)
//				.leftOuterJoin("mOrderL", "mOrderL.ptnDiv='1'")
//				.leftOuterJoin("mOrderL.mGroup")
//				.leftOuterJoin("mOrderR", "mOrderR.ptnDiv='2'")
//				.leftOuterJoin("mOrderR.mGroup")
//				.leftOuterJoin("updateMUser")
//				// 2016/02/24 DA ins start
//				.leftOuterJoin("mBcsignList")
//				// 2016/02/24 DA ins end
//				.where("deleteFlag <> '1'" + conVersion + conBctype + conGroup)
//				.orderBy("itemCode")
//				.getResultList();

		List<MItem> mItemList = jdbcManager.from(MItem.class)
				.leftOuterJoin("mOrderL", "mOrderL.ptnDiv='1'")
				.leftOuterJoin("mOrderL.mGroup")
				.leftOuterJoin("mOrderR", "mOrderR.ptnDiv='2'")
				.leftOuterJoin("mOrderR.mGroup")
				.leftOuterJoin("updateMUser")
				.leftOuterJoin("mBcsignList")
				.where("deleteFlag <> '1'" + conVersion + conBctype + conGroup)
				.orderBy("itemCode")
				.getResultList();

		for (MItem mItem : mItemList) {
			// 値のスペースなどの加工をする
			mItem.itemName = Utils.trimDisplay(mItem.itemName);
			mItem.msgNo = Utils.trimDisplay(mItem.msgNo);
			mItem.notes = Utils.trimDisplay(mItem.notes);
		}

		return mItemList;
		// 2016/02/24 DA upd end
	}

	/**
	 * 指定したマスタバージョンの検査項目のリストを返します。
	 *
	 * @param selectMst 1:本番マスタ 0:仮マスタ
	 * @param mstVersion 使用するマスタバージョン。nullの場合は最新のものを取得。
	 * @param bctype BC車種区分。nullの場合は全車種のデータを取得
	 * @param groupName 工程名。nullの場合は全工程のデータを取得
	 * @param isEndOfProd 生産終了（true:生産終了、false:生産中）
	 * @return 検査項目のリスト
	 */
	public List<MItem> getMItems(Integer selectMst, Integer mstVersion, String bctype, String groupName, boolean isEndOfProd) {

		//マスターのバージョンを決定する。
		String conVersion = "";
		// 2016/02/24 DA upd start
//		if (selectMst == 1) {
		if (MstSelectService.isReal(selectMst)) {
//			if (mstVersion == null || mstVersion.equals(0)) {
			if (mstVersion == null || MstSelectService.isTemporary(mstVersion)) {
		// 2016/02/24 DA upd end
				//本番・仮選択が『1』で、バージョンが指定されていない場合
				//本番マスタの最新（号口）データを指定する。
				conVersion = " AND sopFlag = '1'";
			}
			else {
				//本番・仮選択が『1』で、バージョンが指定されている場合
				//本番マスタの指定バージョンのデータを指定する。
				conVersion = " AND mstVer = " + mstVersion;
			}
		}
		else {
			//本番・仮選択が『0』の場合
			//仮マスタのデータを指定する。
			// 2016/02/24 DA upd start
//			conVersion = " AND mstVer = 0";
			conVersion = " AND mstVer = " + selectMst;
			// 2016/02/24 DA upd end
		}

		// 車種区分の条件分を記述
		String conBctype = "";
		if (StringUtils.isNotEmpty(bctype)) {
			conBctype = " AND bctype = '" + bctype + "'";
		}

		// 工程名称の条件文を記述
		String conGroup = "";
		if (StringUtils.equals(groupName, ALL_GROUPS)) {
			conGroup = "";
		}
		else if (StringUtils.equals(groupName, NO_GROUPS)) {
			conGroup = " AND (mOrderL.mGroup.groupName IS NULL"
					+ " OR mOrderR.mGroup.groupName IS NULL)";
		}
		else {
			conGroup = " AND (mOrderL.mGroup.groupName = '" + groupName + "'"
					+ " OR mOrderR.mGroup.groupName = '" + groupName + "')";
		}

		//生産終了の条件分を記述
		String conEndOfProd = "";
		if (isEndOfProd) {
			conEndOfProd = " AND mOrderL.mGroup.mVehicle.endOfProd = TRUE"
					+ " AND mOrderR.mGroup.mVehicle.endOfProd = TRUE";
		}
		else {
			conEndOfProd = " AND (mOrderL.mGroup.mVehicle.endOfProd IS NULL"
					+ " OR mOrderL.mGroup.mVehicle.endOfProd = FALSE)"
					+ " AND (mOrderR.mGroup.mVehicle.endOfProd IS NULL"
					+ " OR mOrderR.mGroup.mVehicle.endOfProd = FALSE)";
		}

		// 最新バージョンのマスタのみを取得する。
		return jdbcManager.from(MItem.class)
				.leftOuterJoin("mOrderL", "mOrderL.ptnDiv='1'")
				.leftOuterJoin("mOrderL.mGroup")
				.leftOuterJoin("mOrderL.mGroup.mVehicle")
				.leftOuterJoin("mOrderR", "mOrderR.ptnDiv='2'")
				.leftOuterJoin("mOrderR.mGroup")
				.leftOuterJoin("mOrderR.mGroup.mVehicle")
				.leftOuterJoin("updateMUser")
				.where("deleteFlag <> '1'" + conVersion + conBctype + conGroup + conEndOfProd)
				.orderBy("itemCode")
				.getResultList();
	}

	/**
	 * 指定したマスタバージョンの検査項目の単一インスタンスを返します。
	 *
	 * @param selectMst 1:本番マスタ 0:仮マスタ
	 * @param mstVersion 使用するマスタバージョン。nullの場合は最新のものを取得。
	 * @param itemCode 項目コード。
	 * @return 検査項目のリスト
	 */
	public MItem getMItemsByCode(Integer selectMst, Integer mstVersion, Integer itemCode) {

		String conVersion = "";
		String conGroup = "";
		//マスターのバージョンを決定する。
		if (MstSelectService.isReal(selectMst)) {
			if (mstVersion == null || MstSelectService.isTemporary(mstVersion)) {
				//本番・仮選択が『1』で、バージョンが指定されていない場合
				//本番マスタの最新（号口）データを指定する。
				conVersion = " AND sopFlag = '1'";
			}
			else {
				//本番・仮選択が『1』で、バージョンが指定されている場合
				//本番マスタの指定バージョンのデータを指定する。
				conVersion = " AND mstVer = " + mstVersion;
			}
		}
		else {
			//本番・仮選択が『0』の場合
			//仮マスタののデータを指定する。
			conVersion = " AND mstVer = " + selectMst;
		}

		// 最新バージョンのマスタのみを取得する。
		return jdbcManager.from(MItem.class)
				.leftOuterJoin("mOrderL", "mOrderL.ptnDiv='1'")
				.leftOuterJoin("mOrderR", "mOrderR.ptnDiv='2'")
				.where("deleteFlag <> '1' and itemCode = ?" + conVersion + conGroup, itemCode)
				.orderBy("itemCode")
				.getSingleResult();
	}

	/**
	 * 名前で検索して検査項目のリストを返します。
	 *
	 * @param name 名前
	 * @return 検査項目のリスト
	 */
	public List<MItem> getMItemsByName(String name) {
		return jdbcManager.from(MItem.class).where(
				"upper(name) like upper(?)", "%" + name + "%").orderBy("name")
				.getResultList();
	}

	/**
	 * 検査項目をテーブルに挿入します。
	 *
	 * @param item 検査項目
	 * @return 識別子が設定された後の検査項目
	 */
	public MItem create(MItem item) {
		jdbcManager.insert(item).execute();
		return item;
	}

	/**
	 * 検査項目を更新します。
	 *
	 * @param updateItems 追加／更新レコードのリスト
	 * @param removeItems 削除レコードのリスト
	 *
	 */
	public void updateAll(List<MItem> updateItems, List<MItem> removeItems) {
		// 更新レコードにセットするタイムスタンプ
		Timestamp timestamp = new Timestamp(new java.util.Date().getTime());

		// 追加 or 更新
		for (MItem item : updateItems) {

			if (item.editDiv == null) {
				continue;
			}

			// 2016/02/24 DA ins end
			// 値のスペースなどの加工をする
			item.itemName = Utils.trimDbSetting(item.itemName);
			item.msgNo = Utils.trimDbSetting(item.msgNo);
			item.notes = Utils.trimDbSetting(item.notes);
			// 2016/02/24 DA ins end

			item.updateDate = timestamp;
			if (item.editDiv.equals("I")) {

				// 新規追加
				item.insertDate = timestamp;
				jdbcManager.insert(item)
						.excludes("sopFlag", "deleteFlag")
						.execute();

			} else if (item.editDiv.equals("U") || item.editDiv.equals("M")) {

				// 更新
				jdbcManager.update(item).execute();

			}

			// 左ハンドル用検査順
			updateMorder(item, item.mOrderL);

			// 右ハンドル用検査順
			updateMorder(item, item.mOrderR);

		}

		// 削除
		for (MItem item : removeItems) {
			// 削除の場合はdelete flagをONする。
			// 物理削除はしない。
			//item.deleteFlag = "1";
			//jdbcManager.update(item).execute();
			this.deleteEntity(item);

			// 検査順情報は削除する
			deleteMorder(item, item.mOrderL);
			deleteMorder(item, item.mOrderR);

		}
		//return jdbcManager.updateBatch(products).execute();
	}

	/**
	 * 検査順情報を作成、変更します。
	 *
	 * @param item		検査項目情報
	 * @param mOrder	検査順情報
	 *
	 */
	private void updateMorder(MItem item, List<MOrder> mOrder) {

		// 検査順情報がない場合は無処理
		if (mOrder == null) {
			return;
		}

		// 検査順情報がない場合は無処理
		if (mOrder.size() < 1) {
			return;
		}

		Timestamp timestamp = new Timestamp(new java.util.Date().getTime());
		MOrder itemOrder = mOrder.get(0);

		itemOrder.itemCode = item.itemCode;

		// 変更がない場合は無処理
		if (itemOrder.editDiv == null) {
			return;
		}

		if (itemOrder.editDiv.equals("I")) {
			// 検査順情報追加
			itemOrder.insertDate = timestamp;
			jdbcManager.insert(itemOrder)
					.excludes("sopFlag")
					.execute();
			System.out.println("Order Insert");
			return;
		}

		if (itemOrder.editDiv.equals("U")) {
			// 検査順情報変更

			// 一旦、削除(キーは mstVer, groupCode, ptnDiv, itemcode)
			itemOrder.updateDate = timestamp;
			jdbcManager.delete(itemOrder)
					.execute();

			jdbcManager.insert(itemOrder)
					.excludes("sopFlag")
					.execute();
			System.out.println("Order Update");
			return;
		}

		return;
	}

	/**
	 * 検査順情報をテーブルから削除します。
	 *
	 * @param item		検査項目情報
	 * @param mOrder	検査順情報
	 *
	 */
	private void deleteMorder(MItem item, List<MOrder> mOrder) {

		// 検査順情報がない場合は無処理
		if (mOrder == null) {
			return;
		}

		// 検査順情報がない場合は無処理
		if (mOrder.size() < 1) {
			return;
		}

		MOrder itemOrder = mOrder.get(0);
		itemOrder.itemCode = item.itemCode;

		jdbcManager.delete(itemOrder)
				.execute();

		return;
	}

	/**
	 * 検査項目をテーブルから削除します。
	 *
	 * @param product
	 *            検査項目
	 * @return 削除が成功したかどうか
	 */
	public boolean remove(MItem product) {
		return jdbcManager.delete(product).execute() == 1;
	}

	// 2017/12/01 DA ins start
	/**
	 * 撮影システムで使うマスタ情報を出力する。
	 *
	 * @param selectMst 本番マスタ(1)か仮マスタ(0以下)かの指定
	 * @param bctype 車種
	 * @param groupName 工程名
	 * @param loginUser ログインユーザー
	 * @return 出力結果件数（-1:異常終了、0以上:正常終了）
	 */
	public Integer createShotFile(Integer selectMst, String bctype, String groupName, String loginUser)
	{
		Integer totalCount = -1;

		try {
			List<MGroup> mGroup = mGroupService.getMGroups(selectMst, null, bctype, groupName);
			if (mGroup.size() == 0) {
				return 0;
			}

			String praFilename = (String) SingletonS2ContainerFactory.getContainer().getComponent("shotSystemFile");
			Integer retryCount = (Integer) SingletonS2ContainerFactory.getContainer().getComponent("retryCount");

			int loopCount;

			for (MGroup group : mGroup) {

				String csvBctype = group.bctype;
				String csvVehicleName = group.mVehicle.vehicleName;
				String csvGroupCode = group.groupCode.toString();
				String csvGroupName = group.groupName;
				String csvLine = group.line;

				String filename = praFilename.substring(0, praFilename.length() - 4) + "_" + csvBctype + "_" + csvGroupCode + ".csv";

				// ファイル削除
				loopCount = 0;
				for (int i = 0; i <= retryCount; i++) {
					try {
						boolean isDel = true;
						File file = new File(filename);
						if (file.exists()) {
							isDel = file.delete();
						}

						if (isDel) {
							// 正常の場合は処理を抜ける
							break;
						}
						else {
							loopCount++;
						}
					}
					catch (Exception e) {
						loopCount++;
					}

					// リトライ中は処理を待つ、最後は待たない
					if (i != retryCount) {
						sleep();
					}
				}
				if ((loopCount - 1) == retryCount) {
					logger.error("file delete error :" + filename);
					return -1;
				}

				// ファイル作成
				loopCount = 0;
				for (int i = 0; i <= retryCount; i++) {

					FileOutputStream fos = null;
					OutputStreamWriter osw = null;
					BufferedWriter bw = null;
					try {
						fos = new FileOutputStream(filename);
						osw = new OutputStreamWriter(fos, "Windows-31J");
						bw = new BufferedWriter(osw);

						String csvItemCodeBak = "";
						totalCount = 0;

						for (MOrder order : group.mOrderList) {

							String csvItemCode = order.mItem.itemCode.toString();
							String csvItemName = order.mItem.itemName;

							// 左右のハンドルを登録した場合は2件取得する。項目は同じため1件のみ出力する。
							if (csvItemCodeBak.equals(csvItemCode)) {
								continue;
							}
							csvItemCodeBak = csvItemCode;

							String data = csvBctype
									+ "," + csvVehicleName
									+ "," + csvGroupCode
									+ "," + csvGroupName
									+ "," + csvLine
									+ "," + csvItemCode
									+ "," + csvItemName
									+ "\r\n";

							bw.write(data);
							totalCount++;
						}

						// 正常の場合は処理を抜ける
						break;
					}
					catch (Exception e) {
						loopCount++;
					}
					finally {
						IOUtils.closeQuietly(bw);
						IOUtils.closeQuietly(osw);
						IOUtils.closeQuietly(fos);
					}

					// リトライ中は処理を待つ、最後は待たない
					if (i != retryCount) {
						sleep();
					}
				}
				if ((loopCount - 1) == retryCount) {
					logger.error("file creation error :" + filename);
					return -1;
				}

				// サーバーのログ出力
				shotFileOutputLog(selectMst, null, csvBctype, csvGroupName, loginUser, filename);
			}
		}
		catch (Exception e) {
			logger.error("system error", e);
			return -1;
		}

		return totalCount;
	}

	/**
	 * 撮影システムで使うマスタ情報を出力後のログ処理
	 * @param selectMst 本番マスタ(1)か仮マスタ(0以下)かの指定
	 * @param mstVersion 本番マスタで使用するマスタバージョン。nullの場合は最新のものを取得。
	 * @param bctype BC車種区分
	 * @param groupName 工程名称
	 * @param loginUser ログインユーザー
	 * @param fileName ファイル名
	 */
	public void shotFileOutputLog(final Integer selectMst, final Integer mstVersion, final String bctype, final String groupName, final String loginUser, final String fileName)
	{
		StringBuilder logStr = new StringBuilder();
		Properties props = ResourceUtil.getProperties("application_ja.properties");
		String msg = props.getProperty("svr0000007");
		String ip = RequestUtil.getRequest().getRemoteAddr();
		String masterName = MstSelectService.getMasterVersionName(selectMst);

		// メッセージ：svr0000007「撮影システム自動反映を出力しました。[IPアドレス][ログインID][機能名称][マスタ種別][指定条件][ファイル名]」
		logStr.append("[" + ip + "]");
		logStr.append("[" + loginUser + "]");
		logStr.append("[撮影システム自動反映]");
		logStr.append("[" + masterName + "]");
		logStr.append("[BC車種区分=" + bctype + ",工程名称=" + groupName + "]");
		logStr.append("[" + fileName + "]");

		String logData = msg.replace("{0}", logStr.toString());
		logger.info(logData);
	}

	/**
	 * 一定時間スリープする
	 */
	private void sleep()
	{
		try {
			final int milliseconds = 100;
			Thread.sleep(milliseconds);
		}
		catch (InterruptedException e) {
			logger.error("sleep中に例外発生", e);
		}
	}
	// 2017/12/01 DA ins end
}
