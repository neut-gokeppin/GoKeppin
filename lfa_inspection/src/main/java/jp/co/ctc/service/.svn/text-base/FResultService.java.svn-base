package jp.co.ctc.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.extension.jdbc.where.SimpleWhere;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.util.ResourceUtil;
import org.seasar.struts.util.RequestUtil;
import org.seasar.struts.util.ServletContextUtil;

import flex.messaging.HttpFlexSession;
import jp.co.ctc.entity.FBcdata;
import jp.co.ctc.entity.FResult;
import jp.co.ctc.entity.MBcsign;
import jp.co.ctc.entity.MGroup;
import jp.co.ctc.entity.MItem;
import jp.co.ctc.entity.MOrder;
import jp.co.ctc.entity.TMsgno;
import jp.co.ctc.util.LfaCommon;

/**
 * 検査結果を扱うサービス
 *
 * @author sugihara
 *
 */
public class FResultService {
	/**
	 * JDBCマネージャです。
	 */
	@Resource
	public JdbcManager jdbcManager;

	/**
	 * MGroupService
	 */
	@Resource
	public MGroupService mGroupService;

	//  2014/11/17 DA ins start
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

	@Resource
	public FBcdataService fBcdataService;

	//  2014/11/17 DA ins end

	/**
	 * 検査結果を取得する。
	 * @return 取得した検査結果のリスト
	 */
	public List<FResult> getFResult() {
		return jdbcManager.from(FResult.class)
			.innerJoin("fBcdata")
			.innerJoin("mOrder")
			.getResultList();
	}



	/**
	 * 指定した車両、項目Codeの検査結果を取得する。
	 * @param idno アイデントNo
	 * @param loDate ラインオフ計画日
	 * @param itemCode 項目Code
	 * @return 取得した検査結果のリスト
	 */
	public List<FResult> getFResultByKeys(String idno, String loDate, Integer itemCode) {
		return jdbcManager.from(FResult.class)
			.where("idno = ? AND loDate = ? AND itemCode = ?",
					idno,
					loDate,
					itemCode
					)
			// 2016/02/24 DA upd start
			.orderBy("inspecNo Desc, select_number Desc")
			// .orderBy("inspecNo Desc")
			// 2016/02/24 DA upd start
			.getResultList();
	}

	/**
	 * 検査結果をテーブルに挿入します。
	 *
	 * @param result 検査結果
	 * @return 識別子が設定された後の検査結果
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean create(FResult result) {
		try {
			jdbcManager.insert(result)
			.excludes("sopFlag", "deleteFlag")
			.execute();
			return true;
		} catch (Exception e) {
			return false;
		}
	}


	/**
	 * 特定の車両・工程の検査結果を検索。
	 * @param fBcdata 検索対象の車両
	 * @param group 検索対象の工程
	 * @return 検査結果リスト
	 */
	public List<FResult> selectByBcdata(FBcdata fBcdata, MGroup group) {
		List<FResult> fResultList = jdbcManager.from(FResult.class)
				.innerJoin("mOrder", new SimpleWhere()
					.eq("mOrder.ptnDiv", fBcdata.ptnDiv)
					.eq("mOrder.groupCode", group.groupCode))
				.where(new SimpleWhere()
						.eq("idno", fBcdata.idno)
						.eq("loDate", fBcdata.loDate))
				.orderBy("itemCode, inspecNo DESC, selectNumber DESC")
				.getResultList();

		return fResultList;
	}

	/**
	 * 検索最大値の取得
	 * 2014/11/18 DA ins
	 * @return 検索最大値
	 */
	public int getInspecMax() {
        SingletonS2ContainerFactory.init();
        S2Container container = SingletonS2ContainerFactory.getContainer();
        int iInspectMax = (Integer)container.getComponent("inspectMax");

        return iInspectMax;
	}

	/**
	 * 検査結果履歴画面
	 * 2014/11/18 DA ins
	 * @param txtFrameSeq
	 * @param txtBodyNo
	 * @param txtLoDateFrom
	 * @param txtLoDateTo
	 * @param txtGroupName
	 * @param txtInspecResult
	 * @param txtUserCode
	 * @param txtInspecDateFrom
	 * @param txtInspecDateTo
	 * @return 検査結果履歴一覧を返す。最大件数を超えていたらnullを返す。
	 */
	public List<FResult> selectFResult(
			String txtFrameSeq,
			String txtBodyNo,
			String txtLoDateFrom,
			String txtLoDateTo,
			String txtGroupName,
			String txtInspecResult,
			String txtUserCode,
			Date txtInspecDateFrom,
			Date txtInspecDateTo) {


		// あいまい検索にする
		// 2016/09/26 DA del start
		//txtFrameSeq       = "%" + txtFrameSeq        + "%";
		//txtBodyNo         = "%" + txtBodyNo          + "%";
		//txtUserCode       = "%" + txtUserCode        + "%";

		//if (txtGroupName == null) {
		//	txtGroupName = "%";
		//} else {
		//	txtGroupName = "%" + txtGroupName       + "%";
		//}
		// 2016/09/26 DA del end

		if ("OK".equals(txtInspecResult)) {
			txtInspecResult = "0";
		} else if ("NG".equals(txtInspecResult)) {
			txtInspecResult = "1";
		} else if ("ﾀﾞﾐｰ検出".equals(txtInspecResult)) {
			txtInspecResult = "2";
		} else if ("ﾀﾞﾐｰ見逃し".equals(txtInspecResult)) {
			txtInspecResult = "3";
		// 2016/02/24 DA ins start
		} else if ("-".equals(txtInspecResult)) {
			txtInspecResult = "4";
		// 2016/02/24 DA ins end
		} else {
			// 2016/09/26 DA upd start
			//txtInspecResult = "%";
			txtInspecResult = null;
			// 2016/09/26 DA upd end
		}

		// 入力検索条件
		SimpleWhere sWhere = new SimpleWhere();
		// 2016/09/26 DA upd start
		//sWhere.like("fBcdata.frameSeq", txtFrameSeq)
		//.like("fBcdata.bodyNo", txtBodyNo)
		//.ge("loDate", txtLoDateFrom)
		//.le("loDate", txtLoDateTo)
		//.like("mOrder.mGroup.groupName", txtGroupName)
		//.ge("inspecDate", txtInspecDateFrom)
		//.le("inspecDate", txtInspecDateTo)
		//.like("mUser.userCode", txtUserCode)
		//.like("inspecResult", txtInspecResult);
		sWhere.excludesWhitespace()
		.eq("fBcdata.frameSeq", txtFrameSeq)
		.eq("fBcdata.bodyNo", txtBodyNo)
		.ge("loDate", txtLoDateFrom)
		.le("loDate", txtLoDateTo)
		.eq("mOrder.mGroup.groupName", txtGroupName)
		.ge("inspecDate", txtInspecDateFrom)
		.le("inspecDate", txtInspecDateTo)
		.eq("mUser.userCode", txtUserCode)
		.eq("inspecResult", txtInspecResult);
		// 2016/09/26 DA upd end

		// 最大取得件数
		int iInspectMax = getInspecMax() + 1;
		// 指示記号なし時の件数取得
		List<FResult> fResultListDmy = jdbcManager.from(FResult.class)
				.innerJoin("fBcdata")
				.innerJoin("fBcdata.tSf")	// ins 2014/12/09
				.innerJoin("mItem")
				.innerJoin("mOrder", " mOrder.ptnDiv = fBcdata.ptnDiv")
				.innerJoin("mOrder.mGroup")
				.innerJoin("mUser")
				//.innerJoin("mItem.mBcsignList")
				.leftOuterJoin("mItem.tMsgno")
				.where(sWhere)
				.limit(iInspectMax)
				.getResultList();

		// 最大件数を超えていたらnullを返す
		if (iInspectMax <= fResultListDmy.size()) {
			return null;
		}


		// 検査結果履歴
		List<FResult> fResultList = jdbcManager.from(FResult.class)
				.innerJoin("fBcdata")
				.innerJoin("fBcdata.tSf")	// ins 2014/12/09
				.innerJoin("mItem")
				.innerJoin("mOrder", " mOrder.ptnDiv = fBcdata.ptnDiv")
				.innerJoin("mOrder.mGroup")
				.innerJoin("mUser")
				.leftOuterJoin("mItem.mBcsignList")
				.leftOuterJoin("mItem.tMsgno")
				.where(sWhere)
				.orderBy("fBcdata.insertDate"
						+ ",fBcdata.idno"
						+ ",mOrder.mGroup.groupNo"
						+ ",mOrder.inspecOrder"
						+ ",inspecNo"
						// 2016/02/24 DA ins start
						+ ",selectNumber")
						// 2016/02/24 DA ins end
				.getResultList();

//		// 最大件数を超えていたらリストを減らす
//		if (iInspectMax < fResultList.size()) {
//			// 削除件数を求める
//			int rvcount = fResultList.size() - iInspectMax;
//			for (int i=0; i<rvcount; i++) {
//				fResultList.remove(iInspectMax);
//			}
//		}

		String idno = "";
		String loDate = "";
		FBcdata fbcdata = null;
		// 全検査項目に対して処理
		for (FResult fresult : fResultList) {
			// 前回取得した車両と違っていたら再取得する
			if (!idno.equals(fresult.idno) || !loDate.equals(fresult.loDate)) {
				idno = fresult.idno;
				loDate = fresult.loDate;
				fbcdata = tSfbcService.getTSfbcAll(fresult.fBcdata);
			}

			// 正解の指示記号マスタレコードを取得
			MBcsign mBcsign = fBcdataService.getSpecifiedMBcsign(fbcdata, fresult.mItem);
			fresult.bcSign = mBcsign.bcSign == null ? "" : mBcsign.bcSign;
			fresult.signContents1 = mBcsign.signContents;

		}
		return fResultList;
	}

	// 2017/12/01 DA ins start
	/**
	 * 検査結果履歴画像画面に表示する情報のキー情報をセッションに設定する。
	 *
	 * @param idno アイデントNo
	 * @param loDate ラインオフ計画日
	 * @param itemCode 項目Code
	 * @param inspecNo 検査回数
	 * @param selectNumber 選択回数
	 */
	public void setSelectFResult(final String idno, final String loDate, Integer itemCode, Integer inspecNo, Integer selectNumber)
	{
		HttpFlexSession session = HttpFlexSession.getFlexSession(RequestUtil.getRequest());

		session.setAttribute("selectFResultIdno", idno);
		session.setAttribute("selectFResultLoDate", loDate);
		session.setAttribute("selectFResultItemCode", itemCode);
		session.setAttribute("selectFResultInspecNo", inspecNo);
		session.setAttribute("selectFResultSelectNumber", selectNumber);
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
	private String getAlcBcsign(FBcdata fBcdata, TMsgno tMsgno, int position, int length)
	{
		// 生産指示の指示記号を取得
		String alcBcsign = tSfbcService.getTSfbcValue(fBcdata, tMsgno.tblname, tMsgno.colname);

		// 取得した指示記号の「位置」から「桁数」分の値を得る。
		return StringUtils.mid(alcBcsign, position - 1, length);
	}

	/**
	 * 検査結果履歴画像画面の表示する情報を取得する。
	 * @return 取得した情報
	 */
	public List<Object> getFResultImage()
	{

		HttpFlexSession session = HttpFlexSession.getFlexSession(RequestUtil.getRequest());

		Object obj1 = session.getAttribute("selectFResultIdno");
		Object obj2 = session.getAttribute("selectFResultLoDate");
		Object obj3 = session.getAttribute("selectFResultItemCode");
		Object obj4 = session.getAttribute("selectFResultInspecNo");
		Object obj5 = session.getAttribute("selectFResultSelectNumber");

		if (obj1 == null || obj2 == null || obj3 == null || obj4 == null || obj5 == null) {
			return null;
		}

		String idno = (String) obj1;
		String loDate = (String) obj2;
		Integer itemCode = (Integer) obj3;
		Integer inspecNo = (Integer) obj4;
		Integer selectNumber = (Integer) obj5;

		// 検査結果履歴を取得
		SimpleWhere sWhere = new SimpleWhere();
		sWhere.excludesWhitespace()
				.eq("idno", idno)
				.eq("loDate", loDate)
				.eq("itemCode", itemCode)
				.eq("inspecNo", inspecNo)
				.eq("selectNumber", selectNumber);

		List<FResult> fResultList = jdbcManager.from(FResult.class)
				.innerJoin("fBcdata")
				.innerJoin("fBcdata.tSf")
				.innerJoin("mItem", "mItem.deleteFlag = '0'")
				.innerJoin("mOrder", " mOrder.ptnDiv = fBcdata.ptnDiv")
				.innerJoin("mOrder.mGroup", "mOrder.mGroup.deleteFlag = '0'")
				.innerJoin("mUser", "mUser.deleteFlag = '0'")
				.leftOuterJoin("mItem.mBcsignList", "mItem.mBcsignList.deleteFlag = '0'")
				.leftOuterJoin("mItem.tMsgno")
				.leftOuterJoin("mBcsignOk", "mBcsignOk.deleteFlag = '0'")
				.leftOuterJoin("mBcsignNg", "mBcsignNg.deleteFlag = '0'")
				.where(sWhere)
				.orderBy("fBcdata.insertDate"
						+ ",fBcdata.idno"
						+ ",mOrder.mGroup.groupNo"
						+ ",mOrder.inspecOrder"
						+ ",inspecNo"
						+ ",selectNumber")
				.getResultList();

		if (fResultList.size() <= 0) {
			return null;
		}

		// sfbcの情報が欲しいために取得する
		FBcdata fBcdata = fBcdataService.getFBcdata(idno, loDate);

		// 検査項目数を取得
		int inspecOrder = 0;
		int count = 0;
		MGroup mGroup = mGroupService.getMGroupByName(fResultList.get(0).mstVer, fResultList.get(0).mOrder.mGroup.groupCode, fResultList.get(0).mstVer, fResultList.get(0).fBcdata.ptnDiv);
		if (mGroup != null) {
			for (MOrder mOrder : mGroup.mOrderList) {

				MItem mItem = mOrder.mItem;
				if (mItem == null) {
					continue;
				}

				// 正解の指示記号マスタレコードを取得
				MBcsign mBcsign = fBcdataService.getSpecifiedMBcsign(fBcdata, mItem);

				// 検査内容が"-"の場合、検査不要なので飛ばして次の項目へ
				if (StringUtils.equals(mBcsign.signContents, "-")) {
					continue;
				}

				// 検査項目数をカウント
				count++;

				// 項目の順番を取得
				if (mItem.itemCode.equals(fResultList.get(0).itemCode)) {
					inspecOrder = count;
				}
			}
		}

		Properties props = ResourceUtil.getProperties("application_ja.properties");

		// 撮影画像
		Integer shotImageFlg = 0;
		String shotImageContents = "";
		try {
			String img = fResultList.get(0).shotImage;
			if (StringUtils.isBlank(img)) {
				// ファイルがない場合
				String groupName = fResultList.get(0).mOrder.mGroup.groupName;
				boolean isShotimage = LfaCommon.isJudgmentShotimage(groupName);
				if (isShotimage) {
					// 文言の設定：画像未登録
					shotImageContents = props.getProperty("svr0000015");
				}
			}
			else {
				// ファイルがある場合
				shotImageContents = "shotimages/" + img;
				String fullPath = ServletContextUtil.getServletContext().getRealPath(shotImageContents);
				File file = new File(fullPath);
				if (file.exists()) {
					// ファイルが読めたら画像ファイルとする
					boolean isRetImage = false;
					try {
						BufferedImage readImage = ImageIO.read(file);
						if (readImage != null) {
							isRetImage = true;
						}
					}
					catch (Exception e) {
					}
					if (isRetImage) {
						// ファイルあり
						shotImageFlg = 1;
					}
					else {
						// 文言の設定：画像未登録
						shotImageContents = props.getProperty("svr0000019");
					}
				}
				else {
					// ファイルなし
					shotImageFlg = 2;
				}
			}
		}
		catch (Exception e) {
		}

		// 正解画像
		Integer okBcSignFlg = 0;
		String okBcSignContents = "";
		try {
			String sign = fResultList.get(0).okBcSign;
			if (StringUtils.isBlank(sign)) {
				// 指示記号がない場合
				boolean msgDiv = fResultList.get(0).mItem.msgDiv;
				String msgNo = fResultList.get(0).mItem.msgNo;
				if (msgDiv) {
					String alcBcsign = getAlcBcsign(fBcdata, fResultList.get(0).mItem.tMsgno, fResultList.get(0).mItem.bcPosition, fResultList.get(0).mItem.bcLength);
					// 文言の設定：指示ﾏｽﾀ設定なし\n　MsgNo:{0}\n　BC記号:{1}
					okBcSignContents = MessageFormat.format(props.getProperty("svr0000016"), msgNo, alcBcsign);
				}
				else {
					if (StringUtils.isBlank(msgNo)) {
						if (fResultList.get(0).mBcsignOk == null) {
							// 文言の設定：指示ﾏｽﾀ設定なし
							okBcSignContents = props.getProperty("svr0000017");
						}
						else {
							// タイヤメーカーの場合
							if (fResultList.get(0).mItem.tireDiv) {
								// 指示記号の内容の設定
								okBcSignContents = fResultList.get(0).mBcsignOk.signContents;
							}
							// 選択式／一択式の場合
							else {
								String fileName = fResultList.get(0).mBcsignOk.fileName;
								if (StringUtils.isBlank(fileName)) {
									// 指示記号の内容の設定
									okBcSignContents = fResultList.get(0).mBcsignOk.signContents;
								}
								else {
									// 正解指示記号の画像ファイルの設定
									okBcSignContents = "images/" + fileName;
									String fullPath = ServletContextUtil.getServletContext().getRealPath(okBcSignContents);
									File file = new File(fullPath);
									if (file.exists()) {
										// ファイルあり
										okBcSignFlg = 1;
									}
									else {
										// ファイルなし
										okBcSignFlg = 2;
									}
								}
							}
						}
					}
					else {
						// 生産指示の指示記号の設定
						String alcBcsign = getAlcBcsign(fBcdata, fResultList.get(0).mItem.tMsgno, fResultList.get(0).mItem.bcPosition, fResultList.get(0).mItem.bcLength);
						okBcSignContents = alcBcsign;
					}
				}
			}
			else {
				// 指示記号がある場合
				String fileName = "";
				String signContents = "";
				if (fResultList.get(0).mBcsignOk != null) {
					fileName = fResultList.get(0).mBcsignOk.fileName;
					signContents = fResultList.get(0).mBcsignOk.signContents;
				}

				if (StringUtils.isBlank(fileName)) {
					// 指示記号の内容の設定
					okBcSignContents = signContents;
				}
				else {
					// 正解指示記号の画像ファイルの設定
					okBcSignContents = "images/" + fileName;
					String fullPath = ServletContextUtil.getServletContext().getRealPath(okBcSignContents);
					File file = new File(fullPath);
					if (file.exists()) {
						// ファイルあり
						okBcSignFlg = 1;
					}
					else {
						// ファイルなし
						okBcSignFlg = 2;
					}
				}
			}
		}
		catch (Exception e) {
		}

		// 不正解画像
		Integer ngBcSignFlg = 0;
		String ngBcSignContents = "";
		try {
			String sign = fResultList.get(0).ngBcSign;
			if (StringUtils.isBlank(sign)) {
				// 指示記号がない場合
				// 非表示
				ngBcSignFlg = 3;
			}
			else {
				// 指示記号がある場合
				String fileName = "";
				String signContents = "";
				if (fResultList.get(0).mBcsignNg != null) {
					fileName = fResultList.get(0).mBcsignNg.fileName;
					signContents = fResultList.get(0).mBcsignNg.signContents;
				}

				if (StringUtils.isBlank(fileName)) {
					// 指示記号の内容の設定
					ngBcSignContents = signContents;
				}
				else {
					// 不正解指示記号の画像ファイルの設定
					ngBcSignContents = "images/" + fileName;
					String fullPath = ServletContextUtil.getServletContext().getRealPath(ngBcSignContents);
					File file = new File(fullPath);
					if (file.exists()) {
						// ファイルあり
						ngBcSignFlg = 1;
					}
					else {
						// ファイルなし
						ngBcSignFlg = 2;
					}
				}
			}
		}
		catch (Exception e) {
		}

		// 結果を返す
		List<Object> dataList = new ArrayList<Object>();

		dataList.add(fResultList.get(0)); // 検査結果情報
		dataList.add(inspecOrder); // 検査順
		dataList.add(count); // 検査項目数
		dataList.add(shotImageContents); // 撮影画像表示内容
		dataList.add(okBcSignContents); // 正解画像表示内容
		dataList.add(ngBcSignContents); // 不正解画像表示内容
		dataList.add(shotImageFlg); // 撮影画像表示内容の状態
		dataList.add(okBcSignFlg); // 正解画像表示内容の状態
		dataList.add(ngBcSignFlg); // 不正解画像表示内容の状態

		return dataList;
	}
	// 2017/12/01 DA ins end

	// 2020/01/22 DA ins start
	/**
	 * 指定した車両、作成日のタイヤメーカー検査結果を取得する。
	 * @param idno アイデントNo
	 * @param loDate ラインオフ計画日
	 * @param inspecDate 作成日
	 * @return 取得した検査結果のリスト
	 */
	public List<FResult> getFResultByTiremaker(String idno, String loDate, Timestamp inspecDate)
	{
		return jdbcManager.from(FResult.class)
				.innerJoin("mItem", "mItem.deleteFlag = '0'")
				.where(	"idno = ? AND loDate = ? AND inspecDate = ? AND mItem.tireDiv = '1'",
						idno,
						loDate,
						inspecDate)
				.getResultList();
	}
	// 2020/01/22 DA ins end
}
