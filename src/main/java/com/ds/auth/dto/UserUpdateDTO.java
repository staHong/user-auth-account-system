package com.ds.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDTO {

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "올바른 이메일 형식을 입력하세요.")
    private String email; // 이메일

    private String managerName;  // 담당자명
    private String managerPhoneNumber;  // 담당자 연락처
    private String responsibleName;  // 책임자명
    private String responsiblePhoneNumber;  // 책임자 연락처
}
