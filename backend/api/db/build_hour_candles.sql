SELECT
    date_trunc('hour', dt) dt,
    (array_agg(peos ORDER BY dt ASC))[1] o,
    MAX(peos) h,
    MIN(peos) l,
    (array_agg(peos ORDER BY dt DESC))[1] c
FROM "eosram"
WHERE dt BETWEEN $1::timestamp AND now()::timestamp
GROUP BY date_trunc('hour', dt)
ORDER BY dt;
