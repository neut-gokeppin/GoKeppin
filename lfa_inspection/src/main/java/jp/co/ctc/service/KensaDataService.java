
package jp.co.ctc.service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author CJ01615
 *
 */
public class KensaDataService extends XMLReadService {

	/**
	 * 読み込んだ値を格納するMap
	 */
	private Map<String, String> list;

	/**
	 * 検査項目コード
	 */
	public String itemCode;

	/**
	 * ダミー記号
	 */
	public String bcSign2;


	/**
	 * xmlを受け取り、RUpload型のArrayListに変換する
	 * @param xmlStream XMLの格納されたストリーム
	 * @return 値の格納されたRUploadのMap
	 */
	public Map<String, String> readXml(InputStream xmlStream) {

		DefaultHandler dh = this;
		if (this.dataCreate(xmlStream, dh)) {
			return this.list;
		} else {
			return null;
		}
	}

	/**
	 * XML文書の開始時の処理
	 */
	public void startDocument() {
		this.list = new HashMap<String, String>();
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

		this.elemName = qName;
		if (qName.equals("InspecItem")) {
			//Table要素を読み込んだとき、初期化する。
			this.initializeFields();
		}

	}

	/**
	 * 要素の終了時の処理
	 * @param uri 名前空間URI
	 * @param localName ローカル名
	 * @param qName 要素名
	 */
	@Override
	public void endElement(String uri, String localName, String qName) {
		if (qName.equals("InspecItem")) {
			//Tableを読み終えたあと、リストに追加していく。
			this.addList();
		}
	}

	/**
	 * フィールド変数を初期化する
	 */
	protected void initializeFields() {
		this.itemCode = "";
		this.bcSign2 = "";
	}

	/**
	 * リストに値を追加する
	 */
	protected void addList() {
		// XML中のitemCodeの値は『mstVer-itemCode』のため、
		// "-"で分割して、本当のitemCodeだけにする
		// 仮マスタのマスタバージョンはマイナスの場合もある
		// 2016/02/24 DA upd start
		String code = "";
		int index = itemCode.lastIndexOf("-");
		if (index != -1) {
			code = itemCode.substring(index + 1);
			this.list.put(code, bcSign2);
		}
		//this.list.put(itemCode.split("-")[1], bcSign2);
		// 2016/02/24 DA upd end
	}
}
