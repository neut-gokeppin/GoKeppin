/**
 *
 */
package jp.co.ctc.service;

import jp.co.ctc.entity.TMsgno;

import org.seasar.extension.jdbc.service.S2AbstractService;

/**
 * @author CJ01786
 *
 */
public class TMsgnoService extends S2AbstractService<TMsgno> {

	/**
	 * メッセージNoから列を取得する。
	 * @param msgno 捜査対象のMsgNo
	 * @return 取得した読み替えテーブルのリスト
	 */
	public TMsgno selectById(String msgno) {
		return select().id(msgno).getSingleResult();
	}
}
