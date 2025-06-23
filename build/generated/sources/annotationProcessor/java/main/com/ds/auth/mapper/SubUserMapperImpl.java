package com.ds.auth.mapper;

import com.ds.auth.dto.SubUserDTO;
import com.ds.auth.entity.SubUserEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-19T23:31:14+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.11.1.jar, environment: Java 17.0.15 (Homebrew)"
)
@Component
public class SubUserMapperImpl implements SubUserMapper {

    @Override
    public SubUserDTO toDto(SubUserEntity subUserEntity) {
        if ( subUserEntity == null ) {
            return null;
        }

        SubUserDTO.SubUserDTOBuilder subUserDTO = SubUserDTO.builder();

        subUserDTO.id( subUserEntity.getId() );
        subUserDTO.email( subUserEntity.getEmail() );
        subUserDTO.memberId( subUserEntity.getMemberId() );
        subUserDTO.managerName( subUserEntity.getManagerName() );
        subUserDTO.departmentName( subUserEntity.getDepartmentName() );
        subUserDTO.contactNumber( subUserEntity.getContactNumber() );
        subUserDTO.memoDescription( subUserEntity.getMemoDescription() );
        subUserDTO.joinDate( subUserEntity.getJoinDate() );
        subUserDTO.deletedDate( subUserEntity.getDeletedDate() );
        subUserDTO.isDeleted( subUserEntity.getIsDeleted() );

        return subUserDTO.build();
    }

    @Override
    public SubUserEntity toEntity(SubUserDTO subUserDTO) {
        if ( subUserDTO == null ) {
            return null;
        }

        SubUserEntity.SubUserEntityBuilder subUserEntity = SubUserEntity.builder();

        subUserEntity.id( subUserDTO.getId() );
        subUserEntity.memberId( subUserDTO.getMemberId() );
        subUserEntity.password( subUserDTO.getPassword() );
        subUserEntity.email( subUserDTO.getEmail() );
        subUserEntity.managerName( subUserDTO.getManagerName() );
        subUserEntity.departmentName( subUserDTO.getDepartmentName() );
        subUserEntity.contactNumber( subUserDTO.getContactNumber() );
        subUserEntity.memoDescription( subUserDTO.getMemoDescription() );
        subUserEntity.joinDate( subUserDTO.getJoinDate() );
        subUserEntity.deletedDate( subUserDTO.getDeletedDate() );
        subUserEntity.isDeleted( subUserDTO.getIsDeleted() );

        return subUserEntity.build();
    }
}
