package com.ds.auth.repository;

import com.ds.auth.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 회원 정보를 조회하는 JPA Repository
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    /**
     * 아이디 중복 검사
     *
     * @param id 사용자 아이디 (회원가입 시 입력)
     * @return 중복 여부 (true: 중복 있음, false: 사용 가능)
     */
    boolean existsById(String id);

    /**
     * 이메일로 주계정 조회(탈퇴한 계정은 제외)
     * @param email 이메일
     * @return 회원 Entity
     */
    Optional<UserEntity> findByEmailAndIsDeletedFalse(String email);

    /**
     * 법인등록번호가 존재하는지 확인(탈퇴한 계정은 제외)
     * @param corpRegNo 법인등록번호
     * @return 존재하면 true, 없으면 false
     */
    boolean existsByCorpRegNoAndIsDeletedFalse(String corpRegNo);

    /**
     * 사업자등록번호가 존재하는지 확인(탈퇴한 계정은 제외)
     * @param bizRegNo 사업자등록번호
     * @return 존재하면 true, 없으면 false
     */
    boolean existsByBizRegNoAndIsDeletedFalse(String bizRegNo);

    /**
     * 아이디로 회원 조회(탈퇴한 계정은 제외)
     * @param id 회원 아이디
     * @return 회원 Entity
     */
    Optional<UserEntity> findByIdAndIsDeletedFalse(String id);

    /**
     *  비밀번호 업데이트
     *
     * @param id 회원 아이디
     * @param newPassword 새 비밀번호 (암호화된 상태로 전달)
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.password = :newPassword WHERE u.id = :id")
    void updatePasswordById(String id, String newPassword);

    /**
     * 이메일 중복 검사
     *
     * @param email 사용자 이메일 (회원가입 시 입력)
     * @return 중복 여부 (true: 중복 있음, false: 사용 가능)
     */
    boolean existsByEmail(String email);
}
