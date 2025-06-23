package com.ds.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 이메일 인증번호 생성, 저장 및 검증을 담당하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final StringRedisTemplate redisTemplate;

    @Value("${email.verification.expiration-time}")
    private int expirationTime;

    @Value("${email.verification.range-min}")
    private int rangeMin;

    @Value("${email.verification.range-max}")
    private int rangeMax;

    /**
     * 랜덤한 인증번호를 생성하고 Redis에 저장한다.
     *
     * @param email 인증번호를 받을 이메일 주소
     * @return 생성된 인증번호 (6자리)
     */
    public String generateAndSaveVerificationCode(String email) {
        String verificationCode = generateVerificationCode();
        redisTemplate.opsForValue().set(email, verificationCode, expirationTime, TimeUnit.MINUTES);
        return verificationCode;
    }

    /**
     * 6자리 랜덤 인증번호 생성
     *
     * @return 생성된 6자리 숫자 인증번호
     */
    private String generateVerificationCode() {
        Random random = new Random();
        int code = rangeMin + random.nextInt(rangeMax - rangeMin + 1); // 100000 ~ 999999 범위
        return String.valueOf(code);
    }

    /**
     * 입력된 인증번호가 저장된 값과 일치하는지 검증한다.
     *
     * @param email     인증을 요청한 이메일 주소
     * @param inputCode 사용자가 입력한 인증번호
     * @return 인증 결과 코드 (SUCCESS, INVALID_CODE, EXPIRED)
     */
    public VerificationStatus verifyCode(String email, String inputCode) {
        String storedCode = redisTemplate.opsForValue().get(email);

        if (storedCode == null) {
            return VerificationStatus.EXPIRED; // 인증번호가 만료됨
        }

        if (!storedCode.equals(inputCode)) {
            return VerificationStatus.INVALID_CODE; // 인증번호 불일치
        }

        redisTemplate.delete(email); // 인증 성공 시 Redis에서 제거
        return VerificationStatus.SUCCESS;
    }

    public enum VerificationStatus {
        SUCCESS,         // 인증 성공
        INVALID_CODE,    // 인증번호 불일치
        EXPIRED          // 인증번호 만료
    }
}
