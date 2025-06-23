package com.ds.auth.controller;

import com.ds.auth.dto.RegulatoryTrendDTO;
import com.ds.auth.service.FileUploadService;
import com.ds.auth.service.RegulatoryTrendService;
import com.ds.auth.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/trend")
@RequiredArgsConstructor
public class RegulatoryTrendController {

    private final RegulatoryTrendService regulatoryTrendService;
    private final FileUploadService fileUploadService;

    /**
     * [목록 조회 API]
     * - 출처, 제목 검색 가능
     * - 최신순 정렬
     * - 페이징 처리
     *
     * @param filterType 검색 유형 ("source" or "title")
     * @param searchValue 검색어
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 10)
     * @return 규제 동향 목록 (페이징 적용)
     */
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Page<RegulatoryTrendDTO>>> getFilteredTrends(
            @RequestParam(defaultValue = "source") String filterType, // 선택된 검색 타입
            @RequestParam(defaultValue = "") String searchValue, // 검색어
            @RequestParam(defaultValue = "0") int page, // 기본 페이지 번호 (0부터 시작)
            @RequestParam(defaultValue = "10") int size // 페이지 당 데이터 개수 (기본 10개)
            ) {
        try {
            Page<RegulatoryTrendDTO> trends;
            // 출처 검색
            if ("source".equals(filterType)) {
                trends = regulatoryTrendService.getFilteredTrends(searchValue, "", Pageable.ofSize(size).withPage(page));
            } // 제목 검색
            else {
                trends = regulatoryTrendService.getFilteredTrends("", searchValue, Pageable.ofSize(size).withPage(page));
            }

            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "규제 동향 목록 조회 성공", trends));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "규제 동향 목록 조회 실패"));
        }
    }

    /**
     * 규제 동향 상세 조회 (파일 포함)
     *
     * @param id 규제 동향 ID
     * @return ResponseEntity<ApiResponse<RegulatoryTrendDTO>>
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RegulatoryTrendDTO>> getTrendById(@PathVariable Long id) {
        try {
            RegulatoryTrendDTO trend = regulatoryTrendService.getTrendById(id);
            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "규제 동향 상세 조회 성공", trend));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "규제 동향 상세 조회 실패"));
        }
    }

    /**
     * 이전글/다음글 조회 API
     * - 현재 게시글 ID를 기준으로 이전글/다음글 정보를 가져온다.
     *
     * @param id 현재 게시글 ID
     * @return ResponseEntity<ApiResponse<Map<String, RegulatoryTrendDTO>>> 이전/다음 게시글 정보
     */
    @GetMapping("/{id}/prev-next")
    public ResponseEntity<ApiResponse<Map<String, RegulatoryTrendDTO>>> getPrevNextTrend(@PathVariable Long id) {
        try {
            Map<String, RegulatoryTrendDTO> prevNextTrends = regulatoryTrendService.getPrevNextTrend(id);
            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "이전/다음 글 조회 성공", prevNextTrends));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "이전/다음 글 조회 실패"));
        }
    }

    /**
     * 규제 동향 목록 조회 API - 관리자
     * - 출처(source), 제목(title), 날짜 범위(startDate~endDate) 검색 가능
     * - 최신순 정렬
     * - 페이징 처리 지원
     *
     * @param source    검색할 출처 (선택적)
     * @param title     검색할 제목 (선택적)
     * @param startDate 검색 시작 날짜 (YYYY-MM-DD 형식)
     * @param endDate   검색 종료 날짜 (YYYY-MM-DD 형식)
     * @param pageable  페이징 정보
     * @return 검색 조건을 반영한 규제 동향 목록 (페이징 적용)
     */
    @GetMapping("/admin/list")
    public ResponseEntity<ApiResponse<Page<RegulatoryTrendDTO>>> getFilteredTrends(
            @RequestParam(defaultValue = "") String source, // 출처 검색어
            @RequestParam(defaultValue = "") String title,  // 제목 검색어
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, // 시작 날짜 (null 허용)
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate, // 종료 날짜 (null 허용)
            Pageable pageable) {

        try {
            // 날짜 값이 없을 경우 기본값 설정
            LocalDate start = (startDate != null) ? startDate : LocalDate.of(2000, 1, 1); // 기본값: 2000-01-01
            LocalDate end = (endDate != null) ? endDate : LocalDate.now(); // 기본값: 오늘 날짜

            Page<RegulatoryTrendDTO> trends = regulatoryTrendService.getFilteredTrendsAdmin(source, title, start, end, pageable);

            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "규제 동향 목록 조회 성공", trends));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "규제 동향 목록 조회 실패"));
        }
    }

    /**
     * 규제 동향 게시글 등록 (파일 포함)
     *
     * @param regulatoryTrendDTO 게시글 정보 (출처, 제목, 내용)
     * @return 게시판 id와 HTTP 상태 코드
     */
    @PostMapping("/admin/register")
    public ResponseEntity<ApiResponse<Long>> createRegulatoryTrend(
            @Valid @ModelAttribute RegulatoryTrendDTO regulatoryTrendDTO, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            // 유효성 검증 실패 시 첫 번째 에러 메시지를 반환
            String errorMessage = bindingResult.getFieldError().getDefaultMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, errorMessage));
        }

        try{
            Long trendId = regulatoryTrendService.registerRegulatoryTrend(regulatoryTrendDTO);

            List<MultipartFile> files = regulatoryTrendDTO.getAttachedRequestFiles();
            // 파일이 있는 경우, 해당 게시글(trendId)와 연결하여 저장
            if (files != null && !files.isEmpty()) {
                fileUploadService.uploadFiles(files, trendId);
            }

            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "규제 동향 등록 성공", trendId));

        }catch (IllegalArgumentException e) {
            // 파일 10개 초과거나, 파일 한개당 10MB 넘으면 `400 Bad Request`
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage()));
        } catch (Exception e) {
            // 파일 업로드 실패, DB 저장 오류 등 `500 Internal Server Error`
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "규제 동향 등록 실패"));
        }
    }

    /**
     * 규제 동향 게시글 삭제 (파일 포함)
     *
     * @param trendId 삭제할 게시글 ID
     * @return HTTP 응답 코드 및 메시지
     */
    @DeleteMapping("/admin/delete/{trendId}")
    public ResponseEntity<ApiResponse<String>> deleteRegulatoryTrend(@PathVariable Long trendId) {
        try {
            regulatoryTrendService.deleteRegulatoryTrend(trendId);
            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "규제 동향 게시글 삭제 성공", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "규제 동향 게시글 삭제 실패"));
        }
    }

}
