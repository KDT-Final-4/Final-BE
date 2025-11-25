package com.final_team4.finalbe._core.jwt;

import com.final_team4.finalbe._core.security.JwtPrincipal;
import com.final_team4.finalbe.user.domain.Role;
import com.final_team4.finalbe.user.domain.User;
import com.final_team4.finalbe.user.mapper.UserMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.JwtException;
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
    private final UserMapper userMapper;
    private SecretKey secretKey;
    private JwtParser parser;
    private Duration tokenValidity;

    public JwtTokenService(JwtProperties properties, UserMapper userMapper) {
        this.properties = properties;
        this.userMapper = userMapper;
    }

    @PostConstruct
    void initialize() {
        Assert.hasText(properties.getSecret(), "security.jwt.secret must be configured");
        Assert.hasText(properties.getIssuer(), "security.jwt.issuer must be configured");
        Assert.notNull(properties.getTempValidity(), "security.jwt.temp-validity must be configured");
        Assert.isTrue(!properties.getTempValidity().isZero() && !properties.getTempValidity().isNegative(), "security.jwt.temp-validity must be a positive duration");
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.getSecret()));
        this.parser = Jwts.parser().verifyWith(secretKey).build();
        this.tokenValidity = properties.getTempValidity();
    }

    public JwtToken issueToken(User user) {
        Assert.notNull(user, "user must not be null");
        Assert.notNull(user.getId(), "user id must not be null");
        Assert.hasText(user.getEmail(), "user email must not be blank");
        Role role = user.getRole();
        if (role == null && user.getRoleId() != null) {
            role = Role.fromId(user.getRoleId());
            user.assignRole(role);
        }
        Assert.state(role != null, "user role must be set");
        Assert.hasText(role.getName(), "user role must have name");
        return buildToken(user.getEmail(), user.getId(), user.getName(), role.getName());
    }

    public Authentication authenticate(String token) {
        Claims claims = parser.parseSignedClaims(token).getPayload();
        Long userId = asLong(claims.get("uid"));
        if (userId == null) {
            throw new JwtException("토큰에 사용자 ID가 없습니다.");
        }
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new JwtException("존재하지 않는 사용자입니다.");
        }
        Integer deleteFlag = user.getIsDelete();
        if (deleteFlag != null && deleteFlag != 0) {
            throw new JwtException("삭제된 사용자입니다.");
        }
        Role role = user.getRole();
        if (role == null && user.getRoleId() != null) {
            role = Role.fromId(user.getRoleId());
            user.assignRole(role);
        }
        if (role == null || !StringUtils.hasText(role.getName())) {
            throw new JwtException("권한 정보가 없습니다.");
        }
        List<String> roles = normalizeRoles(List.of(role.getName()));
        List<SimpleGrantedAuthority> authorities = toAuthorities(roles);


        boolean enabled = user.getIsDelete() == null || user.getIsDelete() == 0;

        JwtPrincipal principal = new JwtPrincipal(

                user.getId(),
                user.getEmail(),
                user.getName(),
                role.getName(),
                authorities,
                true,
                true,
                true,
                enabled);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    private JwtToken buildToken(String subject, Long userId, String name, String role) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(tokenValidity);
        JwtBuilder builder = Jwts.builder().issuer(properties.getIssuer()).subject(subject).issuedAt(Date.from(issuedAt)).expiration(Date.from(expiresAt)).signWith(secretKey, Jwts.SIG.HS256);
        if (userId != null) {
            builder.claim("uid", userId);
        }
        if (StringUtils.hasText(name)) {
            builder.claim("name", name);
        }
        if (StringUtils.hasText(role)) {
            builder.claim("role", role);
        }
        String token = builder.compact();
        return new JwtToken(token, issuedAt, expiresAt, userId, name, role);
    }

    private List<String> normalizeRoles(Collection<String> requestedRoles) {
        List<String> roles = requestedRoles == null ? List.of() : requestedRoles.stream().filter(Objects::nonNull).map(String::trim).filter(StringUtils::hasText).map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role).collect(Collectors.toList());
        if (roles.isEmpty()) {
            throw new IllegalArgumentException("roles must not be empty");
        }
        return List.copyOf(roles);
    }

    private List<SimpleGrantedAuthority> toAuthorities(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return List.of();
        }
        return roles.stream().filter(Objects::nonNull).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    private Long asLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        return null;
    }
}
