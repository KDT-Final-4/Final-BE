-- upload_channel 테이블에 user_id, name 유니크 추가
ALTER TABLE UPLOAD_CHANNEL
    ADD CONSTRAINT UQ_USER_ID_NAME UNIQUE (user_id, name);