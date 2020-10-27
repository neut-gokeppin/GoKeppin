/**
 *
 */
package jp.co.ctc.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import jp.co.ctc.dto.LgMBcsignDTO;
import jp.co.ctc.dto.MBcsignDTO;
import jp.co.ctc.entity.FBcdata;
import jp.co.ctc.entity.LgMBcsign;
import jp.co.ctc.entity.LgMPart;
import jp.co.ctc.entity.MBcsign;
import jp.co.ctc.entity.MItem;
import jp.co.ctc.entity.TMsgno;
import jp.co.ctc.service.LgMBcsignService;
import jp.co.ctc.service.MBcsignService;

import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.convention.impl.PersistenceConventionImpl;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.FieldUtil;
import org.seasar.framework.util.MethodUtil;
import org.seasar.framework.util.tiger.ReflectionUtil;


/**
 * サーブレットで使用する共通クラス
 * @author CJ01786
 *
 */
public final class ServletUtil {

	/**
	 * 排他情報テーブルの種類
	 * @author CJ01786
	 *
	 */
	public enum Attribute { inspection, logistics }

	/**
	 * 状態の列挙体（ボデー・グループ・SPS台車など）
	 *
	 * @author CJ01786
	 *
	 */
	public enum State { noCheck, reCheck, checking, yetCheck, compleat };

	/**
	 * PDAから受け取るid値の列挙体。
	 *
	 * @author CJ01786
	 */
	// 2020/08/19 NEUT Start
	public enum IdList { novalue, other, body, group, sps, upload, upload_assembly, upload_logistics, supplier, userId, EditDate , TireMaker};
	// public enum IdList { novalue, other, body, group, sps, upload, upload_assembly, upload_logistics, supplier, userId, EditDate, TireMaker  };
	// 2020/08/19 NEUT End
	/**
	 * ロック状態の列挙体
	 * @author CJ01786
	 *
	 */
	public enum LockState { nothing, userLock, anotherLock }

	/**
	 * ハンドル区分の名称
	 */
	public static final String[] NAME_HANDLE = {"", "左", "右"};

	/**
	 * アプリケーション更新日時比較時の許容誤差時間
	 */
	public static final int RANGE_EDITDATE = 2000;

	/**
	 * 使用するマスターの種類 0:仮マスタ 1:本番マスタ
	 */
	public static final Integer SELECT_MST = 1;

	/**
	 * 使用する文字コード
	 */
	public static final String ENCODING = "UTF-8";

	/**
	 * ストリームから1度に読み込むバイト数
	 */
	protected static final int READ_BYTES = 128;

	/**
	 * OKと返す場合のレスポンス
	 */
	public static final String RESPONSE_OK = "OK";

    /**
     * 処理エラー時などにNGとして返すレスポンス値
     */
	public static final String RESPONSE_NG = "NG";

	/**
	 * 自身がロックしているという状態のレスポンス
	 */
	public static final String RESPONSE_LOCK = "LOCK";

	/**
	 * パラメータ名：id
	 */
	public static final String PARAMETER_ID = "id";

	/**
	 * パラメータ名:EditDate
	 */
	public static final String PARAMETER_EDITDATE = "EditDate";

	/**
	 * パラメータ名：group
	 */
	public static final String PARAMETER_GROUP = "group";

	/**
	 * パラメータ名：sps
	 */
	public static final String PARAMETER_SPS = "sps";

	/**
	 * 時間を文字列表記する時のフォーマット
	 */
	public static final String FORMAT_TIME = "yyyy/MM/dd HH:mm:ss";

	/**
	 * 改行コード
	 */
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");

	/**
	 * デフォルトコンストラクタ。
	 * インスタンス化禁止のためprivate宣言
	 */
	private ServletUtil() {
    }

	/**
	 * クライアントからIDパラメータとして送られてきた値を
	 * IdList列挙体に変換する。
	 *
	 * 値が設定されていない場合はnovalue
	 * 列挙に該当しない値の場合はotherが設定される。
	 * @param id クライアントから受け取ったIDパラメータ
	 * @return IdList列挙体
	 */
	public static IdList parseIdList(String id) {
		//受け取ったidパラメータの値からparamIdの値を設定する。
		IdList paramId;
		try {
			if (id == null || id.isEmpty()) {
				//IDパラメータなしは接続確認として使用する
				paramId = IdList.novalue;
			} else {
				paramId = IdList.valueOf(id);
			}
		} catch (IllegalArgumentException e) {
			//どれにも一致しないIDはNGとして返すためその他扱い
			paramId = IdList.other;
		}
		return paramId;
	}

	/**
	 * 時間の文字列からTimestamp型のインスタンスを生成する。
	 * 時間のフォーマットはServletUtil.FORMAT_TIMEに準ずる。
	 * @param timeStr 時間を示す文字列
	 * @return Timestamp型のインスタンス
	 */
	public static Timestamp parseTimestamp(String timeStr) {
		SimpleDateFormat df = new SimpleDateFormat(ServletUtil.FORMAT_TIME);

		if (timeStr == null) {
			return null;
		}

		try {
			long time = df.parse(timeStr).getTime();
			return new Timestamp(time);
		} catch (ParseException e) {
			return null;
		}
	}

    /**
     * InputStreamから格納された文字列を取得します。
     * @param inputStream 入力値となるInputStream
     * @return 取得した文字列。
     */
    public static String inputStreamToString(InputStream inputStream) {
    	try {
			byte[] b = new byte[READ_BYTES];
			byte[] c = new byte[READ_BYTES];
			int len = inputStream.read(b);
			StringBuffer strBuf = new StringBuffer();
			while (len > 0) {
				byte[] s = new byte[len];
				ByteArrayInputStream bs = new ByteArrayInputStream(b);
				bs.read(s);
				strBuf.append(new String(s));
				b = c.clone();
				len = inputStream.read(b);
			}
			return strBuf.toString();
    	} catch (Exception e) {
    		return null;
    	}
    }

	/**
	 * BCデータにひもづくSF部品テーブルから部品セクションを取得します。
	 * @param bcdata BCデータ
	 * @param tblName 取得先テーブル名
	 * @param colmunName 取得先列名
	 * @return 部品セクション
	 */
    public static String getTSfbcValue(FBcdata bcdata, String tblName, String colmunName) {
    	PersistenceConventionImpl persistenceConvention = new PersistenceConventionImpl();

		//テーブル名から値の取得先を決定し、値を取得したエンティティを取得。
		//エンティティのもととなるクラスをClass型のclazzに
		//エンティティをObject型のsfbcに格納する。
		String tbl = tblName.trim().toLowerCase();
		String col = colmunName.trim().toLowerCase();
		String entityClassName = persistenceConvention.fromTableNameToEntityName(tbl);		//テーブル名からクラス名を取得
		String entityFieldName = persistenceConvention.fromColumnNameToPropertyName("t" + tbl);	//テーブル名からフィールド名を取得
		entityFieldName = entityFieldName.substring(1);		//命名規則の調整のため、頭に「t」をつけたので、それをはずす
		String dataFieldName = persistenceConvention.fromColumnNameToPropertyName(col);		//列名からフィールド名を取得
		Field field;

		String entityPkg = "jp.co.ctc.entity";

		// クラスを取得
		Class<?> clazz = ClassUtil.forName(entityPkg + "." + entityClassName);

		// BCデータからエンティティを取得
		field = ClassUtil.getField(FBcdata.class, entityFieldName);
		Object entity = FieldUtil.get(field, bcdata);

		if (entity == null) {
			//エンティティが存在しない場合nullを返す。
			return null;
		} else {
			// エンティティのフィールドから値を取得する。
			field = ClassUtil.getField(clazz, dataFieldName);
			return FieldUtil.getString(field, entity);
		}
    }

	/**
	 * 指示記号マスタを取得する。
	 * @param type サーブレットの処理種別
	 * @param bcdata BCデータ
	 * @param item 対象とする項目（部品）のエンティティ
	 * @return 取得した指示記号マスタ
	 * @throws Exception Attributeによる例外
	 */
	public static Object getMsgSign(Attribute type, FBcdata bcdata, Object item) throws Exception {
		Class<?> itemEntityClass;	//項目（部品）のエンティティクラス
		Class<?> bcsignEntityClass;	//項目（部品）のエンティティクラス
		Class<?> bcsignDTOEntityClass;	//項目（部品）のDTOエンティティクラス
		Class<?> bcsignServiceClass;	//項目（部品）のサービスクラス
		String mstVerFieldName = "";	//BCデータにおいてマスタバージョンが設定されているフィールド名
		String getBcsigndtoMethodName = "";	//キーから指示マスタと項目（部品）マスタを結合したエンティティを取得するメソッド名
		String itemCodeFieldName = "";		//項目（部品）のキーとなるコードのフィールド名
		Integer itemCode;				//取得した項目（部品）のキーとなるコード
		Object bcSign = null;				//取得した指示記号のインスタンス
		Method method;				//リフレクションで使用するメソッド
		Field field;				//リフレクションで使用するフィールド
		switch (type) {
		case inspection:
			itemEntityClass = MItem.class;
			bcsignEntityClass = MBcsign.class;
			bcsignDTOEntityClass = MBcsignDTO.class;
			bcsignServiceClass = MBcsignService.class;
			mstVerFieldName = "mstVer";
			getBcsigndtoMethodName = "getMBcsignDTOByCode";
			itemCodeFieldName = "itemCode";
			break;

		case logistics:
			itemEntityClass = LgMPart.class;
			bcsignEntityClass = LgMBcsign.class;
			bcsignDTOEntityClass = LgMBcsignDTO.class;
			bcsignServiceClass = LgMBcsignService.class;
			mstVerFieldName = "lgmstVer";
			getBcsigndtoMethodName = "getLgMBcsignDTOByCode";
			itemCodeFieldName = "partCode";
			break;

		default:
			throw new Exception("Attributeが不正です。");
		}

		//サービスインスタンスの生成
		Object srvBcsign = SingletonS2Container.getComponent(bcsignServiceClass);

		//マスタバージョンを取得する。
		field = ClassUtil.getField(FBcdata.class, mstVerFieldName);
		int mstVer = Integer.parseInt(FieldUtil.get(field, bcdata).toString());

//		List<TMsgno> resMsgno;

		//項目（部品）コードを取得する
		field = ClassUtil.getField(itemEntityClass, itemCodeFieldName);
		itemCode = Integer.parseInt(FieldUtil.get(field, item).toString());

		//メッセージ区分を取得する。
		field = ClassUtil.getField(itemEntityClass, "msgDiv");
		boolean msgDiv = Boolean.parseBoolean(FieldUtil.get(field, item).toString());

		if (msgDiv) {
			//メッセージ区分がtrue（指示記号を使用する）の場合、指示記号を取得し、指示記号マスタの該当行を返す。

			field = ClassUtil.getField(itemEntityClass, "tMsgno");
			TMsgno tMsgno = (TMsgno) FieldUtil.get(field, item);
			//TMsgno tMsgno;
			//if (resMsgno != null && resMsgno.size() > 0) {
			if (tMsgno != null) {
			//	tMsgno = resMsgno.get(0);

				//取得したテーブルに対し、アイデントNoを指定する。
				//取得した行の、該当列の値を取得。
				String msgString =
					ServletUtil.getTSfbcValue(bcdata, tMsgno.tblname.trim(), tMsgno.colname.trim());
				if (msgString != null) {

					//開始位置と桁数を取得
					field = ClassUtil.getField(itemEntityClass, "bcPosition");
					int bcPosition = Integer.parseInt(FieldUtil.get(field, item).toString());
					field = ClassUtil.getField(itemEntityClass, "bcLength");
					int bcLength = Integer.parseInt(FieldUtil.get(field, item).toString());

					//取得した値の「位置」から「桁数」分の値を得る。
					String msgSign = msgString.substring(bcPosition - 1, bcLength);

					field = ClassUtil.getField(itemEntityClass, "mBcsignList");
					List<?> mBcsignList = (List<?>) FieldUtil.get(field, item);
					for (Object mBcsign : mBcsignList) {
						field = ClassUtil.getField(bcsignEntityClass, "bcSign");
						String sign = FieldUtil.get(field, mBcsign).toString();
						if (sign.trim().equals(msgSign.trim())) {
							Class<?>[] setClass = { bcsignEntityClass };
							method = ClassUtil.getMethod(bcsignDTOEntityClass, "parse", setClass);
							Object[] inputItem = { bcsignEntityClass.cast(mBcsign) };
							bcSign = ReflectionUtil.invokeStatic(method, inputItem);
							break;
						}
					}

				} else {
					bcSign = null;
				}
			} else {
				bcSign = null;
			}
		} else {
			//false（指示記号を使用しない）の場合、指示記号マスタの先頭行を返す。
			method = ClassUtil.getMethod(bcsignServiceClass, getBcsigndtoMethodName,
					new Class<?>[] { Integer.class, Integer.class, Integer.class });
			bcSign = MethodUtil.invoke(method, srvBcsign, new Object[] { 1, mstVer, itemCode });
		}

		if (bcSign == null) {
			//記号が設定されなかった場合、項目（部品）を設定して返す。
			Class<?>[] setClass = { itemEntityClass };
			method = ClassUtil.getMethod(bcsignDTOEntityClass, "parse", setClass);
			Object[] inputItem = { itemEntityClass.cast(item) };
			bcSign = ReflectionUtil.invokeStatic(method, inputItem);
		}
		return bcSign;
	}

}
