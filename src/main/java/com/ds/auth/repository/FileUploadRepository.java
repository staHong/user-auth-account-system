package com.ds.auth.repository;

import com.ds.auth.entity.FileUploadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUploadEntity, Long> {

    /**
     * 게시글(boardRefId)에 속한 파일 목록 조회
     *
     * @param boardRefId 해당 게시판의 글 ID
     * @return 해당 글에 속한 파일 목록
     */
    List<FileUploadEntity> findByBoardRefId(Long boardRefId);
}
