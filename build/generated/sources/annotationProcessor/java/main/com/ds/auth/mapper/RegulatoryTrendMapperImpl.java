package com.ds.auth.mapper;

import com.ds.auth.dto.RegulatoryTrendDTO;
import com.ds.auth.entity.RegulatoryTrendEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-19T23:31:14+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.11.1.jar, environment: Java 17.0.15 (Homebrew)"
)
@Component
public class RegulatoryTrendMapperImpl implements RegulatoryTrendMapper {

    @Override
    public RegulatoryTrendDTO toDto(RegulatoryTrendEntity entity) {
        if ( entity == null ) {
            return null;
        }

        RegulatoryTrendDTO.RegulatoryTrendDTOBuilder regulatoryTrendDTO = RegulatoryTrendDTO.builder();

        regulatoryTrendDTO.trendId( entity.getTrendId() );
        regulatoryTrendDTO.sourceName( entity.getSourceName() );
        regulatoryTrendDTO.title( entity.getTitle() );
        regulatoryTrendDTO.content( entity.getContent() );
        regulatoryTrendDTO.createdAt( entity.getCreatedAt() );

        return regulatoryTrendDTO.build();
    }

    @Override
    public RegulatoryTrendEntity toEntity(RegulatoryTrendDTO dto) {
        if ( dto == null ) {
            return null;
        }

        RegulatoryTrendEntity.RegulatoryTrendEntityBuilder regulatoryTrendEntity = RegulatoryTrendEntity.builder();

        regulatoryTrendEntity.trendId( dto.getTrendId() );
        regulatoryTrendEntity.sourceName( dto.getSourceName() );
        regulatoryTrendEntity.title( dto.getTitle() );
        regulatoryTrendEntity.content( dto.getContent() );
        regulatoryTrendEntity.createdAt( dto.getCreatedAt() );

        return regulatoryTrendEntity.build();
    }
}
