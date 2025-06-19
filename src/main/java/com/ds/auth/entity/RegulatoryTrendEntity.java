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
@Table(name = "tb_regulatory_trend")
public class RegulatoryTrendEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trend_id")
    private Long trendId; // ID

    @Column(name = "src_nm", nullable = false)
    private String sourceName; // 출처

    @Column(name = "trend_title", nullable = false)
    private String title; // 제목

    @Column(name = "trend_content", nullable = false, length = 4000)
    private String content; // 내용

    @Column(name = "create_dtm", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 등록일시

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
