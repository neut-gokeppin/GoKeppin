package jp.co.ctc.service;

import java.io.InputStream;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import jp.co.ctc.entity.RUploadUnlock;

/**
 *
 * @author CJ01786
 *
 */
public class RUploadUnlockService extends XMLReadService {

	/**
	 * 読み込んだ値を格納するArrayList
	 */
	private ArrayList<RUploadUnlock> listUpload;

	/**
	 * ボデーNo.
	 */
	public String bodyNo;

	/**
	 * 受信日
	 */
	public String recvDay;

	/**
	 * spsCodeを保持しておくArrayList
	 */
	private ArrayList<String> spsCodes;

	/**
	 * SPS台車コード
	 */
	public String spsCode;

	/**
	 * 従業員コード
	 */
	public String userCode;

	/**
	 * 処理すべきブロックであるかのフラグ
	 */
	private boolean blockFlg;

	/**
	 * コンストラクタ
	 */
	public RUploadUnlockService() {
		this.listUpload = new ArrayList<RUploadUnlock>();
		this.blockFlg = false;
		this.spsCodes = new ArrayList<String>();
	}

	/**
	 * xmlを受け取り、RUpload型のArrayListに変換する
	 * @param xmlStream XMLの格納されたストリーム
	 * @return 値の格納されたRUploadのArrayList
	 */
	public ArrayList<RUploadUnlock> readXml(InputStream xmlStream) {

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
	@Override
	public void startDocument() {
		this.listUpload = new ArrayList<RUploadUnlock>();
		this.spsCodes = new ArrayList<String>();
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

		if (qName.equals("NewDataSet")) {
			//NewDataSet要素を読み込んだときに処理をしないためフラグをセットする。。
			this.blockFlg = false;
		}
		if (qName.equals("Datasource")) {
			//DataSet要素を読み込んだとき処理するためのフラグをセットする。
			this.blockFlg = true;
		}

	}
	/**
	 * フィールド変数を初期化する
	 */
	@Override
	protected void initializeFields() {
		this.bodyNo = "";
		this.recvDay = "";
		this.spsCodes.clear();
		this.userCode = "";
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

			String str = new String(ch, start, length).trim();
			if (str.length() > 0) {
				if (this.elemName.equals("spsCode")) {
					this.spsCodes.add(this.spsCode);
				}
			}
		}
	}

	/**
	 * リストに値を追加する
	 */
	@Override
	protected void addList() {
		if (blockFlg) {
			//処理対象ブロックのときのみ処理を行う
			for (String gCode : this.spsCodes) {
				RUploadUnlock rUpload = new RUploadUnlock();
				rUpload.bodyNo = this.bodyNo;
				try {
					rUpload.spsCode = Integer.parseInt(gCode);
				} catch (Exception e) {
					rUpload.spsCode = null;
				}
				rUpload.recvDay = this.recvDay;
				rUpload.userCode = this.userCode;

				this.listUpload.add(rUpload);
			}

		}
	}

}
