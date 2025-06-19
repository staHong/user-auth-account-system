package com.ds.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${jwt.expiration-time}")
    private long expirationTime;

    /**
     * JWT 토큰을 생성하는 메서드
     *
     * @param id 사용자 아이디
     * @return 생성된 JWT 토큰
     */
    public String generateToken(String id, boolean isPaid, String userType) {
        return Jwts.builder()
                .setSubject(id)
                .claim("isPaid", isPaid) // 추가 클레임으로 결제 여부 추가
                .claim("userType", userType) // 추가 클레임으로 회원 유형(주계정/부계정)
                .setExpiration(new Date(System.currentTimeMillis()  + expirationTime)) // 만료 시간 6시간
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /**
     * JWT에서 Claims(내용) 추출
     *
     * @param token JWT 토큰
     * @return Claim 내용
     */
    public Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)  // JWT에서 Claims 파싱
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(null, null, "토큰이 만료되었습니다.");
        } catch (Exception e) {
            throw new RuntimeException("유효하지 않은 토큰입니다.", e);
        }
    }
}
