package com.final_team4.finalbe.user.dto.info;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserInfo {

    private Long id;
    private String email;
    private String name;
    private List<String> roles;

}