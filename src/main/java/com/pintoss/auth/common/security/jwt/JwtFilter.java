package com.pintoss.auth.common.security.jwt;

import com.pintoss.auth.common.security.UserRoleEnum;
import com.pintoss.auth.common.util.HttpServletUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtFilter extends OncePerRequestFilter {
    private final JwtParser jwtParser;
    private final JwtValidator jwtValidator;
    private final HttpServletUtils servletUtils;

    public JwtFilter(JwtParser jwtParser, JwtValidator jwtValidator, HttpServletUtils servletUtils) {
        this.jwtParser = jwtParser;
        this.jwtValidator = jwtValidator;
        this.servletUtils = servletUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = getAccessToken(request);

        if ( accessToken != null ) {
            if ( jwtValidator.validateToken(accessToken) ) {
                setAuthentication(accessToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String accessToken) {
        Claims claims = jwtParser.getClaims(accessToken);

        Authentication authentication = new JwtAuthenticationToken(
            ((Number) claims.get("userId")).longValue(),
            (String) claims.get("email"),
            (String) claims.get("name"),
            (String) claims.get("phone"),
            extractRolesFromClaims(claims)
        );
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    private Set<UserRoleEnum> extractRolesFromClaims(Claims claims) {
        List<String> roleNames = (List<String>) claims.get("roles");
        return roleNames.stream()
            .map(UserRoleEnum::valueOf)
            .collect(Collectors.toSet());
    }
    private String getAccessToken(HttpServletRequest request) {
        return servletUtils.getAccessToken(request).orElse(null);
    }
}
