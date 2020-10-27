/**
 *
 */
package jp.co.ctc.service;

import java.io.InputStream;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import jp.co.ctc.entity.RUploadAssembly;

/**
 * @author CJ01786
 *
 */
public class RUploadAssemblyService  extends XMLReadService {
	/**
	 * 読み込んだ値を格納するArrayList
	 */
	private ArrayList<RUploadAssembly> listUpload;
	/**
	 * 棚照合結果フラグ
	 */
	public String shelfResultFlg;

	/**
	 * 棚照合時間
	 */
	public String shelfResultTime;

	/**
	 * 部品チェック結果フラグ
	 */
	public String partsResultFlg;

	/**
	 * 部品チェック時間
	 */
	public String partsResultTime;

	/**
	 * 従業員コード
	 */
	public String userCode;

	/**
	 * ボデーNo.
	 */
	public String bodyNo;

	/**
	 * 受信日
	 */
	public String recvDay;

	/**
	 * 部品Code
	 */
	public String partsCode;

	/**
	 * 棚QRコード内容
	 */
	public String shelfQrrcode;

	/**
	 * 部品QRコード内容
	 */
	public String partsQrcode;

	/**
	 * SPS台車コード
	 */
	public String spsCode;

	/**
	 * 処理すべきブロックであるかのフラグ
	 */
	private boolean blockFlg;

	/**
	 * コンストラクタ
	 */
	public RUploadAssemblyService() {
		this.listUpload = new ArrayList<RUploadAssembly>();
		this.blockFlg = false;
	}

	/**
	 * xmlを受け取り、RUpload型のArrayListに変換する
	 * @param xmlStream XMLの格納されたストリーム
	 * @return 値の格納されたRUploadのArrayList
	 */
	public ArrayList<RUploadAssembly> readXml(InputStream xmlStream) {

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
		this.listUpload = new ArrayList<RUploadAssembly>();
	}

	/**
	 * 要素の開始時の処理
	 * @param uri 名前空間URI
	 * @param localName ローカル名
	 * @param qName 要素名
	 * @param atts 属性リスト
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) {

		super.startElement(uri, localName, qName, atts);

		if (qName.equals("Datasource")) {
			//DataSet要素を読み込んだときに処理をしないためフラグをセットする。。
			this.blockFlg = false;
		}
		if (qName.equals("NewDataSet")) {
			//NewDataSe要素を読み込んだとき処理するためのフラグをセットする。
			this.blockFlg = true;
		}

	}

	/**
	 * 取得したテキストデータの処理
	 * @param ch 文字が格納されたchar型配列
	 * @param start 配列に格納された文字の開始位置
	 * @param length 配列に格納された文字数
	 */
	@Override
	public void characters(char[] ch, int start, int length) {
		if (blockFlg) {
			//処理対象ブロックのときのみ処理を行う。
			super.characters(ch, start, length);
		}
	}

	/**
	 * フィールド変数を初期化する
	 */
	protected void initializeFields() {
		this.shelfResultFlg = "";
		this.shelfResultTime = "";
		this.partsResultFlg = "";
		this.partsResultTime = "";
		this.userCode = "";
		this.bodyNo = "";
		this.recvDay = "";
		this.partsCode = "";
		this.shelfQrrcode = "";
		this.partsQrcode = "";
		this.spsCode = "";
	}

	/**
	 * リストに値を追加する
	 */
	protected void addList() {
		if (blockFlg) {
			//処理対象ブロックのときのみ処理を行う
			RUploadAssembly rUpload = new RUploadAssembly();
			rUpload.shelfResultFlg = this.shelfResultFlg;
			rUpload.shelfResultTime = this.shelfResultTime;
			rUpload.partsResultFlg = this.partsResultFlg;
			rUpload.partsResultTime = this.partsResultTime;
			rUpload.userCode = this.userCode;
			rUpload.bodyNo = this.bodyNo;
			rUpload.recvDay = this.recvDay;
			rUpload.shelfQrrcode = this.shelfQrrcode;
			rUpload.partsQrcode = this.partsQrcode;
			try {
				rUpload.partsCode = Integer.parseInt(this.partsCode);
				rUpload.spsCode = Integer.parseInt(this.spsCode);
			} catch (Exception e) {
				rUpload.partsCode = null;
				rUpload.spsCode = null;
			}

			this.listUpload.add(rUpload);
		}
	}
}
