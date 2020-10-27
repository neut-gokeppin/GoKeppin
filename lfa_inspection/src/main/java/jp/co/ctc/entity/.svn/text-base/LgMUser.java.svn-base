package jp.co.ctc.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

/**
 * 従業員マスタ
 *
 * @author sugihara
 *
 */
@Entity
public class LgMUser extends AbstractEntity implements Serializable {

	/**
	 * Serializableインタフェースの実装に必要。
	 */
	static final long serialVersionUID = 1L;

	/**
	 * 従業員コード
	 */
	@Id
	public String userCode;

	/**
	 * 従業員氏名
	 */
	public String userName;

	/**
	 * 組コード
	 */
	public String teamCode;

	/**
	 * 部署名
	 */
	public String postName;

	/**
	 * 削除フラグ
	 */
	public String deleteFlag;

	/**
	 * パスワード
	 */
	public String passWord;

	/**
	 * 編集区分
	 */
	@Transient
	public String editDiv;

	/**
	 * 関連エンティティ：搬入結果
	 */
	@OneToMany (mappedBy = "lgMUser")
	public List<LgFStoreresult> lgFStoreresultList;

	/**
	 * 関連エンティティ：取出結果
	 */
	@OneToMany (mappedBy = "lgMUser")
	public List<LgFTakeresult> lgFTakeresultList;

	/**
	 * 関連エンティティ：取出結果(View)
	 */
	@OneToMany (mappedBy = "lgMUSer")
	public List<LgVResult> lgVResultList;

	/**
	 * クラスの各要素をArrayで返します。
	 * @return array
	 */
	public Object[] toArray() {
		Object[] array = { this.userCode, this.userName, this.teamCode,
				this.postName, this.deleteFlag, this.insertUser,
				this.updateUser, this.insertDate, this.updateDate, this.passWord };
		return array;
	}

}
