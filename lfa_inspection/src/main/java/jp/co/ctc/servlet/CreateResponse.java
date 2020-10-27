/**
 *
 */
package jp.co.ctc.servlet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.text.SimpleDateFormat;

import jp.co.ctc.service.XMLWriteService;
import jp.co.ctc.util.ServletUtil;

import org.seasar.struts.util.ServletContextUtil;



/**
 * サーブレットのレスポンス生成用の基底クラス
 * @author CJ01786
 *
 */
public abstract class CreateResponse {

	/**
	 * クライアントから受け取ったEditDateパラメータの値
	 */
	protected String editDate;

	/**ロック確認を行うかどうかのフラグ。
	 *
	 */
	protected boolean lockCheckFlg;


	/**
	 * XMLを出力するインスタンス
	 */
	protected XMLWriteService srvXmlWriter;

	/**
	 * クライアントへのレスポンスを取得
	 * @param inputStream クライアントから送られてくるストリーム
	 * @return クライアントへ送る値
	 */
	public abstract String getResponse(ByteArrayInputStream inputStream);

	/**
	 * コンストラクタ
	 */
	public CreateResponse() {
		this.editDate = "";
		this.lockCheckFlg = false;
		this.srvXmlWriter = new XMLWriteService();
	}

	/**
	 * EditDatを設定するセッター
	 * @param editdate クライアントから取得したEditDate
	 */
	public void setEditDate(String editdate) {
		this.editDate = editdate;
	}

	/**
	 * 排他の確認有無を設定する。
	 * @param lockFlg 排他を確認するか。
	 */
	public void setLockCheckFlg(boolean lockFlg) {
		this.lockCheckFlg = lockFlg;
	}

	/**
	 * 型が指定したものと一致することを保障されたオブジェクトを返します。
	 * @param object 型を確認したいオブジェクト
	 * @param clazz 保障したい型
	 * @return 型が一致すると保障されたオブジェクト
	 * @throws Exception データ型が異なる例外
	 */
	protected Object getTypeCheckedObject(Object object, Class<?> clazz) throws Exception {
		if (clazz.isInstance(object)) {
			return object;
		} else {
			throw new Exception("データ型が異なります。");
		}
	}

	/**
	 * ファイル名からファイルの更新日を取得する。
	 * @param directory ファイルの格納されたディレクトリ名
	 * @param fileName ファイル名
	 * @return ファイルの更新日の文字列
	 */
	protected String getFileUpdateTime(String directory, String fileName) {
		String imgPath = "";
		imgPath += ServletContextUtil.getServletContext().getRealPath(directory);
		imgPath += "/";
		imgPath += fileName;
		File imgFile = new File(imgPath);
		java.util.Date updateTime = new java.util.Date(imgFile.lastModified());
		SimpleDateFormat df = new SimpleDateFormat(ServletUtil.FORMAT_TIME);

		return df.format(updateTime);
	}
}
