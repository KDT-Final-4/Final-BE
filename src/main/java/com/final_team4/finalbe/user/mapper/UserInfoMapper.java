package com.final_team4.finalbe.user.mapper;

import com.final_team4.finalbe.user.domain.User;
import com.final_team4.finalbe.user.domain.Role;
import com.final_team4.finalbe.user.domain.RoleType;
import com.final_team4.finalbe.user.dto.UserInfoDto;
import com.final_team4.finalbe.user.dto.response.UserFullResponse;
import com.final_team4.finalbe.user.dto.response.UserSummaryResponse;
import com.final_team4.finalbe.user.dto.response.UserUpdateResponse;
import org.springframework.stereotype.Component;

@Component
public class UserInfoMapper {

    public UserInfoDto toUserInfo(User user) {
        if (user == null) {
            return null;
        }

        RoleType roleType = resolveRoleType(user);

        return UserInfoDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(roleType != null ? roleType.getName() : null)
                .build();
    }

    public UserSummaryResponse toUserSummary(User user) {
        if (user == null) {
            return null;
        }

        RoleType roleType = resolveRoleType(user);
        return UserSummaryResponse.builder()
                .userId(user.getId())
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

    public UserFullResponse toUserFull(User user) {
        if(user == null) {
            return null;
        }
        RoleType roleType = resolveRoleType(user);
        return UserFullResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(roleType != null ? roleType.getName() : null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .isDelete(user.getIsDelete())
                .build();
    }


    public static UserUpdateResponse of(User updated) {
        return UserUpdateResponse.builder()
                .name(updated.getName())
                .build();
    }
}
