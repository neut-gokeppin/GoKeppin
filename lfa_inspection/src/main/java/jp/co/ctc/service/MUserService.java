package jp.co.ctc.service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.service.S2AbstractService;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.struts.util.RequestUtil;

import flex.messaging.HttpFlexSession;
import jp.co.ctc.entity.MUser;
import jp.co.ctc.util.Utils;

/**
 * 従業員マスタを扱うサービス
 *
 * @author sugihara
 *
 */
public class MUserService extends S2AbstractService<MUser> {

	/**
	 * ユーザテーブルのリストを取得する。
	 * @return 取得したユーザのリスト
	 */
	public List<MUser> getMUsers() {
		return jdbcManager.from(MUser.class)
			.where("deleteFlag <> '1'")
			.getResultList();
	}

	/**
	 * ユーザテーブルから指定した従業員コードのエンティティを取得する。
	 * @param userCode 従業員コード
	 * @return 取得したユーザのエンティティ
	 */
	public MUser getMUser(String userCode) {
		return jdbcManager.from(MUser.class)
			.where("userCode = ? AND deleteFlag <> '1'", userCode)
			.getSingleResult();
	}
	/**
	 * ログイン処理を行う。
	 *
	 * @param userCode
	 *            ユーザーコード
	 * @return true:ログイン成功、false:ログイン失敗
	 */
	public boolean login(final String userCode) {
		MUser user = jdbcManager.from(MUser.class).where(
				"userCode = ? AND deleteFlag <> '1'", userCode)
				.getSingleResult();

		HttpFlexSession session = HttpFlexSession.getFlexSession(RequestUtil
				.getRequest());
		if (user != null) {
			// ログイン成功
			session.setAttribute("user", user);
			return true;
		} else {
			// ログイン失敗
			session.setAttribute("user", null);
			return false;
		}
	}

	/**
	 * ログイン処理を行う。
	 *
	 * @param userCode
	 *            ユーザーコード
	 * @param password パスワード
	 * @return true:ログイン成功、false:ログイン失敗
	 */
	public boolean login(final String userCode, final String password) {
		MUser user = jdbcManager.from(MUser.class).where(
				"userCode = ? AND passWord = ? AND deleteFlag <> '1'", userCode, password)
				.getSingleResult();

		HttpFlexSession session = HttpFlexSession.getFlexSession(RequestUtil
				.getRequest());
		if (user != null) {
			// ログイン成功
			session.setAttribute("user", user);
			return true;
		} else {
			// ログイン失敗
			session.setAttribute("user", null);
			return false;
		}
	}


	/**
	 * アクセス権限チェック付きログイン処理を行う。
	 *
	 * @param userCode ユーザーコード
	 * @param password パスワード
	 * @return 1:ログイン成功、2:パスワード期限切れ、3:アクセス権限なし、0:ログイン失敗
	 */
	public Integer loginAuthority(final String userCode, final String password)
	{
		MUser user = jdbcManager.from(MUser.class).where("userCode = ? AND passWord = ? AND deleteFlag <> '1'", userCode, password).getSingleResult();

		HttpFlexSession session = HttpFlexSession.getFlexSession(RequestUtil.getRequest());
		if (user != null) {
			// ログイン成功
			session.setAttribute("user", user);

			// 検査員の場合
			if (StringUtils.equals(user.authority, "0")) {
				return 3;
			}

			// パスワードが切れている場合
			String ret = getMUserPassWarningCheck();
			if (StringUtils.equals(ret, "-1")) {
				return 2;
			}

			// 成功の場合
			return 1;
		}
		else {
			// ログイン失敗
			session.setAttribute("user", null);
			return 0;
		}
	}

	/**
	 * ログインしているかどうかチェックする
	 *
	 * @return true:ログイン済み、false:ログインしていない
	 */
	public boolean isLogin() {
		HttpFlexSession session = HttpFlexSession.getFlexSession(RequestUtil
				.getRequest());
		Object userObj = session.getAttribute("user");
		return userObj != null;
	}

	/**
	 * ユーザーコードを返す
	 *
	 * @return userCode
	 */
	public String getUserCode() {
		HttpFlexSession session = HttpFlexSession.getFlexSession(RequestUtil
				.getRequest());

		Object userObj = session.getAttribute("user");

		if (userObj != null) {
			MUser user = (MUser) (userObj);
			return user.userCode;
		}
		return null;
	}

	/**
	 * マスタメンテナンスをセットする
	 *
	 * @param selectMst
	 *            セレクトマスタ
	 */
	public void setSelectMst(final int selectMst) {
		HttpFlexSession session = HttpFlexSession.getFlexSession(RequestUtil
				.getRequest());
		session.setAttribute("selectMst", selectMst);
	}

	/**
	 * マスタメンテナンスを取得する
	 *
	 * @return selectMst
	 */
	public Integer getSelectMst() {
		HttpFlexSession session = HttpFlexSession.getFlexSession(RequestUtil
				.getRequest());

		Object selectMstObj = session.getAttribute("selectMst");

		if (selectMstObj != null) {
			Integer selectMst = (Integer) selectMstObj;
			return selectMst;
		}
		return null;
	}

	/**
	 * 従業員マスタメンテ画面検索（初期ロード時使用）
	 *
	 * @return 従業員全件データ
	 */
	public List<MUser> getMUsersByKeys() {

		return getMUsersByKeys("", "", "", "", "1", "1", "1", "1", "1", "1");

	}


	/**
	 * 従業員マスタメンテ検索（検索ボタン押下）
	 *
	 * @param userCode ユーザーコード
	 * @param userName ユーザー名
	 * @param teamCode グループコード
	 * @param deleteFlag 削除フラグ
	 * @return 検索に対応するユーザー
	 */
	public List<MUser> getMUsersByKeys(String userCode, String userName,
			String teamCode, String deleteFlag,
			// 2016/02/24 DA ins start
			String passNotExpired, String passExpired,
			String masterAdmin, String officeOrganization, String officeOrganizationNot, String examiner
			// 2016/02/24 DA ins end
			) {

		//ワイルドカード付与
		userCode = "%" + userCode + "%";
		userName = "%" + userName + "%";
		teamCode = "%" + teamCode + "%";
		deleteFlag = "%" + deleteFlag + "%";

		// 2016/02/24 DA ins start
		int passwordExpireDays = (Integer) SingletonS2ContainerFactory.getContainer().getComponent("passwordExpireDays");
		StringBuilder where1 = new StringBuilder();

		// パスワード登録日の条件
		where1.append(" AND (current_timestamp <= ");
		if("1".equals(passNotExpired)) {
			where1.append("pass_date + interval '" + passwordExpireDays + " day'");
		} else {
			where1.append("'0001/01/01'");
		}

		where1.append(" OR current_timestamp > ");
		if("1".equals(passExpired)) {
			where1.append("pass_date + interval '" + passwordExpireDays + " day'");
		} else {
			where1.append("'9999/01/01'");
		}

		// 権限の条件
		where1.append(") AND authority IN (''");

		if("1".equals(masterAdmin)) {
			where1.append(",'3'");
		}

		if("1".equals(officeOrganization)) {
			where1.append(",'2'");
		}

		if("1".equals(officeOrganizationNot)) {
			where1.append(",'1'");
		}

		if("1".equals(examiner)) {
			where1.append(",'0'");
		}

		where1.append(")");
		// 2016/02/24 DA ins end

		// 2016/02/24 DA del start
//		return jdbcManager.from(MUser.class)
//				.where("userCode like ? AND userName like ? AND deleteFlag like ?"
//						+ " AND teamCode like ? ",
//						userCode, userName, deleteFlag, teamCode)
//				.orderBy("deleteFlag, replace(upper(team_code), 'CJ', 'ZZ'), userCode")
//				.getResultList();
		// 2016/02/24 DA del end

		// 2016/02/24 DA ins start
		List<MUser> userList = jdbcManager.from(MUser.class)
				.where("userCode like ? AND userName like ? AND deleteFlag like ?"
						+ " AND teamCode like ? "
						+ where1.toString(),
						userCode, userName, deleteFlag, teamCode
						)
				.orderBy("deleteFlag, replace(upper(team_code), 'CJ', 'ZZ'), userCode")
				.getResultList();

		for(MUser user : userList) {
			// 値のスペースなどの加工をする
			user.userName = Utils.trimDisplay(user.userName);
			user.teamCode = Utils.trimDisplay(user.teamCode);
			user.postName = Utils.trimDisplay(user.postName);

			// パスワード期限に変換する
			user.passDateExpire = this.calcDate(user.passDate, passwordExpireDays);
		}

		return userList;
		// 2016/02/24 DA ins end
	}


	/**
	 * 従業員マスタを更新します。
	 *
	 * @param updateUsers
	 *            追加／更新レコードのリスト
	 *
	 */
	public void updateAll(List<MUser> updateUsers) {
		// 更新レコードにセットするタイムスタンプ
		Timestamp timestamp = Utils.nowts();

		// 追加 or 更新
		for (MUser user : updateUsers) {

			if (user.editDiv == null) {
				continue;
			}
			if (user.editDiv.equals("I")) {
				// 新規追加
				user.insertDate = timestamp;
				user.passWord = user.userCode.toUpperCase();
				insert(user);

			} else if (user.editDiv.equals("U")) {
				// 更新
				user.updateDate = timestamp;
				update(user);
			}
		}
	}

	/**
	 * パスワード期限警告チェック
	 * パスワード期限の警告を表示するためのチェックし、警告する必要ない場合は何も返さない。警告が必要な場合は期限までの残り日数を返す。
	 * @return 数値：残り日数、空白：警告不要
	 */
	public String getMUserPassWarningCheck()
	{
		HttpFlexSession session = HttpFlexSession.getFlexSession(RequestUtil.getRequest());

		Object userObj = session.getAttribute("user");
		if (userObj != null) {

			MUser user = (MUser) (userObj);
			if (user.passDate != null) {

				int wNum = (Integer) SingletonS2ContainerFactory.getContainer().getComponent("passwordExpireWarnDays");
				int eNum = (Integer) SingletonS2ContainerFactory.getContainer().getComponent("passwordExpireDays");

				// パスワード期限を算出
				Calendar calPass = Calendar.getInstance();
				calPass.setTime(user.passDate);
				calPass.add(Calendar.DATE, eNum);

				Calendar calNow = Calendar.getInstance();

				// 日単位（24h × 60m × 60s × 1000ms）
				double dayTime = 24 * 60 * 60 * 1000;

				// 残り期限を秒単位で取得
				long diffTime = (long) (calPass.getTimeInMillis() + dayTime - 1 - calNow.getTimeInMillis());

				// 残り期限を日単位に変換
				Double diffDays = diffTime / dayTime;

				if (diffDays < 0) {
					// 期限切れ
					return "-1";
				}
				else if (diffDays < 1) {
					// 本日切れ
					return "0";
				}
				else if (diffDays <= wNum) {
					// 警告期限
					Long day = (long) (diffDays + 1);
					return day.toString();
				}
				else {
					// 有効期限
					return "";
				}
			}
		}
		return null;
	}

	/**
	 * パスワードを更新する
	 * @param userCode 従業員コード
	 * @param password パスワード
	 * @param loginUser ログインユーザーのユーザーコード
	 */
	public void updatePassword(String userCode, String password, String loginUser)
	{
		MUser user = new MUser();
		user.userCode = userCode;
		user.passWord = password;
		user.passDate = Utils.nowtsDate();
		user.updateDate = Utils.nowts();
		user.updateUser = loginUser;
		jdbcManager.update(user).includes("passWord", "passDate", "updateUser", "updateDate").execute();

		// ユーザ情報が変更されたので、ログイン処理を行いセッションを更新させる。
		login(userCode, password);
	}

	/**
	 * 日付計算
	 * @param dateTime 日付
	 * @param offsetDay 計算する日数
	 * @return 計算後の日付
	 */
	private Timestamp calcDate(Timestamp dateTime, int offsetDay) {

		if(dateTime == null) {
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(dateTime);
		cal.add(Calendar.DATE, offsetDay);

		return new Timestamp(cal.getTimeInMillis());
	}
}
