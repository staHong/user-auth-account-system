package com.ds.auth.service;

import com.ds.auth.dto.InquiryDTO;
import com.ds.auth.entity.InquiryEntity;
import com.ds.auth.mapper.InquiryMapper;
import com.ds.auth.repository.InquiryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final InquiryMapper inquiryMapper;
    private final EmailService emailService;

    /**
     * 문의 등록 서비스
     *
     * @param inquiryDTO 문의 데이터를 포함한 DTO
     *                   - userName (String): 문의한 사람의 이름
     *                   - companyName (String): 회사명
     *                   - email (String): 이메일 주소
     *                   - inquiryContent (String): 문의 내용
     *                   - isPublic (boolean): 공개 여부 (true: 공개, false: 비공개)
     * @return InquiryDTO 저장된 문의 정보
     * @throws RuntimeException 문의 등록 중 오류 발생 시 예외 발생
     */
    public InquiryDTO saveInquiry(InquiryDTO inquiryDTO) {
        try {
            InquiryEntity inquiryEntity = inquiryMapper.toEntity(inquiryDTO);
            InquiryEntity savedEntity = inquiryRepository.save(inquiryEntity);
            return inquiryMapper.toDto(savedEntity);
        } catch (Exception e) {
            throw new RuntimeException("문의 등록 중 오류 발생", e);
        }
    }

    /**
     * 문의 목록 조회 서비스
     *
     * @param userName    (String) 문의한 사람의 이름 (선택적)
     * @param companyName (String) 회사명 (선택적)
     * @param email       (String) 이메일 주소 (선택적)
     * @param startDate   (LocalDate) 조회 시작 날짜 (선택적)
     * @param endDate     (LocalDate) 조회 종료 날짜 (선택적)
     * @param pageable    (Pageable) 페이징 정보
     * @return Page<InquiryDTO> 조회된 문의 목록 (페이지형태)
     */
    public Page<InquiryDTO> getInquiries(String userName, String companyName, String email,
                                         LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Specification<InquiryEntity> spec = Specification.where(null);

        if (!userName.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("userName"), "%" + userName + "%"));
        }
        if (!companyName.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("companyName"), "%" + companyName + "%"));
        }
        if (!email.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("email"), "%" + email + "%"));
        }
        if (startDate != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate.atStartOfDay()));
        }
        if (endDate != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate.atTime(23, 59, 59)));
        }

        Page<InquiryEntity> inquiries = inquiryRepository.findAll(spec, pageable);
        return inquiries.map(inquiryMapper::toDto);
    }

    /**
     * 문의 답변 등록 및 이메일 전송
     *
     * @param id (Long) 문의 ID
     * @param answerContent (String) 답변 내용
     * @return InquiryDTO 업데이트된 문의 정보 반환
     * @throws IllegalArgumentException 문의가 존재하지 않을 경우 예외 발생
     */
    @Transactional
    public InquiryDTO answerInquiry(Long id, String answerContent) {
        // 1. 문의 조회
        InquiryEntity inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 문의가 존재하지 않습니다."));

        // 2. 문의 답변 업데이트
        inquiry.setAnswerContent(answerContent);
        InquiryEntity updatedInquiry = inquiryRepository.save(inquiry);

        // 3. 이메일 전송
        sendInquiryAnswerEmail(inquiryMapper.toDto(updatedInquiry));

        return inquiryMapper.toDto(updatedInquiry);
    }

    /**
     * 문의 상세 조회
     *
     * @param id (Long) 조회할 문의 ID
     * @return InquiryDTO 조회된 문의 정보 반환
     * @throws IllegalArgumentException 문의가 존재하지 않을 경우 예외 발생
     */
    public InquiryDTO getInquiryById(Long id) {
        InquiryEntity inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 문의가 존재하지 않습니다."));

        return inquiryMapper.toDto(inquiry);
    }

    /**
     * 문의 답변 이메일 전송
     *
     * @param inquiry 문의 정보 (답변 포함)
     */
    private void sendInquiryAnswerEmail(InquiryDTO inquiry) {
        String subject = "[CRISKZ] 문의에 대한 답변 안내";
        String content = buildInquiryAnswerEmail(inquiry);

        emailService.sendEmail(inquiry.getEmail(), subject, content);
    }

    /**
     * 문의 답변 이메일 본문 생성
     *
     * @param inquiry 문의 정보
     * @return 이메일 본문 (HTML)
     */
    private String buildInquiryAnswerEmail(InquiryDTO inquiry) {
        return String.format(
                "<html><body>" +
                        "<p>안녕하세요, %s님.</p>" +
                        "<p>문의하신 내용에 대한 답변을 드립니다.</p>" +
                        "<br>" +
                        "<p><strong>문의 내용</strong></p>" +
                        "<p>%s</p>" +
                        "<br>" +
                        "<p><strong>답변 내용</strong></p>" +
                        "<p>%s</p>" +
                        "<br>" +
                        "<p>감사합니다.</p>" +
                        "</body></html>",
                inquiry.getUserName(),
                inquiry.getInquiryContent(),
                inquiry.getAnswerContent()
        );
    }
}
