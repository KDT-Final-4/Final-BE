-- 1) LOG_TYPE에 WARN 추가 (중복 방지)
INSERT INTO log_type (type_name)
SELECT 'WARN' FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM log_type WHERE type_name = 'WARN'
);

-- 2) CONTENT: trend FK/컬럼 제거, keyword 추가
ALTER TABLE content DROP CONSTRAINT fk_content_trend;
ALTER TABLE content DROP COLUMN trend_id;
ALTER TABLE content ADD (keyword VARCHAR2(200));

-- 3) LLM_CHANNEL: BASE_URL, TOP_P 컬럼 제거
ALTER TABLE llm_channel DROP COLUMN base_url;
ALTER TABLE llm_channel DROP COLUMN top_p;