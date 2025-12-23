-- LLM 채널 테이블에 PROMPT 컬럼 추가 (CLOB 타입)
ALTER TABLE llm_channel
ADD prompt CLOB NULL;


