package jp.co.ctc.entity
{
	import mx.collections.ArrayCollection;


	[Bindable]
	[RemoteClass(alias="jp.co.ctc.entity.FBcdata")]
	public class FBcdata
	{
		public function FBcdata()
		{
		}

		/**
		 * ボデーNo.
		 */
		public var bodyNo:String;

		/**
		 * 受信日
		 */
		public var recvDay:String;

		/**
		 * 物流マスタバージョン
		 */
		public var lgmstVer:int;


		/**
		 * パターン区分
		 */
		public var ptnDiv:String;

		/**
		 * 作成者
		 */
		public var insertUser:String;

		/**
		 * 作成日時
		 */
		public var insertDate:Date;

		/**
		 * アイデントNo
		 */
		public var idno:String;
		
		/**
		 * ラインオフ計画日
		 */
		public var loDate:String;
		
		/**
		 * フレーム区分
		 */
		public var frameCode:String;

		/**
		 * フレーム連番
		 */
		public var frameSeq:String;

		/**
		 * BC車種区分コード
		 */
		public var bctype:String;

		/**
		 * 組立連番
		 */
		public var bcnoH0:String;

		// 2014/04/07 DA ins start
		/**
		 * ライン(艤装)。
		 */
		public var lineGiso:String;
		/**
		 * ライン(セールス)。
		 */
		public var lineSales:String;
		/**
		 * ライン(拡張用)。
		 */
		public var lineArea03:String;
		/**
		 * TP通過日時(艤装)。
		 */
		public var tpN0:Date;
		/**
		 * TP通過日時(セールス)。
		 */
		public var tpSales:Date;
		/**
		 * TP通過日時(拡張用)。
		 */
		public var tpArea03:Date;
		// 2014/04/07 DA ins end

		/**
		 * コントロール型式
		 */
		public var ctrlKata:String;

		/**
		 * 関連エンティティ：車種マスタ
		 */
		public var mVehicle:MVehicle;

		/**
		 * 関連エンティティ：検査順
		 */
		public var mOrderList:ArrayCollection;

	}
}