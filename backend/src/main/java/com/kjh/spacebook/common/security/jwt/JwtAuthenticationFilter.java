package com.kjh.spacebook.common.security.jwt;

import java.io.IOException;
import java.util.List;

import com.kjh.spacebook.domain.user.enums.Role;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest req,
        @NonNull HttpServletResponse res,
        @NonNull FilterChain chain) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(req, res);

            return;
        }

        String token = resolveToken(req);

        if (token == null || token.isBlank()) {
            chain.doFilter(req, res);

            return;
        }

        try {
            Claims claims = jwtUtil.validateAndGetClaims(token);
            Long userId = Long.valueOf(claims.getSubject());
            String roleValue = claims.get("role", String.class);

            if (roleValue == null || roleValue.isBlank()) {
                SecurityContextHolder.clearContext();
                chain.doFilter(req, res);

                return;
            }

            Role role;
            try {
                role = Role.valueOf(roleValue);
            } catch (IllegalArgumentException e) {
                log.warn("유효하지 않은 role: {}", roleValue);
                SecurityContextHolder.clearContext();
                chain.doFilter(req, res);

                return;
            }

            var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
            var authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.warn("JWT 만료: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.warn("JWT 서명 불일치: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            log.warn("JWT 인증 실패: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }
        chain.doFilter(req, res);
    }

    private String resolveToken(HttpServletRequest req) {
        String h = req.getHeader("Authorization");

        if (h != null && h.startsWith("Bearer ")) {
            return h.substring(7);
        }

        return null;
    }
}
