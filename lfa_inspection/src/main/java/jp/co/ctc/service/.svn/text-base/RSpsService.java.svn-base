/**
 *
 */
package jp.co.ctc.service;

import java.io.InputStream;
import java.util.ArrayList;

import org.xml.sax.helpers.DefaultHandler;

import jp.co.ctc.entity.RSps;

/**
 * @author CJ01786
 *
 */
public class RSpsService extends XMLReadService {

	/**
	 * 読み込んだ値を格納するArrayList
	 */
	private ArrayList<RSps> listSps;


	/**
	 * bodyNoを保持しておく変数
	 */
	public String bodyNo = "";

	/**
	 * spsCodeを保持しておくArrayList
	 */
	private ArrayList<String> spsCodes;

	/**
	 * spsCodeを保持する変数
	 */
	public String spsCode;

	/**
	 * recvDayを保持しておく変数
	 */
	public String recvDay = "";

	/**
	 * userCodeを保持しておく変数
	 */
	public String userCode = "";

	/**
	 * コンストラクタ
	 */
	public RSpsService() {
		this.listSps = new ArrayList<RSps>();
		this.spsCodes = new ArrayList<String>();
	}

	/**
	 * xmlを受け取り、RSps型のArrayListに変換する
	 * @param xmlStream XMLの格納されたストリーム
	 * @return 値の格納されたRSpsのArrayList
	 */
	public ArrayList<RSps> readXml(InputStream xmlStream) {
		DefaultHandler dh = this;
		if (this.dataCreate(xmlStream, dh)) {
			return this.listSps;
		} else {
			return null;
		}

	}

	/**
	 * XML文書の開始時の処理
	 */
	public void startDocument() {
		this.listSps = new ArrayList<RSps>();
		this.spsCodes = new ArrayList<String>();
	}
	/**
	 * 取得したテキストデータの処理
	 * @param ch 文字が格納されたchar型配列
	 * @param start 配列に格納された文字の開始位置
	 * @param length 配列に格納された文字数
	 */
	public void characters(char[] ch, int start, int length) {
		super.characters(ch, start, length);

		String str = new String(ch, start, length).trim();
		if (str.length() > 0) {
			if (this.elemName.equals("spsCode")) {
				this.spsCodes.add(this.spsCode);
			}
		}
	}

	/**
	 * フィールド変数を初期化する
	 */
	protected void initializeFields() {
		this.bodyNo = "";
		this.spsCodes.clear();
		this.recvDay = "";
		this.userCode = "";
	}

	/**
	 * リストに値を追加する
	 */
	protected void addList() {
		for (String gCode : this.spsCodes) {
			RSps rSps = new RSps();
			rSps.bodyNo = this.bodyNo;
			rSps.spsCode = Integer.parseInt(gCode);
			rSps.recvDay = this.recvDay;
			rSps.userCode = this.userCode;

			this.listSps.add(rSps);
		}
	}
}
