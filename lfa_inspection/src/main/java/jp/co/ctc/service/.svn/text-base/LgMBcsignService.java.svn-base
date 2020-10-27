package jp.co.ctc.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import jp.co.ctc.dto.LgMBcsignDTO;
import jp.co.ctc.entity.LgMBcsign;
import jp.co.ctc.entity.LgMPart;

import org.seasar.extension.jdbc.AutoSelect;
import org.seasar.framework.util.FileUtil;
import org.seasar.struts.util.ServletContextUtil;


/**
 * 指示記号マスタを扱うサービスです.
 *
 * @author kaidu
 *
 */
public class LgMBcsignService extends UpdateService  {

	/**
	 * JDBCマネージャです.
	 */
	//public JdbcManager jdbcManager;

	/**
	 * 部品+指示記号のリストを返します.
	 *
	 * @param selectMst
	 *            取得するマスタの状態。0:仮、1:本番
	 * @return 部品項目+指示記号のリスト
	 */
	public List<LgMBcsignDTO> getLgMBcsignDTO(final Integer selectMst) {
		return getLgMBcsignDTO(selectMst, null);
	}

	/**
	 * 指示マスタのデータを取得します。
	 *
	 * @param selectMst
	 *            取得するマスタの状態。0:仮、1:本番
	 * @param spsCode
	 *            取得する対象のSPS台車コード
	 * @return 指示マスタのデータ
	 */
	public List<LgMBcsignDTO> getLgMBcsignDTO(final Integer selectMst,
			final Integer spsCode) {
		return getLgMBcsignDTO(selectMst, spsCode, null, null);
	}

	/**
	 * 指示マスタのデータを取得します。
	 *
	 * @param selectMst
	 *            取得するマスタの状態。0:仮、1:本番
	 * @param spsCode
	 *            取得する対象のグループ
	 * @param mstVersion
	 *            マスタバージョン
	 * @param partCode
	 *            取得する対象の検査項目
	 * @return 指示マスタのデータ
	 */
	public List<LgMBcsignDTO> getLgMBcsignDTO(final Integer selectMst,
			final Integer spsCode, final Integer mstVersion,
			final Integer partCode) {
		return getLgMBcsignDTO(selectMst, spsCode, mstVersion, partCode, null);
	}

	/**
	 * 指示マスタのデータを取得します。
	 *
	 * @param selectMst
	 *            取得するマスタの状態。0:仮、1:本番
	 * @param spsCode
	 *            取得する対象のSPS台車
	 * @param mstVersion
	 *            マスタバージョン
	 * @param partCode
	 *            取得する対象の部品
	 * @param bcSign
	 *            取得する対象の指示記号
	 * @return 指示マスタのデータ
	 */
	public List<LgMBcsignDTO> getLgMBcsignDTO(final Integer selectMst,
			final Integer spsCode, final Integer mstVersion,
			final Integer partCode, final String bcSign) {

		String conBcsign = "";
		String conVersion = "";
		// バージョン指定が0の場合号口レコードを返す。
		if (selectMst.equals(1)) {
			if (mstVersion == null || mstVersion.equals(0)) {
				// 本番・仮選択が『1』で、バージョンが指定されていない場合
				// 本番マスタの最新（号口）データを指定する。
				conVersion = "sopFlag = '1'";
			} else {
				// 本番・仮選択が『1』で、バージョンが指定されている場合
				// 本番マスタの指定バージョンのデータを指定する。
				conVersion = "mstVer = " + mstVersion;
			}
		} else {
			// 本番・仮選択が『0』の場合
			// 仮マスタののデータを指定する。
			conVersion = "mstVer = 0";
		}

		// 指示記号が指定されていない場合指示記号による選択を行わない。
		// 指定されていれば指示記号による選択を行う。
		if (bcSign != null) {
			conBcsign = " AND mBcsignList.bcSign = '" + bcSign + "'";
		}

		// AutoSelect構築
		AutoSelect<LgMPart> select = jdbcManager.from(LgMPart.class)
				.leftOuterJoin("mBcsignList", "mBcsignList.deleteFlag <> '1' " + conBcsign) // LgMPart.mBcSignListの変数名を指定
				.leftOuterJoin("mOrderList")
				.leftOuterJoin("mOrderList.mSps", "mOrderList.mSps.deleteFlag <> '1'");

		// WHERE条件構築
		StringBuilder conditions = new StringBuilder();
		conditions.append(" deleteFlag <> '1'");
		/*
		 * switch (selectMst) { case 1:
		 * conditions.append(" AND sopFlag = '1' "); break; case 0:
		 * conditions.append(" AND mstVer = 0 "); break; }
		 */
		conditions.append(" AND " + conVersion);

		if (partCode != null) {
			// アイテム条件付与
			conditions.append(" AND partCode = " + partCode);
		}

		if (spsCode != null) {
			// SPS台車条件付与
			conditions.append(" AND ");
			conditions
					.append(" COALESCE(mOrderList.spsCode,0,mOrderList.spsCode) = ? ");
			select = select.where(conditions.toString(), spsCode);
		} else {
			select = select.where(conditions.toString());
		}

		// DB検索
		List<LgMPart> items = select.orderBy(
				"msgNo, mBcsignList.bcSign, partCode").getResultList();

		// 返却用のLgMBcsignDTOのリストを作成
		List<LgMBcsignDTO> mBcsignDTOList = new ArrayList<LgMBcsignDTO>();
		for (LgMPart item : items) {
			if (item.mBcsignList.size() == 0) {
				LgMBcsignDTO dto = new LgMBcsignDTO(item);
				mBcsignDTOList.add(dto);
			} else {
				for (LgMBcsign bcsign : item.mBcsignList) {
					LgMBcsignDTO dto = new LgMBcsignDTO(bcsign);
					mBcsignDTOList.add(dto);
				}
			}
		}

		return mBcsignDTOList;
	}

	/**
	 * 指示マスタの単一データを取得します。
	 *
	 * @param selectMst
	 *            取得するマスタの状態。0:仮、1:本番
	 * @param mstVersion
	 *            マスタバージョン
	 * @param partCode
	 *            取得する対象の部品
	 * @param bcSign
	 *            取得する対象の指示記号
	 * @return 指示マスタのデータ
	 */
	public LgMBcsignDTO getLgMBcsignDTOByCode(final Integer selectMst,
			final Integer mstVersion, final Integer partCode, final String bcSign) {
		List<LgMBcsignDTO> mBcsignList = getLgMBcsignDTO(selectMst, null, mstVersion, partCode, bcSign);
		if (mBcsignList != null && mBcsignList.size() > 0) {
			return mBcsignList.get(0);
		} else {
			return null;
		}
	}
	/**
	 * 指示マスタの単一データを取得します。
	 *
	 * @param selectMst
	 *            取得するマスタの状態。0:仮、1:本番
	 * @param mstVersion
	 *            マスタバージョン
	 * @param partCode
	 *            取得する対象の検査項目
	 * @return 指示マスタのデータ
	 */
	public LgMBcsignDTO getLgMBcsignDTOByCode(final Integer selectMst,
			final Integer mstVersion, final Integer partCode) {
		return getLgMBcsignDTOByCode(selectMst, mstVersion, partCode, null);
	}

	/**
	 * 指示記号変換マスタを1レコード更新します.
	 *
	 * @param dto
	 *            追加／更新レコード
	 */
	public void update(final LgMBcsignDTO dto) {
		// 画像の格納先パス
		String path = "/lg_images/";
		// 更新レコードにセットするタイムスタンプ
		java.util.Date now = new java.util.Date();
		Timestamp timestamp = new Timestamp(now.getTime());
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateStr = df.format(now);

		// ファイル本体の保存
		if (dto.fileBody != null) {
			// 保存用ファイル名を決定
			String bcsign = dto.bcSign;
			if (bcsign == null) {
				bcsign = "";
			} else {
				try {
					bcsign = URLEncoder.encode(bcsign.trim(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			String fileName = dto.partCode + "-" + bcsign + "-" + dateStr
					+ ".jpg";
			String fullPath = ServletContextUtil.getServletContext()
					.getRealPath(path + fileName);

			// ファイル保存
			FileUtil.write(fullPath, dto.fileBody);
			// DB登録用ファイル名をセット
			dto.fileName = fileName;
		}

		// DB登録
		dto.updateDate = timestamp;
		LgMBcsign bcsign = dto.getLgMBcsign();
		if (dto.editDiv.equals("I")) {
			// 新規追加
			bcsign.insertDate = timestamp;
			jdbcManager.insert(bcsign).excludes("sopFlag", "deleteFlag")
					.execute();
		} else if (dto.editDiv.equals("U")) {
			// 更新
			//jdbcManager.update(bcsign).execute();
			super.updateEntity(bcsign);
		}
	}

	/**
	 * 指示記号レコードをテーブルから削除します.
	 *
	 * @param removedRows
	 *            削除対象の指示記号レコードのリスト
	 */
	public void remove(final List<LgMBcsignDTO> removedRows) {
		java.util.Date now = new java.util.Date();
		Timestamp timestamp = new Timestamp(now.getTime());
		for (LgMBcsignDTO dto : removedRows) {
			//jdbcManager.delete(dto.getLgMBcsign()).execute();
			dto.updateDate = timestamp;
			LgMBcsign mBcsign = dto.getLgMBcsign();
			this.deleteEntity(mBcsign);
		}
	}
}