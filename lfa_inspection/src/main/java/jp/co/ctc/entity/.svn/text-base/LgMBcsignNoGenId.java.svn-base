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
@Table(name = "LG_M_BCSIGN")
public class LgMBcsignNoGenId extends AbstractLgMBcsign implements Serializable {

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
	public LgMBcsignNoGenId getClass(Object[] item) {

		LgMBcsignNoGenId sg = new LgMBcsignNoGenId();

		sg.mstVer = CsvService.itemCastInteger(item[1]);
		sg.signCode = CsvService.itemCastInteger(item[2]);
		sg.partCode = CsvService.itemCastInteger(item[3]);
		sg.bcSign = CsvService.itemCastString(item[4]);
		sg.supplierName = CsvService.itemCastString(item[5]);
		sg.backNo = CsvService.itemCastString(item[6]);
		sg.partNo = CsvService.itemCastString(item[7]);
		sg.identName = CsvService.itemCastString(item[8]);
		sg.fileName = CsvService.itemCastString(item[9]);
		sg.notuseFlag = CsvService.itemCastBoolean(item[10]);
		sg.deleteFlag = CsvService.itemCastString(item[11]);
		sg.sopFlag = CsvService.itemCastString(item[12]);
		sg.insertUser = CsvService.itemCastString(item[13]);
		sg.updateUser = CsvService.itemCastString(item[14]);

		sg.insertDate = CsvService.itemCastTimestamp(item[15]);
		sg.updateDate = CsvService.itemCastTimestamp(item[16]);

		sg.checkFlag = CsvService.itemCastBoolean(item[17]);
		sg.rackAddress = CsvService.itemCastString(item[18]);

		return sg;
	}
}