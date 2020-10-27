/**
 *
 */
package jp.co.ctc.service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.seasar.extension.jdbc.Where;
import org.seasar.extension.jdbc.where.ComplexWhere;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

import jp.co.ctc.entity.MOrder;
import jp.co.ctc.entity.MVehicle;
import jp.co.ctc.entity.VBcfmt;
import jp.co.ctc.util.Utils;

/**
 * G-ALCのBCフォーマットマスタを扱うサービス
 * @author CJ01615
 *
 */
public class VBcfmtService extends GalcAbstractService<VBcfmt> {

	/**
	 * MOrderService
	 */
	@Resource
	public MOrderService mOrderService;

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
	 * BCフォーマットマスタを検索します。
	 * @param bctype BC車種区分
	 * @param tlcode 仮本区分（スペック：BCフォーマットマスタ）
	 * @return 検索結果データ
	 */
	public List<VBcfmt> selectQc(String bctype, String tlcode) {
		return select()
				.where(a.selectQcWhere(bctype, tlcode))
				.orderBy("bctype, groupno, fieldname")
				.getResultList();
	}


	/**
	 * 検査項目クロスチェック
	 * @param tlcode 仮本区分（スペック：BCフォーマットマスタ）
	 * @param tlcodePda 号口フラグ（PDA：検査項目マスタ）
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

		SortedMap<String, Rec> map = crossCheck(tlcode, tlcodePda);
		return mapToCsv(tlcode, tlcodePda, map);
	}


	/**
	 * 検査項目クロスチェック
	 * @param tlcode 仮本区分（スペック：BCフォーマットマスタ）
	 * @param sopFlag 号口フラグ（PDA：検査項目マスタ）
	 * @return チェック結果
	 */
	public SortedMap<String, Rec> crossCheck(String tlcode, String sopFlag) {
		SortedMap<String, Rec> total = new TreeMap<String, Rec>();

		List<MVehicle> vehicles = mVehicleService.getMVehicle();
		for (MVehicle mVehicle : vehicles) {
			SortedMap<String, Rec> map = crossCheckVehicle(mVehicle.bctype, tlcode, sopFlag);
			total.putAll(map);
		}

		return total;
	}


	/**
	 * 検査項目クロスチェック
	 * @param bctype BC車種区分
	 * @param tlcode 仮本区分（スペック：BCフォーマットマスタ）
	 * @param sopFlag 号口フラグ（PDA：検査項目マスタ）
	 * @return チェック結果
	 */
	public SortedMap<String, Rec> crossCheckVehicle(String bctype, String tlcode, String sopFlag) {
		// BCフォーマットマスタ取得
		Map<String, SortedMap<String, Rec>> vBcfmtMapMap = getVBcfmtMap(bctype, tlcode);

		// チェック結果格納用
		SortedMap<String, Rec> vBcfmtMapAll = new TreeMap<String, VBcfmtService.Rec>();

		for (Entry<String, SortedMap<String, Rec>> entry : vBcfmtMapMap.entrySet()) {
			String group = entry.getKey();
			SortedMap<String, Rec> vBcfmtMap = entry.getValue();

			// vBcfmtMapにエントリが無いときはクロスチェックしない
			if (vBcfmtMap.isEmpty()) {
				continue;
			}

			// ハンドルごとにクロスチェックする
			crossCheckVehicleSub(bctype, sopFlag, group, vBcfmtMap);

			vBcfmtMapAll.putAll(vBcfmtMap);
		}

		return vBcfmtMapAll;
	}


	/**
	 * 検査項目クロスチェック
	 * @param bctype BC車種区分
	 * @param sopFlag 号口フラグ（PDA：検査項目マスタ）
	 * @param group ハンドル ・・・ "L"：左ハンドル、"R"：右ハンドル
	 * @param vBcfmtMap チェック結果
	 */
	private void crossCheckVehicleSub(String bctype, String sopFlag,
			String group, SortedMap<String, Rec> vBcfmtMap) {
		// 検査順リストを取得
		List<MOrder> orders = mOrderService.getMOrders(
				Integer.parseInt(sopFlag), null, bctype, null,
				group.equals("L") ? "1" : "2");

		MOrder prevOrder = null;

		for (Entry<String, Rec> entry : vBcfmtMap.entrySet()) {

			VBcfmt vBcfmtMsgno = entry.getValue().vBcfmtMsgno;
			Rec rec = entry.getValue();

			for (MOrder order : orders) {
				// リストに1項目でも追加された後、MsgNoが変わっていたらbreakする。
				// 同じMsgNoが連続した場合、分割項目とみなしてリスト追加するため。
				if (vBcfmtMsgno.msgno.equals(order.mItem.msgNo)) {
					// 同じMsgNoの場合、リスト追加する。
					if (order.inspecOrder == 1) {
						// 検査順が1ならリスト追加する
						rec.mOrderList.add(order);
						prevOrder = order;
					} else if (prevOrder != null && prevOrder.groupCode.equals(order.groupCode)) {
						// 前行と同じ工程ならリスト追加する
						rec.mOrderList.add(order);
						prevOrder = order;
					}
				} else if (!Utils.isEmpty(rec.mOrderList)) {
					// MsgNoが変わっていたらbreakする。
					break;
				}
			}

			orders.removeAll(rec.mOrderList);
		}

		// 検査順リストに残ったデータをチェック結果に格納
		int i = 0;
		for (MOrder order : orders) {
			String key = order.mGroup.line + ":" + bctype + ":_:" + group + ":" + String.format("_%1$05d", i++);
			Rec val = new Rec();
			val.mOrderList.add(order);
			vBcfmtMap.put(key, val);
		}
	}


	/**
	 * BCフォーマットマスタのデータをハンドルごとに分割して取得
	 * @param bctype BC車種区分
	 * @param tlcode 仮本区分（スペック：BCフォーマットマスタ）
	 * @return ハンドルごとに分割したBCフォーマットマスタ
	 */
	private Map<String, SortedMap<String, Rec>> getVBcfmtMap(String bctype, String tlcode) {
		// G-ALCのBCﾌｫｰﾏｯﾄﾏｽﾀを検索
		List<VBcfmt> vBcfmtList = selectQc(bctype, tlcode);

		// ハンドルごとのクロスチェック結果を格納するMap
		Map<String, SortedMap<String, Rec>> vBcfmtMapMap = new HashMap<String, SortedMap<String, Rec>>();
		vBcfmtMapMap.put("L", new TreeMap<String, Rec>());
		vBcfmtMapMap.put("R", new TreeMap<String, Rec>());

		// クロスチェック結果格納用のMapにBCフォーマットデータを格納
		for (VBcfmt vBcfmt : vBcfmtList) {
			if (vBcfmt.groupno.equals("L") || vBcfmt.groupno.equals("R")) {
				setVBcfmtMap(vBcfmtMapMap, vBcfmt, vBcfmt.groupno);
			} else {
				setVBcfmtMap(vBcfmtMapMap, vBcfmt, "L");
				setVBcfmtMap(vBcfmtMapMap, vBcfmt, "R");
			}
		}

		return vBcfmtMapMap;
	}


	/**
	 * クロスチェック結果格納用のMapにBCフォーマットデータを格納
	 * @param vBcfmtMapMap ハンドル別クロスチェック結果格納用Map
	 * @param vBcfmt G-ALCのBCフォーマットマスタレコード
	 * @param groupno R:右ハンドル、L:左ハンドル
	 */
	protected void setVBcfmtMap(Map<String, SortedMap<String, Rec>> vBcfmtMapMap,
			VBcfmt vBcfmt, String groupno) {
		SortedMap<String, Rec> vBcfmtMap = vBcfmtMapMap.get(groupno);

		String fieldname = a.getFieldName(vBcfmt.fieldName);
		String key = vBcfmt.line + ":" + vBcfmt.bctype + ":" + vBcfmt.ltname + ":" + groupno + ":" + fieldname;
		Rec rec;
		if (vBcfmtMap.containsKey(key)) {
			rec = vBcfmtMap.get(key);
		} else {
			rec = new Rec();
		}

		a.setVBcfmtToRec(rec, vBcfmt);

		vBcfmtMap.put(key, rec);
		vBcfmtMapMap.put(groupno, vBcfmtMap);
	}


	/**
	 * MapをCSVに変換します。
	 * @param tlcode 仮本区分（スペック：BCフォーマットマスタ）
	 * @param tlcodePda 号口フラグ（PDA：検査項目マスタ）
	 * @param map 変換元のMap
	 * @return CSV形式の文字列
	 */
	private String mapToCsv(String tlcode, String tlcodePda, SortedMap<String, Rec> map) {
		StringBuilder csv = new StringBuilder();

		// ﾁｪｯｸ実施日時
		csv.append("ﾁｪｯｸ実施日時," + DateFormatUtils.format(new Date(), "yyyy/M/d H:mm:ss"));
		csv.append("\r\n");

		// ｽﾍﾟｯｸｼｰﾄ ﾏｽﾀ種類
		csv.append("ｽﾍﾟｯｸｼｰﾄ ﾏｽﾀ種類," + (tlcode.equals("L") ? "本番" : "仮" + tlcode));
		csv.append("\r\n");

		// PDA ﾏｽﾀ種類
		// 2016/02/24 DA upd start
//		csv.append("PDA ﾏｽﾀ種類," + (sopFlag.equals("1") ? "本番" : "仮"));
		String selectMst_Label = MstSelectService.getMasterName(Integer.valueOf(tlcodePda));
		selectMst_Label =  Normalizer.normalize(selectMst_Label, Normalizer.Form.NFKC);
		csv.append("PDA ﾏｽﾀ種類," + selectMst_Label);
		// 2016/02/24 DA upd end
		csv.append("\r\n");

		// タイトル行
		csv.append("ﾗｲﾝ:車種:端末:右左:ｽﾍﾟｯｸ位置,ｽﾍﾟｯｸMsgNo,ｽﾍﾟｯｸ項目名,PDA MsgNo,PDA項目名,PDA工程,PDA検査順,項目名一致,検査順ﾁｪｯｸ");

		int inspecOrderPrev = -1;
		int groupCodePrev = -1;
		String ptnDivPrev = "";
		for (Entry<String, Rec> entry : map.entrySet()) {

			csv.append("\r\n");
			// キー
			csv.append(entry.getKey() + ",");
			if (entry.getValue().vBcfmtMsgno != null) {
				// スペックMsgNo
				csv.append(entry.getValue().vBcfmtMsgno.msgno);
			}
			csv.append(",");

			String bcName = "";
			if (entry.getValue().vBcfmtName != null) {
				bcName = a.getConstantString(entry.getValue().vBcfmtName).trim();
				// スペック項目名
				csv.append('"' + bcName + '"');
			}
			csv.append(",");

			int i = 0;
			for (MOrder order : entry.getValue().mOrderList) {
				if (i > 0) {
					csv.append("\r\n");
					csv.append(",,,");
				}
				// PDA MsgNo
				csv.append(order.mItem.msgNo);
				csv.append(",");

				// PDA項目名
				csv.append('"' + order.mItem.itemName + '"');
				csv.append(",");

				// PDA工程
				csv.append(order.mGroup.groupName);
				csv.append(",");

				// PDA検査順
				csv.append(order.inspecOrder);
				csv.append(",");

				// 項目名一致
				csv.append(order.mItem.itemName.equals(bcName) ? "○" : "×");
				csv.append(",");

				// 検査順ﾁｪｯｸ
				if (groupCodePrev != order.groupCode || !ptnDivPrev.equals(order.ptnDiv)) {
					inspecOrderPrev = 0;
				}
				if (order.inspecOrder == 1) {
					csv.append("－");
				} else {
					csv.append(order.inspecOrder == inspecOrderPrev + 1 ? "○" : "×");
				}
				inspecOrderPrev = order.inspecOrder;
				groupCodePrev = order.groupCode;
				ptnDivPrev = order.ptnDiv;

				i++;
			}
		}

		csv.append("\r\n");

		return csv.toString();
	}


	/**
	 * クロスチェックするMsgNo, 項目等の情報を格納するためのクラス
	 * @author Z087567
	 *
	 */
	class Rec {
		/**
		 * スペック：BCフォーマットマスタのレコード。MsgNo取得用
		 */
		VBcfmt vBcfmtMsgno;

		/**
		 * スペック：BCフォーマットマスタのレコード。項目名用
		 */
		VBcfmt vBcfmtName;

		/**
		 * PDA：検査順マスタのレコード
		 */
		List<MOrder> mOrderList = new ArrayList<MOrder>();
	}


	/**
	 * 工場ごとの違いを吸収するためのクラス
	 * @author Z127884
	 */
	abstract class Abst {
		/**
		 * BCフォーマットマスタ検索時のWHERE句
		 * @param bctype BC車種区分
		 * @param tlcode 仮本区分
		 * @return 検索条件のWhereオブジェクト
		 */
		// 2016/02/24 DA upd start
		//public abstract Where selectQcWhere(String bctype, String tlcode);
		public Where selectQcWhere(String bctype, String tlcode) {

			String ltnameData = (String) SingletonS2ContainerFactory.getContainer().getComponent("ltname");
			ComplexWhere ltnameWhere = createWhere("ltname", ltnameData);

			String fieldNameData = (String) SingletonS2ContainerFactory.getContainer().getComponent("fieldName");
			ComplexWhere fieldnameWhere = createWhere("fieldName", fieldNameData);

			Where where = new ComplexWhere()
					.eq("bctype", bctype)
					.eq("tlcode", tlcode)
					.and(ltnameWhere)
					.and(fieldnameWhere);

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
		 * フィールド名を取得
		 * @param fieldName スペックシートのフィールド名
		 * @return クロスチェック用フィールド名
		 */
		public String getFieldName(String fieldName) {

			// 2016/02/24 DA upd start
			String str = (String) SingletonS2ContainerFactory.getContainer().getComponent("useFieldName");
			String[] strSplit = StringUtils.split(str, ",");
			String data = "";

			for (int i = 0; i < strSplit.length; i++) {
				// 開始位置
				int idxStart = NumberUtils.toInt(strSplit[i].trim());
				if (idxStart == 0) {
					break;
				}
				idxStart = idxStart - 1;

				// 終了位置
				i++;
				if (i >= strSplit.length) {
					break;
				}
				int idxEnd = NumberUtils.toInt(strSplit[i].trim());
				if (idxEnd == 0) {
					idxEnd = fieldName.length();
				}
				idxEnd = idxStart + idxEnd;

				data = data + StringUtils.substring(fieldName, idxStart, idxEnd);
			}

			return data;
			//return fieldName.substring(0, 2) + fieldName.substring(3, 6);
			// 2016/02/24 DA upd end
		}

		/**
		 * RecにMsgNo, 項目等の情報を格納
		 * @param rec 対象のRec
		 * @param vBcfmt 格納されるBCフォーマットマスタ情報
		 */
		public void setVBcfmtToRec(Rec rec, VBcfmt vBcfmt) {

			// 2016/02/24 DA upd start
			Integer line = (Integer) SingletonS2ContainerFactory.getContainer().getComponent("numberOfLines");
			if (line == 1) {
				rec.vBcfmtMsgno = vBcfmt;
				rec.vBcfmtName = vBcfmt;
			}
			else {
				if (vBcfmt.fieldName.charAt(2) == 'A') {
					rec.vBcfmtMsgno = vBcfmt;
				}
				else if (vBcfmt.fieldName.charAt(2) == 'Z') {
					rec.vBcfmtName = vBcfmt;
				}
			}

			//if (vBcfmt.fieldName.charAt(2) == 'A') {
			//	rec.vBcfmtMsgno = vBcfmt;
			//} else if (vBcfmt.fieldName.charAt(2) == 'Z') {
			//	rec.vBcfmtName = vBcfmt;
			//}
			// 2016/02/24 DA upd end
		}


		/**
		 * スペックシート項目名を返します。
		 * @param f 対象のVBcfmtオブジェクト
		 * @return スペックシート項目名
		 */
		public String getConstantString(VBcfmt f) {
			return f.constantString;
		}
	}


	/**
	 * 堤
	 * @author Z127884
	 */
	class Tutumi extends Abst {
// 2016/02/24 DA del start
//		@Override
//		public Where selectQcWhere(String bctype, String tlcode) {
//			return new ComplexWhere()
//					.eq("bctype", bctype)
//					.in("ltname", "LIQ63L", "LIQ64L", "LIQ83L", "LIQ84L")
//					.eq("tlcode", tlcode)
//					.and(new ComplexWhere()
//							.like("fieldname", "S1A%").or()
//							.like("fieldname", "S1B%").or()
//							.like("fieldname", "S1C%").or()
//							.like("fieldname", "S2A%").or()
//							.like("fieldname", "S2B%").or()
//							.like("fieldname", "S2C%"));
//		}

//		@Override
//		public String getFieldName(String fieldName) {
//			return fieldName;
//		}

//		@Override
//		public void setVBcfmtToRec(Rec rec, VBcfmt vBcfmt) {
//			rec.vBcfmtMsgno = vBcfmt;
//			rec.vBcfmtName = vBcfmt;
//		}
// 2016/02/24 DA del end

		@Override
		public String getConstantString(VBcfmt f) {
			return f.comments;
		}
	}


	/**
	 * 高岡
	 * @author Z127884
	 */
	class Takaoka extends Abst {
// 2016/02/24 DA del start
//		@Override
//		public Where selectQcWhere(String bctype, String tlcode) {
//			return new ComplexWhere()
//					.eq("bctype", bctype)
//					.like("ltname", "LIQ_A0")
//					.eq("tlcode", tlcode)
//					.and(new ComplexWhere()
//							.like("fieldname", "S1A%").or()
//							.like("fieldname", "S1Z%").or()
//							.like("fieldname", "S2A%").or()
//							.like("fieldname", "S2Z%"));
//		}
// 2016/02/24 DA del end
	}

	/**
	 * 元町
	 * @author Z127884
	 */
	class Motomachi extends Abst {
// 2016/02/24 DA del start
//		@Override
//		public Where selectQcWhere(String bctype, String tlcode) {
//			return new ComplexWhere()
//					.eq("bctype", bctype)
//					.eq("ltname", "LIQ100")
//					.eq("tlcode", tlcode)
//					.and(new ComplexWhere()
//							.like("fieldname", "S1A___").or()
//							.like("fieldname", "S1Z___").or()
//							.like("fieldname", "S2A___").or()
//							.like("fieldname", "S2Z___"));
//		}
// 2016/02/24 DA del end
	}


	/**
	 * 田原
	 * @author Z127884
	 */
	class Tahara extends Abst {
// 2016/02/24 DA del start
//		@Override
//		public Where selectQcWhere(String bctype, String tlcode) {
//			return new ComplexWhere()
//					.eq("bctype", bctype)
//					.in("ltname", "LI1Q1L", "LI1Q2L", "LI1Q3L", "LI1Q4L",
//							"LI1QBL", "LI5Q7L", "LI5Q8L", "LI5Q9L",
//							"LI5QAL", "LI5QBL", "LI5QCL", "LI5QDL")
//					.eq("tlcode", tlcode)
//					.and(new ComplexWhere()
//							.like("fieldname", "S1A%").or()
//							.like("fieldname", "S2A%"));
//		}

//		@Override
//		public String getFieldName(String fieldName) {
//			return fieldName;
//		}

//		@Override
//		public void setVBcfmtToRec(Rec rec, VBcfmt vBcfmt) {
//			rec.vBcfmtMsgno = vBcfmt;
//			rec.vBcfmtName = vBcfmt;
//		}
// 2016/02/24 DA del end

// 2018/10/05 TMC del start 田原クロスチェック定義変更対応
//		@Override
//		public String getConstantString(VBcfmt f) {
//			return f.comments;
//		}
// 2018/10/05 TMC del end
	}
}
