-- content_channel 테이블 제거 (존재하는 경우에만)
BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE CONTENT_CHANNEL CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN  -- ORA-00942: table or view does not exist
         RAISE;
      END IF;
END;
/

-- content 테이블에 upload_channel_id fk 추가
alter table CONTENT add(upload_channel_id number not null);
alter table CONTENT add
    constraint fk_channel_id foreign key(upload_channel_id)
        references UPLOAD_CHANNEL(ID);

-- notification 테이블에서 is_read 컬럼 제거 (존재하는 경우에만)
BEGIN
   EXECUTE IMMEDIATE 'ALTER TABLE NOTIFICATION DROP COLUMN IS_READ';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -904 THEN  -- ORA-00904: invalid column name
         RAISE;
      END IF;
END;
/