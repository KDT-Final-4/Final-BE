-- 1) user_id FK 먼저 제거
ALTER TABLE clicks DROP CONSTRAINT fk_clicks_user;

-- 2) user_id 컬럼 삭제
ALTER TABLE clicks DROP COLUMN user_id;

-- 3) IP 컬럼 추가
ALTER TABLE clicks ADD (ip VARCHAR2(15));

-- 4) (ip, product_id) 복합 유니크 추가
ALTER TABLE clicks ADD CONSTRAINT uk_clicks_ip_product UNIQUE (ip,
                                                               product_id);
