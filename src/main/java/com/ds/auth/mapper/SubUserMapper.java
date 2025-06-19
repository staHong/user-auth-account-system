package com.ds.auth.mapper;

import com.ds.auth.dto.SubUserDTO;
import com.ds.auth.entity.SubUserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring") // Spring Bean으로 등록
public interface SubUserMapper {

    /**
     * SubUserEntity를 SubUserDTO로 변환
     *
     * @param subUserEntity 부계정 Entity
     * @return 변환된 부계정 DTO
     */
    @Mapping(target = "password", ignore = true) // 보안상 비밀번호 제외 가능
    SubUserDTO toDto(SubUserEntity subUserEntity);

    /**
     * SubUserDTO를 SubUserEntity로 변환
     *
     * @param subUserDTO 부계정 DTO
     * @return 변환된 부계정 Entity
     */
    SubUserEntity toEntity(SubUserDTO subUserDTO);
}
