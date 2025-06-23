package com.ds.auth.service;

import com.ds.auth.dto.FileUploadDTO;
import com.ds.auth.entity.FileUploadEntity;
import com.ds.auth.mapper.FileUploadMapper;
import com.ds.auth.repository.FileUploadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final FileUploadRepository fileUploadRepository;
    private final FileUploadMapper fileUploadMapper;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    @Value("${file.upload.biz-directory}") // application.properties에서 사업자 등록증 파일 저장 경로 불러오기
    private String uploadBizDir;

    @Value("${file.upload.base-directory}") // application.properties에서 사업자 등록증 파일 저장 경로 불러오기
    private String uploadBaseDir;

    /**
     * 특정 게시판의 파일 목록 조회
     *
     * @param boardRefId 해당 게시판의 ID
     * @return FileUploadDTO 목록
     */
    public List<FileUploadDTO> getFilesByBoard(Long boardRefId) {
        List<FileUploadEntity> fileEntities = fileUploadRepository.findByBoardRefId(boardRefId);
        return fileUploadMapper.toDtoList(fileEntities); // Mapper 사용하여 변환
    }


    /**
     * [파일 정보 조회]
     * - 파일 ID를 기준으로 특정 파일 정보 조회
     *
     * @param fileId 파일 ID
     * @return 파일 정보 DTO
     */
    public FileUploadDTO getFileById(Long fileId) {
        FileUploadEntity file = fileUploadRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));
        return fileUploadMapper.toDto(file);
    }

    /**
     * 파일 업로드 (사업자등록증)
     *
     * @param file 업로드할 파일
     * @return 저장할 파일 경로
     */
    public String saveFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 없습니다.");
        }

        // 파일 크기 제한 (10MB 이하)
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("파일 크기는 최대 10MB까지만 가능합니다.");
        }

        // 허용된 확장자 목록
        List<String> allowedExtensions = Arrays.asList(".pdf", ".png", ".jpeg", ".jpg", ".hwp", ".doc", ".docx");
        String extension = getFileExtension(file.getOriginalFilename());

        if (!allowedExtensions.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("허용되지 않은 파일 형식입니다. (pdf, png, jpeg, jpg, hwp, doc, docx만 가능)");
        }

        try {
            // 저장할 디렉토리 생성 (연도별 폴더 포함)
            String year = String.valueOf(Year.now().getValue());
            String uploadDir = uploadBizDir + "/" + year + "/";
            Path directoryPath = Paths.get(uploadDir);
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            // 파일 저장 (UUID로 고유 파일명 생성)
            String uniqueFileName = UUID.randomUUID() + extension;
            String filePath = uploadDir + uniqueFileName;

            // 파일 저장 실행
            file.transferTo(new File(filePath));

            return filePath; // 저장된 파일 경로 반환
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 파일 확장자 가져오기
     *
     * @param fileName 원본 파일명
     * @return 파일 확장자 (예: .png, .jpg)
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
    }

    /**
     * 파일 삭제하기
     *
     * @param filePath 파일 경로
     */
    public void deleteFile(String filePath) {
        if (filePath != null) {
            File file = new File(filePath);
            if (file.exists()) {
                boolean isDeleted = file.delete();
                if (!isDeleted) {
                    throw new RuntimeException("파일 삭제 실패: " + filePath);
                }
            }
        }
    }

    /**
     * 게시글과 연결된 파일 업로드
     *
     * @param files 업로드할 파일 리스트
     * @param boardRefId 연결할 게시글 ID
     */
    public void uploadFiles(List<MultipartFile> files, Long boardRefId) {
        if (files == null || files.isEmpty()) {
            return; // 파일이 없는 경우 그냥 리턴
        }

        // 파일 개수 제한 체크
        if (files.size() > 10) {
            throw new IllegalArgumentException("최대 " + 10 + "개의 파일만 업로드할 수 있습니다.");
        }

        List<FileUploadEntity> fileEntities = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.getSize() > MAX_FILE_SIZE) {
                throw new IllegalArgumentException("파일 크기는 최대 10MB까지만 가능합니다.");
            }

            try {
                // 저장할 디렉토리 생성 (연도별 폴더 포함)
                String year = String.valueOf(Year.now().getValue());
                String uploadDir = uploadBaseDir + "/" + year + "/";
                Path directoryPath = Paths.get(uploadDir);
                if (!Files.exists(directoryPath)) {
                    Files.createDirectories(directoryPath);
                }

                String extension = getFileExtension(file.getOriginalFilename());
                // 파일 저장 (UUID로 고유 파일명 생성)
                String uniqueFileName = UUID.randomUUID() + extension;
                String filePath = uploadDir + uniqueFileName;

                // 파일 저장 실행
                file.transferTo(new File(filePath));

                // DB 저장
                FileUploadEntity fileEntity = FileUploadEntity.builder()
                        .boardRefId(boardRefId) // 게시글 ID 참조
                        .fileSavePath(filePath)
                        .fileOrgNm(file.getOriginalFilename())
                        .build();

                fileEntities.add(fileEntity);

            } catch (IOException e) {
                throw new RuntimeException("파일 업로드 실패: " + file.getOriginalFilename(), e);
            }
        }

        fileUploadRepository.saveAll(fileEntities);
    }

    /**
     * 특정 게시글과 연결된 파일 삭제
     *
     * @param boardRefId 삭제할 게시글 ID
     */
    public void deleteFilesByBoardRefId(Long boardRefId) {
        // 게시글에 연결된 파일 목록 조회
        List<FileUploadEntity> files = fileUploadRepository.findByBoardRefId(boardRefId);

        if (files == null || files.isEmpty()) {
            return; // 파일이 없는 경우, 별도 처리 없이 종료
        }

        // 각 파일 삭제
        for (int i = 0; i < files.size(); i++) {
            FileUploadEntity file = files.get(i);

            // 실제 파일 삭제
            deleteFile(file.getFileSavePath());

            // DB에서 파일 정보 삭제
            fileUploadRepository.delete(file);
        }
    }
}
