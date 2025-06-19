package com.ds.auth.mapper;

import com.ds.auth.dto.InquiryDTO;
import com.ds.auth.entity.InquiryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring") // Spring Bean으로 등록
public interface InquiryMapper {

    /**
     * InquiryEntity → InquiryDTO 변환
     *
     * @param entity 변환할 InquiryEntity 객체
     * @return 변환된 InquiryDTO 객체
     */
    InquiryDTO toDto(InquiryEntity entity);

    /**
     * InquiryDTO → InquiryEntity 변환
     *
     * @param dto 변환할 InquiryDTO 객체
     * @return 변환된 InquiryEntity 객체
     */
    InquiryEntity toEntity(InquiryDTO dto);
}
