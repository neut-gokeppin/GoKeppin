package jp.co.ctc.components
{
	import flash.net.URLRequest;
	import flash.net.navigateToURL;
	import jp.co.ctc.entity.MUser;

	import mx.collections.ArrayCollection;
	import mx.collections.IList;
	import mx.controls.Alert;
	import mx.events.FlexEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.mxml.RemoteObject;

	import spark.components.Application;

	public class BaseApplication extends Application
	{
		// メニュー&ログイン画面から選択されたマスタ、従業員コードを取得するように修正する
		// selectMstで画像登録ボタンの有効／無効を制御するため、Bindableタグをつける
		[Bindable]
		public var selectMst:int = 0;
		public var loginUser:String = "";
		protected var srvUser:RemoteObject;
		protected var removedRows:ArrayCollection = new ArrayCollection();
		// 2014/04/07 DA ins start
		protected var removedImgs:ArrayCollection = new ArrayCollection();
		// 2014/04/07 DA ins end
		public var loginUserInfo:MUser; //ログインユーザ情報

		/**
		 * コンストラクタ
		 */
		public function BaseApplication() {
			super();
			this.addEventListener(FlexEvent.CREATION_COMPLETE, baseApplication_creationCompleteHandler);
		}

		/**
		 * アプリケーション初期化
		 * ・ログインユーザー、マスタ選択の取得
		 * ・コンテキストメニューの準備
		 */
		protected function baseApplication_creationCompleteHandler(event:FlexEvent):void
		{
			// srvUser定義
			srvUser = new RemoteObject("MUserService");
			srvUser.getUserCode.addEventListener(ResultEvent.RESULT, getUserCode_resultHandler);
			srvUser.getSelectMst.addEventListener(ResultEvent.RESULT, getSelectMst_resultHandler);
			srvUser.getMUser.addEventListener(ResultEvent.RESULT, getMUser_resultHandler);

			// Debug用 ****************************************
			//　Debugログインさせ、ログインが行われたようにする。
			//srvUser.login("9");
			//srvUser.setSelectMst(0);

			//ユーザーコードを取得
			srvUser.getUserCode();

			// Alertのボタンの幅を変更
			// デフォルトの60だと"キャンセル"の文字が表示しきれない。
			Alert.buttonWidth = 100;
		}

		/**
		 * ユーザーコードの取得後の処理。
		 * ログインチェックも兼ねる。
		 */
		protected function getUserCode_resultHandler(event:ResultEvent):void {
			//ユーザコードがなければログインページに戻す
			if (event.result == null) {
				var request:URLRequest = new URLRequest("login.html");
				navigateToURL(request, "_self");
			} else {
				this.loginUser = event.result.toString();
				//セレクトマスタを取得
				srvUser.getSelectMst();

				// 2016/02/24 DA ins start
				// ログインユーザ情報を取得
				srvUser.getMUser(this.loginUser);
				// 2016/02/24 DA ins end
			}
		}

		/**
		 * セレクトマスタの取得後の処理
		 */
		protected function getSelectMst_resultHandler(event:ResultEvent):void {
			selectMst = parseInt(event.result.toString());
		}

		/**
		 * 画面の終了
		 */
		protected function close():void {
			// マスタメンテナンスメニュー画面に遷移する
			var request:URLRequest = new URLRequest("master_menu.html");
			navigateToURL(request, "_self");
		}

		/**
		 * データが変更されているかチェック
		 */
		public function updateExists(table:ArrayCollection):Boolean {
			var updExists:Boolean = false;

			// 追加/更新データのチェック
			for each (var row:Object in table) {
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
			if (removedRows.length > 0) {
				updExists = true;
			}

			return updExists;
		}
		
		/**
		 * ユーザーログイン情報の取得後の処理。
		 */
		public function getMUser_resultHandler(event:ResultEvent):void {
			this.loginUserInfo = event.result as MUser;
			
			// 指示記号マスタメンテナンス画面の場合
			if(this.className == "bcsign_mainte") {
				if(this.hasOwnProperty("enabledComponent")) {
					//コンポーネントの制御をする
					this.document.enabledComponent();
				}
			
			}

		}
	}
}
