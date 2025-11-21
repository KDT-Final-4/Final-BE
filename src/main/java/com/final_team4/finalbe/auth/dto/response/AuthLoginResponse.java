package com.final_team4.finalbe.auth.dto.response;

import com.final_team4.finalbe.auth.dto.info.TokenInfo;
import com.final_team4.finalbe.user.dto.info.UserInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(
    description = "로그인 성공 응답",
    example = """
        {
          "user": {
            "id": 1,
            "email": "user@example.com",
            "name": "사용자",
            "role": "ROLE_USER"
          },
          "token": {
            "token": "eyJhbGciOiJIUzI1NiJ9...",
            "issuedAt": "2024-07-25T09:20:00Z",
            "roles": ["ROLE_USER"]
          }
        }
        """
)
public class AuthLoginResponse {

    private UserInfo user;
    private TokenInfo token;

    //UserInfo와 TokenInfo에서 필요한 값을 각각 가져와 합쳐서 반환
    public static AuthLoginResponse of(
        Long userId,
        String email,
        String name,
        String userRole,
        String tokenValue,
        Instant issuedAt,
        List<String> tokenRoles
    ) {
        return AuthLoginResponse.builder()
            .user(UserInfo.builder()
                .id(userId)
                .email(email)
                .name(name)
                .role(userRole)
                .build())
            .token(TokenInfo.builder()
                .token(tokenValue)
                .issuedAt(issuedAt)
                .roles(tokenRoles)
                .build())
            .build();
    }




}
