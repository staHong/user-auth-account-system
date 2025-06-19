package com.ds.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    @NotBlank(message = "아이디는 필수 입력 항목입니다.")
    @Pattern(regexp = "^[a-zA-Z]{6,20}$", message = "아이디는 영문자로 이루어진 6~20자여야 합니다.")
    private String id;          // 사용자가 직접 입력하는 회원 ID

    private String corpRegNo;   // 법인등록번호
    private String bizRegNo;    // 사업자등록번호
    private String companyName; // 상호명

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "비밀번호는 최소 8자 이상이며, 영문자, 숫자, 특수문자를 포함해야 합니다.")
    private String password;    // 비밀번호

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "올바른 이메일 형식을 입력하세요.")
    private String email;       // 이메일

    private String memberType;  // 회원 분류 코드
    private MultipartFile file;  // 사업자등록증 파일 업로드
    private String businessLicensePath;  // 사업자등록증 파일경로
    private String businessLicenseName;  // 사업자등록증 파일명
    private String managerName;  // 담당자명
    private String managerPhoneNumber;  // 담당자 연락처
    private String responsibleName;  // 책임자명
    private String responsiblePhoneNumber;  // 책임자 연락처
    private LocalDateTime joinDate;  // 가입일시
    private LocalDateTime withdrawalDate;  // 탈퇴일시
    private Boolean isDeleted;  // 삭제 여부
}
