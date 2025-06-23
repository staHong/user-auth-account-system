package com.ds.auth.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InquiryDTO {
    private Long id;  // ID
    private String userName;  // 이름
    private String companyName;  // 회사명
    private String email;  // 이메일
    private String inquiryContent;  // 문의 내용
    private String answerContent;  // 답변 내용 (선택 값)
    private boolean isPublic;  // 공개 여부 (true: 공개, false: 비공개)
    private LocalDateTime createdAt;  // 등록일시
}
