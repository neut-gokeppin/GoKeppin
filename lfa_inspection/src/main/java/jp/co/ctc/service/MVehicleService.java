package jp.co.ctc.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.operation.Operations;
import org.seasar.extension.jdbc.service.S2AbstractService;
import org.seasar.extension.jdbc.where.SimpleWhere;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

import jp.co.ctc.entity.FBcdata;
import jp.co.ctc.entity.MBcsign;
import jp.co.ctc.entity.MReserve;
import jp.co.ctc.entity.MVehicle;
import jp.co.ctc.util.Utils;




/**
 * 車種マスタを扱うサービス
 *
 * @author CJ01615
 *
 */
public class MVehicleService extends S2AbstractService<MVehicle> {

	/**
	 * StoredFunctionService
	 */
	@Resource
	public StoredFunctionService storedFunctionService;

	/**
	 * FBcdataService
	 */
	@Resource
	public FBcdataService fBcdataService;

	/**
	 * MstRegistService
	 */
	@Resource
	public MstRegistService mstRegistService;


	/**
	 * 車種マスタのデータを取得します。
	 * @param bctype BC車種区分
	 * @return 車種データ
	 */
	public MVehicle selectById(String bctype) {
		return select().id(bctype).getSingleResult();
	}

	/**
	 * 車種テーブルのリストを取得する。
	 * @return 取得した車種のリスト
	 */
	public List<MVehicle> getMVehicle() {
		return select()
				.where("endOfProd = false")
				.orderBy("vehicleName")
				.getResultList();
	}

	// 2016/02/24 DA upd start
	/**
	 * 検査データ生成。
	 * 仮マスタ本番登録後、新本番マスタで検査データを生成する。
	 * @param bctype BC車種区分
	 */
	public void rebuildInpectionXmlByMstRegist(String bctype, String bcnoH0, String bodyNo, String line) {

//		// 対象の車種情報を取得
//		MVehicle vehicle = selectById(bctype);

		// 2016/09/09 DA upd start
		// 本番登録を予約されていなければ何もしない
		//if (StringUtils.isEmpty(bcnoH0) && StringUtils.isEmpty(bodyNo) && StringUtils.isEmpty(line)) {
		// 組立連番、ボデーNOの両方とも入力がなけれな、検査データ作成処理は不要。
		if (StringUtils.isEmpty(bcnoH0) && StringUtils.isEmpty(bodyNo)) {
			return;
		}

		// 指定された車両のラインを取得
		String strLine = fBcdataService.getLineByBctype(bctype);

		// 車種のラインがなければ、検査データも無いので作成処理は不要。
		if (strLine == null) {
			return;
		}
		// 2016/09/09 DA upd end

		// 仮マスタ本番登録画面にて指定された車両を取得
		FBcdata reservedBody = fBcdataService.selectByBodyNoOrBcno(bodyNo, bcnoH0, strLine);

		// 指定車両がH0を未通過ならば何もしない。
		// （H0通過した時に処理するため）
		//if (reservedBody.tpN0 == null) {
		if (reservedBody == null || reservedBody.tpN0 == null) {
			return;
		}

//		// 号口フラグ切り替え
//		updateSopflag(vehicle);

		// 号口フラグ切替後の新本番マスタで検査データ生成を行う
		// TODO 工程変更時の検査データ再生成。不要になったXMLファイルが残ってしまう
		fBcdataService.createKensaDataByMstRegist(bctype, reservedBody);
	}
	// 2016/02/24 DA upd end


	/**
	 * 号口フラグ切り替え。
	 * 仮マスタ本番登録画面にて予約していた車両が引数に渡されると
	 * 本番マスタの最新バージョンの号口フラグを立てる。
	 * @param bcdata 切り替え判断を行う車両
	 */
	public void updateSopflag(FBcdata bcdata) {
		// 号口フラグを切り替える車種があるか検索
		List<MVehicle> vehicles = select()
				.where("endOfProd = false AND (bodyNo = ? or bcnoH0 = ?)",
						bcdata.bodyNo, bcdata.bcnoH0)
				.getResultList();

		// 号口フラグ切り替え実施
		for (MVehicle vehicle : vehicles) {
			updateSopflag(vehicle);
		}
	}


	/**
	 * 号口フラグ切り替え
	 * @param vehicle 切り替え対象車種
	 */
	public void updateSopflag(MVehicle vehicle) {
		// 号口フラグ切り替えストアド呼び出し
		storedFunctionService.mstSopflag(vehicle.bctype);

		// 車種マスタ更新
		vehicle.bcnoH0 = null;
		vehicle.bodyNo = null;
		update(vehicle);
	}


	/* ITAGE JYO 2013-04-16 仮マスタ本番登録 001 S */

	/**
	 * 車種マスタテーブルのリストを取得する。
	 *
	 * @return 取得した車種マスタのリスト
	 */
	public List<MVehicle> getMVehicleMstRegist() {
		return jdbcManager
				.from(MVehicle.class)
				.leftOuterJoin("updateMUser")
				.orderBy(Operations.asc("endOfProd")    // 生産終了 Falseが上、Trueが下。
						, Operations.asc("vehicleName") // 車種名称
						, Operations.asc("bctype")      // BC車種区分コード
				)
				.getResultList();
	}

	// 2016/02/24 DA upd start
	/**
	 * 車種マスタテーブルのリストを取得する。
	 *
	 * @return 取得した車種マスタのリスト
	 */
	public List<MVehicle> getMVehicleMstRegist(int selectMst) {

		List<MVehicle> lstMVehicle = jdbcManager.from(MVehicle.class)
				.leftOuterJoin("updateMUser")
				.orderBy(Operations.asc("endOfProd")    // 生産終了 Falseが上、Trueが下。
						, Operations.asc("vehicleName") // 車種名称
						, Operations.asc("bctype")      // BC車種区分コード
				)
				.getResultList();

		List<MReserve> lstMReserve = jdbcManager.from(MReserve.class)
				.leftOuterJoin("mUser")
				.where("mstVer = ?", selectMst)         // マスタバージョン = プライベート変数のマスタバージョン
				.getResultList();

		for (MVehicle mVehicle : lstMVehicle){
			for (MReserve mReserve : lstMReserve){
				if (mVehicle.bctype.equals(mReserve.bctype)){
					mVehicle.mReserve = mReserve;
					break;
				}
			}
			mVehicle.bcsignReserve = mstRegistService.isReservedMBcsign(selectMst, mVehicle.bctype);
		}
		return lstMVehicle;
	}

	/**
	 * 検査済み車両有無チェック。
	 * 選択された車種のライン上に、指定された組立連番、ボデーNO以降で全工程検査済みの車両があるかを調べる。
	 *
	 * @param bctype 選択された車種
	 * @param bcnoH0 入力された切替組立連番
	 * @param bodyNo 入力された切替ボデーNo
	 * @param line 入力されたライン（本番登録ライン入力有無パラメータがライン有りの場合）
	 * @return "0"：全工程検査済み車両無し→仮マスタ本番登録可能
	 *         "1"：車種のライン不一致→仮マスタ本番登録不可
	 *         "2"：全工程検査済み車両あり→仮マスタ本番登録不可
	 *         "3"：車種のラインなし→仮マスタ本番登録可能
	 */
	public String isAllNotCheckOfBcdata(String bctype, String bcnoH0, String bodyNo, String line) {

		String strWhereSql = "";

		// 指定された車両のラインを取得
		// 2016/09/09 DA upd start
		String lineGiso = fBcdataService.getLineByBctype(bctype);
		//List<FBcdata> bcDatalist = jdbcManager.from(FBcdata.class)
		//		.innerJoin("fResultsumList", "fResultsumList.groupStatus= '0'")
		//		.where(new SimpleWhere().eq("bctype", bctype).isNotNull("lineGiso", true))
		//		.orderBy("tpN0 DESC")
		//		.getResultList();
		//
		//if (bcDatalist.size() < 1){
		//	// 車両がない場合はラインがないので、ラインチェックは不要
		//	return "0";
		//}
		//
		//// ライン(艤装用)
		//String lineGiso = bcDatalist.get(0).lineGiso;
		// 2016/09/09 DA upd end

		// 本番登録ライン入力有無パラメータがライン有りの場合、取得したラインと入力したラインをチェックする。
		boolean lineInputExistence = (Boolean) SingletonS2ContainerFactory.getContainer().getComponent("lineInputExistence");
		if (lineInputExistence) {
			// 2016/09/09 DA ins start
			// 車種のラインがなければ、以降のチェック処理はスキップし、通常のメッセージでない確認ダイアログで本番登録だけを行う。
			if (lineGiso == null) {
				return "3";
			}
			// 2016/09/09 DA ins end

			// 違う場合、処理を終了する。戻り値を"1"で返す。
			if (!line.equals(lineGiso)) {
				return "1";
			}
		}
		// 2016/09/09 DA ins start
		else {
			// 車種のラインがなければ、以降のチェック処理はスキップし、通常のメッセージでない確認ダイアログで本番登録だけを行う。
			if (lineGiso == null) {
				return "3";
			}
		}

		// 組立連番、ボデーNOの両方とも入力がなけれな、本番登録だけを行うので以降のチェック処理はスキップする。
		if (StringUtils.isEmpty(bcnoH0) && StringUtils.isEmpty(bodyNo)) {
			return "0";
		}
		// 2016/09/09 DA ins end

		strWhereSql = "lineGiso = '" + lineGiso + "'";

		// 指定車両の最新のBCデータを取得する。(1件取得)
		// 検索条件を作成
		if (StringUtils.isEmpty(bcnoH0) && StringUtils.isEmpty(bodyNo)) {
			strWhereSql += "";
		}
		else if (StringUtils.isEmpty(bcnoH0)) {
			strWhereSql += " AND bodyNo = '" + bodyNo + "'";
		}
		else if (StringUtils.isEmpty(bodyNo)) {
			strWhereSql += " AND bcnoH0 = '" + bcnoH0 + "'";
		}
		else {
			strWhereSql += " AND bodyNo = '" + bodyNo + "' AND " + "bcnoH0 = '" + bcnoH0 + "'";
		}

		// 基本データの有無を確認
		FBcdata target = jdbcManager
				.from(FBcdata.class)
				.where(strWhereSql)
				.orderBy("tpN0 DESC")
				.limit(1)
				.getSingleResult();

		// 引数で指定された車両が存在しない
		if (target == null) {
			return "2";
		}

		// 2017/04/26 CTC ins start
		// 本番登録の際、本番登録対象車両より前に検査済み車両が存在すると本番登録できないため、
		// 工程が検査済みかのチェックを削除

//		// 指定された車両以降で、1項目以上検査されている車両を取得
//		List<FBcdata> bclist = jdbcManager.from(FBcdata.class)
//				.innerJoin("fResultsumList", "fResultsumList.groupStatus<> '0'")
//				.where(new SimpleWhere()
//					.ge("tpN0", target.tpN0)
//					.eq("lineGiso", target.lineGiso))
//				.orderBy("tpN0 DESC")
//				.getResultList();
//
//		// 1項目でも検査された車両について、全工程が検査済みかどうか調べる
//		for (FBcdata fBcdata : bclist) {
//			if (fBcdataService.inspectedAllGroups(fBcdata)) {
//				// 全工程検査済み
//				return "2";
//			}
//		}

		// 2017/04/26 CTC ins end

		// 全工程検査済み車両無し
		return "0";
	}

	/**
	 * トランザクション
	 */
	@Resource
    private UserTransaction tx;

	/**
	 * 切替組立連番、切替ボデーNOを更新する。
	 * 仮マスタ本番登録処理（ストアドファンクション）を実行する。
	 * @param vehicle 	更新されたデータ
	 * @param loginUser ログインユーザーのユーザーコード
	 * @param registType 登録種類。nullの場合、仮マスタ本番登録せずに検査データ再生成のみ実行する
	 * 			{@link StoredFunctionService#tempMstRegist(String, String, Integer, boolean)}
	 * @return  true => 更新成功 false=>更新失敗
	 *
	 */
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public String updateBook(MVehicle vehicle, String loginUser, int selectMst, String registType) {

		String bcnoH0 = StringUtils.trimToNull(vehicle.bcnoH0);
		String bodyNo = StringUtils.trimToNull(vehicle.bodyNo);

		// 予約中チェック
		if (mstRegistService.isReservedVehicle(selectMst, vehicle.bctype)){
			return "1";
		}

		// マスタ存在チェック
		if (mstRegistService.isTemporaryNoMaster(selectMst, vehicle.bctype)) {
			return "4";
		}
		if (StringUtils.equals(registType, "1")) {
			// 検査順は本番マスタもチェックする
			if (mstRegistService.isRealNoMaster(vehicle.bctype)) {
				return "4";
			}
		}

//		// データがない場合は、NULLをセットする
//		if (vehicle.bcnoH0 != null && vehicle.bcnoH0.equals("")) {
//			vehicle.bcnoH0 = null;
//		}
//		// データがない場合は、NULLをセットする
//		if (vehicle.bodyNo != null && vehicle.bodyNo.equals("")) {
//			vehicle.bodyNo = null;
//		}

		// 画面上に更新日、更新者を表示するために更新する。
		vehicle.updateDate = Utils.nowts();
		vehicle.updateUser = loginUser;
		vehicle.bcnoH0 = null;
		vehicle.bodyNo = null;

		/**
		 *  0 => 正常終了
		 * -1 => 異常終了
		 */
		int ngFlg = -1;	//エラー等でfalseを返す状態であるか。

		try {
			// トランザクションの開始
            tx.begin();

            jdbcManager.update(vehicle)
            .excludes("listOkFlag")
            .execute();

            // registTypeによって処理内容を変える
            if (StringUtils.isEmpty(registType)) {
            	// registTypeが空の場合、仮マスタ本番登録しない。検査データ再生成のみ実行する
            	ngFlg = 0;
            } else {
				// 指示記号を削除をする
				// ※削除した指示記号が本番登録できるように本番削除フラグは落とさない。
				List<MBcsign> mBcsignList = new ArrayList<MBcsign>();
				SimpleWhere sWhere = null;
				if (registType.equals("0")) {
					sWhere = new SimpleWhere()
							.eq("mstVer", selectMst)
							.eq("sopDeleteFlag", "1")
							.eq("mItemList.bctype", vehicle.bctype);
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
				ngFlg = storedFunctionService.tempMstRegist(selectMst, vehicle.bctype, registType, null, null, false);

				// 指示記号を削除をする
				// ※本番登録が終わったので本番削除フラグを落とす。
				for (MBcsign mBcsign : mBcsignList) {

					mBcsign.sopDeleteFlag = "0";

					// すでに削除フラグが立っていることで、この後で行う予約フラグの更新がされないので、ここで予約フラグの更新も行う。
					if (mBcsign.reserveFlag.equals("2")) {
						mBcsign.reserveFlag = "3";
					}

					mBcsign.updateUser = loginUser;
					mBcsign.updateDate = Utils.nowts();
					jdbcManager.update(mBcsign).execute();
				}
            }

            if (!StringUtils.isEmpty(bcnoH0) || !StringUtils.isEmpty(bodyNo)){

                // ストアドが成功したら、検査データ生成処理を呼び出す
                if (ngFlg == 0) {
                	rebuildInpectionXmlByMstRegist(vehicle.bctype, bcnoH0, bodyNo, vehicle.line);
                }
                else {
                	return "2";
                }
            }
		} catch (Exception e) {
            e.printStackTrace();
            try {
            	// トランザクションをロールバック
                tx.setRollbackOnly();
            } catch (IllegalStateException e1) {
                e1.printStackTrace();
                return "3";
            } catch (SystemException e2) {
                e2.printStackTrace();
                return "3";
            }
            return "3";
        } finally {
            try {
            	//
                if (tx.getStatus() == Status.STATUS_ACTIVE && ngFlg != -1) {
                	// コミット
                    tx.commit();

                } else {
                	// トランザクションをロールバック
                    tx.rollback();
                    return "3";
                }
            } catch (Exception e3) {
                e3.printStackTrace();
                return "3";
            }
        }

        return "0";

	}
	// 2016/02/24 DA upd end

	/**
	 * 切替組立連番、切替ボデーNOを更新する。
	 *
	 * @param vehicle 	更新されたデータ
	 * @param loginUser ログインユーザーのユーザーコード
	 *
	 */
	public void updateCancel(MVehicle vehicle, String loginUser) {
		// 更新レコードにセットするタイムスタンプ
		Timestamp timestamp = new Timestamp(new java.util.Date().getTime());

		//
		vehicle.bcnoH0 = null;
		vehicle.bodyNo = null;
		//
		vehicle.updateDate = timestamp;
		vehicle.updateUser = loginUser;
		jdbcManager.update(vehicle).execute();

	}

	/* ITAGE JYO 2013-04-16 仮マスタ本番登録 001 E */

	// 2016/02/24 DA del start
//	/**
//	 * 仮マスタ本番登録の即時実行。
//	 * 車種マスタを更新し、ストアドプロシージャを実行する。
//	 *
//	 * @param vehicle 処理対象車種
//	 * @param loginUser ログインユーザー
//	 * @param registType 登録種類
//	 * @return 処理結果。成功 or 失敗
//	 */
//	public int updateImmediate(MVehicle vehicle, String loginUser, String registType) {
//
//		// 画面上に更新日、更新者を表示するために更新する。
//		vehicle.updateDate = Utils.nowts();
//		vehicle.updateUser = loginUser;
//		update(vehicle);
//
//
//		return storedFunctionService.tempMstRegist(0, vehicle.bctype, registType, null, false);
//
//
//	}
	// 2016/02/24 DA del end

	/**
	 * 車両マスタを削除、更新します。
	 * 2014/11/20 DA ins
	 * @param updateVehicle
	 * @param deleteVehicle
	 *            追加／更新レコードのリスト
	 *
	 */
	public String updateAll(List<MVehicle> updateVehicle, List<MVehicle> deleteVehicle) {
		String msg = "";
		// 更新レコードにセットするタイムスタンプ
		Timestamp timestamp = Utils.nowts();

		for (MVehicle vehicle : deleteVehicle) {
			int result = storedFunctionService.deleteVehicle(vehicle.bctype);
			if (result == -1) {
				msg = "BC車種区分:" + vehicle.bctype + "  車種名称:" + vehicle.vehicleName + " は削除できません";
				return msg;
			}
		}

		// 追加 or 更新
		for (MVehicle vehicle : updateVehicle) {

			if (vehicle.editDiv == null) {
				continue;
			}

			// 2016/02/24 DA ins start
			// 空白文字を削除する
			vehicle.bctype = vehicle.bctype.trim();
			vehicle.vehicleName= vehicle.vehicleName.trim();
			// 2016/02/24 DA ins end

			if (vehicle.editDiv.equals("I")) {
				// 新規追加
				vehicle.insertDate = timestamp;
				insert(vehicle);

			} else if (vehicle.editDiv.equals("U")) {
				// 更新
				vehicle.updateDate = timestamp;
				update(vehicle);
			}
		}

		return msg;
	}

	/**
	 * BC車種区分をコピーする。
	 * @param bctypeFrom	BC車種区分 コピー元
	 * @param bctypeTo		BC車種区分 コピー先
	 * @param sop			号口フラグ　0:仮、1:本番
	 * @param userCode		ユーザーコード
	 * @return 処理結果 0:正常、-1:異常
	 */
	// 2016/02/24 DA upd start
	public int copyVehicle(String bctypeFrom, String bctypeTo, String sop, Integer mstVerFrom, Integer mstVerTo, String userCode) {
		int result = storedFunctionService.copyVehicle(bctypeFrom, bctypeTo, sop, mstVerFrom, mstVerTo, userCode);
	// 2016/02/24 DA upd end
		return result;
	}
}
