package jp.co.ctc.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import jp.co.ctc.service.CsvService;

/**
 * 検査項目
 *
 * @author kaidu
 */
@Entity
@Table(name = "M_ITEM")
public class MItemNoGenId extends AbstractMItem implements Serializable {

	/**
	 * Serializableインタフェースの実装に必要。
	 */
	static final long serialVersionUID = 1L;

	/**
	 * 識別子
	 */
	@Id
	public Integer itemCode;

	/**
	 *
	 * @param item 値の格納されたオブジェクト配列
	 * @return 生成されたMItem型のインスタンス
	 */
	public MItemNoGenId getClass(Object[] item) {

		MItemNoGenId it = new MItemNoGenId();

		int i = 0;
		it.mstVer = CsvService.itemCastInteger(item[++i]);
		it.itemCode = CsvService.itemCastInteger(item[++i]);
		it.bctype = CsvService.itemCastString(item[++i]);
		it.itemName = CsvService.itemCastString(item[++i]);
		it.resultDiv = CsvService.itemCastString(item[++i]);
		it.msgDiv = CsvService.itemCastBoolean(item[++i]);
		it.msgNo = CsvService.itemCastString(item[++i]);
		it.bcPosition = CsvService.itemCastInteger(item[++i]);
		it.bcLength = CsvService.itemCastInteger(item[++i]);
		it.tireDiv = CsvService.itemCastBoolean(item[++i]);
		it.okngDiv = CsvService.itemCastBoolean(item[++i]);
		it.tLimit = CsvService.itemCastString(item[++i]);
		it.bLimit = CsvService.itemCastString(item[++i]);
		it.deleteFlag = CsvService.itemCastString(item[++i]);
		it.sopFlag = CsvService.itemCastString(item[++i]);
		it.notes = CsvService.itemCastString(item[++i]);
		it.insertUser = CsvService.itemCastString(item[++i]);
		it.updateUser = CsvService.itemCastString(item[++i]);

		it.insertDate = CsvService.itemCastTimestamp(item[++i]);
		it.updateDate = CsvService.itemCastTimestamp(item[++i]);

		return it;
	}

}
