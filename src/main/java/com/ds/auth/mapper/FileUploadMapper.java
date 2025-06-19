package com.ds.auth.mapper;

import com.ds.auth.dto.FileUploadDTO;
import com.ds.auth.entity.FileUploadEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring") // Spring Bean으로 등록
public interface FileUploadMapper {

    /**
     * FileUploadEntity → FileUploadDTO 변환
     *
     * @param entity 변환할 FileUploadEntity 객체
     * @return 변환된 FileUploadDTO 객체
     */
    FileUploadDTO toDto(FileUploadEntity entity);

    /**
     * FileUploadDTO → FileUploadEntity 변환
     *
     * @param dto 변환할 FileUploadDTO 객체
     * @return 변환된 FileUploadEntity 객체
     */
    FileUploadEntity toEntity(FileUploadDTO dto);

    /**
     * FileUploadEntity 리스트 -> FileUploadDTO 리스트 변환
     *
     * @param entities 변환할 FileUploadEntity 리스트
     * @return 변환된 FileUploadDTO 리스트
     */
    List<FileUploadDTO> toDtoList(List<FileUploadEntity> entities);
}
