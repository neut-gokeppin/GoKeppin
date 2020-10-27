package jp.co.ctc.entity
{
	import flash.net.FileReference;
	import flash.utils.ByteArray;

	import mx.collections.ArrayCollection;

	[Bindable]
	[RemoteClass(alias="jp.co.ctc.dto.LgMBcsignDTO")]
	public class LgMBcsignDTO
	{
		public function LgMBcsignDTO()
		{
		}

		public var mstVer:int;

		public var signCode:int;

		public var partCode:int;

		public var partName:String;

		public var msgDiv:Boolean;

		public var msgNo:String;

		public var bcPosition:int;

		public var bcLength:int;

		public var bcSign:String;

		public var supplierName:String;

		public var checkFlag:Boolean;

		public var rackAddress:String;

		public var backNo:String;

		public var partNo:String;

		public var identName:String;

		public var fileName:String;

		[Transient]
		public var fileReference:FileReference;

		public var fileBody:ByteArray;

		public var notuseFlag:Boolean;

		public var deleteFlag:String;

		public var sopFlag:String;

		public var spsNo:String;

		public var spsName:String;

		public var spsOrder:int;

		public var editDiv:String;

		public var insertUser:String;

		public var updateUser:String;

		public var insertDate:Date;

		public var updateDate:Date;
	}
}