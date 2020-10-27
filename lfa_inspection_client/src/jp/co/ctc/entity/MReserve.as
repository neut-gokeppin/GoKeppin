package jp.co.ctc.entity
{
	[Bindable]
	[RemoteClass(alias="jp.co.ctc.entity.MReserve")]
	public class MReserve
	{
		public function MReserve()
		{
		}

		/**
		 * マスタバージョン
		 */
		public var mstVer:int;
		
		/**
		 * BC車種区分
		 */
		public var bctype:String;

		/**
		 * 予約フラグ
		 */
		public var reserveFlag:String;

		/**
		 * 予約者
		 */
		public var reserveUser:String;

		/**
		 * 予約日
		 */
		public var reserveDate:Date;

		/**
		 * 本番登録方法
		 */
		public var registrationMethod:String;
		
		/**
		 * 予約のMUserオブジェクト
		 */
		public var mUser:MUser;
	}
}