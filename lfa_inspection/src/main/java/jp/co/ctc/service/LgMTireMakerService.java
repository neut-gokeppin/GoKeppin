package jp.co.ctc.service;

import java.util.List;

import jp.co.ctc.entity.LgMTireMaker;

import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.struts.util.RequestUtil;

import flex.messaging.HttpFlexSession;


/**
 *
 * @author neutral
 *
 */
public class LgMTireMakerService {
	/**
	 * JdbcManagerを使います。
	 */
	public JdbcManager jdbcManager;

	/**
	 * タイヤメーカーテーブルのリストを取得する。
	 * @return 取得したタイヤメーカーのリスト
	 */
	public List<LgMTireMaker> getMTireMakers() {
		return jdbcManager.from(LgMTireMaker.class)
			.where("deleteFlag <> '1'")
			.getResultList();
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

}
