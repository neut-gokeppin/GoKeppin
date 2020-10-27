-- Function: mst_sopflag(character)

-- DROP FUNCTION mst_sopflag(character);

CREATE OR REPLACE FUNCTION mst_sopflag(in_bctype character)
  RETURNS integer AS
$BODY$
--------------------------------------------------------------------------------
-- [Title] 号口フラグ切り替え
-- [Description]
--     各マスタの最新バージョンを号口とするよう、号口フラグを設定する
-- [History]
--     2013.05.21 T.Kaizu 新規作成
--------------------------------------------------------------------------------

-- PostgreSQLのFUNCTIONではトランザクション制御ができない。
-- 例外が発生した場合は全ての更新がrollbackされる。
-- 正常に完了した場合は自動でcommitされる。

BEGIN
	-- m_group
	UPDATE	m_group
	SET	sop_flag = '0'
	WHERE	bctype = $1
	AND	sop_flag = '1';
	
	UPDATE	m_group
	SET	sop_flag = '1'
	WHERE	bctype = $1
	AND	mst_ver = (SELECT MAX(mst_ver) FROM m_group WHERE bctype = $1);

	-- m_item
	UPDATE	m_item
	SET	sop_flag = '0'
	WHERE	bctype = $1
	AND	sop_flag = '1';
	
	UPDATE	m_item
	SET	sop_flag = '1'
	WHERE	bctype = $1
	AND	mst_ver = (SELECT MAX(mst_ver) FROM m_item WHERE bctype = $1);

	-- m_order
	UPDATE	m_order
	SET	sop_flag = '0'
	WHERE	EXISTS (SELECT 1 FROM m_group WHERE mst_ver = m_order.mst_ver AND group_code = m_order.group_code AND bctype = $1)
	AND	sop_flag = '1';
	
	UPDATE	m_order
	SET	sop_flag = '1'
	WHERE	EXISTS (SELECT 1 FROM m_group WHERE mst_ver = m_order.mst_ver AND group_code = m_order.group_code AND bctype = $1 AND sop_flag = '1');

	-- m_bcsign
	UPDATE	m_bcsign
	SET	sop_flag = '0'
	WHERE	EXISTS (SELECT 1 FROM m_item WHERE mst_ver = m_bcsign.mst_ver AND item_code = m_bcsign.item_code AND bctype = $1)
	AND	sop_flag = '1';
	
	UPDATE	m_bcsign
	SET	sop_flag = '1'
	WHERE	EXISTS (SELECT 1 FROM m_item WHERE mst_ver = m_bcsign.mst_ver AND item_code = m_bcsign.item_code AND bctype = $1 AND sop_flag = '1');

	RETURN 0;

EXCEPTION WHEN OTHERS THEN
	RETURN -1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION mst_sopflag(character)
  OWNER TO spec;
COMMENT ON FUNCTION mst_sopflag(character) IS '号口フラグ切り替え';
