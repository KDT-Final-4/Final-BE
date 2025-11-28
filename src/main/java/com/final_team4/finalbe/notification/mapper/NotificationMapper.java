package com.final_team4.finalbe.notification.mapper;

import com.final_team4.finalbe.notification.domain.Notification;
import com.final_team4.finalbe.notification.vo.NotificationWithTypeAndChannelAndCredential;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {
    Notification findById(@Param("userId") Long userId, @Param("id") Long id);
    void insert(Notification entity);
    List<Notification> findAllByUserId(@Param("userId") Long userId);
    NotificationWithTypeAndChannelAndCredential findByIdWithTypeAndChannelAndCredential(@Param("userId") Long userId, @Param("id") Long id);

}
