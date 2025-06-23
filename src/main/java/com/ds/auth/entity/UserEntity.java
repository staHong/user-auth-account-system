package com.ds.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 주계정 엔티티 클래스.
 * tb_member 테이블에 매핑됩니다.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_member")
public class UserEntity {

    @Id
    @Column(name = "mbr_id", length = 50, nullable = false, unique = true)
    private String id;  // 회원아이디

    @Column(name = "corp_reg_no", length = 20, nullable = false)
    private String corpRegNo;  // 법인등록번호

    @Column(name = "biz_reg_no", length = 20, nullable = false)
    private String bizRegNo;  // 사업자등록번호

    @Column(name = "corp_nm", length = 100, nullable = false)
    private String companyName;  // 상호명

    @Column(name = "login_pwd", length = 255, nullable = false)
    private String password;  // 비밀번호 (암호화)

    @Column(name = "email_addr", length = 100, nullable = false, unique = true)
    private String email;  // 이메일

    @Column(name = "mbr_type_cd", length = 10, nullable = false)
    private String memberType;  // 회원분류코드

    @Column(name = "biz_lic_file_path", length = 255, nullable = false)
    private String businessLicensePath;  // 사업자등록증 파일경로

    @Column(name = "biz_lic_file_nm", length = 100, nullable = false)
    private String businessLicenseName;  // 사업자등록증 파일명

    @Column(name = "cntrct_file_path", length = 255)
    private String contractFilePath;  // 계약서 파일경로

    @Column(name = "cntrct_file_nm", length = 100)
    private String contractFileName;  // 계약서 파일명

    @Column(name = "mgr_nm", length = 50)
    private String managerName;  // 담당자명

    @Column(name = "mgr_tel_no", length = 20)
    private String managerPhoneNumber;  // 담당자 연락처

    @Column(name = "resp_nm", length = 50)
    private String responsibleName;  // 책임자명

    @Column(name = "resp_tel_no", length = 20)
    private String responsiblePhoneNumber;  // 책임자 연락처

    @Column(name = "join_dtm", nullable = false, updatable = false)
    private LocalDateTime joinDate;  // 가입일시

    @Column(name = "withdraw_dtm")
    private LocalDateTime withdrawalDate;  // 탈퇴일시

    @Column(name = "is_deleted")
    private Boolean isDeleted;  // 삭제 여부

    @Column(name = "is_paid")
    private Boolean isPaid;  // 결제 여부

    @PrePersist
    protected void onCreate() {
        this.joinDate = LocalDateTime.now();
        this.isDeleted = false; // 기본값: 삭제되지 않음
        this.isPaid = false; // 기본값: 결제되지 않음
    }
}
