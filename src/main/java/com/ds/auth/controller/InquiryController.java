package com.ds.auth.controller;

import com.ds.auth.dto.InquiryDTO;
import com.ds.auth.response.ApiResponse;
import com.ds.auth.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/inquiry")
@RequiredArgsConstructor
public class InquiryController {
    private final InquiryService inquiryService;

    /**
     * 문의 등록 API
     *
     * @param inquiryDTO 문의 데이터를 포함한 요청 본문 (JSON 형식)
     *                   - userName (String): 문의한 사람의 이름
     *                   - companyName (String): 회사명
     *                   - email (String): 이메일 주소
     *                   - inquiryContent (String): 문의 내용
     *                   - isPublic (boolean): 공개 여부 (true: 공개, false: 비공개)
     * @return ResponseEntity<ApiResponse<InquiryDTO>>
     *         - 성공 시: HTTP 200 OK + 저장된 문의 정보 반환
     *         - 실패 시: HTTP 500 Internal Server Error + 에러 메시지 반환
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<InquiryDTO>> createInquiry(@RequestBody InquiryDTO inquiryDTO) {
        try {
            InquiryDTO savedInquiry = inquiryService.saveInquiry(inquiryDTO);
            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "문의 등록 성공", savedInquiry));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "문의 등록 실패"));
        }
    }

    /**
     * 문의 목록 조회 API
     *
     * @param userName    (String) 문의한 사람의 이름 (선택적)
     * @param companyName (String) 회사명 (선택적)
     * @param email       (String) 이메일 주소 (선택적)
     * @param startDate   (LocalDate) 조회 시작 날짜 (선택적, 형식: yyyy-MM-dd)
     * @param endDate     (LocalDate) 조회 종료 날짜 (선택적, 형식: yyyy-MM-dd)
     * @param pageable    (Pageable) 페이징 정보
     * @return ResponseEntity<ApiResponse<Page<InquiryDTO>>>
     *         - 성공 시: HTTP 200 OK + 조회된 문의 목록 반환
     *         - 요청 오류 시: HTTP 400 Bad Request + 오류 메시지 반환 (잘못된 날짜 형식 등)
     *         - 서버 오류 시: HTTP 500 Internal Server Error + 에러 메시지 반환
     */
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Page<InquiryDTO>>> getInquiries(
            @RequestParam(defaultValue = "") String userName, // 이름
            @RequestParam(defaultValue = "") String companyName, // 회사명
            @RequestParam(defaultValue = "") String email, // 이메일
            @RequestParam(required = false) LocalDate startDate, // 시작 날짜
            @RequestParam(required = false) LocalDate endDate,   // 종료 날짜
            @PageableDefault(size = 15) Pageable pageable // 기본 페이지 크기 설정
    ) {
        try {
            // 최신일 순으로 정렬 추가 (createdAt 기준 내림차순)
            Pageable sortedPageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "createdAt")
            );

            // 여러 조건을 고려하여 조회
            Page<InquiryDTO> inquiries = inquiryService.getInquiries(userName, companyName, email, startDate, endDate, sortedPageable);
            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "문의 목록 조회 성공", inquiries));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "문의 목록 조회 실패"));
        }
    }

    /**
     * 문의에 대한 응답(답변) 등록 API
     *
     * @param inquiryDTO (InquiryDTO) 문의 ID와 답변 내용 포함
     *                   - id (Long): 문의 ID
     *                   - answerContent (String): 답변 내용
     * @return ResponseEntity<ApiResponse<InquiryDTO>>
     *         - 성공 시: HTTP 200 OK + 업데이트된 문의 정보 반환
     *         - 요청 오류 시: HTTP 400 Bad Request (존재하지 않는 문의 ID, 잘못된 요청 등)
     *         - 서버 오류 시: HTTP 500 Internal Server Error
     */
    @PatchMapping("/answer")
    public ResponseEntity<ApiResponse<InquiryDTO>> answerInquiry(@RequestBody InquiryDTO inquiryDTO) {
        try {
            InquiryDTO updatedInquiry = inquiryService.answerInquiry(inquiryDTO.getId(), inquiryDTO.getAnswerContent());
            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "문의 답변 등록 성공", updatedInquiry));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "문의 답변 등록 실패"));
        }
    }

    /**
     * 문의 상세 조회 API
     *
     * @param id (Long) 조회할 문의 ID
     * @return ResponseEntity<ApiResponse<InquiryDTO>>
     *         - 성공 시: HTTP 200 OK + 조회된 문의 정보 반환
     *         - 요청 오류 시: HTTP 400 Bad Request (존재하지 않는 문의 ID, 잘못된 요청 등)
     *         - 서버 오류 시: HTTP 500 Internal Server Error
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InquiryDTO>> getInquiryDetail(@PathVariable Long id) {
        try {
            InquiryDTO inquiry = inquiryService.getInquiryById(id);
            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "문의 상세 조회 성공", inquiry));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "문의 상세 조회 실패"));
        }
    }
}
