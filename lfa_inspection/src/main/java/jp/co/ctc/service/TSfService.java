package jp.co.ctc.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import jp.co.ctc.entity.TSf;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.service.S2AbstractService;

/**
 * @author CJ01786
 *
 */
public class TSfService extends S2AbstractService<TSf> {

	/**
	 * SF基本テーブルから、アイデントNo、ラインオフ計画日をもとにデータを1行取出す。
	 * @param idno アイデントNo
	 * @param loDate ラインオフ計画日
	 * @return TSfオブジェクト
	 */
	public TSf selectById(String idno, String loDate) {
		return select().id(idno, loDate).getSingleResult();
	}


	/**
	 * t_sf, t_sfbc*_*テーブルにデータ登録
	 * @param bcdataMap 登録するデータ
	 */
	public void createTSf(HashMap<String, String> bcdataMap) {
		// t_sf, t_sfbc*_*テーブルにデータ登録するため、
		// 登録用データを格納するMapの初期化
		HashMap<String, ArrayList<ArrayList<String>>> tSf = initializeTSfMap();

		// 登録用データをMapに格納
		for (Entry<String, String> entry : bcdataMap.entrySet()) {

			String msgno = entry.getKey();
			String value = entry.getValue();

			// #で始まる行はDBに登録しない
			if (msgno.startsWith("#")) {
				continue;
			}

			String table = getTableName(msgno);

			tSf.get(table).get(0).add(msgno);
			tSf.get(table).get(1).add(value);

		}

		// t_sf, t_sfbc*_*テーブルにデータ投入
		create(bcdataMap, tSf);
	}

	/**
     * t_sf, t_sfbc*_*テーブル用のmapを生成し初期化します。
     *
     * @return t_sf, t_sfbc*_*テーブル用のmap
     */
    private HashMap<String, ArrayList<ArrayList<String>>> initializeTSfMap() {

    	String[] tableNames = {
    			"t_sf", "t_sfbc1_1", "t_sfbc1_2", "t_sfbc2_1", "t_sfbc2_2",
    			"t_sfbc3_1", "t_sfbc3_2", "t_sfbc4_1", "t_sfbc4_2"};

    	HashMap<String, ArrayList<ArrayList<String>>> tSf = new HashMap<String, ArrayList<ArrayList<String>>>();

    	for (String table : tableNames) {
    	    tSf.put(table, new ArrayList<ArrayList<String>>());
    	    tSf.get(table).add(new ArrayList<String>());
    	    tSf.get(table).add(new ArrayList<String>());
        }

	    return tSf;
    }



	/**
     * メッセージNoより、登録先のテーブル名（t_sf, t_sfbc*_*）を取得する。
     * @param msgno メッセージNo
     * @return 登録先のテーブル名
     */
    private String getTableName(String msgno) {

    	String table = "";
	    if (msgno.matches("B.\\d\\d") && msgno.compareTo("B001") >= 0 && msgno.compareTo("B499") <= 0) {
	    	table = "t_sfbc1_1";
	    } else if (msgno.matches("B.\\d\\d") && msgno.compareTo("B500") >= 0 && msgno.compareTo("B899") <= 0) {
	    	table = "t_sfbc1_2";
	    } else if (msgno.matches("B.\\d\\d") && msgno.compareTo("B900") >= 0 && msgno.compareTo("BD98") <= 0) {
	    	table = "t_sfbc2_1";
	    } else if (msgno.matches("B.\\d\\d") && msgno.compareTo("BD99") >= 0 && msgno.compareTo("BH99") <= 0) {
	    	table = "t_sfbc2_2";
	    } else if (msgno.matches("B.\\d\\d") && msgno.compareTo("BI00") >= 0 && msgno.compareTo("BM98") <= 0) {
	    	table = "t_sfbc3_1";
	    } else if (msgno.matches("B.\\d\\d") && msgno.compareTo("BM99") >= 0 && msgno.compareTo("BQ99") <= 0) {
	    	table = "t_sfbc3_2";
	    } else if (msgno.matches("B.\\d\\d") && msgno.compareTo("BR00") >= 0 && msgno.compareTo("BV98") <= 0) {
	    	table = "t_sfbc4_1";
	    } else if (msgno.matches("B.\\d\\d") && msgno.compareTo("BV99") >= 0 && msgno.compareTo("BZ99") <= 0) {
	    	table = "t_sfbc4_2";
	    } else {
	    	table = "t_sf";
	    }

	    return table;
    }

	/**
     * t_sf, t_sfbc*_*テーブルにデータ投入
     *
     * @param bcdataMap BCデータ
     * @param tSf 投入するデータ
     */
    private void create(HashMap<String, String> bcdataMap, HashMap<String, ArrayList<ArrayList<String>>> tSf) {

	    String idno = bcdataMap.get("IDNO");
	    String loDate = bcdataMap.get("LO_DATE");

	    for (Entry<String, ArrayList<ArrayList<String>>> entry : tSf.entrySet()) {
	    	String table = entry.getKey();
	        ArrayList<String> columnList = entry.getValue().get(0);
	        ArrayList<String> valueList = entry.getValue().get(1);

	        if (!table.equals("t_sf")) {
	        	columnList.add("idno");
	        	valueList.add(idno);

	        	columnList.add("lo_date");
	        	valueList.add(loDate);
	        }

	        // delete文構築
	        String deleteSql = "delete from " + table + " where idno=? and lo_date=?";

	        // insert文構築
	        String insertSql
	        		= "insert into " + table + " (" + StringUtils.join(columnList, ',') + ") \n"
	        		+ "values ('" + StringUtils.join(valueList, "','") + "')";

	        // sql実行
	        jdbcManager.updateBySql(deleteSql, String.class, String.class).params(idno, loDate).execute();
	        jdbcManager.updateBySql(insertSql).execute();
	    }
    }

}
