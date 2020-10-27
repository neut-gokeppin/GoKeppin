package jp.co.ctc.service;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.extension.timer.TimeoutManager;
import org.seasar.extension.timer.TimeoutTarget;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.log.Logger;
import org.seasar.framework.util.ResourceUtil;
import org.seasar.struts.util.ServletContextUtil;

import jp.co.ctc.entity.FBcdata;
import jp.co.ctc.entity.FShotimage;
import jp.co.ctc.entity.MGroup;
import jp.co.ctc.entity.MItem;
import jp.co.ctc.entity.MOrder;
import jp.co.ctc.util.Utils;

/**
 * ファイル監視処理を行う。
 *
 * @author DA 2017/12/01
 */
public class FileMonitoringService implements TimeoutTarget
{
	/**
	 * JdbcManagerを使います。
	 */
	public JdbcManager jdbcManager;

	/**
	 * ログ出力用
	 */
	private Logger logger = Logger.getLogger(FileMonitoringService.class);

	/**
	 * FBcdataServiceを使います
	 */
	@Resource
	public FBcdataService fBcdataService;

	/**
	 * MGroupService
	 */
	@Resource
	public MGroupService mGroupService;

	/**
	 * MItemService
	 */
	@Resource
	public MItemService mItemService;

	/**
	 * 撮影画像テーブルを扱うサービス
	 */
	@Resource
	public FShotimageService fShotimageService;

	/**
	 * 予約処理を実行するためのタイマーを起動します。 このメソッドは、S2Container起動時に自動的に呼び出すよう、
	 * app.diconにて設定しています。
	 *
	 * @param permanent
	 *            タイマーを永続的に実行するか。true:永続実行、false:1回だけ実行
	 */
	public void importStart(boolean permanent)
	{
		// タイマー実行間隔
		int interval = 1;

		// タイマー起動
		TimeoutManager timeoutManager = TimeoutManager.getInstance();
		timeoutManager.addTimeoutTarget(this, interval, permanent);

		logger.info("ファイル監視処理起動");
	}

	/**
	 * 定期的に実行する処理。 このメソッドは、importStartメソッドにて指定したタイミングで 定期的に実行されます。
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void expired()
	{
		// サービスを停止した直後に処理が実行されるとS2コンテナが取得できない。サービス停止したので処理は終了させる
		try {
			SingletonS2ContainerFactory.getContainer();
		}
		catch (Exception e) {
			return;
		}

		Properties props = ResourceUtil.getProperties("application_ja.properties");
		List<String> errList = new ArrayList<String>();
		try {
			// パラメータチェック
			List<String> paraList = getErrorParameters();
			if (paraList.size() != 0) {
				String msg = props.getProperty("svr0000018");
				errList.add(msg);
				errList.addAll(paraList);
				logger.error(msg);
				return;
			}

			String imageFolder = (String) SingletonS2ContainerFactory.getContainer().getComponent("recvShotimageFolder");
			String errorFolder = (String) SingletonS2ContainerFactory.getContainer().getComponent("recvShotimageErrorFolder");
			String completionFolder = (String) SingletonS2ContainerFactory.getContainer().getComponent("recvShotimageCompletionFolder");
			Integer retryCount = (Integer) SingletonS2ContainerFactory.getContainer().getComponent("retryCount");
			Integer inspectionExpireDays = (Integer) SingletonS2ContainerFactory.getContainer().getComponent("shotimageInspectionExpireDays");

			String siFolder = ServletContextUtil.getServletContext().getRealPath("shotimages");

			// 撮影完了ファイル一覧を取得する
			String[] txtfiles = getEofFileList(imageFolder);

			for (String txtFilename : txtfiles) {

				try {
					// 撮影完了ファイルの移動
					boolean isMoveRet = moveShotimageFile(txtFilename, imageFolder, completionFolder, retryCount, null, null, null);
					if (isMoveRet == false) {
						// ファイルの移動に失敗しました。ファイル名={0}
						String msg = MessageFormat.format(props.getProperty("svr0000009"), txtFilename);
						errList.add(msg);
						logger.error(msg);
						continue;
					}

					// 撮影完了ファイル名の分解
					String[] txtFileArray = getFileSplit(txtFilename);
					if (txtFileArray == null) {
						// ファイル名の分解に失敗しました。ファイル名={0}
						String msg = MessageFormat.format(props.getProperty("svr0000010"), txtFilename);
						errList.add(msg);
						logger.error(msg);
						continue;
					}

					String bodyNo = txtFileArray[1];
					String bctype = txtFileArray[2];
					Integer groupCode = Integer.valueOf(txtFileArray[3]);

					// 車両情報を取得
					FBcdata bcdata = fBcdataService.selectByBodyNoOrBcno(bodyNo, null);
					if (bcdata == null) {
						moveShotimageFile(null, imageFolder, errorFolder, retryCount, bodyNo, bctype, txtFileArray[3]);
						// 該当ボデーNOがありません。ファイル名={0}, ボデーNO={1}, 車種区分={2}
						String msg = MessageFormat.format(props.getProperty("svr0000011"), txtFilename, bodyNo, bctype);
						errList.add(msg);
						logger.error(msg);
						continue;
					}

					// 過去30日以内のデータかチェック
					boolean isNotDate = isDateIsNotWithinDays(txtFileArray[5], bcdata.loDate, inspectionExpireDays);
					if (isNotDate) {
						moveShotimageFile(null, imageFolder, errorFolder, retryCount, bodyNo, bctype, txtFileArray[3]);
						// 該当ボデーNOがありません。ファイル名={0}, ボデーNO={1}, 車種区分={2}
						String msg = MessageFormat.format(props.getProperty("svr0000011"), txtFilename, bodyNo, bctype);
						errList.add(msg);
						logger.error(msg);
						continue;
					}

					// 項目情報の取得
					MGroup mGroup = mGroupService.getMGroupByName(1, groupCode, null, bcdata.ptnDiv);
					if (mGroup == null) {
						moveShotimageFile(null, imageFolder, errorFolder, retryCount, bodyNo, bctype, txtFileArray[3]);
						// 該当検査項目がありません。ファイル名={0}, ボデーNO={1}, 車種区分={2}, 工程コード={3}, 項目Code={4}
						String msg = MessageFormat.format(props.getProperty("svr0000012"), txtFilename, bodyNo, bctype, txtFileArray[3], "ALL");
						errList.add(msg);
						logger.error(msg);
						continue;
					}

					// レコードにセットするタイムスタンプは撮影完了ファイル単位で同じになるようにする
					Timestamp timestamp = Utils.nowts();

					// 撮影画像ファイル一覧を取得する
					String[] jpgfiles = getImageFileList(imageFolder, bodyNo, bctype, txtFileArray[3]);

					List<Integer> itemCodeList = new ArrayList<Integer>();

					for (String jpgFilename : jpgfiles) {

						try {
							// 撮影画像ファイル名の分解
							String[] jpgFileArray = getFileSplit(jpgFilename);
							if (jpgFileArray == null) {
								moveShotimageFile(jpgFilename, imageFolder, errorFolder, retryCount, null, null, null);
								// ファイル名の分解に失敗しました。ファイル名={0}
								String msg = MessageFormat.format(props.getProperty("svr0000010"), jpgFilename);
								errList.add(msg);
								logger.error(msg);
								continue;
							}

							Integer itemCode = Integer.valueOf(jpgFileArray[4]);
							itemCodeList.add(itemCode);

							// 過去30日以内のデータかチェック
							isNotDate = isDateIsNotWithinDays(jpgFileArray[5], bcdata.loDate, inspectionExpireDays);
							if (isNotDate) {
								moveShotimageFile(jpgFilename, imageFolder, errorFolder, retryCount, null, null, null);
								// 撮影画像が古いため取込をしません。ファイル名={0}
								String msg = MessageFormat.format(props.getProperty("svr0000014"), jpgFilename);
								logger.error(msg);
								continue;
							}

							// 項目チェック
							boolean isOther = true;
							for (MOrder mOrder : mGroup.mOrderList) {
								MItem mItem = mOrder.mItem;
								if (mItem != null && mItem.itemCode.equals(itemCode)) {
									isOther = false;
									break;
								}
							}
							if (isOther) {
								// 車両のパター区分が違うマスタ情報は取得していないので、取得し直す。
								MItem item = mItemService.getMItemsByCode(1, null, itemCode);
								if (item == null) {
									moveShotimageFile(jpgFilename, imageFolder, errorFolder, retryCount, null, null, null);
									// 該当検査項目がありません。ファイル名={0}, ボデーNO={1}, 車種区分={2}, 工程コード={3}, 項目Code={4}
									String msg = MessageFormat.format(props.getProperty("svr0000012"), jpgFilename, bodyNo, bctype, txtFileArray[3], itemCode.toString());
									errList.add(msg);
									logger.error(msg);
									continue;
								}
							}

							// 登録
							FShotimage fShotimage = new FShotimage();
							fShotimage.bodyNo = jpgFileArray[1];
							fShotimage.bctype = jpgFileArray[2];
							fShotimage.groupCode = Integer.valueOf(jpgFileArray[3]);
							fShotimage.itemCode = Integer.valueOf(jpgFileArray[4]);
							fShotimage.shotDate = jpgFileArray[5];
							fShotimage.productionSign = jpgFileArray[6];
							fShotimage.shotImage = jpgFilename;
							fShotimage.completionFlag = "0";
							fShotimage.idno = bcdata.idno;
							fShotimage.loDate = bcdata.loDate;
							fShotimage.insertUser = "SYSTEM";
							fShotimage.updateUser = "SYSTEM";
							fShotimage.insertDate = timestamp;
							fShotimage.updateDate = timestamp;

							fShotimageService.createFShotimage(fShotimage);

							// 撮影画像ファイルの移動
							isMoveRet = moveShotimageFile(jpgFilename, imageFolder, siFolder, retryCount, null, null, null);
							if (isMoveRet == false) {
								// ファイルの移動に失敗しました。ファイル名={0}
								String msg = MessageFormat.format(props.getProperty("svr0000009"), jpgFilename);
								errList.add(msg);
								logger.error(msg);
								continue;
							}
						}
						catch (Exception e) {
							// 撮影画像ファイルで異常が起きた場合
							// 撮影画像の取込に失敗しました。ファイル名={0}
							String msg = MessageFormat.format(props.getProperty("svr0000008"), jpgFilename);
							errList.add(msg);
							logger.error(msg, e);
						}
					}

					// 後処理を行う
					// 項目の撮影画像が存在しているかチェック
					List<Integer> errCodeList = new ArrayList<Integer>();
					for (MOrder mOrder : mGroup.mOrderList) {
						MItem mItem = mOrder.mItem;
						if (mItem != null) {
							boolean isNotEqualsCode = true;
							for (Integer itemCode : itemCodeList) {
								if (mItem.itemCode.equals(itemCode)) {
									isNotEqualsCode = false;
									break;
								}
							}
							if (isNotEqualsCode) {
								errCodeList.add(mItem.itemCode);
							}
						}
					}
					if (errCodeList.size() != 0) {
						for (Integer errCode : errCodeList) {
							// 該当検査項目の画像がありません。ボデーNO={0}, 車種区分={1}, 工程コード={2}, 項目Code={3}
							String msg = MessageFormat.format(props.getProperty("svr0000013"), bodyNo, bctype, groupCode.toString(), errCode.toString());
							errList.add(msg);
							logger.error(msg);
						}
					}

					// 撮影完了フラグを更新
					FShotimage fShotimage = new FShotimage();
					fShotimage.bctype = txtFileArray[2];
					fShotimage.groupCode = Integer.valueOf(txtFileArray[3]);
					fShotimage.idno = bcdata.idno;
					fShotimage.loDate = bcdata.loDate;
					fShotimage.completionFlag = "1";
					fShotimage.updateUser = "SYSTEM";
					fShotimage.updateDate = Utils.nowts();
					fShotimageService.updateCompletionFlag(fShotimage);
				}
				catch (Exception e) {
					// 撮影完了ファイルで異常が起きた場合
					// 撮影画像の取込に失敗しました。ファイル名={0}
					String msg = MessageFormat.format(props.getProperty("svr0000008"), txtFilename);
					errList.add(msg);
					logger.error(msg, e);
				}
			}
		}
		catch (Exception e) {
			String msg = props.getProperty("svr0000018");
			errList.add(msg);
			logger.error(msg, e);
		}
		finally {
			// 異常があった場合の処理
			if (errList.size() != 0) {
				sendNotifyMail(errList);
			}
		}
	}

	/**
	 * パラメータチェック
	 * @return 異常結果の情報
	 */
	private List<String> getErrorParameters()
	{
		List<String> errList = new ArrayList<String>();
		try {
			String imageFolder = (String) SingletonS2ContainerFactory.getContainer().getComponent("recvShotimageFolder");

			File dir = new File(imageFolder);
			if (dir.exists() == false) {
				String msg = "Parameters Error : " + imageFolder;
				errList.add(msg);
				logger.error(msg);
			}
		}
		catch (Exception e) {
			String msg = "Parameters Error : recvShotimageFolder";
			errList.add(msg);
			logger.error(msg, e);
		}

		try {
			String errorFolder = (String) SingletonS2ContainerFactory.getContainer().getComponent("recvShotimageErrorFolder");

			File dir = new File(errorFolder);
			if (dir.exists() == false) {
				String msg = "Parameters Error : " + errorFolder;
				errList.add(msg);
				logger.error(msg);
			}
		}
		catch (Exception e) {
			String msg = "Parameters Error : recvShotimageErrorFolder";
			errList.add(msg);
			logger.error(msg, e);
		}

		try {
			String completionFolder = (String) SingletonS2ContainerFactory.getContainer().getComponent("recvShotimageCompletionFolder");

			File dir = new File(completionFolder);
			if (dir.exists() == false) {
				String msg = "Parameters Error : " + completionFolder;
				errList.add(msg);
				logger.error(msg);
			}
		}
		catch (Exception e) {
			String msg = "Parameters Error : recvShotimageCompletionFolder";
			errList.add(msg);
			logger.error(msg, e);
		}

		try {
			SingletonS2ContainerFactory.getContainer().getComponent("retryCount");
		}
		catch (Exception e) {
			String msg = "Parameters Error : retryCount";
			errList.add(msg);
			logger.error(msg, e);
		}

		try {
			SingletonS2ContainerFactory.getContainer().getComponent("shotimageInspectionExpireDays");
		}
		catch (Exception e) {
			String msg = "Parameters Error : shotimageInspectionExpireDays";
			errList.add(msg);
			logger.error(msg, e);
		}

		try {
			String scriptFile = (String) SingletonS2ContainerFactory.getContainer().getComponent("fileMonitoringErrorNotifyMail");
			File notify = ResourceUtil.getResourceAsFile(scriptFile);

			if (notify.exists() == false) {
				String msg = "Parameters Error : " + scriptFile;
				errList.add(msg);
				logger.error(msg);
			}
		}
		catch (Exception e) {
			String msg = "Parameters Error : fileMonitoringErrorNotifyMail";
			errList.add(msg);
			logger.error(msg, e);
		}

		try {
			String siFolder = ServletContextUtil.getServletContext().getRealPath("shotimages");

			File dir = new File(siFolder);
			if (dir.exists() == false) {
				String msg = "Parameters Error : " + siFolder;
				errList.add(msg);
				logger.error(msg);
			}
		}
		catch (Exception e) {
			String msg = "Parameters Error : shotimages";
			errList.add(msg);
			logger.error(msg, e);
		}

		return errList;
	}

	/**
	 * 撮影完了ファイル一覧を取得する
	 *
	 * @param imagesFolder 撮影画像受信フォルダ
	 * @return ファイル一覧
	 */
	private String[] getEofFileList(String imagesFolder)
	{
		String[] files = null;
		try {
			FilenameFilter filter = new FilenameFilter()
			{
				public boolean accept(File dir, String name)
				{
					if (name.endsWith(".eof")) {
						return true;
					}
					else {
						return false;
					}
				}
			};
			files = new File(imagesFolder).list(filter);
		}
		catch (Exception e) {
			logger.error("File Get Error", e);
		}
		if (files == null) {
			files = new String[0];
		}
		return files;
	}

	/**
	 * 撮影画像ファイル一覧を取得する
	 *
	 * @param imagesFolder 撮影画像受信フォルダ
	 * @param bodyNo ボデーNO
	 * @param bctype BC車種区分コード
	 * @param groupCode 工程コード
	 * @return ファイル一覧
	 */
	private String[] getImageFileList(String imagesFolder, String bodyNo, String bctype, String groupCode)
	{
		String[] files = null;
		try {
			String targetVehicle = "img_" + bodyNo + "_" + bctype + "_" + groupCode + "_*.jpg";
			FilenameFilter jpgNamefilter = new WildcardFileFilter(targetVehicle);
			files = new File(imagesFolder).list(jpgNamefilter);
		}
		catch (Exception e) {
			logger.error("File Get Error", e);
		}
		if (files == null) {
			files = new String[0];
		}
		return files;
	}

	/**
	 * 日付2が日付1の何日以前ではないかチェック
	 *
	 * @param data1 日付1 (yyyyMMddHHmmss)
	 * @param data2 日付2 (yyyyMMdd)
	 * @param inspectionExpireDays 過去日数
	 * @return 判定結果（true:日数以前である、false:日数以前でない）
	 */
	private boolean isDateIsNotWithinDays(String data1, String data2, Integer inspectionExpireDays)
	{
		boolean isNotDate = false;
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			Date date1 = df.parse(data1.substring(0, 8));
			Date date2 = df.parse(data2);

			Calendar cal1 = Calendar.getInstance();
			cal1.setTime(date1);
			cal1.add(Calendar.DAY_OF_MONTH, inspectionExpireDays * -1);

			Calendar cal2 = Calendar.getInstance();
			cal2.setTime(date2);

			if (cal2.compareTo(cal1) < 0) {
				isNotDate = true;
			}
		}
		catch (Exception e) {
			isNotDate = true;
			logger.error("Date Error", e);
		}
		return isNotDate;
	}

	/**
	 * ファイル移動
	 *
	 * @param sourceFilename ファイル名
	 * @param sourcePath 移動元フォルダ
	 * @param targetPath 移動先フォルダ
	 * @param retryCount リトライ回数
	 * @param bodyNo ボデーNO
	 * @param bctype BC車種区分コード
	 * @param groupCode 工程コード
	 * @return 処理結果（true:正常終了、false:異常終了）
	 */
	private boolean moveShotimageFile(String sourceFilename, String sourcePath, String targetPath, Integer retryCount, String bodyNo, String bctype, String groupCode)
	{
		String[] files = null;
		if (sourceFilename == null) {
			// 撮影画像ファイル一覧を取得する
			files = getImageFileList(sourcePath, bodyNo, bctype, groupCode);
		}
		else {
			files = new String[1];
			files[0] = sourceFilename;
		}

		for (String filename : files) {

			int loopCount = 0;
			for (int i = 0; i <= retryCount; i++) {
				try {
					boolean isRen = true;
					File sourceFile = new File(sourcePath + "/" + filename);
					if (sourceFile.exists() == false) {
						// この時点でファイルが無いなんてありえないと思うけどね。
						loopCount++;
						continue;
					}

					File targetFile = new File(targetPath + "/" + filename);
					if (targetFile.exists()) {
						targetFile.delete();
					}

					isRen = sourceFile.renameTo(targetFile);
					if (isRen) {
						// 正常の場合は処理を抜ける
						break;
					}
					else {
						loopCount++;
					}
				}
				catch (Exception e) {
					loopCount++;
				}

				// リトライ中は処理を待つ、最後は待たない
				if (i != retryCount) {
					sleep();
				}
			}
			if ((loopCount - 1) == retryCount) {
				logger.error("File Move Error");
				return false;
			}
		}
		return true;
	}

	/**
	 * エラーメール送信
	 *
	 * @param itemList 異常メッセージ一覧
	 */
	private void sendNotifyMail(List<String> itemList)
	{
		if (itemList == null) {
			return;
		}

		// メール送信コマンド
		String scriptFile = (String) SingletonS2ContainerFactory.getContainer().getComponent("fileMonitoringErrorNotifyMail");
		File notify = ResourceUtil.getResourceAsFile(scriptFile);
		// コマンド実行
		try {
			ProcessBuilder pb = new ProcessBuilder();
			List<String> command = new ArrayList<String>();
			command.add("cmd");
			command.add("/c");
			command.add(notify.getAbsolutePath());
			command.addAll(itemList);
			pb.command(command).start();
		}
		catch (Exception e) {
			logger.error("Mail Error", e);
		}
	}

	/** ファイル名の各項目の位置 */
	public static class FILESPLIT
	{
		/** 固定値：img */
		public static final int INDEX_IMG = 0;
		/** ボテーNO */
		public static final int INDEX_BODY_NO = 1;
		/** 車種区分 */
		public static final int INDEX_BCTYPE = 2;
		/** 工程コード */
		public static final int INDEX_GROUP_CODE = 3;
		/** 項目Code */
		public static final int INDEX_ITEM_CODE = 4;
		/** 撮影日時 */
		public static final int INDEX_SHOT_DATE = 5;
		/** 生産指示記号 */
		public static final int INDEX_PRODUCTION_SIGN = 6;
		/** 拡張子 */
		public static final int INDEX_EXTENSION = 7;

		/** 項目数 */
		public static final int MAX = 8;
	}

	/**
	 * ファイル名の分解
	 * @param data ファイル名
	 * @return 各情報
	 */
	public static String[] getFileSplit(String data)
	{
		String[] ret = null;

		if (data != null) {

			String name = ""; // 名前
			String extension = ""; // 拡張子

			int len = data.lastIndexOf(".");
			if (len == -1) {
				name = data;
			}
			else {
				name = data.substring(0, len);
				if (data.length() > len + 1) {
					extension = data.substring(len + 1);
				}
			}

			String filename = name + "_" + extension;

			String[] siArray = filename.split("_");
			if (siArray.length == FILESPLIT.MAX) {
				ret = siArray;
			}
		}

		return ret;
	}

	/**
	 * 一定時間スリープする
	 */
	private void sleep()
	{
		try {
			final int milliseconds = 100;
			Thread.sleep(milliseconds);
		}
		catch (InterruptedException e) {
			logger.error("sleep中に例外発生", e);
		}
	}
}
