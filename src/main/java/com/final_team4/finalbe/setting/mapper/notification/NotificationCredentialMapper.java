package com.final_team4.finalbe.setting.mapper.notification;

import com.final_team4.finalbe.setting.domain.notification.NotificationCredential;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NotificationCredentialMapper {

   // Id 별 조회
   NotificationCredential findById(@Param("userId") Long userId, @Param("id") Long id);

   // 알림 채널 Id 별 조회
   NotificationCredential findByChannelId(@Param("userId") Long userId, @Param("channelId") Long channelId);

   // 알림 설정 업데이트
   void update(NotificationCredential entity);

   // 활성, 비활성만 업데이트
   int updateByActive(@Param("userId") Long userId, @Param("id") Long id, @Param("isActive") Boolean isActive);

   // 생성
   void insert(NotificationCredential entity);

   Long findActiveChannelIdByUserId(Long userId);
}
