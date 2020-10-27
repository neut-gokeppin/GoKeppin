package jp.co.ctc.entity
{
	import flash.net.FileReference;
	import flash.utils.ByteArray;

	import mx.collections.ArrayCollection;

	[Bindable]
	[RemoteClass(alias="jp.co.ctc.entity.VResult")]
	public class VResult
	{
		public function VResult()
		{
		}
		/**
		 * ボデーNo
		 */
		public var bodyNo:String;

		/**
		 * グループ名
		 */
		public var groupName:String;

		/**
		 * 検査結果
		 */
		public var inspecResult:int;

		/**
		 * 検査実施者
		 */
		public var inspecUser:String;

		/**
		 * 検査実施日
		 */
		public var inspecDate:String;

		/**
		 * グループコード
		 */
		public var groupCode:String;

		/**
		 * 受信日
		 */
		public var recvDay:String;

		/**
		 * 関連エンティティ:従業員マスタ
		 */
		public var mUser:MUser;

	}
}