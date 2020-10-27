package jp.co.ctc.entity
{
	import flash.net.FileReference;
	import flash.utils.ByteArray;
	
	import mx.collections.ArrayCollection;

	[Bindable]
	[RemoteClass(alias="jp.co.ctc.entity.MBcsign")]
	public class MBcsign
	{
		public function MBcsign()
		{
		}

		public var signCode:int;
		
		public var itemCode:int;

		public var mstVer:int;

		public var bcSign:String;

		public var signContents:String;

		public var dummySign:String;

		public var signOrder:int;

		public var fileName:String;

		public var tLimit:String;

		public var bLimit:String;
		
		public var reserveFlag:String;
		
		public var reserveUser:String;
		
		public var reserveDate:Date;
		
		public var reserveMUser:MUser;

		public var deleteFlag:String;

		public var sopFlag:String;

		public var notes:String;
		
		public var sopDeleteFlag:String;
	}
}