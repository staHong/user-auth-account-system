package com.ds.auth.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegulatoryTrendDTO {
    private Long trendId;  // ID
    private String sourceName; // 출처
    private String title;  // 제목
    private String content; // 내용
    private LocalDateTime createdAt; // 등록일시
    private List<MultipartFile> attachedRequestFiles; // 등록 시, 첨부파일 목록
    private List<FileUploadDTO> attachedFiles; // 조회 시, 첨부파일 목록
}
