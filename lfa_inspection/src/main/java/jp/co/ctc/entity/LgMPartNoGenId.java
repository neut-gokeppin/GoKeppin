package jp.co.ctc.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import jp.co.ctc.service.CsvService;

/**
 * 部品
 *
 * @author kaidu
 */
@Entity
@Table(name = "LG_M_PART")
public class LgMPartNoGenId extends AbstractLgMPart implements Serializable {

	/**
	 * Serializableインタフェースの実装に必要。
	 */
	static final long serialVersionUID = 1L;

	/**
	 * 識別子
	 */
	@Id
	public Integer partCode;


	/**
	 *
	 * @param item 値の格納されたオブジェクト配列
	 * @return 生成されたMItem型のインスタンス
	 */
	public LgMPartNoGenId getClass(Object[] item) {

		LgMPartNoGenId it = new LgMPartNoGenId();

		it.mstVer = CsvService.itemCastInteger(item[1]);
		it.partCode = CsvService.itemCastInteger(item[2]);
		it.partName = CsvService.itemCastString(item[3]);
		it.msgDiv = CsvService.itemCastBoolean(item[4]);
		it.msgNo = CsvService.itemCastString(item[5]);

		it.bcPosition = 0;
		it.bcLength = 0;
		if (!item[6].toString().equals("")) {
			it.bcPosition = (Integer.parseInt(item[6].toString()));
		}
		if (!item[7].toString().equals("")) {
			it.bcLength = (Integer.parseInt(item[7].toString()));
		}

//		it.checkFlag = CsvService.itemCastBoolean(item[8]);
//		it.rackAddress = CsvService.itemCastString(item[9]);
		it.deleteFlag = CsvService.itemCastString(item[8]);
		it.sopFlag = CsvService.itemCastString(item[9]);
		it.insertUser = CsvService.itemCastString(item[10]);
		it.updateUser = CsvService.itemCastString(item[11]);

		it.insertDate = CsvService.itemCastTimestamp(item[12]);
		it.updateDate =	CsvService.itemCastTimestamp(item[13]);

		return it;
	}

}
