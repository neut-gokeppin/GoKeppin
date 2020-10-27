package jp.co.ctc.service;

import java.sql.Timestamp;

import javax.annotation.Resource;

import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.extension.jdbc.annotation.ResultSet;


/**
* サーバーの関数をCallするサービスです。
*
* @author Kato
*
*/
@SuppressWarnings("unused")
public class StoredFunctionService {

	/* ITAGE JYO 2013-05-17 仮マスタ本番登録 001 S */

	/**
	 * 引数あり(リターンコードのみ取得)のStoredFunction Call時用クラス
	 */
	private class mstRegist {
		// 2016/02/24 DA ins start
		/**
		 * マスタバージョン
		 */
		public Integer mstVer;	// IN
		// 2016/02/24 DA ins end

		/**
		 * BC車種区分
		 */
		public String bctype;	// IN

		/**
		 * 登録種類
		 */
		public String registType;	// IN

		/**
		 * 登録検査項目
		 */
		public String registItem;	// IN

		// 2016/02/24 DA ins start
		/**
		 * 予約日時
		 */
		public Timestamp reserveDate;	// IN
		// 2016/02/24 DA ins end

		/**
		 * 即時切替かどうかのフラグ
		 */
		public boolean isbook;	// IN

		@ResultSet
		int retCode;
	}

	/* ITAGE JYO 2013-05-17 仮マスタ本番登録 001 E */

	// 2014/11/26 DA ins start
	/**
	 * 引数あり(リターンコードのみ取得)のストアド Call時用クラス
	 */
	private class clsDel {
		/**
		 * BC車種区分
		 */
		public String bctype;	// IN

		@ResultSet
		int retCode;
	}
	/**
	 * 引数あり(リターンコードのみ取得)のストアド Call時用クラス
	 */
	private class clsCopy {
		public String bctypeFrom;
		public String bctypeTo;
		public String sop;

		// 2016/02/24 DA ins start
		public Integer mstVerFrom;
		public Integer mstVerTo;
		// 2016/02/24 DA ins end

		public String userCode;

		@ResultSet
		int retCode;
	}
	// 2014/11/26 DA ins end

	/**
	 * JDBCマネージャ
	 */
	@Resource
	public JdbcManager jdbcManager;

	/**
	 * mst_registファンクションを呼び出します。
	 * @param mstVer        マスタバージョン
	 * @param bctype        BC車種区分
	 * @param registType    登録種類。どのマスタを仮から本番に登録するか選択。
	 *                          0:全マスタ、1:工程/項目/検査順、2:指示記号全て、3:検査項目ごと指示記号、4:指示記号、5:指示記号予約
	 * @param registItem    登録検査項目。または、登録指示記号
	 *                          in_regist_type=3 のとき、指定された検査項目に紐付く指示記号を仮マスタ本番登録する。
	 *                          in_regist_type=4 のとき、指定された指示記号を仮マスタ本番登録する。
	 *                          それ以外は無視。
	 *                          複数指定する場合は、カンマ区切り。※配列だとデータの受け渡しが上手くいかなかったので文字列対策した。
	 * @param reserveDate   予約登録日時。予約日時以前の検査項目に紐付く指示記号を仮マスタ本番登録する。
	 *                          in_regist_type=5 のときのみ使用。それ以外は無視。
	 * @param isbook        true:予約、false:即時切替
	 * @return 処理結果（0:正常、-1:異常）
	 */
	// 2016/02/24 DA upd start
	public int tempMstRegist(Integer mstVer, String bctype, String registType, String registItem, Timestamp reserveDate, boolean isbook) {
	// 2016/02/24 DA upd end

		/* ITAGE JYO 2013-05-17 仮マスタ本番登録 002 S */
		//onlyRetCode funcPara = new onlyRetCode();

		mstRegist funcPara = new mstRegist();
		// 2016/02/24 DA ins start
		funcPara.mstVer = mstVer;
		// 2016/02/24 DA ins end
		funcPara.bctype = bctype;
		funcPara.registType = registType;
		funcPara.registItem = registItem;
		// 2016/02/24 DA ins start
		if (reserveDate == null) {
			funcPara.reserveDate = new Timestamp(0);
		}
		else {
			funcPara.reserveDate = reserveDate;
		}
		// 2016/02/24 DA ins end
		funcPara.isbook = isbook;
		/* ITAGE JYO 2013-05-17 仮マスタ本番登録 002 E */


		jdbcManager.call("mst_regist", funcPara).execute();

		if (funcPara.retCode == 0) {
			// 正常終了
			return 0;
		}
		// 異常終了
		return -1;

	}

	/**
	 * mst_sopflagファンクションを呼び出します。
	 * @param bctype BC車種区分
	 * @return 処理結果。0:正常、0以外:異常
	 */
	public int mstSopflag(String bctype) {

		return jdbcManager.call(Integer.class, "mst_sopflag", bctype).getSingleResult();

	}

	/**
	 * 車種区分に紐づくテーブルの削除.
	 * @param bctype BC車種区分
	 * @return 処理結果 0:正常、-1:異常終了
	 */
	public int deleteVehicle(String bctype) {

		clsDel funcPara = new clsDel();
		funcPara.bctype = bctype;

		jdbcManager.call("delete_vehicle", funcPara).execute();
		if (funcPara.retCode == 0) {
			// 正常終了
			return 0;
		}
		// 異常終了
		return -1;
	}

	/**
	 * 車種区分に紐づくテーブルのコピー.
	 * @param bctypeFrom	BC車種区分 コピー元
	 * @param bctypeTo		BC車種区分 コピー先
	 * @param sop			号口フラグ　0:仮、1:本番
	 * @param userCode		ユーザーコード
	 * @return 処理結果 0:正常、-1:異常
	 */
	public int copyVehicle(String bctypeFrom, String bctypeTo, String sop, Integer mstVerFrom, Integer mstVerTo, String userCode) {

		clsCopy funcPara = new clsCopy();
		funcPara.bctypeFrom = bctypeFrom;
		funcPara.bctypeTo = bctypeTo;
		funcPara.sop = sop;
		funcPara.mstVerFrom = mstVerFrom;

		// 2016/02/24 DA ins start
		funcPara.mstVerTo = mstVerTo;
		funcPara.userCode = userCode;
		// 2016/02/24 DA ins end

		jdbcManager.call("copy_vehicle", funcPara).execute();
		if (funcPara.retCode == 0) {
			// 正常終了
			return 0;
		}
		// 異常終了
		return -1;
	}

}
