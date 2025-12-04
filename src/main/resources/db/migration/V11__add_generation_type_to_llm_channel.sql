-- LLM 채널 테이블에 GENERATION_TYPE 컬럼 추가
ALTER TABLE llm_channel
ADD generation_type VARCHAR2(20) DEFAULT 'AUTO' NOT NULL;

