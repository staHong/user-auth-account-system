package com.ds.auth.mapper;

import com.ds.auth.dto.UserDTO;
import com.ds.auth.entity.UserEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-19T23:31:13+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.11.1.jar, environment: Java 17.0.15 (Homebrew)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserEntity toEntity(UserDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        UserEntity.UserEntityBuilder userEntity = UserEntity.builder();

        userEntity.id( userDTO.getId() );
        userEntity.corpRegNo( userDTO.getCorpRegNo() );
        userEntity.bizRegNo( userDTO.getBizRegNo() );
        userEntity.companyName( userDTO.getCompanyName() );
        userEntity.password( userDTO.getPassword() );
        userEntity.email( userDTO.getEmail() );
        userEntity.memberType( userDTO.getMemberType() );
        userEntity.businessLicensePath( userDTO.getBusinessLicensePath() );
        userEntity.businessLicenseName( userDTO.getBusinessLicenseName() );
        userEntity.managerName( userDTO.getManagerName() );
        userEntity.managerPhoneNumber( userDTO.getManagerPhoneNumber() );
        userEntity.responsibleName( userDTO.getResponsibleName() );
        userEntity.responsiblePhoneNumber( userDTO.getResponsiblePhoneNumber() );
        userEntity.joinDate( userDTO.getJoinDate() );
        userEntity.withdrawalDate( userDTO.getWithdrawalDate() );
        userEntity.isDeleted( userDTO.getIsDeleted() );

        return userEntity.build();
    }

    @Override
    public UserDTO toDto(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }

        UserDTO.UserDTOBuilder userDTO = UserDTO.builder();

        userDTO.id( userEntity.getId() );
        userDTO.corpRegNo( userEntity.getCorpRegNo() );
        userDTO.bizRegNo( userEntity.getBizRegNo() );
        userDTO.companyName( userEntity.getCompanyName() );
        userDTO.email( userEntity.getEmail() );
        userDTO.memberType( userEntity.getMemberType() );
        userDTO.businessLicensePath( userEntity.getBusinessLicensePath() );
        userDTO.businessLicenseName( userEntity.getBusinessLicenseName() );
        userDTO.managerName( userEntity.getManagerName() );
        userDTO.managerPhoneNumber( userEntity.getManagerPhoneNumber() );
        userDTO.responsibleName( userEntity.getResponsibleName() );
        userDTO.responsiblePhoneNumber( userEntity.getResponsiblePhoneNumber() );
        userDTO.joinDate( userEntity.getJoinDate() );
        userDTO.withdrawalDate( userEntity.getWithdrawalDate() );
        userDTO.isDeleted( userEntity.getIsDeleted() );

        return userDTO.build();
    }
}
