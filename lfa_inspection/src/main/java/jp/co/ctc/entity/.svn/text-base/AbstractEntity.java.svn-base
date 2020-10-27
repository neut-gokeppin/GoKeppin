package jp.co.ctc.entity;

import java.sql.Timestamp;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

/**
 * 共通カラムを定義するクラス。
 *
 * @author kaidu
 *
 */
@MappedSuperclass
public abstract class AbstractEntity {

	/**
	 * マスタバージョン
	 */
//	public Integer mstVer;

	/**
	 * 作成者
	 */
	public String insertUser;

	/**
	 * 更新者
	 */
	public String updateUser;

	/**
	 * 作成日時
	 */
	public Timestamp insertDate;

	/**
	 * 更新日時
	 */
	public Timestamp updateDate;

	/**
	 * 関連エンティティ：更新ユーザー
	 */
	@ManyToOne
	@JoinColumn(name = "update_user", referencedColumnName = "user_code")
	public MUser updateMUser;

}