package com.ds.auth.util;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 로그인 요청 URL인지 확인 (예: "/login" 경로는 JWT 토큰 필요 없음)
        String requestURI = request.getRequestURI();

        // 로그인 요청일 경우 Authorization 헤더를 확인하지 않음
        if (requestURI.equals("/user/login")) {
            filterChain.doFilter(request, response);
            return; // 로그인 요청에서는 필터를 더 이상 진행하지 않음
        }

        // Authorization 헤더에서 JWT 토큰 추출
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {  // "Bearer "로 시작하는 토큰만 처리
            token = token.substring(7);  // "Bearer " 부분 제거


            try {
                // claims 추출 (JWT 검증)
                Claims claims = jwtUtil.extractClaims(token);
                String username = claims.getSubject();

                // 인증 정보 설정
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, null);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            }catch (Exception e) {
                // JWT 검증 실패 시 예외 처리 401
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰");
                return;
            }
        }

        filterChain.doFilter(request, response);  // 필터 체인에 요청을 전달
    }
}
