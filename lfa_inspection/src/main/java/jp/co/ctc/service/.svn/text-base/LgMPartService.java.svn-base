package jp.co.ctc.service;

import java.sql.Timestamp;
import java.util.List;

import jp.co.ctc.entity.LgMPart;
import jp.co.ctc.entity.LgMOrder;

//import org.seasar.extension.jdbc.JdbcManager;

/**
 * 部品を扱うサービスです。
 *
 * @author kaidu
 *
 */
public class LgMPartService extends UpdateService {

	/**
	 * JDBCマネージャです。
	 */
	//public JdbcManager jdbcManager;

	/**
	 * 部品のリストを返します。
	 * @param selectMst 本番マスタ(1)か仮マスタ(0)かの指定
	 * @return 部品のリスト
	 */
	public List<LgMPart> getLgMParts(Integer selectMst) {
		return this.getLgMParts(selectMst, null);
	}

	/**
	 * 部品のリストを返します。
	 * @param selectMst 本番マスタ(1)か仮マスタ(0)かの指定
	 * @param spsCode SPS台車コード
	 * @return 部品のリスト
	 */
	public List<LgMPart> getLgMParts(Integer selectMst, Integer spsCode) {

		return this.getLgMParts(selectMst, spsCode, null);

	}


	/**
	 * 指定したマスタバージョンの部品のリストを返します。
	 *
	 * @param selectMst 1:本番マスタ 0:仮マスタ
	 * @param smsCode SPSコード。指定しない場合nullを設定すること。
	 * @param mstVersion 使用するマスタバージョン。nullの場合は最新のものを取得。
	 * @return 部品のリスト
	 */
	public List<LgMPart> getLgMParts(Integer selectMst, Integer smsCode, Integer mstVersion) {

		String conVersion = "";
		String conSms = "";
		//マスターのバージョンを決定する。
		if (selectMst == 1) {
			if (mstVersion == null || mstVersion.equals(0)) {
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
			//仮マスタののデータを指定する。
			conVersion = " AND mstVer = 0";
		}

		if (smsCode != null) {
			conSms += " AND ";
			conSms += "COALESCE(mOrderList.spsCode,0,mOrderList.spsCode) = " + smsCode;
		}


		// 最新バージョンのマスタのみを取得する。
		return jdbcManager.from(LgMPart.class)
				.leftOuterJoin("mOrderList")
				.where("deleteFlag <> '1'" + conVersion + conSms)
				.orderBy("partCode")
				.getResultList();
	}
	/**
	 * 指定したマスタバージョンの部品の単一インスタンスを返します。
	 *
	 * @param selectMst 1:本番マスタ 0:仮マスタ
	 * @param partCode 部品コード。
	 * @param mstVersion 使用するマスタバージョン。nullの場合は最新のものを取得。
	 * @return 部品のリスト
	 */
	public LgMPart getLgMPartsByCode(Integer selectMst, Integer partCode, Integer mstVersion) {

		String conVersion = "";
		String conGroup = "";
		//マスターのバージョンを決定する。
		if (selectMst == 1) {
			if (mstVersion == null || mstVersion.equals(0)) {
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
			//仮マスタののデータを指定する。
			conVersion = " AND mstVer = 0";
		}

		// 最新バージョンのマスタのみを取得する。
		return jdbcManager.from(LgMPart.class)
				.leftOuterJoin("mOrderList")
				.where("deleteFlag <> '1' AND partCode = ?" + conVersion + conGroup, partCode)
				.getSingleResult();
	}
	/**
	 * 名前で検索して部品のリストを返します。
	 *
	 * @param name 名前
	 * @return 部品のリスト
	 */
	public List<LgMPart> getLgMPartsByName(String name) {
		return jdbcManager.from(LgMPart.class).where(
				"upper(name) like upper(?)", "%" + name + "%").orderBy("name")
				.getResultList();
	}

	/**
	 * 部品をテーブルに挿入します。
	 *
	 * @param part 部品
	 * @return 識別子が設定された後の部品
	 */
	public LgMPart create(LgMPart part) {
		jdbcManager.insert(part).execute();
		return part;
	}

	/**
	 * 部品を更新します。
	 *
	 * @param part 部品
	 * @return 更新した行数
	 *//*
	public int update(LgMPart part) {
		return jdbcManager.update(part).execute();
	}*/

	/**
	 * 部品を更新します。
	 *
	 * @param updateItems 追加／更新レコードのリスト
	 * @param removeItems 削除レコードのリスト
	 *
	 */
	public void updateAll(List<LgMPart> updateItems, List<LgMPart> removeItems) {
		// 更新レコードにセットするタイムスタンプ
		Timestamp timestamp = new Timestamp(new java.util.Date().getTime());

		// 追加 or 更新
		for (LgMPart part : updateItems) {

			if (part.editDiv == null) {
				continue;
			}

			if (part.editDiv.equals("I")) {

				// 新規追加
				part.insertDate = timestamp;
				jdbcManager.insert(part)
							.excludes("sopFlag", "deleteFlag")
							.execute();
				System.out.println("Insert");

			} else if (part.editDiv.equals("U") || part.editDiv.equals("M")) {
				// 更新
				part.updateDate = timestamp;
				jdbcManager.update(part).execute();
				System.out.println("Update");
			}

			// 取出順
			updateMorder(part, part.mOrderList);

		}

		// 削除
		for (LgMPart part : removeItems) {
			// 削除の場合はdelete flagをONする。
			// 物理削除はしない。
			//part.deleteFlag = "1";
			//jdbcManager.update(part).execute();
			this.deleteEntity(part);

			// 検査順情報は削除する
			deleteMorder(part, part.mOrderList);
		}
		//return jdbcManager.updateBatch(products).execute();
	}



	/**
	 * 取出順情報を作成、変更します。
	 *
	 * @param part		部品情報
	 * @param mOrder	検査順情報
	 *
	 */
	private void updateMorder(LgMPart part, List<LgMOrder> mOrder) {

		// 取出順情報がない場合は無処理
		if (mOrder == null) {
			return;
		}

		// 取出順情報がない場合は無処理
		if (mOrder.size() < 1) {
			return;
		}

		Timestamp timestamp = new Timestamp(new java.util.Date().getTime());
		LgMOrder partOrder = mOrder.get(0);

		partOrder.partCode = part.partCode;

		// 変更がない場合は無処理
		if (partOrder.editDiv == null) {
			return;
		}

		if (partOrder.editDiv.equals("I")) {
			// 取出順情報追加
			partOrder.insertDate = timestamp;
			jdbcManager.insert(partOrder)
			           .excludes("sopFlag")
			           .execute();
			System.out.println("Order Insert");
			return;
		}

		if (partOrder.editDiv.equals("U")) {
			// 取出順情報変更

			// 一旦、削除(キーは mstVer, spsCode, partcode)
			partOrder.updateDate = timestamp;
			jdbcManager.delete(partOrder)
			   .execute();

			jdbcManager.insert(partOrder)
					   .excludes("sopFlag")
					   .execute();
			System.out.println("Order Update");
			return;
		}

		return;
	}

	/**
	 * 取出順情報をテーブルから削除します。
	 *
	 * @param part		部品情報
	 * @param mOrder	取出順情報
	 *
	 */
	private void deleteMorder(LgMPart part, List<LgMOrder> mOrder) {

		// 検査順情報がない場合は無処理
		if (mOrder == null) {
			return;
		}

		// 検査順情報がない場合は無処理
		if (mOrder.size() < 1) {
			return;
		}

		LgMOrder partOrder = mOrder.get(0);
		partOrder.partCode = part.partCode;

		jdbcManager.delete(partOrder)
				   .execute();

		return;
	}

	/**
	 * 部品をテーブルから削除します。
	 *
	 * @param product
	 *            部品
	 * @return 削除が成功したかどうか
	 */
	public boolean remove(LgMPart product) {
		return jdbcManager.delete(product).execute() == 1;
	}

	/**
	 * 取出し結果を取得用
	 * @param bodyNo ボデーNo
	 * @param recvDay 受信日
	 * @param spsCode SPS台車コード
	 * @return 取出し結果
	 */
	public List<LgMPart> getLgMPartsByKeys(String bodyNo,
			String recvDay, Integer spsCode) {
		return jdbcManager.from(LgMPart.class)
				.leftOuterJoin("mOrderList")
				.leftOuterJoin("fTakeresultList")
				.leftOuterJoin("mBcsignList")
				.leftOuterJoin("mOrderList.mSps", "mOrderList.mSps.spsCode = mOrderList.spsCode")
				.where("fTakeresultList.bodyNo = ? AND fTakeresultList.recvDay = ? "
						+ "AND mOrderList.spsCode = ?", bodyNo, recvDay, spsCode)
				.orderBy("fTakeresultList.takeNo Desc")
				.getResultList();
	}

}