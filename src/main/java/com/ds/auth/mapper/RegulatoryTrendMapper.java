package com.ds.auth.mapper;

import com.ds.auth.dto.RegulatoryTrendDTO;
import com.ds.auth.entity.RegulatoryTrendEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring") // Spring Bean으로 등록
public interface RegulatoryTrendMapper {

    /**
     * RegulatoryTrendEntity를 RegulatoryTrendDTO로 변환
     *
     * @param entity 규제동향 Entity
     * @return 변환된 규제동향 DTO
     */
    RegulatoryTrendDTO toDto(RegulatoryTrendEntity entity);

    /**
     * RegulatoryTrendDTO를 RegulatoryTrendEntity로 변환
     *
     * @param dto 규제동향 DTO
     * @return 변환된 규제동향 entity
     */
    RegulatoryTrendEntity toEntity(RegulatoryTrendDTO dto);
}