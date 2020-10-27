package jp.co.ctc.service;

import java.sql.Timestamp;
import java.util.List;

import jp.co.ctc.entity.LgMUser;

import org.seasar.struts.util.RequestUtil;
import flex.messaging.HttpFlexSession;
import org.seasar.extension.jdbc.JdbcManager;


/**
 *
 * @author CJ01785
 *
 */
public class LgMUserService {
	/**
	 * JdbcManagerを使います。
	 */
	public JdbcManager jdbcManager;

	/**
	 * ユーザテーブルのリストを取得する。
	 * @return 取得したユーザのリスト
	 */
	public List<LgMUser> getMUsers() {
		return jdbcManager.from(LgMUser.class)
			.where("deleteFlag <> '1'")
			.getResultList();
	}

	/**
	 * ユーザテーブルから指定した従業員コードのエンティティを取得する。
	 * @param userCode 従業員コード
	 * @return 取得したユーザのエンティティ
	 */
	public LgMUser getMUser(String userCode) {
		return jdbcManager.from(LgMUser.class)
			.where("userCode = ? AND deleteFlag <> '1'", userCode)
			.getSingleResult();
	}

	/**
	 * ログイン処理を行う。
	 * ログイン成功時はセッションにユーザーコードを格納。
	 *
	 * @param userCode 入力されたユーザーコード
	 * @return true or false
	 */
	public boolean login(final String userCode) {

		LgMUser user = jdbcManager.from(LgMUser.class).where(
				"userCode = ? AND deleteFlag <> '1'", userCode)
				.getSingleResult();

		HttpFlexSession session = HttpFlexSession.getFlexSession(RequestUtil
				.getRequest());
		if (user != null) {
			// ログイン成功
			session.setAttribute("lgUser", user);
			return true;
		} else {
			// ログイン失敗
			session.setAttribute("lgUser", null);
			return false;
		}
	}
	/**
	 * ログイン処理を行う。
	 * ログイン成功時はセッションにユーザーコードを格納。
	 *
	 * @param userCode 入力されたユーザーコード
	 * @param password パスワード
	 * @return true or false
	 */
	public boolean login(final String userCode, final String password) {

		LgMUser user = jdbcManager.from(LgMUser.class).where(
				"userCode = ? AND passWord = ? and deleteFlag <> '1'", userCode, password)
				.getSingleResult();

		HttpFlexSession session = HttpFlexSession.getFlexSession(RequestUtil
				.getRequest());
		if (user != null) {
			// ログイン成功
			session.setAttribute("lgUser", user);
			return true;
		} else {
			// ログイン失敗
			session.setAttribute("lgUser", null);
			return false;
		}
	}
	/**
	 * ログインチェックを行います。
	 * @return true or false
	 */
	public boolean isLogin() {
		HttpFlexSession session = HttpFlexSession.getFlexSession(RequestUtil
				.getRequest());
		Object userObj = session.getAttribute("lgUser");
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

		Object userObj = session.getAttribute("lgUser");

		if (userObj != null) {
			LgMUser user = (LgMUser) (userObj);
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
	public List<LgMUser> getMUsersByKeys() {

		return jdbcManager.from(LgMUser.class).orderBy("userCode")
				.getResultList();

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
	public List<LgMUser> getMUsersByKeys(String userCode, String userName,
			String teamCode, String deleteFlag) {

		//ワイルドカード付与
		userCode = "%" + userCode + "%";
		userName = "%" + userName + "%";
		teamCode = "%" + teamCode + "%";
		deleteFlag = "%" + deleteFlag + "%";

		return jdbcManager.from(LgMUser.class)
			.where("userCode like ? AND userName like ? AND deleteFlag like ?"
					+ " AND teamCode like ? ",
					userCode, userName, deleteFlag, teamCode)
			.orderBy("user_code")
			.getResultList();

	}

	/**
	 * 従業員マスタ登録
	 * @param user 登録するユーザエンティティ
	 * @return 挿入されたデータ
	 */
	public LgMUser create(LgMUser user) {
		jdbcManager.insert(user).execute();
		return user;
	}

	/**
	 * 従業員マスタ更新
	 * @param user 登録するユーザエンティティ
	 * @return 更新されたデータ
	 */
	public LgMUser update(LgMUser user) {
		jdbcManager.update(user).execute();
		return user;
	}

	/**
	 * 従業員マスタを更新します。
	 *
	 * @param updateUsers
	 *            追加／更新レコードのリスト
	 *
	 */
	public void updateAll(List<LgMUser> updateUsers) {
		// 更新レコードにセットするタイムスタンプ
		Timestamp timestamp = new Timestamp(new java.util.Date().getTime());

		System.out.println("updateAll Start");

		// 追加 or 更新
		for (LgMUser user : updateUsers) {

			System.out.println("Edit Div = " + user.editDiv);
			if (user.editDiv == null) {
				continue;
			}
			if (user.editDiv.equals("I")) {
				// 新規追加
				user.insertDate = timestamp;
				user.passWord = user.userCode.toUpperCase();
				create(user);
				System.out.println("Insert");
			} else if (user.editDiv.equals("U")) {
				// 更新
				user.updateDate = timestamp;
				update(user);
				System.out.println("Update");
			}
		}
	}

}
