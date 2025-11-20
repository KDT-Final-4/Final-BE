package com.final_team4.finalbe.auth.controller;

import com.final_team4.finalbe.auth.dto.request.TokenIssueRequest;
import com.final_team4.finalbe.auth.dto.response.TokenResponse;
import com.final_team4.finalbe._core.jwt.JwtToken;
import com.final_team4.finalbe._core.jwt.JwtTokenService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.Collection;
import java.util.List;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final JwtTokenService jwtTokenService;

  public AuthController(JwtTokenService jwtTokenService) {
    this.jwtTokenService = jwtTokenService;
  }

  @Operation(summary = "임시 인증")
  @PostMapping("/test-token")
  public TokenResponse issueTestToken(@Valid @RequestBody TokenIssueRequest request) {
    JwtToken token = jwtTokenService.issueToken(
        request.username(),
        defaultRoles(request.roles()));
    return new TokenResponse(token.value(), token.issuedAt(), token.roles());
  }

  private Collection<String> defaultRoles(List<String> roles) {
    return CollectionUtils.isEmpty(roles) ? List.of("ROLE_TESTER") : roles;
  }

}
