package jp.co.ctc.util
{
	import flash.utils.ByteArray;
	
	import mx.controls.advancedDataGridClasses.AdvancedDataGridColumn;

	import mx.collections.ArrayCollection;

	public class Utils
	{
		import mx.formatters.DateFormatter;
		import mx.controls.AdvancedDataGrid;

		/**
		 * 日本語用trim。
		 * 全角スペースもtrimします。
		 * @param str 対象の文字列
		 * @return trim後の文字列
		 */
		public static function jTrim(str:String):String {
			// nullはそのまま返却
			if (str == null) {
				return null;
			}
			// スペース削除用の正規パターン
			var pattern:RegExp = /^[\u3000\s]+|[\u3000\s]+$/g;
			return str.replace(pattern, "");
		}

		/**
		 * 日本語用trim。Nullだったときに空白で返します。
		 * @param str 対象の文字列
		 * @return trim後の文字列
		 */
		public static function jTrimToEmpty(str:String):String {
			// nullは空白で返す
			if (str == null) {
				return "";
			}
			// スペース削除用の正規パターン
			var pattern:RegExp = /^[\u3000\s]+|[\u3000\s]+$/g;
			return str.replace(pattern, "");
		}

		/**
		 * 文字列がnull、空、またはスペースのみかどうかをチェックします。
		 * スペースには全角文字も含みます
		 * @param str チェック対象の文字列
		 * @return true:null/空/スペースのみ、false:それ以外
		 */
		public static function isSpaceJ(str:String):Boolean {
			return isEmpty(str) || jTrim(str) == "";
		}

		/**
		 * 文字列がnullまたは空かどうかをチェックします。
		 * @param str チェック対象の文字列
		 * @return true:nullまたは空、false:それ以外
		 */
		public static function isEmpty(str:String):Boolean {
			return str == null || str == "";
		}

		/**
		 * 入力日付の前後チェック
		 * @param beforeDate 日付１
		 * @param afterDate 日付２
		 * @return 日付１の方が新しい日付ならばfalseを返します
		 */
		public static function dateBeforeAfterCheck(beforeDate:Date,afterDate:Date):Boolean{

			return beforeDate.getTime() <= afterDate.getTime();

		}
		
		// 2016/09/18 DA ins start
		/**
		 * 入力日付の前後チェック2
		 * @param beforeDate 日付１
		 * @param afterDate 日付２
		 * @return 日付１ <= 日付２ならばtrueを返します
		 */
		public static function dateBeforeAfterCheck2(beforeDate:Date, afterDate:Date):Boolean{
			
			var formatter:DateFormatter = new DateFormatter();
			formatter.formatString = 'YYYY/MM/DD JJ:NN';
			var beforeDate2:Date = new Date(formatter.format(beforeDate));
			var afterDate2:Date = new Date(formatter.format(afterDate));
			
			return beforeDate2 <= afterDate2;
			
		}
		// 2016/09/18 DA ins end

		/**
		 * "YYYY/MM/DD"で入力された日付をDateに変換して返します
		 * (入力に不備があればNullを返します)
		 * @param strDate 文字列形式の日付
		 * @return "YYYY/MM/DD"の値が入ったDate
		 */
		public static function inputTextToDate(strDate:String):Date
		{
			var df:DateFormatter = new DateFormatter;
			df.formatString = "YYYY/MM/DD";

			//   DateFormatterは入力された日付が
			//   "yyyy/MM/dd"の形式でなければ空を返す。
			if(df.format(strDate)==""){
				return null;
			}

			// [2000//1/1]このような入力であればなぜか通してしまうため
			//もうひとつ制御をつけています
			var arDate:Array = strDate.split("/");
			if(arDate.length!=3){
				return null;
			}

			var theYear:int = parseInt(arDate[0],10);
			var theMonth:int = parseInt(arDate[1],10);
			var theDay:int = parseInt(arDate[2],10);

			//Javaファイルに渡した際に月が１増えるためここで減らしておく
			var date:Date = new Date(theYear,theMonth-1,theDay);

			return date;
		}

		
		// 2016/02/24 DA ins start
		/**
		 * "YYYY/MM/DD JJ:NN"で入力された日付をDateに変換して返します
		 * (入力に不備があればNullを返します)
		 * @param strDate 文字列形式の日付
		 * @param intHour 数字形式の時
		 * @param intMinute 数字形式の分
		 * @return "YYYY/MM/DD JJ:NN"の値が入ったDate
		 */
		public static function inputTextToDateWithTime(strDate:String, intHour:int, intMinute:int):Date
		{
			var df:DateFormatter = new DateFormatter;
			df.formatString = "YYYY/MM/DD JJ:NN";
			
			//   DateFormatterは入力された日付が
			//   "yyyy/MM/dd"の形式でなければ空を返す。
			if(df.format(strDate + " " + String(intHour) + ":" + String(intMinute))==""){
				return null;
			}
			
			// [2000//1/1]このような入力であればなぜか通してしまうため
			//もうひとつ制御をつけています
			var arDate:Array = strDate.split("/");
			if(arDate.length!=3){
				return null;
			}
			
			var theYear:int = parseInt(arDate[0],10);
			var theMonth:int = parseInt(arDate[1],10);
			var theDay:int = parseInt(arDate[2],10);
			
			//Javaファイルに渡した際に月が１増えるためここで減らしておく
			var date:Date = new Date(theYear, theMonth-1, theDay, intHour, intMinute);
			
			return date;
		}
		// 2016/02/24 DA ins end
		
		/**
		 * グリッドのSelectedIndexを指定します。
		 * 同時にSelectIndexのある行を表示するようにグリッドをスクロールします。
		 */
		public static function gridSelectRow(dataGrid:AdvancedDataGrid,index:int):void{
			dataGrid.selectedIndex = index;
			// SelectIndexに表示をあわせます
			dataGrid.scrollToIndex(index);
		}


		/**
		 * 更新者を表示用文字列に変換する
		 */
		public static function updateUserToLabel(data:Object, column:AdvancedDataGridColumn):String {
			return data.updateMUser == null ? data.updateUser : data.updateMUser.userName;
		}


		/**
		 * 更新日を表示用文字列に変換する
		 */
		public static function updateDateToLabel(data:Object, column:AdvancedDataGridColumn):String {
			if (data.updateDate == null)
			{
				return null;
			}

			var formatter:DateFormatter = new DateFormatter();
			formatter.formatString = 'YYYY/MM/DD JJ:NN';
			return formatter.format(data.updateDate);
		}


		/**
		 *  オブジェクトのディープコピーを行います.
		 */
		public static function clone(arg:*):*
		{
			var myBA:ByteArray = new ByteArray();
			myBA.writeObject(arg);
			myBA.position = 0;
			return myBA.readObject();
		}

		/**
		 * メッセージ内の{0}...を置換する。
		 * @param format メッセージ
		 * @param params 置換文字
		 * @return 置換後のメッセージ
		 */
		public static function messageFormat(format:String, ...params:Array):String
		{
			var data:String = format;
			for (var i:int = 0; i < params.length; i++) {
				data = data.replace("{" + i + "}", params[i]);
			}
			return data;
		}

		// 2017/12/01 DA ins start
		/**
		 * データが変更されているかチェック
		 * 
		 * @param gridTable 画面の情報を格納しているテーブル
		 * @param removedTable 削除した情報を格納しているテーブル
		 * @return 判定結果（true:変更あり、false:変更なし）
		 */
		public static function updateExists(gridTable:ArrayCollection, removedTable:ArrayCollection):Boolean
		{
			var updExists:Boolean = false;
			
			// 追加/更新データのチェック
			for each (var row:Object in gridTable) {
				// editDivプロパティを持っていなければチェック対象外
				if (!row.hasOwnProperty("editDiv")) {
					break;
				}
				
				// editDivプロパティをチェック
				if (row.editDiv != null) {
					updExists = true;
					break;
				}
			}
			
			// 削除データのチェック
			if (removedTable.length > 0) {
				updExists = true;
			}
			
			return updExists;
		}

		/**
		 * 撮影画像の工程か判定をする。
		 * @param groupName 工程名称
		 * @return 判定結果（true:工程である、false:工程でない）
		 */
		public static function isJudgmentShotimage(groupName:String):Boolean
		{
			var isResult:Boolean = false;
			var index:int; 

			//うさぎ追いでない場合
			index = groupName.search("G__$");
			if (index == -1) {

				//うさぎ追いの場合
				index = groupName.search("GU[0-9]$");
				if (index == -1) {
					//工程ではない
					isResult = false;
				}
				else {
					isResult = true;
				}
			}
			else {
				isResult = true;
			}

			return isResult;
		}
		// 2017/12/01 DA ins end
	}
}