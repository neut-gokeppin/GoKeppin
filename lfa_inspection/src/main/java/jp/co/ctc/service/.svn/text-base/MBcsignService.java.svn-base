package jp.co.ctc.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.seasar.extension.jdbc.AutoSelect;
import org.seasar.extension.jdbc.where.ComplexWhere;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.log.Logger;
import org.seasar.framework.util.FileUtil;
import org.seasar.framework.util.ResourceUtil;
import org.seasar.struts.util.RequestUtil;
import org.seasar.struts.util.ServletContextUtil;

import jp.co.ctc.dto.MBcsignDTO;
import jp.co.ctc.entity.MBcsign;
import jp.co.ctc.entity.MItem;
import jp.co.ctc.entity.MUser;
import jp.co.ctc.entity.MVehicle;
import jp.co.ctc.service.MstRegistService.ReservationState;
import jp.co.ctc.util.ServletUtil;
import jp.co.ctc.util.Utils;


/**
 * 指示記号マスタを扱うサービスです.
 *
 * @author kaidu
 *
 */
public class MBcsignService extends UpdateService {

	// 2016/02/24 DA ins start
	/**
	 * ログ出力用
	 */
	private Logger logger = Logger.getLogger(this.getClass());
	// 2016/02/24 DA ins end

	/**
	 * 指示マスタのデータを取得します。クロスチェック用
	 *
	 * @param selectMst 取得するマスタの状態。0:仮、1:本番
	 * @param mstVersion マスタバージョン
	 * @param bctype 取得する対象のBC車種区分
	 * @param groupName 取得する対象の工程名
	 * @param itemCode 取得する対象の検査項目
	 * @param bcSign 取得する対象の指示記号
	 * @return 指示マスタのデータ
	 */
	public List<MBcsignDTO> getMBcsignDTO(final Integer selectMst,
			final Integer mstVersion, final String bctype,
			final String groupName, final Integer itemCode,
			final String bcSign) {

		List<MItem> items = getMItemWithMBcsign(selectMst, mstVersion, bctype,
				groupName, itemCode, bcSign,
		// 2016/02/24 DA ins start
				null, null, null);
		// 2016/02/24 DA ins end

		// 返却用のMBcsignDTOのリストを作成
		List<MBcsignDTO> mBcsignDTOList = new ArrayList<MBcsignDTO>();
		for (MItem item : items) {
			if (item.mBcsignList.size() == 0) {
				MBcsignDTO dto = new MBcsignDTO(item);
				mBcsignDTOList.add(dto);
			} else {
				for (MBcsign bcsign : item.mBcsignList) {
					MBcsignDTO dto = new MBcsignDTO(bcsign);
					mBcsignDTOList.add(dto);
				}
			}
		}

		return mBcsignDTOList;
	}

// 2016/08/26 DA del start
//	// 2016/02/24 DA ins start
//	/**
//	 * 指示マスタのデータを取得します。
//	 *
//	 * @param selectMst 取得するマスタの状態。0:仮、1:本番
//	 * @param mstVersion マスタバージョン
//	 * @param bctype 取得する対象のBC車種区分
//	 * @param groupName 取得する対象の工程名
//	 * @param itemCode 取得する対象の検査項目
//	 * @param bcSign 取得する対象の指示記号
//	 * @param msgNo
//	 * @param reserveFlag
//	 * @param searchFlag
//	 * @param diffFlag 差異確認フラグ
//	 * @return 指示マスタのデータ
//	 */
//	public List<MBcsignDTO> getMBcsignDTO2(final Integer selectMst,
//			final Integer mstVersion, final String bctype,
//			final String groupName, final Integer itemCode,
//			final String bcSign,
//			final String msgNo, final Boolean reserveFlag, final String searchFlag,
//			final boolean diffFlag)
//	{
//
//		List<MItem> items = getMItemWithMBcsign(selectMst, mstVersion, bctype,
//				groupName, itemCode, bcSign,
//				msgNo, reserveFlag, searchFlag);
//
//		// 返却用のMBcsignDTOのリストを作成
//		List<MBcsignDTO> mBcsignDTOList = new ArrayList<MBcsignDTO>();
//		for (MItem item : items) {
//			if (item.mBcsignList.size() == 0) {
//				MBcsignDTO dto = new MBcsignDTO(item);
//				mBcsignDTOList.add(dto);
//			} else {
//				for (MBcsign bcsign : item.mBcsignList) {
//					MBcsignDTO dto = new MBcsignDTO(bcsign);
//					mBcsignDTOList.add(dto);
//				}
//			}
//		}
//
//		// trueの場合、差異確認を行う
//		if (diffFlag) {
//			// 指示マスタのデータを取得します。(本番)
//			List<MItem> itemsReal = getMItemWithMBcsign(ServletUtil.SELECT_MST, mstVersion, bctype,
//					groupName, itemCode, bcSign,
//					msgNo, reserveFlag, searchFlag);
//
//			// 返却用のMBcsignDTOのリストを作成
//			List<MBcsignDTO> mBcsignDTOListReal = new ArrayList<MBcsignDTO>();
//			for (MItem item : itemsReal) {
//				if (item.mBcsignList.size() == 0) {
//					MBcsignDTO dto = new MBcsignDTO(item);
//					mBcsignDTOListReal.add(dto);
//				} else {
//					for (MBcsign bcsign : item.mBcsignList) {
//						MBcsignDTO dto = new MBcsignDTO(bcsign);
//						mBcsignDTOListReal.add(dto);
//					}
//				}
//			}
//
//			// すべて一致フラグ
//			boolean diffAll = false;
//			// 本番にデータがあるか
//			boolean diffTemp;
//			String temp;
//			String real;
////			int tempSignCode;
////			int realSignCode;
//			String tempBcSign;
//			String realBcSign;
//			int tempItemCode;
//			int realItemCode;
//			// 記号、検査内容、ダミー記号、ファイル名が一致しているかチェック
//			for (MBcsignDTO dtoTemp : mBcsignDTOList) {
//				diffTemp = false;
//				for (MBcsignDTO dtoReal : mBcsignDTOListReal) {
////					tempSignCode = (dtoTemp.signCode == null) ? 0 : dtoTemp.signCode;
////					realSignCode = (dtoReal.signCode == null) ? 0 : dtoReal.signCode;
//					tempBcSign = (dtoTemp.bcSign == null) ? "" : dtoTemp.bcSign;
//					realBcSign = (dtoReal.bcSign == null) ? "" : dtoReal.bcSign;
//					tempItemCode = (dtoTemp.itemCode == null) ? 0 : dtoTemp.itemCode;
//					realItemCode = (dtoReal.itemCode == null) ? 0 : dtoReal.itemCode;
//
//					// 同じ指示記号コードを探す
////					if (tempSignCode == realSignCode && tempItemCode == realItemCode) {
//					if (tempBcSign.equals(realBcSign) && tempItemCode == realItemCode) {
//						// 本番にデータ有
//						diffTemp = true;
//
////						// 記号
////						temp = nullToSpc(dtoTemp.bcSign);
////						real = nullToSpc(dtoReal.bcSign);
////						if (!temp.equals(real)) {
////							dtoTemp.bcSignUnMat = true;
////							diffAll = true;
////						}
//						// 検査内容
//						temp = nullToSpc(dtoTemp.signContents);
//						real = nullToSpc(dtoReal.signContents);
//						if (!temp.equals(real)) {
//							dtoTemp.signContentsUnMat = true;
//							diffAll = true;
//						}
//						// ダミー記号
//						temp = nullToSpc(dtoTemp.dummySign);
//						real = nullToSpc(dtoReal.dummySign);
//						if (!temp.equals(real)) {
//							dtoTemp.dummySignUnMat = true;
//							diffAll = true;
//						}
//						// ファイル名
//						temp = nullToSpc(dtoTemp.fileName);
//						real = nullToSpc(dtoReal.fileName);
//						if (!temp.equals(real)) {
//							dtoTemp.fileNameUnMat = true;
//							diffAll = true;
//						}
//						mBcsignDTOListReal.remove(dtoReal);
//						break;
//					}
//				}
//				// 本番にマッチしない
//				if (!diffTemp) {
//					dtoTemp.bcSignUnMat = true;
//					dtoTemp.signContentsUnMat = true;
//					dtoTemp.dummySignUnMat = true;
//					dtoTemp.fileNameUnMat = true;
//					diffAll = true;
//				}
//			}
//
//			Properties props = ResourceUtil.getProperties("application_ja.properties");
//			String diffResultMsg = "";
//
//			SingletonS2ContainerFactory.init();
//			S2Container container = SingletonS2ContainerFactory.getContainer();
//			// 本番マスタ差異削除表示件数
//			int differenceDetailCount = (Integer)container.getComponent("differenceDetailCount");
//
//			int realDiffCount = mBcsignDTOListReal.size();
//			// 件数一致
//			if (realDiffCount == 0) {
//				// 項目も一致
//				if (diffAll==false) {
//					// 本番マスタと内容が一致しています。
//					diffResultMsg = props.getProperty("svr0000004");
//				}
//
//			// 本番マスタ差異削除表示件数=0の場合
//			} else if (differenceDetailCount == 0){
//				diffResultMsg = MessageFormat.format(props.getProperty("svr0000005"), String.valueOf(realDiffCount), "");
//
//			// 本番不一致件数 <本番マスタ差異削除表示件数
//			} else if (realDiffCount <= differenceDetailCount) {
//				String msg = "";
//				for (MBcsignDTO dtoReal : mBcsignDTOListReal) {
//					msg += "MsgNo " + dtoReal.msgNo + " 記号 " + dtoReal.bcSign + "\n";
//				}
//				diffResultMsg = MessageFormat.format(props.getProperty("svr0000005"), String.valueOf(realDiffCount), msg);
//
//			} else {
//				String msg = "";
//				// 他n件
//				int other = realDiffCount - differenceDetailCount;
//				int i = 0;
//				for (MBcsignDTO dtoReal : mBcsignDTOListReal) {
//					msg += "MsgNo " + dtoReal.msgNo + " 記号 " + dtoReal.bcSign + "\n";
//					i++;
//					// 本番マスタ差異削除表示件数分表示
//					if (i >= differenceDetailCount) {
//						break;
//					}
//				}
//				// nullを空文字に変更
//				msg = msg.replaceAll("null", "");
//				diffResultMsg = MessageFormat.format(props.getProperty("svr0000006"), String.valueOf(realDiffCount), msg, String.valueOf(other));
//
//			}
//			for (MBcsignDTO dtoTemp : mBcsignDTOList) {
//				dtoTemp.diffResultMsg =  diffResultMsg;
//			}
//		}
//
//		for (MBcsignDTO mBcsign : mBcsignDTOList) {
//			// 値のスペースなどの加工をする
//			mBcsign.bcSign =  StringUtils.trimToEmpty(mBcsign.bcSign);
//
//			mBcsign.signContents =  Utils.trimDisplay(mBcsign.signContents);
//			mBcsign.dummySign =  Utils.trimDisplay(mBcsign.dummySign);
//			mBcsign.notes =  Utils.trimDisplay(mBcsign.notes);
//		}
//
//		return mBcsignDTOList;
//	}
//	// 2016/02/24 DA ins end
// 2016/08/26 DA del end

	// 2016/08/26 DA ins start
	/**
	 * 指示マスタのデータ有無を取得します。
	 *
	 * @param selectMst 取得するマスタの状態。0:仮、1:本番
	 * @param mstVersion マスタバージョン
	 * @param bctype 取得する対象のBC車種区分
	 * @param groupName 取得する対象の工程名
	 * @param itemCode 取得する対象の検査項目
	 * @param bcSign 取得する対象の指示記号
	 * @param msgNo
	 * @param reserveFlag
	 * @param searchFlag
	 * @param diffFlag 差異確認フラグ
	 * @return 指示マスタのデータ有無（true：データあり、false：データなし）
	 */
	public boolean isSearchMBcsign(final Integer selectMst,
			final Integer mstVersion, final String bctype,
			final String groupName, final Integer itemCode,
			final String bcSign,
			final String msgNo, final Boolean reserveFlag, final String searchFlag,
			final boolean diffFlag)
	{
		List<MItem> items = getMItemWithMBcsign(selectMst, mstVersion, bctype,
				groupName, itemCode, bcSign,
				msgNo, reserveFlag, searchFlag);

		if (items.size() == 0) {
			return false;
		}

		return true;
	}

	/**
	 * 指示マスタのデータを取得します。
	 *
	 * @param selectMst 取得するマスタの状態。0:仮、1:本番
	 * @param mstVersion マスタバージョン
	 * @param bctype 取得する対象のBC車種区分
	 * @param groupName 取得する対象の工程名
	 * @param itemCode 取得する対象の検査項目
	 * @param bcSign 取得する対象の指示記号
	 * @param msgNo
	 * @param reserveFlag
	 * @param searchFlag
	 * @param diffFlag 差異確認フラグ
	 * @return 指示マスタのデータ
	 */
	public List<MBcsignDTO> getMBcsignDTO(final Integer selectMst,
			final Integer mstVersion, final String bctype,
			final String groupName, final Integer itemCode,
			final String bcSign,
			final String msgNo, final Boolean reserveFlag, final String searchFlag,
			final boolean diffFlag)
	{
		List<MBcsignDTO> mBcsignDTOList = new ArrayList<MBcsignDTO>();

		String diffResultMsg = differMBcsign(selectMst, mstVersion, bctype,
				groupName, itemCode, bcSign,
				msgNo, reserveFlag, searchFlag,
				diffFlag, mBcsignDTOList);

		for (MBcsignDTO mBcsign : mBcsignDTOList) {

			mBcsign.diffResultMsg =  diffResultMsg;

			// 値のスペースなどの加工をする
			mBcsign.bcSign = StringUtils.trimToEmpty(mBcsign.bcSign);

			mBcsign.signContents = Utils.trimDisplay(mBcsign.signContents);
			mBcsign.dummySign = Utils.trimDisplay(mBcsign.dummySign);
			mBcsign.notes = Utils.trimDisplay(mBcsign.notes);
		}

		return mBcsignDTOList;
	}

	/**
	 * 指示マスタを比較した結果のメッセージを取得します。
	 *
	 * @param selectMst 取得するマスタの状態。0:仮、1:本番
	 * @param mstVersion マスタバージョン
	 * @param bctype 取得する対象のBC車種区分
	 * @param groupName 取得する対象の工程名
	 * @param itemCode 取得する対象の検査項目
	 * @param bcSign 取得する対象の指示記号
	 * @param msgNo
	 * @param reserveFlag
	 * @param searchFlag
	 * @param diffFlag 差異確認フラグ
	 * @return 比較結果のメッセージ（null：比較しない場合、以外：比較した結果）
	 */
	public String getDiffMBcsign(final Integer selectMst,
			final Integer mstVersion, final String bctype,
			final String groupName, final Integer itemCode,
			final String bcSign,
			final String msgNo, final Boolean reserveFlag, final String searchFlag,
			final boolean diffFlag)
	{
		List<MBcsignDTO> mBcsignDTOList = new ArrayList<MBcsignDTO>();

		String diffResultMsg = differMBcsign(selectMst, mstVersion, bctype,
				groupName, itemCode, bcSign,
				msgNo, reserveFlag, searchFlag,
				diffFlag, mBcsignDTOList);

		return diffResultMsg;
	}

	/**
	 * 指示マスタを比較します。
	 *
	 * @param selectMst 取得するマスタの状態。0:仮、1:本番
	 * @param mstVersion マスタバージョン
	 * @param bctype 取得する対象のBC車種区分
	 * @param groupName 取得する対象の工程名
	 * @param itemCode 取得する対象の検査項目
	 * @param bcSign 取得する対象の指示記号
	 * @param msgNo
	 * @param reserveFlag
	 * @param searchFlag
	 * @param diffFlag 差異確認フラグ
	 * @param mBcsignDTOList 指示マスタのデータ（戻り情報）
	 * @return 比較結果のメッセージ（null：比較しない場合、以外：比較した結果）
	 */
	private String differMBcsign(final Integer selectMst,
			final Integer mstVersion, final String bctype,
			final String groupName, final Integer itemCode,
			final String bcSign,
			final String msgNo, final Boolean reserveFlag, final String searchFlag,
			final boolean diffFlag,
			List<MBcsignDTO> mBcsignDTOList
			)
	{
		List<MItem> items = getMItemWithMBcsign(selectMst, mstVersion, bctype,
				groupName, itemCode, bcSign,
				msgNo, reserveFlag, searchFlag);

		// 返却用のMBcsignDTOのリストを作成
		for (MItem item : items) {
			if (item.mBcsignList.size() == 0) {
				MBcsignDTO dto = new MBcsignDTO(item);
				mBcsignDTOList.add(dto);
			} else {
				for (MBcsign bcsign : item.mBcsignList) {
					MBcsignDTO dto = new MBcsignDTO(bcsign);
					mBcsignDTOList.add(dto);
				}
			}
		}

		String diffResultMsg = null;

		// trueの場合、差異確認を行う
		if (diffFlag) {
			// 指示マスタのデータを取得します。(本番)
			List<MItem> itemsReal = getMItemWithMBcsign(ServletUtil.SELECT_MST, mstVersion, bctype,
					groupName, itemCode, bcSign,
					msgNo, reserveFlag, searchFlag);

			// 返却用のMBcsignDTOのリストを作成
			List<MBcsignDTO> mBcsignDTOListReal = new ArrayList<MBcsignDTO>();
			for (MItem item : itemsReal) {
				if (item.mBcsignList.size() == 0) {
					MBcsignDTO dto = new MBcsignDTO(item);
					mBcsignDTOListReal.add(dto);
				} else {
					for (MBcsign bcsign : item.mBcsignList) {
						MBcsignDTO dto = new MBcsignDTO(bcsign);
						mBcsignDTOListReal.add(dto);
					}
				}
			}

			// すべて一致フラグ
			boolean diffAll = false;
			// 本番にデータがあるか
			boolean diffTemp;
			String temp;
			String real;
			String tempBcSign;
			String realBcSign;
			int tempItemCode;
			int realItemCode;
			// 記号、検査内容、ダミー記号、ファイル名が一致しているかチェック
			for (MBcsignDTO dtoTemp : mBcsignDTOList) {
				diffTemp = false;
				for (MBcsignDTO dtoReal : mBcsignDTOListReal) {
					tempBcSign = (dtoTemp.bcSign == null) ? "" : dtoTemp.bcSign;
					realBcSign = (dtoReal.bcSign == null) ? "" : dtoReal.bcSign;
					tempItemCode = (dtoTemp.itemCode == null) ? 0 : dtoTemp.itemCode;
					realItemCode = (dtoReal.itemCode == null) ? 0 : dtoReal.itemCode;

					// 同じ指示記号を探す
					if (tempBcSign.equals(realBcSign) && tempItemCode == realItemCode) {
						// 本番にデータ有
						diffTemp = true;

						// 検査内容
						temp = nullToSpc(dtoTemp.signContents);
						real = nullToSpc(dtoReal.signContents);
						if (!temp.equals(real)) {
							dtoTemp.signContentsUnMat = true;
							diffAll = true;
						}
						// ダミー記号
						temp = nullToSpc(dtoTemp.dummySign);
						real = nullToSpc(dtoReal.dummySign);
						if (!temp.equals(real)) {
							dtoTemp.dummySignUnMat = true;
							diffAll = true;
						}
						// ファイル名
						temp = nullToSpc(dtoTemp.fileName);
						real = nullToSpc(dtoReal.fileName);
						if (!temp.equals(real)) {
							dtoTemp.fileNameUnMat = true;
							diffAll = true;
						}
						mBcsignDTOListReal.remove(dtoReal);
						break;
					}
				}
				// 本番にマッチしない
				if (!diffTemp) {
					dtoTemp.bcSignUnMat = true;
					dtoTemp.signContentsUnMat = true;
					dtoTemp.dummySignUnMat = true;
					dtoTemp.fileNameUnMat = true;
					diffAll = true;
				}
			}

			// 比較結果で表示するダイアログの設定
			Properties props = ResourceUtil.getProperties("application_ja.properties");

			SingletonS2ContainerFactory.init();
			S2Container container = SingletonS2ContainerFactory.getContainer();
			// 本番マスタ差異削除表示件数
			int differenceDetailCount = (Integer)container.getComponent("differenceDetailCount");

			int realDiffCount = mBcsignDTOListReal.size();
			// 件数一致
			if (realDiffCount == 0) {
				// 項目も一致
				if (diffAll==false) {

					boolean isNonSdf = true;

					// 本番削除フラグがたっている場合はメッセージ表示させない不一致対策をする
					for (MBcsignDTO dtoTemp : mBcsignDTOList) {
						if (StringUtils.equals(dtoTemp.sopDeleteFlag, "0") == false) {
							isNonSdf = false;
							break;
						}
					}

					if (isNonSdf) {
						// 本番マスタと内容が一致しています。
						diffResultMsg = props.getProperty("svr0000004");
					}
				}

			// 本番マスタ差異削除表示件数=0の場合
			} else if (differenceDetailCount == 0){
				diffResultMsg = MessageFormat.format(props.getProperty("svr0000005"), String.valueOf(realDiffCount), "");

			// 本番不一致件数 <本番マスタ差異削除表示件数
			} else if (realDiffCount <= differenceDetailCount) {
				String msg = "";
				for (MBcsignDTO dtoReal : mBcsignDTOListReal) {
					msg += "MsgNo " + dtoReal.msgNo + " 記号 " + dtoReal.bcSign + "\n";
				}
				diffResultMsg = MessageFormat.format(props.getProperty("svr0000005"), String.valueOf(realDiffCount), msg);

			} else {
				String msg = "";
				// 他n件
				int other = realDiffCount - differenceDetailCount;
				int i = 0;
				for (MBcsignDTO dtoReal : mBcsignDTOListReal) {
					msg += "MsgNo " + dtoReal.msgNo + " 記号 " + dtoReal.bcSign + "\n";
					i++;
					// 本番マスタ差異削除表示件数分表示
					if (i >= differenceDetailCount) {
						break;
					}
				}
				// nullを空文字に変更
				msg = msg.replaceAll("null", "");
				diffResultMsg = MessageFormat.format(props.getProperty("svr0000006"), String.valueOf(realDiffCount), msg, String.valueOf(other));

			}
		}

		return diffResultMsg;
	}
	// 2016/08/26 DA ins end

	// 2016/02/24 DA ins start
	private String nullToSpc(String value) {
		if (value == null) {
			return "";
		} else {
			return value.trim();
		}
	}
	// 2016/02/24 DA ins end

	/**
	 * 指示マスタのデータを取得します。
	 * 親となる検査項目マスタ（M_ITEM）の子として取得します。
	 *
	 * @param selectMst 取得するマスタの状態。0:仮、1:本番
	 * @param mstVersion マスタバージョン
	 * @param bctype 取得する対象のBC車種区分
	 * @param groupName 取得する対象の工程名
	 * @param itemCode 取得する対象の検査項目
	 * @param bcSign 取得する対象の指示記号
	 * @param msgNo
	 * @param reserveFlag 予約中フラグ
	 * @param searchFlag
	 * @return 指示マスタのデータ
	 */
	public List<MItem> getMItemWithMBcsign(final Integer selectMst,
			final Integer mstVersion, final String bctype,
			final String groupName, final Integer itemCode, final String bcSign,
			// 2016/02/24 DA ins start
			final String msgNo, final Boolean reserveFlag, final String searchFlag)
			// 2016/02/24 DA ins end
	{
		String conVersion = "";
		// バージョン指定が0の場合号口レコードを返す。
		// 2016/02/24 DA upd start
//		if (selectMst.equals(1)) {
		if (MstSelectService.isReal(selectMst)) {
//			if (mstVersion == null || mstVersion.equals(0)) {
			if (mstVersion == null || MstSelectService.isTemporary(mstVersion)) {
		// 2016/02/24 DA upd end
				// 本番・仮選択が『1』で、バージョンが指定されていない場合
				// 本番マスタの最新（号口）データを指定する。
				conVersion = "sopFlag = '1'";
			} else {
				// 本番・仮選択が『1』で、バージョンが指定されている場合
				// 本番マスタの指定バージョンのデータを指定する。
				conVersion = "mstVer = " + mstVersion;
			}
		} else {
			// 本番・仮選択が『0』の場合
			// 仮マスタののデータを指定する。
			// 2016/02/24 DA upd start
//			conVersion = "mstVer = 0";
			conVersion = "mstVer = " + selectMst;
			// 2016/02/24 DA upd end
		}

		// 指示記号が指定されていない場合指示記号による選択を行わない。
		// 指定されていれば指示記号による選択を行う。
		String conBcsign = "";
		if (bcSign != null) {
			conBcsign = " AND mBcsignList.bcSign = '" + bcSign + "'";
		}

		// AutoSelect構築
		AutoSelect<MItem> select = jdbcManager.from(MItem.class)
				.leftOuterJoin("mBcsignList", "mBcsignList.deleteFlag <> '1' " + conBcsign) // MItem.mBcSignListの変数名を指定
				.leftOuterJoin("mBcsignList.updateMUser")
				// 2016/02/24 DA ins start
				.leftOuterJoin("mBcsignList.reserveMUser")
				// 2016/02/24 DA ins end
				.leftOuterJoin("mOrderL", "mOrderL.ptnDiv='1'")
				.leftOuterJoin("mOrderL.mGroup", "mOrderL.mGroup.deleteFlag <> '1'")
				.leftOuterJoin("mOrderR", "mOrderR.ptnDiv='2'")
				.leftOuterJoin("mOrderR.mGroup", "mOrderR.mGroup.deleteFlag <> '1'");

		// WHERE条件構築
		StringBuilder conditions = new StringBuilder();
		conditions.append(" deleteFlag <> '1'");
		conditions.append(" AND " + conVersion);

		// 車種条件付与
		if (bctype != null) {
			conditions.append(" AND bctype = '" + bctype + "'");
		}

		// アイテム条件付与
		if (itemCode != null) {
			conditions.append(" AND itemCode = " + itemCode);
		}

		// 2016/02/24 DA ins start
		if(msgNo != null && msgNo.length() > 0) {
			conditions.append(" AND msgNo = '" + msgNo + "'");
		}

		if (reserveFlag == null) {
			//予約中以外
			conditions.append(" AND mBcsignList.reserveFlag NOT IN ('1', '2')");
		} else if(reserveFlag != null && reserveFlag) {
			conditions.append(" AND mBcsignList.reserveFlag IN ('1', '2')");
		}

		// 画像一覧出力の場合
		if (searchFlag != null) {
			// 検査順が0以外
			if (searchFlag.equals("1")) {
				conditions.append(" AND (mOrderL.inspecOrder is null OR mOrderL.inspecOrder <> 0)");
			// 検査順が0のデータ
			} else if (searchFlag.equals("2")) {
				conditions.append(" AND mOrderL.inspecOrder = 0");
			}
		}
		// 2016/02/24 DA ins end

		// 工程名称の条件付与
		if (StringUtils.equals(groupName, ALL_GROUPS)) {
			select = select.where(conditions.toString());
		} else if (StringUtils.equals(groupName, NO_GROUPS)) {
			conditions.append(" AND ");
			conditions.append(" (mOrderL.mGroup.groupName IS NULL OR");
			conditions.append(" mOrderR.mGroup.groupName IS NULL)");
			select = select.where(conditions.toString());
		} else {
			conditions.append(" AND ");
			conditions.append(" (mOrderL.mGroup.groupName = ? OR");
			conditions.append(" mOrderR.mGroup.groupName = ?)");
			select = select.where(conditions.toString(), groupName, groupName);
		}

		// DB検索
		// 2016/02/24 DA upd start
//		List<MItem> items = select.orderBy("msgNo, mBcsignList.bcSign, itemCode").getResultList();
		List<MItem> items = null;
		if(searchFlag == null) {
			items = select.orderBy("msgNo, mBcsignList.bcSign, itemCode").getResultList();
		} else if(searchFlag.equals("1") || searchFlag.equals("2")) {
//			items = select.orderBy("mOrderL.ptnDiv, mOrderL.inspecOrder, msgNo, mBcsignList.bcSign, itemCode").getResultList();
			items = select.orderBy("mOrderL.ptnDiv, mOrderL.inspecOrder, "
					+ "mOrderR.ptnDiv, mOrderR.inspecOrder,"
					+ "msgNo, mBcsignList.bcSign, itemCode").getResultList();
		}
		// 2016/02/24 DA upd end

		return items;
	}


	/**
	 * 指示記号変換マスタを1レコード更新します.
	 *
	 * @param dto
	 *            追加／更新レコード
	 */
	public void update(final MBcsignDTO dto) {
		// 画像の格納先パス
		String path = "/images/";
		// 更新レコードにセットするタイムスタンプ
		java.util.Date now = new java.util.Date();
		Timestamp timestamp = new Timestamp(now.getTime());
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateStr = df.format(now);

		// ファイル本体の保存
		if (dto.fileBody != null) {
			// 保存用ファイル名を決定
			String bcsign = Utils.urlEncode(StringUtils.trimToEmpty(dto.bcSign), "UTF-8");
			String fileName = dto.itemCode + "-" + bcsign + "-" + dateStr
					+ ".jpg";
			String fullPath = ServletContextUtil.getServletContext()
					.getRealPath(path + fileName);

			// ファイル保存
			FileUtil.write(fullPath, dto.fileBody);
			// DB登録用ファイル名をセット
			dto.fileName = fileName;
		}

		// 値のスペースなどの加工をする
		dto.bcSign =  StringUtils.trimToEmpty(dto.bcSign);
		dto.signContents = Utils.trimDbSetting(dto.signContents);
		dto.dummySign = Utils.trimDbSetting(dto.dummySign);
		dto.notes = Utils.trimDbSetting(dto.notes);

		// DB登録
		dto.updateDate = timestamp;
		MBcsign bcsign = dto.getMBcsign();
		if (dto.editDiv.equals("I")) {
			// 新規追加
			bcsign.insertDate = timestamp;
			// 2016/02/24 DA ins start
			//jdbcManager.insert(bcsign).excludes("sopFlag", "deleteFlag")
			jdbcManager.insert(bcsign).excludes("reserveFlag", "sopDeleteFlag", "sopFlag", "deleteFlag")
			// 2016/02/24 DA ins end
					.execute();
		} else if (dto.editDiv.equals("U")) {
			// 更新
			//jdbcManager.update(bcsign).execute();
			super.updateEntity(bcsign);
		}
	}

	/**
	 * 指示記号レコードをテーブルから削除します.
	 *
	 * @param removedRows
	 *            削除対象の指示記号レコードのリスト
	 */
	public void remove(final List<MBcsignDTO> removedRows) {
		java.util.Date now = new java.util.Date();
		Timestamp timestamp = new Timestamp(now.getTime());
		for (MBcsignDTO dto : removedRows) {
			//jdbcManager.delete(dto.getMBcsign()).execute();
			dto.updateDate = timestamp;
			super.deleteEntity(dto.getMBcsign());
		}
	}

	// 2016/02/24 DA ins start
	/**
	 * 指示記号マスタの予約情報を取得するSELECT文
	 * @param selectMst 取得する仮マスタ。nullの場合はすべて対象。
	 * @param bctype 取得する対象のBC車種区分
	 * @param signCodeList 対象の指示記号Codeの一覧。nullの場合はすべて対象。
	 * @param flag 予約状態
	 * @return SELECT文
	 */
	private AutoSelect<MBcsign> selectReserveFlagSign(final Integer selectMst, final String bctype, final ArrayList<String> signCodeList, final ReservationState flag)
	{
		ComplexWhere sWhere = new ComplexWhere();

		if (selectMst == null) {
			// すべて
			sWhere.in("mstVer", MstSelectService.getTempMasterCodeList());
		}
		else {
			// 指定
			sWhere.eq("mstVer", selectMst);
		}

		sWhere.eq("mItem.bctype", bctype);
		sWhere.in("signCode", signCodeList);
		if (flag != null && flag.equals(ReservationState.RESERVATION)) {
			sWhere.in("reserveFlag", "1", "2");
		}
		else if (flag != null && flag.equals(ReservationState.RESERV_ONLY)) {
			sWhere.in("reserveFlag", "1");
		}
		else {
			sWhere.in("reserveFlag", "2");
		}
		sWhere.eq("deleteFlag", "0");
		sWhere.eq("mItem.deleteFlag", "0");

		return this.jdbcManager.from(MBcsign.class)
				.innerJoin("mItem")
				.leftOuterJoin("reserveMUser")
				.where(sWhere)
				.orderBy("mItem.msgNo, bcSign, itemCode");
	}

	/**
	 * 指示記号の予約状況を取得
	 * @param selectMst 取得する仮マスタ
	 * @param bctype 取得する対象のBC車種区分
	 * @param signCodeList 対象の指示記号Codeの一覧。nullの場合はすべて対象。
	 * @param flag 予約状態
	 * @return 判定結果（true:予約中、false:予約中なし）
	 */
	public Boolean getReserveFlagSign(final Integer selectMst, final String bctype, final ArrayList<String> signCodeList, final ReservationState flag)
	{
		Boolean result = false;

		// 本番マスタ選択の場合、本番には存在しないので必ず予約中はなし。
		if (MstSelectService.isReal(selectMst)) {
			return result;
		}

		// 0件の場合は、一致させる条件がないので予約中はなし。
		if (signCodeList != null && signCodeList.size() == 0) {
			return result;
		}

		// チェックする仮マスタを設定する
		Integer mst = null;
		mst = selectMst;

		long count = selectReserveFlagSign(mst, bctype, signCodeList, flag).orderBy("").getCount();
		if (count == 0) {
			result = false;
		}
		else {
			result = true;
		}

		return result;
	}

	/**
	 * 指示記号の予約者を取得
	 * @param selectMst 取得する仮マスタ
	 * @param bctype 取得する対象のBC車種区分
	 * @param signCodeList 対象の指示記号Codeの一覧。nullの場合はすべて対象。
	 * @param flag 予約状態
	 * @return 予約者一覧
	 */
	public ArrayList<String> getReservationUserVehicleSign(final Integer selectMst, final String bctype, final ArrayList<String> signCodeList, final ReservationState flag)
	{
		ArrayList<String> list = new ArrayList<String>();

		// 本番マスタ選択の場合、本番には存在しないので必ず予約中はなし。
		if (MstSelectService.isReal(selectMst)) {
			return list;
		}

		// 0件の場合は、一致させる条件がないので予約中はなし。
		if (signCodeList != null && signCodeList.size() == 0) {
			return list;
		}

		// チェックする仮マスタを設定する
		Integer mst = null;
		mst = selectMst;

		List<MBcsign> bcsigns = selectReserveFlagSign(mst, bctype, signCodeList, flag).getResultList();

		// 予約者で集約する。
		for (MBcsign bcsign : bcsigns) {

			String reserveName = "";
			if (bcsign.reserveMUser == null || bcsign.reserveMUser.userName == null || bcsign.reserveMUser.userName.trim().equals("")) {
				reserveName = bcsign.reserveUser;
			}
			else {
				reserveName = bcsign.reserveMUser.userName;
			}

			if (list.contains(reserveName) == false) {
				list.add(reserveName);
			}
		}

		return list;
	}

	/**
	 * 指示記号の予約情報を取得
	 * @param selectMst 取得する仮マスタ
	 * @param bctype 取得する対象のBC車種区分
	 * @param signCodeList 対象の指示記号Codeの一覧。nullの場合はすべて対象。
	 * @param flag 予約状態
	 * @return 予約情報一覧
	 */
	public ArrayList<String> getReservationInformationMVehicleSign(final Integer selectMst, final String bctype, final ArrayList<String> signCodeList, final ReservationState flag)
	{
		ArrayList<String> list = new ArrayList<String>();

		// 本番マスタ選択の場合、本番には存在しないので必ず予約中はなし。
		if (MstSelectService.isReal(selectMst)) {
			return list;
		}

		// 0件の場合は、一致させる条件がないので予約中はなし。
		if (signCodeList != null && signCodeList.size() == 0) {
			return list;
		}

		// チェックする仮マスタを設定する
		Integer mst = null;
		mst = selectMst;

		List<MBcsign> bcsigns = selectReserveFlagSign(mst, bctype, signCodeList, flag).getResultList();

		// 予約日時と予約者で集約する。
		for (MBcsign bcsign : bcsigns) {

			String reserveDate = "";
			if (bcsign.reserveDate != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				reserveDate = sdf.format(bcsign.reserveDate);
			}

			String reserveName = "";
			if (bcsign.reserveMUser == null || bcsign.reserveMUser.userName == null || bcsign.reserveMUser.userName.trim().equals("")) {
				reserveName = bcsign.reserveUser;
			}
			else {
				reserveName = bcsign.reserveMUser.userName;
			}

			String reserveInfo = reserveDate + ", " + reserveName;

			if (list.contains(reserveInfo) == false) {
				list.add(reserveInfo);
			}
		}

		return list;
	}

	/**
	 * 指示記号マスタの予約情報を取得するSELECT文
	 * @param selectMst 取得する仮マスタ。nullの場合はすべて対象。
	 * @param bctype 取得する対象のBC車種区分
	 * @param itemCodeList 対象の項目Codeの一覧。nullの場合はすべて対象。
	 * @param flag 予約状態
	 * @return SELECT文
	 */
	private AutoSelect<MItem> selectReserveFlag(final Integer selectMst, final String bctype, final ArrayList<String> itemCodeList, final ReservationState flag)
	{
		ComplexWhere sWhere = new ComplexWhere();

		if (selectMst == null) {
			// すべて
			sWhere.in("mstVer", MstSelectService.getTempMasterCodeList());
		}
		else {
			// 指定
			sWhere.eq("mstVer", selectMst);
		}

		sWhere.eq("bctype", bctype);
		sWhere.in("itemCode", itemCodeList);
		sWhere.eq("deleteFlag", "0");
		if (flag != null && flag.equals(ReservationState.RESERVATION)) {
			sWhere.in("mBcsignList.reserveFlag", "1", "2");
		}
		else {
			sWhere.in("mBcsignList.reserveFlag", "2");
		}
		sWhere.eq("mBcsignList.deleteFlag", "0");

		return this.jdbcManager.from(MItem.class)
				.innerJoin("mBcsignList")
				.leftOuterJoin("mBcsignList.reserveMUser")
				.where(sWhere)
				.orderBy("msgNo, mBcsignList.bcSign, itemCode");
	}

	/**
	 * 車種、項目コードに紐付く指示記号の予約状況を取得
	 * @param selectMst 取得する仮マスタ
	 * @param bctype 取得する対象のBC車種区分
	 * @param itemCodeList 対象の項目Codeの一覧。nullの場合はすべて対象。
	 * @param flag 予約状態
	 * @return 判定結果（true:予約中、false:予約中なし）
	 */
	public Boolean getReserveFlag(final Integer selectMst, final String bctype, final ArrayList<String> itemCodeList, final ReservationState flag)
	{
		Boolean result = false;

		// 本番マスタ選択の場合、本番には存在しないので必ず予約中はなし。
		if (MstSelectService.isReal(selectMst)) {
			return result;
		}

		// 0件の場合は、一致させる条件がないので予約中はなし。
		if (itemCodeList != null && itemCodeList.size() == 0) {
			return result;
		}

		// チェックする仮マスタを設定する
		Integer mst = null;
		mst = selectMst;

		long count = selectReserveFlag(mst, bctype, itemCodeList, flag).orderBy("").getCount();
		if (count == 0) {
			result = false;
		}
		else {
			result = true;
		}

		return result;
	}

	/**
	 * 車種、項目コードに紐付く指示記号の予約者を取得
	 * @param selectMst 取得する仮マスタ
	 * @param bctype 取得する対象のBC車種区分
	 * @param itemCodeList 対象の項目Codeの一覧。nullの場合はすべて対象。
	 * @param flag 予約状態
	 * @return 予約者一覧
	 */
	public ArrayList<String> getReservationUserVehicle(final Integer selectMst, final String bctype, final ArrayList<String> itemCodeList, final ReservationState flag)
	{
		ArrayList<String> list = new ArrayList<String>();

		// 本番マスタ選択の場合、本番には存在しないので必ず予約中はなし。
		if (MstSelectService.isReal(selectMst)) {
			return list;
		}

		// 0件の場合は、一致させる条件がないので予約中はなし。
		if (itemCodeList != null && itemCodeList.size() == 0) {
			return list;
		}

		// チェックする仮マスタを設定する
		Integer mst = null;
		mst = selectMst;

		List<MItem> items = selectReserveFlag(mst, bctype, itemCodeList, flag).getResultList();

		// 予約者で集約する。
		for (MItem item : items) {

			String reserveName = "";
			for (MBcsign bcsign : item.mBcsignList) {
				if (bcsign.reserveMUser == null || bcsign.reserveMUser.userName == null || bcsign.reserveMUser.userName.trim().equals("")) {
					reserveName = bcsign.reserveUser;
				}
				else {
					reserveName = bcsign.reserveMUser.userName;
				}

				if (list.contains(reserveName) == false) {
					list.add(reserveName);
				}

				// 同一の項目コードは同じ予約者のため、次へ
				break;
			}
		}

		return list;
	}

	/**
	 * 車種、項目コードに紐付く指示記号の予約情報を取得
	 * @param selectMst 取得する仮マスタ
	 * @param bctype 取得する対象のBC車種区分
	 * @param itemCodeList 対象の項目Codeの一覧。nullの場合はすべて対象。
	 * @param flag 予約状態
	 * @return 予約情報一覧
	 */
	public ArrayList<String> getReservationInformationMVehicle(final Integer selectMst, final String bctype, final ArrayList<String> itemCodeList, final ReservationState flag)
	{
		ArrayList<String> list = new ArrayList<String>();

		// 本番マスタ選択の場合、本番には存在しないので必ず予約中はなし。
		if (MstSelectService.isReal(selectMst)) {
			return list;
		}

		// 0件の場合は、一致させる条件がないので予約中はなし。
		if (itemCodeList != null && itemCodeList.size() == 0) {
			return list;
		}

		// チェックする仮マスタを設定する
		Integer mst = null;
		mst = selectMst;

		List<MItem> items = selectReserveFlag(mst, bctype, itemCodeList, flag).getResultList();

		// 予約日時と予約者で集約する。
		for (MItem item : items) {

			for (MBcsign bcsign : item.mBcsignList) {

				String reserveDate = "";
				if (bcsign.reserveDate != null) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					reserveDate = sdf.format(bcsign.reserveDate);
				}

				String reserveName = "";
				if (bcsign.reserveMUser == null || bcsign.reserveMUser.userName == null || bcsign.reserveMUser.userName.trim().equals("")) {
					reserveName = bcsign.reserveUser;
				}
				else {
					reserveName = bcsign.reserveMUser.userName;
				}

				String reserveInfo = reserveDate + ", " + reserveName;

				if (list.contains(reserveInfo) == false) {
					list.add(reserveInfo);
				}

				// 同一の項目コードは同じ予約者のため、次へ
				break;
			}
		}

		return list;
	}

	/**
	 * 車種、工程コードに紐付く指示記号の予約状況を取得
	 * @param selectMst 取得する仮マスタ
	 * @param bctype 取得する対象のBC車種区分
	 * @param groupName 対象の工程名称
	 * @param flag 予約状態
	 * @return 判定結果（true:予約中、false:予約中なし）
	 */
	public Boolean getReserveFlagGroup(final Integer selectMst, final String bctype, final String groupName, final ReservationState flag)
	{
		Boolean result = false;
		ComplexWhere sWhere = new ComplexWhere();
		ComplexWhere sWhereL = new ComplexWhere();
		ComplexWhere sWhereR = new ComplexWhere();
		ComplexWhere sWhereGroupL = new ComplexWhere();
		ComplexWhere sWhereGroupR = new ComplexWhere();

		if (MstSelectService.isReal(selectMst)) {
			// 本番マスタ選択の場合
			// 本番は存在しないため
			return false;
		}
		else {
			// 仮マスタ選択の場合
			// 仮マスタのデータを指定する。
			sWhere.eq("mstVer", selectMst);
		}

		sWhere.eq("bctype", bctype);
		sWhere.eq("deleteFlag", "0");

		if (flag != null && flag.equals(ReservationState.RESERVATION)) {
			sWhere.in("mBcsignList.reserveFlag", "1", "2");
		}
		else {
			sWhere.in("mBcsignList.reserveFlag", "2");
		}
		sWhere.eq("mBcsignList.deleteFlag", "0");

		if (StringUtils.equals(groupName, ALL_GROUPS)) {
		}
		else if (StringUtils.equals(groupName, NO_GROUPS)) {
			sWhere.and(new ComplexWhere().isNull("mOrderL.mGroup.groupName", true).or().isNull("mOrderR.mGroup.groupName", true));
		}
		else {
			sWhere.and(new ComplexWhere().eq("mOrderL.mGroup.groupName", groupName).or().eq("mOrderR.mGroup.groupName", groupName));
		}

		// 左H
		sWhereL.eq("mOrderL.ptnDiv", "1");
		sWhereGroupL.eq("mOrderL.mGroup.deleteFlag", "0");

		// 右H
		sWhereR.eq("mOrderR.ptnDiv", "2");
		sWhereGroupR.eq("mOrderR.mGroup.deleteFlag", "0");

		long count = this.jdbcManager.from(MItem.class)
				.innerJoin("mBcsignList")
				.leftOuterJoin("mOrderL", sWhereL)
				.leftOuterJoin("mOrderL.mGroup", sWhereGroupL)
				.leftOuterJoin("mOrderR", sWhereR)
				.leftOuterJoin("mOrderR.mGroup", sWhereGroupR)
				.leftOuterJoin("mBcsignList.reserveMUser")
				.where(sWhere).getCount();

		if (count == 0) {
			result = false;
		}
		else {
			result = true;
		}

		return result;
	}

	/**
	 * MsgNo一覧を取得
	 * @param selectMst
	 * @param mstVersion
	 * @param bctype
	 * @param groupName
	 * @param itemCode
	 * @param bcSign
	 * @param msgNo
	 * @param reserveFlag
	 * @return
	 */
	public List<String> getMsgNoList(final Integer selectMst,
			final Integer mstVersion, final String bctype,
			final String groupName, final Integer itemCode,
			final String bcSign,
			final String msgNo, final Boolean reserveFlag)
	{

		List<MItem> items = getMItemWithMBcsign(selectMst, mstVersion, bctype, groupName, itemCode, bcSign, msgNo, reserveFlag, null);

		// 返却用のMsnNoのリストを作成
		List<String> msgNoList = new ArrayList<String>();
		for (MItem item : items) {
			if(item.msgNo == null || item.msgNo.trim().equals("")) {
				continue;
			}

			if(msgNoList.contains(item.msgNo) == false) {
				msgNoList.add(item.msgNo);
			}
		}

		return msgNoList;
	}

	/**
	 * 画像一覧出力権限を取得
	 * @param bctype
	 * @param loginUser
	 * @return
	 */
	public Boolean getImageOutputFlag(final String bctype, final String loginUser)
	{
		MVehicle vehicle;
		MUser user;

		// ログインユーザーの権限のチェック
		user = this.jdbcManager.from(MUser.class).where("userCode = ?", loginUser).getSingleResult();
		// マスタ管理者の場合は出力OK
		if(user.authority.equals("3")) {
			return true;
		}

		// 画像一覧出力OKフラグのチェック
		vehicle = this.jdbcManager.from(MVehicle.class).where("bctype = ?", bctype).getSingleResult();
		if(vehicle.listOkFlag == false) {
			return false;
		}

		return true;
	}

	/**
	 * シート作成（テンプレートシートをコピーする）
	 * @param workbook ワークブック
	 * @param sheetName 作成するシート名
	 * @return 作成したシート
	 */
	private XSSFSheet createSheet(XSSFWorkbook workbook, String sheetName)
	{
		XSSFSheet newSheet = workbook.cloneSheet(0);
		String newSheetName = newSheet.getSheetName();
		int newSheetIndex = workbook.getSheetIndex(newSheetName);
		workbook.setSheetName(newSheetIndex, sheetName);
		return newSheet;
	}

	/**
	 * 画像一覧Excelの作成
	 * @param selectMst
	 * @param mstVersion
	 * @param bctype
	 * @param groupName
	 * @param itemCode
	 * @param bcSign
	 * @param msgNo
	 * @param reserveFlag
	 * @param searchFlag
	 * @param loginUser
	 * @return
	 */
	public byte[] imageListOutput(final Integer selectMst,
			final Integer mstVersion, final String bctype,
			final String groupName, final Integer itemCode,
			final String bcSign,
			final String msgNo, final Boolean reserveFlag, final String searchFlag, final String loginUser)
	{
		final int VEHICLE_ROW = 2;
		final int VEHICLE_COL = 3;

		final int GROUP_ROW = 3;
		final int GROUP_COL = 3;

		final int PTN_DIV_ROW = 4;
		final int PTN_DIV_COL = 3;

		final int MASTER_ROW = 5;
		final int MASTER_COL = 3;

		final int MSG_NO_ROW = 6;
		final int MSG_NO_COL = 3;

		final int OUTPUT_USER_ROW = 7;
		final int OUTPUT_USER_COL = 3;

		final int OUTPUT_DATE_ROW = 8;
		final int OUTPUT_DATE_COL = 3;

		final int GRID_ROW_INDEX = 11;
		final int GRID_GROUP_ORDER_COL = 1;
		final int GRID_ITEM_NAME_COL = 2;
		final int GRID_MSG_NO_COL = 3;
		final int GRID_BC_SIGN_COL = 4;
		final int GRID_SIGN_CONTENTS_COL = 5;
		final int GRID_DUMMY_SIGN_COL = 6;
		final int GRID_FILE_NAME_COL = 7;

		byte[] data = null;
		Integer prevItemCode = null;
		CellStyle cellStyleRect;

		try {
			// 検査順 =0を最後にするため、２回検索しマージする。
			List<MBcsignDTO> mBcsignDTOList = this.getMBcsignDTO(selectMst, mstVersion, bctype, groupName, itemCode, bcSign, msgNo, reserveFlag, "1", false);
			List<MBcsignDTO> mBcsignDTOList2 = this.getMBcsignDTO(selectMst, mstVersion, bctype, groupName, itemCode, bcSign, msgNo, reserveFlag, "2", false);
			for (MBcsignDTO dao : mBcsignDTOList2) {
				mBcsignDTOList.add(dao);
			}

			String templateFileName = (String) SingletonS2ContainerFactory.getContainer().getComponent("imageListTemplateFile");
			String leftSheetName = (String) SingletonS2ContainerFactory.getContainer().getComponent("imageListLeftDefaultSheet");
			String rightSheetName = (String) SingletonS2ContainerFactory.getContainer().getComponent("imageListRightDefaultSheet");

			// テンプレートファイル読み込み
			File template = ResourceUtil.getResourceAsFile(templateFileName);

			//読み込みたいExcelファイルを指定する
			XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(template));

			// シートを選択
			XSSFSheet leftSheet = createSheet(workbook, leftSheetName);
			XSSFSheet rightSheet = createSheet(workbook, rightSheetName);

			cellStyleRect = workbook.createCellStyle();
			cellStyleRect.setBorderTop(CellStyle.BORDER_THIN);
			cellStyleRect.setBorderBottom(CellStyle.BORDER_THIN);
			cellStyleRect.setBorderLeft(CellStyle.BORDER_THIN);
			cellStyleRect.setBorderRight(CellStyle.BORDER_THIN);


			MVehicle vehicle = this.getMVehicle(bctype);
			if(vehicle != null) {
				this.setCell(leftSheet, VEHICLE_ROW, VEHICLE_COL, vehicle.vehicleName);
				this.setCell(rightSheet, VEHICLE_ROW, VEHICLE_COL, vehicle.vehicleName);
			}

			if("_all groups".equals(groupName)) {
				this.setCell(leftSheet, GROUP_ROW, GROUP_COL, "全て");
				this.setCell(rightSheet, GROUP_ROW, GROUP_COL, "全て");
			} else {
				this.setCell(leftSheet, GROUP_ROW, GROUP_COL, groupName);
				this.setCell(rightSheet, GROUP_ROW, GROUP_COL, groupName);
			}

			this.setCell(leftSheet, PTN_DIV_ROW, PTN_DIV_COL, leftSheetName);
			this.setCell(rightSheet, PTN_DIV_ROW, PTN_DIV_COL, rightSheetName);

			String masterName = MstSelectService.getMasterName(selectMst);
			this.setCell(leftSheet, MASTER_ROW, MASTER_COL, masterName);
			this.setCell(rightSheet, MASTER_ROW, MASTER_COL, masterName);

			this.setCell(leftSheet, MSG_NO_ROW, MSG_NO_COL, msgNo);
			this.setCell(rightSheet, MSG_NO_ROW, MSG_NO_COL, msgNo);

			MUser user = this.getMUser(loginUser);
			this.setCell(leftSheet, OUTPUT_USER_ROW, OUTPUT_USER_COL, user.userName);
			this.setCell(rightSheet, OUTPUT_USER_ROW, OUTPUT_USER_COL, user.userName);

			String outputDate = DateFormatUtils.format(new Date(), "yyyy/MM/dd HH:mm:ss");
			this.setCell(leftSheet, OUTPUT_DATE_ROW, OUTPUT_DATE_COL, outputDate);
			this.setCell(rightSheet, OUTPUT_DATE_ROW, OUTPUT_DATE_COL, outputDate);

			int leftRowIndex = GRID_ROW_INDEX;
			int rightRowIndex = GRID_ROW_INDEX;
			for(MBcsignDTO mBcsignDTO : mBcsignDTOList) {
				// 2018/10/16 TMC ADD START 他の工程を表示させないように追加
				if ((mBcsignDTO.groupNameL != null && mBcsignDTO.groupNameL.equals(groupName)) || "_all groups".equals(groupName)) {
				// 2018/10/16 TMC ADD END
					if(mBcsignDTO.groupNoL != null && mBcsignDTO.groupNoL.length() > 0) {
						// 左H
						this.setCell(leftSheet, leftRowIndex, GRID_GROUP_ORDER_COL, mBcsignDTO.groupOrderL);
						this.setCell(leftSheet, leftRowIndex, GRID_ITEM_NAME_COL, mBcsignDTO.itemName);
						this.setCell(leftSheet, leftRowIndex, GRID_MSG_NO_COL, mBcsignDTO.msgNo + "-" + mBcsignDTO.bcPosition + "-" + mBcsignDTO.bcLength);
						this.setCell(leftSheet, leftRowIndex, GRID_BC_SIGN_COL, mBcsignDTO.bcSign);
						this.setCell(leftSheet, leftRowIndex, GRID_SIGN_CONTENTS_COL, mBcsignDTO.signContents);
						this.setCell(leftSheet, leftRowIndex, GRID_DUMMY_SIGN_COL, mBcsignDTO.dummySign);
						this.setPicture(workbook, leftSheet, leftRowIndex, GRID_FILE_NAME_COL, mBcsignDTO.fileName);

						leftSheet.getRow(leftRowIndex).setHeightInPoints(189);

						leftSheet.getRow(leftRowIndex).getCell(GRID_GROUP_ORDER_COL).setCellStyle(workbook.createCellStyle());
						leftSheet.getRow(leftRowIndex).getCell(GRID_GROUP_ORDER_COL).getCellStyle().setBorderLeft(CellStyle.BORDER_THIN);
						leftSheet.getRow(leftRowIndex).getCell(GRID_GROUP_ORDER_COL).getCellStyle().setBorderRight(CellStyle.BORDER_THIN);

						leftSheet.getRow(leftRowIndex).getCell(GRID_ITEM_NAME_COL).setCellStyle(workbook.createCellStyle());
						leftSheet.getRow(leftRowIndex).getCell(GRID_ITEM_NAME_COL).getCellStyle().setBorderLeft(CellStyle.BORDER_THIN);
						leftSheet.getRow(leftRowIndex).getCell(GRID_ITEM_NAME_COL).getCellStyle().setBorderRight(CellStyle.BORDER_THIN);

						leftSheet.getRow(leftRowIndex).getCell(GRID_MSG_NO_COL).setCellStyle(workbook.createCellStyle());
						leftSheet.getRow(leftRowIndex).getCell(GRID_MSG_NO_COL).getCellStyle().setBorderLeft(CellStyle.BORDER_THIN);
						leftSheet.getRow(leftRowIndex).getCell(GRID_MSG_NO_COL).getCellStyle().setBorderRight(CellStyle.BORDER_THIN);

						leftSheet.getRow(leftRowIndex).getCell(GRID_BC_SIGN_COL).setCellStyle(cellStyleRect);
						leftSheet.getRow(leftRowIndex).getCell(GRID_SIGN_CONTENTS_COL).setCellStyle(cellStyleRect);
						leftSheet.getRow(leftRowIndex).getCell(GRID_DUMMY_SIGN_COL).setCellStyle(cellStyleRect);
						leftSheet.getRow(leftRowIndex).getCell(GRID_FILE_NAME_COL).setCellStyle(cellStyleRect);

						if(prevItemCode != null) {
							if (prevItemCode.equals(mBcsignDTO.itemCode)) {
								this.setCell(leftSheet, leftRowIndex, GRID_GROUP_ORDER_COL, "");
								this.setCell(leftSheet, leftRowIndex, GRID_ITEM_NAME_COL, "");
								this.setCell(leftSheet, leftRowIndex, GRID_MSG_NO_COL, "");
							}
							else {
								leftSheet.getRow(leftRowIndex).getCell(GRID_GROUP_ORDER_COL).getCellStyle().setBorderTop(CellStyle.BORDER_THIN);
								leftSheet.getRow(leftRowIndex).getCell(GRID_ITEM_NAME_COL).getCellStyle().setBorderTop(CellStyle.BORDER_THIN);
								leftSheet.getRow(leftRowIndex).getCell(GRID_MSG_NO_COL).getCellStyle().setBorderTop(CellStyle.BORDER_THIN);
							}
						}
						leftRowIndex++;
					}
				}
// 2018/10/16 TMC ADD START
				// ループをいったん区切る
				prevItemCode = mBcsignDTO.itemCode;
			}

			// ここでmBcsignDTOListのデータを右ソートします
			Collections.sort(mBcsignDTOList, new ComparatorClass());

			// 前回検査項目のクリア
			prevItemCode = null;

			for(MBcsignDTO mBcsignDTO : mBcsignDTOList) {
				if ((mBcsignDTO.groupNameR != null && mBcsignDTO.groupNameR.equals(groupName)) || "_all groups".equals(groupName)) {
// 2018/10/16 TMC ADD END
					if(mBcsignDTO.groupNoR != null && mBcsignDTO.groupNoR.length() > 0) {
						// 右H
						this.setCell(rightSheet, rightRowIndex, GRID_GROUP_ORDER_COL, mBcsignDTO.groupOrderR);
						this.setCell(rightSheet, rightRowIndex, GRID_ITEM_NAME_COL, mBcsignDTO.itemName);
						this.setCell(rightSheet, rightRowIndex, GRID_MSG_NO_COL, mBcsignDTO.msgNo + "-" + mBcsignDTO.bcPosition + "-" + mBcsignDTO.bcLength);
						this.setCell(rightSheet, rightRowIndex, GRID_BC_SIGN_COL, mBcsignDTO.bcSign);
						this.setCell(rightSheet, rightRowIndex, GRID_SIGN_CONTENTS_COL, mBcsignDTO.signContents);
						this.setCell(rightSheet, rightRowIndex, GRID_DUMMY_SIGN_COL, mBcsignDTO.dummySign);
						this.setPicture(workbook, rightSheet, rightRowIndex, GRID_FILE_NAME_COL, mBcsignDTO.fileName);

						rightSheet.getRow(rightRowIndex).setHeightInPoints(189);

						rightSheet.getRow(rightRowIndex).getCell(GRID_GROUP_ORDER_COL).setCellStyle(workbook.createCellStyle());
						rightSheet.getRow(rightRowIndex).getCell(GRID_GROUP_ORDER_COL).getCellStyle().setBorderLeft(CellStyle.BORDER_THIN);
						rightSheet.getRow(rightRowIndex).getCell(GRID_GROUP_ORDER_COL).getCellStyle().setBorderRight(CellStyle.BORDER_THIN);

						rightSheet.getRow(rightRowIndex).getCell(GRID_ITEM_NAME_COL).setCellStyle(workbook.createCellStyle());
						rightSheet.getRow(rightRowIndex).getCell(GRID_ITEM_NAME_COL).getCellStyle().setBorderLeft(CellStyle.BORDER_THIN);
						rightSheet.getRow(rightRowIndex).getCell(GRID_ITEM_NAME_COL).getCellStyle().setBorderRight(CellStyle.BORDER_THIN);

						rightSheet.getRow(rightRowIndex).getCell(GRID_MSG_NO_COL).setCellStyle(workbook.createCellStyle());
						rightSheet.getRow(rightRowIndex).getCell(GRID_MSG_NO_COL).getCellStyle().setBorderLeft(CellStyle.BORDER_THIN);
						rightSheet.getRow(rightRowIndex).getCell(GRID_MSG_NO_COL).getCellStyle().setBorderRight(CellStyle.BORDER_THIN);

						rightSheet.getRow(rightRowIndex).getCell(GRID_BC_SIGN_COL).setCellStyle(cellStyleRect);
						rightSheet.getRow(rightRowIndex).getCell(GRID_SIGN_CONTENTS_COL).setCellStyle(cellStyleRect);
						rightSheet.getRow(rightRowIndex).getCell(GRID_DUMMY_SIGN_COL).setCellStyle(cellStyleRect);
						rightSheet.getRow(rightRowIndex).getCell(GRID_FILE_NAME_COL).setCellStyle(cellStyleRect);

						if(prevItemCode != null) {
							if (prevItemCode.equals(mBcsignDTO.itemCode)) {
								this.setCell(rightSheet, rightRowIndex, GRID_GROUP_ORDER_COL, "");
								this.setCell(rightSheet, rightRowIndex, GRID_ITEM_NAME_COL, "");
								this.setCell(rightSheet, rightRowIndex, GRID_MSG_NO_COL, "");
							}
							else {
								rightSheet.getRow(rightRowIndex).getCell(GRID_GROUP_ORDER_COL).getCellStyle().setBorderTop(CellStyle.BORDER_THIN);
								rightSheet.getRow(rightRowIndex).getCell(GRID_ITEM_NAME_COL).getCellStyle().setBorderTop(CellStyle.BORDER_THIN);
								rightSheet.getRow(rightRowIndex).getCell(GRID_MSG_NO_COL).getCellStyle().setBorderTop(CellStyle.BORDER_THIN);
							}
						}
						rightRowIndex ++;
					}

				}

				prevItemCode = mBcsignDTO.itemCode;

			}

			leftSheet.getRow(leftRowIndex - 1).getCell(GRID_GROUP_ORDER_COL).getCellStyle().setBorderBottom(CellStyle.BORDER_THIN);
			leftSheet.getRow(leftRowIndex - 1).getCell(GRID_ITEM_NAME_COL).getCellStyle().setBorderBottom(CellStyle.BORDER_THIN);
			leftSheet.getRow(leftRowIndex - 1).getCell(GRID_MSG_NO_COL).getCellStyle().setBorderBottom(CellStyle.BORDER_THIN);

			rightSheet.getRow(rightRowIndex - 1).getCell(GRID_GROUP_ORDER_COL).getCellStyle().setBorderBottom(CellStyle.BORDER_THIN);
			rightSheet.getRow(rightRowIndex - 1).getCell(GRID_ITEM_NAME_COL).getCellStyle().setBorderBottom(CellStyle.BORDER_THIN);
			rightSheet.getRow(rightRowIndex - 1).getCell(GRID_MSG_NO_COL).getCellStyle().setBorderBottom(CellStyle.BORDER_THIN);

			// テンプレートシートの削除
			workbook.removeSheetAt(0);

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			workbook.write(os);
			data = os.toByteArray();

		} catch(Exception ex) {
			ex.printStackTrace();
		}

		return data;
	}

	// 2018/10/16 TMC ADD START 右Hでソート処理
	public class ComparatorClass implements Comparator<MBcsignDTO> {
		@Override
		public int compare(MBcsignDTO p1, MBcsignDTO p2) {
			int no1, no2;
			if (p1.groupOrderR != null) {
				no1 = p1.groupOrderR;
			}else{
				no1 = 0;
			}
			if (p2.groupOrderR != null){
				no2 = p2.groupOrderR;
			}else{
				no2 = 0;
			}
	        if (no1 > no2) {
	            return 1;
	        } else if (no1 == no2) {
	            return 0;
	        } else {
	            return -1;
	        }
		}
	}
	// 2018/10/16 TMC ADD END

	/**
	 * 画像一覧ファイルダウンロード後のログ処理
	 * @param selectMst
	 * @param mstVersion
	 * @param bctype
	 * @param groupName
	 * @param msgNo
	 * @param loginUser
	 * @param fileName
	 * @return
	 */
	public Boolean imageListOutputLog(final Integer selectMst, final Integer mstVersion, final String bctype, final String groupName, final String msgNo, final String loginUser, final String fileName)
	{
		StringBuilder logStr = new StringBuilder();
		Properties props = ResourceUtil.getProperties("application_ja.properties");
		String msg = props.getProperty("svr0000002");
		String ip = RequestUtil.getRequest().getRemoteAddr();
		String masterName = MstSelectService.getMasterName(selectMst);

		// メッセージ：svr0000002「画像一覧を出力しました。[IPアドレス][ログインID][機能名称][マスタ種別][指定条件][ファイル名]」
		logStr.append("[" + ip + "]");
		logStr.append("[" + loginUser + "]");
		logStr.append("[画像一覧出力]");
		logStr.append("[" + masterName + "]");
		logStr.append("[BC車種区分=" + bctype + ",工程名称=" + groupName + ",MsgNo=" + msgNo + "]");
		logStr.append("[" + fileName + "]");

		String logData = msg.replace("{0}", logStr.toString());
		this.logger.info(logData);

		return true;
	}

	/**
	 * 車種を取得
	 * @param bctype
	 * @return
	 */
	private MVehicle getMVehicle(String bctype) {
		MVehicle vehicle = this.jdbcManager.from(MVehicle.class).where("bctype = ?", bctype).getSingleResult();

		return vehicle;
	}

	/**
	 * ユーザー情報取得
	 * @param userCode
	 * @return
	 */
	private MUser getMUser(String userCode) {
		MUser user = this.jdbcManager.from(MUser.class).where("userCode = ?", userCode).getSingleResult();

		return user;
	}

	/**
	 * Excelのセルに値を設定する
	 * @param sheet
	 * @param row
	 * @param col
	 * @param value
	 */
	private void setCell(XSSFSheet sheet, int row, int col, Object data)
	{

		Row xRow = sheet.getRow(row);
		if(xRow == null)
		{
			xRow = sheet.createRow(row);
		}

		Cell xCell = xRow.getCell(col);
		if(xCell == null)
		{
			xCell = xRow.createCell(col);
		}

		String value = "";
		if(data != null)
		{
			value = data.toString();
		}

		xCell.setCellValue(value);

	}

	/**
	 * Excelに画像を挿入
	 * @param workbook
	 * @param sheet
	 * @param row
	 * @param col
	 * @param imageFile
	 */
	private void setPicture(XSSFWorkbook workbook, XSSFSheet sheet, int row, int col, String imageFile)
	{
		String filePath;
		File file;
		byte[] data;
		int pictureIndex;
		int margin;

		Row xRow = sheet.getRow(row);
		if(xRow == null)
		{
			xRow = sheet.createRow(row);
		}

		Cell xCell = xRow.getCell(col);
		if(xCell == null)
		{
			xCell = xRow.createCell(col);
		}

		filePath = ServletContextUtil.getServletContext().getRealPath("images/" + imageFile);
		file = new File(filePath);
		if(file.exists() == false) {
			return;
		}

		data = FileUtil.getBytes(file);

		margin = XSSFShape.EMU_PER_PIXEL * 10;
		Drawing drawing =sheet.createDrawingPatriarch();
		ClientAnchor anchor = drawing.createAnchor(margin, margin, -margin, -margin, col, row, col + 1, row + 1);
		anchor.setAnchorType(0);

		pictureIndex = workbook.addPicture(data, XSSFWorkbook.PICTURE_TYPE_JPEG);
		Picture pic = drawing.createPicture(anchor, pictureIndex);
	}
	// 2016/02/24 DA ins end

	// 2016/09/26 DA ins start
	/**
	 * 指示記号の削除チェック
	 * 検査項目に紐づく指示記号がすべて削除された状態になるかチェックする
	 * @param selectMst 対象の仮マスタ
	 * @param bctype 対象のBC車種区分
	 * @param mBcsignDTOList 対象の指示記号の一覧
	 * @return 判定結果（NULL:すべて削除された状態の検査項目はない、文字列:すべて削除された状態の検査項目の項目名称）
	 */
	public String getCheckBcsignDelete(final Integer selectMst, final String bctype, final List<MBcsignDTO> mBcsignDTOList)
	{
		String resultName = null;

		// 対象の項目Codeを集約
		Map<String, Integer> itemCodeMap = new HashMap<String, Integer>();
		for (MBcsignDTO mBcsignDTO : mBcsignDTOList) {
			itemCodeMap.put(mBcsignDTO.itemCode.toString(), mBcsignDTO.itemCode);
		}

		// 更新する指示記号の検査項目と検査項目に紐づく指示記号をDBから取得する
		ComplexWhere sWhere = new ComplexWhere();
		sWhere.eq("mstVer", selectMst);
		sWhere.eq("bctype", bctype);
		sWhere.in("itemCode", itemCodeMap.values());
		sWhere.eq("deleteFlag", "0");
		sWhere.eq("mBcsignList.deleteFlag", "0");

		List<MItem> items = this.jdbcManager.from(MItem.class)
				.innerJoin("mBcsignList")
				.where(sWhere)
				.orderBy("msgNo, mBcsignList.bcSign, itemCode")
				.getResultList();

		// DBから取得した情報に、更新する指示記号の本番削除フラグをマージする
		for (MBcsignDTO mBcsignDTO : mBcsignDTOList) {
			for (MItem item : items) {
				if (item.itemCode.equals(mBcsignDTO.itemCode)) {
					int idx = 0;
					for (idx = 0; idx < item.mBcsignList.size(); idx++) {
						MBcsign bcsign = item.mBcsignList.get(idx);
						if (bcsign.signCode != null && bcsign.signCode.equals(mBcsignDTO.signCode)) {
							bcsign.sopDeleteFlag = mBcsignDTO.sopDeleteFlag;
							break;
						}
					}
					if (idx >= item.mBcsignList.size()) {
						MBcsign bcsign = new MBcsign();
						bcsign.signCode = null;		// 新規行を表すためにNULLとする
						bcsign.sopDeleteFlag = mBcsignDTO.sopDeleteFlag;
						item.mBcsignList.add(bcsign);
					}
				}
			}
		}

		// すべて削除された状態になるかどうかチェックする
		for (MItem item : items) {
			int deleteCount = 0;
			for (MBcsign bcsign : item.mBcsignList) {
				if (StringUtils.equals(bcsign.sopDeleteFlag, "1")) {
					deleteCount++;
				}
			}
			if (deleteCount == item.mBcsignList.size()) {
				// 見つかったら、すぐに終了する
				resultName = item.itemName;
				break;
			}
		}

		return resultName;
	}
	// 2016/09/26 DA ins end
}