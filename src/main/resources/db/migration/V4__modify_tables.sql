--content 테이블에 job_id 컬럼 추가 --
ALTER TABLE CONTENT ADD (job_id varchar2(128) not null);

-- LOG 테이블에 job_id 컬럼의 타입 수정 --
ALTER TABLE LOG MODIFY JOB_ID VARCHAR2(128) not null;

-- 필요 없는 테이블 DROP (존재하는 경우에만)
BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE user_notification_channel CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN  -- ORA-00942: table or view does not exist
         RAISE;
      END IF;
END;
/

-- Category table name 변경
RENAME CATEGORY to PRODUCT_CATEGORY;

-- 테이블 추가
CREATE TABLE user_notification_credential (
                                              id                       NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                              user_id                  NUMBER                      NOT NULL,
                                              notification_channel_id  NUMBER                      NOT NULL,
                                              webhook_url              VARCHAR2(512)               NULL,  -- Slack/webhook형 채널만 사용
                                              api_token                VARCHAR2(256)               NULL,  -- 필요 시 채널 토큰/키
                                              is_active                NUMBER(1) DEFAULT 0         NOT NULL, -- 기본값 비활성화
                                              created_at               TIMESTAMP(6) WITH TIME ZONE DEFAULT SYSTIMESTAMP NOT NULL,
                                              updated_at               TIMESTAMP(6) WITH TIME ZONE DEFAULT SYSTIMESTAMP NOT NULL,
                                              CONSTRAINT uq_unc_cred_user_channel UNIQUE (user_id, notification_channel_id),
                                              CONSTRAINT fk_unc_cred_user     FOREIGN KEY (user_id)                 REFERENCES users (id),
                                              CONSTRAINT fk_unc_cred_channel  FOREIGN KEY (notification_channel_id) REFERENCES notification_channel (id),
                                              CONSTRAINT chk_unc_cred_active CHECK (is_active IN (0, 1))
);
COMMENT ON COLUMN user_notification_credential.webhook_url IS '웹훅 기반 채널만 사용; email 등은 NULL';
COMMENT ON COLUMN user_notification_credential.api_token IS '필요 시 채널 토큰/키';
COMMENT ON COLUMN user_notification_credential.is_active IS '0=비활성, 1=활성';