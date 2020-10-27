/**
 *
 */
package jp.co.ctc.servlet;

import java.util.List;

import jp.co.ctc.entity.LgMUser;
import jp.co.ctc.service.LgMUserService;

import org.seasar.framework.container.SingletonS2Container;

/**
 * @author CJ01786
 *
 */
public class LogUserid extends Userid {

	/* (非 Javadoc)
	 * @see jp.co.ctc.servlet.Userid#getUserCode(java.lang.Object)
	 */
	@Override
	protected String getUserCode(Object user) throws Exception {
		LgMUser mUser = (LgMUser) this.getTypeCheckedObject(user, LgMUser.class);
		if (mUser.userCode == null) {
			return "";
		}
		return mUser.userCode;
	}

	/* (非 Javadoc)
	 * @see jp.co.ctc.servlet.Userid#getUserList()
	 */
	@Override
	protected List<?> getUserList() throws Exception {
		LgMUserService srvUser = SingletonS2Container.getComponent(LgMUserService.class);
		return srvUser.getMUsers();
	}

	/* (非 Javadoc)
	 * @see jp.co.ctc.servlet.Userid#getUserName(java.lang.Object)
	 */
	@Override
	protected String getUserName(Object user) throws Exception {
		LgMUser mUser = (LgMUser) this.getTypeCheckedObject(user, LgMUser.class);
		if (mUser.userName == null) {
			return "";
		}
		return mUser.userName;
	}

}
