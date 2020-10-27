package jp.co.ctc.entity
{
	import mx.collections.ArrayCollection;

	[Bindable]
	[RemoteClass(alias="jp.co.ctc.entity.LgMSps")]
	public class LgMSps
	{
		public function LgMSps()
		{
		}

		public var spsCode:int;

		public var mstVer:int;

		public var spsNo:String;

		public var spsName:String;

		public var deleteFlag:String;

		public var sopFlag:String;

		public var editDiv:String;

		public var spsState:String;

		public var insertUser:String;

		public var updateUser:String;

		public var insertDate:Date;

		public var updateDate:Date;

		public var mOrderList:ArrayCollection;

	}
}