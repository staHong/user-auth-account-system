package com.ds.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 부계정 엔티티 클래스.
 * tb_sub_account 테이블에 매핑됩니다.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_sub_account")
public class SubUserEntity {

    @Id
    @Column(name = "sub_acnt_id", length = 50, nullable = false, unique = true)
    private String id; // 부계정아이디

    @Column(name = "mbr_id", length = 50, nullable = false)
    private String memberId; // 주계정과 연결된 회원 아이디

    @Column(name = "login_pwd", nullable = false)
    private String password; // 부계정 비밀번호

    @Column(name = "email_addr", length = 100, nullable = false, unique = true)
    private String email; // 부계정 이메일

    @Column(name = "mgr_nm", length = 50)
    private String managerName; // 담당자명

    @Column(name = "dept_nm", length = 50)
    private String departmentName; // 부서명

    @Column(name = "tel_no", length = 20)
    private String contactNumber; // 연락처

    @Column(name = "memo_desc", length = 4000)
    private String memoDescription; // 메모

    @Column(name = "join_dtm", nullable = false, updatable = false)
    private LocalDateTime joinDate; // 가입일시

    @Column(name = "deleted_dtm")
    private LocalDateTime deletedDate;  // 삭제일시

    @Column(name = "is_deleted")
    private Boolean isDeleted;  // 삭제 여부

    @PrePersist
    protected void onCreate() {
        this.joinDate = LocalDateTime.now();
        this.isDeleted = false; // 기본값: 삭제되지 않음
    }
}

