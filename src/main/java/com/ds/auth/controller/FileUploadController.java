package com.ds.auth.controller;

import com.ds.auth.dto.FileUploadDTO;
import com.ds.auth.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
@Slf4j
public class FileUploadController {

    private final FileUploadService fileUploadService;

    /**
     * [파일 다운로드 API]
     * - 파일 ID 기반으로 저장된 파일 경로 조회 후 다운로드
     *
     * @param fileId 다운로드할 파일의 ID
     * @return 해당 파일 다운로드
     */
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        try {
            // 파일 정보 가져오기
            FileUploadDTO file = fileUploadService.getFileById(fileId);
            if (file == null) {
                log.error("파일을 찾을 수 없습니다. fileId={}", fileId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // 파일 경로
            Path filePath = Paths.get(file.getFileSavePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                log.error("파일을 찾을 수 없습니다: {}", filePath);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // 파일 크기 가져오기
            long fileSize = filePath.toFile().length();

            // 원본 파일명 UTF-8 인코딩 처리
            String encodedFileName = URLEncoder.encode(file.getFileOrgNm(), StandardCharsets.UTF_8)
                    .replace("+", "%20"); // 공백 처리

            log.info("파일 다운로드 요청: {}", file.getFileOrgNm());

            // 응답 헤더 설정 (Content-Length 추가)
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileSize)) // 파일 크기 추가
                    .body(resource);

        } catch (Exception e) {
            log.error("파일 다운로드 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
