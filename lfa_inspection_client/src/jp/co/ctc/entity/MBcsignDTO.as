package jp.co.ctc.entity
{
	import flash.net.FileReference;
	import flash.utils.ByteArray;
	
	import mx.collections.ArrayCollection;

	[Bindable]
	[RemoteClass(alias="jp.co.ctc.dto.MBcsignDTO")]
	public class MBcsignDTO
	{
		public function MBcsignDTO()
		{
		}

		public var signCode:int;

		public var itemCode:int;

		public var mstVer:int;

		public var itemNo:String;

		public var itemName:String;

		public var resultDiv:String;

		public var msgDiv:Boolean;

		public var msgNo:String;

		public var bcPosition:int;

		public var bcLength:int;

		public var bcSign:String;

		public var signContents:String;

		public var dummySign:String;

		public var signOrder:int;

		public var fileName:String;

		[Transient]
		public var fileReference:FileReference;

		public var fileBody:ByteArray;

		public var tLimit:String;

		public var bLimit:String;
		
		// 2016/02/24 DA ins start
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
		 * 関連エンティティ：予約者情報
		 */
		public var reserveMUser:MUser;
		// 2016/02/24 DA ins end

		public var deleteFlag:String;

		public var sopFlag:String;

		public var notes:String;

		public var groupNoL:String;

		public var groupNameL:String;

		public var groupOrderL:int;

		public var groupNoR:String;

		public var groupNameR:String;

		public var groupOrderR:int;

		public var editDiv:String;

		public var insertUser:String;

		public var updateUser:String;

		public var insertDate:Date;

		public var updateDate:Date;

		/**
		 * 更新者のMUserオブジェクト
		 */
		public var updateMUser:MUser;

		// 2014/04/07 DA ins start
		// 画像削除フラグ  削除時:Dをセット
		[Transient]
		public var imgDel:String; 
		// 2014/04/07 DA ins end
		
		// 2016/02/24 DA ins start
		public var selectFlag:String;
		
		public var sopDeleteFlag:String;
		// 2016/02/24 DA ins end

		// 2016/02/24 DA ins start
		/**
		 * 記号比較結果 
		 */
		public var bcSignUnMat:String;
		/**
		 * 検査内容比較結果 
		 */
		public var signContentsUnMat:String;
		/**
		 * ダミー内容比較結果 
		 */
		public var dummySignUnMat:String;
		/**
		 * ファイル名比較結果 
		 */
		public var fileNameUnMat:String;
		/**
		 * 差異確認結果メッセージ 
		 */
		public var diffResultMsg:String; 
		// 2016/02/24 DA ins end
	}
}