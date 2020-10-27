package jp.co.ctc.entity
{
	import flash.net.FileReference;
	import flash.utils.ByteArray;
	import mx.collections.ArrayCollection;

	[Bindable]
	[RemoteClass(alias="jp.co.ctc.entity.LgFTakeresult")]
	public class LgFTakeresult
	{
		public function LgFTakeresult()
		{
		}
		/**
		 * ボデーNo.
		 */
		public var bodyNo:String;
		/**
		 * 受信日
		 */
		public var recyDay:String;
		/**
		 * 部品Code
		 */
		public var partCode:String;
		/**
		 * 取出回数
		 */
		public var takeNo:int;
		/**
		 * 棚チェック結果
		 */
		public var rackResult:String;
		/**
		 * 部品チェック結果
		 */
		public var partResult:String;
		/**
		 * 作成者
		 */
		public var takeUser:String;
		/**
		 * 棚照合時間
		 */
		public var rackDate:Date;
		/**
		 * 部品チェック時間
		 */
		public var partDate:Date;
		/**
		 * 部品QRコード
		 */
		public var partQrcode:String;
		/**
		 * 棚QRコード
		 */
		public var rackQrcode:String;

		/**
		 * 関連エンティティ:従業員マスタ
		 */
		public var lgMUser:LgMUser;
		/**
		 * 関連エンティティ:部品マスタ
		 */
		public var mPart:LgMPart;
		/**
		 * 関連エンティティ:指示記号マスタ
		 */
		public var mBcsign:LgMBcsign;

//		/**
//		 * 関連エンティティ：BCデータ
//		 */
//		public var fBcdata:FBcdata;

	}
}