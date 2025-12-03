-- LLM 채널 테이블에 PROMPT 컬럼 추가 (CLOB 타입)
drop table SCHEDULE;

CREATE TABLE schedule (
                          id                NUMBER GENERATED ALWAYS AS IDENTITY,
                          user_id           NUMBER          NOT NULL,
                          title             VARCHAR2(100)   NOT NULL,

                          start_time        TIMESTAMP       NOT NULL,
                          repeat_interval   VARCHAR2(20)    NOT NULL,

                          next_execution_at TIMESTAMP       NOT NULL,
                          last_executed_at  TIMESTAMP       NULL,

                          is_active         NUMBER(1)       DEFAULT 1 NOT NULL,
                          is_locked         NUMBER(1)       DEFAULT 0 NOT NULL,

                          created_at        TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
                          updated_at        TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,

                          CONSTRAINT pk_schedule PRIMARY KEY (id)
);