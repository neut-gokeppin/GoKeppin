package jp.co.ctc.entity
{
	import flash.net.FileReference;
	import flash.utils.ByteArray;
	import mx.collections.ArrayCollection;

	[Bindable]
	[RemoteClass(alias="jp.co.ctc.entity.LgMUser")]
	public class LgMUser
	{
		public function LgMUser()
		{
		}

		/**
		 * 従業員コード
		 */
		public var userCode:String;

		/**
		 * 従業員氏名
		 */
		public var userName:String;

		/**
		 * 組コード
		 */
		public var teamCode:String;

		/**
		 * 部署名
		 */
		public var postName:String;

		/**
		 * 削除フラグ
		 */
		public var deleteFlag:String;

		/**
		 * 編集区分
		 */
		public var editDiv:String;

		/**
		 * 作成者
		 */
		public var insertUser:String;

		/**
		 * 更新者
		 */
		public var updateUser:String;

		/**
		 * 作成日
		 */
		public var insertDate:Date;

		/**
		 * 更新日
		 */
		public var updateDate:Date;

		/**
		 * パスワード
		 */
		public var passWord:String;

		/**
		 * 関連エンティティ
		 */
		public var lgFStoreresultList:ArrayCollection;


	}
}