/**
 *
 */
package jp.co.ctc.service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.seasar.extension.jdbc.Where;
import org.seasar.extension.jdbc.where.ComplexWhere;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

import jp.co.ctc.dto.MBcsignDTO;
import jp.co.ctc.entity.MItem;
import jp.co.ctc.entity.MVehicle;
import jp.co.ctc.entity.VConvertRuleData;
import jp.co.ctc.util.Utils;

/**
 * G-ALCの貼紙記号変換マスタを扱うサービス
 * @author CJ01615
 *
 */
public class VConvertRuleDataService extends GalcAbstractService<VConvertRuleData> {

	/**
	 * MItemService
	 */
	@Resource
	public MItemService mItemService;

	/**
	 * MBcsignService
	 */
	@Resource
	public MBcsignService mBcsignService;

	/**
	 * MVehicleService
	 */
	@Resource
	public MVehicleService mVehicleService;

	/**
	 * 工場別の違いを吸収するためのクラス
	 */
	private Abst a;

	/**
	 * 貼紙記号変換マスタを検索します。
	 * @param bctype BC車種区分
	 * @param tlcode 仮本区分
	 * @return 検索結果データ
	 */
	public List<VConvertRuleData> selectQc(String bctype, String tlcode) {
		return select()
				// 2016/02/24 DA upd start
				.where(a.selectQcWhere(bctype, tlcode))
				//.where(new SimpleWhere()
				//	.in("userGroupCode", (Object[]) a.userGroupCode)
				//	.eq("bctype", bctype)
				//	.eq("testLive", tlcode))
				// 2016/02/24 DA upd end
				.orderBy("bctype, msgno, broadcastSpecCode")
				.getResultList();
	}


	/**
	 * 貼紙記号変換マスタを検索し、変換後文字列を返します。
	 * @param bctype BC車種区分
	 * @param tlcode 仮本区分
	 * @return 検索結果データ
	 */
	public List<VConvertRuleData> selectAndReplace(String bctype, String tlcode) {
		List<VConvertRuleData> vConvertRuleDataList = selectQc(bctype, tlcode);
		for (VConvertRuleData vConvertRuleData : vConvertRuleDataList) {
			vConvertRuleData.replaceParameter();
		}
		return vConvertRuleDataList;
	}


	/**
	 * クロスチェック
	 * @param tlcode 仮本区分（スペック：貼紙記号変換マスタ）
	 * @param tlcodePda 号口フラグ（PDA：指示記号マスタ）
	 * @return チェック結果CSV
	 */
	public String crossCheckCsv(String tlcode, String tlcodePda) {
		// どの工場のクロスチェックを行うか判定
		switch (Utils.getPlant()) {
		case TUTUMI:
			this.a = new Tutumi();
			break;
		case TAKAOKA:
			this.a = new Takaoka();
			break;
		case TAHARA:
			this.a = new Tahara();
			break;
		case MOTOMACHI:
			this.a = new Motomachi();
			break;
		default:
			break;
		}

		SortedMap<String, Rec> totalMap = crossCheck(tlcode, tlcodePda);

		StringBuilder csv = new StringBuilder();
		// ﾁｪｯｸ実施日時
		csv.append("ﾁｪｯｸ実施日時," + DateFormatUtils.format(new Date(), "yyyy/M/d H:mm:ss"));
		csv.append("\r\n");

		// ｽﾍﾟｯｸｼｰﾄ ﾏｽﾀ種類
		csv.append("ｽﾍﾟｯｸｼｰﾄ ﾏｽﾀ種類," + (tlcode.equals("L") ? "本番" : "仮" + tlcode));
		csv.append("\r\n");

		// PDA ﾏｽﾀ種類
		// 2016/02/24 DA upd start
//		csv.append("PDA ﾏｽﾀ種類," + (tlcodePda.equals("1") ? "本番" : "仮"));
		String selectMst_Label = MstSelectService.getMasterName(Integer.valueOf(tlcodePda));
		selectMst_Label =  Normalizer.normalize(selectMst_Label, Normalizer.Form.NFKC);
		csv.append("PDA ﾏｽﾀ種類," + selectMst_Label);
		// 2016/02/24 DA upd end
		csv.append("\r\n");

		// タイトル行
		csv.append("車種:MsgNo:記号,ｽﾍﾟｯｸ打出し,PDA打出し,打出し一致,備考");

		for (Entry<String, Rec> entry : totalMap.entrySet()) {
			csv.append("\r\n");
			// キー
			csv.append(entry.getKey());
			csv.append(",");
			// スペック項目名
			VConvertRuleData vConvertRuleData = entry.getValue().vConvertRuleData;
			String letterSpecContent = vConvertRuleData != null ? vConvertRuleData.letterSpecContent2 : "";
			csv.append('"' + letterSpecContent + '"');
			csv.append(",");

			int i = 0;
			for (MBcsignDTO mBcsign : entry.getValue().mBcsignList) {
				if (i > 0) {
					csv.append("\r\n");
					csv.append(",,");
				}
				String signContents = mBcsign != null ? a.getSignContents(mBcsign) : "";
				csv.append('"' + signContents + '"');
				csv.append(",");
				csv.append(a.equalsSignContents(letterSpecContent, signContents) ? "○" : "×");
				csv.append(",");
				String notes = mBcsign != null && mBcsign.notes != null ? mBcsign.notes : "";
				csv.append('"' + notes + '"');

				i++;
			}

			// mBcsignListが0件だったときは、打出し一致＝×
			if (i == 0) {
				csv.append(",");
				csv.append("×");
			}
		}

		return csv.toString();
	}

	/**
	 * クロスチェック
	 * @param tlcode 仮本区分（スペック：貼紙記号変換マスタ）
	 * @param sopFlag 号口フラグ（PDA：指示記号マスタ）
	 * @return クロスチェック結果
	 */
	public SortedMap<String, Rec> crossCheck(String tlcode, String sopFlag) {
		SortedMap<String, Rec> totalMap = new TreeMap<String, Rec>();

		List<MVehicle> vehicles = mVehicleService.getMVehicle();
		for (MVehicle mVehicle : vehicles) {
			SortedMap<String, Rec> map = crossCheck(mVehicle.bctype, tlcode, sopFlag);
			totalMap.putAll(map);
		}

		return totalMap;
	}



	/**
	 * クロスチェック
	 * @param bctype BC車種区分
	 * @param tlcode 仮本区分（スペック：貼紙記号変換マスタ）
	 * @param sopFlag 号口フラグ（PDA：指示記号マスタ）
	 * @return クロスチェック結果
	 */
	public SortedMap<String, Rec> crossCheck(String bctype, String tlcode, String sopFlag) {

		SortedMap<String, Rec> map = new TreeMap<String, Rec>();

		// 貼紙記号変換マスタを検索
		List<VConvertRuleData> vConvertRuleDataList = selectAndReplace(bctype, tlcode);

		// 検査項目マスタを検索。
		// 貼紙記号変換マスタには不要なMsgNoが大量に含まれているため、
		// PDAで使用するMsgNoだけに絞る
		List<MItem> items = mItemService.getMItems(Integer.parseInt(sopFlag), null, bctype, MItemService.ALL_GROUPS);
		Set<String> msgs = new HashSet<String>();
		for (MItem item : items) {
			msgs.add(item.msgNo);
		}

		for (VConvertRuleData vConvertRuleData : vConvertRuleDataList) {
			// 不要なMsgNoのときはスキップする
			if (!msgs.contains(vConvertRuleData.msgno)) {
				continue;
			}

			String key = vConvertRuleData.bcType + ":" + vConvertRuleData.msgno + ":" + StringUtils.trim(vConvertRuleData.broadcastSpecCode);
			Rec val = new Rec();
			val.vConvertRuleData = vConvertRuleData;
			map.put(key, val);
		}

		// 指示記号マスタを検索
		List<MBcsignDTO> bcsigns = mBcsignService.getMBcsignDTO(Integer.parseInt(sopFlag), null, bctype, MBcsignService.ALL_GROUPS, null, null);
		for (MBcsignDTO bcsign : bcsigns) {
			String key = bctype + ":" + bcsign.msgNo + ":" + StringUtils.trim(bcsign.bcSign);
			Rec val;
			if (map.containsKey(key)) {
				val = map.get(key);
			} else {
				val = new Rec();
				map.put(key, val);
			}
			val.mBcsignList.add(bcsign);
		}

		return map;
	}


	/**
	 * クロスチェックする貼紙記号変換マスタ, 指示記号マスタの情報を格納するためのクラス
	 *
	 */
	class Rec {
		/**
		 * スペック：貼紙記号変換マスタのレコード
		 */
		VConvertRuleData vConvertRuleData;

		/**
		 * PDA：指示記号マスタのレコード
		 */
		List<MBcsignDTO> mBcsignList = new ArrayList<MBcsignDTO>();;
	}


	/**
	 * 工場ごとの違いを吸収するためのクラス
	 */
	abstract class Abst {
		// 2016/02/24 DA upd start
		///**
		// * BCフォーマットマスタ検索時のuserGroupCode
		// */
		//public String[] userGroupCode;
		/**
		 * BCフォーマットマスタ検索時のWHERE句
		 * @param bctype BC車種区分
		 * @param tlcode 仮本区分
		 * @return 検索条件のWhereオブジェクト
		 */
		public Where selectQcWhere(String bctype, String tlcode) {

			String userGroupCodeData = (String) SingletonS2ContainerFactory.getContainer().getComponent("userGroupCode");
			ComplexWhere userGroupCodeWhere = createWhere("userGroupCode", userGroupCodeData);

			Where where = new ComplexWhere()
					.eq("bctype", bctype)
					.eq("testLive", tlcode)
					.and(userGroupCodeWhere);

			return where;
		}

		/**
		 * 条件文の生成
		 * @param propertyName 項目名
		 * @param data 条件の値。カンマ区切り。
		 * @return 条件文
		 */
		private ComplexWhere createWhere(String propertyName, String data)
		{
			ComplexWhere where = new ComplexWhere();
			String[] strSplit = StringUtils.split(data, ",");
			ArrayList<String> fieldnameList = new ArrayList<String>();

			for (int i = 0; i < strSplit.length; i++) {
				if (strSplit[i].indexOf("%") != -1 || strSplit[i].indexOf("_") != -1) {
					where.or().like(propertyName, strSplit[i]);
				}
				else {
					fieldnameList.add(strSplit[i]);
				}
			}
			if (fieldnameList.size() == 1) {
				where.or().eq(propertyName, fieldnameList.get(0));
			}
			else if (fieldnameList.size() > 1) {
				where.or().in(propertyName, fieldnameList);
			}
			return where;
		}
		// 2016/02/24 DA upd end


		/**
		 * PDA指示記号マスタの検査内容を取得
		 * @param mBcsign 指示記号マスタレコード
		 * @return 検査内容
		 */
		public String getSignContents(MBcsignDTO mBcsign) {
			return mBcsign.signContents;
		}


		/**
		 * 検査内容が一致しているかを返す
		 * @param letterSpecContent スペックシートの検査内容
		 * @param signContents PDAの検査内容
		 * @return true:検査内容が一致、false:検査内容が異なる
		 */
		public boolean equalsSignContents(String letterSpecContent, String signContents) {
			return Utils.trimEqual(letterSpecContent, signContents);
		}
	}


	/**
	 * 堤
	 */
	class Tutumi extends Abst {
		/**
		 * コンストラクタ
		 */
		Tutumi() {
			// 2016/02/24 DA del start
			//userGroupCode = new String[]{"RCR"};
			// 2016/02/24 DA del end
		}

// 2019/07/05 DEL
//		@Override
//		public String getSignContents(MBcsignDTO mBcsign) {
//			return mBcsign.itemName + "          " + mBcsign.signContents;
//		}
//
//
//		@Override
//		public boolean equalsSignContents(String letterSpecContent, String signContents) {
//			String letterSpecContentWoSpaces = StringUtils.replaceChars(letterSpecContent, " 　", "");
//			String signContentsWoSpaces = StringUtils.replaceChars(signContents, " 　", "");
//			return letterSpecContentWoSpaces.equals(signContentsWoSpaces);
//		}
// 2019/07/05 DEL
	}


	/**
	 * 高岡
	 */
	class Takaoka extends Abst {
		/**
		 * コンストラクタ
		 */
		Takaoka() {
			// 2016/02/24 DA del start
			//userGroupCode = new String[]{"QC0"};
			// 2016/02/24 DA del end
		}
	}


	/**
	 * 元町
	 */
	class Motomachi extends Abst {
		/**
		 * コンストラクタ
		 */
		Motomachi() {
			// 2016/02/24 DA del start
			//userGroupCode = new String[]{"1QC"};
			// 2016/02/24 DA del end
		}
	}


	/**
	 * 田原
	 */
	class Tahara extends Abst {
		/**
		 * コンストラクタ
		 */
		Tahara() {
			// 2016/02/24 DA del start
			//userGroupCode = new String[]{"QI1", "QI5"};
			// 2016/02/24 DA del end
		}

//2018/10/12 TMC DEL START 田原のクロスチェック変更
//		@Override
//		public String getSignContents(MBcsignDTO mBcsign) {
//			return mBcsign.itemName + "          " + mBcsign.signContents;
//		}
//
//
//		@Override
//		public boolean equalsSignContents(String letterSpecContent, String signContents) {
//			String letterSpecContentWoSpaces = StringUtils.replaceChars(letterSpecContent, " 　", "");
//			String signContentsWoSpaces = StringUtils.replaceChars(signContents, " 　", "");
//			return letterSpecContentWoSpaces.equals(signContentsWoSpaces);
//		}
//2018/10/12 TMC DEL END
	}
}
