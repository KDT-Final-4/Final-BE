-- ============================================
-- Oracle 21c Optimized DDL
-- 작성일: 2025.11.21
-- 특징: Identity Column 사용, 제약조건 통합, 데이터 타입 최적화
-- ============================================

-- ============================================
-- 1. 기초 참조 테이블 (Master Data)
-- ============================================

-- role 테이블
CREATE TABLE role (
                      id              NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                      name            VARCHAR2(50)    NOT NULL,
                      description     VARCHAR2(128)   NOT NULL,
                      CONSTRAINT uq_role_name UNIQUE (name)
);

-- category 테이블
CREATE TABLE category (
                          id              NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          name            VARCHAR2(256)   NOT NULL,
                          description     VARCHAR2(512)   NULL,
                          CONSTRAINT uq_category_name UNIQUE (name)
);

-- notification_type 테이블
CREATE TABLE notification_type (
                                   id              NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                   name            VARCHAR2(19)    NOT NULL,
                                   description     VARCHAR2(128)   NULL,
                                   CONSTRAINT uq_notification_type_name UNIQUE (name)
);
COMMENT ON COLUMN notification_type.name IS '알람 종류(업로드 성공/실패, API 만료 등)';

-- notification_channel 테이블
CREATE TABLE notification_channel (
                                      id              NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                      name            VARCHAR2(19)    NOT NULL,
                                      description     VARCHAR2(128)   NULL,
                                      CONSTRAINT uq_notification_channel_name UNIQUE (name)
);
COMMENT ON COLUMN notification_channel.name IS '채널명 (slack, email 등)';

-- log_type 테이블
CREATE TABLE log_type (
                          id              NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          type_name       VARCHAR2(128)   NOT NULL,
                          CONSTRAINT uq_log_type_name UNIQUE (type_name)
);
COMMENT ON COLUMN log_type.type_name IS '로그 유형 (crawl, llm_generate, error 등)';


-- ============================================
-- 2. 주요 엔티티 테이블 (Main Data)
-- ============================================

-- users 테이블
CREATE TABLE users (
                       id              NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       role_id         NUMBER                          NOT NULL,
                       name            VARCHAR2(50)                    NOT NULL,
                       email           VARCHAR2(128)                   NOT NULL,
                       password        VARCHAR2(128)                   NULL,
                       is_delete       NUMBER(1) DEFAULT 0             NOT NULL,
                       created_at      TIMESTAMP(6) WITH TIME ZONE     DEFAULT SYSTIMESTAMP NOT NULL,
                       updated_at      TIMESTAMP(6) WITH TIME ZONE     DEFAULT SYSTIMESTAMP NOT NULL,
                       CONSTRAINT uq_users_email UNIQUE (email),
                       CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES role (id),
                       CONSTRAINT chk_users_is_delete CHECK (is_delete IN (0, 1))
);

-- product 테이블
CREATE TABLE product (
                         id              NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                         category_id     NUMBER                          NOT NULL,
                         name            VARCHAR2(256)                   NOT NULL,
                         link            VARCHAR2(512)                   NOT NULL,
                         thumbnail       VARCHAR2(512)                   NULL,
                         price           NUMBER                          NOT NULL,
                         created_at      TIMESTAMP(6) WITH TIME ZONE     DEFAULT SYSTIMESTAMP NOT NULL,
                         CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category (id)
);

-- content 테이블
CREATE TABLE content (
                         id              NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                         user_id         NUMBER                          NOT NULL,
                         title           VARCHAR2(100)                   NOT NULL,
                         body            CLOB                            NOT NULL,
                         status          VARCHAR2(20)                    NOT NULL,
                         generation_type VARCHAR2(20)                    NOT NULL,
                         created_at      TIMESTAMP(6) WITH TIME ZONE     DEFAULT SYSTIMESTAMP NOT NULL,
                         updated_at      TIMESTAMP(6) WITH TIME ZONE     DEFAULT SYSTIMESTAMP NOT NULL,
                         CONSTRAINT fk_content_users FOREIGN KEY (user_id) REFERENCES users (id)
);

-- trend 테이블
CREATE TABLE trend (
                       id              NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       category_id     NUMBER                          NOT NULL,
                       keyword         VARCHAR2(200)                   NOT NULL,
                       search_volume   NUMBER(10,2)                    NULL,
                       sns_type        VARCHAR2(50)                    NULL,
                       created_at      TIMESTAMP(6) WITH TIME ZONE     DEFAULT SYSTIMESTAMP NOT NULL,
                       CONSTRAINT fk_trend_category FOREIGN KEY (category_id) REFERENCES category (id)
);
인기 검색어 조회 exception 적용

-- ============================================
-- 3. 관계 및 로그 테이블 (Relation & Log)
-- ============================================

-- user_notification_channel (N:M 해소)
CREATE TABLE user_notification_channel (
                                           user_id             NUMBER      NOT NULL,
                                           notification_channel_id NUMBER  NOT NULL,
                                           CONSTRAINT pk_user_notification_channel PRIMARY KEY (user_id, notification_channel_id),
                                           CONSTRAINT fk_unc_user FOREIGN KEY (user_id) REFERENCES users (id),
                                           CONSTRAINT fk_unc_channel FOREIGN KEY (notification_channel_id) REFERENCES notification_channel (id)
);

-- product_content (N:M 해소)
CREATE TABLE product_content (
                                 product_id      NUMBER      NOT NULL,
                                 content_id      NUMBER      NOT NULL,
                                 CONSTRAINT pk_product_content PRIMARY KEY (product_id, content_id),
                                 CONSTRAINT fk_pc_product FOREIGN KEY (product_id) REFERENCES product (id),
                                 CONSTRAINT fk_pc_content FOREIGN KEY (content_id) REFERENCES content (id)
);

-- clicks 테이블 (클릭 로그)
CREATE TABLE clicks (
                        id              NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                        user_id         NUMBER                          NOT NULL,
                        product_id      NUMBER                          NOT NULL,
                        clicked_at      TIMESTAMP(6) WITH TIME ZONE     DEFAULT SYSTIMESTAMP NOT NULL,
                        CONSTRAINT fk_clicks_user FOREIGN KEY (user_id) REFERENCES users (id),
                        CONSTRAINT fk_clicks_product FOREIGN KEY (product_id) REFERENCES product (id)
);

-- ai_prediction 테이블 (트렌드 기반 AI 추천 결과)
CREATE TABLE ai_prediction (
                               id                  NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                               trend_id            NUMBER                          NOT NULL,
                               product_id          NUMBER                          NOT NULL,
                               model_type          VARCHAR2(50)                    NOT NULL,
                               score               NUMBER(5,2)                     NULL,
                               created_at          TIMESTAMP(6) WITH TIME ZONE     DEFAULT SYSTIMESTAMP NOT NULL,
                               CONSTRAINT fk_ai_prediction_trend FOREIGN KEY (trend_id) REFERENCES trend (id),
                               CONSTRAINT fk_ai_prediction_product FOREIGN KEY (product_id) REFERENCES product (id)
);

-- notification 테이블
CREATE TABLE notification (
                              id                  NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                              channel_id          NUMBER                          NOT NULL,
                              user_id             NUMBER                          NOT NULL,
                              type_id             NUMBER                          NOT NULL,
                              content_id          NUMBER                          NOT NULL,
                              title               VARCHAR2(50)                    NOT NULL,
                              message             CLOB                            NOT NULL,
                              notification_level  VARCHAR2(20)                    NOT NULL,
                              is_read             NUMBER(1) DEFAULT 0             NOT NULL,
                              created_at      TIMESTAMP(6) WITH TIME ZONE     DEFAULT SYSTIMESTAMP NOT NULL,
                              CONSTRAINT fk_notification_channel FOREIGN KEY (channel_id) REFERENCES notification_channel (id),
                              CONSTRAINT fk_notification_users FOREIGN KEY (user_id) REFERENCES users (id),
                              CONSTRAINT fk_notification_type FOREIGN KEY (type_id) REFERENCES notification_type (id),
                              CONSTRAINT fk_notification_content FOREIGN KEY (content_id) REFERENCES content (id),
                              CONSTRAINT chk_notification_is_read CHECK (is_read IN (0, 1))
);

-- schedule 테이블
CREATE TABLE schedule (
                          id              NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          user_id         NUMBER                          NOT NULL,
                          title           VARCHAR2(32)                    NOT NULL,
                          start_time      TIMESTAMP(6) WITH TIME ZONE     NOT NULL,
                          repeat_interval VARCHAR2(64)                    NULL,
                          last_executed_at TIMESTAMP(6) WITH TIME ZONE    NULL,
                          created_at      TIMESTAMP(6) WITH TIME ZONE     DEFAULT SYSTIMESTAMP NOT NULL,
                          updated_at      TIMESTAMP(6) WITH TIME ZONE     DEFAULT SYSTIMESTAMP NOT NULL,
                          CONSTRAINT fk_schedule_users FOREIGN KEY (user_id) REFERENCES users (id)
);

-- schedule_setting 테이블
CREATE TABLE schedule_setting (
                                  id              NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                  user_id         NUMBER                          NOT NULL,
                                  is_run          NUMBER(1) DEFAULT 0             NOT NULL,
                                  max_daily_runs  NUMBER                          NOT NULL,
                                  retry_on_fail   NUMBER                          NOT NULL,
                                  created_at      TIMESTAMP(6) WITH TIME ZONE     DEFAULT SYSTIMESTAMP NOT NULL,
                                  updated_at      TIMESTAMP(6) WITH TIME ZONE     DEFAULT SYSTIMESTAMP NOT NULL,
                                  CONSTRAINT fk_schedule_setting_users FOREIGN KEY (user_id) REFERENCES users (id),
                                  CONSTRAINT chk_schedule_setting_is_run CHECK (is_run IN (0, 1))
);

-- log 테이블
CREATE TABLE log (
                     id              NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                     user_id         NUMBER                          NULL,
                     type_id         NUMBER                          NOT NULL,
                     job_id          NUMBER                          NULL,
                     message         CLOB                            NOT NULL,
                     created_at      TIMESTAMP(6) WITH TIME ZONE     DEFAULT SYSTIMESTAMP NOT NULL,
                     CONSTRAINT fk_log_user FOREIGN KEY (user_id) REFERENCES users (id),
                     CONSTRAINT fk_log_type FOREIGN KEY (type_id) REFERENCES log_type (id)
);

-- trend_analysis 테이블
CREATE TABLE trend_analysis (
                                id              NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                trend_id        NUMBER                          NOT NULL,
                                summary         CLOB                            NOT NULL,
                                sentiment       VARCHAR2(20)                    NULL,
                                created_at      TIMESTAMP(6) WITH TIME ZONE     DEFAULT SYSTIMESTAMP NOT NULL,
                                CONSTRAINT fk_trend_analysis_trend FOREIGN KEY (trend_id) REFERENCES trend (id)
);

-- api_usage_log 테이블
CREATE TABLE api_usage_log (
                               id                  NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                               user_id             NUMBER                          NOT NULL,
                               content_id          NUMBER                          NOT NULL,
                               request_payload     CLOB                            NULL,
                               response_payload    CLOB                            NULL,
                               tokens_used         NUMBER(12,2)                    NULL,
                               created_at          TIMESTAMP(6) WITH TIME ZONE     DEFAULT SYSTIMESTAMP NOT NULL,
                               CONSTRAINT fk_api_usage_log_user FOREIGN KEY (user_id) REFERENCES users (id),
                               CONSTRAINT fk_api_usage_log_content FOREIGN KEY (content_id) REFERENCES content (id)
);

-- llm_channel 테이블
CREATE TABLE llm_channel (
                             id                  NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                             user_id             NUMBER                          NOT NULL,
                             name                VARCHAR2(50)                    NOT NULL,
                             model_name          VARCHAR2(100)                   NOT NULL,
                             api_key             VARCHAR2(200)                   NULL,
                             base_url            VARCHAR2(200)                   NULL,
                             status              NUMBER(1) DEFAULT 0             NOT NULL,
                             max_tokens          NUMBER                          NULL,
                             temperature         NUMBER(5,2)                     NULL,
                             top_p               NUMBER(5,2)                     NULL,
                             created_at          TIMESTAMP(6) WITH TIME ZONE     DEFAULT SYSTIMESTAMP NOT NULL,
                             updated_at          TIMESTAMP(6) WITH TIME ZONE     DEFAULT SYSTIMESTAMP NOT NULL,
                             CONSTRAINT fk_llm_channel_users FOREIGN KEY (user_id) REFERENCES users (id),
                             CONSTRAINT chk_llm_channel_status CHECK (status IN (0, 1))
);

-- upload_channel 테이블
CREATE TABLE upload_channel (
                                id              NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                user_id         NUMBER                          NOT NULL,
                                name            VARCHAR2(50)                    NOT NULL,
                                api_key         VARCHAR2(200)                   NULL,
                                status          NUMBER(1) DEFAULT 0             NOT NULL,
                                created_at      TIMESTAMP(6) WITH TIME ZONE     DEFAULT SYSTIMESTAMP NOT NULL,
                                updated_at      TIMESTAMP(6) WITH TIME ZONE     DEFAULT SYSTIMESTAMP NOT NULL,
                                CONSTRAINT fk_upload_channel_users FOREIGN KEY (user_id) REFERENCES users (id),
                                CONSTRAINT chk_upload_channel_status CHECK (status IN (0, 1))
);

-- content_channel 테이블 (N:M)
CREATE TABLE content_channel (
                                 channel_id      NUMBER      NOT NULL,
                                 content_id      NUMBER      NOT NULL,
                                 CONSTRAINT pk_content_channel PRIMARY KEY (channel_id, content_id),
                                 CONSTRAINT fk_cc_channel FOREIGN KEY (channel_id) REFERENCES upload_channel (id),
                                 CONSTRAINT fk_cc_content FOREIGN KEY (content_id) REFERENCES content (id)
);

-- ============================================
-- 4. 인덱스 생성 (성능 최적화)
-- ============================================

CREATE INDEX idx_product_category_id ON product (category_id);
CREATE INDEX idx_content_user_id ON content (user_id);
CREATE INDEX idx_trend_category_id ON trend (category_id);
CREATE INDEX idx_clicks_product_id ON clicks (product_id);
CREATE INDEX idx_notification_user_id ON notification (user_id);
CREATE INDEX idx_notification_created_at ON notification (created_at);
CREATE INDEX idx_notification_user_created ON notification (user_id, created_at DESC);
CREATE INDEX idx_log_user_id ON log (user_id);
CREATE INDEX idx_log_created_at ON log (created_at);
CREATE INDEX idx_schedule_user_id ON schedule (user_id);
CREATE INDEX idx_trend_analysis_trend_id ON trend_analysis (trend_id);
CREATE INDEX idx_api_usage_log_content_id ON api_usage_log (content_id);
CREATE INDEX idx_llm_channel_user_id ON llm_channel (user_id);
CREATE INDEX idx_upload_channel_user_id ON upload_channel (user_id);