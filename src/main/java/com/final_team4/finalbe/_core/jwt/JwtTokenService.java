package com.final_team4.finalbe._core.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Service
public class JwtTokenService {

  private final JwtProperties properties;
  private SecretKey secretKey;
  private JwtParser parser;
  private Duration tokenValidity;

  public JwtTokenService(JwtProperties properties) {
    this.properties = properties;
  }

  @PostConstruct
  void initialize() {
    Assert.hasText(properties.getSecret(), "security.jwt.secret must be configured");
    Assert.hasText(properties.getIssuer(), "security.jwt.issuer must be configured");
    Assert.notNull(properties.getTempValidity(), "security.jwt.temp-validity must be configured");
    Assert.isTrue(!properties.getTempValidity().isZero() && !properties.getTempValidity().isNegative(),
        "security.jwt.temp-validity must be a positive duration");
    this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.getSecret()));
    this.parser = Jwts.parser().verifyWith(secretKey).build();
    this.tokenValidity = properties.getTempValidity();
  }

  public JwtToken issueToken(String username, Collection<String> requestedRoles) {
    Assert.hasText(username, "username must not be blank");
    Instant issuedAt = Instant.now();
    List<String> roles = normalizeRoles(requestedRoles);
    Instant expiresAt = issuedAt.plus(tokenValidity);
    String token = Jwts.builder()
        .issuer(properties.getIssuer())
        .subject(username)
        .issuedAt(Date.from(issuedAt))
        .expiration(Date.from(expiresAt))
        .claim("roles", roles)
        .signWith(secretKey, Jwts.SIG.HS256)
        .compact();
    return new JwtToken(token, issuedAt, roles);
  }

  public Authentication authenticate(String token) {
    Claims claims = parser.parseSignedClaims(token).getPayload();
    List<String> roles = claims.get("roles", List.class);
    List<SimpleGrantedAuthority> authorities = toAuthorities(roles);
    return new UsernamePasswordAuthenticationToken(
        claims.getSubject(),
        token,
        authorities);
  }

  private List<String> normalizeRoles(Collection<String> requestedRoles) {
    if (requestedRoles == null || requestedRoles.isEmpty()) {
      return List.of("ROLE_TESTER");
    }
    List<String> roles = requestedRoles.stream()
        .filter(Objects::nonNull)
        .map(String::trim)
        .filter(StringUtils::hasText)
        .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
        .collect(Collectors.toList());
    return roles.isEmpty() ? List.of("ROLE_TESTER") : List.copyOf(roles);
  }

  private List<SimpleGrantedAuthority> toAuthorities(List<String> roles) {
    if (roles == null || roles.isEmpty()) {
      return List.of();
    }
    return roles.stream()
        .filter(Objects::nonNull)
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
  }
}
