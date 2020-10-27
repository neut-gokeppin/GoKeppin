package jp.co.ctc.entity;

import jp.co.ctc.service.CsvService;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * 検査グループ
 *
 * @author kaidu
 *
 */
@Entity
@Table(name = "M_GROUP")
public class MGroupNoGenId extends AbstractMGroup implements Serializable {

	/**
	 * Serializableインタフェースの実装に必要。
	 */
	static final long serialVersionUID = 1L;

	/**
	 * 識別子
	 */
	@Id
	public Integer groupCode;

	/**
	 * MGroup型のインスタンスを生成する
	 *
	 * @param item 格納する値の配列
	 * @return MGroup型のインスタンス
	 */
	public MGroupNoGenId getClass(final Object[] item) {

		MGroupNoGenId gp = new MGroupNoGenId();

		int i = 0;
		gp.mstVer = CsvService.itemCastInteger(item[++i]);
		gp.groupCode = CsvService.itemCastInteger(item[++i]);
		gp.bctype = CsvService.itemCastString(item[++i]);
		gp.groupNo = CsvService.itemCastString(item[++i]);
		gp.groupName = CsvService.itemCastString(item[++i]);
		// 2014/04/07 DA ins start
		gp.line = CsvService.itemCastString(item[++i]);
		gp.area = CsvService.itemCastString(item[++i]);
		// 2014/04/07 DA ins end
		// 2016/02/24 DA ins start
		gp.nonDisplayFlag = CsvService.itemCastBoolean(item[++i]);
		// 2016/02/24 DA ins end
		gp.deleteFlag = CsvService.itemCastString(item[++i]);
		gp.sopFlag = CsvService.itemCastString(item[++i]);
		gp.insertUser = CsvService.itemCastString(item[++i]);
		gp.updateUser = CsvService.itemCastString(item[++i]);

		gp.insertDate = CsvService.itemCastTimestamp(item[++i]);
		gp.updateDate = CsvService.itemCastTimestamp(item[++i]);

		return gp;
	}
}