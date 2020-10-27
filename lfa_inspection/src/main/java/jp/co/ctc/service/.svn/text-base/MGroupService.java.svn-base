package jp.co.ctc.service;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.AutoSelect;
import org.seasar.extension.jdbc.where.ComplexWhere;
import org.seasar.extension.jdbc.where.SimpleWhere;
import org.seasar.framework.util.ResourceUtil;
import org.seasar.framework.util.TextUtil;

import jp.co.ctc.entity.FBcdata;
import jp.co.ctc.entity.MGroup;
import jp.co.ctc.util.Utils;

/**
 * 検査グループを扱うサービスです。
 *
 * @author kaidu
 *
 */
public class MGroupService extends UpdateService {

	/**
	 * 検査グループ(本番テーブル)のリストを返します。
	 *
	 * @return 検査グループのリスト
	 */
	public List<MGroup> getMGroups() {

		// 最新バージョンのマスタのみを取得する。
		return getMGroups(1);
	}

	/**
	 * 検査グループ(本番テーブル)のリストを返します。
	 *@param selectMst 本番マスタ(1)か仮マスタ(0)かの指定
	 * @return 検査グループのリスト
	 */
	public List<MGroup> getMGroups(final Integer selectMst) {

		return getMGroups(selectMst, null, null);
	}

	/**
	 * 検査グループ(本番テーブル)のリストを返します。
	 *
	 * @param selectMst 本番マスタ(1)か仮マスタ(0)かの指定
	 * @param mstVersion マスタバージョン
	 * @param bctype BC車種区分（nullの場合、全車種分を返す）
	 * @return 検査グループのリスト
	 */
	public List<MGroup> getMGroups(final Integer selectMst, final Integer mstVersion, final String bctype) {

		String conBctype = "";
		if (StringUtils.isNotEmpty(bctype)) {
			conBctype = " AND bctype = '" + bctype + "'";
		} else {
			conBctype = " AND MVehicle.endOfProd = false";
		}

		String conVersion = "";

		// 2016/02/24 DA upd start
		//switch (selectMst) {
		//case 1:
		//	//-----------------------------------------
		//	// 本番マスタ取得
		//	//-----------------------------------------
		//	// 最新バージョンのマスタのみを取得する。
		//	//バージョン指定が0の場合号口レコードを返す。
		//	if (mstVersion == null || mstVersion.equals(0)) {
		//		conVersion = "sopFlag = '1'";
		//	} else {
		//		conVersion = "mstVer = " + mstVersion;
		//	}
		//	break;
		//
		//case 0:
		//	//-----------------------------------------
		//	// 仮マスタ取得
		//	//-----------------------------------------
		//	conVersion = "mstVer = 0";
		//	break;
		//
		//default:
		//	return null;
		//}
		if (MstSelectService.isReal(selectMst)) {
			if (mstVersion == null || MstSelectService.isTemporary(mstVersion)) {
				// 本番マスタ選択で、バージョンが指定されていない場合
				// 本番マスタの最新（号口）データを指定する。
				conVersion = "sopFlag = '1'";
			}
			else {
				// 本番マスタ選択で、バージョンが指定されている場合
				// 本番マスタの指定バージョンのデータを指定する。
				conVersion = "mstVer = " + mstVersion;
			}
		}
		else {
			// 仮マスタ選択の場合
			// 仮マスタのデータを指定する。
			conVersion = "mstVer = " + selectMst;
		}

//		return jdbcManager.from(MGroup.class)
		List<MGroup> mList = jdbcManager.from(MGroup.class)
			.innerJoin("MVehicle")
			.leftOuterJoin("mOrderList")
			.leftOuterJoin("updateMUser")
			.where("deleteFlag <> '1' AND " + conVersion + conBctype)
			.orderBy("line, groupNo, mOrderList.inspecOrder")
			.getResultList();

		for (MGroup mItem : mList) {
			// 値のスペースなどの加工をする
			mItem.groupName = Utils.trimDisplay(mItem.groupName);
		}

		return mList;
		// 2016/02/24 DA upd end
	}

	// 2016/02/24 DA ins start
	/**
	 * 検査グループ(本番テーブル)の表示対象リストを返します。
	 *
	 * @param selectMst 本番マスタ(1)か仮マスタ(0)かの指定
	 * @param bctype BC車種区分（nullの場合、全車種分を返す）
	 * @return 検査グループのリスト
	 */
	public List<MGroup> getMGroupsNonDisplay(final Integer selectMst, final String bctype) {

		String conBctype = "";
		if (StringUtils.isNotEmpty(bctype)) {
			conBctype = " AND bctype = '" + bctype + "'";
		} else {
			conBctype = " AND MVehicle.endOfProd = false";
		}

		String conVersion = "mstVer = " + selectMst;

		return jdbcManager.from(MGroup.class)
			.innerJoin("MVehicle")
			.leftOuterJoin("mOrderList")
			.leftOuterJoin("updateMUser")
			.where("deleteFlag <> '1' AND non_display_flag <> '0' AND " + conVersion + conBctype)
			.orderBy("line, groupNo, mOrderList.inspecOrder")
			.getResultList();
	}
	// 2016/02/24 DA ins end


	/**
	 * 工程名の一覧を取得します。
	 *
	 * @param selectMst 本番マスタ(1)か仮マスタ(0)かの指定
	 * @return 工程名一覧が格納されたSetオブジェクト
	 */
	public LinkedHashSet<String> getMGroupNames(final Integer selectMst) {
		LinkedHashSet<String> groupNames = new LinkedHashSet<String>();

		List<MGroup> groups = getMGroups(selectMst);

		for (MGroup mGroup : groups) {
			groupNames.add(mGroup.groupName);
		}

		return groupNames;
	}

	// 2016/02/24 DA ins start
	/**
	 * 非表示除外した工程名の一覧を取得します。
	 *
	 * @param selectMst 本番マスタ(1)か仮マスタ(0)かの指定
	 * @return 工程名一覧が格納されたSetオブジェクト
	 */
	public LinkedHashSet<String> getMGroupNamesDisplay(final Integer selectMst) {
		LinkedHashSet<String> groupNames = new LinkedHashSet<String>();

		List<MGroup> groups = getMGroups(selectMst);

		for (MGroup mGroup : groups) {
			if (mGroup.nonDisplayFlag == true) {
				continue;
			}
			groupNames.add(mGroup.groupName);
		}

		return groupNames;
	}
	// 2016/02/24 DA ins end

	/**
	 * getMGroups for body.
	 * ボデーNo選択画面用のグループのリストを返します。
	 * @return 検査グループのリスト
	 */
	public List<MGroup> getMGroups4Body() {
		return jdbcManager.from(MGroup.class)
				.innerJoin("mOrderList", false)
				.where("deleteFlag<>'1' and mstVer>0")
				.orderBy("mstVer, groupNo")
				.getResultList();
	}

	// 2016/02/24 DA ins start
	/**
	 * 工程コードより指定のマスタバージョンの工程コードを取得する。
	 * ※仮0と仮1の工程名が同じ場合、仮0の工程コードから仮1の工程コードを取得する。またはその逆。
	 * @param bctype BC車種区分
	 * @param selectMst 本番マスタか仮マスタかの指定
	 * @param mstVersion 使用するマスタバージョン
	 * @param groupCode 工程コード
	 * @return 工程コード
	 */
	public Integer getMGroupByCode(String bctype, Integer selectMst, Integer mstVersion, Integer groupCode)
	{
		// 工程名を取得
		ComplexWhere where1 = new ComplexWhere();
		where1.eq("bctype", bctype);
		where1.eq("groupCode", groupCode);
		where1.eq("deleteFlag", "0");

		MGroup group1 = jdbcManager.from(MGroup.class)
				.where(where1)
				.limit(1)
				.getSingleResult();
		if (group1 == null) {
			return groupCode;
		}

		// マスタバージョンと工程名から工程コードを取得
		ComplexWhere where2 = new ComplexWhere();
		if (MstSelectService.isReal(selectMst)) {
			// 本番マスタ選択の場合
			if (mstVersion == null || MstSelectService.isTemporary(mstVersion)) {
				where2.eq("sopFlag", "1");
			}
			else {
				where2.eq("mstVer", mstVersion);
			}
		}
		else {
			// 仮マスタ選択の場合
			where2.eq("mstVer", selectMst);
		}
		where2.eq("bctype", bctype);
		where2.eq("groupName", group1.groupName);
		where2.eq("deleteFlag", "0");

		MGroup group2 = jdbcManager.from(MGroup.class)
				.where(where2)
				.limit(1)
				.getSingleResult();
		if (group2 == null) {
			return groupCode;
		}

		return group2.groupCode;
	}
	// 2016/02/24 DA ins end

	/**
	 * グループCodeで検索して検査項目のリストを返します。
	 * mstVersionに「0」を指定すると最新のバージョンを取得する。
	 * @param selectMst 本番マスタ(1)か仮マスタ(0)かの指定
	 * @param groupCode グループCode
	 * @param mstVersion 使用するマスタバージョン
	 * @param ptnDiv パターン区分（1:左H、2:右H）
	 * @return 検査グループ
	 * @author sugihara
	 */
	public MGroup getMGroupByName(Integer selectMst, Integer groupCode, Integer mstVersion, String ptnDiv) {
		AutoSelect<MGroup> autoSelect = jdbcManager.from(MGroup.class);

		// 2016/02/24 DA upd start
//		if (selectMst == 1) {
		if (MstSelectService.isReal(selectMst)) {
//			if (mstVersion == null || mstVersion.equals(0)) {
			if (mstVersion == null || MstSelectService.isTemporary(mstVersion)) {
		// 2016/02/24 DA upd end
				autoSelect.where("groupCode = ? AND sopFlag = '1' AND deleteFlag <> '1'", groupCode);
			} else {
				autoSelect.where("groupCode = ? AND mstVer = ? AND deleteFlag <> '1'", groupCode, mstVersion);
			}
		} else {
			// 2016/02/24 DA upd start
//			autoSelect.where("groupCode = ? AND mstVer = '0' AND deleteFlag <> '1'", groupCode);
			autoSelect.where("groupCode = ? AND mstVer = ? AND deleteFlag <> '1'", groupCode, selectMst);
			// 2016/02/24 DA upd end
		}
		return autoSelect
				.leftOuterJoin("mOrderList", "mOrderList.ptnDiv=?", ptnDiv)
				.innerJoin("mOrderList.mItem", "mOrderList.mItem.deleteFlag <> '1'")
				.leftOuterJoin("mOrderList.mItem.tMsgno")
				.leftOuterJoin("mOrderList.mItem.mBcsignList", "mOrderList.mItem.mBcsignList.deleteFlag <> '1'")
				.orderBy("groupNo, mOrderList.inspecOrder")
				.getSingleResult();
	}


	/**
	 * 工程名よりMGroupオブジェクトを取得
	 * @param bctype BC車種区分
	 * @param koutei 工程名
	 * @return MGroupオブジェクト
	 */
	public MGroup getMGroupByName(String bctype, String koutei) {
		MGroup group;
		MGroup shokuGroup = MGroupService.getShokuGroup();

		// 職制・確認工程かどうかで分岐
		if (koutei.equals(shokuGroup.groupName)) {
			group = shokuGroup;
		} else {
			// 2016/02/24 DA upd start
			ComplexWhere mstWhere = new ComplexWhere();
			mstWhere.or().eq("sopFlag", "1");
			List<Integer> codeList = MstSelectService.getTempMasterCodeList();
			for (Integer selectMst : codeList) {
				mstWhere.or().eq("mstVer", selectMst);
			}
			group = jdbcManager.from(MGroup.class)
					.where(new ComplexWhere()
							.eq("bctype", bctype)
							.eq("groupName", koutei)
							.eq("deleteFlag", "0")
							.and(mstWhere))
					.orderBy("sopFlag DESC, mstVer DESC")
					.limit(1)
					.getSingleResult();
			//group = jdbcManager.from(MGroup.class)
			//		.where(new ComplexWhere()
			//				.eq("bctype", bctype)
			//				.eq("groupName", koutei)
			//				.eq("deleteFlag", "0")
			//				.and(new ComplexWhere()
			//						.eq("sopFlag", 1)
			//						.or()
			//						.eq("mstVer", 0)))
			//		.orderBy("sopFlag DESC")
			//		.limit(1)
			//		.getSingleResult();
			// 2016/02/24 DA upd end
		}

		return group;
	}


	/**
	 * 検査済み工程を検索します。
	 * @param bcdata 対象の車両
	 * @return 検査済み工程
	 */
	public List<MGroup> getMGroupWithResult(FBcdata bcdata) {
		List<MGroup> inspectedGroups = jdbcManager.from(MGroup.class)
				.innerJoin("mOrderList", "mOrderList.ptnDiv = ?", bcdata.ptnDiv)
				.innerJoin("mOrderList.fResultList", new SimpleWhere()
						.eq("mOrderList.fResultList.idno", bcdata.idno)
						.eq("mOrderList.fResultList.loDate", bcdata.loDate))
				.getResultList();

		return inspectedGroups;
	}


	/**
	 * 検査グループをテーブルに挿入します。
	 *
	 * @param group 検査グループ
	 * @return 識別子が設定された後の検査グループ
	 */
	public MGroup create(MGroup group) {
		jdbcManager.insert(group)
		.excludes("sopFlag", "deleteFlag")
		.execute();
		return group;
	}

	/**
	 * 検査グループを更新します。
	 *
	 * @param updateGroups 追加／更新レコードのリスト
	 * @param removeGroups 削除レコードのリスト
	 *
	 */
	public void updateAll(List<MGroup> updateGroups, List<MGroup> removeGroups) {
		// 更新レコードにセットするタイムスタンプ
		Timestamp timestamp = new Timestamp(new java.util.Date().getTime());

		System.out.println("updateAll Start");

		// 追加 or 更新
		for (MGroup group : updateGroups) {

			System.out.println("Edit Div = " + group.editDiv);

			if (group.editDiv == null) {
				continue;
			}

			// 2016/02/24 DA ins start
			// 値のスペースなどの加工をする
			group.groupName = Utils.trimDbSetting(group.groupName);
			// 2016/02/24 DA ins end

			if (group.editDiv.equals("I")) {
				// 新規追加
				group.insertDate = timestamp;
				create(group);
				System.out.println("Insert");

			} else if (group.editDiv.equals("U") || group.editDiv.equals("M")) {
				// 更新
				group.updateDate = timestamp;
				updateEntity(group);
				System.out.println("Update");
			}

		}

//		MOrderService orderSrv = new MOrderService();

		// 削除
		for (MGroup group : removeGroups) {

			// 物理削除はしない。deleteFlagのONのみ
			group.updateDate = timestamp;
			//group.deleteFlag = "1";

			//update(group);

			//更新日・更新者・削除フラグのみ更新する。
			deleteEntity(group);

			// 削除対象のグループ内の検査順情報を削除する。
//			orderSrv.deleteByGroupCode(group.groupCode);

			// 2016/02/24 DA upd start
			//jdbcManager.updateBySql("DELETE FROM M_ORDER WHERE MST_VER = 0 AND GROUP_CODE = ?", Integer.class)
			//.params(group.groupCode).execute();
			jdbcManager.updateBySql("DELETE FROM M_ORDER WHERE MST_VER = ? AND GROUP_CODE = ?", Integer.class, Integer.class)
					.params(group.mstVer, group.groupCode)
					.execute();
			// 2016/02/24 DA upd end
		}
	}


	/**
	 * 職制・確認工程用のグループオブジェクト取得
	 * @return 職制・確認工程用MGroupオブジェクト
	 */
	public static MGroup getShokuGroup() {
		MGroup shokuGroup = new MGroup();
		shokuGroup.groupCode = 0;
		shokuGroup.groupNo = "00";
		shokuGroup.groupName = "職制・確認工程";
		return shokuGroup;
	}

	// 2014/04/07 DA ins start
	/**
	 * 工程マスタメンテのエリアリストボックスの取得
	 * @return
	 */
	public ArrayList<Object> getArea() {
		ArrayList<Object> arr = new ArrayList<Object>();

		// フォーマット設定ファイル読み込み
		File areaFormatFile = ResourceUtil.getResourceAsFile("area_list.txt");
		String areaFormat = TextUtil.readUTF8(areaFormatFile);

		// １行目の空文字追加
		MGroup group = new MGroup();
		group = new MGroup();
		group.area = "";
		arr.add(group);

		for (String s : areaFormat.split("\\r\\n")) {
			// ２行目移行を追加
    		group = new MGroup();
    		group.area = s;
    		arr.add(group);
		}

		return arr;
	}

	/**
	 * 工程マスタメンテのラインリストボックスの取得
	 * @return
	 */
	public ArrayList<Object> getLine() {
		ArrayList<Object> arr = new ArrayList<Object>();

		// １行目の空文字追加
		MGroup group = new MGroup();
		group = new MGroup();
		group.line = "";
		arr.add(group);

		// ２行目移行を追加
        for (int i=1; i<=9; i++) {
            String key = String.valueOf(i);

    		group = new MGroup();
    		group.line = key;
    		arr.add(group);

        }
		return arr;
	}
	// 2014/04/07 DA ins end

	// 2014/12/08 DA ins start
	/**
	 * 検査グループ(本番テーブル)のリストを返します。
	 * @return 検査グループのリスト
	 */
	public List<MGroup> getMGroups2() {

		return jdbcManager.from(MGroup.class)
				.where("mstVer > 0")
				.orderBy("line, groupNo, groupName")
				.getResultList();
	}


	/**
	 * 工程名の一覧を取得します。
	 * @return 本番マスタの工程名一覧が格納されたSetオブジェクト
	 */
	public LinkedHashSet<String> getMGroupNames2() {
		LinkedHashSet<String> groupNames = new LinkedHashSet<String>();

		groupNames.add("");	// 1行目の空白行追加
		List<MGroup> groups = getMGroups2();

		for (MGroup mGroup : groups) {
			groupNames.add(mGroup.groupName);
		}

		return groupNames;
	}
	// 2014/12/08 DA ins end

	// 2017/12/01 DA ins start
	/**
	 * 撮影システムで使うマスタ情報を出力する情報を取得する。
	 *
	 * @param selectMst 本番マスタ(1)か仮マスタ(0以下)かの指定
	 * @param mstVersion マスタバージョン
	 * @param bctype 車種
	 * @param groupName 工程名
	 * @return 検査グループのリスト
	 */
	public List<MGroup> getMGroups(final Integer selectMst, final Integer mstVersion, final String bctype, final String groupName)
	{
		ComplexWhere where = new ComplexWhere();
		if (MstSelectService.isReal(selectMst)) {
			// 本番マスタ選択の場合
			if (mstVersion == null || MstSelectService.isTemporary(mstVersion)) {
				where.eq("sopFlag", "1");
			}
			else {
				where.eq("mstVer", mstVersion);
			}
		}
		else {
			// 仮マスタ選択の場合
			where.eq("mstVer", selectMst);
		}
		where.eq("bctype", bctype);
		where.eq("groupName", groupName);
		where.eq("deleteFlag", "0");

		List<MGroup> mList = jdbcManager.from(MGroup.class)
				.innerJoin("MVehicle")
				.leftOuterJoin("mOrderList")
				.innerJoin("mOrderList.mItem", "mOrderList.mItem.deleteFlag = '0'")
				.where(where)
				.orderBy("mOrderList.itemCode, mOrderList.inspecOrder")
				.getResultList();

		return mList;
	}
	// 2017/12/01 DA ins end

	// 2020/01/22 DA ins start
	/**
	 * 工程コードの検査グループを返します。
	 *
	 * @param selectMst 本番マスタ(1)か仮マスタ(0以下)かの指定
	 * @param mstVersion マスタバージョン
	 * @param groupCode 工程コード
	 * @return 検査グループ
	 */
	public MGroup getMGroupByCode(final Integer selectMst, final Integer mstVersion, final Integer groupCode)
	{
		ComplexWhere where = new ComplexWhere();
		if (MstSelectService.isReal(selectMst)) {
			// 本番マスタ選択の場合
			if (mstVersion == null || MstSelectService.isTemporary(mstVersion)) {
				where.eq("sopFlag", "1");
			}
			else {
				where.eq("mstVer", mstVersion);
			}
		}
		else {
			// 仮マスタ選択の場合
			where.eq("mstVer", selectMst);
		}

		where.eq("groupCode", groupCode);
		where.eq("deleteFlag", "0");

		MGroup group = jdbcManager.from(MGroup.class)
				.where(where)
				.limit(1)
				.getSingleResult();

		return group;
	}
	// 2020/01/22 DA ins end
}