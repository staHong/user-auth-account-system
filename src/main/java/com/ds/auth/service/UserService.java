package com.ds.auth.service;

import com.ds.auth.dto.SubUserDTO;
import com.ds.auth.dto.UserUpdateDTO;
import com.ds.auth.entity.SubUserEntity;
import com.ds.auth.dto.UserDTO;
import com.ds.auth.entity.UserEntity;
import com.ds.auth.mapper.UserMapper;
import com.ds.auth.repository.SubUserRepository;
import com.ds.auth.repository.UserRepository;
import com.ds.auth.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final SubUserRepository subUserRepository; // 부계정 리포지토리
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화
    private final EmailService emailService;    // 이메일 전송(아이디 찾기)
    private final JwtUtil jwtUtil; // JWT
    private final FileUploadService fileUploadService; // 파일 업로드
    private final UserMapper userMapper; // 사용자 Mapper
    private final SubUserService subUserService; // 부계정 서비스

    @Value("${email.find-id.subject}")
    private String findIdSubject;

    /**
     * 회원가입 서비스
     *
     * @param userDTO 회원가입 요청 데이터
     */
    @Transactional
    public void registerUser(UserDTO userDTO) {
        // 1. 아이디, 이메일 중복 검사
        if (userRepository.existsById(userDTO.getId())) {
            throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
        }

        if (subUserRepository.existsById(userDTO.getId())) {
            throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
        }

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }

        if (subUserRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }

        // 2. 파일 업로드 처리
        MultipartFile file = userDTO.getFile();
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("사업자등록증 파일은 필수입니다.");
        }

        String filePath = fileUploadService.saveFile(file); // 파일 저장
        userDTO.setBusinessLicensePath(filePath);
        userDTO.setBusinessLicenseName(file.getOriginalFilename());

        try {
            // 3. 비밀번호 암호화
            String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
            userDTO.setPassword(encryptedPassword);

            // 4. DTO → Entity 변환 후 DB 저장
            UserEntity userEntity = userMapper.toEntity(userDTO);
            userRepository.save(userEntity);
        }catch (Exception e) {
            // 회원가입 실패 시 업로드된 파일 삭제
            fileUploadService.deleteFile(filePath);
            throw new RuntimeException("회원가입 실패: " + e.getMessage());
        }
    }

    /**
     * 아이디 중복 검사
     *
     * @param id 사용자 아이디 (회원가입 시 입력)
     * @return 중복 여부 (true: 중복 있음, false: 사용 가능)
     */
    public boolean checkIdDuplicate(String id) {

        if(userRepository.existsById(id)){
            return true;
        }

        return subUserRepository.existsById(id);
    }

    /**
     * 이메일 중복 검사
     *
     * @param email 사용자 이메일 (회원가입 시 입력)
     * @return 중복 여부 (true: 중복 있음, false: 사용 가능)
     */
    public boolean checkEmailDuplicate(String email) {

        if(userRepository.existsByEmail(email)){
            return true;
        }

        return subUserRepository.existsByEmail(email);
    }

    /**
     * 법인번호, 사업자등록번호, 이메일을 검증하고, 모두 일치하면 이메일 전송
     *
     * @param corpRegNo 법인등록번호
     * @param bizRegNo 사업자등록번호
     * @param email 이메일
     * @return 검증 결과 메시지 (아이디 전송 또는 오류 메시지)
     */
    public String findUserId(String corpRegNo, String bizRegNo, String email) {
        try {
            boolean corporateExists = userRepository.existsByCorpRegNoAndIsDeletedFalse(corpRegNo);
            boolean businessExists = userRepository.existsByBizRegNoAndIsDeletedFalse(bizRegNo);

            // 2. 불일치 시 오류 메시지 반환
            if (!corporateExists) {
                return "입력된 법인등록번호가 불일치 합니다. 다시 입력해주세요.";
            }
            if (!businessExists) {
                return "입력된 사업자 등록번호가 불일치 합니다. 다시 입력해주세요.";
            }

            // 1. 주계정에서 이메일 확인
            Optional<UserEntity> userEntity = userRepository.findByEmailAndIsDeletedFalse(email);

            // 주계정이 이메일을 가지고 있으면
            if (userEntity.isPresent()) {
                // 주계정의 법인번호와 사업자등록번호 일치 여부 확인
                if (!userEntity.get().getCorpRegNo().equals(corpRegNo)) {
                    return("입력된 법인등록번호가 불일치 합니다. 다시 입력해주세요.");
                }
                if (!userEntity.get().getBizRegNo().equals(bizRegNo)) {
                    return("입력된 사업자 등록번호가 불일치 합니다. 다시 입력해주세요.");
                }

                // 주계정 아이디 반환
                String userId = userEntity.get().getId();

                // 이메일 아이디 찾기 내용 구성
                String content = String.format(
                        "<p>안녕하세요.</p>" +
                                "<p>요청하신 아이디 찾기 결과를 안내드립니다.</p>" +
                                "<p>회원님의 아이디는 <strong>%s</strong> 입니다.</p>" +
                                "<p>감사합니다.</p>" +
                                "<hr>" +
                                "<p style='font-size: 12px; color: gray;'>본 이메일은 자동 발송되었습니다.</p>",
                        userId
                );

                // 이메일 전송
                boolean emailSent = emailService.sendEmail(email, findIdSubject, content);

                if (emailSent) {
                    return "이메일로 아이디를 보내드렸습니다.";
                } else {
                    throw new IllegalStateException("이메일 전송에 실패하였습니다. 다시 시도해주세요.");
                }
            }

            // 2. 부계정에서 이메일 확인
            Optional<SubUserEntity> subUserEntity = subUserRepository.findByEmailAndIsDeletedFalse(email);

            if (subUserEntity.isPresent()) {
                // 부계정의 이메일이 존재하면, 부계정의 memberId를 통해 주계정 찾기
                String memberId = subUserEntity.get().getMemberId(); // 부계정의 memberId (주계정 id)

                // 주계정의 법인번호와 사업자등록번호 일치 여부 확인
                Optional<UserEntity> relatedUser = userRepository.findByIdAndIsDeletedFalse(memberId);
                if (relatedUser.isPresent()) {
                    UserEntity user = relatedUser.get();

                    if (!user.getCorpRegNo().equals(corpRegNo)) {
                        return ("입력된 법인등록번호가 불일치 합니다. 다시 입력해주세요.");
                    }
                    if (!user.getBizRegNo().equals(bizRegNo)) {
                        return ("입력된 사업자등록번호가 불일치 합니다. 다시 입력해주세요.");
                    }

                    // 부계정 아이디 반환
                    String userId = subUserEntity.get().getId();

                    // 이메일 아이디 찾기 내용 구성
                    String content = String.format(
                            "<p>안녕하세요.</p>" +
                                    "<p>요청하신 아이디 찾기 결과를 안내드립니다.</p>" +
                                    "<p>회원님의 아이디는 <strong>%s</strong> 입니다.</p>" +
                                    "<p>감사합니다.</p>" +
                                    "<hr>" +
                                    "<p style='font-size: 12px; color: gray;'>본 이메일은 자동 발송되었습니다.</p>",
                            userId
                    );

                    // 이메일 전송
                    boolean emailSent = emailService.sendEmail(email, findIdSubject, content);

                    if (emailSent) {
                        return "이메일로 아이디를 보내드렸습니다.";
                    } else {
                        throw new IllegalStateException("이메일 전송에 실패하였습니다. 다시 시도해주세요.");
                    }
                }
            }

            // 이메일이 주계정과 부계정 모두에서 일치하지 않으면 오류 메시지
            return ("입력된 이메일 주소가 불일치 합니다. 다시 입력해주세요.");

        } catch (Exception e) {
            // 일반적인 예외 처리
            throw new RuntimeException("아이디 찾기 처리 중 오류가 발생하였습니다.", e);
        }
    }


    /**
     * 비밀번호 변경 시, 아이디 존재 여부 검사 후, 이메일 가져오기 (주계정, 부계정 모두 확인)
     *
     * @param id 사용자 아이디 (Query Parameter)
     * @return 이메일
     */
    public String existsIdAndGetEmail(String id) {
        // 1. 주계정에서 아이디 확인
        Optional<UserEntity> userEntity = userRepository.findByIdAndIsDeletedFalse(id);

        if (userEntity.isPresent()) {
            // 주계정이 존재하는 경우, 이메일 반환
            return userEntity.get().getEmail();
        }

        // 2. 부계정에서 아이디 확인
        Optional<SubUserEntity> subUserEntity = subUserRepository.findByIdAndIsDeletedFalse(id);

        // 부계정이 존재하는 경우, 이메일 반환
        return subUserEntity.map(SubUserEntity::getEmail).orElse(null);
    }

    /**
     * 비밀번호 변경 (아이디 & 이메일 확인 후)
     *
     * @param id 회원 아이디
     * @param email 이메일
     * @param newPassword 새 비밀번호
     * @return 변경 성공 여부
     */
    public boolean updatePassword(String id, String email, String newPassword) {

        // 1. 주계정에서 아이디와 이메일 확인
        Optional<UserEntity> userEntity = userRepository.findByIdAndIsDeletedFalse(id);

        if (userEntity.isPresent() && userEntity.get().getEmail().equals(email)) {
            // 주계정의 이메일이 일치하면 비밀번호 변경
            String encryptedPassword = passwordEncoder.encode(newPassword);
            userRepository.updatePasswordById(id, encryptedPassword);
            return true;
        }

        // 2. 부계정에서 아이디와 이메일 확인
        Optional<SubUserEntity> subUserEntity = subUserRepository.findByIdAndIsDeletedFalse(id);

        if (subUserEntity.isPresent() && subUserEntity.get().getEmail().equals(email)) {
            // 부계정의 이메일이 일치하면 비밀번호 변경
            String encryptedPassword = passwordEncoder.encode(newPassword);
            subUserRepository.updatePasswordById(id, encryptedPassword);
            return true;
        }

        // 아이디와 이메일이 일치하지 않으면 false 반환
        return false;
    }

    /**
     * 로그인 처리: 사용자 아이디와 암호화된 비밀번호를 확인하여 JWT 토큰을 반환
     *
     * @param id 사용자 아이디
     * @param password 사용자가 입력한 비밀번호
     * @return 생성된 JWT 토큰
     * @throws RuntimeException 아이디 또는 비밀번호가 불일치할 경우
     */
    public String login(String id, String password) {
        // 1. 사용자 아이디로 사용자 조회 - 탈퇴 회원 제외
        Optional<UserEntity> userEntity = userRepository.findByIdAndIsDeletedFalse(id);

        // 주계정에 해당하는 사용자가 없으면 부계정에서 조회 - 탈퇴나 삭제 회원 제외
        if (userEntity.isEmpty()) {

            Optional<SubUserEntity> subUserEntity = subUserRepository.findByIdAndIsDeletedFalse(id);

            if (subUserEntity.isEmpty()){
                throw new IllegalArgumentException("아이디를 잘못 입력했습니다. 다시 한번 확인해주세요.");
            }

            // 2. 부계정 비밀번호 비교 (DB에 저장된 암호화된 비밀번호와 비교)
            if (!passwordEncoder.matches(password, subUserEntity.get().getPassword())) {
                throw new IllegalArgumentException("비밀번호를 잘못 입력했습니다. 다시 한번 확인해주세요.");
            }

            Optional<UserEntity> userEntityBySub = userRepository.findByIdAndIsDeletedFalse(subUserEntity.get().getMemberId());

            // 3. 부계정에 대한 JWT 토큰 생성
            return jwtUtil.generateToken(id, userEntityBySub.get().getIsPaid(), "SUB");  // 부계정은 isPaid 정보가 없거나 false일 수 있음

        }

        // 2. 주계정 비밀번호 비교 (DB에 저장된 암호화된 비밀번호와 비교)
        if (!passwordEncoder.matches(password, userEntity.get().getPassword())) {
            throw new IllegalArgumentException("비밀번호를 잘못 입력했습니다. 다시 한번 확인해주세요.");
        }

        // 3. 주계정에 대한 JWT 토큰 생성

        return jwtUtil.generateToken(id, userEntity.get().getIsPaid(), "MAIN");  // 로그인 성공 시 JWT 토큰 반환
    }


    /**
     * 사용자 아이디로 사용자 정보 조회 후 DTO로 반환
     *
     * @param id: 사용자 이름
     * @return: UserDto - 사용자 정보를 담은 DTO 객체
     */
    public UserDTO getUserById(String id) {
        // UserRepository를 사용하여 사용자 이름으로 사용자 정보 조회
        Optional<UserEntity> optionalEntity = userRepository.findByIdAndIsDeletedFalse(id);

        // 사용자 정보가 없으면 예외 처리
        if (optionalEntity.isEmpty()) {
            throw new IllegalArgumentException("해당 사용자를 찾을 수 없습니다.");
        }

        // 사용자 정보 조회 후 DTO로 변환
        UserEntity entity = optionalEntity.get();  // Optional에서 실제 UserEntity 객체를 추출
        return userMapper.toDto(entity);
    }

    /**
     * 회원 탈퇴 처리 (DB에서 삭제)
     *
     * @param id 탈퇴할 회원 ID
     */
    @Transactional
    public void withdrawUser(String id) {
        Optional<UserEntity> userOptional = userRepository.findByIdAndIsDeletedFalse(id);
        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            user.setIsDeleted(true); // 탈퇴 여부 설정
            user.setWithdrawalDate(LocalDateTime.now()); // 탈퇴 일시 설정
            userRepository.save(user);

            // 부계정 삭제 처리 (논리적 삭제)
            List<SubUserDTO> subUsers = subUserService.getSubUsersByMemberId(id);
            for (SubUserDTO subUser : subUsers) {
                subUserService.deleteSubUser(subUser.getId());
            }
        } else {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
    }

    /**
     * 회원정보 수정
     * @param userId 수정할 사용자 ID
     * @param updateDTO 수정할 정보
     */
    @Transactional
    public void updateUserInfo(String userId, UserUpdateDTO updateDTO) {
        // 기존 사용자 정보 조회
        UserEntity user = userRepository.findByIdAndIsDeletedFalse(userId).orElse(null);

        // 사용자가 존재하지 않을 경우 예외 발생
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        // 수정 가능한 필드만 업데이트
        user.setEmail(updateDTO.getEmail());
        user.setManagerName(updateDTO.getManagerName());
        user.setManagerPhoneNumber(updateDTO.getManagerPhoneNumber());
        user.setResponsibleName(updateDTO.getResponsibleName());
        user.setResponsiblePhoneNumber(updateDTO.getResponsiblePhoneNumber());

        userRepository.save(user);
    }
}
