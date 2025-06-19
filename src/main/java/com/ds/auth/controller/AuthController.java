package com.ds.auth.controller;

import com.ds.auth.dto.SubUserDTO;
import com.ds.auth.dto.UserDTO;
import com.ds.auth.dto.UserUpdateDTO;
import com.ds.auth.response.ApiResponse;
import com.ds.auth.service.SubUserService;
import com.ds.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final SubUserService subUserService;

    /**
     * 마이페이지 정보를 조회하는 API
     * JWT 토큰을 사용하여 인증된 사용자 정보만 조회합니다.
     *
     * @return: 마이페이지 정보를 담은 사용자 DTO를 반환
     */
    @GetMapping("/myPage")
    public ResponseEntity<ApiResponse<UserDTO>> getMyPage() {
        try {
            // 인증된 사용자의 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();  // 인증된 사용자 정보
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않거나 존재하지 않습니다."));
            }

            String userId = authentication.getName();  // 인증된 사용자 이름

            // 사용자 정보 조회
            UserDTO userDto = userService.getUserById(userId);
            if (userDto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(HttpStatus.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."));
            }

            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "마이페이지 정보 조회 성공", userDto));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생했습니다."));
        }
    }

    /**
     * 회원 탈퇴 API
     *
     * @param id      탈퇴할 회원 ID
     * @return 회원 탈퇴 처리 결과
     */
    @PatchMapping("/myPage/withdraw/{id}")
    public ResponseEntity<ApiResponse<Void>> withdrawUser(@PathVariable String id) {
        try {
            // 인증된 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않거나 존재하지 않습니다."));
            }

            String userId = authentication.getName(); // 현재 로그인한 사용자 ID

            // 요청한 ID와 로그인한 ID가 일치하는지 확인
            if (!userId.equals(id)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, "본인만 탈퇴할 수 있습니다."));
            }

            // 회원 탈퇴 처리
            userService.withdrawUser(id);

            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "회원 탈퇴 성공", null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "회원 탈퇴 처리 중 오류 발생"));
        }
    }

    /**
     * 회원정보 수정 API
     * @param userUpdateDTO 변경할 정보 (이메일, 담당자명, 담당자 연락처, 책임자명, 책임자 연락처)
     * @return ResponseEntity<ApiResponse<Void>>
     */
    @PostMapping("/myPage/update")
    public ResponseEntity<ApiResponse<Void>> updateUser(@Valid @RequestBody UserUpdateDTO userUpdateDTO, BindingResult bindingResult){
        // 유효성 검사 실패 시 400 Bad Request 반환
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError().getDefaultMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, errorMessage));
        }

        try {
            // 인증된 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않거나 존재하지 않습니다."));
            }

            String userId = authentication.getName(); // 인증된 사용자 ID

            // 회원정보 업데이트
            userService.updateUserInfo(userId, userUpdateDTO);
            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "회원정보가 수정되었습니다.", null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "회원정보 수정 중 오류 발생"));
        }
    }

    /**
     * 마이페이지 부계정관리를 조회하는 API
     * JWT 토큰을 사용하여 인증된 사용자 정보만 조회합니다.
     *
     * @param: 없음
     * @return: 부계정 SubUserDto 반환
     */
    @GetMapping("/myPage/subUserList")
    public ResponseEntity<ApiResponse<List<SubUserDTO>>> getMyPageAndSubUser() {
        try {
            // 인증된 사용자의 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();  // 인증된 사용자 정보
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않거나 존재하지 않습니다."));
            }

            String userId = authentication.getName();  // 인증된 사용자 아아디

            // 사용자 아이디로 가져온 부계정 정보
            List<SubUserDTO> subUserDTOList = subUserService.getSubUsersByMemberId(userId);
            // 부계정 정보가 없을 경우 200 OK + data: null
            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "부계정관리 정보 조회 성공",
                    (subUserDTOList == null || subUserDTOList.isEmpty()) ? null : subUserDTOList));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생했습니다."));
        }
    }

    /**
     * 마이페이지 부계정관리를 삭제하는 API
     * JWT 토큰을 사용하여 인증된 사용자 정보만 조회합니다.
     *
     * @param: subUserId 부계정 ID
     * @return: ResponseEntity<ApiResponse<Void>>
     */
    @PatchMapping("/myPage/subUser/{subUserId}/delete")
    public ResponseEntity<ApiResponse<Void>> softDeleteSubUser(@PathVariable String subUserId) {
        try {
            // 인증된 사용자의 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();  // 인증된 사용자 정보
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않거나 존재하지 않습니다."));
            }
            // 부계정 삭제 처리 (논리적 삭제)
            subUserService.deleteSubUser(subUserId);

            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "부계정 삭제 성공", null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "부계정 삭제 중 오류가 발생했습니다."));
        }
    }

    /**
     * 부계정 추가 API
     * JWT 인증된 사용자(주계정)만 부계정을 추가할 수 있음.
     *
     * @param subUserDTO 부계정 정보를 담은 DTO 객체
     * @param bindingResult 요청 데이터의 유효성 검사 결과
     * @return ResponseEntity<ApiResponse<Void>> 부계정 추가 성공 또는 실패 응답
     */
    @PostMapping("/myPage/subUser/add")
    public ResponseEntity<ApiResponse<Void>> addSubUser(@Valid @RequestBody SubUserDTO subUserDTO, BindingResult bindingResult) {
        // 유효성 검사 실패 시 400 Bad Request 반환
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, bindingResult.getFieldError().getDefaultMessage()));
        }

        try {
            // 인증된 사용자의 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();  // 인증된 사용자 정보
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않거나 존재하지 않습니다."));
            }

            String userId = authentication.getName();  // 인증된 사용자 아아디

            // 최대 10개의 부계정만 추가 가능
            int subUserCount = subUserService.getSubUserCount(userId);
            if (subUserCount >= 10) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "부계정은 최대 10개까지만 추가할 수 있습니다."));
            }

            // 부계정 추가
            subUserService.addSubUser(userId, subUserDTO);

            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "부계정 추가 성공", null));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "부계정 추가 중 오류 발생"));
        }
    }

}
