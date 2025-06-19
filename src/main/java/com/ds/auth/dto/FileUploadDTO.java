package com.ds.auth.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUploadDTO {
    private Long fileId; // 파일 ID
    private Long boardRefId; // 해당 게시판의 ID (RegulatoryTrend의 trendId와 매칭)
    private String fileSavePath; // 저장된 파일 경로
    private String fileOrgNm; // 원본 파일명
    private LocalDateTime createDtm; // 등록일시
}