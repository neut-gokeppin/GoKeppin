
-- DROP FUNCTION mst_regist(integer, character, character, character varying, timestamp without time zone, boolean);

CREATE OR REPLACE FUNCTION mst_regist(
    in_mstver           integer
  , in_bctype           character
  , in_regist_type      character
  , in_regist_item      character varying
  , in_reserve_date     timestamp without time zone
  , in_isbook           boolean
)
    RETURNS integer AS
$BODY$
--------------------------------------------------------------------------------
-- [Title] 仮マスタ本番登録
-- [Description]
--     各マスタの仮マスタを本番マスタにコピーする
-- [Arguments]
--     in_mstver            マスタバージョン
--     in_bctype            BC車種区分
--     in_regist_type       登録種類。どのマスタを仮から本番に登録するか選択。
--                              0:全マスタ、1:工程/項目/検査順、2:指示記号全て、3:検査項目ごと指示記号、4:指示記号、5:指示記号予約
--     in_regist_item       登録検査項目。または、登録指示記号
--                              in_regist_type=3 のとき、指定された検査項目に紐付く指示記号を仮マスタ本番登録する。
--                              in_regist_type=4 のとき、指定された指示記号を仮マスタ本番登録する。
--                              それ以外は無視。
--                              複数指定する場合は、カンマ区切り。※配列だとデータの受け渡しが上手くいかなかったので文字列対策した。
--     in_reserve_date      予約登録日時。予約日時以前の検査項目に紐付く指示記号を仮マスタ本番登録する。
--                              in_regist_type=5 のときのみ使用。それ以外は無視。
--     in_isbook            true:予約、false:即時切替
-- [History]
--     2013.05.20 CTC     新規作成
--     2013.06.04 T.Kaizu 更新
--     2013.09.10 T.Kaizu 検査順と指示記号を別々に本番登録するよう変更
--     2013.10.25 T.Kaizu 検査項目マスタと指示記号マスタにメモ欄を追加
--     2014.04.07 DA      工程マスタにラインとエリアを追加
--     2014.07.29 T.Kaizu 古いマスタバージョンの号口フラグが1のまま残ってしまう問題に対応
--     2016.02.24 DA      新機能追加
--     2016.08.31 DA      マスタバージョンは1から採番されるように修正
--------------------------------------------------------------------------------

DECLARE

    -- 現在の最新マスタバージョン（本番が無ければ仮からコピーするよう、初期値=0）
    max_mst_ver integer;

    -- 追加マスタバージョン
    new_mst_ver integer;

    -- コピー元マスタバージョン（工程、項目、検査順）
    src_proc_mst_ver integer;

    -- コピー元マスタバージョン（指示記号）
    src_bcsign_mst_ver integer;

    -- 新号口フラグ
    new_sop_flag character(1) := '0';

    -- 登録検査項目
    regist_item integer;

    -- 登録指示記号
    regist_sign integer;

    -- 指示記号予約(in_regist_type=5)の登録で使用するカーソル
    cur refcursor;

BEGIN
-- PostgreSQLのFUNCTIONではトランザクション制御ができない。
-- 例外が発生した場合は全ての更新がrollbackされる。
-- 正常に完了した場合は自動でcommitされる。


    -- 即時切替の場合のみ
    IF NOT in_isbook THEN

        -- 現本番マスタの号口フラグを"0"に変更
        UPDATE m_item
        SET    sop_flag = '0'
        WHERE  sop_flag = '1'
        AND    bctype   = in_bctype;

        UPDATE m_group
        SET    sop_flag = '0'
        WHERE  sop_flag = '1'
        AND    bctype   = in_bctype;

        UPDATE m_order
        SET    sop_flag = '0'
        FROM
        (   SELECT DISTINCT o.mst_ver,
                o.group_code,
                o.ptn_div,
                o.item_code
            FROM m_order o
            LEFT JOIN m_group g
            ON    o.group_code = g.group_code AND o.mst_ver = g.mst_ver
            WHERE g.bctype     = in_bctype
        ) AS wt
        WHERE m_order.mst_ver     = wt.mst_ver
        AND   m_order.group_code  = wt.group_code
        AND   m_order.ptn_div     = wt.ptn_div
        AND   m_order.item_code   = wt.item_code
        AND   m_order.sop_flag    = '1';

        UPDATE m_bcsign
        SET sop_flag = '0'
        FROM
        (   SELECT DISTINCT b.mst_ver,
                b.sign_code
            FROM m_bcsign b
            LEFT JOIN m_item i
            ON    b.item_code = i.item_code AND b.mst_ver = i.mst_ver
            WHERE i.bctype    = in_bctype
        ) AS wt
        WHERE m_bcsign.mst_ver   = wt.mst_ver
        AND   m_bcsign.sign_code = wt.sign_code
        AND   m_bcsign.sop_flag  = '1';

        -- 新本番マスタの号口フラグを"1"に変更
        new_sop_flag := '1';

    END IF;


    -- 現在の最新マスタバージョン取得
    SELECT MAX(mst_ver) INTO max_mst_ver FROM m_group WHERE bctype = in_bctype;

    -- 追加マスタバージョンを採番
    new_mst_ver := max_mst_ver + 1;
    IF new_mst_ver <= 0 THEN
        new_mst_ver := 1;
    END IF;

    -- コピー元が仮マスタなのか本番マスタなのか判別
    IF in_regist_type = '0' THEN
        -- 全マスタを仮から本番にコピー
        src_proc_mst_ver := in_mstver;
        src_bcsign_mst_ver := in_mstver;

    ELSIF in_regist_type = '1' THEN
        -- 工程/項目/検査順マスタを仮から本番にコピー
        src_proc_mst_ver := in_mstver;
        src_bcsign_mst_ver := max_mst_ver;

    ELSIF in_regist_type = '2' THEN
        -- 指示記号マスタの全レコードを仮から本番にコピー
        src_proc_mst_ver := max_mst_ver;
        src_bcsign_mst_ver := in_mstver;

    ELSIF in_regist_type = '3' THEN
        -- 指定された検査項目に紐付く指示記号マスタのみ仮から本番にコピー
        -- 一旦、全マスタを本番から本番にコピーする。
        src_proc_mst_ver := max_mst_ver;
        src_bcsign_mst_ver := max_mst_ver;

    ELSIF in_regist_type = '4' THEN
        -- 指定された指示記号マスタのみ仮から本番にコピー
        -- 一旦、全マスタを本番から本番にコピーする。
        src_proc_mst_ver := max_mst_ver;
        src_bcsign_mst_ver := max_mst_ver;

    ELSIF in_regist_type = '5' THEN
        -- 予約した指示記号マスタのみ仮から本番にコピー
        -- 一旦、全マスタを本番から本番にコピーする。
        src_proc_mst_ver := max_mst_ver;
        src_bcsign_mst_ver := max_mst_ver;

    ELSE
        -- 全マスタを本番から本番にコピーする。
        src_proc_mst_ver := max_mst_ver;
        src_bcsign_mst_ver := max_mst_ver;

    END IF;


    -- m_item本番マスタ作成
    INSERT INTO m_item( mst_ver,
            item_code,
            bctype,
            item_name,
            result_div,
            msg_div,
            msg_no,
            bc_position,
            bc_length,
            tire_div,
            okng_div,
            t_limit,
            b_limit,
            delete_flag,
            sop_flag,
            notes,
            insert_user,
            update_user,
            insert_date,
            update_date )
    SELECT  new_mst_ver,
            item_code,
            bctype,
            item_name,
            result_div,
            msg_div,
            msg_no,
            bc_position,
            bc_length,
            tire_div,
            okng_div,
            t_limit,
            b_limit,
            delete_flag,
            new_sop_flag,
            notes,
            insert_user,
            update_user,
            insert_date,
            update_date
    FROM  m_item
    WHERE mst_ver = src_proc_mst_ver
    AND   bctype = in_bctype
    AND   delete_flag = '0';


    -- m_group本番マスタ作成
    INSERT INTO m_group( mst_ver,
            group_code,
            bctype,
            group_no,
            group_name,
            line,
            area,
            non_display_flag,
            delete_flag,
            sop_flag,
            insert_user,
            update_user,
            insert_date,
            update_date )
    SELECT  new_mst_ver,
            group_code,
            bctype,
            group_no,
            group_name,
            line,
            area,
            non_display_flag,
            delete_flag,
            new_sop_flag,
            insert_user,
            update_user,
            insert_date,
            update_date
    FROM  m_group
    WHERE mst_ver = src_proc_mst_ver
    AND   bctype = in_bctype
    AND   delete_flag = '0';


    -- m_order本番作成
    INSERT INTO m_order( mst_ver,
            group_code,
            ptn_div,
            inspec_order,
            item_code,
            sop_flag,
            insert_user,
            update_user,
            insert_date,
            update_date )
    SELECT  new_mst_ver,
            o.group_code,
            o.ptn_div,
            o.inspec_order,
            o.item_code,
            new_sop_flag,
            o.insert_user,
            o.update_user,
            o.insert_date,
            o.update_date
    FROM  m_order o INNER JOIN
          m_group g USING (mst_ver, group_code)
    WHERE g.bctype      = in_bctype
    AND   mst_ver       = src_proc_mst_ver
    AND   g.delete_flag = '0';


    -- m_bcsign本番作成
    -- コピー元が仮マスタなのか本番マスタなのか判別
    IF in_regist_type = '0' OR in_regist_type = '2' THEN
        -- 全マスタ
        -- 指示記号マスタすべて
        INSERT INTO m_bcsign( mst_ver,
                sign_code,
                item_code,
                bc_sign,
                sign_contents,
                file_name,
                dummy_sign,
                sign_order,
                t_limit,
                b_limit,
                reserve_flag,
                reserve_user,
                reserve_date,
                sop_delete_flag,
                delete_flag,
                sop_flag,
                notes,
                insert_user,
                update_user,
                insert_date,
                update_date)
        SELECT  new_mst_ver,
                b.sign_code,
                b.item_code,
                b.bc_sign,
                b.sign_contents,
                b.file_name,
                b.dummy_sign,
                b.sign_order,
                b.t_limit,
                b.b_limit,
                CASE WHEN b.reserve_flag = '1' THEN '4'            ELSE (CASE WHEN b.reserve_flag = '2' THEN '3'            ELSE '0'  END) END,
                CASE WHEN b.reserve_flag = '1' THEN b.reserve_user ELSE (CASE WHEN b.reserve_flag = '2' THEN b.reserve_user ELSE NULL END) END,
                CASE WHEN b.reserve_flag = '1' THEN b.reserve_date ELSE (CASE WHEN b.reserve_flag = '2' THEN b.reserve_date ELSE NULL END) END,
                '0',
                b.delete_flag,
                new_sop_flag,
                b.notes,
                b.insert_user,
                b.update_user,
                b.insert_date,
                b.update_date
        FROM  m_bcsign b INNER JOIN
              m_item i USING (mst_ver, item_code)
        WHERE i.bctype      = in_bctype
        AND   mst_ver       = src_bcsign_mst_ver
        AND   i.delete_flag = '0'
        AND   b.delete_flag = '0';

    ELSE
        -- 工程/項目/検査順
        -- 検査項目ごと指示記号
        -- 指示記号予約
        -- ※一旦、全マスタを本番から本番にコピーする。
        INSERT INTO m_bcsign( mst_ver,
                sign_code,
                item_code,
                bc_sign,
                sign_contents,
                file_name,
                dummy_sign,
                sign_order,
                t_limit,
                b_limit,
                reserve_flag,
                reserve_user,
                reserve_date,
                sop_delete_flag,
                delete_flag,
                sop_flag,
                notes,
                insert_user,
                update_user,
                insert_date,
                update_date)
        SELECT  new_mst_ver,
                b.sign_code,
                b.item_code,
                b.bc_sign,
                b.sign_contents,
                b.file_name,
                b.dummy_sign,
                b.sign_order,
                b.t_limit,
                b.b_limit,
                '0',
                NULL,
                NULL,
                b.sop_delete_flag,
                b.delete_flag,
                new_sop_flag,
                b.notes,
                b.insert_user,
                b.update_user,
                b.insert_date,
                b.update_date
        FROM  m_bcsign b INNER JOIN
              m_item i USING (mst_ver, item_code)
        WHERE i.bctype      = in_bctype
        AND   mst_ver       = src_bcsign_mst_ver
        AND   i.delete_flag = '0'
        AND   b.delete_flag = '0';

    END IF;

    -- 検査項目ごと指示記号の登録の場合、
    -- 指定された検査項目に紐付く指示記号のみ仮マスタからコピーする。
    -- 既に追加が行われているため、一旦削除してから追加することになる。
    IF in_regist_type = '3' THEN

        -- カンマ区切りの項目コードを取得する
        OPEN cur FOR
            SELECT unnest(string_to_array(in_regist_item, ','));

        LOOP
            FETCH cur INTO regist_item;
            IF NOT FOUND THEN
                EXIT;
            END IF;

            -- 親となる検査項目が本番マスタに存在しない場合は追加。
            -- 検査項目を追加しておかないと、後で仮マスタ本番登録したときに
            -- 号口フラグが立ったまま残ってしまうため。
            INSERT INTO m_item( mst_ver,
                    item_code,
                    bctype,
                    item_name,
                    result_div,
                    msg_div,
                    msg_no,
                    bc_position,
                    bc_length,
                    tire_div,
                    okng_div,
                    t_limit,
                    b_limit,
                    delete_flag,
                    sop_flag,
                    notes,
                    insert_user,
                    update_user,
                    insert_date,
                    update_date )
            SELECT  new_mst_ver,
                    item_code,
                    bctype,
                    item_name,
                    result_div,
                    msg_div,
                    msg_no,
                    bc_position,
                    bc_length,
                    tire_div,
                    okng_div,
                    t_limit,
                    b_limit,
                    delete_flag,
                    new_sop_flag,
                    notes,
                    insert_user,
                    update_user,
                    insert_date,
                    update_date
            FROM  m_item
            WHERE mst_ver = in_mstver
            AND   item_code = regist_item
            AND   delete_flag = '0'
            AND   NOT EXISTS
                    (SELECT 1 FROM m_item
                     WHERE mst_ver = new_mst_ver
                     AND   item_code = regist_item);

            -- 指示記号を削除して仮マスタからコピー
            DELETE FROM m_bcsign
            WHERE mst_ver = new_mst_ver
            AND   item_code = regist_item;

            INSERT INTO m_bcsign( mst_ver,
                    sign_code,
                    item_code,
                    bc_sign,
                    sign_contents,
                    file_name,
                    dummy_sign,
                    sign_order,
                    t_limit,
                    b_limit,
                    reserve_flag,
                    reserve_user,
                    reserve_date,
                    sop_delete_flag,
                    delete_flag,
                    sop_flag,
                    notes,
                    insert_user,
                    update_user,
                    insert_date,
                    update_date)
            SELECT  new_mst_ver,
                    b.sign_code,
                    b.item_code,
                    b.bc_sign,
                    b.sign_contents,
                    b.file_name,
                    b.dummy_sign,
                    b.sign_order,
                    b.t_limit,
                    b.b_limit,
                    CASE WHEN b.reserve_flag = '1' THEN '4'            ELSE '0'  END,
                    CASE WHEN b.reserve_flag = '1' THEN b.reserve_user ELSE NULL END,
                    CASE WHEN b.reserve_flag = '1' THEN b.reserve_date ELSE NULL END,
                    b.sop_delete_flag,
                    b.delete_flag,
                    new_sop_flag,
                    b.notes,
                    b.insert_user,
                    b.update_user,
                    b.insert_date,
                    b.update_date
            FROM  m_bcsign b INNER JOIN
                  m_item i USING (mst_ver, item_code)
            WHERE i.bctype      = in_bctype
            AND   mst_ver       = in_mstver
            AND   item_code     = regist_item
            AND   i.delete_flag = '0'
            AND   b.delete_flag = '0';

        END LOOP;
        CLOSE cur;

    END IF;


    -- 指示記号の登録の場合、
    -- 指定された指示記号のみ仮マスタからコピーする。
    -- 既に追加が行われているため、一旦削除してから追加することになる。
    IF in_regist_type = '4' THEN

        -- カンマ区切りの項目コードを取得する
        OPEN cur FOR
            SELECT unnest(string_to_array(in_regist_item, ','));

        LOOP
            FETCH cur INTO regist_sign;
            IF NOT FOUND THEN
                EXIT;
            END IF;

            -- 親となる検査項目が本番マスタに存在しない場合は追加。
            -- 検査項目を追加しておかないと、後で仮マスタ本番登録したときに
            -- 号口フラグが立ったまま残ってしまうため。
            INSERT INTO m_item( mst_ver,
                    item_code,
                    bctype,
                    item_name,
                    result_div,
                    msg_div,
                    msg_no,
                    bc_position,
                    bc_length,
                    tire_div,
                    okng_div,
                    t_limit,
                    b_limit,
                    delete_flag,
                    sop_flag,
                    notes,
                    insert_user,
                    update_user,
                    insert_date,
                    update_date )
            SELECT  new_mst_ver,
                    item_code,
                    bctype,
                    item_name,
                    result_div,
                    msg_div,
                    msg_no,
                    bc_position,
                    bc_length,
                    tire_div,
                    okng_div,
                    t_limit,
                    b_limit,
                    delete_flag,
                    new_sop_flag,
                    notes,
                    insert_user,
                    update_user,
                    insert_date,
                    update_date
            FROM  m_item
            WHERE mst_ver = in_mstver
            AND   item_code = (SELECT item_code FROM m_bcsign WHERE mst_ver = in_mstver AND sign_code = regist_sign)
            AND   delete_flag = '0'
            AND   NOT EXISTS
                    (SELECT 1 FROM m_item
                     WHERE mst_ver = new_mst_ver
                     AND   item_code = (SELECT item_code FROM m_bcsign WHERE mst_ver = in_mstver AND sign_code = regist_sign));

            -- 指示記号を削除して仮マスタからコピー
            DELETE FROM m_bcsign
            WHERE mst_ver = new_mst_ver
            AND   sign_code = regist_sign;

            INSERT INTO m_bcsign( mst_ver,
                    sign_code,
                    item_code,
                    bc_sign,
                    sign_contents,
                    file_name,
                    dummy_sign,
                    sign_order,
                    t_limit,
                    b_limit,
                    reserve_flag,
                    reserve_user,
                    reserve_date,
                    sop_delete_flag,
                    delete_flag,
                    sop_flag,
                    notes,
                    insert_user,
                    update_user,
                    insert_date,
                    update_date)
            SELECT  new_mst_ver,
                    b.sign_code,
                    b.item_code,
                    b.bc_sign,
                    b.sign_contents,
                    b.file_name,
                    b.dummy_sign,
                    b.sign_order,
                    b.t_limit,
                    b.b_limit,
                    CASE WHEN b.reserve_flag = '1' THEN '4'            ELSE '0'  END,
                    CASE WHEN b.reserve_flag = '1' THEN b.reserve_user ELSE NULL END,
                    CASE WHEN b.reserve_flag = '1' THEN b.reserve_date ELSE NULL END,
                    b.sop_delete_flag,
                    b.delete_flag,
                    new_sop_flag,
                    b.notes,
                    b.insert_user,
                    b.update_user,
                    b.insert_date,
                    b.update_date
            FROM  m_bcsign b INNER JOIN
                  m_item i USING (mst_ver, item_code)
            WHERE i.bctype      = in_bctype
            AND   mst_ver       = in_mstver
            AND   sign_code     = regist_sign
            AND   i.delete_flag = '0'
            AND   b.delete_flag = '0';

        END LOOP;
        CLOSE cur;

    END IF;

    -- 指示記号予約の登録の場合、
    -- 予約された指示記号のみ仮マスタからコピーする。
    -- 既に追加が行われているため、一旦削除してから追加することになる。
    IF in_regist_type = '5' THEN

        -- 予約実行する項目コードを取得する
        -- 予約フラグは、ここに来る前に"1:予約中"から"2:実行中"に更新されている
        OPEN cur FOR
            SELECT
                T1_.item_code
              , T1_.sign_code
            FROM
                M_BCSIGN T1_
                INNER JOIN M_ITEM T2_
                     ON T2_.mst_ver = T1_.mst_ver
                    AND T2_.item_code = T1_.item_code
            WHERE
                    T1_.reserve_flag = '2'
                AND (T1_.delete_flag = '0' OR (T1_.sop_delete_flag = '1' AND T1_.delete_flag = '1'))
                AND T2_.delete_flag = '0'
                AND T2_.bctype = in_bctype
                AND T1_.mst_ver = in_mstver
                AND T1_.reserve_date <= in_reserve_date
            GROUP BY
                T1_.item_code
              , T1_.sign_code;

        LOOP
            FETCH cur INTO regist_item, regist_sign;
            IF NOT FOUND THEN
                EXIT;
            END IF;

            -- 親となる検査項目が本番マスタに存在しない場合は追加。
            -- 検査項目を追加しておかないと、後で仮マスタ本番登録したときに
            -- 号口フラグが立ったまま残ってしまうため。
            INSERT INTO m_item( mst_ver,
                    item_code,
                    bctype,
                    item_name,
                    result_div,
                    msg_div,
                    msg_no,
                    bc_position,
                    bc_length,
                    tire_div,
                    okng_div,
                    t_limit,
                    b_limit,
                    delete_flag,
                    sop_flag,
                    notes,
                    insert_user,
                    update_user,
                    insert_date,
                    update_date )
            SELECT  new_mst_ver,
                    item_code,
                    bctype,
                    item_name,
                    result_div,
                    msg_div,
                    msg_no,
                    bc_position,
                    bc_length,
                    tire_div,
                    okng_div,
                    t_limit,
                    b_limit,
                    delete_flag,
                    new_sop_flag,
                    notes,
                    insert_user,
                    update_user,
                    insert_date,
                    update_date
            FROM  m_item
            WHERE mst_ver = in_mstver
            AND   item_code = regist_item
            AND   delete_flag = '0'
            AND   NOT EXISTS
                    (SELECT 1 FROM m_item
                     WHERE mst_ver = new_mst_ver
                     AND   item_code = regist_item);

            -- 指示記号を削除して仮マスタからコピー
            DELETE FROM m_bcsign
            WHERE mst_ver = new_mst_ver
            AND   sign_code = regist_sign;

            INSERT INTO m_bcsign( mst_ver,
                    sign_code,
                    item_code,
                    bc_sign,
                    sign_contents,
                    file_name,
                    dummy_sign,
                    sign_order,
                    t_limit,
                    b_limit,
                    reserve_flag,
                    reserve_user,
                    reserve_date,
                    sop_delete_flag,
                    delete_flag,
                    sop_flag,
                    notes,
                    insert_user,
                    update_user,
                    insert_date,
                    update_date)
            SELECT  new_mst_ver,
                    b.sign_code,
                    b.item_code,
                    b.bc_sign,
                    b.sign_contents,
                    b.file_name,
                    b.dummy_sign,
                    b.sign_order,
                    b.t_limit,
                    b.b_limit,
                    CASE WHEN b.reserve_flag = '2' THEN '3'            ELSE '0'  END,
                    CASE WHEN b.reserve_flag = '2' THEN b.reserve_user ELSE NULL END,
                    CASE WHEN b.reserve_flag = '2' THEN b.reserve_date ELSE NULL END,
                    b.sop_delete_flag,
                    b.delete_flag,
                    new_sop_flag,
                    b.notes,
                    b.insert_user,
                    b.update_user,
                    b.insert_date,
                    b.update_date
            FROM  m_bcsign b INNER JOIN
                  m_item i USING (mst_ver, item_code)
            WHERE i.bctype      = in_bctype
            AND   mst_ver       = in_mstver
            AND   sign_code     = regist_sign
            AND   i.delete_flag = '0'
            AND   b.delete_flag = '0';

        END LOOP;
        CLOSE cur;

    END IF;

    RETURN 0;

EXCEPTION WHEN OTHERS THEN
    RETURN -1;

END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION mst_regist(integer, character, character, character varying, timestamp without time zone, boolean)
  OWNER TO spec;
COMMENT ON FUNCTION mst_regist(integer, character, character, character varying, timestamp without time zone, boolean) IS '仮マスタ本番登録処理';
