package com.final_team4.finalbe.user.mapper;

import com.final_team4.finalbe.user.dto.info.UserInfo;
import com.final_team4.finalbe.user.domain.User;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class UserInfoMapper {

    public UserInfo toUserInfo(User user) {
        return toUserInfo(user, Collections.emptyList());
    }

    public UserInfo toUserInfo(User user, List<String> roles) {
        if (user == null) {
            return null;
        }

        List<String> safeRoles = roles == null ? Collections.emptyList() : List.copyOf(roles);

        return UserInfo.builder()
            .id(user.getId())
            .email(user.getEmail())
            .name(user.getName())
            .roles(safeRoles)
            .build();
    }
}
