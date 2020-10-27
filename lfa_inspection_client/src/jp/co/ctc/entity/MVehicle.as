package jp.co.ctc.entity
{


	[Bindable]
	[RemoteClass(alias="jp.co.ctc.entity.MVehicle")]
	public class MVehicle
	{
		public function MVehicle()
		{
		}

		/**
		 * BC車種区分
		 */
		public var bctype:String;

		/**
		 * 車種名称
		 */
		public var vehicleName:String;

		// 2016/02/24 DA ins start
		/**
		 * 画像一覧出力OKフラグ
		 */
		public var listOkFlag:Boolean;
		// 2016/02/24 DA ins end

		/**
		 * 生産終了
		 */
		public var endOfProd:Boolean;
		
		// 2016/02/24 DA ins start
		/**
		 * ライン
		 */
		public var line:String;
		// 2016/02/24 DA ins end

		/* 車両テーブルに切替組立連番、切替ボデーNoっていうの２つコラムを追加されるのため、追加する  ITAGE JYO ADD 2013.04.13 S 001 */
		/**
		 * 切替組立連番
		 */
		public var bcnoH0:String;

		/**
		 * 切替ボデーNo
		 */
		public var bodyNo:String;
		/* 車両テーブルに切替組立連番、切替ボデーNoっていうの２つコラムを追加されるのため、追加する  ITAGE JYO ADD 2013.04.13 E 001 */

		/**
		 * 作成者
		 */
		public var insertUser:String;

		/**
		 * 更新者
		 */
		public var updateUser:String;

		/**
		 * 作成日
		 */
		public var insertDate:Date;

		/**
		 * 更新日
		 */
		public var updateDate:Date;

		/* ITAGE JYO 2013-04-16 仮マスタ本番登録 002 S */
		/**
		 * 予約の状態
		 * true  => 予約中
		 * false => 未予約
		 */
		public var bookStatus:Boolean;
		/* ITAGE JYO 2013-04-16 仮マスタ本番登録 002 E */

		/**
		 * 更新者のMUserオブジェクト
		 */
		public var updateMUser:MUser;

		// 2014/11/20 DA ins start
		/**
		 * 編集区分
		 */
		public var editDiv:String;
		/**
		 * BC車種区分コード ＋ 車種名称
		 */
		public var bctypeVehicle:String;
		// 2014/11/20 DA ins end
		
		// 2016/02/24 DA ins start
		/**
		 * 車種の本番予約オブジェクト
		 */
		public var mReserve:MReserve;
		/**
		 * 指示記号予約の状態
		 */
		public var bcsignReserve:Boolean;
		// 2016/02/24 DA ins end
	}
}