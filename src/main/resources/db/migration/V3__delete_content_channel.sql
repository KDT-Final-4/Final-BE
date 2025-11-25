-- content_channel 테이블 제거
drop table CONTENT_CHANNEL;

-- content 테이블에 upload_channel_id fk 추가
alter table CONTENT add(upload_channel_id number);
alter table CONTENT add
    constraint fk_channel_id foreign key(upload_channel_id)
        references UPLOAD_CHANNEL(ID);

-- notification 테이블에서 is_read 컬럼 제거
alter table NOTIFICATION drop column IS_READ;