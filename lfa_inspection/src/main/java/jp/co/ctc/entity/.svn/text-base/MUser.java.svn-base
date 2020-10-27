package jp.co.ctc.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

/**
 * 従業員マスタ
 *
 * @author sugihara
 *
 */
@Entity
public class MUser extends AbstractEntity implements Serializable {

	/**
	 * ?
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

	// 2016/02/24 DA ins start
	/**
	 * パスワード登録日
	 */
	public Timestamp passDate;

	/**
	 * パスワード期限
	 */
	@Transient
	public Timestamp passDateExpire;

	/**
	 * アクセス権限
	 */
	public String authority;
	// 2016/02/24 DA ins end

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
	 * クラスの各要素をArrayで返します。
	 * @return array
	 */
	public Object[] toArray() {
		Object[] array = { this.userCode, this.userName, this.teamCode,
				this.postName, this.passDate, this.authority, this.deleteFlag, this.insertUser,
				this.updateUser, this.insertDate, this.updateDate, this.passWord };
		return array;
	}

	/**
	 * 関連エンティティ：検査結果(View)
	 */
	@OneToMany (mappedBy = "mUSer")
	public List<VResult> vResultList;

	/**
	 * 関連エンティティ：車種マスタ
	 */
	@OneToMany (mappedBy = "updateMUser")
	public List<MVehicle> mVehicleList;

	/**
	 * 関連エンティティ：工程マスタ
	 */
	@OneToMany (mappedBy = "updateMUser")
	public List<MGroup> mGroupList;

	/**
	 * 関連エンティティ：検査項目マスタ
	 */
	@OneToMany (mappedBy = "updateMUser")
	public List<MItem> mItemList;

	/**
	 * 関連エンティティ：検査順マスタ
	 */
	@OneToMany (mappedBy = "updateMUser")
	public List<MOrder> mOrderList;

	/**
	 * 関連エンティティ：指示記号マスタ
	 */
	@OneToMany (mappedBy = "updateMUser")
	public List<MBcsign> mBcsignList;

	// 2016/02/24 DA ins start
	/**
	 * 関連エンティティ：予約者ユーザー
	 */
	@OneToOne
	@JoinColumns ({
		@JoinColumn(name = "user_code", referencedColumnName = "reserve_user") })
	public MReserve mReserve;
	// 2016/02/24 DA ins end
}
