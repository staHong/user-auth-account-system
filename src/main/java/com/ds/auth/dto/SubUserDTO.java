package com.ds.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 부계정 DTO 클래스.
 * 클라이언트와 서버 간에 데이터를 전송할 때 사용됩니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubUserDTO {
    @NotBlank(message = "아이디는 필수 입력 항목입니다.")
    @Pattern(regexp = "^[a-zA-Z]{6,20}$", message = "아이디는 영문자로 이루어진 6~20자여야 합니다.")
    private String id;  // 부계정아이디

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "비밀번호는 최소 8자 이상이며, 영문자, 숫자, 특수문자를 포함해야 합니다.")
    private String password;  // 비밀번호

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "올바른 이메일 형식을 입력하세요.")
    private String email;  // 이메일

    private String memberId; // 주계정 아이디
    private String managerName;  // 담당자명
    private String departmentName;  // 부서명
    private String contactNumber;  // 연락처
    private String memoDescription;  // 메모
    private LocalDateTime joinDate;  // 가입일시
    private LocalDateTime deletedDate;  // 삭제일시
    private Boolean isDeleted;  // 삭제 여부
}
