package jp.co.ctc.entity
{
	import mx.collections.ArrayCollection;

	[Bindable]
	[RemoteClass(alias="jp.co.ctc.entity.LgMOrder")]
	public class LgMOrder
	{
		public function LgMOrder()
		{
		}

		public var mstVer:int;

		public var spsCode:int;

		public var partCode:int;

		public var takeOrder:int;

		public var sopFlag:String;

		public var editDiv:String;

		public var insertUser:String;

		public var updateUser:String;

		public var insertDate:Date;

		public var updateDate:Date;

		public var mPart:LgMPart;
		public var mSps:LgMSps;

	}
}