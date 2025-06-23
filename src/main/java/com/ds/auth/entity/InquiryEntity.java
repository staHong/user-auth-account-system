package com.ds.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_inquiry")  // 테이블 이름 지정
public class InquiryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inq_id")
    private Long id;  // ID (PK)

    @Column(name = "user_nm", length = 50, nullable = false)
    private String userName;  // 이름

    @Column(name = "corp_nm", length = 100, nullable = false)
    private String companyName;  // 회사명

    @Column(name = "email_addr", length = 100, nullable = false)
    private String email;  // 이메일

    @Column(name = "inq_content", length = 4000, nullable = false)
    private String inquiryContent;  // 문의 내용

    @Column(name = "ans_content", length = 4000)
    @Setter
    private String answerContent;  // 답변 내용

    @Column(name = "is_public")
    private boolean isPublic;  // 공개 여부

    @Column(name = "create_dtm", nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 등록일시

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();  // 저장될 때 자동으로 현재 시간 입력
    }
}
