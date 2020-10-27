package jp.co.ctc.event
{
	import flash.events.Event;


	/**
	 * 仮本番登録処理用のイベント
	 *
	 * ITAGE JYO
	 */
	public class MasterRegistEvent extends Event
	{
		// 2016/02/24 DA del start
//		/**
//		 * 予約
//		 */
//		public static const Book:String ="MasterRegistEventBook";
//
//		/**
//		 * 検査順の予約
//		 */
//		public static const Procedure:String ="MasterRegistEventProcedure";
//
//		/**
//		 * 予約の解除
//		 */
//		public static const Cancel:String = "MasterRegistEventCancel";
		// 2016/02/24 DA del end
		
		// 2016/02/24 DA ins start
		/**
		 * 全体
		 */
		public static const CheckAll:String ="MasterRegistEventCheckAll";
		/**
		 * 検査順のみ
		 */
		public static const CheckInspec:String ="MasterRegistEventCheckInspec";
		/**
		 * 予約
		 */
		public static const Reserve:String = "MasterRegistEventReserve";
		/**
		 * 予約解除
		 */
		public static const ReserveCancel:String = "MasterRegistEventReserveCancel";
		/**
		 * 本番登録
		 */
		public static const RealRegist:String = "MasterRegistEventRealRegist";
		// 2016/02/24 DA ins end

		/**
		 * 編集されたイベント
		 *
		 */
		public static const InputTextChanged:String = "MasterRegistEventInputTextChanged";

		/**
		 * 切替組立連番と切替ボデーNOのどちらの入力も無い場合 仮マスタ本番登録を即時実行します。
		 */
		public static const ImmediatelyUpdate:String = "MasterRegistEventImmediatelyUpdate";

		/**
		 * 切替組立連番と切替ボデーNOのどちらの入力も無い場合 仮マスタ本番登録を即時実行します。
		 * 検査順を本番登録
		 */
		public static const ImmediatelyProcedure:String = "MasterRegistEventImmediatelyProcedure";


		/**
		 * イベント送信のとき、選択され行数
		 */
		public var rowIndex:int =0;

		/**
		 * イベント送信のとき、選択された行のデータ
		 *
		 */
		public var params:Object;

		/**
		 * コンストラクター
		 *
		 */
		public function MasterRegistEvent(type:String, rowIndex:int, params:Object=null, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			//
			this.rowIndex = rowIndex;

			this.params = params;

			super(type, bubbles, cancelable);

		}


	}
}