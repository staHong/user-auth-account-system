package com.ds.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_file_upload")
public class FileUploadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long fileId; // 파일 ID

    @Column(name = "board_ref_id", nullable = false)
    private Long boardRefId; // 참조하는 게시글 ID

    @Column(name = "file_save_path", nullable = false)
    private String fileSavePath; // 파일 저장 경로

    @Column(name = "file_org_nm", nullable = false, length = 100)
    private String fileOrgNm; // 원본 파일명

    @Column(name = "create_dtm", nullable = false, updatable = false)
    private LocalDateTime createDtm; // 파일 업로드 날짜

    @PrePersist
    protected void onCreate() {
        this.createDtm = LocalDateTime.now();
    }
}
