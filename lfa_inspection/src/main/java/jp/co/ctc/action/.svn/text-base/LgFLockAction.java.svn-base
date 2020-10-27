/**
 *
 */
package jp.co.ctc.action;

import java.util.List;

import javax.annotation.Resource;

import jp.co.ctc.entity.FBcdata;
import jp.co.ctc.entity.LgFLock;
import jp.co.ctc.entity.LgMSps;
import jp.co.ctc.entity.LgMUser;
import jp.co.ctc.service.FBcdataService;
import jp.co.ctc.service.LgFLockService;
import jp.co.ctc.service.LgMSpsService;
import jp.co.ctc.service.LgMUserService;

import org.apache.commons.lang.StringUtils;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.util.ResponseUtil;

/**
 * テスト用。
 * http://localhost:8400/lfa_inspection/lgFLock
 *
 * @author CJ01615
 *
 */
public class LgFLockAction {

	/**
	 * LgFLockServiceクラスを利用します
	 */
	@Resource
	protected LgFLockService lgFLockService;

	/**
	 * LgMUserServiceクラスを利用します
	 */
	@Resource
	protected LgMUserService lgMUserService;

	/**
	 * FBcdataServiceクラスを利用します
	 */
	@Resource
	protected FBcdataService fBcdataService;

	/**
	 * LgMSpsServiceクラスを利用します
	 */
	@Resource
	protected LgMSpsService lgMSpsService;

	/**
	 * ダウンロードロックをかけている（部品取出中の）ユーザーの一覧を表示します。
	 *
	 * @return null
	 */
	@Execute(validator = false)
	public String index() {
		StringBuffer responseText = new StringBuffer();
		responseText.append("≪取出中一覧≫\n\n");
		responseText.append("ﾎﾞﾃﾞｰNo\t号車\tSPS台車\t取出者ID\t取出者氏名\t取出開始日時\n");

		List<LgFLock> lgFLockList = lgFLockService.getLgFLock();
		for (LgFLock lgFLock : lgFLockList) {
			String bodyNo = lgFLock.bodyNo;
			String recvDay = lgFLock.recvDay;
			int spsCode = Integer.parseInt(lgFLock.spsCode.trim());
			FBcdata fBcdata = fBcdataService.getLogFBcdata(bodyNo, recvDay).get(0);
			LgMSps lgMSps = lgMSpsService.getLgMSpsByName(1, spsCode, fBcdata.lgmstVer).get(0);
			LgMUser lgMUser = lgMUserService.getMUser(lgFLock.userCode);
			responseText.append(bodyNo + "\t");
			responseText.append(StringUtils.trim(fBcdata.tSf.comment1) + "\t");
			responseText.append(lgMSps.spsName + "\t");
			responseText.append(lgFLock.userCode + "\t");
			responseText.append(lgMUser == null ? "削除済み" : lgMUser.userName + "\t");
			responseText.append(lgFLock.insertDate + "\n");
		}

		ResponseUtil.write(responseText.toString(), null, "Windows-31J");

		return null;
	}
}
