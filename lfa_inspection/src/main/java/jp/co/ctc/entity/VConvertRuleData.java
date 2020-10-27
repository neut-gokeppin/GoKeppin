/**
 *
 */
package jp.co.ctc.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;




/**
 * G-ALC貼紙記号変換マスタ
 *
 * @author CJ01615
 *
 */
@Entity
public class VConvertRuleData implements Serializable {

	/**
	 * Serializableインタフェースの実装に必要。
	 */
	static final long serialVersionUID = 1L;

	/**
	 * user_group_code:bpchar(3) <Primary Key>
	 */
	@Id
	public String userGroupCode;

	/**
	 * test_live:bpchar(1) <Primary Key>
	 */
	@Id
	public String testLive;

//	/**
//	 * detail_condition_usage:numeric(1)
//	 */
//	public Integer detailConditionUsage;

	/**
	 * form_name:bpchar(30) <Primary Key>
	 */
	@Id
	public String formName;

	/**
	 *logical_terminal_name:bpchar(6) <Primary Key>
	 */
	@Id
	public String logicalTerminalName;

	/**
	 * bc_type:bpchar(1) <Primary Key>
	 */
	@Id
	public String bcType;

	/**
	 * group_no:bpchar(1) <Primary Key>
	 */
	@Id
	public String groupNo;

	/**
	 * field_name:bpchar(20) <Primary Key>
	 */
	@Id
	public String fieldName;

	/**
	 * msgno:bpchar(4) <Primary Key>
	 */
	@Id
	public String msgno;

	/**
	 * msgno_position:bpchar(2) <Primary Key>
	 */
	@Id
	public String msgnoPosition;

	/**
	 * msgno_length:bpchar(2) <Primary Key>
	 */
	@Id
	public String msgnoLength;

	/**
	 * symbol_type:bpchar(1) <Primary Key>
	 */
	@Id
	public String symbolType;

	/**
	 * broadcast_spec_code:bpchar(20) <Primary Key>
	 */
	@Id
	public String broadcastSpecCode;

	/**
	 * broadcast_spec_code2
	 */
	@Transient
	public String broadcastSpecCode2;

	/**
	 * convert_method:bpchar(4)
	 */
	public String convertMethod;

	/**
	 * image_file_name:bpchar(30)
	 */
	public String imageFileName;

	/**
	 * letter_spec_content:bpchar(1850)
	 */
	public String letterSpecContent;

	/**
	 * letter_spec_content2
	 */
	@Transient
	public String letterSpecContent2;


	/**
	 * broadcast_spec_code,letter_spec_contentを変換した値を取得
	 * @return
	 */
	public void replaceParameter() {
		broadcastSpecCode2 = replaceToEmpty(broadcastSpecCode);
		letterSpecContent2 = replaceToEmpty(letterSpecContent);
	}


	/**
	 * 文字中の任意の'\t'を空文字へ変換する
	 *
	 * @param t 変換前文字列
	 * @return replaceValue 変換後文字列
	 */
	public static String replaceToEmpty(String t) {
		String replaceValue = t;
		if (replaceValue != null) {
		replaceValue = replaceValue.replaceAll("\t", "");
		}
		return replaceValue;
	}

}

