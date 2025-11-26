package com.final_team4.finalbe.notification.mapper;

import com.final_team4.finalbe.notification.domain.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NotificationMapper {
    Notification findById(@Param("userId") Long userId, @Param("id") Long id);
    void insert(Notification entity);

}
