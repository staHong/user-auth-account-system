package com.ds.auth.controller;

import com.ds.auth.dto.UserDTO;
import com.ds.auth.response.ApiResponse;
import com.ds.auth.service.EmailService;
import com.ds.auth.service.EmailVerificationService;
import com.ds.auth.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final EmailService emailService;
    private final EmailVerificationService verificationService;
    private final UserService userService;

    @Value("${email.verification.subject}")
    private String emailSubject;

    @Value("${email.verification.content}")
    private String emailContent;

    /**
     * 이메일로 인증번호를 전송하는 API
     *
     * @param email 이메일 주소 (쿼리 파라미터)
     * @return 이메일 전송 성공 메시지
     */
    @PostMapping("/email/send")
    public ResponseEntity<ApiResponse<Void>> sendVerificationEmail(@RequestParam String email) {
        // 인증번호 생성 및 저장
        String code = verificationService.generateAndSaveVerificationCode(email);
        String content = emailContent.replace("{code}", code); // {code}를 실제 인증번호로 변경

        // 이메일 전송 시도
        boolean isSent = emailService.sendEmail(email, emailSubject, content);
        if (!isSent) { // 이메일 전송 실패 시 500 응답 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송에 실패하였습니다."));
        }

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "인증코드가 발송되었습니다.", null));
    }

    /**
     * 이메일 중복값 확인 후 이메일로 인증번호를 전송하는 API
     *
     * @param email 이메일 주소 (쿼리 파라미터)
     * @return 이메일 전송 성공 메시지
     */
    @PostMapping("/email/checkEmailAndSend")
    public ResponseEntity<ApiResponse<String>> checkAndSendVerificationEmail(@RequestParam String email) {

        boolean isDuplicate = userService.checkEmailDuplicate(email);

        if (isDuplicate) {
            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "이미 사용 중인 이메일입니다.", "DUPLICATE"));
        }

        // 인증번호 생성 및 저장
        String code = verificationService.generateAndSaveVerificationCode(email);
        String content = emailContent.replace("{code}", code); // {code}를 실제 인증번호로 변경

        // 이메일 전송 시도
        boolean isSent = emailService.sendEmail(email, emailSubject, content);
        if (!isSent) { // 이메일 전송 실패 시 500 응답 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송에 실패하였습니다."));
        }

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "인증코드가 발송되었습니다.", null));
    }

    /**
     * 이메일 인증번호를 검증하는 API
     *
     * @param email 이메일 주소 (쿼리 파라미터)
     * @param code  사용자가 입력한 인증번호 (쿼리 파라미터)
     * @return 인증 성공/인증 실패/인증 만료/서버 오류
     */
    @PostMapping("/email/verify")
    public ResponseEntity<ApiResponse<Void>> verifyCode(@RequestParam String email, @RequestParam String code) {
        EmailVerificationService.VerificationStatus status = verificationService.verifyCode(email, code);

        return switch (status) {
            case SUCCESS -> ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "인증되었습니다.", null));
            case INVALID_CODE ->
                    ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "인증코드가 맞지 않습니다."));
            case EXPIRED ->
                    ResponseEntity.status(HttpStatus.GONE).body(ApiResponse.error(HttpStatus.GONE, "인증번호가 만료되었습니다. 다시 인증번호를 요청해 주세요."));
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류 발생"));
        };
    }

    /**
     * 아이디 중복 검사 API
     *
     * @param id 사용자 아이디 (Query Parameter)
     * @return 중복 여부 (true: 중복 있음, false: 사용 가능)
     */
    @GetMapping("/check-id")
    public ResponseEntity<ApiResponse<String>> checkIdDuplicate(@RequestParam String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("아이디는 필수 입력 항목입니다.");
            }

            boolean isDuplicate = userService.checkIdDuplicate(id);

            if (isDuplicate) {
                return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "이미 사용 중인 아이디입니다.", "DUPLICATE"));
            } else {
                return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "사용 가능한 아이디입니다.", "AVAILABLE"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."));
        }
    }

    /**
     * 회원가입 API
     *
     * @param userDTO 회원가입 요청 DTO (id, email, password 포함)
     * @return 회원가입 성공 시 200/ 아이디 중복 시, 400/ 오류 시, 500
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> registerUser(@Valid @ModelAttribute UserDTO userDTO, BindingResult bindingResult) {
        // 유효성 검사 실패 시 400 Bad Request 반환
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError().getDefaultMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, errorMessage));
        }
        try {
            // 회원가입 로직 실행
            userService.registerUser(userDTO);
            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "회원가입 완료 되었습니다.", null));
        } catch (IllegalArgumentException e) {
            // 아이디 중복 시 `400 Bad Request`
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage()));
        } catch (RuntimeException e) {
            // 파일 업로드 실패, DB 저장 오류 등 `500 Internal Server Error`
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "회원가입 실패하였습니다."));
        }
    }

    /**
     * 아이디 찾기 API
     *
     * @param corpRegNo 법인등록번호
     * @param bizRegNo 사업자등록번호
     * @param email 이메일
     * @return 검증 결과 메시지 (아이디 전송 또는 오류 메시지)
     */
    @PostMapping("/find-id")
    public ResponseEntity<ApiResponse<Void>> findUserId(
            @RequestParam String corpRegNo,
            @RequestParam String bizRegNo,
            @RequestParam String email) {

        String result = userService.findUserId(corpRegNo, bizRegNo, email);

        // 아이디 찾기 성공 시 (200 OK)
        if ("이메일로 아이디를 보내드렸습니다.".equals(result)) {
            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, result, null));
        }

        // 이메일 전송 실패 (500 Internal Server Error)
        if ("이메일 전송에 실패하였습니다. 다시 시도해주세요.".equals(result)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, result));
        }

        // 입력값 불일치 또는 정보 없음 (400 Bad Request)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST, result));
    }

    /**
     * 비밀번호 변경 시, 아이디 존재 여부 검사 후, 이메일 가져오기 API
     *
     * @param id 사용자 아이디 (Query Parameter)
     * @return email 이메일
     */
    @GetMapping("/exists-id")
    public ResponseEntity<ApiResponse<String>> existsIdAndGetEmail(@RequestParam String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("아이디는 필수 입력 항목입니다.");
            }

            String email = userService.existsIdAndGetEmail(id);

            if (email == null) {
                return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "존재하지 않는 아이디입니다.", email));
            } else {
                return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "존재하는 아이디입니다.", email));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."));
        }
    }

    /**
     * 비밀번호 변경 API
     *
     * @param id 사용자 아이디
     * @param email 이메일
     * @param newPassword 새 비밀번호
     *
     * @return 비밀번호 성공 시, 200/ 오류 시, 500
     */
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changPassword(@RequestParam String id, String email, String newPassword) {
        try {
            if(!userService.updatePassword(id, email, newPassword)){  // DB 내 아이디와 이메일 일치하는 정보가 없을 시
                return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "정보가 일치하지 않습니다. 다시 입력해주세요.", null));
            }
            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "새 비밀번호로 변경 되었습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."));
        }
    }

    /**
     * 로그인 API
     *
     * @param id 사용자 아이디
     * @param password 사용자 비밀번호
     * @return 로그인 성공 시 JWT 토큰 반환
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestParam String id, @RequestParam String password) {
        try {
            // 로그인 시 JWT 토큰 생성
            String token = userService.login(id, password);

            // 로그인 성공 시 JWT 토큰 반환
            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "로그인 성공", token));
        } catch (IllegalArgumentException e) {
            // 아이디나 비밀번호 오류
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage()));
        } catch (ExpiredJwtException e) {
            // 만료된 토큰에 대한 처리
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."));
        } catch (Exception e) {
            // 그 외 예외(401)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage()));
        }
    }

    /**
     * 로그아웃 API (SecurityContextHolder 방식)
     *
     * @return ApiResponse<Void> 로그아웃 처리 결과 메시지
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        try {
            // 인증된 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();  // 인증된 사용자 정보

            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않거나 존재하지 않습니다."));
            }

            // SecurityContextHolder 초기화 (로그아웃 처리)
            SecurityContextHolder.clearContext();

            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "로그아웃 성공", null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생했습니다."));
        }
    }
}
