-- upload_channel 테이블에 user_id, name 유니크 추가
-- 1) 중복행 정리: user_id, name 이 동일한 경우 가장 오래된 행만 남기고 삭제
DELETE FROM UPLOAD_CHANNEL
WHERE ROWID IN (
    SELECT rid
    FROM (
        SELECT ROWID rid,
               ROW_NUMBER() OVER (PARTITION BY user_id, name ORDER BY id) AS rn
        FROM UPLOAD_CHANNEL
    )
    WHERE rn > 1
);

-- 2) 유니크 제약조건 생성
ALTER TABLE UPLOAD_CHANNEL
    ADD CONSTRAINT UQ_USER_ID_NAME UNIQUE (user_id, name);
