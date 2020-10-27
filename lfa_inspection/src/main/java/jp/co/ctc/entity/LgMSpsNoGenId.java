package jp.co.ctc.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import jp.co.ctc.service.CsvService;

/**
 * SPS台車
 *
 * @author kaidu
 *
 */
@Entity
@Table(name = "LG_M_SPS")
public class LgMSpsNoGenId extends AbstractLgMSps implements Serializable {

	/**
	 * Serializableインタフェースの実装に必要。
	 */
	static final long serialVersionUID = 1L;

	/**
	 * 識別子
	 */
	@Id
	public Integer spsCode;


	/**
	 * MSps型のインスタンスを生成する
	 *
	 * @param item 格納する値の配列
	 * @return MGroup型のインスタンス
	 */
	public LgMSpsNoGenId getClass(final Object[] item) {

		LgMSpsNoGenId sp = new LgMSpsNoGenId();

		sp.mstVer = CsvService.itemCastInteger(item[1]);
		sp.spsCode = CsvService.itemCastInteger(item[2]);
		sp.spsNo = CsvService.itemCastString(item[3]);
		sp.spsName = CsvService.itemCastString(item[4]);
		sp.deleteFlag = CsvService.itemCastString(item[5]);
		sp.sopFlag = CsvService.itemCastString(item[6]);
		sp.insertUser = CsvService.itemCastString(item[7]);
		sp.updateUser = CsvService.itemCastString(item[8]);

		sp.insertDate = CsvService.itemCastTimestamp(item[9]);
		sp.updateDate = CsvService.itemCastTimestamp(item[10]);

		return sp;
	}


}