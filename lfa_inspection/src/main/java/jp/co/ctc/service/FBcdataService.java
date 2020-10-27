/**
 *
 */
package jp.co.ctc.service;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.seasar.extension.jdbc.service.S2AbstractService;
import org.seasar.extension.jdbc.where.ComplexWhere;
import org.seasar.extension.jdbc.where.SimpleWhere;
import org.seasar.extension.timer.TimeoutManager;
import org.seasar.extension.timer.TimeoutTarget;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.log.Logger;
import org.seasar.framework.util.DateConversionUtil;
import org.seasar.framework.util.FileInputStreamUtil;
import org.seasar.framework.util.FileUtil;
import org.seasar.framework.util.IntegerConversionUtil;
import org.seasar.framework.util.ResourceUtil;
import org.seasar.framework.util.TextUtil;
import org.seasar.struts.util.ServletContextUtil;

import jp.co.ctc.dto.InspectionTireInfoDto;
import jp.co.ctc.entity.FBcdata;
import jp.co.ctc.entity.FBcdataDummy;
import jp.co.ctc.entity.FResult;
import jp.co.ctc.entity.FResultsum;
import jp.co.ctc.entity.MBcsign;
import jp.co.ctc.entity.MGroup;
import jp.co.ctc.entity.MItem;
import jp.co.ctc.entity.MOrder;
import jp.co.ctc.entity.TMsgno;
import jp.co.ctc.entity.TSf;
import jp.co.ctc.entity.TSfbc1;
import jp.co.ctc.entity.TSfbc2;
import jp.co.ctc.entity.TSfbc3;
import jp.co.ctc.entity.TSfbc4;
import jp.co.ctc.util.LfaCommon;
import jp.co.ctc.util.LfaCommon.AreaList;
import jp.co.ctc.util.LfaCommon.ModeList;
import jp.co.ctc.util.LfaCommon.SELECTSTATUSLIST;
import jp.co.ctc.util.LfaCommon.TIREMAKER_JUDGMENT;
import jp.co.ctc.util.ServletUtil;
import jp.co.ctc.util.ServletUtil.State;
import jp.co.ctc.util.UniqUtils;
import jp.co.ctc.util.Utils;


/**
 * BCデータを扱うサービスです。
 *
 * BCデータ取込処理をタイマー起動するため、
 * TimeoutTargetインタフェースを実装します。
 *
 * @author sugihara
 *
 */
public class FBcdataService extends S2AbstractService<FBcdata> implements TimeoutTarget {

	// 2014/10/30 DA upd start
//	private final String AREA_GISO = "艤装";
//	private final String AREA_SALES = "セールス";
	private final String AREA_GISO = "01";	// 艤装
	private final String AREA_SALES = "02";	// セールス
	private final String AREA_03 = "03";	// 拡張用
	// 2014/10/30 DA upd end

	/**
	 * ログ出力用
	 */
	private Logger logger = Logger.getLogger(this.getClass());

	/**
	 * MVehicleService
	 */
	@Resource
	public MVehicleService mVehicleService;

	/**
	 * MGroupService
	 */
	@Resource
	public MGroupService mGroupService;

	/**
	 * MOrderService
	 */
	@Resource
	public MOrderService mOrderService;

	/**
	 * FBcdataDummyService
	 */
	@Resource
	public FBcdataDummyService fBcdataDummyService;

	/**
	 * FResultService
	 */
	@Resource
	public FResultService fResultService;

	/**
	 * FResultsumService
	 */
	@Resource
	public FResultsumService fResultsumService;

	/**
	 * TSfService
	 */
	@Resource
	public TSfService tSfService;

	/**
	 * TSfbcService
	 */
	@Resource
	public TSfbcService tSfbcService;

	/**
	 * TMsgnoService
	 */
	@Resource
	public TMsgnoService tMsgnoService;

	/**
	 * 使用するマスターの種類 0:仮マスタ 1:本番マスタ
	 * 開発時だけ、仮マスタを使用する
	 */
	public static final Integer SELECT_MST = 1;

	/**
	 * ALC受信プログラムフォルダ
	 */
	public String alcHomePath = "";

	// 2014/04/07 DA ins start
	/**
	 * 検査判定リストの各項目の位置
	 */
	public static class JUDGMENT_TIRE_LIST
	{
		/** 工程No */
		public static final int GROUPNO = 0;
		/** 検査順 */
		public static final int ORDERSIGNNO = 1;
		/** 検査項目 */
		public static final int ITEMNAME = 2;
		/** 測定値 */
		public static final int INPUTDATA = 3;
		/** 回数 */
		public static final int INSPECNO = 4;
		/** 判定結果 */
		public static final int INSPECRESULT = 5;
		/** NG内容 */
		public static final int NGREASON = 6;
		/** 選択状態 */
		public static final int SELECTSTATUSLIST = 7;
	}
	// 2014/04/07 DA ins end


	/**
	 * 検査用のBCデータを取得します。
	 * BCデータを取得する際、検査結果サマリー（F_RESULTSUMテーブル）を結合します。
	 * @return BCデータのList
	 */
	public List<FBcdata> getFBcdataWithResultsum() {
		List<FBcdata> fBcdataList = jdbcManager.from(FBcdata.class)
			.leftOuterJoin("fResultsumList")
			.orderBy("bodyNo, recvDay, fResultsumList.groupCode")
			.getResultList();

		return fBcdataList;
	}


	/**
	 * 最後尾から1000台分の車両データを取得します。
	 *
	 * @return 最後尾から1000台分の車両データ
	 */
	public List<FBcdata> getLastFBcdata() {
		final int count = 1000;

		return select()
				.innerJoin("mVehicle")
				.leftOuterJoin("fResultsumList")
				.orderBy("tpN0 DESC")
				.limit(count)
				.getResultList();
	}


	/**
	 * 物流用のBCデータを取得します。
	 * @return BCデータのList
	 */
	public List<FBcdata> getLogFBcdata() {
		List<FBcdata> fBcdataList = jdbcManager.from(FBcdata.class)
			.leftOuterJoin("fTakeResultList")
			.orderBy("bodyNo, recvDay, fTakeResultList.takeNo Desc")
			.getResultList();

		if (fBcdataList != null) {
			for (int idx = 0; idx < fBcdataList.size(); idx++) {
				String idNo = fBcdataList.get(idx).idno;

				fBcdataList.get(idx).tSf = jdbcManager.from(TSf.class)
				.where("idno = ?", idNo)
				.getSingleResult();

				fBcdataList.get(idx).tSfbc1 = jdbcManager.from(TSfbc1.class)
						.where("idno = ?", idNo)
						.getSingleResult();

				fBcdataList.get(idx).tSfbc2 = jdbcManager.from(TSfbc2.class)
						.where("idno = ?", idNo)
						.getSingleResult();

				fBcdataList.get(idx).tSfbc3 = jdbcManager.from(TSfbc3.class)
						.where("idno = ?", idNo)
						.getSingleResult();

				fBcdataList.get(idx).tSfbc4 = jdbcManager.from(TSfbc4.class)
						.where("idno = ?", idNo)
						.getSingleResult();
			}
		}

		return fBcdataList;
	}


	/**
	 * 物流用のBCデータを取得します。
	 * BCデータを取得する際、取出結果サマリー（LG_F_TAKERESULTSUMテーブル）
	 * を結合します。
	 * 過去全ての車両データを対象とするとレスポンスが遅くなるため、
	 * 2ヶ月前までの車両データを対象とします。
	 *
	 * @return BCデータのList
	 */
	public List<FBcdata> getLogFBcdataWithResultsum() {
		final int months = -2;
		Date date = DateUtils.addMonths(Utils.now(), months);
		String strDate = DateFormatUtils.format(date, "yyyyMMdd");

		List<FBcdata> fBcdataList = jdbcManager.from(FBcdata.class)
			.innerJoin("tSf")
			.leftOuterJoin("fTakeResultsumList")
			.where("recvDay>=? or bodyNo like 'T%'", strDate)
			.orderBy("bodyNo, recvDay, fTakeResultsumList.spsCode")
			.getResultList();

		return fBcdataList;
	}


	/**
	 * アイデントNo、ラインオフ計画日からデータを取得する
	 * @param idno アイデントNo
	 * @param loDate ラインオフ計画日
	 * @return 取得したFBcdata
	 */
	public FBcdata getFBcdata(String idno, String loDate) {

		FBcdata fBcdata = select()
				.innerJoin("mVehicle")
				.innerJoin("tSf")
				.id(idno, loDate)
				.getSingleResult();

		// SQLを1回にまとめるとSQL実行エラーになるため、
		// 4回に分けて実行する
		tSfbcService.getTSfbcAll(fBcdata);

		return fBcdata;
	}


	/**
	 * FBcdataレコードを取得します。
	 * @param idno アイデントNo
	 * @param loDate ラインオフ計画日
	 * @return FBcdataレコード
	 */
	public FBcdata getSimpleFBcdata(String idno, String loDate) {
		FBcdata fBcdata = select()
				.id(idno, loDate)
				.getSingleResult();
		return fBcdata;
	}


	/**
	 * ボデーNo. 受信日からデータを取得する
	 * @param bodyNo ボデーNo.
	 * @param recvDay 受信日
	 * @return 取得したFBcdataのリスト
	 */
	public List<FBcdata> getLogFBcdata(String bodyNo, String recvDay) {

		List<FBcdata> fBcdataList = jdbcManager.from(FBcdata.class)
		.innerJoin("tSf")
		.leftOuterJoin("fTakeResultList")
		.where("bodyNo = ? AND recvDay = ?", bodyNo, recvDay)
		.orderBy("bodyNo, recvDay, fTakeResultList.takeNo Desc")
		.getResultList();
		if (fBcdataList != null) {
			for (int idx = 0; idx < fBcdataList.size(); idx++) {
				String idNo = fBcdataList.get(idx).idno;
				fBcdataList.get(idx).tSfbc1 = jdbcManager.from(TSfbc1.class)
						.where("idno = ?", idNo)
						.getSingleResult();

				fBcdataList.get(idx).tSfbc2 = jdbcManager.from(TSfbc2.class)
						.where("idno = ?", idNo)
						.getSingleResult();

				fBcdataList.get(idx).tSfbc3 = jdbcManager.from(TSfbc3.class)
						.where("idno = ?", idNo)
						.getSingleResult();

				fBcdataList.get(idx).tSfbc4 = jdbcManager.from(TSfbc4.class)
						.where("idno = ?", idNo)
						.getSingleResult();
			}
		}
		return fBcdataList;
	}


	/**
	 * 次処理車両情報取得
	 * @param kouTei 検索対象の工程名
	 * @param body ボデーNO
	 * @return 取得した車両のリスト
	 */
	public List<String[]> getNextFBcdataWithResultsum(String kouTei, String body) {
		Calendar cal = Calendar.getInstance();
		Date nowtime = cal.getTime();
		Timestamp startTime = new Timestamp(nowtime.getTime());
		// 2016/02/24 DA upd start
//		cal.add(Calendar.MONTH , -2);
//		Date pretime = cal.getTime();
//		Timestamp overTime = new Timestamp(pretime.getTime());
		// 検査データ有効日数パラメータ の取得
		int inspectionExpireDays = (Integer) SingletonS2ContainerFactory.getContainer().getComponent("inspectionExpireDays") * -1;
		// (現在日 - 検査データ有効日数パラメータ)
		cal.add(Calendar.DAY_OF_MONTH , inspectionExpireDays);
		Date pretime = cal.getTime();
		Timestamp overTime = new Timestamp(pretime.getTime());
		// 2016/02/24 DA upd end

		// 2014/04/07 DA ins start
		// 2016/02/24 DA upd start
		ComplexWhere mstWhere = new ComplexWhere();
		mstWhere.or().eq("sopFlag", "1");
		List<Integer> codeList = MstSelectService.getTempMasterCodeList();
		for (Integer selectMst : codeList) {
			mstWhere.or().eq("mstVer", selectMst);
		}
		List<MGroup> groups = jdbcManager.from(MGroup.class)
				.where(new ComplexWhere()
						.eq("groupName", kouTei)
						.eq("deleteFlag", "0")
						.and(mstWhere))
				.orderBy("sopFlag DESC, mstVer DESC")
				.getResultList();

//		List<MGroup> groups = jdbcManager.from(MGroup.class)
//				.where("groupName = ? "
//						+ " AND deleteFlag = '0'"
//						+ " AND (sopFlag = '1' OR mstVer = 0)", kouTei)
//				.orderBy("sopFlag DESC")
//				.getResultList();
		// 2016/02/24 DA upd end
		if (groups.size() == 0) {
			String msg = "工程マスタのエリアが取得できませんでした。" + kouTei;
			logger.error(msg);
			throw new RuntimeException(msg);
		}
		// 2014/04/07 DA ins end

		// 工程コードのリスト作成
		Map<String, Integer> groupCodeMap = new HashMap<String, Integer>();
		for (MGroup mGroup : groups) {
			groupCodeMap.put(mGroup.bctype, mGroup.groupCode);
		}

		// 2014/04/07 DA upd start
		// 艤装とセールで条件分け
		MGroup group = groups.get(0);
		String fieldTp = null;
		String fieldLine = null;
//		if (group.area.equals(AREA_GISO)) {	// 艤装
		if (group.area.startsWith(AREA_GISO)) {	// 艤装  2014/10/30 DA upd
			fieldTp = "tpN0";
			fieldLine = "lineGiso";
//		} else if (group.area.equals(AREA_SALES)) {	// セールス
		} else if (group.area.startsWith(AREA_SALES)) {	// セールス 2014/10/30 DA upd
			fieldTp = "tpSales";
			fieldLine = "lineSales";
		} else if (group.area.startsWith(AREA_03)) {	// 拡張 2014/10/30 DA ins
			fieldTp = "tpArea03";						// 2014/10/30 DA ins
			fieldLine = "lineArea03";					// 2014/10/30 DA ins
		}

		// 並び取得の基準となる車両を取得
		FBcdata bcdata = null;

		//DBのタイムアウトを設定
		int dbTimeout = (Integer) SingletonS2ContainerFactory.getContainer().getComponent("dbTimeout");
		String sql = "set statement_timeout = " + Integer.toString(dbTimeout) ;
		jdbcManager
	    .callBySql(sql)
	    .execute();

//		Integer dbTimeout =
//		jdbcManager
//	        .selectBySql(
//	            Integer.class,
//	            "set statement_timeout = 10000")
//	        .getSingleResult();


		if (StringUtils.isEmpty(body)) {
			// 最後に検査された車両
			bcdata = select()
					.innerJoin("fResultsumList")
					.where(
							new SimpleWhere()
							// 2016/02/24 DA upd start
//							.ge(fieldTp, overTime)
							.ge("loDate", overTime)
							// 2016/02/24 DA upd end
							.le(fieldTp, startTime)
							.eq(fieldLine, group.line)
							.ne("fResultsumList.groupStatus", 0)
							.in("fResultsumList.groupCode", groupCodeMap.values())
							)
					.orderBy(fieldTp + " DESC")
					.limit(1)
					.getSingleResult();
		} else {
			// 指定された車両を取得
			// 同じボデーNOの車両は、工場内に同時に複数存在しないため、
			// 艤装とセールスの分岐は不要
			bcdata = selectByBodyNoOrBcno(body, null);
		}
		// 2014/04/07 DA upd end
		// 2016/02/24 DA ins start
		if (bcdata == null) {
			// 取得できない場合は車両がないので処理を終わる
			return new ArrayList<String[]>();
		}
		// 2016/02/24 DA ins end

		// 2018/06/28 DA ins start
		if (Utils.readDeclaredField(bcdata, fieldTp) == null) {
			// TP通過日時が取得できない場合は次の車両を特定できないので処理を終わる
			return new ArrayList<String[]>();
		}
		// 2018/06/28 DA ins end

		// 2014/04/07 DA upd start
		// 工程名の最後から2文字目が"U"の場合、うさぎ追いと判定
		int nameLength = kouTei.length();
		int usagioi = 0;	// うさぎ追いの数
		if (kouTei.substring(nameLength - 2, nameLength - 1).equals("U")) {
			usagioi = Integer.parseInt(kouTei.substring(nameLength - 1));
		}

		// 2014/10/30 DA ins start
		SimpleWhere sWhere = new SimpleWhere()
			.gt(fieldTp, Utils.readDeclaredField(bcdata, fieldTp))
			.eq(fieldLine, group.line);
		// エリアが艤装、セールスの場合条件追加
		if (group.area.startsWith(AREA_GISO)
				|| group.area.startsWith(AREA_SALES)) {

			// 次処理車両取得の追加条件
			UniqUtils uUtils = new UniqUtils();
			sWhere = uUtils.getNextFBcdataWithResultsumOption(sWhere);
		}
		// 2014/10/30 DA ins end

		// 2017/03/29 CT upd start
/*
		// 2016/02/24 DA ins start
		ComplexWhere sWhereResultsum = new ComplexWhere()
				.eq("fResultsumList.groupStatus", 0)
				.in("fResultsumList.groupCode", groupCodeMap.values())
				.or().isNull("fResultsumList.groupCode", true);
		// 2016/02/24 DA ins end
*/
		// 基準車両と同じ工程を条件に追加
		ComplexWhere sWhereResultsum = new ComplexWhere()
				.in("fResultsumList.groupCode", groupCodeMap.values())
				.or().isNull("fResultsumList.groupCode", true);
		// 2017/03/29 CT upd end

		// 基準から4台分の車両を取得
		List<FBcdata> fBcdataList = select()
				// 2016/02/24 DA ins start
				.leftOuterJoin("fResultsumList")
				// 2016/02/24 DA ins end
				.where(
//						2014/11/06 DA upd start
//						new SimpleWhere()
//						.gt(fieldTp, Utils.readDeclaredField(bcdata, fieldTp))
//						.eq(fieldLine, group.line)
						sWhere, sWhereResultsum
//						2014/11/06 DA upd end
						)
				.orderBy(fieldTp + ", insertDate, bcnoH0")
				.limit(4)
				.offset(usagioi)
				.getResultList();
		// 2014/04/07 DA upd end

		// 返却用のリスト作成
		List<String[]> res = new ArrayList<String[]>();
		for (FBcdata fBcdata : fBcdataList) {
			String groupCode = groupCodeMap.get(fBcdata.bctype).toString();
			// 2016/02/24 DA upd start
			String[] a = {fBcdata.idno, fBcdata.loDate, groupCode, fBcdata.bodyNo};
			// String[] a = {fBcdata.idno, fBcdata.loDate, groupCode};
			// 2016/02/24 DA upd end
			res.add(a);
		}

		return res;
	}


	/**
	 * 処理対象のボディよりデータを取得。
	 * @param koutei 工程ネーム
	 * @param bodyNo ボディNo
	 * @param mode モード（訓練-本番マスタ, 訓練-仮マスタ, 本番）
	 * @return {アイデントNo、ラインオフ計画日、工程コード} の配列
	 */
	public String[] getBodyResultsum(String koutei, String bodyNo, ModeList mode) {
		// 車両情報を取得
		FBcdata bcdata = selectByBodyNoOrBcno(bodyNo, null);

		// 工程を取得
		MGroup group = mGroupService.getMGroupByName(bcdata.bctype, koutei);

		if (mode == ModeList.production) {
			//通過日時更新
			boolean isJudgment = LfaCommon.isJudgmentGroupPassDatetime(koutei);
			if (isJudgment) {
				updateTp(koutei, bodyNo);
			}
		}

		// 返却用の配列作成
		String[] res = {bcdata.idno, bcdata.loDate, group.groupCode.toString()};

		return res;
	}


	/**
	 * BCデータをデータベースに取り込むためのタイマーを起動します。
	 * このメソッドは、S2Container起動時に自動的に呼び出すよう、
	 * app.diconにて設定しています。
	 * @param arg ALC受信プログラムフォルダ
	 * @param permanent タイマーを永続的に実行するか。true:永続実行、false:1回だけ実行
	 */
	public void importStart(String arg, boolean permanent) {
		// ALC受信フォルダをセット
		alcHomePath = arg;
		// タイマー実行間隔
		final int interval = 10;

		// タイマー起動
		TimeoutManager timeoutManager = TimeoutManager.getInstance();
		timeoutManager.addTimeoutTarget(this, interval, permanent);
	}


	/**
	 * 定期的に実行する処理。
	 * このメソッドは、importStartメソッドにて指定したタイミングで
	 * 定期的に実行されます。
	 */
	@Override
	public void expired() {

		// ファイル一覧の取得
		File dir = new File(alcHomePath);
		if (!dir.exists()) {
			// ディレクトリが存在しなければ、何もしない。
			logger.error("ディレクトリが存在しません。" + dir.getAbsolutePath());
			return;
		}

		// BCフォーマット設定ファイル読み込み
		LinkedHashMap<String, Integer> bcformatList = getBcformat();

		// BCフォーマットの長さを合計（改行コードがあるので2バイト足す）
		int bcLength = 2;
		for (Integer len : bcformatList.values()) {
			bcLength += len;
		}

		// 2014/04/07 DA ins start
		// T/P処理内容ファイル読み込み
		ArrayList<String[]> tpFormatList = getTpformat();
		// 2014/04/07 DA ins end


		// 2014/04/07 DA ins start
		for (String[] tp : tpFormatList) {

			// BCデータテキストファイル一覧取得
			String subPath = alcHomePath + "Line" + tp[0] + "\\" + tp[1];
			File dirTp = new File(subPath + "\\RecvData");
			if (!dirTp.exists()) {
				// ディレクトリが存在しなければ、エラー
				String msg = "ディレクトリが存在しません。" + dirTp.getAbsolutePath();
				logger.error(msg);
				continue;
			}

			// BCデータテキストファイル一覧取得
			File[] files = dirTp.listFiles();
			// 2014/04/07 DA ins end

			// ファイル書出が完了する前に読み込んでエラーとなる問題の
			// 対策として、一定時間待機する。
			if (files.length > 0) {
				sleep();
			}

			// 受信ファイルの処理
			for (File file : files) {

				// BCデータ取込＆検査データ生成
				try {
					// ファイル内容の読み込み
					String bcdata = TextUtil.readText(file);

					// ファイル書出が完了する前に読み込んでエラーとなる問題の
					// 対策として、ファイル文字数チェックする
					if (bcdata.length() != bcLength) {
						logger.warn("ALC受信データ文字数異常");
						logger.warn(file.getAbsoluteFile());
						logger.warn("string length=" + bcdata.length());
						logger.warn(bcdata);
						throw new RuntimeException("ALC受信データ文字数異常");
					}

					int i = 0;
					HashMap<String, String> bcdataMap = new HashMap<String, String>();

					// BCデータを項目ごとに切り分け
					for (Entry<String, Integer> entry : bcformatList.entrySet()) {
						String key = entry.getKey();
						int len = entry.getValue();
						String value = bcdata.substring(i, i + len);
						i = i + len;

						bcdataMap.put(key, value);
					}

					// 2014/04/07 DA ins start
					String kubun = "";
					String area = "0";
					for (String[] tpCompBc : tpFormatList) {
						if (tpCompBc[0].equals(bcdataMap.get("ASMLINE"))
								&& tpCompBc[1].equals(bcdataMap.get("#T/P"))) {
							kubun = tpCompBc[2];
							area = tpCompBc[3];
						}
					}
					// 処理区分を取得する
					logger.debug("ライン=" + bcdataMap.get("ASMLINE"));
					logger.debug("T/P=" + bcdataMap.get("#T/P"));
					logger.debug("処理区分=" + kubun);
					logger.debug("エリア=" + area);
					if ("1".equals(kubun)
							|| "3".equals(kubun)) {
						// t_sf, t_sfbc*_*テーブルにデータ投入
						tSfService.createTSf(bcdataMap);

						// f_bcdataテーブルにデータ投入
						FBcdata fBcdata;
						if ("1".equals(kubun)) {
							fBcdata = createFBcdata(bcdataMap, "0");
						} else {
							fBcdata = createFBcdata(bcdataMap, area);
						}

				    	// 号口フラグ切り替え
						// 仮マスタ本番登録画面にて予約していた車両が来たときに
						// 本番マスタの最新バージョンの号口フラグを立てる。
						// TPがN0のときは意味があったが、TPをH0に変更したため
						// ここで号口フラグが切り替わることは無くなった
				    	mVehicleService.updateSopflag(fBcdata);

						// 検査結果サマリと検査データの初期データ作成
				    	updateResultsumAndKensaData(fBcdata);

					} else if ("2".equals(kubun)) {

						// 車両データが存在するものだけ処理する。
						// 車両データが存在しない場合、不要な車両データができて
						// しまい、エラーの原因となるため。
					    String idno = bcdataMap.get("IDNO");
					    String loDate = bcdataMap.get("LO_DATE");
					    if (getSimpleFBcdata(idno, loDate) != null) {
							// f_bcdataテーブルにデータ投入
							FBcdata fBcdata = createFBcdata(bcdataMap, area);

					    	// 号口フラグ切り替え
							// 仮マスタ本番登録画面にて予約していた車両が来たときに
							// 本番マスタの最新バージョンの号口フラグを立てる。
							// TPがN0のときは意味があったが、TPをH0に変更したため
							// ここで号口フラグが切り替わることは無くなった
					    	mVehicleService.updateSopflag(fBcdata);
					    }

					} else {
						String msg = "処理区分が不正です。処理区分=" + kubun;
						logger.error(msg);
						throw new RuntimeException(msg);
					}
					// 2014/04/07 DA ins end

					// 2016/02/24 DA ins start
					// 処理終了後、成否に関わらず処理済フォルダにファイルを移動。
					// Tomcat再起動したときに同じファイルでエラーメールを飛ばさないようにするため。
					// 2014/04/07 DA upd start
				    File dest = new File(subPath + "\\CompData\\" + file.getName());
					// 2014/04/07 DA upd end
				    if (!file.renameTo(dest)) {
						logger.error("ファイルの移動に失敗しました。");
				    	throw new RuntimeException("ALC受信ファイル移動失敗。"
				    			+ "\n移動元:" + file.getPath()
				    			+ "\n移動先:" + dest.getPath());
				    }
					// 2016/02/24 DA ins end
				// 2016/02/24 DA upd start
				} catch (IllegalArgumentException e) {
					logger.error(file.getName() + " の処理でマスタ未登録。", e);

					// マスタ未登録メール送信スクリプトファイル
					String scriptFile = (String)SingletonS2ContainerFactory.getContainer().getComponent("bcdataMasterUnregisteredNotifyMail");
					// エラーメール送信
					sendNotifyMail(scriptFile, file);

					//受信失敗フォルダ
					String ErrorFolder = (String)SingletonS2ContainerFactory.getContainer().getComponent("RecvErrorFolder");
				    File dest = new File(subPath + "\\" + ErrorFolder + "\\" + file.getName());
				    if (!file.renameTo(dest)) {
						logger.error("ファイルの移動に失敗しました。");
				    	throw new RuntimeException("ALC受信ファイル移動失敗。"
				    			+ "\n移動元:" + file.getPath()
				    			+ "\n移動先:" + dest.getPath());
				    }
				// 2016/02/24 DA upd end
				} catch (RuntimeException e) {
					logger.error(file.getName() + " の処理で例外発生。", e);

					// 2016/02/24 DA upd start
					// エラーメール送信
//					sendNotifyMail(file);

//					// エラーメールが連続送信されないよう、例外スローしてタイマー停止
//					throw e;

					//BCデータ取込異常メール送信スクリプトファイル
					String scriptFile = (String)SingletonS2ContainerFactory.getContainer().getComponent("bcdataAcquisitionErrorNotifyMail");
					// エラーメール送信
					sendNotifyMail(scriptFile, file);

					//受信失敗フォルダ
					String ErrorFolder = (String)SingletonS2ContainerFactory.getContainer().getComponent("RecvErrorFolder");
				    File dest = new File(subPath + "\\" + ErrorFolder + "\\" + file.getName());
				    if (!file.renameTo(dest)) {
						logger.error("ファイルの移動に失敗しました。");
				    	throw new RuntimeException("ALC受信ファイル移動失敗。"
				    			+ "\n移動元:" + file.getPath()
				    			+ "\n移動先:" + dest.getPath());
				    }
					// 2016/02/24 DA upd end

				} finally {
					// 2016/02/24 DA del start
//					// 処理終了後、成否に関わらず処理済フォルダにファイルを移動。
//					// Tomcat再起動したときに同じファイルでエラーメールを飛ばさないようにするため。
//					// 2014/04/07 DA upd start
//				    File dest = new File(subPath + "\\CompData\\" + file.getName());
//					// 2014/04/07 DA upd end
//				    if (!file.renameTo(dest)) {
//						logger.error("ファイルの移動に失敗しました。");
//				    	throw new RuntimeException("ALC受信ファイル移動失敗。"
//				    			+ "\n移動元:" + file.getPath()
//				    			+ "\n移動先:" + dest.getPath());
//				    }
					// 2016/02/24 DA del end
				}
			}
		}
	}


	/**
	 * 一定時間スリープする
	 */
	private void sleep() {
		try {
			final int milliseconds = 1000;
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			logger.error("sleep中に例外発生", e);
		}
	}


	/**
	 * エラーメール送信
	 * @param scriptFile 送信スクリプトファイル
	 * @param file 処理エラーとなったファイル
	 */
	private void sendNotifyMail(String scriptFile, File file) {
		// メール送信コマンド
		// 2016/02/24 DA upd start
//		File notify = ResourceUtil.getResourceAsFile("notify_mail.vbs");
		File notify = ResourceUtil.getResourceAsFile(scriptFile);
		// 2016/02/24 DA upd end

		// コマンド実行
		try {
			ProcessBuilder pb = new ProcessBuilder();
			pb.command("cmd",  "/c", notify.getAbsolutePath(), file.getName()).start();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}


	/**
	 * BCフォーマット設定ファイル読み込み
	 * @return BCフォーマット設定リスト
	 */
	private LinkedHashMap<String, Integer> getBcformat() {
		// フォーマット設定ファイル読み込み
		File bcformatFile = ResourceUtil.getResourceAsFile("bc_format.txt");
		String bcformat = TextUtil.readUTF8(bcformatFile);

		// フォーマット設定リスト作成
		LinkedHashMap<String, Integer> bcformatList = new LinkedHashMap<String, Integer>();

		for (String s : bcformat.split("\\r\\n")) {
	        String[] a = s.split(",");
	        if (a.length > 1) {
	        	bcformatList.put(
	        			StringUtils.strip(a[0]),
	        			Integer.parseInt(StringUtils.strip(a[1])));
	        }
        }
		return bcformatList;
	}

	/**
	 * T/P処理内容ファイル読み込み
	 * @return TPフォーマット設定リスト
	 */
	private ArrayList<String[]> getTpformat() {
		// フォーマット設定ファイル読み込み
		File tpFormatFile = ResourceUtil.getResourceAsFile("tp.txt");
		String tpFormat = TextUtil.readUTF8(tpFormatFile);

		// フォーマット設定リスト作成
		ArrayList<String[]> tpFormatList = new ArrayList<String[]>();

		if (!tpFormatFile.exists()) {
			// ファイルが存在しなければ、エラー
			String msg = "T/P処理内容ファイルが存在しません。" + tpFormatFile.getAbsolutePath();
			logger.error(msg);
	    	throw new RuntimeException(msg);
		}

		for (String s : tpFormat.split("\\r\\n")) {
	        String[] a = s.split(",");
	        if (a.length > 3) {
	        	a[0] = StringUtils.strip(a[0]);
	        	a[1] = StringUtils.strip(a[1]);
	        	a[2] = StringUtils.strip(a[2]);
	        	a[3] = StringUtils.strip(a[3]);
	        	tpFormatList.add(a);
	        }
        }
		return tpFormatList;
	}


	/**
     * f_bcdataテーブルにデータ投入
     *
     * @param bcdataMap 投入するデータ
     * @param area エリア
     * @return 投入したデータのFBcdataオブジェクト
     */
    private FBcdata createFBcdata(HashMap<String, String> bcdataMap, String area) {
    	// コントロール型式の中で、右Hか左Hかを区別する文字の桁目
    	final int posLR = 6;

    	// １秒（ミリ秒単位）
    	final int sec1 = 1000;

    	// update時に除外するフィールド
    	List<String> excludes = new ArrayList<String>();
    	excludes.add("recvDay");
    	excludes.add("insertDate");
    	excludes.add("insertUser");

    	// コントロール型式
    	String ctrlKata = bcdataMap.get("CTLKATA");

    	// FBcdataオブジェクト作成
	    FBcdata fBcdata = new FBcdata();
	    fBcdata.idno = bcdataMap.get("IDNO");
	    fBcdata.loDate = bcdataMap.get("LO_DATE");
	    fBcdata.frameCode = bcdataMap.get("FR_CODE");
	    fBcdata.frameSeq = bcdataMap.get("FR_SEQ");
	    fBcdata.bodyNo = bcdataMap.get("BDNO");
	    fBcdata.recvDay = DateFormatUtils.format(Utils.now(), "yyyyMMdd");
	    fBcdata.ctrlKata = ctrlKata;
	    fBcdata.ptnDiv = ctrlKata.charAt(posLR) == 'L' ? "1" : "2";
	    fBcdata.insertDate = Utils.nowts();
	    fBcdata.insertUser = "SYSTEM";
	    fBcdata.bctype = bcdataMap.get("BCTYPE");
	    fBcdata.bcnoH0 = bcdataMap.get("#BCNO_H0");

	    // N0通過日時
	    String strTpN0 = bcdataMap.get("#TP_N0_YYYY")
			    + bcdataMap.get("#TP_N0_MM")
			    + bcdataMap.get("#TP_N0_DD")
			    + bcdataMap.get("#TP_N0_HH")
			    + bcdataMap.get("#TP_N0_MI");

	    if (StringUtils.isNotBlank(strTpN0)) {
	    	//-------------------------------------------------------------
	    	// G-ALCのTP通過時刻は『分』の単位でしか取得できないため、
	    	// 1分以内に複数台の車両が入ってくると、同時刻となってしまう。
	    	// 現状のPDAに表示する車両並び順取得の処理内容だと、同時刻に
	    	// 複数の車両が存在したときに、2車両目以降がPDAに表示されない
	    	// 問題がある。
	    	// その対策として、TP通過時刻が一意になるように秒を付与する。
	    	//-------------------------------------------------------------

	    	// 2014/04/07 DA ins start
//	    	if (AREA_SALES.equals(area)) {
	    	if (area.startsWith(AREA_SALES)) {	// 2014/10/30 DA upd
	        	// 2014/04/07 DA ins start
	    	    fBcdata.lineSales = bcdataMap.get("ASMLINE");
		    	excludes.add("lineGiso");
		    	excludes.add("tpN0");
		    	excludes.add("lineArea03");	// 2014/10/30 DA ins
		    	excludes.add("tpArea03");	// 2014/10/30 DA ins
	        	// 2014/04/07 DA ins end
		    	// 分単位で同時刻の車両があるか検索
		    	List<FBcdata> tmpBcdataList = select()
		    		.where("TO_CHAR(tpSales, 'YYYYMMDDHH24MI') = ? "
		    				+ " AND lineSales = ? ", strTpN0, bcdataMap.get("ASMLINE"))
		    		.orderBy("tpSales DESC")
		    		.getResultList();

		    	if (Utils.isEmpty(tmpBcdataList)) {
			    	// 同時刻の車両が無い場合
			    	fBcdata.tpSales = new Timestamp(
			    			DateConversionUtil.toDate(strTpN0, "yyyyMMddHHmm").getTime());
		    	} else {
			    	// 同時刻の車両があった場合、
		    		FBcdata tmpBcdata = findFBcdata(tmpBcdataList, fBcdata);
		    		if (tmpBcdata != null) {
		    			// 同一車両が既にあれば時刻を変更しない
		    			fBcdata.tpSales = tmpBcdata.tpSales;
		    		} else {
		    			// 同一車両が無かったら、+1秒しておく
		    			fBcdata.tpSales = new Timestamp(tmpBcdataList.get(0).tpSales.getTime() + sec1);
		    		}
		    	}

//	    	} if (AREA_GISO.equals(area)) {	// 艤装
	    	} if (area.startsWith(AREA_GISO)) {	// 艤装 2014/10/30 DA upd
	        	// 2014/04/07 DA ins start
	    	    fBcdata.lineGiso = bcdataMap.get("ASMLINE");
		    	excludes.add("lineSales");
		    	excludes.add("tpSales");
		    	excludes.add("lineArea03");	// 2014/10/30 DA ins
		    	excludes.add("tpArea03");	// 2014/10/30 DA ins
	        	// 2014/04/07 DA ins end
		    	// 分単位で同時刻の車両があるか検索
		    	List<FBcdata> tmpBcdataList = select()
		    		.where("TO_CHAR(tpN0, 'YYYYMMDDHH24MI') = ? "
		    				+ " AND lineGiso = ?", strTpN0, bcdataMap.get("ASMLINE"))
		    		.orderBy("tpN0 DESC")
		    		.getResultList();

		    	if (Utils.isEmpty(tmpBcdataList)) {
			    	// 同時刻の車両が無い場合
			    	fBcdata.tpN0 = new Timestamp(
			    			DateConversionUtil.toDate(strTpN0, "yyyyMMddHHmm").getTime());
		    	} else {
			    	// 同時刻の車両があった場合、
		    		FBcdata tmpBcdata = findFBcdata(tmpBcdataList, fBcdata);
		    		if (tmpBcdata != null) {
		    			// 同一車両が既にあれば時刻を変更しない
	    				fBcdata.tpN0 = tmpBcdata.tpN0;
		    		} else {
		    			// 同一車両が無かったら、+1秒しておく
		    			fBcdata.tpN0 = new Timestamp(tmpBcdataList.get(0).tpN0.getTime() + sec1);
		    		}
		    	}
		    	// 2014/10/30 DA ins start
	    	} if (area.startsWith(AREA_03)) {	// 拡張
	    	    fBcdata.lineArea03 = bcdataMap.get("ASMLINE");
		    	excludes.add("tpN0");
		    	excludes.add("tpSales");
		    	excludes.add("lineGiso");
		    	excludes.add("lineSales");
		    	// 分単位で同時刻の車両があるか検索
		    	List<FBcdata> tmpBcdataList = select()
		    		.where("TO_CHAR(tpArea03, 'YYYYMMDDHH24MI') = ? "
		    				+ " AND lineArea03 = ?", strTpN0, bcdataMap.get("ASMLINE"))
		    		.orderBy("tpArea03 DESC")
		    		.getResultList();

		    	if (Utils.isEmpty(tmpBcdataList)) {
			    	// 同時刻の車両が無い場合
			    	fBcdata.tpArea03 = new Timestamp(
			    			DateConversionUtil.toDate(strTpN0, "yyyyMMddHHmm").getTime());
		    	} else {
			    	// 同時刻の車両があった場合、
		    		FBcdata tmpBcdata = findFBcdata(tmpBcdataList, fBcdata);
		    		if (tmpBcdata != null) {
		    			// 同一車両が既にあれば時刻を変更しない
		    			fBcdata.tpArea03 = tmpBcdata.tpArea03;
		    		} else {
		    			// 同一車両が無かったら、+1秒しておく
		    			fBcdata.tpArea03 = new Timestamp(tmpBcdataList.get(0).tpArea03.getTime() + sec1);
		    		}
		    	}
		    	// 2014/10/30 DA ins start
	    	}
			// 2014/04/07 DA ins end

			// 2014/04/07 DA del start
//	    	// 分単位で同時刻の車両があるか検索
//	    	FBcdata tmpBcdata = select()
//	    		.where("TO_CHAR(tpN0, 'YYYYMMDDHH24MI') = ?", strTpN0)
//	    		.orderBy("tpN0 DESC")
//	    		.limit(1)
//	    		.getSingleResult();
//
//	    	if (tmpBcdata == null) {
//		    	// 同時刻の車両が無い場合
//		    	fBcdata.tpN0 = new Timestamp(
//		    			DateConversionUtil.toDate(strTpN0, "yyyyMMddHHmm").getTime());
//	    	} else {
//		    	// 同時刻の車両がある場合、+1秒しておく
//	    		fBcdata.tpN0 = new Timestamp(tmpBcdata.tpN0.getTime() + sec1);
//	    	}
			// 2014/04/07 DA del end
	    }

		// 2014/04/07 DA upd start
//	    // N0通過日時がnullで上書きされないようにする
	    if (fBcdata.tpN0 == null) {
	    	excludes.add("tpN0");
	    }
	    if (fBcdata.tpSales == null) {
	    	excludes.add("tpSales");
	    }
	    if (fBcdata.tpArea03 == null) {	// 2014/11/06 DA ins
	    	excludes.add("tpArea03");	// 2014/11/06 DA ins
	    }
		// 2014/04/07 DA upd start


	    // DB更新
	    int count = jdbcManager.update(fBcdata)
	    		.excludes(excludes.toArray(new String[0]))
	    		.execute();
	    if (count == 0) {
	    	insert(fBcdata);
	    }

	    return fBcdata;
    }

    /**
     * リストから検索条件に一致するオブジェクトを取得する
     * @param fBcdataList 検索対象のリスト
     * @param condition 検索条件
     * @return 検索条件に一致したオブジェクト。無かったときはnull
     */
    public static FBcdata findFBcdata(List<FBcdata> fBcdataList, FBcdata condition) {
		for (FBcdata fBcdata : fBcdataList) {
			if (fBcdata.idno.equals(condition.idno) && fBcdata.loDate.equals(condition.loDate)) {
				return fBcdata;
			}
		}

		return null;
    }


	/**
	 * 検査済み工程かどうか確認し、未検査であれば検査結果サマリと検査データを作成します。
	 * 検査済みの場合は何もしません。
	 * @param bcdata 対象の車両
	 */
	public void updateResultsumAndKensaData(FBcdata bcdata) {
		// 最新の本番マスタを取得
		List<MGroup> groups = mGroupService.getMGroups(1, null, bcdata.bctype);
		// 2016/02/24 DA ins start
		// マスタチェック(工程、検査順)
		if (groups.size() == 0) {
			logger.error("マスタが本番登録されていない車種データを受信しました。");
	    	throw new IllegalArgumentException("マスタが本番登録されていない車種データを受信しました");
		}
		// 2016/02/24 DA ins end

		// 検査済みの工程を取得
		List<MGroup> inspectedGroups = mGroupService.getMGroupWithResult(bcdata);

		// 検査データ生成する工程を覚えておくための変数
		List<Integer> createXmlList = new ArrayList<Integer>();

		for (MGroup group : groups) {
			// 検査済み工程かどうか確認。
			// 検査済みなら処理しない。
			// 検査していない場合、後続の処理を実施する。
			if (isInspected(group.groupCode, inspectedGroups)) {
				continue;
			}

			// 検査結果サマリレコード作成
			fResultsumService.setInitialData(bcdata, group);

			// 検査データ生成する工程を記録
			createXmlList.add(group.groupCode);
		}

		// 検査データ生成
		createKensaData(bcdata.idno, bcdata.loDate, createXmlList, 1);
	}


	/**
	 * 工程が検査済みかどうか。
	 * @param groupCode 確認対象の工程の工程コード
	 * @param inspectedGroups 検査済み工程のリスト
	 * @return true:検査済み、false:未検査
	 */
	private boolean isInspected(Integer groupCode, List<MGroup> inspectedGroups) {
		for (MGroup inspectedGroup : inspectedGroups) {
			if (groupCode.equals(inspectedGroup.groupCode)) {
				return true;
			}
		}

		return false;
	}


	/**
     * テスト用検査データ生成
     * @param name ボデーNO
     */
    public void createKensaDataTest(String name) {

    	//List<FBcdata> bodies = select().where("tpN0 between '2013-05-01' and '2013-06-30'").getResultList();
    	List<FBcdata> bodies = select()
    			.where("bodyNo=?", name)
    			.orderBy("tpN0 DESC")
    			.limit(1)
    			.getResultList();

    	for (FBcdata body : bodies) {
			// 検査結果サマリと検査データの初期データ作成
	    	updateResultsumAndKensaData(body);
		}
    }


	/**
	 * ボデーNOまたは組立連番にて、車両情報を検索する。
	 * 同じボデーNOまたは組立連番が複数存在する場合、最新の車両情報を取得。
	 * @param bodyNo ボデーNO
	 * @param bcnoH0 組立連番
	 * @return 車両情報
	 */
	public FBcdata selectByBodyNoOrBcno(String bodyNo, String bcnoH0) {

		FBcdata reservedBody = select()
				.where(new SimpleWhere()
						.eq("bodyNo", bodyNo)
						.eq("bcnoH0", bcnoH0))
//				.orderBy("tpN0 DESC NULLS FIRST")
				.orderBy("loDate DESC, insertDate DESC")
				.limit(1)
				.getSingleResult();

		return reservedBody;
	}
	// 2016/02/24 DA ins start
	public FBcdata selectByBodyNoOrBcno(String bodyNo, String bcnoH0, String line) {

		FBcdata reservedBody = select()
				.where(new SimpleWhere()
						.eq("bodyNo", bodyNo)
						.eq("bcnoH0", bcnoH0)
						.eq("lineGiso", line))
				.orderBy("loDate DESC, insertDate DESC")
				.limit(1)
				.getSingleResult();

		return reservedBody;
	}
	// 2016/02/24 DA ins end


	/**
	 * 検査データ再生成。
	 * 再生成する対象は、引数で指定された車種で、bcdata以降N0通過済みの車両。
	 * @param bctype BC車種区分
	 * @param firstBody 再生成を開始する車両
	 */
	public void createKensaDataByMstRegist(String bctype, FBcdata firstBody) {
		// 再生成対象の車両データを取得
		List<FBcdata> fBcdataList = select()
				.where("tpN0 >= ? AND bctype = ?", firstBody.tpN0, bctype)
				.getResultList();

		for (FBcdata fBcdata : fBcdataList) {
			// 検査結果サマリと検査データの初期データ作成
	    	updateResultsumAndKensaData(fBcdata);
		}
	}


	/**
     * 仮マスタ用検査データ生成
     * @param idno 生成する対象の車両のアイデントNo
     * @param loDate 生成する対象の車両のラインオフ計画日
     * @param groupCode 生成する対象の工程。null/0なら全工程
     * @param selectmst 生成する対象のマスタバージョン。仮0(0)/仮1(-1)
	 * @return 生成したXML
     */
   	// 2016/02/24 DA upd start
    public XMLWriteService createKensaDataKari(String idno, String loDate, Integer groupCode, Integer selectmst) {
    // public XMLWriteService createKensaDataKari(String idno, String loDate, Integer groupCode) {
	// 2016/02/24 DA upd end
    	// 車両情報を取得する。
		FBcdata fBcdata = getFBcdata(idno, loDate);

		// 職制・確認工程かどうか判定
		MGroup shokuGroup = MGroupService.getShokuGroup();
		boolean isShoku = groupCode.equals(shokuGroup.groupCode);

		// 対象車種用の工程データを取得
		MGroup group;
		List<MGroup> mGroupList;
		if (isShoku) {
			// 職制・確認工程
			group = shokuGroup;
			// 2016/02/24 DA upd start
			mGroupList = mGroupService.getMGroups(selectmst, null, fBcdata.bctype);
			//mGroupList = mGroupService.getMGroups(0, null, fBcdata.bctype);
			// 2016/02/24 DA upd end
		} else {
			// 通常の工程
			// 2016/02/24 DA upd start
			Integer groupCodeTrue = mGroupService.getMGroupByCode(fBcdata.bctype, selectmst, null, groupCode);
			group = mGroupService.getMGroupByName(selectmst, groupCodeTrue, 0, fBcdata.ptnDiv);
			//group = mGroupService.getMGroupByName(0, groupCode, 0, fBcdata.ptnDiv);
			// 2016/02/24 DA upd end
			mGroupList = new ArrayList<MGroup>();
			mGroupList.add(group);
		}

		// XML作成用オブジェクトを初期化
		XMLWriteService srvXML = initializeXml(fBcdata, group);

		// XMLの内容を作成
		for (MGroup mGroup : mGroupList) {
			createKensaData(fBcdata, mGroup, srvXML, isShoku);
		}

		return srvXML;
    }


	/**
     * 検査データ生成
     * @param idno 生成する対象の車両のアイデントNo
     * @param loDate 生成する対象の車両のラインオフ計画日
     * @param groupCode 生成する対象の工程。nullなら全工程
     * @param selectMst 使用するマスタ。0:仮マスタ、1:号口マスタ
     */
	public void createKensaData(String idno, String loDate, Integer groupCode, int selectMst) {
		List<Integer> groupList = new ArrayList<Integer>();
		groupList.add(groupCode);
		createKensaData(idno, loDate, groupList, 1);
	}


	/**
     * 検査データ生成
     * @param idno 生成する対象の車両のアイデントNo
     * @param loDate 生成する対象の車両のラインオフ計画日
     * @param groupList 生成する対象の工程。null/Emptyなら全工程
     * @param selectMst 使用するマスタ。0:仮マスタ、1:号口マスタ
     */
    public void createKensaData(String idno, String loDate, List<Integer> groupList, int selectMst) {

		// 車両情報を取得する。
		FBcdata fBcdata = getFBcdata(idno, loDate);

		// 検査結果サマリを取得する。
		List<FResultsum> fResultsumList = fResultsumService.selectByFrame(idno, loDate);

		// 工程ごとに処理
		for (FResultsum fResultsum : fResultsumList) {
			// 工程指定が無ければ、全工程が対象
			// 工程指定があれば、指定された工程が対象
			if (Utils.isEmpty(groupList) || groupList.contains(fResultsum.groupCode)) {
				// XML作成用オブジェクトを初期化
				XMLWriteService srvXML = initializeXml(fBcdata, fResultsum.mGroup);
				// XMLの内容を作成
				createKensaData(fBcdata, fResultsum.mGroup, srvXML, false);
				// XML出力
				if (srvXML.getTableSize() > 0) {
					outputXml(false, fBcdata, fResultsum.mGroup, srvXML);
				}
			}
		}


		//---------------------------------------------------------------------
		// 職制・確認工程用のXML生成。
		// 全工程の全検査項目を1つのXMLファイルに出力する。
		//---------------------------------------------------------------------
		// 職制・確認工程用のグループオブジェクト取得
		MGroup shokuGroup = MGroupService.getShokuGroup();
		// XML作成用オブジェクトを初期化
		XMLWriteService shokuXML = initializeXml(fBcdata, shokuGroup);
		// XMLの内容を作成
		for (FResultsum fResultsum : fResultsumList) {
			createKensaData(fBcdata, fResultsum.mGroup, shokuXML, true);
		}
		// XML出力
		outputXml(false, fBcdata, shokuGroup, shokuXML);
    }


	/**
     * 検査データ生成
     * @param fBcdata 対象車両
     * @param group 対象工程
     * @param srvXML 生成した検査データを格納するオブジェクト
     * @param shokusei 職制・確認工程か否か
     */
    private void createKensaData(FBcdata fBcdata, MGroup group, XMLWriteService srvXML, boolean shokusei) {

		// グループCodeから該当するグループレコードを取得する。
		// 関連するMItemまで取得するために再度検索実施。
		// 2016/02/24 DA upd start
		MGroup mGroup = mGroupService.getMGroupByName(group.mstVer, group.groupCode, group.mstVer, fBcdata.ptnDiv);
		//MGroup mGroup = mGroupService.getMGroupByName(group.mstVer == 0 ? 0 : 1, group.groupCode, group.mstVer, fBcdata.ptnDiv);
		// 2016/02/24 DA upd end
		if (mGroup == null) {
			// 工程に紐づく検査項目が無いので、検査データ作成しない
			return;
		}

		// 検査結果を取得
		List<FResult> fResultList = fResultService.selectByBcdata(fBcdata, group);

		// 対象車両のダミーチェック登録を検索
		List<FBcdataDummy> fBcdataDummyList = fBcdataDummyService.selectByBody(fBcdata.idno, fBcdata.loDate);

		// 検査データXMLが既に存在する場合、ダミー画像を同じものにするために
    	// 既存検査データを読み込む。
		// 職制・確認工程は、ダミー画像無しなので不要。
		Map<String, String> kensaData = null;
		if (!shokusei) {
			kensaData = readKensaData(fBcdata, group);
		}

		// 検査内容が"[msgno-桁目-桁数]"の書式になっているかどうかを判定するための正規表現
		Pattern msgnoPattern = Pattern.compile("\\[[A-Z][0-9A-Z]\\d\\d\\-\\d+\\-\\d+\\]");

		// 全検査項目に対して処理
		for (MOrder mOrder : mGroup.mOrderList) {

			MItem mItem = mOrder.mItem;
			// 2016/02/24 DA ins start
			// マスタチェック(項目)
			if (mItem == null) {
				logger.error("マスタが本番登録されていない車種データを受信しました。");
		    	throw new IllegalArgumentException("マスタが本番登録されていない車種データを受信しました");
			}
			// 2016/02/24 DA ins end

			// 正解の指示記号マスタレコードを取得
			MBcsign mBcsign = getSpecifiedMBcsign(fBcdata, mItem);

			// 検査内容が"-"の場合、検査不要なので飛ばして次の項目へ
			if (StringUtils.equals(mBcsign.signContents, "-")) {
				continue;
			}

			// ダミー決定
			MBcsign dummy = getDummyMBcsign(mItem, mBcsign, kensaData, shokusei, fBcdataDummyList);

			// 検査内容が"[msgno-桁目-桁数]"の書式になっている場合、
			// 検査内容にて指定された生産指示を取得する
			if (mBcsign.signContents != null
					&& msgnoPattern.matcher(mBcsign.signContents).matches()) {
				String[] a = StringUtils.replaceChars(mBcsign.signContents, "[]", "").split("-");
				mBcsign = new MBcsign();
				mBcsign.signContents = getAlcBcsign(fBcdata, a[0],
						IntegerConversionUtil.toPrimitiveInt(a[1]),
						IntegerConversionUtil.toPrimitiveInt(a[2]));
			}

			// 検査結果から値を取得する。
			FResult result = new FResult();
			for (FResult tmpResult : fResultList) {
				if (mItem.itemCode.equals(tmpResult.itemCode)) {
					result = tmpResult;
					break;
				}
			}


			// okngDivをセット
			//   0: 選択式
			//   1: 一択式
			//   2: 選択式ダミーチェック
			Character okngDiv = '0';
			if (mItem.okngDiv) {
				okngDiv = '1';
			} else if (ObjectUtils.equals(dummy.usageForBody, '2')) {
				okngDiv = '2';
			}

			//-----------------------------------------------------------------
			// 画像の表示順を決定。
			//   ・検査項目マスタメンテにて、設定された表示順の若い方を上側に表示する。
			//   ・表示順がnullなら、下側に表示する。
			//   ・正解およびダミーの両方の表示順がnullなら、正解を上に表示する。
			//   ・表示順が同値なら、指示記号の若い方を上側に表示する。
			//-----------------------------------------------------------------
			// 表示順の設定値を取得
			int signOrder = mBcsign.signOrder != null ? (int) mBcsign.signOrder : Integer.MAX_VALUE - 1;
			int signOrder2 = dummy.signOrder != null ? (int) dummy.signOrder : Integer.MAX_VALUE;
			// OKを上側に表示するかどうか
			boolean upOk = signOrder < signOrder2;
			// signOrderが一緒なら、記号順にする
			if (signOrder == signOrder2) {
				upOk = mBcsign.bcSign.compareTo(dummy.bcSign) <= 0;
			}


			// XMLに書き込み
			srvXML.ceateTable();

			// 検査順
			srvXML.addOrderSignNo();

			srvXML.addData("ordersignName", mItem.itemName);
			srvXML.addData("ordersignContents", mBcsign.signContents);
			srvXML.addData("fileName", Utils.urlEncode(mBcsign.fileName, "UTF-8"));
			srvXML.addData("imageFlg", mItem.resultDiv);
			srvXML.addData("resultFlg", StringUtils.trim(result.inspecResult));
			//srvXML.addData("uLimit", mBcsign.tLimit);	//必要になったらコメント解除 //
			//srvXML.addData("bLimit", mBcsign.bLimit);	//必要になったらコメント解除 //
			srvXML.addData("inputData", result.inputData);
			srvXML.addData("ngContents", result.ngReason);
			srvXML.addData("ordersingFlg", "0");
			srvXML.addData("itemCode", mItem.mstVer + "-" + mItem.itemCode);
			srvXML.addData("imgRecvDay", getLastModifiedDate(mBcsign.fileName));
			srvXML.addData("tireDiv", mItem.tireDiv ? "1" : "0");
			srvXML.addData("okngDiv", okngDiv);
			srvXML.addData("signOrder", upOk ? "0" : "1");
			srvXML.addData("bcSign", mBcsign.bcSign);
			srvXML.addData("ordersignContents2", dummy.signContents);
			srvXML.addData("fileName2", Utils.urlEncode(dummy.fileName, "UTF-8"));
			srvXML.addData("imgRecvDay2", getLastModifiedDate(dummy.fileName));
			srvXML.addData("signOrder2", upOk ? "1" : "0");
			srvXML.addData("bcSign2", dummy.bcSign);
		}
    }


	/**
	 * 正解の指示記号マスタレコードを取得
	 * @param fBcdata BCデータ
	 * @param mItem 検査項目（指示記号マスタを含む）
	 * @return 正解の指示記号マスタレコード
	 */
	public MBcsign getSpecifiedMBcsign(FBcdata fBcdata, MItem mItem) {
		MBcsign mBcsign = null;

		//MSG_DIVによって検査内容の取得先を切り替える。
		if (mItem.msgDiv) {

			// 正解の指示記号マスタのレコードを取得
			mBcsign = getSpecifiedBcsign(fBcdata, mItem);

			if (mBcsign == null) {
				mBcsign = new MBcsign();
				mBcsign.signContents =
					"指示ﾏｽﾀ設定なし\n"
					+ "MsgNo:" + mItem.msgNo + "\n"
					+ "BC記号:" + getAlcBcsign(fBcdata, mItem);
			}

		} else if (mItem.tMsgno != null) {

			// 生産指示の指示記号をそのまま表示する（指示記号マスタで変換しない）
			mBcsign = new MBcsign();
			mBcsign.signContents = getAlcBcsign(fBcdata, mItem);

		} else {
			// G-ALCの指示記号を利用しない場合
			// 固定の検査内容を表示する
			if (!Utils.isEmpty(mItem.mBcsignList)) {
				mBcsign = mItem.mBcsignList.get(0);
			}

			if (mBcsign == null) {
				mBcsign = new MBcsign();
				mBcsign.signContents = "指示ﾏｽﾀ設定なし";
			}
		}

		return mBcsign;
	}


	/**
	 * 生産指示に対応する指示記号マスタレコードを取得
	 *
	 * @param fBcdata BCデータ
	 * @param mItem 検査項目（指示記号マスタを含む）
	 * @return 指示記号マスタレコード
	 */
	private MBcsign getSpecifiedBcsign(FBcdata fBcdata, MItem mItem) {

		// 生産指示の指示記号を取得
		String msgSign = getAlcBcsign(fBcdata, mItem);

		// M_BCSIGNマスタから指示記号の一致するレコードを取得。
		return Utils.findBcsign(mItem.mBcsignList, msgSign);
	}


	/**
	 * 検査データ読み込み。
	 * @param fBcdata 読み込み対象の車両
	 * @param group 読み込み対象の工程
	 * @return 検査データ。検査データファイルが無い場合、nullを返す。
	 */
	private Map<String, String> readKensaData(FBcdata fBcdata, MGroup group) {
		String filename =
				ServletContextUtil.getServletContext().getRealPath("xml")
				+ "/" + fBcdata.idno + "_" + fBcdata.loDate + "_" + group.groupCode + ".xml";
		File file = new File(filename);
		if (file.exists()) {
			KensaDataService kensaDataReader = new KensaDataService();
			Map<String, String> kensaData = kensaDataReader.readXml(FileInputStreamUtil.create(file));
			// TODO 調査用。後で消す
			if (kensaData == null) {
				System.out.println(new Date() + ": kensaData=null. filename=" + filename);
			}
			return kensaData;
		}

		return null;
	}


	/**
	 * ALCから送信されてきた生産指示の記号を取得する。
	 *
	 * @param fBcdata BCデータ
	 * @param mItem 検査項目マスタ
	 * @return ALCから送信されてきた生産指示の記号
	 */
	private String getAlcBcsign(FBcdata fBcdata, MItem mItem) {
		return getAlcBcsign(fBcdata, mItem.tMsgno, mItem.bcPosition, mItem.bcLength);
	}


	/**
	 * ALCから送信されてきた生産指示の記号を取得する。
	 *
	 * @param fBcdata BCデータ
	 * @param msgno 取得対象のメッセージNO
	 * @param position 取得対象の桁目（先頭が0ではなく1）
	 * @param length 取得対象の桁数
	 * @return ALCから送信されてきた生産指示の記号
	 */
	private String getAlcBcsign(FBcdata fBcdata, String msgno, int position, int length) {
		TMsgno tMsgno = tMsgnoService.selectById(msgno);
		return getAlcBcsign(fBcdata, tMsgno, position, length);
	}


	/**
	 * ALCから送信されてきた生産指示の記号を取得する。
	 *
	 * @param fBcdata BCデータ
	 * @param tMsgno 取得対象のメッセージNO
	 * @param position 取得対象の桁目（先頭が0ではなく1）
	 * @param length 取得対象の桁数
	 * @return ALCから送信されてきた生産指示の記号
	 */
	private String getAlcBcsign(FBcdata fBcdata, TMsgno tMsgno, int position, int length) {
		// 生産指示の指示記号を取得
		String alcBcsign = tSfbcService.getTSfbcValue(fBcdata, tMsgno.tblname, tMsgno.colname);

		// 取得した指示記号の「位置」から「桁数」分の値を得る。
		return StringUtils.mid(alcBcsign, position - 1, length);
	}


	/**
	 * ダミー決定
	 * @param mItem 検査項目
	 * @param mBcsign 正解のMBcsign
	 * @param kensaData 既存の検査データ
	 * @param shokusei 職制・確認工程か否か
     * @param fBcdataDummyList ダミーチェック登録内容
	 * @return ダミー用MBcsignオブジェクト
	 */
	private MBcsign getDummyMBcsign(MItem mItem, MBcsign mBcsign, Map<String, String> kensaData, boolean shokusei, List<FBcdataDummy> fBcdataDummyList) {

		// タイヤメーカー、一択式項目、もしくは職制・確認工程は、ダミー表示不要
		if (mItem.tireDiv || mItem.okngDiv || shokusei) {
			return new MBcsign();
		}


		// ダミーチェック登録があれば、ダミーチェック登録された指示記号を返却する
		if (!Utils.isEmpty(fBcdataDummyList)) {

			// ダミーチェック登録された検査項目かどうかを調査
			for (FBcdataDummy fBcdataDummy : fBcdataDummyList) {
				if (mItem.itemCode.equals(fBcdataDummy.itemCode)) {

					// ダミーチェック登録された指示記号を探し、返却する
					MBcsign tmpBcsign = Utils.findBcsign(mItem.mBcsignList, fBcdataDummy.dummySign);
					if (tmpBcsign != null) {
						// ダミーチェック用であることを通知
						tmpBcsign.usageForBody = '2';
						return tmpBcsign;
					}
				}
			}
		}


		// ダミー記号の設定値を取得
		String[] dummySigns = StringUtils.stripAll(StringUtils.split(mBcsign.dummySign, ','));
		dummySigns = Utils.removeEmpty(dummySigns);

		// 検査データが既に存在する場合、ダミーは既存の検査データから取得
		if (kensaData != null) {
			// 検査データが既に存在する場合の処理
			String dummySign = kensaData.get(mItem.itemCode.toString());
			if (StringUtils.isNotEmpty(dummySign)) {
				// 検査データ中に検査項目が存在する場合の処理
				MBcsign tmpBcsign = Utils.findBcsign(mItem.mBcsignList, dummySign);
				if (tmpBcsign != null && isAcceptableDummy(mBcsign, dummySigns, tmpBcsign)) {
					return tmpBcsign;
				}
			}
		}


		// ダミーを決める
		List<MBcsign> list = new ArrayList<MBcsign>();
		for (MBcsign tmpBcsign : mItem.mBcsignList) {
			// ダミーになり得る記号かどうか
			if (isAcceptableDummy(mBcsign, dummySigns, tmpBcsign)) {
				list.add(tmpBcsign);
			}
		}

		if (list.size() > 0) {
			// ダミー候補の中からランダムでダミーを決定
			int pos = (int) (Math.random() * list.size());
			return list.get(pos);
		} else {
			// ダミー候補が無ければ、ダミー表示しない
			return new MBcsign();
		}

	}


	/**
	 * tmpBcsignがダミーになり得るかどうかチェックする。
	 * @param mBcsign 正解のMBcsignオブジェクト
	 * @param dummySigns ダミー記号の設定内容
	 * @param tmpBcsign チェック対象のMBcsignオブジェクト
	 * @return true:ダミーになり得る　false:ダミーになり得ない
	 */
	private boolean isAcceptableDummy(MBcsign mBcsign, String[] dummySigns,
			MBcsign tmpBcsign) {
		// 検査内容が"-"のものはダミーにする意味がないため、ダミー候補から除外。
		// 検査内容が正解と同一のものは、正解とダミーの見分けが付かなくなるため、
		// ダミー候補から除外。
		if (Utils.trimEqual(tmpBcsign.signContents, "-")
				|| Utils.trimEqual(tmpBcsign.signContents, mBcsign.signContents)) {
			return false;
		}

		// ダミー記号が設定されている場合、ダミー記号以外の記号はダミー候補から除外。
		if (ArrayUtils.isNotEmpty(dummySigns)
				&& !ArrayUtils.contains(dummySigns, StringUtils.trim(tmpBcsign.bcSign))) {
			return false;
		}

		return true;
	}


	/**
	 * XML作成用オブジェクトの初期化
	 * @param fBcdata 車両
	 * @param mGroup 工程
	 * @return XML作成用オブジェクト
	 */
	private XMLWriteService initializeXml(FBcdata fBcdata, MGroup mGroup) {

		//XMLを生成するサービスクラスの生成
		XMLWriteService srvXML = new XMLWriteService();

		//データセットの作成
		srvXML.createDataset();
		srvXML.setTablaName("InspecItem");

		// 車両・工程情報を設定
		srvXML.addBodyGroupData("idno", fBcdata.idno);
		srvXML.addBodyGroupData("loDate", fBcdata.loDate);
		srvXML.addBodyGroupData("frameCode", fBcdata.frameCode);
		srvXML.addBodyGroupData("frameSeq", fBcdata.frameSeq);
		srvXML.addBodyGroupData("bodyNo", fBcdata.bodyNo);
		srvXML.addBodyGroupData("recvDay", fBcdata.recvDay);
		srvXML.addBodyGroupData("bcnoH0", fBcdata.bcnoH0);

// 2016/08/22 DA del start
//		// 2014/04/07 DA upd start
//		//srvXML.addBodyGroupData("tp", DateFormatUtils.format(fBcdata.tpN0.getTime(), "yyyyMMddHHmmss"));
//		if (mGroup.area == null				// 職制・確認工程用の条件
//				|| fBcdata.tpSales == null	// セールス
//		//		|| mGroup.area.equals(AREA_GISO)) {
//				|| mGroup.area.startsWith(AREA_GISO)) {	// 2014/10/30 DA upd
//			String tpN0 = "";
//			if (fBcdata.tpN0 != null) {
//				tpN0 = DateFormatUtils.format(fBcdata.tpN0.getTime(), "yyyyMMddHHmmss");
//			}
//			srvXML.addBodyGroupData("tp", tpN0);
//		//} else {
//		} else if (mGroup.area.startsWith(AREA_SALES)) {	// 2014/10/30 DA upd
//			String tpSales = "";
//			if (fBcdata.tpSales != null) {
//				tpSales = DateFormatUtils.format(fBcdata.tpSales.getTime(), "yyyyMMddHHmmss");
//			}
//			srvXML.addBodyGroupData("tp", tpSales);
//
//			// 2014/10/30 DA ins start
//		} else if (mGroup.area.startsWith(AREA_03)) {
//			String tpArea03 = "";
//			if (fBcdata.tpArea03 != null) {
//				tpArea03 = DateFormatUtils.format(fBcdata.tpArea03.getTime(), "yyyyMMddHHmmss");
//			}
//			srvXML.addBodyGroupData("tp", tpArea03);
//			// 2014/10/30 DA ins end
//		}
//		// 2014/04/07 DA upd end
// 2016/08/22 DA del end
		srvXML.addBodyGroupData("vehicleName", fBcdata.mVehicle.vehicleName);
		srvXML.addBodyGroupData("loDate", fBcdata.tSf.loDate);
		srvXML.addBodyGroupData("groupName", mGroup.groupName);
		srvXML.addBodyGroupData("groupNo", mGroup.groupNo);
		srvXML.addBodyGroupData("groupCode", mGroup.groupCode);
		srvXML.addBodyGroupData("groupState", "0");

		return srvXML;
	}


	/**
	 * XMLファイルを出力する。
	 *
	 * @param ngFlg true:失敗、false:成功
	 * @param recBcdata FBcdataオブジェクト
	 * @param mGroup MGroupオブジェクト
	 * @param srvXML XMLデータ
	 */
	private void outputXml(boolean ngFlg, FBcdata recBcdata, MGroup mGroup,
			XMLWriteService srvXML) {

		String filename =
				ServletContextUtil.getServletContext().getRealPath("xml")
				+ "/" + recBcdata.idno + "_" + recBcdata.loDate + "_" + mGroup.groupCode + ".xml";

		if (ngFlg) {
			//NGを渡す
			FileUtil.write(filename, "NG".getBytes());
		} else {
			FileUtil.write(filename, Utils.getBytes(srvXML.getXMLData(), ServletUtil.ENCODING));
		}
	}


	/**
	 * 検査データの存在チェック
	 * @param fBcdata 読み込み対象の車両
	 * @param group 読み込み対象の工程
	 * @return 存在結果（true:存在する、false:存在しない）
	 */
	public boolean isKensaData(FBcdata fBcdata, MGroup group) {

		String filename = ServletContextUtil.getServletContext().getRealPath("xml")
				+ "/" + fBcdata.idno + "_" + fBcdata.loDate + "_" + group.groupCode + ".xml";
		File file = new File(filename);
		if (file.exists()) {
			return true;
		}

		return false;
	}


	/**
	 * ファイルの更新日を取得。
	 * ファイルが存在しない場合、nullを返す。
	 * ファイル名がnullまたは空文字の場合、nullを返す。
	 *
	 * @param fileName ファイル名
	 * @return 更新日　"yyyy/MM/dd HH:mm:ss"形式
	 */
	private String getLastModifiedDate(String fileName) {
		// ファイル名がnullまたは空文字の場合、nullを返す
		if (StringUtils.isEmpty(fileName)) {
			return null;
		}

		File file = new File(
				ServletContextUtil.getServletContext().getRealPath("images")
				+ "/" + fileName);

		// ファイルが存在しない場合、nullを返す
		if (!file.exists()) {
			return null;
		}

		String lastModified = DateFormatUtils.format(file.lastModified(), "yyyy/MM/dd HH:mm:ss");
		return lastModified;
	}


	/**
	 * 全工程検査済みかどうか調べる。
	 * TODO 使ってないけど検査項目を紐付けていることもあるがどうする？
	 *
	 * @param fBcdata 調査対象車両
	 * @return true:全工程検査済み ／ false:未検査工程あり
	 */
	public boolean inspectedAllGroups(FBcdata fBcdata) {
		// 検査項目を含む工程だけ取得
		List<FResultsum> fResultsumList = jdbcManager.from(FResultsum.class)
				.innerJoin("mOrderList", "mOrderList.ptnDiv = ?", fBcdata.ptnDiv)
				.where(new SimpleWhere()
						.eq("idno", fBcdata.idno)
						.eq("loDate", fBcdata.loDate))
				.getResultList();

		for (FResultsum fResultsum : fResultsumList) {
			if (fResultsum.groupStatus == State.noCheck.ordinal()) {
				return false;
			}
		}

		// 全工程検査済みかどうかの判定結果を戻す
		return true;
	}


	/**
	 * 対象の検査項目を含む工程を、最後に検査した車両からオフセット台数分後ろの位置にある車両を取得します。
	 * 対象の車両が無い場合、nullを返します。
	 * @param itemCode 対象の検査項目
	 * @param offset オフセット
	 * @return BCデータ
	 */
	public FBcdata getLastInspectedBodyOffset(Integer itemCode, int offset) {
		// 最後に検査した車両を取得
		FBcdata lastInspectedBody = getLastInspectedBody(itemCode);

		if (lastInspectedBody == null) {
			return null;
		}

		return select()
				.where(new SimpleWhere()
						.eq("lineGiso", lastInspectedBody.lineGiso)
						.gt("tpN0", lastInspectedBody.tpN0))
				.orderBy("tpN0 ASC")
				.offset(offset)
				.limit(1)
				.getSingleResult();
	}


	/**
	 * 対象の検査項目を含む工程を、最後に検査した車両を取得。
	 * 1件も対象がないときはnullを返します。
	 * @param itemCode 対象の検査項目
	 * @return 最後に検査した車両
	 */
	public FBcdata getLastInspectedBody(Integer itemCode) {
		// 指定された検査項目を含む検査順レコードを取得。
		List<MOrder> orderList = mOrderService.selectByItem(itemCode);

		// 工程名を取得。
		// 右Hと左Hとで別工程に割り当てられていることもあるため、
		// 工程名が複数になる可能性がある。
		Set<String> groupSet = new HashSet<String>();
		for (MOrder order : orderList) {
			groupSet.add(order.mGroup.groupName);
		}

		// 対象の工程を検査した車両の中で、並び順が最後尾の車両を取得
		return select()
				.innerJoin("fResultsumList")
				.innerJoin("fResultsumList.mGroup")
				.where(new SimpleWhere()
						.in("fResultsumList.mGroup.groupName", groupSet)
						.ne("fResultsumList.groupStatus", State.noCheck.ordinal()))
				.orderBy("tpN0 DESC")
				.limit(1)
				.getSingleResult();
	}

	/**
	 * タイヤメーカーの検査データ取得
	 * @param idno アイデントNo
	 * @param loDate ラインオフ計画日
	 * @param groupCode グループコード
	 * @param tiremakerJudgment タイヤメーカー判定パターン
	 * @return 検査データ一覧
	 */
	private List<InspectionTireInfoDto> selectJudgmentTireInfo(String idno, String loDate, Integer groupCode, int tiremakerJudgment) {

		List<InspectionTireInfoDto> tireAllList = new ArrayList<InspectionTireInfoDto>();

		// 車両情報を取得する。
		FBcdata fBcdata = getFBcdata(idno, loDate);

		// 検査結果サマリを取得する。
		List<FResultsum> fResultsumList = fResultsumService.selectByFrame(idno, loDate);

		// 工程ごとに処理
		for (FResultsum fResultsum : fResultsumList) {

			MGroup group = fResultsum.mGroup;

			// 2020/01/22 DA ins start
			if (tiremakerJudgment == TIREMAKER_JUDGMENT.PATTERN2) {
				// 同じ工程のみでチェックするパターン
				boolean isGroup = groupCode.equals(group.groupCode);
				if (isGroup == false) {
					continue;
				}
			}
			else {
				// 全工程でチェックするパターン
				boolean isSiTm = LfaCommon.isJudgmentShotimageTiremaker(group.groupName);
				if (isSiTm) {
					continue;
				}
			}
			// 2020/01/22 DA ins end

			// 2016/02/24 DA upd start
			MGroup mGroup = mGroupService.getMGroupByName(group.mstVer, group.groupCode, group.mstVer, fBcdata.ptnDiv);
			//MGroup mGroup = mGroupService.getMGroupByName(group.mstVer == 0 ? 0 : 1, group.groupCode, group.mstVer, fBcdata.ptnDiv);
			// 2016/02/24 DA upd end
			if (mGroup == null) {
				// 工程に紐づく検査項目が無いので、検査データ作成しない
				continue;
			}

			// 検査結果を取得
			List<FResult> fResultList = fResultService.selectByBcdata(fBcdata, group);

			String strSaveContents = "";

			for (MOrder mOrder : mGroup.mOrderList) {

				MItem mItem = mOrder.mItem;

				//タイヤメーカー以外は飛ばす
				if (mItem.tireDiv == false) {
					continue;
				}

				// 正解の指示記号マスタレコードを取得
				MBcsign mBcsign = getSpecifiedMBcsign(fBcdata, mItem);

				// 検査内容が"-"の場合、検査不要なので飛ばして次の項目へ
				if (StringUtils.equals(mBcsign.signContents, "-")) {
					continue;
				}

				//内容は、最初の内容に統一する
				if (strSaveContents == null || strSaveContents.equals("")) {
					strSaveContents = mBcsign.signContents;
				}

				// 検査結果から値を取得する。
				FResult result = new FResult();
				for (FResult tmpResult : fResultList) {
					if (mItem.itemCode.equals(tmpResult.itemCode)) {
						result = tmpResult;

						//最初に一致した情報が、最後に検査した結果となるため、ここで抜ける
						break;
					}
				}

				InspectionTireInfoDto tireInfo = new InspectionTireInfoDto();
				tireInfo.itemName = mItem.itemName;
				tireInfo.idno = fBcdata.idno;
				tireInfo.loDate = fBcdata.loDate;
				// 2016/02/24 DA ins start
				tireInfo.bodyNo = fBcdata.bodyNo;
				tireInfo.recvDay = fBcdata.recvDay;
				// 2016/02/24 DA ins end
				tireInfo.itemCode = mItem.itemCode;
				tireInfo.inspecNo = result.inspecNo == null ? 0 : result.inspecNo;
				// 2016/02/24 DA ins start
				tireInfo.selectNumber = result.selectNumber == null ? 0 : result.selectNumber;
				// 2016/02/24 DA ins end
				tireInfo.inspecResult = result.inspecResult == null ? "" : result.inspecResult;
				tireInfo.ngReason = result.ngReason == null ? "" : result.ngReason;
				tireInfo.inputData = result.inputData == null ? "" : result.inputData;
				tireInfo.mstVer = group.mstVer;
				tireInfo.groupNo = group.groupNo;
				tireInfo.inspecOrder = mOrder.inspecOrder;
				tireInfo.signContents = strSaveContents;
				tireInfo.selectStatus = 0;
				// 2020/01/22 DA ins start
				tireInfo.shotImage = result.shotImage == null ? "" : result.shotImage;
				tireInfo.okBcSign = result.okBcSign == null ? "" : result.okBcSign;
				tireInfo.ngBcSign = result.ngBcSign == null ? "" : result.ngBcSign;
				// 2020/01/22 DA ins end
				tireAllList.add(tireInfo);
			}
		}

		return tireAllList;
	}

	/**
	 * 処理対象のボデーよりタイヤメーカーの判定結果を行った検査情報を取得
	 * @param groupCode グループコード
	 * @param idno アイデントNo
	 * @param loDate ラインオフ計画日
	 * @param tiremakerJudgment タイヤメーカー判定パターン
	 * @param isReturnData 戻り値返却フラグ（true:常に返す、false:条件により返す）
	 * @return 検査判定リスト
	 */
	public List<List<String>> getJudgmentTireInfo(Integer groupCode, String idno, String loDate, int tiremakerJudgment, boolean isReturnData)
	{
		List<List<String>> judgmentTireList = new ArrayList<List<String>>();

		// 工程内の件数を取得
		long tireList = 0;
		tireList = jdbcManager.from(FBcdata.class)
				.innerJoin("fResultsumList")
				.innerJoin("fResultsumList.mOrderList", "fResultsumList.mOrderList.ptnDiv = ptnDiv")
				.innerJoin("fResultsumList.mOrderList.mItem", "fResultsumList.mOrderList.mItem.deleteFlag = '0'")
				.where("fResultsumList.mOrderList.groupCode = ?"
						+ " AND idno = ?"
						+ " AND loDate = ?"
						+ " AND fResultsumList.mOrderList.mItem.tireDiv = TRUE"
						, groupCode, idno, loDate)
				.getCount();

		// 検査項目がない場合は判定の必要がないため行わない
		if(tireList == 0) {
			return judgmentTireList;
		}

		// 車両内の検査情報を取得
		// 2014/12/02 DA upd start
//		Map<String, Object> param = new HashMap<String, Object>();
//		param.put("idno", idno);
//		param.put("loDate", loDate);
//		List<InspectionTireInfoDto> tireAllList = jdbcManager.selectBySqlFile(
//				InspectionTireInfoDto.class,
//				"jp/co/ctc/sql/selectJudgmentTireInfo.sql",
//				param
//				).getResultList();
		// 2020/01/22 DA upd start
		List<InspectionTireInfoDto> tireAllList = selectJudgmentTireInfo(idno, loDate, groupCode, tiremakerJudgment);
		// 2020/01/22 DA upd end
		// 2014/12/02 DA upd end

		// 2014/12/02 DA del start
//		// 車両内の件数と工程内の件数が同じ場合は判定の必要がないため行わない
//		if(tireAllList.size() == tireList) {
//			return judgmentTireList;
//		}
		// 2014/12/02 DA del end

		// 2014/12/02 DA ins start
		for (InspectionTireInfoDto tireInfo : tireAllList) {
			//選択状態を設定
			tireInfo.selectStatus = LfaCommon.getTireSelectStatus(tireInfo.inputData, tireInfo.signContents);
		}
		// 2014/12/02 DA ins end

		// タイヤ判定
		List<List<String>> measurementList = new ArrayList<List<String>>();
		List<List<String>> othersList = new ArrayList<List<String>>();
		for(InspectionTireInfoDto tireData : tireAllList) {

			List<String> itemList = new ArrayList<String>();

			if (tireData.selectStatus != SELECTSTATUSLIST.CORRECTANSWER) {
				// 未検査や他データは、最後に結合するため退避
				itemList.add(tireData.groupNo);
				itemList.add(tireData.inspecOrder.toString());
				itemList.add(tireData.itemName);
				itemList.add(tireData.inputData);
				itemList.add(tireData.inspecNo.toString());
				itemList.add(tireData.inspecResult);
				itemList.add(tireData.ngReason);
				itemList.add(tireData.selectStatus.toString());
				othersList.add(itemList);
			}
			else {
				// 選択データ
				itemList.add(tireData.inputData);
				itemList.add(tireData.groupNo);
				itemList.add(tireData.inspecOrder.toString());
				measurementList.add(itemList);
			}
		}

		// 2020/01/22 DA upd start
		List<List<String>> hanteiList = LfaCommon.getTireJudgment(measurementList, othersList, tiremakerJudgment);
		// 2020/01/22 DA upd end

		// 2016/02/24 DA del start
		//// update時に除外するフィールド
		//List<String> excludes = new ArrayList<String>();
		//excludes.add("idno");
		//excludes.add("loDate");
		//excludes.add("bodyNo");
		//excludes.add("recvDay");
		//excludes.add("itemCode");
		//excludes.add("inspecNo");
		//excludes.add("inputData");
		//excludes.add("mstVer");
		//excludes.add("inspecUser");
		//excludes.add("inspecDate");
		// 2016/02/24 DA del end

		// 2016/02/24 DA ins start
		//検査日時にシステム日付を使用する
		Date now = new Date();
		Timestamp timestamp = new Timestamp(now.getTime());
		// 2016/02/24 DA ins end

		// 検査判定リストの作成
		for(InspectionTireInfoDto tireData : tireAllList) {

			if (tireData.selectStatus != SELECTSTATUSLIST.CORRECTANSWER) {
				// 未検査や他データは処理対象にしない
				continue;
			}

			// 入力値に該当する判定結果を取得
			List<String> hanteiData = null;
			for(List<String> data : hanteiList) {
				if(data.get(LfaCommon.JUDGMENTLIST.INPUTDATA).equals(tireData.inputData)) {
					hanteiData = data;
					break;
				}
			}

			// リストに追加
			List<String> itemList = new ArrayList<String>();
			if(hanteiData != null) {
				itemList.add(tireData.groupNo);
				itemList.add(tireData.inspecOrder.toString());
				itemList.add(tireData.itemName);
				itemList.add(hanteiData.get(LfaCommon.JUDGMENTLIST.INPUTDATA));
				itemList.add(hanteiData.get(LfaCommon.JUDGMENTLIST.COUNT));
				itemList.add(hanteiData.get(LfaCommon.JUDGMENTLIST.RESULTFLG));
				itemList.add(hanteiData.get(LfaCommon.JUDGMENTLIST.NGCONTENTS));
				itemList.add(tireData.selectStatus.toString());

				// 2016/02/24 DA upd start
				//// DB更新
				//FResult result = new FResult();
				//result.idno = tireData.idno;
				//result.loDate = tireData.loDate;
				//result.itemCode = tireData.itemCode;
				//result.inspecNo = tireData.inspecNo;
				//result.mstVer = tireData.mstVer;
				//result.inspecResult = hanteiData.get(LfaCommon.JUDGMENTLIST.RESULTFLG);
				//result.ngReason = hanteiData.get(LfaCommon.JUDGMENTLIST.NGCONTENTS);
				//int count = jdbcManager.update(result)
				//		.excludes(excludes.toArray(new String[0]))
				//		.execute();
				//if (count == 0) {
				//	// データは存在するため更新できないことはありえない
				//	return null;
				//}

				// 検査結果が異なる場合DB追加
				if (!tireData.inspecResult.equals(hanteiData.get(LfaCommon.JUDGMENTLIST.RESULTFLG))) {
					FResult insResult = new FResult();
					insResult.idno = tireData.idno;
					insResult.loDate = tireData.loDate;
					insResult.bodyNo = tireData.bodyNo;
					insResult.recvDay = tireData.recvDay;
					insResult.mstVer = tireData.mstVer;
					insResult.itemCode = tireData.itemCode;
					insResult.inspecNo = tireData.inspecNo + 1;
					insResult.selectNumber = 1;
					insResult.inspecResult = hanteiData.get(LfaCommon.JUDGMENTLIST.RESULTFLG);
					insResult.ngReason = hanteiData.get(LfaCommon.JUDGMENTLIST.NGCONTENTS);
					insResult.inputData = tireData.inputData;
					insResult.inspecDate = timestamp;
					insResult.inspecUser = "SYSTEM";
					// 2020/01/22 DA ins start
					insResult.shotImage = tireData.shotImage;
					insResult.okBcSign = tireData.okBcSign;
					insResult.ngBcSign = tireData.ngBcSign;
					// 2020/01/22 DA ins end

					jdbcManager.insert(insResult).execute();
				}
				// 2016/02/24 DA upd end
			}
			else {
				itemList.add(tireData.groupNo);
				itemList.add(tireData.inspecOrder.toString());
				itemList.add(tireData.itemName);
				itemList.add(tireData.inputData);
				itemList.add(tireData.inspecNo.toString());
				itemList.add(tireData.inspecResult);
				itemList.add(tireData.ngReason);
				itemList.add(tireData.selectStatus.toString());
			}
			judgmentTireList.add(itemList);
		}

		// 未検査や他データを結合する
		judgmentTireList.addAll(othersList);

		// 検査順に並べ替える
		Collections.sort(judgmentTireList, new Comparator<List<String>>()
		{
			public int compare(List<String> o1, List<String> o2)
			{
				// 工程Noの昇順
				int v1 = Integer.parseInt(o1.get(0));
				int v2 = Integer.parseInt(o2.get(0));
				int ret = v1 - v2;
				if(ret != 0) {
					return ret;
				}

				// 検査順の昇順
				v1 = Integer.parseInt(o1.get(1));
				v2 = Integer.parseInt(o2.get(1));
				ret = v1 - v2;
				if(ret != 0) {
					return ret;
				}
				return 0;
			}
		});

		// 2020/01/22 DA upd start
		// 戻り値を常に返すかどうか判定
		if (isReturnData == false) {

			// 2014/12/02 DA ins start
			// 車両内の件数と工程内の件数が同じ場合は判定の必要がないため行わないので空を返す
			if (tireAllList.size() == tireList) {
				return new ArrayList<List<String>>();
			}
			// 2014/12/02 DA ins end
		}
		// 2020/01/22 DA upd end

		return judgmentTireList;
	}

	/**
	 * 通過日時更新
	 * @param koutei 工程名
	 * @param bodyNo ボデーNO
	 * @return 処理結果（true:正常終了、false:異常終了）
	 */
	public boolean updateTp(String koutei, String bodyNo) {

		boolean isResult = true;

		// 車両情報を取得
		FBcdata bcdata = selectByBodyNoOrBcno(bodyNo, null);

		// 工程を取得
		MGroup group = mGroupService.getMGroupByName(bcdata.bctype, koutei);

		// 検査結果を取得
		List<FResult> fResultList = fResultService.selectByBcdata(bcdata, group);

		//検査済の場合は更新しない
		if (fResultList.size() != 0) {
			return true;
		}

		// エリアを取得
		AreaList area = LfaCommon.getArea(group.area);

		// 更新する対象のデータを設定
		switch (area) {
		case area01:
			bcdata.lineGiso = group.line;
			bcdata.tpN0 = Utils.nowts();
			break;

		case area02:
			bcdata.lineSales = group.line;
			bcdata.tpSales = Utils.nowts();
			break;

		case area03:
			bcdata.lineArea03 = group.line;
			bcdata.tpArea03 = Utils.nowts();
			break;

		default:
			//エリアが不明の場合は異常
			return false;
		}

		// DB更新
		int count = jdbcManager.update(bcdata).execute();
		if (count == 0) {
			//この時点でデータがなくなることはないため、更新できなければ異常
			isResult = false;
		}

		return isResult;
	}

	// 2016/09/09 DA ins start
	/**
	 * 車種のラインを取得する
	 * 最後にTP通過日時を設定した車両から取得する。
	 * @param bctype BC車種区分
	 * @return ライン（NULL以外：車種のライン、NULL：車種が存在しない）
	 */
	public String getLineByBctype(String bctype) {

		FBcdata target = jdbcManager.from(FBcdata.class)
				.where(new SimpleWhere()
						.eq("bctype", bctype)
						.isNotNull("lineGiso", true))
				.orderBy("tpN0 DESC")
				.limit(1)
				.getSingleResult();

		// 車種が存在しない
		if (target == null) {
			return null;
		}

		return target.lineGiso;
	}
	// 2016/09/09 DA ins end

	// 2017/12/01 DA ins start
	/**
	 * XMLファイルから指示記号を取得する
	 * @param idno アイデントNo
	 * @param loDate ラインオフ計画日
	 * @param groupCode 工程コード
	 * @param itemCode 項目コード
	 * @return 指示記号の配列（[0]:正解、[1]:不正解）
	 */
	public List<String> getKensaDataBcSign(String idno, String loDate, Integer groupCode, Integer itemCode)
	{
		String data1 = null;
		String data2 = null;
		List<String> list = new ArrayList<String>();

		String filename = ServletContextUtil.getServletContext().getRealPath("xml")
				+ "/" + idno + "_" + loDate + "_" + groupCode + ".xml";
		File file = new File(filename);
		if (file.exists()) {
			KensaDataBcSignService cKensa = new KensaDataBcSignService();
			Map<String, Map<String, String>> kensaList = cKensa.readXml(FileInputStreamUtil.create(file));
			if (kensaList != null) {
				Map<String, String> kensaData = kensaList.get(itemCode.toString());
				if (kensaData != null) {
					data1 = kensaData.get(KensaDataBcSignService.INFO_INDEX1);
					data2 = kensaData.get(KensaDataBcSignService.INFO_INDEX2);
				}

			}
		}

		list.add(data1);
		list.add(data2);

		return list;
	}
	// 2017/12/01 DA ins end

	// 2020/01/22 DA ins start
	/**
	 * タイヤメーカー検査結果連携ファイルを作成する。
	 * @param groupCode グループコード
	 * @param idno アイデントNo
	 * @param loDate ラインオフ計画日
	 * @param tiremakerJudgment タイヤメーカー判定パターン
	 * @param timestamp 作成日時
	 * @param groupName 工程名
	 * @param line ライン
	 * @param bodyNo ボデーNO
	 * @return 処理結果（true:正常終了、false:異常終了）
	 */
	public boolean createTireMakerCooperation(Integer groupCode, String idno, String loDate, int tiremakerJudgment, Timestamp timestamp, String groupName, String line, String bodyNo)
	{
		// 連携しない場合は何もしない
		String str = (String) SingletonS2ContainerFactory.getContainer().getComponent("tireMakerCooperation");
		String[] strSplit = StringUtils.split(str, ",");
		boolean isNotCooperation = true;
		for (int i = 0; i < strSplit.length; i++) {
			if (strSplit[i].trim().equals(line)) {
				isNotCooperation = false;
			}
		}
		if (isNotCooperation) {
			return true;
		}

		// タイヤメーカー検査をしているかチェック
		List<FResult> fResultList = fResultService.getFResultByTiremaker(idno, loDate, timestamp);
		if (fResultList.size() == 0) {
			return true;
		}

		// タイヤメーカー検査結果を取得
		List<List<String>> list = getJudgmentTireInfo(groupCode, idno, loDate, tiremakerJudgment, true);
		if (list == null) {
			return false;
		}

		// ここに来る時点でありえないはずだが、検査結果がない場合は何もしない
		if (list.size() == 0) {
			return true;
		}

		// すべてOKの場合にファイルを作成
		boolean isOk = true;
		for (List<String> data : list) {
			if (data.get(JUDGMENT_TIRE_LIST.INSPECRESULT).equals("0") == false) {
				isOk = false;
				break;
			}
		}
		if (isOk) {
			String tireMaker = list.get(0).get(JUDGMENT_TIRE_LIST.INPUTDATA);
			boolean isCreate = createTireMakerCooperationFile(timestamp, groupName, tireMaker, line, bodyNo, idno, loDate);
			if (isCreate == false) {
				return false;
			}
		}

		return true;
	}

	/**
	 * タイヤメーカー検査結果連携ファイルを作成する。
	 * @param timestamp 作成日時
	 * @param groupName 工程名
	 * @param tireMaker タイヤメーカー
	 * @param line ライン
	 * @param bodyNo ボデーNO
	 * @param idno アイデントNo
	 * @param loDate ラインオフ計画日
	 * @return 処理結果（true:正常終了、false:異常終了）
	 */
	private boolean createTireMakerCooperationFile(Timestamp timestamp, String groupName, String tireMaker, String line, String bodyNo, String idno, String loDate)
	{
		boolean isResult = false;
		String filename = "";

		try {
			String folder = (String) SingletonS2ContainerFactory.getContainer().getComponent("tireMakerCooperationFolder");

			String date = DateFormatUtils.format(timestamp, "yyyyMMddHHmmss");

			filename = folder + "/" + date + "_" + line + "_" + bodyNo + "_" + idno + "_" + loDate + "_" + tireMaker + ".txt";

			File file = new File(filename);
			boolean isCreate = file.createNewFile();
			if (isCreate) {
				isResult = true;
			}
			else {
				String msg = "ファイルの作成に失敗しました。" + filename;
				logger.error(msg);
				isResult = false;
			}
		}
		catch (Exception e) {
			logger.error(filename + " ファイルの作成で例外発生。", e);
			isResult = false;
		}

		return isResult;
	}
	// 2020/01/22 DA ins end
}
