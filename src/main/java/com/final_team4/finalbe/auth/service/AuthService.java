package com.final_team4.finalbe.auth.service;

import com.final_team4.finalbe._core.exception.UnauthorizedException;
import com.final_team4.finalbe._core.jwt.JwtToken;
import com.final_team4.finalbe._core.jwt.JwtTokenService;
import com.final_team4.finalbe.auth.dto.request.LoginRequest;
import com.final_team4.finalbe.auth.dto.response.LoginResponse;
import com.final_team4.finalbe.user.domain.User;
import com.final_team4.finalbe.user.mapper.UserMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenService jwtTokenService;

  public LoginResponse login(LoginRequest request) {
    User user = Optional.ofNullable(userMapper.findByEmail(request.email()))
        .orElseThrow(() -> new UnauthorizedException("가입되지 않은 이메일입니다."));

    if (!passwordEncoder.matches(request.password(), user.getPassword())) {
      throw new UnauthorizedException("비밀번호가 일치하지 않습니다.");
    }

    user.getRole();
    JwtToken token = jwtTokenService.issueToken(user);
    return new LoginResponse(
        token.value(),
        token.issuedAt(),
        token.expiresAt(),
        token.userId(),
        token.name(),
        token.role());
  }
}
