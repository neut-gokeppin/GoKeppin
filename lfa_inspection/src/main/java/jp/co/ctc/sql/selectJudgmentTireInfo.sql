SELECT
    m_item.item_name
  , f_bcdata.idno
  , f_bcdata.lo_date
  , COALESCE(f_result.item_code, 0)      AS item_code
  , COALESCE(f_result.inspec_no, 0)      AS inspec_no
  , COALESCE(f_result.inspec_result, '') AS inspec_result
  , COALESCE(f_result.ng_reason, '')     AS ng_reason
  , COALESCE(f_result.input_data, '')    AS input_data
  , COALESCE(f_result.mst_ver, 0)        AS mst_ver
  , m_group.group_no
  , m_order.inspec_order
FROM
    f_bcdata
    INNER JOIN f_resultsum
         ON f_resultsum.idno = f_bcdata.idno
        AND f_resultsum.lo_date = f_bcdata.lo_date
    INNER JOIN m_order
         ON m_order.mst_ver = f_resultsum.mst_ver
        AND m_order.group_code = f_resultsum.group_code
        AND m_order.ptn_div = f_bcdata.ptn_div
    INNER JOIN m_item
         ON m_item.mst_ver = f_resultsum.mst_ver
        AND m_item.item_code = m_order.item_code
        AND m_item.delete_flag = '0'
    INNER JOIN m_group
         ON m_group.mst_ver = f_resultsum.mst_ver
        AND m_group.group_code = f_resultsum.group_code
        AND m_group.delete_flag = '0'
    LEFT JOIN (
        SELECT
            DISTINCT f_result.*
        FROM
            f_result
            INNER JOIN (
                SELECT
                    idno
                  , lo_date
                  , item_code
                  , MAX(inspec_no) AS inspec_no
                FROM
                    f_result
                GROUP BY
                    idno
                  , lo_date
                  , item_code
            ) AS f_result_max
                 ON f_result_max.idno = f_result.idno
                AND f_result_max.lo_date = f_result.lo_date
                AND f_result_max.item_code = f_result.item_code
                AND f_result_max.inspec_no = f_result.inspec_no
    ) AS f_result
         ON f_result.idno = f_bcdata.idno
        AND f_result.lo_date = f_bcdata.lo_date
        AND f_result.item_code = m_order.item_code
WHERE
    f_bcdata.idno = /*idno*/
AND f_bcdata.lo_date = /*loDate*/
AND m_item.tire_div = true
ORDER BY
    m_group.group_no ASC
  , m_order.inspec_order ASC
