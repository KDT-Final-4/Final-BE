package com.final_team4.finalbe.user.mapper;

import com.final_team4.finalbe.user.domain.Role;
import com.final_team4.finalbe.user.domain.RoleType;
import com.final_team4.finalbe.user.dto.info.UserInfo;
import com.final_team4.finalbe.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class UserInfoMapper {

  public UserInfo toUserInfo(User user) {
    if (user == null) {
      return null;
    }

    RoleType roleType = resolveRoleType(user);

    return UserInfo.builder()
        .id(user.getId())
        .email(user.getEmail())
        .name(user.getName())
        .role(roleType != null ? roleType.getName() : null)
        .build();
  }

  private RoleType resolveRoleType(User user) {
    if (user == null) {
      return null;
    }

    Role role = user.getRole();
    if (role != null && role.getId() != null) {
      return RoleType.fromId(role.getId());
    }

    Long roleId = user.getRoleId();
    if (roleId != null) {
      return RoleType.fromId(roleId);
    }

    return null;
  }
}
