package com.financesystem.finance.common.security.jwt;

import com.financesystem.finance.common.security.principal.AuthenticatedUserPrincipal;
import com.financesystem.finance.common.tenancy.TenancyProperties;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenService jwtTokenService;
    private final TenancyProperties tenancyProperties;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public JwtAuthenticationFilter(
            JwtTokenService jwtTokenService,
            TenancyProperties tenancyProperties,
            AuthenticationEntryPoint authenticationEntryPoint
    ) {
        this.jwtTokenService = jwtTokenService;
        this.tenancyProperties = tenancyProperties;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestUri = request.getRequestURI();

        if (tenancyProperties.isPublicPath(requestUri)) {
            return true;
        }

        return antPathMatcher.match("/api/auth/login", requestUri)
                || antPathMatcher.match("/api/auth/refresh", requestUri)
                || antPathMatcher.match("/api/auth/forgot-password", requestUri)
                || antPathMatcher.match("/api/auth/reset-password", requestUri)
                || antPathMatcher.match("/api/platform/auth/login", requestUri)
                || antPathMatcher.match("/api/platform/auth/refresh", requestUri);
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String authorizationHeader = request.getHeader("Authorization");

            if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith(BEARER_PREFIX)) {
                throw new InsufficientAuthenticationException("Missing or invalid Authorization header");
            }

            String token = authorizationHeader.substring(BEARER_PREFIX.length());
            AuthenticatedUserPrincipal principal = jwtTokenService.parseAccessToken(token);

            String tenantHeaderValue = request.getHeader(tenancyProperties.getHeaderName());
            if (StringUtils.hasText(tenantHeaderValue) && !tenantHeaderValue.equalsIgnoreCase(principal.tenantSlug())) {
                throw new BadCredentialsException("Token tenant does not match request tenant header");
            }

            List<SimpleGrantedAuthority> authorities = principal.roles()
                    .stream()
                    .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role.toUpperCase())
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principal, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (JwtException | InsufficientAuthenticationException | BadCredentialsException ex) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(
                    request,
                    response,
                    new BadCredentialsException(ex.getMessage(), ex)
            );
        }
    }
}
