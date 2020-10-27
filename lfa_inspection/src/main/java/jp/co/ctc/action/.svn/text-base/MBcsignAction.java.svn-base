/**
 *
 */
package jp.co.ctc.action;

import javax.annotation.Resource;

import org.seasar.struts.annotation.Execute;
import org.seasar.struts.util.ResponseUtil;

import jp.co.ctc.service.MBcsignService;

/**
 * 以下のURLで接続する。 http://localhost:8400/lfa_inspection/MBcsign/imageListOutput
 *
 * @author
 *
 */
public class MBcsignAction {

	/**
	 * MBcsignServiceクラスを利用します(指示記号マスタ用)
	 */
	@Resource
	protected MBcsignService mBcsignService;

	/**
	 * マスタメニューからセレクトマスタを受け取ります
	 */
	public int selectMst;

	/**
	 * マスタメニューからloginUserを受け取ります
	 */
	public String loginUser;

	/**
	 * マスタバージョン
	 */
	public Integer mstVersion;

	/**
	 * 車種
	 */
	public String bctype;

	/**
	 * 工程名
	 */
	public String groupName;

	/**
	 * 項目名
	 */
	public Integer itemCode;

	/**
	 * 指示記号
	 */
	public String bcSign;

	/**
	 * MsgNo
	 */
	public String msgNo;

	/**
	 * 予約中フラグ
	 */
	public Boolean reserveFlag;

	/**
	 * 検索フラグ
	 */
	public String searchFlag;

	/**
	 * 画像一覧を作成し、ダウンロードします。
	 * @return null
	 */
	@Execute(validator = false)
	public String imageListOutput()
	{

		byte[] imageListFileData = null;

		try {
			if (this.bcSign == null || this.bcSign.length() == 0) {
				this.bcSign = null;
			}

			if (this.msgNo == null || this.msgNo.length() == 0) {
				this.msgNo = null;
			}

			// 画像一覧Excelを作成する
			imageListFileData = this.mBcsignService.imageListOutput(this.selectMst, this.mstVersion, this.bctype, this.groupName, this.itemCode, this.bcSign, this.msgNo, this.reserveFlag, this.searchFlag, this.loginUser);
			if (imageListFileData == null) {
				// 何らかの文字を返す？（返さないと再度、ボタン押下した場合に動かなかったため）
				return "NG";
			}

			// ファイル名は使用されないので固定値
			ResponseUtil.download("imageListDefaultFile", imageListFileData);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}
}
