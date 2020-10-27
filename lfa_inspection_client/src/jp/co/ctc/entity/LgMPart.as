package jp.co.ctc.entity
{
	import mx.collections.ArrayCollection;

	[Bindable]
	[RemoteClass(alias="jp.co.ctc.entity.LgMPart")]
	public class LgMPart
	{
		public function LgMPart()
		{
			this.bcLength = 1;
			this.bcPosition = 1;
		}

		public var partCode:int;

		public var mstVer:int;

		public var partName:String;

		public var msgDiv:Boolean;

		public var msgNo:String;

		public var bcPosition:int;

		public var bcLength:int;

//		public var checkFlag:Boolean;

//		public var rackAddress:String;

		public var deleteFlag:String;

		public var sopFlag:String;

		public var editDiv:String;

		public var dummy:int;

		public var insertUser:String;

		public var updateUser:String;

		public var insertDate:Date;

		public var updateDate:Date;

//		public var mOrderList:ArrayCollection;
//		public var mOrderL:MOrder;
//		public var mOrderR:MOrder;
		public var mOrderList:ArrayCollection;

		public var mBcsignList:ArrayCollection;

		public var fTakeResultList:ArrayCollection;
	}
}