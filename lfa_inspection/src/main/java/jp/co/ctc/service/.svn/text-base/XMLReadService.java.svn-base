/**
 *
 */
package jp.co.ctc.service;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.FieldUtil;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XMLからエンティティを生成するクラスのスーパークラス
 * @author CJ01786
 *
 */
public abstract class XMLReadService extends DefaultHandler {

	/**
	 * 処理中の要素名を保持しておく変数
	 */
	protected String elemName = "";

	/**
	 * xmlを受け取り処理を行う。
	 * @param xmlStream XMLの格納されたストリーム
	 * @param dh 使用するデフォルトハンドラー
	 * @return 処理成功の可否
	 */
	protected boolean dataCreate(InputStream xmlStream, DefaultHandler dh) {
		try {
			SAXParserFactory saxFactory = SAXParserFactory.newInstance();
			SAXParser sax = saxFactory.newSAXParser();
			XMLReader xmlReader = sax.getXMLReader();

			xmlReader.setContentHandler(dh);

			xmlReader.parse(new InputSource(xmlStream));

		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
			return false;
		} catch (SAXException e1) {
			e1.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * フィールド変数を初期化する
	 */
	protected void initializeFields() {

	}

	/**
	 * リストに値を追加する
	 */
	protected void addList() {

	}

	/**
	 * XML文書の開始時の処理
	 */
	public void startDocument() {
	}
	/**
	 * XML文書の終了時の処理
	 */
	public void endDocument() {

	}
	/**
	 * 要素の開始時の処理
	 * @param uri 名前空間URI
	 * @param localName ローカル名
	 * @param qName 要素名
	 * @param atts 属性リスト
	 */
	public void startElement(String uri, String localName, String qName, Attributes atts) {

		this.elemName = qName;
		if (qName.equals("Table")) {
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
	public void endElement(String uri, String localName, String qName) {
		if (qName.equals("Table")) {
			//Tableを読み終えたあと、リストに追加していく。
			this.addList();
		}
	}

	/**
	 * 取得したテキストデータの処理
	 * @param ch 文字が格納されたchar型配列
	 * @param start 配列に格納された文字の開始位置
	 * @param length 配列に格納された文字数
	 */
	public void characters(char[] ch, int start, int length) {
		String str = new String(ch, start, length).trim();
		if (str.length() > 0) {
			try {
				java.lang.reflect.Field field =
					ClassUtil.getField(
							this.getClass(),
							this.elemName
							);
				FieldUtil.set(field, this, str);
			} catch (Exception e) {
				e.getStackTrace();
			}
		}
	}
}
