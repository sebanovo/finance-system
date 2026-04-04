package com.financesystem.finance.common.security.jwt;

import com.financesystem.finance.common.security.JwtProperties;
import com.financesystem.finance.common.security.principal.AuthenticatedUserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JwtTokenService {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtTokenService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String subject, String tenantSlug, List<String> roles) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(jwtProperties.getAccessExpirationMs());

        return Jwts.builder()
                .setSubject(subject)
                .addClaims(Map.of(
                        JwtClaimNames.TENANT, tenantSlug,
                        JwtClaimNames.ROLES, roles,
                        JwtClaimNames.TOKEN_TYPE, JwtClaimNames.ACCESS
                ))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(String subject, String tenantSlug) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(jwtProperties.getRefreshExpirationMs());

        return Jwts.builder()
                .setSubject(subject)
                .addClaims(Map.of(
                        JwtClaimNames.TENANT, tenantSlug,
                        JwtClaimNames.TOKEN_TYPE, JwtClaimNames.REFRESH
                ))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    public AuthenticatedUserPrincipal parseAccessToken(String token) {
        Claims claims = parseClaims(token);

        String tokenType = claims.get(JwtClaimNames.TOKEN_TYPE, String.class);
        if (!JwtClaimNames.ACCESS.equals(tokenType)) {
            throw new JwtException("Invalid token type");
        }

        String subject = claims.getSubject();
        String tenantSlug = claims.get(JwtClaimNames.TENANT, String.class);

        @SuppressWarnings("unchecked")
        List<String> roles = claims.get(JwtClaimNames.ROLES, List.class);

        if (!StringUtils.hasText(subject)) {
            throw new JwtException("Token subject is missing");
        }

        if (!StringUtils.hasText(tenantSlug)) {
            throw new JwtException("Token tenant is missing");
        }

        return new AuthenticatedUserPrincipal(
                subject,
                tenantSlug,
                roles == null ? List.of() : roles
        );
    }

    public Claims parseRefreshToken(String token) {
        Claims claims = parseClaims(token);

        String tokenType = claims.get(JwtClaimNames.TOKEN_TYPE, String.class);
        if (!JwtClaimNames.REFRESH.equals(tokenType)) {
            throw new JwtException("Invalid refresh token type");
        }

        return claims;
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}