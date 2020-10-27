package jp.co.ctc.event
{
	import mx.collections.ArrayCollection;

	/**
	 * マスタバージョンを管理する
     *
     * @author DA 2016/02/24
	 */
	public class MstSelectEvent
	{
		/**
		 * 仮0のマスタバージョン
		 */
		public static var MSTVER_TEMPORARY0 : int = 0;

		/**
		 * 仮1のマスタバージョン
		 */
		// 2017/03/02 CT del start
		//public static var MSTVER_TEMPORARY1 : int = -1;
		// 2017/03/02 CT del end

		/**
		 * 本番のマスタバージョン
		 * ※実際の本番マスターバージョンは、マスタバージョンのMAX値となります。
		 */
		public static var MSTVER_REAL : int = 1;

		/**
		 * マスタバージョン一覧
		 */
		private static var mstList : Array = new Array(
			{name : "仮０", code : MSTVER_TEMPORARY0 },
			// 2017/03/02 CT del start
			//{name : "仮１", code : MSTVER_TEMPORARY1 },
			// 2017/03/02 CT del end
			{name : "本番", code : MSTVER_REAL }
		);

		/**
		 * コンストラクタ
		 */
		public function MstSelectEvent()
		{
		}

		/**
		 * マスタバージョンの名称一覧を取得する
		 * @return 名称一覧
		 */
		public static function getMasterList() : ArrayCollection
		{
			var list:ArrayCollection = new ArrayCollection();
			for each (var item:Object in mstList) {
				var data:String = item["name"];
				list.addItem(data);
			}
			return list;
		}

		/**
		 * 仮マスタバージョン一覧を取得する
		 * @return マスタバージョン一覧
		 */
		public static function getTempMasterCodeList() : ArrayCollection
		{
			var list:ArrayCollection = new ArrayCollection();
			for each (var item:Object in mstList) {
				var data:int = item["code"];
				if (data != MSTVER_REAL) {
					list.addItem(data);
				}
			}
			return list;
		}

		/**
		 * 仮マスタバージョン名称一覧を取得する
		 * @return 名称一覧
		 */
		public static function getTempMasterNameList() : ArrayCollection
		{
			var list:ArrayCollection = new ArrayCollection();
			for each (var item:Object in mstList) {
				var dataCode:int = item["code"];
				if (dataCode != MSTVER_REAL) {
					var dataName:String = item["name"];
					list.addItem(dataName);
				}
			}
			return list;
		}

		/**
		 * 名称からマスタバージョンを取得する
		 * @param name マスタバージョン名称
		 * @return マスタバージョン
		 */
		public static function getMasterCode(name:String) : int
		{
			var code:int = 0;
			for each (var item:Object in mstList) {
				var data:String = item["name"];
				if (data == name) {
					code = item["code"];
					break;
				}
			}
			return code;
		}

		/**
		 * マスタバージョンから名称を取得する
		 * @param code マスタバージョン
		 * @return マスタバージョン名称
		 */
		public static function getMasterName(code:int) : String
		{
			var name:String = "";
			for each (var item:Object in mstList) {
				var data:int = item["code"];
				if (data == code) {
					name = item["name"];
					break;
				}
			}
			return name;
		}

		/**
		 * マスタバージョンが仮マスタかどうか判定する
		 * @param obj マスタバージョン or マスタバージョン名称
		 * @return true:仮マスタ、false:仮マスタでない
		 */
		public static function isTemporary(obj:Object) : Boolean
		{
			var code:int = 0;

			if (obj is int) {
				code = new int(obj);
			}
			else if (obj is String) {
				code = getMasterCode(obj.toString());
			}

			if (code <= 0) {
				return true;
			}
			return false;
		}

		/**
		 * マスタバージョンが仮0マスタかどうか判定する
		 * @param obj マスタバージョン or マスタバージョン名称
		 * @return true:仮0マスタ、false:仮0マスタでない
		 */
		public static function isTemporary0(obj:Object) : Boolean
		{
			var code:int = 0;

			if (obj is int) {
				code = new int(obj);
			}
			else if (obj is String) {
				code = getMasterCode(obj.toString());
			}

			if (code == 0) {
				return true;
			}
			return false;
		}

		/**
		 * マスタバージョンが本番マスタかどうか判定する
		 * @param obj マスタバージョン or マスタバージョン名称
		 * @return true:本番マスタ、false:本番マスタでない
		 */
		public static function isReal(obj:Object) : Boolean
		{
			var code:int = 0;

			if (obj is int) {
				code = new int(obj);
			}
			else if (obj is String) {
				code = getMasterCode(obj.toString());
			}

			if (code <= 0) {
				return false;
			}
			return true;
		}
	}
}
