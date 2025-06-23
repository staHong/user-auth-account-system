package com.ds.auth.service;

import com.ds.auth.dto.RegulatoryTrendDTO;
import com.ds.auth.dto.FileUploadDTO;
import com.ds.auth.entity.RegulatoryTrendEntity;
import com.ds.auth.repository.RegulatoryTrendRepository;
import com.ds.auth.mapper.RegulatoryTrendMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RegulatoryTrendService {

    private final RegulatoryTrendRepository regulatoryTrendRepository;
    private final FileUploadService fileUploadService;
    private final RegulatoryTrendMapper regulatoryTrendMapper;

    /**
     * [규제 동향 목록 조회]
     * - 출처 또는 제목을 기준으로 검색
     * - 최신순 정렬
     *
     * @param source 출처 내용
     * @param pageable 페이지
     * @return 검색된 규제 동향 목록
     */
    public Page<RegulatoryTrendDTO> getFilteredTrends(String source, String title, Pageable pageable) {
        Page<RegulatoryTrendEntity> trends;

        // 출처 또는 제목 필터 적용
        if (!source.isEmpty()) {
            trends = regulatoryTrendRepository.findBySourceNameContainingOrderByTrendIdDesc(source, pageable);
        } else {
            trends = regulatoryTrendRepository.findByTitleContainingOrderByTrendIdDesc(title, pageable);
        }

        // `Page`에서 `List`로 변환 후 수동 매핑
        List<RegulatoryTrendDTO> dtoList = new ArrayList<>();
        for (RegulatoryTrendEntity entity : trends.getContent()) {
            RegulatoryTrendDTO dto = regulatoryTrendMapper.toDto(entity);
            dtoList.add(dto);
        }

        // `PageImpl`을 사용하여 `List<DTO>`를 `Page<DTO>`로 변환
        return new PageImpl<>(dtoList, pageable, trends.getTotalElements());
    }

    /**
     * 규제 동향 상세 조회 (파일 포함)
     *
     * @param id 규제 동향 ID
     * @return RegulatoryTrendDTO (파일 포함)
     */
    public RegulatoryTrendDTO getTrendById(Long id) {
        Optional<RegulatoryTrendEntity> optionalEntity = regulatoryTrendRepository.findById(id);
        if (optionalEntity.isEmpty()) {
            throw new RuntimeException("해당 규제 동향을 찾을 수 없습니다.");
        }

        // 파일 목록 조회
        List<FileUploadDTO> fileList = fileUploadService.getFilesByBoard(id);

        // DTO 변환
        RegulatoryTrendEntity entity = optionalEntity.get();
        // DTO 변환 (Mapper 사용)
        RegulatoryTrendDTO dto = regulatoryTrendMapper.toDto(entity);
        dto.setAttachedFiles(fileList); // 파일 목록 추가

        return dto;
    }

    /**
     * 현재 게시글 기준 이전글 / 다음글 조회
     *
     * @param trendId 현재 게시글 ID
     * @return Map<String, RegulatoryTrendDTO> 이전글/다음글 데이터
     */
    public Map<String, RegulatoryTrendDTO> getPrevNextTrend(Long trendId) {
        Map<String, RegulatoryTrendDTO> result = new HashMap<>();

        // 이전 글 찾기
        Optional<RegulatoryTrendEntity> optionalPrevEntity =
                regulatoryTrendRepository.findFirstByTrendIdGreaterThanOrderByTrendIdAsc(trendId);
        RegulatoryTrendDTO prevTrend = null;
        if (optionalPrevEntity.isPresent()) {
            prevTrend = regulatoryTrendMapper.toDto(optionalPrevEntity.get());
        }

        // 다음 글 찾기
        Optional<RegulatoryTrendEntity> optionalNextEntity =
                regulatoryTrendRepository.findFirstByTrendIdLessThanOrderByTrendIdDesc(trendId);
        RegulatoryTrendDTO nextTrend = null;
        if (optionalNextEntity.isPresent()) {
            nextTrend = regulatoryTrendMapper.toDto(optionalNextEntity.get());
        }

        result.put("prevTrend", prevTrend);
        result.put("nextTrend", nextTrend);

        return result;
    }

    /**
     * 규제 동향 조회 (검색 조건 적용) - 관리자
     * - 출처(source), 제목(title), 날짜 범위(startDate~endDate)로 필터링
     * - 페이징 적용하여 데이터 반환
     *
     * @param sourceName    검색할 출처 (선택적)
     * @param title     검색할 제목 (선택적)
     * @param startDate 검색 시작 날짜
     * @param endDate   검색 종료 날짜
     * @param pageable  페이징 정보
     * @return 검색 조건에 맞는 규제 동향 목록 (페이징 포함)
     */
    public Page<RegulatoryTrendDTO> getFilteredTrendsAdmin(String sourceName, String title, LocalDate startDate, LocalDate endDate, Pageable pageable) {

        Page<RegulatoryTrendEntity> trends = regulatoryTrendRepository.getFilteredTrendsAdmin(sourceName, title, startDate, endDate, pageable);

        // `Page`에서 `List`로 변환 후 수동 매핑
        List<RegulatoryTrendDTO> dtoList = new ArrayList<>();
        for (RegulatoryTrendEntity entity : trends.getContent()) {
            RegulatoryTrendDTO dto = regulatoryTrendMapper.toDto(entity);
            dtoList.add(dto);
        }

        // `PageImpl`을 사용하여 `List<DTO>`를 `Page<DTO>`로 변환
        return new PageImpl<>(dtoList, pageable, trends.getTotalElements());
    }

    /**
     * 규제 동향 게시글 등록 (파일 포함)
     *
     * @param regulatoryTrendDTO 게시글 정보 (출처, 제목, 내용)
     * @return trendId 게시글 ID
     */
    @Transactional
    public Long registerRegulatoryTrend(RegulatoryTrendDTO regulatoryTrendDTO) {

        // 게시글 저장
        RegulatoryTrendEntity regulatoryTrend = regulatoryTrendMapper.toEntity(regulatoryTrendDTO);
        regulatoryTrendRepository.save(regulatoryTrend);

        return regulatoryTrend.getTrendId();
    }

    /**
     * 규제 동향 게시글 삭제 (연결된 파일 포함)
     *
     * @param trendId 삭제할 게시글 ID
     */
    @Transactional
    public void deleteRegulatoryTrend(Long trendId) {
        // 게시글 조회
        Optional<RegulatoryTrendEntity> optionalTrend = regulatoryTrendRepository.findById(trendId);
        // 게시글이 존재하는지 확인
        if (!optionalTrend.isPresent()) {
            throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
        }

        RegulatoryTrendEntity regulatoryTrend = optionalTrend.get();

        // 파일 삭제 처리 (게시글 ID를 기준으로 파일 삭제)
        fileUploadService.deleteFilesByBoardRefId(trendId);

        // 게시글 삭제
        regulatoryTrendRepository.delete(regulatoryTrend);
    }

}
