package com.final_team4.finalbe.user.service;

import com.final_team4.finalbe._core.exception.ContentNotFoundException;
import com.final_team4.finalbe._core.exception.DuplicateEmailException;
import com.final_team4.finalbe._core.exception.UnauthorizedException;
import com.final_team4.finalbe._core.security.JwtPrincipal;
import com.final_team4.finalbe.user.domain.User;
import com.final_team4.finalbe.user.dto.PasswordUpdateRequest;
import com.final_team4.finalbe.user.dto.UserRegisterRequestDto;
import com.final_team4.finalbe.user.dto.UserUpdateRequest;
import com.final_team4.finalbe.user.dto.response.UserFullResponse;
import com.final_team4.finalbe.user.dto.response.UserSummaryResponse;
import com.final_team4.finalbe.user.mapper.UserMapper;
import com.final_team4.finalbe.user.mapper.UserInfoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserMapper userMapper;
    private final UserInfoMapper userInfoMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserSummaryResponse register(@Valid UserRegisterRequestDto request) {
        ensureEmailAvailable(request.getEmail());
        String encoded = passwordEncoder.encode(request.getPassword());

        User user = request.toEntity().toBuilder()
                .password(encoded)
                .build();
        userMapper.insert(user);
        return userInfoMapper.toUserSummary(user);
    }

    private void ensureEmailAvailable(String email) {
        if (userMapper.findByEmail(email) != null) {
            throw new DuplicateEmailException("이미 존재하는 이메일입니다 : " + email);
        }
    }

    @Transactional(readOnly = true)
    public UserFullResponse findProfile(Long userId) {
        User user = Optional.ofNullable(userMapper.findAvailableById(userId))
                .orElseThrow(()->new ContentNotFoundException("사용자를 찾을 수 없습니다."));
                return userInfoMapper.toUserFull(user);
    }


    @Transactional
    public User updateProfile(Long userId, UserUpdateRequest request ) {
        User user = Optional.ofNullable(userMapper.findAvailableById(userId))
                .orElseThrow(()->new ContentNotFoundException("사용자를 찾을 수 없습니다."));


        int updated = userMapper.updateProfile(userId,request.getName());
        if(updated == 0){
            throw new ContentNotFoundException("사용자를 찾을 수 없습니다.");
        }

        return Optional.ofNullable(userMapper.findAvailableById(userId))
                .orElseThrow(()->new ContentNotFoundException("사용자를 찾을 수 없습니다."));
    }

    @Transactional
    public void updatePassword(PasswordUpdateRequest request, JwtPrincipal principal) {
        User user = userMapper.findAvailableById(principal.userId());

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new UnauthorizedException("비밀번호가 일치하지 않습니다.");
        }
        boolean confirmed = request.getNewPassword().equals(request.getConfirmNewPassword());

        if (!confirmed) {
            throw new UnauthorizedException("새로운 비밀번호가 일치하지 않습니다.");
        }
        String encoded = passwordEncoder.encode(request.getNewPassword());

        int updated = userMapper.updatePassword(user.getId(),encoded);
        if(updated == 0){
            throw new ContentNotFoundException("비밀번호를 저장하지 못했습니다.");
        }


    }
}
