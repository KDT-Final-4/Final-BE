-- 1) user_id FK 먼저 제거 (존재하는 경우에만)
BEGIN
   EXECUTE IMMEDIATE 'ALTER TABLE clicks DROP CONSTRAINT fk_clicks_user';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -2443 THEN  -- ORA-02443: Cannot drop constraint - does not exist
         RAISE;
      END IF;
END;
/

-- 2) user_id 컬럼 삭제
ALTER TABLE clicks DROP COLUMN user_id;

-- 3) IP 컬럼 추가
ALTER TABLE clicks ADD (ip VARCHAR2(45));

-- 4) (ip, product_id) 복합 유니크 추가
ALTER TABLE clicks ADD CONSTRAINT uk_clicks_ip_product UNIQUE (ip,
                                                               product_id);
