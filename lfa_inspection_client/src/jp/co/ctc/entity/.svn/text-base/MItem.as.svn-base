package jp.co.ctc.entity
{
	import mx.collections.ArrayCollection;
	import jp.co.ctc.entity.MBcsign;

	[Bindable]
	[RemoteClass(alias="jp.co.ctc.entity.MItem")]
	public class MItem
	{
		public function MItem()
		{
			this.bcLength = 1;
			this.bcPosition = 1;
		}

		public var itemCode:int;

		public var mstVer:int;

		public var bctype:String;

		public var itemNo:String;

		public var itemName:String;

		public var resultDiv:String;

		public var msgDiv:Boolean;

		public var msgNo:String;

		public var bcPosition:int;

		public var bcLength:int;

		public var tLimit:String;

		public var bLimit:String;

		public var tireDiv:Boolean;

		public var okngDiv:Boolean;

		public var deleteFlag:String;

		public var sopFlag:String;

		public var notes:String;

		public var editDiv:String;

		public var dummy:int;

		public var insertUser:String;

		public var updateUser:String;

		public var insertDate:Date;

		public var updateDate:Date;

//		public var mOrderList:ArrayCollection;
//		public var mOrderL:MOrder;
//		public var mOrderR:MOrder;
		public var mOrderL:ArrayCollection;
		public var mOrderR:ArrayCollection;

		public var mBcsignList:ArrayCollection;

		/**
		 * 更新者のMUserオブジェクト
		 */
		public var updateMUser:MUser;
		
		[Transient]
		public function checkReserveFlag():Boolean {
			if (mBcsignList != null){
				for ( var i:int; i < mBcsignList.length; i++ ) {
					var mBcsign:MBcsign = mBcsignList[i] as MBcsign;
					if (mBcsign != null && (mBcsign.reserveFlag == "1" || mBcsign.reserveFlag == "2")){
						return true;
					}
				}	
			}
			return false;
		}
	}
}