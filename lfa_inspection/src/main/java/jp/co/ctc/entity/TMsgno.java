package jp.co.ctc.entity;

/* Code Generator Information.
 * generator Version 1.0.0 release 2007/10/10
 * generated Date Mon Sep 13 11:14:43 JST 2010
 */
import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * T_msgnoVo.
 * @author CJ01786
 * @version 1.0
 * history
 * Symbol	Date		Person		Note
 * [1]		2010/09/13	CJ01786		Generated.
 */
@Entity
public class TMsgno implements Serializable {

	/**
	 * Serializableインタフェースの実装に必要。
	 */
	static final long serialVersionUID = 1L;

	/**
	 * msgno:bpchar(4) <Primary Key>
	 */
	@Id
	public String msgno;

	/**
	 * length:numeric(2)
	 */
	public java.math.BigDecimal length;

	/**
	 * colname:bpchar(20)
	 */
	public String colname;

	/**
	 * tblname:bpchar(20)
	 */
	public String tblname;

	/**
	 * amend_flg:bpchar(1)
	 */
	public String amendFlg;

	/**
	 * forceame_flg:bpchar(1)
	 */
	public String forceameFlg;

	/**
	 * hisstor_flg:bpchar(1)
	 */
	public String hisstorFlg;

	/**
	 * reassign_flg:bpchar(1)
	 */
	public String reassignFlg;

	/**
	 * entry_flg:bpchar(1)
	 */
	public String entryFlg;

	/**
	 * sf_exchange_flg:bpchar(1)
	 */
	public String sfExchangeFlg;

	/**
	 * 関連エンティティ：部品
	 */
	@OneToMany (mappedBy = "tMsgno")
	public List<LgMPart> mPartList;

	/**
	 * 関連エンティティ：項目
	 */
	@OneToMany (mappedBy = "tMsgno")
	public List<MItem> mItemList;

	/**
	 * エンティティを文字列に変換します。
	 * @return String配列
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[T_msgnoVo:");
		buffer.append(" msgno: ");
		buffer.append(msgno);
		buffer.append(" length: ");
		buffer.append(length);
		buffer.append(" colname: ");
		buffer.append(colname);
		buffer.append(" tblname: ");
		buffer.append(tblname);
		buffer.append(" amend_flg: ");
		buffer.append(amendFlg);
		buffer.append(" forceame_flg: ");
		buffer.append(forceameFlg);
		buffer.append(" hisstor_flg: ");
		buffer.append(hisstorFlg);
		buffer.append(" reassign_flg: ");
		buffer.append(reassignFlg);
		buffer.append(" entry_flg: ");
		buffer.append(entryFlg);
		buffer.append(" sf_exchange_flg: ");
		buffer.append(sfExchangeFlg);
		buffer.append("]");
		return buffer.toString();
	}

}
