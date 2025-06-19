package com.ds.auth.service;

import com.ds.auth.dto.SubUserDTO;
import com.ds.auth.entity.SubUserEntity;
import com.ds.auth.mapper.SubUserMapper;
import com.ds.auth.repository.SubUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubUserService {

    private final SubUserRepository subUserRepository;
    private final SubUserMapper subUserMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 사용자 아이디으로 사용자 정보 조회 후 List<DTO>로 반환
     *
     * @param id: 사용자 아이디
     * @return: SubUserDto - 부계정 정보를 담은 DTO 객체
     */
    public List<SubUserDTO> getSubUsersByMemberId(String id) {
        List<SubUserEntity> subUsers = subUserRepository.findByMemberIdAndIsDeletedFalse(id);
        return subUsers.stream().map(subUserMapper::toDto).collect(Collectors.toList());
    }

    /**
     * 마이페이지 부계정관리를 삭제하는 API
     *
     * @param: subUserId 부계정 ID
     */
    @Transactional
    public void deleteSubUser(String subUserId) {
        Optional<SubUserEntity> subUserOptional = subUserRepository.findByIdAndIsDeletedFalse(subUserId);

        if (subUserOptional.isPresent()) {
            SubUserEntity subUser = subUserOptional.get();
            subUser.setIsDeleted(true); // 삭제 여부 true
            subUser.setDeletedDate(LocalDateTime.now()); // 삭제 일시 기록
            subUserRepository.save(subUser);
        } else {
            throw new IllegalArgumentException("부계정을 찾을 수 없습니다.");
        }
    }

    /**
     * 부계정 추가 서비스
     * 주계정 ID를 기반으로 새로운 부계정을 생성하여 저장.
     *
     * @param memberId 주계정 ID (부계정의 소유자)
     * @param subUserDTO 부계정 등록을 위한 정보 (DTO)
     * @throws IllegalArgumentException 부계정 ID가 중복되었을 경우 예외 발생
     */
    @Transactional
    public void addSubUser(String memberId, SubUserDTO subUserDTO) {
        // 1. 부계정 ID 중복 확인
        if (subUserRepository.existsById(subUserDTO.getId())) {
            throw new IllegalArgumentException("이미 존재하는 부계정 ID입니다.");
        }

        // 2. 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(subUserDTO.getPassword());
        subUserDTO.setPassword(encryptedPassword);

        // 3. DTO → Entity 변환 후 저장
        SubUserEntity subUserEntity = subUserMapper.toEntity(subUserDTO);
        subUserEntity.setMemberId(memberId); // 주계정 ID 설정
        subUserRepository.save(subUserEntity);
    }

    /**
     * 사용자의 부계정 개수를 조회합니다.
     *
     * @param userId 주계정 사용자의 ID
     * @return 해당 사용자의 부계정 개수 (int) - 삭제된거 제외
     */
    public int getSubUserCount(String userId) {
        return subUserRepository.countByMemberIdAndIsDeletedFalse(userId);
    }
}
