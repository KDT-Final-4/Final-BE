package com.final_team4.finalbe.user.service;

import com.final_team4.finalbe.user.domain.User;
import com.final_team4.finalbe.user.dto.request.UserRegisterRequest;
import com.final_team4.finalbe.user.mapper.UserInfoMapper;
import com.final_team4.finalbe.user.mapper.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserMapper userMapper;
    private final UserInfoMapper userInfoMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(@Valid UserRegisterRequest request) {
        ensureEmailAvailable(request.getEmail());
        String encoded = passwordEncoder.encode(request.getPassword());

        User user = request.toEntity().toBuilder()
                .password(encoded)
                .build();
        userMapper.insert(user);

    }

    private void ensureEmailAvailable(String email) {
        if (userMapper.findByEmail(email) != null) {
            throw new RuntimeException("이미 존재하는 이메일" + email);
        }
    }

}
