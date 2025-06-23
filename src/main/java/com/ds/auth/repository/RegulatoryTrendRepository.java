package com.ds.auth.repository;

import com.ds.auth.entity.RegulatoryTrendEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface RegulatoryTrendRepository extends JpaRepository<RegulatoryTrendEntity, Long> {
    /**
     * [규제 동향 목록 조회]
     * - 출처를 기준으로 검색
     * - 최신순 정렬
     *
     * @param source 출처 내용
     * @param pageable 페이지
     * @return 검색된 규제 동향 목록
     */
    Page<RegulatoryTrendEntity> findBySourceNameContainingOrderByTrendIdDesc(String source, Pageable pageable);
    /**
     * [규제 동향 목록 조회]
     * - 제목을 기준으로 검색
     * - 최신순 정렬
     *
     * @param title 제목 내용
     * @param pageable 페이지
     * @return 검색된 규제 동향 목록
     */
    Page<RegulatoryTrendEntity> findByTitleContainingOrderByTrendIdDesc(String title, Pageable pageable);

    /**
     * 현재 게시글 ID보다 큰 값 중에서 가장 작은 ID의 게시글 (이전글) 조회
     *
     * @param trendId 현재 게시글 ID
     * @return Optional<RegulatoryTrendEntity> 이전 게시글
     */
    Optional<RegulatoryTrendEntity> findFirstByTrendIdGreaterThanOrderByTrendIdAsc(Long trendId);

    /**
     * 현재 게시글 ID보다 작은 값 중에서 가장 큰 ID의 게시글 (다음글) 조회
     *
     * @param trendId 현재 게시글 ID
     * @return Optional<RegulatoryTrendEntity> 다음 게시글
     */
    Optional<RegulatoryTrendEntity> findFirstByTrendIdLessThanOrderByTrendIdDesc(Long trendId);

    /**
     * 규제 동향 데이터 조회 (검색 필터 적용)
     * - 출처(sourceName), 제목(title), 등록일(createdAt) 기준으로 검색
     * - 최신순 정렬 (trendId DESC)
     * - 페이징 처리 지원 (Pageable)
     */
    @Query("SELECT r FROM RegulatoryTrendEntity r " +
            "WHERE (:source IS NULL OR r.sourceName LIKE %:source%) " +
            "AND (:title IS NULL OR r.title LIKE %:title%) " +
            "AND (CAST(:startDate AS date) IS NULL OR CAST(r.createdAt AS date) >= :startDate) " +
            "AND (CAST(:endDate AS date) IS NULL OR CAST(r.createdAt AS date) <= :endDate) " +
            "ORDER BY r.trendId DESC")
    Page<RegulatoryTrendEntity> getFilteredTrendsAdmin(
            @Param("source") String source,
            @Param("title") String title,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );
}



