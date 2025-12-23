package com.final_team4.finalbe.user.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {


    @NotBlank
    @Size(min = 1, max = 10, message = "이름은 1자 이상 10자 이하여야 합니다.")
    private String name;

}
