package com.ds.auth.mapper;

import com.ds.auth.dto.InquiryDTO;
import com.ds.auth.entity.InquiryEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-19T23:31:14+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.11.1.jar, environment: Java 17.0.15 (Homebrew)"
)
@Component
public class InquiryMapperImpl implements InquiryMapper {

    @Override
    public InquiryDTO toDto(InquiryEntity entity) {
        if ( entity == null ) {
            return null;
        }

        InquiryDTO.InquiryDTOBuilder inquiryDTO = InquiryDTO.builder();

        inquiryDTO.id( entity.getId() );
        inquiryDTO.userName( entity.getUserName() );
        inquiryDTO.companyName( entity.getCompanyName() );
        inquiryDTO.email( entity.getEmail() );
        inquiryDTO.inquiryContent( entity.getInquiryContent() );
        inquiryDTO.answerContent( entity.getAnswerContent() );
        inquiryDTO.createdAt( entity.getCreatedAt() );

        return inquiryDTO.build();
    }

    @Override
    public InquiryEntity toEntity(InquiryDTO dto) {
        if ( dto == null ) {
            return null;
        }

        InquiryEntity.InquiryEntityBuilder inquiryEntity = InquiryEntity.builder();

        inquiryEntity.id( dto.getId() );
        inquiryEntity.userName( dto.getUserName() );
        inquiryEntity.companyName( dto.getCompanyName() );
        inquiryEntity.email( dto.getEmail() );
        inquiryEntity.inquiryContent( dto.getInquiryContent() );
        inquiryEntity.answerContent( dto.getAnswerContent() );
        inquiryEntity.createdAt( dto.getCreatedAt() );

        return inquiryEntity.build();
    }
}
