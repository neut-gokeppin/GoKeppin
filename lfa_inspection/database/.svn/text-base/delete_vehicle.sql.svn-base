-- Function: spec.delete_vehicle(character)

-- DROP FUNCTION spec.delete_vehicle(character);

CREATE OR REPLACE FUNCTION spec.delete_vehicle(p_bctype character)
  RETURNS integer AS
$BODY$
--------------------------------------------------------------------------------
-- [Title] BC車種区分マスタ、指示記号マスタ、検査項目マスタ、検査順マスタ、工程マスタの削除
-- [Description]
--     車種マスタメンテナンスから使用
--     各マスタを削除する
-- [History]
--     2014.11.26 DA      新規作成
--     2016.02.24 DA      新機能追加
--------------------------------------------------------------------------------

-- PostgreSQLのFUNCTIONではトランザクション制御ができない。
-- 例外が発生した場合は全ての更新がrollbackされる。
-- 正常に完了した場合は自動でcommitされる。
declare
	p_cnt int;
BEGIN

	-- 検査データの存在を確認する
	SELECT
	  into p_cnt count(*)
	FROM
	  spec.m_vehicle
	  INNER JOIN spec.m_item
	    ON m_vehicle.bctype = m_item.bctype
	  INNER JOIN spec.f_result
	    ON m_item.mst_ver = f_result.mst_ver
	    AND m_item.item_code = f_result.item_code
	where
	  m_vehicle.bctype = p_bctype;

	if p_cnt > 0 then
		-- データが存在しているため削除処理の終了
		return -1;
	end if;

	-- 指示記号マスタの削除
	delete
	from
	  spec.m_bcsign
	where
	  exists (
	    select
	      *
	    from
	      spec.m_vehicle
	      inner join spec.m_item
	        on m_vehicle.bctype = m_item.bctype
	        and m_vehicle.bctype = p_bctype
	    where
	      m_item.mst_ver = m_bcsign.mst_ver
	      and m_item.item_code = m_bcsign.item_code
	  );

	-- 検査項目マスタの削除
	delete
	from
	  spec.m_item
	where
	  bctype = p_bctype;

	-- 検査順マスタの削除
	delete
	from
	  spec.m_order
	where
	  exists (
	    select
	      *
	    from
	      spec.m_vehicle
	      inner join spec.m_group
	        on m_vehicle.bctype = m_group.bctype
	        and m_vehicle.bctype = p_bctype
	    where
	      m_group.mst_ver = m_order.mst_ver
	      and m_group.group_code = m_order.group_code
	  );

	-- 工程マスタの削除
	delete
	from
	  spec.m_group
	where
	  exists (
	    select
	      *
	    from
	      spec.m_vehicle
	    where
	      m_vehicle.bctype = m_group.bctype
	      and m_vehicle.bctype = p_bctype
	  );

	-- 車種マスタの削除
	delete
	from
	  spec.m_vehicle
	where
	  bctype = p_bctype;

	-- 本番予約マスタの削除
	delete
	from
	  spec.m_reserve
	where
	  bctype = p_bctype;

	RETURN 0;

EXCEPTION WHEN OTHERS THEN
	-- 例外エラー
	RETURN -99;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION spec.delete_vehicle(character)
  OWNER TO spec;
COMMENT ON FUNCTION spec.delete_vehicle(character) IS 'マスタ削除処理';
