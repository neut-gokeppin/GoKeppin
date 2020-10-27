-- Function: spec.copy_vehicle(character varying, character varying, character varying, integer, integer, character varying)

-- DROP FUNCTION spec.copy_vehicle(character varying, character varying, character varying, integer, integer, character varying);

CREATE OR REPLACE FUNCTION spec.copy_vehicle(
    in_bctype_from          character varying
  , in_bctype_to            character varying
  , in_sop                  character varying
  , in_mst_ver_from         integer
  , in_mst_ver_to           integer
  , in_user_code            character varying
)
  RETURNS integer AS
$BODY$
--------------------------------------------------------------------------------
-- [Title] 車種間マスタコピー
-- [Description]
--     車種マスタメンテナンスから使用
--     指示記号マスタ、検査項目マスタ、検査順マスタ、工程マスタをコピーする
-- [Arguments]
--     in_bctype_from コピー元のBC車種区分
--     in_bctype_to   コピー先のBC車種区分
--     in_sop_flag    号口フラグ　0:仮、1:本番
--     in_mst_ver     コピー元マスタバージョン
--     in_mst_ver_to  コピー先マスタバージョン
--     in_user_code   ユーザーコード
--
-- [History]
--     2014.11.27 DA      新規作成
--     2016.02.24 DA      新機能追加
--------------------------------------------------------------------------------

DECLARE
    in_sop_flag     char(1);            -- 号口フラグ 0:仮、1:本番
    in_mst_ver      character varying;  -- マスタバージョン
    in_timestamp    timestamp;          -- 現在日時を取得
BEGIN

-- PostgreSQLのFUNCTIONではトランザクション制御ができない。
-- 例外が発生した場合は全ての更新がrollbackされる。
-- 正常に完了した場合は自動でcommitされる。

    -- コピー元が本番の場合
    IF in_sop = '1' THEN
        in_sop_flag := '1';
        in_mst_ver := '%';

    -- コピー元が仮の場合
    ELSE
        in_sop_flag := '%';
        in_mst_ver := in_mst_ver_from;

    END IF;

    -- 現在日時を取得
    select into in_timestamp current_timestamp;

    -- コピー先の検査順マスタの削除
    delete
    from
      spec.m_order
    where
      exists (
        select
          *
        from
          spec.m_group
        where
          bctype = in_bctype_to
          and mst_ver = in_mst_ver_to
          and mst_ver = m_order.mst_ver
          and group_code = m_order.group_code
      )
    ;

    -- コピー先の工程マスタの削除
    delete
    from
      spec.m_group
    where
      mst_ver = in_mst_ver_to
      and bctype = in_bctype_to
    ;

    -- 指示記号マスタの削除
    delete
    from
      spec.m_bcsign
    where
      exists (
        select
          *
        from
          spec.m_item
        where
          bctype = in_bctype_to
          and mst_ver = in_mst_ver_to
          and mst_ver = m_bcsign.mst_ver
          and item_code = m_bcsign.item_code
      )
    ;

    -- コピー先の検査項目マスタの削除
    delete
    from
      spec.m_item
    where
      mst_ver = in_mst_ver_to
      and bctype = in_bctype_to
    ;


    -- m_group本番マスタ作成
    IF in_bctype_from = in_bctype_to THEN
        INSERT
          INTO spec.m_group(
              mst_ver
            , group_code
            , group_no
            , group_name
            , line
            , area
            , delete_flag
            , sop_flag
            , insert_user
            , insert_date
            , bctype
            , group_code_copy
          )
        SELECT
              in_mst_ver_to
            , group_code
            , group_no
            , group_name
            , line
            , area
            , delete_flag
            , '0'
            , insert_user
            , in_timestamp  -- insert_date
            , in_bctype_to  -- コピー先BC車種区分
            , group_code
          FROM
            spec.m_group
          WHERE
            sop_flag like in_sop_flag
            AND cast(mst_ver as character varying) like in_mst_ver
            AND bctype = in_bctype_from
            AND delete_flag <> '1'
        ;
    ELSE
        INSERT
          INTO spec.m_group(
            mst_ver
            , group_no
            , group_name
            , line
            , area
            , delete_flag
            , sop_flag
            , insert_user
            , insert_date
            , bctype
            , group_code_copy
          )
        SELECT
              in_mst_ver_to
            , group_no
            , group_name
            , line
            , area
            , delete_flag
            , '0'
            , insert_user
            , in_timestamp  -- insert_date
            , in_bctype_to  -- コピー先BC車種区分
            , group_code
          FROM
            spec.m_group
          WHERE
            sop_flag like in_sop_flag
            AND cast(mst_ver as character varying) like in_mst_ver
            AND bctype = in_bctype_from
            AND delete_flag <> '1'
        ;
    END IF;

    -- m_item本番マスタ作成
    IF in_bctype_from = in_bctype_to THEN
        INSERT
          INTO spec.m_item(
            mst_ver
            , item_code
            , item_no
            , item_name
            , result_div
            , msg_div
            , msg_no
            , bc_position
            , bc_length
            , t_limit
            , b_limit
            , delete_flag
            , sop_flag
            , insert_user
            , insert_date
            , bctype
            , tire_div
            , okng_div
            , notes
            , item_code_copy
          )
        SELECT
              in_mst_ver_to
            , item_code
            , item_no
            , item_name
            , result_div
            , msg_div
            , msg_no
            , bc_position
            , bc_length
            , t_limit
            , b_limit
            , delete_flag
            , '0'
            , insert_user
            , in_timestamp  -- insert_date
            , in_bctype_to  -- コピー先BC車種区分
            , tire_div
            , okng_div
            , notes
            , item_code
          FROM
            spec.m_item
          WHERE
            sop_flag like in_sop_flag
            AND cast(mst_ver as character varying) like in_mst_ver
            AND bctype = in_bctype_from
            AND delete_flag <> '1'
        ;
    ELSE
        INSERT
          INTO spec.m_item(
            mst_ver
            , item_no
            , item_name
            , result_div
            , msg_div
            , msg_no
            , bc_position
            , bc_length
            , t_limit
            , b_limit
            , delete_flag
            , sop_flag
            , insert_user
            , insert_date
            , bctype
            , tire_div
            , okng_div
            , notes
            , item_code_copy
          )
        SELECT
              in_mst_ver_to
            , item_no
            , item_name
            , result_div
            , msg_div
            , msg_no
            , bc_position
            , bc_length
            , t_limit
            , b_limit
            , delete_flag
            , '0'
            , insert_user
            , in_timestamp  -- insert_date
            , in_bctype_to  -- コピー先BC車種区分
            , tire_div
            , okng_div
            , notes
            , item_code
          FROM
            spec.m_item
          WHERE
            sop_flag like in_sop_flag
            AND cast(mst_ver as character varying) like in_mst_ver
            AND bctype = in_bctype_from
            AND delete_flag <> '1'
        ;
    END IF;

    -- m_bcsign本番作成
    IF in_bctype_from = in_bctype_to THEN
        INSERT
          INTO spec.m_bcsign(
              mst_ver
            , sign_code
            , item_code
            , bc_sign
            , sign_contents
            , file_name
            , t_limit
            , b_limit
            , delete_flag
            , sop_flag
            , insert_user
            , insert_date
            , dummy_sign
            , sign_order
            , notes
            , sign_code_copy
          )
        SELECT
              in_mst_ver_to
            , sign_code
            , (
              SELECT
                    item_code
                FROM
                  spec.m_item
                WHERE
                  item_code_copy = T2_.item_code
                  AND mst_ver =  in_mst_ver_to  -- コピー先マスタバージョン
                  AND bctype = in_bctype_to     -- コピー先BC車種区分
            )
            , T1_.bc_sign
            , T1_.sign_contents
            , T1_.file_name
            , T1_.t_limit
            , T1_.b_limit
            , T1_.delete_flag
            , '0'
            , T1_.insert_user
            , in_timestamp  -- T1_.insert_date
            , T1_.dummy_sign
            , T1_.sign_order
            , T1_.notes
            , T1_.sign_code
          FROM
            spec.m_bcsign T1_
            INNER JOIN spec.m_item T2_
              ON T2_.mst_ver = T1_.mst_ver
              AND T2_.item_code = T1_.item_code
              AND T2_.delete_flag <> '1'
          WHERE
            T1_.sop_flag like in_sop_flag
            AND cast(T1_.mst_ver as character varying) like in_mst_ver
            AND T2_.bctype = in_bctype_from
            AND T1_.delete_flag <> '1'
        ;
    ELSE
        INSERT
          INTO spec.m_bcsign(
            mst_ver
            , item_code
            , bc_sign
            , sign_contents
            , file_name
            , t_limit
            , b_limit
            , delete_flag
            , sop_flag
            , insert_user
            , insert_date
            , dummy_sign
            , sign_order
            , notes
            , sign_code_copy
          )
        SELECT
              in_mst_ver_to
            , (
              SELECT
                    item_code
                FROM
                  spec.m_item
                WHERE
                  item_code_copy = T2_.item_code
                  AND mst_ver =  in_mst_ver_to  -- コピー先マスタバージョン
                  AND bctype = in_bctype_to     -- コピー先BC車種区分
            )
            , T1_.bc_sign
            , T1_.sign_contents
            , T1_.file_name
            , T1_.t_limit
            , T1_.b_limit
            , T1_.delete_flag
            , '0'
            , T1_.insert_user
            , in_timestamp  -- T1_.insert_date
            , T1_.dummy_sign
            , T1_.sign_order
            , T1_.notes
            , T1_.sign_code
          FROM
            spec.m_bcsign T1_
            INNER JOIN spec.m_item T2_
              ON T2_.mst_ver = T1_.mst_ver
              AND T2_.item_code = T1_.item_code
              AND T2_.delete_flag <> '1'
          WHERE
            T1_.sop_flag like in_sop_flag
            AND cast(T1_.mst_ver as character varying) like in_mst_ver
            AND T2_.bctype = in_bctype_from
            AND T1_.delete_flag <> '1'
        ;
    END IF;

    -- m_order本番作成
    INSERT
      INTO spec.m_order(
        mst_ver
        , group_code
        , ptn_div
        , inspec_order
        , item_code
        , sop_flag
        , insert_user
        , insert_date
        , item_code_copy
      )
    SELECT
          in_mst_ver_to
        , (
          SELECT
                group_code
            FROM
              spec.m_group
            WHERE
              group_code_copy = T2_.group_code
              AND mst_ver =  in_mst_ver_to      -- コピー先マスタバージョン
              AND bctype = in_bctype_to         -- コピー先BC車種区分
        )
        , T1_.ptn_div
        , T1_.inspec_order
        , (
          SELECT
                item_code
            FROM
              spec.m_item
            WHERE
              item_code_copy = T3_.item_code
              AND mst_ver =  in_mst_ver_to      -- コピー先マスタバージョン
              AND bctype = in_bctype_to         -- コピー先BC車種区分
        )
        , '0'
        , T1_.insert_user
        , in_timestamp      -- T1_.insert_date
        , NULL              -- T1_.item_code_copy
      FROM
        spec.m_order T1_
        INNER JOIN spec.m_group T2_
          ON T2_.mst_ver = T1_.mst_ver
          AND T2_.group_code = T1_.group_code
          AND T2_.delete_flag <> '1'
        INNER JOIN spec.m_item T3_
          ON T3_.mst_ver = T1_.mst_ver
          AND T3_.item_code = T1_.item_code
          AND T3_.delete_flag <> '1'
      WHERE
        T1_.sop_flag like in_sop_flag
        AND cast(T1_.mst_ver as character varying) like in_mst_ver
        AND T2_.bctype = in_bctype_from
    ;

    RETURN 0;

EXCEPTION WHEN OTHERS THEN
    RETURN -1;

END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION spec.copy_vehicle(character varying, character varying, character varying, integer, integer, character varying)
  OWNER TO spec;
COMMENT ON FUNCTION spec.copy_vehicle(character varying, character varying, character varying, integer, integer, character varying) IS '車種間マスタコピー処理';
