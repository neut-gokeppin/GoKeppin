package jp.co.ctc.service;

import java.util.ArrayList;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import jp.co.ctc.util.ServletUtil;
import jp.co.ctc.util.Utils;


/**
 * XMLを生成するサービスクラス。
 * @author CJ01786
 */
public class XMLWriteService {

	/**
	 * XML内で1レコードを表すテーブル名。
	 * 初期値は"Table"
	 */
	private String tableName = "Table";

	/**
	 * XML内で1レコードを表すテーブル名をセットする
	 * @param tableName セットするテーブル名
	 */
	public void setTablaName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * 車両・工程情報
	 */
	private String bodyGroup = "";

	/**
	 * Tableタグ単位の格納データタグのリスト
	 */
	private ArrayList<String> listTable;


	/**
	 * コンストラクタ
	 */
	public XMLWriteService() {
		this.listTable = new ArrayList<String>();
	}

	/**
	 * 新しいデータセットを作成します。
	 */
	public void createDataset() {
		this.listTable.clear();
	}

	/**
	 * 新しいデータテーブルを作成します。
	 */
	public void ceateTable() {
		this.listTable.add("");
	}

	/**
	 * データテーブルの作成を取り消します。
	 */
	public void rollbackTable() {
		this.listTable.remove(this.listTable.size() - 1);
	}

	/**
	 * テーブルに要素と値を追加します。
	 * @param qName 追加する要素名
	 * @param value 追加する値
	 */
	public void addData(String qName, Object value) {
		String addString = "";	//追加する文字列
		String addValue;	//実際に追加する値
		int listIdx = 0;	//リストの挿入先インデックス
		String listValue = "";	//リストに設定されている値

		if (value == null) {
			//値がnullの場合、空値を設定する。
			addValue = "";
		} else {
			//nullでなければ、文字列化して値を設定する
			addValue = value.toString().trim();
		}

		//タグの生成
		addString = String.format("<%1$s>%2$s</%1$s>" + ServletUtil.LINE_SEPARATOR, qName, Utils.escapeXml(addValue));

		//リストへの追加
		listIdx = this.listTable.size() - 1;
		listValue = this.listTable.get(listIdx);
		this.listTable.set(listIdx, listValue + addString);
	}


	/**
	 * 車両・工程の要素を追加
	 * @param qName 追加する要素名
	 * @param value 追加する値
	 */
	public void addBodyGroupData(String qName, Object value) {
		// nullは空文字にする
		String strValue = ObjectUtils.toString(value).trim();

		// タグの生成
		String line = String.format(
				"  <%1$s>%2$s</%1$s>" + ServletUtil.LINE_SEPARATOR,
				qName, Utils.escapeXml(strValue));

		this.bodyGroup += line;
	}


	/**
	 * 検査順を採番し、要素を追加。検査データ生成用。
	 *
	 * 検査順未設定の場合など"0"が複数項目入っていると
	 * Android側で検査内容が正しく表示されない問題がある。
	 * その対策として、"ordersignNo"がXML内でユニーク
	 * になるように採番してAndroidに送信する。
	 */
	public void addOrderSignNo() {
		addData("ordersignNo", listTable.size());
	}


	/**
	 * XML文書を取得
	 * @return 生成されたXML文書
	 */
	public String getXMLData() {
		StringBuilder xml = new StringBuilder();	//戻り値として返すXML

		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + ServletUtil.LINE_SEPARATOR);
		xml.append("<DataSet>" + ServletUtil.LINE_SEPARATOR);

		// 車両・工程情報があれば追加
		if (StringUtils.isNotEmpty(bodyGroup)) {
			xml.append(" <BodyGroup>" + ServletUtil.LINE_SEPARATOR);
			xml.append(bodyGroup);
			xml.append(" </BodyGroup>" + ServletUtil.LINE_SEPARATOR);
		}

		for (String tblData : this.listTable) {
			xml.append(" <" + tableName + ">" + ServletUtil.LINE_SEPARATOR);
			xml.append(tblData);
			xml.append(" </" + tableName + ">" + ServletUtil.LINE_SEPARATOR);
		}

		xml.append("</DataSet>");

		return xml.toString();
	}


	/**
	 * テーブルのレコード件数取得
	 * @return テーブルのレコード件数
	 */
	public int getTableSize() {
		return listTable.size();
	}
}
