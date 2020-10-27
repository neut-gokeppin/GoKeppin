package jp.co.ctc.service;

import java.io.InputStream;
import java.util.ArrayList;

import org.xml.sax.helpers.DefaultHandler;

import jp.co.ctc.entity.RUploadLogistics;

/**
 * @author CJ01786
 *
 */
public class RUploadLogisticsService  extends XMLReadService {
	/**
	 * 読み込んだ値を格納するArrayList
	 */
	private ArrayList<RUploadLogistics> listUpload;
	/**
	 * 棚照合結果フラグ
	 */
	public String shelfResultFlg;

	/**
	 * 部品読取時間
	 */
	public String partsReadTime;

	/**
	 * 棚読取時間
	 */
	public String shelfReadTime;

	/**
	 * 従業員コード
	 */
	public String userCode;

	/**
	 * 棚QRコード内容
	 */
	public String shelfQrcode;

	/**
	 * 部品QRコード内容
	 */
	public String partsQrcode;

	/**
	 * コンストラクタ
	 */
	public RUploadLogisticsService() {
		this.listUpload = new ArrayList<RUploadLogistics>();
	}

	/**
	 * xmlを受け取り、RUpload型のArrayListに変換する
	 * @param xmlStream XMLの格納されたストリーム
	 * @return 値の格納されたRUploadのArrayList
	 */
	public ArrayList<RUploadLogistics> readXml(InputStream xmlStream) {

		DefaultHandler dh = this;
		if (this.dataCreate(xmlStream, dh)) {
			return this.listUpload;
		} else {
			return null;
		}
	}

	/**
	 * XML文書の開始時の処理
	 */
	public void startDocument() {
		this.listUpload = new ArrayList<RUploadLogistics>();
	}

	/**
	 * フィールド変数を初期化する
	 */
	protected void initializeFields() {
		this.shelfResultFlg = "";
		this.partsReadTime = "";
		this.shelfReadTime = "";
		this.userCode = "";
		this.shelfQrcode = "";
		this.partsQrcode = "";
	}

	/**
	 * リストに値を追加する
	 */
	protected void addList() {

		RUploadLogistics rUpload = new RUploadLogistics();
		rUpload.shelfResultFlg = this.shelfResultFlg;
		rUpload.partsReadTime = this.partsReadTime;
		rUpload.shelfReadTime = this.shelfReadTime;
		rUpload.userCode = this.userCode;
		rUpload.shelfQrcode = this.shelfQrcode;
		rUpload.partsQrcode = this.partsQrcode;

		this.listUpload.add(rUpload);
	}
}
