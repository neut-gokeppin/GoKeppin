package jp.co.ctc.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import jp.co.ctc.service.CsvService;

/**
 * 指示記号マスタ。
 *
 * @author kaidu
 */
@Entity
@Table(name = "M_BCSIGN")
public class MBcsignNoGenId extends AbstractMBcsign implements Serializable {

	/**
	 * Serializableインタフェースの実装に必要。
	 */
	static final long serialVersionUID = 1L;

	/**
	 * 識別子。
	 */
	@Id
	public Integer signCode;

	/**
	 *
	 * @param item 値の格納されたオブジェクト配列
	 * @return MBcsignインスタンス
	 */
	public MBcsignNoGenId getClass(Object[] item) {

		MBcsignNoGenId sg = new MBcsignNoGenId();

		int i = 0;
		sg.mstVer = CsvService.itemCastInteger(item[++i]);
		sg.signCode = CsvService.itemCastInteger(item[++i]);
		sg.itemCode = CsvService.itemCastInteger(item[++i]);
		sg.bcSign = CsvService.itemCastString(item[++i]);
		sg.signContents = CsvService.itemCastString(item[++i]);
		sg.dummySign = CsvService.itemCastString(item[++i]);
		sg.signOrder = CsvService.itemCastInteger(item[++i]);
		sg.fileName = CsvService.itemCastString(item[++i]);
		sg.tLimit = CsvService.itemCastString(item[++i]);
		sg.bLimit = CsvService.itemCastString(item[++i]);
		// 2016/02/24 DA ins start
		sg.reserveFlag = CsvService.itemCastString(item[++i]);
		sg.reserveUser = CsvService.itemCastString(item[++i]);
		sg.reserveDate = CsvService.itemCastTimestamp(item[++i]);
		sg.sopDeleteFlag = CsvService.itemCastString(item[++i]);
		// 2016/02/24 DA ins end
		sg.deleteFlag = CsvService.itemCastString(item[++i]);
		sg.sopFlag = CsvService.itemCastString(item[++i]);
		sg.notes = CsvService.itemCastString(item[++i]);
		sg.insertUser = CsvService.itemCastString(item[++i]);
		sg.updateUser = CsvService.itemCastString(item[++i]);
		sg.insertDate = CsvService.itemCastTimestamp(item[++i]);
		sg.updateDate = CsvService.itemCastTimestamp(item[++i]);

		return sg;
	}
}