-- 기존 CONTENT 테이블에 LINK 컬럼 추가 (NOT NULL, VARCHAR2(256))
ALTER TABLE content
    ADD (link VARCHAR2(256) NOT NULL);


-- 1) TREND_ID 컬럼 추가 + NOT NULL
ALTER TABLE content
    ADD (trend_id NUMBER(19) NOT NULL);

-- 2) FK 추가
ALTER TABLE content
    ADD CONSTRAINT fk_content_trend
        FOREIGN KEY (trend_id)
            REFERENCES trend(id);


-- 3) log 의 jobId nullable로 변경
ALTER TABLE log
    MODIFY (job_id NULL);

-- 4) schedule의 user_id를 unique로 설정

ALTER TABLE schedule_setting
    ADD CONSTRAINT uk_schedule_setting_user UNIQUE (user_id);