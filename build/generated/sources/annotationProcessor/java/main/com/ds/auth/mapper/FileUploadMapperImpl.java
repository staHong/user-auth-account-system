package com.ds.auth.mapper;

import com.ds.auth.dto.FileUploadDTO;
import com.ds.auth.entity.FileUploadEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-19T23:31:14+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.11.1.jar, environment: Java 17.0.15 (Homebrew)"
)
@Component
public class FileUploadMapperImpl implements FileUploadMapper {

    @Override
    public FileUploadDTO toDto(FileUploadEntity entity) {
        if ( entity == null ) {
            return null;
        }

        FileUploadDTO.FileUploadDTOBuilder fileUploadDTO = FileUploadDTO.builder();

        fileUploadDTO.fileId( entity.getFileId() );
        fileUploadDTO.boardRefId( entity.getBoardRefId() );
        fileUploadDTO.fileSavePath( entity.getFileSavePath() );
        fileUploadDTO.fileOrgNm( entity.getFileOrgNm() );
        fileUploadDTO.createDtm( entity.getCreateDtm() );

        return fileUploadDTO.build();
    }

    @Override
    public FileUploadEntity toEntity(FileUploadDTO dto) {
        if ( dto == null ) {
            return null;
        }

        FileUploadEntity.FileUploadEntityBuilder fileUploadEntity = FileUploadEntity.builder();

        fileUploadEntity.fileId( dto.getFileId() );
        fileUploadEntity.boardRefId( dto.getBoardRefId() );
        fileUploadEntity.fileSavePath( dto.getFileSavePath() );
        fileUploadEntity.fileOrgNm( dto.getFileOrgNm() );
        fileUploadEntity.createDtm( dto.getCreateDtm() );

        return fileUploadEntity.build();
    }

    @Override
    public List<FileUploadDTO> toDtoList(List<FileUploadEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<FileUploadDTO> list = new ArrayList<FileUploadDTO>( entities.size() );
        for ( FileUploadEntity fileUploadEntity : entities ) {
            list.add( toDto( fileUploadEntity ) );
        }

        return list;
    }
}
