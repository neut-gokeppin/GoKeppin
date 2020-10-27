package jp.co.ctc.service;

import java.util.List;

import jp.co.ctc.entity.MTiremaker;

import org.seasar.extension.jdbc.service.S2AbstractService;
import org.seasar.struts.util.RequestUtil;

import flex.messaging.HttpFlexSession;

/**
 * タイヤメーカーを扱うサービス
 *
 * @author kato
 *
 */
public class MTiremakerService extends S2AbstractService<MTiremaker> {

	/**
	 * ユーザテーブルのリストを取得する。
	 *
	 * @return 取得したユーザのリスト
	 */
	public List<MTiremaker> getMTiremakers() {
		return jdbcManager.from(MTiremaker.class).where("deleteFlag <> '1'")
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
