package com.ds.auth.mapper;

import com.ds.auth.dto.UserDTO;
import com.ds.auth.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * UserSignUpDTO -> UserEntity 변환
     * @param userDTO 회원 DTO
     * @return UserEntity 객체
     */
    UserEntity toEntity(UserDTO userDTO);

    /**
     * UserEntity -> UserDTO 변환
     * @param userEntity 사용자 엔티티
     * @return UserDTO 객체
     */
    @Mapping(target = "password", ignore = true) // 보안상 password 제외
    UserDTO toDto(UserEntity userEntity);
}