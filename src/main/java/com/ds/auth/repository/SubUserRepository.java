package com.ds.auth.repository;

import com.ds.auth.entity.SubUserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * 부계정에 대한 CRUD 작업을 처리하는 리포지토리.
 */
public interface SubUserRepository extends JpaRepository<SubUserEntity, String> {

    /**
     * 부계정 아이디로 부계정을 조회합니다.(탈퇴나 삭제하지 않은 계정만 조회)
     *
     * @param id 부계정 아이디
     * @return 부계정 정보 (옵셔널)
     */
    Optional<SubUserEntity> findByIdAndIsDeletedFalse(String id);

    /**
     * 부계정 이메일로 부계정을 조회합니다. (탈퇴나 삭제하지 않은 계정만 조회)
     *
     * @param email 부계정 이메일
     * @return 부계정 정보 (옵셔널)
     */
    Optional<SubUserEntity> findByEmailAndIsDeletedFalse(String email);

    /**
     * 부계정 비밀번호 업데이트.
     *
     * @param id 아이디
     * @param encryptedPassword 암호화된 비밀번호
     */
    @Transactional
    @Modifying
    @Query("UPDATE SubUserEntity s SET s.password = :encryptedPassword WHERE s.id = :id")
    void updatePasswordById(String id, String encryptedPassword);

    /**
     * 주계정 아이디로 부계정 목록 조회 (탈퇴나 삭제하지 않은 계정만 조회)
     *
     * @param id 아이디
     * @return 활성화된 부계정 목록 조회
     */
    List<SubUserEntity> findByMemberIdAndIsDeletedFalse(String id);

    /**
     * 이메일 중복 검사
     *
     * @param email 사용자 이메일 (회원가입 시 입력)
     * @return 중복 여부 (true: 중복 있음, false: 사용 가능)
     */
    boolean existsByEmail(String email);

    /**
     * 특정 사용자의 부계정 개수를 조회합니다.
     *
     * @param memberId 부계정을 소유한 주계정 사용자의 ID (String 타입)
     * @return 해당 사용자의 부계정 개수 (int)
     */
    int countByMemberIdAndIsDeletedFalse(String memberId);
}
