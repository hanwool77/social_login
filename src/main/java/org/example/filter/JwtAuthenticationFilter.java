package org.example.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.util.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.Claims;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        
        // OAuth 관련 경로는 JWT 검증 건너뛰기
        if (shouldSkipJwtValidation(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 1. Authorization Header에서 JWT 토큰 추출
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7); // "Bearer " 제거

            try {
                // 2. JWT 토큰 검증 및 이메일 추출
                Claims claims = jwtUtil.getClaimsFromToken(token);
                String email = claims.getSubject();

                System.out.println("JWT Filter - Email from token: " + email);

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 3. 사용자 정보 로드 (이메일을 username으로 사용)
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                    // 4. 토큰이 유효하면 SecurityContext에 인증 정보 설정
                    if (jwtUtil.validateToken(token, email)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        
                        System.out.println("JWT authentication successful for: " + email);
                    }
                }
            } catch (Exception e) {
                System.err.println("JWT authentication failed: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
    
    private boolean shouldSkipJwtValidation(String requestURI) {
        // OAuth 관련 경로들은 JWT 검증 건너뛰기
        return requestURI.startsWith("/oauth2/") ||
               requestURI.startsWith("/login/") ||
               requestURI.equals("/auth/login/google") ||
               requestURI.equals("/auth/login/naver") ||
               requestURI.startsWith("/h2-console/") ||
               requestURI.startsWith("/test/") ||
               requestURI.equals("/") ||
               requestURI.equals("/error");
    }
}