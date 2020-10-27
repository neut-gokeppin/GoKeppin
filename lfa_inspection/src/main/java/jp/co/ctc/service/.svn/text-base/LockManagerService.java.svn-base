/**
 *
 */
package jp.co.ctc.service;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.List;

import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.FieldUtil;

import jp.co.ctc.entity.LgFLock;
import jp.co.ctc.util.ServletUtil.Attribute;
import jp.co.ctc.util.ServletUtil.LockState;

/**
 * @author CJ01786
 *
 */
public class LockManagerService {



	/**
	 * 排他情報テーブルのエンティティクラス
	 */
	private Class<?> lockClass;

	/**
	 * 排他情報テーブルにおいて「ボデーNo」「受信日」と並んで主キーとなる「コード」
	 */
	private String codeFieldName;

	/**
	 * JDBCマネージャです。
	 */
	public JdbcManager jdbcManager;

	/**
	 * テーブルの種類に応じた値の設定を行う。
	 * @param type テーブルの種類
	 * @throws Exception テーブル種別が異なることによる例外
	 */
	private void getTypeSetting(Attribute type) throws Exception {
		switch (type) {
		case logistics:
			this.lockClass = LgFLock.class;
			this.codeFieldName = "spsCode";
			break;

		default:
			throw new Exception("種別選択が不正です");
		}
	}

	/**
	 * 排他情報に対し、指定したユーザがロックをしているか
	 * @param type 排他情報をチェックするテーブルの種類
	 * @param bodyNo ボデーNo.
	 * @param recvDay 受信日
	 * @param typeCode 種類によるコード（グループ・SPSなど）
	 * @param userCode ユーザーコード
	 * @return ロック状況の列挙定数
	 * @throws Exception テーブル種別が異なることによる例外
	 */
	public LockState getLockState(
			Attribute type, String bodyNo, String recvDay, Integer typeCode, String userCode
			) throws Exception {

		this.getTypeSetting(type);

		//ロックの有無を検索する。
		List<?> lockList = jdbcManager.from(this.lockClass)
			.where("bodyNo = ? AND recvDay = ? AND " + this.codeFieldName + " = ?",
					bodyNo, recvDay, typeCode.toString()
					)
			.getResultList();

		if (lockList.size() > 0) {
			//排他情報がある場合。

			//取得したロックエンティティを格納
			Object lock = lockList.get(0);

			//エンティティからユーザコードのフィールドを取得。
			Field userCodeField = ClassUtil.getField(this.lockClass, "userCode");

			//ロックエンティティからユーザコードの値を取得
			String lockUser = FieldUtil.getString(userCodeField, lock);

			//大文字で処理する（DBは確実に大文字になってる？）
			lockUser = lockUser.toUpperCase().trim();
			if (!userCode.toUpperCase().trim().equals(lockUser)) {
				//ロックしているユーザと、要求を送ってきたユーザが異なる場合

				return LockState.anotherLock;	//ほかの人がロックしているとみなす
			} else {
				return LockState.userLock;		//ユーザ自身のロックとみなす
			}
		}
		return LockState.nothing;				//ロックなしとみなす。
	}


	/**
	 * 排他情報を作成する。
	 * @param type 排他情報を作成するテーブルの種類
	 * @param bodyNo ボデーNo.
	 * @param recvDay 受信日
	 * @param typeCode 種類によるコード（グループ・SPSなど）
	 * @param userCode ユーザーコード
	 * @return 排他情報生成の可否
	 * @throws Exception テーブル種別が異なることによる例外
	 */
	public boolean createLock(
			Attribute type, String bodyNo, String recvDay, Integer typeCode, String userCode
			) throws Exception {

		//ロックをかけていなければ、排他テーブルにレコードを追加する。
		LockState lockState = this.getLockState(type, bodyNo, recvDay, typeCode, userCode);

		if (lockState == LockState.nothing) {
			return this.simpleCreateLock(type, bodyNo, recvDay, typeCode, userCode);
		} else {
			return false;
		}
	}
	/**
	 * ロック重複の確認をせず排他情報を作成する。
	 * @param type 排他情報を作成するテーブルの種類
	 * @param bodyNo ボデーNo.
	 * @param recvDay 受信日
	 * @param typeCode 種類によるコード（グループ・SPSなど）
	 * @param userCode ユーザーコード
	 * @return 排他情報生成の可否
	 * @throws Exception テーブル種別が異なることによる例外
	 */
	public boolean simpleCreateLock(
			Attribute type, String bodyNo, String recvDay, Integer typeCode, String userCode
			) throws Exception {

		Object insLock;

		this.getTypeSetting(type);

		insLock = ClassUtil.newInstance(this.lockClass);
		Field setField;

		setField = ClassUtil.getField(this.lockClass, "bodyNo");
		FieldUtil.set(setField, insLock, bodyNo);

		setField = ClassUtil.getField(this.lockClass, "recvDay");
		FieldUtil.set(setField, insLock, recvDay);

		setField = ClassUtil.getField(this.lockClass, this.codeFieldName);
		FieldUtil.set(setField, insLock, typeCode.toString());

		setField = ClassUtil.getField(this.lockClass, "userCode");
		FieldUtil.set(setField, insLock, userCode.toUpperCase());

		setField = ClassUtil.getField(this.lockClass, "insertUser");
		FieldUtil.set(setField, insLock, userCode.toUpperCase());

		// 更新レコードにセットするタイムスタンプ
		Timestamp timestamp = new Timestamp(new java.util.Date().getTime());

		setField = ClassUtil.getField(this.lockClass, "insertDate");
		FieldUtil.set(setField, insLock, timestamp);

		return jdbcManager.insert(insLock).execute() == 1;

	}

	/**
	 * 排他情報を削除する
	 * @param type 排他情報を削除するテーブルの種類
	 * @param bodyNo ボデーNo.
	 * @param recvDay 受信日
	 * @param typeCode 種類によるコード（グループ・SPSなど）
	 * @param userCode ユーザーコード
	 * @return 削除の可否
	 * @throws Exception テーブル種別が異なることによる例外
	 */
	public boolean removeLock(
			Attribute type, String bodyNo, String recvDay, Integer typeCode, String userCode
			) throws Exception {

		LockState state = this.getLockState(type, bodyNo, recvDay, typeCode, userCode);

		if (state == LockState.userLock) {
			return this.simpleRemoveLock(type, bodyNo, recvDay, typeCode, userCode);

		} else {
			return false;
		}
	}

	/**
	 * テーブルの存在確認をせずに排他情報を削除する
	 * @param type 排他情報を削除するテーブルの種類
	 * @param bodyNo ボデーNo.
	 * @param recvDay 受信日
	 * @param typeCode 種類によるコード（グループ・SPSなど）
	 * @param userCode ユーザーコード
	 * @return 削除の可否
	 * @throws Exception テーブル種別が異なることによる例外
	 */
	public boolean simpleRemoveLock(
			Attribute type, String bodyNo, String recvDay, Integer typeCode, String userCode
			) throws Exception {

		Object delLock;

		this.getTypeSetting(type);

		delLock = ClassUtil.newInstance(this.lockClass);
		Field setField;

		setField = ClassUtil.getField(this.lockClass, "bodyNo");
		FieldUtil.set(setField, delLock, bodyNo);

		setField = ClassUtil.getField(this.lockClass, "recvDay");
		FieldUtil.set(setField, delLock, recvDay);

		setField = ClassUtil.getField(this.lockClass, this.codeFieldName);
		FieldUtil.set(setField, delLock, typeCode.toString());

		return jdbcManager.delete(delLock).execute() == 1;
	}
}
