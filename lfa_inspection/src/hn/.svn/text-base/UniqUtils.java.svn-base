package jp.co.ctc.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import jp.co.ctc.entity.MItem;
import jp.co.ctc.entity.MOrder;
import jp.co.ctc.entity.MVehicle;
import jp.co.ctc.service.MItemService;
import jp.co.ctc.service.MVehicleService;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.seasar.extension.jdbc.where.SimpleWhere;
import org.seasar.framework.log.Logger;

/**
 * 個別処理を実装するクラス　日野用.
 *
 * @author DA
 *
 */
@SuppressWarnings("unused")
public class UniqUtils {

	/**
	 * ログ出力用
	 */
	private Logger logger = Logger.getLogger(this.getClass());

	/**
	 * 次処理車両取得の追加条件を返す
	 * @param sWhere 条件
	 * @return 追加条件
	 */
	public SimpleWhere getNextFBcdataWithResultsumOption(SimpleWhere sWhere) {

		sWhere.notLike("ctrlKata", "________R%");
		logger.debug("条件:" + sWhere.toString());
		return sWhere;
	}

	//=========================================================================
	//クロスチェック関係
	//=========================================================================

	/** HN用の識別コード */
	public static final int CROSSCHECK_FLG = 2;

	/** HN用のJSPファイル */
	public static final String CROSSCHECK_JSP = "index_hn.jsp";

	/**
	 * 識別コードを取得する
	 * @return 識別コード
	 */
	public static int getFlg() {
		return CROSSCHECK_FLG;
	}

	//検査項目ファイルの未使用
	private static final String SPEC_UNUSED = "未";

	//検査項目ファイルの一致
	private static final String SPEC_MATCH_OK = "○";
	private static final String SPEC_MATCH_NG = "×";

	//検査項目ファイルの開始行
	private static final int SPEC_ROW_START = 1;

	//検査項目ファイルの列位置
	private static final int SPEC_COLUMN_MSGNO = 0;
	private static final int SPEC_COLUMN_ITEM = 2;
	private static final int SPEC_COLUMN_FLG = 3;

	//項目マスタのメッセージNoのサイズ
	private static final int MITEM_MSGNO_LENGTH = 4;

	/** エラーメッセージ */
	private String errorContent = "";

	/**
	 * スペックシートのレコード
	 */
	private class SpecRecord {

		/**
		 * NO
		 */
		public Integer no;

		/**
		 * MSGNO
		 */
		public String msgNo;

		/**
		 * 項目
		 */
		public String item;

		/**
		 * 使用
		 */
		public String flg;
	}

	/**
	 * チェック結果のレコード
	 */
	private class CheckRecord implements Cloneable {

		@Override
		protected CheckRecord clone() {
			try {
				return (CheckRecord) super.clone();
			}
			catch (CloneNotSupportedException e) {
				throw new InternalError(e.toString());
			}
		}

		/**
		 * コンストラクタ
		 */
		public CheckRecord() {
			specsheet = 0;
			master = 0;
			no = 0;
			specMsgNo = "";
			specItem = "";
			pdaMsgNo = "";
			pdaItem = "";
			groupName = "";
			inspecOrder = 0;
			line = "";
			bctype = "";
			ptnDiv = "";
			renban = 0;
			renbanMasterOnly = 0;
			result = "";
		}

		/**
		 * スペックシート有無（0:スペックシートにデータあり、1:スペックシートにデータなし）
		 */
		public Integer specsheet;

		/**
		 * マスター有無（0:マスターにデータあり、1:マスターにデータなし）
		 */
		public Integer master;

		/**
		 * NO
		 */
		public Integer no;

		/**
		 * スペックシートMSGNO
		 */
		public String specMsgNo;

		/**
		 * スペックシート項目
		 */
		public String specItem;

		/**
		 * PDA MSGNO
		 */
		public String pdaMsgNo;

		/**
		 * PDA項目
		 */
		public String pdaItem;

		/**
		 * 工程
		 */
		public String groupName;

		/**
		 * 検査順
		 */
		public Integer inspecOrder;

		/**
		 * ライン
		 */
		public String line;

		/**
		 * BC車種区分コード
		 */
		public String bctype;

		/**
		 * パターン区分
		 */
		public String ptnDiv;

		/**
		 * 連番
		 */
		public Integer renban;

		/**
		 * 連番（マスタのみ）
		 */
		public Integer renbanMasterOnly;

		/**
		 * 判定結果
		 */
		public String result;
	}

	/**
	 * コンソール＆ログ出力
	 * @param data
	 */
	private void outputLog(String data) {
		try {
			//System.out.println(data);
			logger.info(data);
		}
		catch (Exception e) {
		}
	}

	/**
	 * 時間を出力
	 */
	private void outputTime(String flg) {
		try {
			String data = "";
			SimpleDateFormat fomatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.S");
			Date now = new Date(System.currentTimeMillis());
			data = "time:" + fomatter.format(now) + " " + flg;
			outputLog(data);
		}
		catch (Exception e) {
		}
	}

	/**
	 * エラーメッセージを取得する。
	 * @return エラーメッセージ。エラーがない場合はブランク。
	 */
	public String getErrorContent() {
		return errorContent;
	}

	/**
	 * クロスチェックを実行する。（項目単位）
	 * @param inputStream ファイル
	 * @param filename ファイル名
	 * @param pass パスワード
	 * @param sheetName シート名
	 * @param tlcodePda PDAマスタ種類
	 * @param mItemService 項目マスタサービス
	 * @return チェック結果データ
	 * @throws Exception
	 */
	public String crossCheckCsv(InputStream inputStream, String filename, String pass, String sheetName, String tlcodePda, MItemService mItemService) throws Exception {

		if (StringUtils.isBlank(filename)) {
			errorContent = "errors.crosscheck.nofile";
			return null;
		}

		String csv = "";
		try {
			//スペックシートを取得する
			List<SpecRecord> specList = getSpecData(inputStream, filename, pass, sheetName);
			if (specList == null) {
				return null;
			}

			//クロスチェックを実行する
			int sopFlag = NumberUtils.toInt(tlcodePda);
			List<MItem> mitemList = mItemService.getMItems(sopFlag, null, null, MItemService.ALL_GROUPS, false);

			List<CheckRecord> cheakList = crosscheck(specList, mitemList, null);

			//チェック結果をテキストに変換する
			csv = outputResultData(cheakList, tlcodePda);
			if (StringUtils.isBlank(csv)) {
				errorContent = "errors.crosscheck.noresult";
				return null;
			}
		}
		catch (Exception e) {
			logger.info(e);
			throw e;
		}

		return csv;
	}

	/**
	 * クロスチェックを実行する。
	 * @param inputStream ファイル
	 * @param filename ファイル名
	 * @param pass パスワード
	 * @param sheetName シート名
	 * @param tlcodePda PDAマスタ種類
	 * @param mVehicleService 車種マスタサービス
	 * @param mItemService 項目マスタサービス
	 * @return チェック結果データ
	 * @throws Exception
	 */
	public String crossCheckCsv(InputStream inputStream, String filename, String pass, String sheetName, String tlcodePda, MVehicleService mVehicleService, MItemService mItemService) throws Exception {

		if (StringUtils.isBlank(filename)) {
			errorContent = "errors.crosscheck.nofile";
			return null;
		}

		String csv = "";
		try {
			//スペックシートを取得する
			List<SpecRecord> specList = getSpecData(inputStream, filename, pass, sheetName);
			if (specList == null) {
				return null;
			}

			//クロスチェックを実行する
			List<CheckRecord> cheakList = new java.util.ArrayList<CheckRecord>();

			List<MVehicle> vehicles = mVehicleService.getMVehicle();
			for (MVehicle mVehicle : vehicles) {

				int sopFlag = NumberUtils.toInt(tlcodePda);
				List<MItem> mitemList = mItemService.getMItems(sopFlag, null, mVehicle.bctype, MItemService.ALL_GROUPS);

				List<CheckRecord> chkList = crosscheck(specList, mitemList, mVehicle);
				cheakList.addAll(chkList);
			}

			//チェック結果をソートする
			Collections.sort(cheakList, new Comparator<CheckRecord>()
			{
				public int compare(CheckRecord o1, CheckRecord o2)
				{
					String oo1;
					String oo2;
					int ret;

					//BC車種区分コードの昇順
					oo1 = StringUtils.defaultString(o1.bctype);
					oo2 = StringUtils.defaultString(o2.bctype);
					ret = oo1.compareTo(oo2);
					if (ret != 0) {
						return ret;
					}

					//マスター有無の昇順（マスター有→マスター無の順）
					ret = o1.master - o2.master;
					if (ret != 0) {
						return ret;
					}

					//パターン区分の昇順
					oo1 = StringUtils.defaultString(o1.ptnDiv);
					oo2 = StringUtils.defaultString(o2.ptnDiv);
					ret = oo1.compareTo(oo2);
					if (ret != 0) {
						return ret;
					}

					//スペックシート有無の昇順（スペックシート有→スペックシート無の順）
					ret = o1.specsheet - o2.specsheet;
					if (ret != 0) {
						return ret;
					}

					//スペックシートのA列順
					ret = o1.no - o2.no;
					if (ret != 0) {
						return ret;
					}

					//PDA MSGNOの昇順
					oo1 = StringUtils.defaultString(o1.pdaMsgNo);
					oo2 = StringUtils.defaultString(o2.pdaMsgNo);
					ret = oo1.compareTo(oo2);
					if (ret != 0) {
						return ret;
					}

					//PDA項目の昇順
					oo1 = StringUtils.defaultString(o1.pdaItem);
					oo2 = StringUtils.defaultString(o2.pdaItem);
					ret = oo1.compareTo(oo2);
					if (ret != 0) {
						return ret;
					}

					return 0;
				}
			});

			//チェック結果をテキストに変換する
			csv = outputResultData(cheakList, tlcodePda);
			if (StringUtils.isBlank(csv)) {
				errorContent = "errors.crosscheck.noresult";
				return null;
			}
		}
		catch (Exception e) {
			logger.info(e);
			throw e;
		}

		return csv;
	}

	/**
	 * ファイルをbyte配列に変換する
	 * @param inputStream ファイル
	 * @return
	 * @throws IOException
	 */
	private byte[] readAll(InputStream inputStream) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		while (true) {
			int len = inputStream.read(buffer);
			if (len < 0) {
				break;
			}
			baos.write(buffer, 0, len);
		}
		return baos.toByteArray();
	}

	/**
	 * セルが日付タイプか判定する
	 * @param cell
	 * @return
	 */
	private boolean isCellDateFormat(Cell cell) {

		boolean isCellDateFormat = false;

		isCellDateFormat = DateUtil.isCellDateFormatted(cell);
		if (isCellDateFormat) {
			return isCellDateFormat;
		}

		// isCellDateFormattedでは日付と判断されない場合がある場合の回避策

		int dataformat = cell.getCellStyle().getDataFormat();

		//m"月"d"日"
		if (dataformat == 56) {
			isCellDateFormat = true;
		}
		return isCellDateFormat;
	}

	/**
	 * セルの値を取得する
	 * @param cell
	 * @return
	 */
	private String getCellValue(Cell cell) {

		Object cellObject = null;
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			cellObject = cell.getRichStringCellValue();
			break;

		case Cell.CELL_TYPE_NUMERIC:
			if (isCellDateFormat(cell)) {
				cellObject = cell.getDateCellValue();
			}
			else {
				cellObject = cell.getNumericCellValue();
			}
			break;

		case Cell.CELL_TYPE_BOOLEAN:
			cellObject = cell.getBooleanCellValue();
			break;

		case Cell.CELL_TYPE_FORMULA:
			Workbook book = cell.getSheet().getWorkbook();
			CreationHelper helper = book.getCreationHelper();
			FormulaEvaluator evaluator = helper.createFormulaEvaluator();
			Cell value = evaluator.evaluateInCell(cell);
			cellObject = getCellValue(value);
			break;

		case Cell.CELL_TYPE_ERROR:
			cellObject = cell.getErrorCellValue();
			break;

		case Cell.CELL_TYPE_BLANK:
			cellObject = "";
			break;

		default:
			cellObject = "";
			break;
		}
		return cellObject.toString();
	}

	/**
	 * パスワードありExcel2007形式のファイルかどうか判定する
	 * @param inputStream ファイル
	 * @param filename ファイル名
	 * @param pass パスワード
	 * @return 処理結果（true:パスワードありExcel2007形式、false:Excel2003形式、パスワードなしExcel2007形式）
	 * @throws Exception
	 */
	private boolean isPasswordXlsxFile(InputStream inputStream, String filename, String pass) throws Exception {

		if (StringUtils.isBlank(pass)) {
			return false;
		}

		try {
			boolean isXls = false;
			boolean isXlsx = false;

			if (!inputStream.markSupported()) {
				inputStream = new PushbackInputStream(inputStream, 8);
			}
			isXls = POIFSFileSystem.hasPOIFSHeader(inputStream);

			if (!inputStream.markSupported()) {
				inputStream = new PushbackInputStream(inputStream, 8);
			}
			isXlsx = POIXMLDocument.hasOOXMLHeader(inputStream);

			if (isXls) {
				//パスワードありは、isXlsx=trueとならず isXls=trueになるため、拡張子がxlsxか判定（2014/11時点）
				boolean isFileXlsx = StringUtils.endsWith(filename, ".xlsx");
				if (isFileXlsx) {
					return true;
				}

				return false;
			}
			if (isXlsx) {
				return false;
			}
		}
		catch (Exception e) {
			logger.info(e);
			throw e;
		}
		return false;
	}

	/**
	 * スペックシートのデータを取得する
	 * @param inputStream ファイル
	 * @param filename ファイル名
	 * @param pass パスワード
	 * @param sheetName シート名
	 * @return スペックシートのデータ一覧
	 * @throws Exception
	 */
	private List<SpecRecord> getSpecData(InputStream inputStream, String filename, String pass, String sheetName) throws Exception {

		outputTime("start getSpecData");

		List<SpecRecord> dataList = new java.util.ArrayList<SpecRecord>();

		InputStream is = null;
		try {
			//FormFileをそのまま使うと、後の処理で想定通りに開けなくなるので一旦、別のInputStreamを生成する
			byte[] fileByte = readAll(inputStream);
			is = new ByteArrayInputStream(fileByte);

			Workbook wb = null;
			try {
				//パスワードありExcel2007形式対応
				boolean isPasswordXlsxFile = isPasswordXlsxFile(is, filename, pass);
				if (isPasswordXlsxFile) {
					POIFSFileSystem pfs = new POIFSFileSystem(is);
					EncryptionInfo encInfo = new EncryptionInfo(pfs);
					Decryptor decryptor = Decryptor.getInstance(encInfo);
					boolean isPassword = decryptor.verifyPassword(pass);
					if (isPassword) {
						is = decryptor.getDataStream(pfs);
					}
					else {
						errorContent = "errors.crosscheck.fileopenpass";
						return null;
					}
				}

				Biff8EncryptionKey.setCurrentUserPassword(pass);
				wb = WorkbookFactory.create(is);

			}
			catch (EncryptedDocumentException e) {
				errorContent = "errors.crosscheck.fileopenpass";
				return null;
			}
			catch (Exception e) {
				errorContent = "errors.crosscheck.fileopen";
				return null;
			}

			//シートの読み込み
			Sheet sheet = wb.getSheet(sheetName);
			if (sheet == null) {
				errorContent = "errors.crosscheck.ngfile";
				return null;
			}
			//outputLog("sheet:" + sheet.getSheetName() + " row_count:" + sheet.getLastRowNum());

			//値読み込み
			for (int i = SPEC_ROW_START; i <= sheet.getLastRowNum(); i++) {

				Row row = sheet.getRow(i);
				if (row != null) {

					SpecRecord data = new SpecRecord();
					Cell cell = null;

					data.no = i + 1;	//Excelの開始は1から始まるため、1を加算する

					//対象データかどうか判定
					boolean isFlg = true;
					cell = row.getCell(SPEC_COLUMN_FLG);
					if (cell != null) {
						data.flg = getCellValue(cell);
						if (StringUtils.equals(data.flg, SPEC_UNUSED)) {
							isFlg = false;
						}
					}
					else {
						data.flg = "";
					}

					if (isFlg) {
						cell = row.getCell(SPEC_COLUMN_MSGNO);
						if (cell != null) {
							data.msgNo = getCellValue(cell);

							if (StringUtils.isEmpty(data.msgNo)) {
								isFlg = false;
							}
							else {
								//メッセージNoの桁数に満たない場合はスペースを付加する
								data.msgNo = StringUtils.rightPad(data.msgNo, MITEM_MSGNO_LENGTH);
							}
						}
						else {
							isFlg = false;
						}
					}

					if (isFlg) {
						cell = row.getCell(SPEC_COLUMN_ITEM);
						if (cell != null) {
							data.item = getCellValue(cell);
						}
						else {
							isFlg = false;
						}
					}

					//チェック対象であれば追加する
					if (isFlg) {
						dataList.add(data);
					}
				}
			}
		}
		catch (Exception e) {
			logger.info(e);
			throw e;
		}
		finally {
			IOUtils.closeQuietly(is);
			outputTime("end getSpecData");
		}
		return dataList;
	}

	/**
	 * クロスチェックを実行する
	 * @param specList スペックシートのデータ一覧
	 * @param mitemList 項目マスタのデータ一覧
	 * @param mVehicle 車種マスタのデータ
	 * @return チェック結果のデータ一覧
	 */
	private List<CheckRecord> crosscheck(List<SpecRecord> specList, List<MItem> mitemList, MVehicle mVehicle) {

		outputTime("start crosscheck");

		List<CheckRecord> dataList = new java.util.ArrayList<CheckRecord>();

		//MSGNOで昇順にソートする
		Collections.sort(mitemList, new Comparator<MItem>()
		{
			public int compare(MItem o1, MItem o2)
			{
				String oo1 = StringUtils.defaultString(o1.msgNo);
				String oo2 = StringUtils.defaultString(o2.msgNo);
				return oo1.compareTo(oo2);
			}
		});

		int renban = 0;
		for (SpecRecord specData : specList) {

			//MSGNOの最初の位置を探す
			int idx = 0;
			for (idx = 0; idx < mitemList.size(); idx++) {
				MItem item = mitemList.get(idx);
				if (StringUtils.equals(item.msgNo, specData.msgNo)) {
					break;
				}
			}

			for (ListIterator<MItem> mitemData = mitemList.listIterator(idx); mitemData.hasNext();) {

				MItem item = mitemData.next();

				//同じMSGNOでなくなったら次のMSGNOへ移る
				if (StringUtils.equals(item.msgNo, specData.msgNo) == false) {
					break;
				}

				CheckRecord checkData = new CheckRecord();
				checkData.specsheet = 0;
				checkData.master = 0;
				checkData.no = specData.no;
				checkData.specMsgNo = specData.msgNo;
				checkData.specItem = specData.item;
				checkData.pdaMsgNo = StringUtils.defaultString(item.msgNo);
				checkData.pdaItem = item.itemName;
				checkData.groupName = "";
				checkData.inspecOrder = -1;
				checkData.line = "";
				checkData.bctype = item.bctype;
				checkData.ptnDiv = "";
				checkData.renbanMasterOnly = 0;
				if (StringUtils.equals(item.itemName, specData.item)) {
					checkData.result = SPEC_MATCH_OK;
				}
				else {
					checkData.result = SPEC_MATCH_NG;
				}

				//左の場合
				boolean isPtnDivL = true;
				if (item.mOrderL != null) {
					for (MOrder orderData : item.mOrderL) {
						CheckRecord checkDataDiv = new CheckRecord();
						checkDataDiv = checkData.clone();
						checkDataDiv.groupName = orderData.mGroup == null ? "" : orderData.mGroup.groupName;
						checkDataDiv.inspecOrder = orderData.inspecOrder;
						checkDataDiv.line = orderData.mGroup == null ? "" : orderData.mGroup.line;
						checkDataDiv.ptnDiv = orderData.ptnDiv;
						checkDataDiv.renban = ++renban;
						dataList.add(checkDataDiv);
						isPtnDivL = false;
					}
				}
				//右の場合
				boolean isPtnDivR = true;
				if (item.mOrderR != null) {
					for (MOrder orderData : item.mOrderR) {
						CheckRecord checkDataDiv = new CheckRecord();
						checkDataDiv = checkData.clone();
						checkDataDiv.groupName = orderData.mGroup == null ? "" : orderData.mGroup.groupName;
						checkDataDiv.inspecOrder = orderData.inspecOrder;
						checkDataDiv.line = orderData.mGroup == null ? "" : orderData.mGroup.line;
						checkDataDiv.ptnDiv = orderData.ptnDiv;
						checkDataDiv.renban = ++renban;
						dataList.add(checkDataDiv);
						isPtnDivR = false;
					}
				}
				//左も右もない場合
				if (isPtnDivL && isPtnDivR) {
					checkData.renban = ++renban;
					dataList.add(checkData);
				}
			}

			//一致がない場合（スペックシートのみに存在する）
			if (idx >= mitemList.size()) {
				CheckRecord checkData = new CheckRecord();
				checkData.specsheet = 0;
				checkData.master = 1;
				checkData.no = specData.no;
				checkData.specMsgNo = specData.msgNo;
				checkData.specItem = specData.item;
				checkData.pdaMsgNo = "";
				checkData.pdaItem = "";
				checkData.groupName = "";
				checkData.inspecOrder = -1;
				checkData.line = "";
				checkData.bctype = mVehicle == null ? "" : mVehicle.bctype;
				checkData.ptnDiv = "";
				checkData.renban = ++renban;
				checkData.renbanMasterOnly = 0;
				checkData.result = SPEC_MATCH_NG;
				dataList.add(checkData);
			}

		}

		//一致がない場合（マスタのみに存在する）
		int renbanMasterOnly = 0;
		for (MItem mitemData : mitemList) {

			int idx = 0;
			for (idx = 0; idx < specList.size(); idx++) {
				SpecRecord item = specList.get(idx);
				if (StringUtils.equals(item.msgNo, mitemData.msgNo)) {
					break;
				}
			}

			if (idx >= specList.size()) {
				CheckRecord checkData = new CheckRecord();
				checkData.specsheet = 1;
				checkData.master = 0;
				checkData.no = Integer.MAX_VALUE;
				checkData.specMsgNo = "";
				checkData.specItem = "";
				checkData.pdaMsgNo = StringUtils.defaultString(mitemData.msgNo);
				checkData.pdaItem = mitemData.itemName;
				checkData.groupName = "";
				checkData.inspecOrder = -1;
				checkData.line = "";
				checkData.bctype = mitemData.bctype;
				checkData.ptnDiv = "";
				checkData.renban = renban;
				checkData.result = SPEC_MATCH_NG;

				//左の場合
				boolean isPtnDivL = true;
				if (mitemData.mOrderL != null) {
					for (MOrder orderData : mitemData.mOrderL) {
						CheckRecord checkDataDiv = new CheckRecord();
						checkDataDiv = checkData.clone();
						checkDataDiv.groupName = orderData.mGroup == null ? "" : orderData.mGroup.groupName;
						checkDataDiv.inspecOrder = orderData.inspecOrder;
						checkDataDiv.line = orderData.mGroup == null ? "" : orderData.mGroup.line;
						checkDataDiv.ptnDiv = orderData.ptnDiv;
						checkDataDiv.renbanMasterOnly = ++renbanMasterOnly;
						dataList.add(checkDataDiv);
						isPtnDivL = false;
					}
				}
				//右の場合
				boolean isPtnDivR = true;
				if (mitemData.mOrderR != null) {
					for (MOrder orderData : mitemData.mOrderR) {
						CheckRecord checkDataDiv = new CheckRecord();
						checkDataDiv = checkData.clone();
						checkDataDiv.groupName = orderData.mGroup == null ? "" : orderData.mGroup.groupName;
						checkDataDiv.inspecOrder = orderData.inspecOrder;
						checkDataDiv.line = orderData.mGroup == null ? "" : orderData.mGroup.line;
						checkDataDiv.ptnDiv = orderData.ptnDiv;
						checkDataDiv.renbanMasterOnly = ++renbanMasterOnly;
						dataList.add(checkDataDiv);
						isPtnDivR = false;
					}
				}
				//左も右もない場合
				if (isPtnDivL && isPtnDivR) {
					checkData.renbanMasterOnly = ++renbanMasterOnly;
					dataList.add(checkData);
				}
			}
		}

		outputTime("end crosscheck");

		return dataList;
	}

	/**
	 * チェック結果を出力する
	 * @param list チェック結果のデータ一覧
	 * @param sopFlag 号口フラグ（PDA：項目マスタ）
	 * @return 出力データ
	 */
	private String outputResultData(List<CheckRecord> list, String sopFlag) {

		outputTime("start outputResultData");

		StringBuilder outData = new StringBuilder();
		String lineData = "";

		//判定結果
		int ngCount = 0;
		for (CheckRecord data : list) {
			if (StringUtils.equals(data.result, SPEC_MATCH_NG)) {
				ngCount++;
			}
		}
		String result = ngCount == 0 ? "OK" : "NG(" + ngCount + "件)";
		lineData = "ﾁｪｯｸ判定結果"
				+ "," + result
				+ "\r\n";
		outData.append(lineData);

		//処理日時
		String datetime = DateFormatUtils.format(new Date(), "yyyy/M/d HH:mm");
		lineData = "ﾁｪｯｸ実施日時"
				+ "," + datetime
				+ "\r\n";
		outData.append(lineData);

		//マスタ種類
		String sopName = sopFlag.equals("1") ? "本番" : "仮";
		lineData = "PDA ﾏｽﾀ種類"
				+ "," + sopName
				+ "\r\n";
		outData.append(lineData);

		//タイトル
		lineData = "ﾗｲﾝ:車種:右左:連番"
				+ "," + "ｽﾍﾟｯｸMsgNo"
				+ "," + "ｽﾍﾟｯｸ項目名"
				+ "," + "PDA MsgNo"
				+ "," + "PDA項目名"
				+ "," + "PDA工程"
				+ "," + "PDA検査順"
				+ "," + "項目名一致"
				+ "\r\n";
		outData.append(lineData);

		//チェック結果
		for (CheckRecord data : list) {
			String ptn = StringUtils.equals(data.ptnDiv, "1") ? "L" : StringUtils.equals(data.ptnDiv, "2") ? "R" : "";
			String renban = data.specsheet == 0 ? String.format("%1$05d", data.no) : "";
			String inspecOrder = data.inspecOrder == -1 ? "" : data.inspecOrder.toString();

			lineData = data.line + ":" + data.bctype + ":" + ptn + ":" + renban
					+ "," + data.specMsgNo
					+ "," + data.specItem
					+ "," + data.pdaMsgNo
					+ "," + data.pdaItem
					+ "," + data.groupName
					+ "," + inspecOrder
					+ "," + data.result
					+ "\r\n";
			outData.append(lineData);
		}

		outputTime("end outputResultData");

		return outData.toString();
	}
}
