package com.final_team4.finalbe.user.dto.response;


import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserUpdateResponse {

    private String name;

}
