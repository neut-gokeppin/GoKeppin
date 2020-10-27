/**
 *
 */
package jp.co.ctc.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;


/**
 * BCフォーマットマスタ
 *
 * @author CJ01615
 *
 */
@Entity
public class VBcfmt implements Serializable {

	/**
	 * Serializableインタフェースの実装に必要。
	 */
	static final long serialVersionUID = 1L;

	/**
	 * line:bpchar(1) <Primary Key>
	 */
	@Id
	public String line;

	/**
	 * point:bpchar(2) <Primary Key>
	 */
	@Id
	public String point;

	/**
	 * ltname:bpchar(6) <Primary Key>
	 */
	@Id
	public String ltname;

	/**
	 * bctype:bpchar(1) <Primary Key>
	 */
	@Id
	public String bctype;

	/**
	 * groupno:bpchar(1) <Primary Key>
	 */
	@Id
	public String groupno;

	/**
	 * tlcode:bpchar(1) <Primary Key>
	 */
	@Id
	public String tlcode;

	/**
	 * edit_no:bpchar(3) <Primary Key>
	 */
	@Id
	public String editNo;

	/**
	 * space_len:bpchar(3)
	 */
	public String spaceLen;

	/**
	 * field_name:bpchar(20)
	 */
	public String fieldName;

	/**
	 * msgno:bpchar(4)
	 */
	public String msgno;

	/**
	 * msgno_pos:bpchar(2)
	 */
	public Integer msgnoPos;

	/**
	 * msgno_len:bpchar(2)
	 */
	public Integer msgnoLen;

	/**
	 * char_type:bpchar(1)
	 */
	public String charType;

	/**
	 * h_magnification:bpchar(3)
	 */
	public String hMagnification;

	/**
	 * w_magnification:bpchar(3)
	 */
	public String wMagnification;

	/**
	 * char_size:bpchar(1)
	 */
	public String charSize;

	/**
	 * constant_string:bpchar(20)
	 */
	public String constantString;

	/**
	 * comments:bpchar(50)
	 */
	public String comments;

}
