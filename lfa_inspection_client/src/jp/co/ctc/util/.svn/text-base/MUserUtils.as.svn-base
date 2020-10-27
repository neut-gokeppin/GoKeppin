package jp.co.ctc.util
{
	import jp.co.ctc.entity.MUser;

	/**
	 * 従業員マスタの共通処理
	 */
	public class MUserUtils
	{
		/**
		 * アクセス権限が検査員かどうか判定する
		 * @param user ユーザ情報
		 * @return true:検査員、false:検査員でない
		 */
		public static function isExaminer(user:MUser) : Boolean
		{
			if (user.authority == "0") {
				return true;
			}
			return false;
		}

		/**
		 * アクセス権限が検査職制本番不可かどうか判定する
		 * @param user ユーザ情報
		 * @return true:検査職制本番不可、false:検査職制本番不可でない
		 */
		public static function OfficeOrganizationNot(user:MUser) : Boolean
		{
			if (user.authority == "1") {
				return true;
			}
			return false;
		}

		/**
		 * アクセス権限が検査職制本番可かどうか判定する
		 * @param user ユーザ情報
		 * @return true:検査職制本番可、false:検査職制本番可でない
		 */
		public static function OfficeOrganization(user:MUser) : Boolean
		{
			if (user.authority == "2") {
				return true;
			}
			return false;
		}

		/**
		 * アクセス権限がマスタ管理者かどうか判定する
		 * @param user ユーザ情報
		 * @return true:マスタ管理者、false:マスタ管理者でない
		 */
		public static function isAdministrator(user:MUser) : Boolean
		{
			if (user.authority == "3") {
				return true;
			}
			return false;
		}
	}
}
