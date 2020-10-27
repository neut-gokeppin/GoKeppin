package jp.co.ctc.entity
{
	import flash.net.FileReference;
	import flash.utils.ByteArray;

	import mx.collections.ArrayCollection;

	[Bindable]
	[RemoteClass(alias="jp.co.ctc.entity.LgFStoreresult")]
	public class LgFStoreresult
	{
		public function LgFStoreresult()
		{
		}
		/**
		 * 部品QRコード
		 */
		public var partQrcode:String;
		/**
		 * 棚QRコード
		 */
		public var rackQrcode:String;
		/**
		 * 部品読取時刻
		 */
		public var partReaddate:Date;
		/**
		 * 棚読取時刻
		 */
		public var rackReaddate:Date;
		/**
		 * 担当者Code
		 */
		public var storeUser:String;
		/**
		 * 棚照合結果
		 */
		public var resultDiv:String

		/**
		 * 関連エンティティ
		 */
		public var lgMUser:LgMUser;
		/**
		* 関連エンティティ
		*/
		public var lgFTakeresult:LgFTakeresult;

	}
}
