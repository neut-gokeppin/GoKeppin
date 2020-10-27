/**
 *
 */
package jp.co.ctc.servlet;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;

import jp.co.ctc.util.ServletUtil;

/**
 * @author CJ01786
 *
 */
public class Editdate extends CreateResponse {

	/**
	 * アプリケーション格納先ディレクトリ名
	 */
	protected String apkDir;

	/**
	 * アプリケーションファイル名
	 */
	protected String apkName;


	/* (非 Javadoc)
	 * @see jp.co.ctc.servlet.CreateResponse#getResponse(java.io.ByteArrayInputStream)
	 */
	@Override
	public String getResponse(ByteArrayInputStream inputStream) {
		return this.getResponse(inputStream, this.editDate);
	}

	/**
	 * クライアントへのレスポンスを取得
	 * @param inputStream クライアントから送られてくるストリーム
	 * @param editDate 受信したEditDateパラメータの値
	 * @return クライアントへ送る値
	 */
	public String getResponse(ByteArrayInputStream inputStream, String editDate) {
		String response = "";

		String updateString = this.getFileUpdateTime(this.apkDir, this.apkName);	//返信用の日付文字列
		SimpleDateFormat df = new SimpleDateFormat(ServletUtil.FORMAT_TIME);	//フォーマットの設定

		try {
			//アプリケーションファイルの更新日時を取得する。
			java.util.Date updateTime = df.parse(updateString);	//基準となる更新日
			java.util.Date minTime = new java.util.Date(updateTime.getTime() - ServletUtil.RANGE_EDITDATE);	//誤差
			java.util.Date maxTime = new java.util.Date(updateTime.getTime() + ServletUtil.RANGE_EDITDATE);	//誤差

			if (editDate == null || editDate.equals("")) {
				response = updateString;
			} else {
				java.util.Date rcvEditdate = df.parse(editDate);	//受信した更新日

				//受信した更新日時とファイルの更新日時を比較する。
				if (minTime.before(rcvEditdate) && maxTime.after(rcvEditdate)) {
					//一致する場合OKを返す
					response = ServletUtil.RESPONSE_OK;
				} else {
					//一致しない場合updateStringを返す
					response = updateString;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			response = updateString;
		}

		return response;
	}

}
