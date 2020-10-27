
package jp.co.ctc.service;

import java.io.InputStream;
import java.util.ArrayList;

import org.xml.sax.helpers.DefaultHandler;

import jp.co.ctc.entity.RUpload;

/**
 * @author CJ01786
 *
 */
public class RUploadService extends XMLReadService {

	/**
	 * 読み込んだ値を格納するArrayList
	 */
	private ArrayList<RUpload> listUpload;

	/**
	 * resultFlgを保持しておく変数
	 */
	public String resultFlg;

	/**
	 * userCodeを保持しておく変数
	 */
	public String userID;

	/**
	 * orderTimeを保持しておく変数
	 */
	public String orderTime;

	/**
	 * ngContentsを保持しておく変数
	 */
	public String ngContents;

	/**
	 * bodyNoを保持しておく変数
	 */
	public String bodyNo;

	/**
	 * recvDayを保持しておく変数
	 */
	public String recvDay;

	/**
	 * itemCodeを保持しておく変数
	 */
	public String itemCode;

	/**
	 * groupCodeを保持しておく変数
	 */
	public String groupCode;

	/**
	 * loDateを保持しておく変数
	 */
	public String loDate;

	/**
	 * idnoを保持しておく変数
	 */
	public String idno;

	/**
	 * inputDataを保持しておく変数
	 */
	public String inputData;

	// 2016/02/24 DA ins start
	/**
	 * selectNumberを保持しておく変数
	 */
	public String selectNumber;
	// 2016/02/24 DA ins end

	/**
	 * コンストラクタ
	 */
	public RUploadService() {
		this.listUpload = new ArrayList<RUpload>();
	}

	/**
	 * xmlを受け取り、RUpload型のArrayListに変換する
	 * @param xmlStream XMLの格納されたストリーム
	 * @return 値の格納されたRUploadのArrayList
	 */
	public ArrayList<RUpload> readXml(InputStream xmlStream) {

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
		this.listUpload = new ArrayList<RUpload>();
	}

	/**
	 * フィールド変数を初期化する
	 */
	protected void initializeFields() {
		this.resultFlg = "";
		this.userID = "";
		this.orderTime = "";
		this.ngContents = "";
		this.bodyNo = "";
		this.recvDay = "";
		this.itemCode = "";
		this.groupCode = "";
		this.idno = "";
		this.loDate = "";
		this.inputData = "";
		// 2016/02/24 DA ins start
		this.selectNumber = "";
		// 2016/02/24 DA ins end
	}

	/**
	 * リストに値を追加する
	 */
	protected void addList() {

		RUpload rUpload = new RUpload();
		rUpload.resultFlg = this.resultFlg;
		rUpload.userID = this.userID;
		rUpload.ordertime = this.orderTime;
		rUpload.ngContents = this.ngContents;
		rUpload.bodyNo = this.bodyNo;
		rUpload.recvDay = this.recvDay;
		rUpload.idno = this.idno;
		rUpload.loDate = this.loDate;
		rUpload.inputData = this.inputData;
		try {
			// XML中のitemCodeの値は『mstVer-itemCode』のため、
			// "-"で分割する
			// 仮マスタのマスタバージョンはマイナスの場合もある
			// 2016/02/24 DA upd start
			String mstVer = "";
			String code = "";
			int index = this.itemCode.lastIndexOf("-");
			if (index != -1) {
				mstVer = this.itemCode.substring(0, index);
				code = this.itemCode.substring(index + 1);
			}
			rUpload.mstVer = Integer.parseInt(mstVer);
			rUpload.itemCode = Integer.parseInt(code);
			//String[] itemCodeArray = this.itemCode.split("-");
			//rUpload.mstVer = Integer.parseInt(itemCodeArray[0]);
			//rUpload.itemCode = Integer.parseInt(itemCodeArray[1]);
			// 2016/02/24 DA upd end
			rUpload.groupCode = Integer.parseInt(this.groupCode);
			// 2016/02/24 DA ins start
			rUpload.selectNumber = Integer.parseInt(this.selectNumber);
			// 2016/02/24 DA ins end
		} catch (Exception e) {
			rUpload.mstVer = null;
			rUpload.itemCode = null;
			rUpload.groupCode = null;
			// 2016/02/24 DA ins start
			rUpload.selectNumber = null;
			// 2016/02/24 DA ins end
		}

		this.listUpload.add(rUpload);
	}


}
