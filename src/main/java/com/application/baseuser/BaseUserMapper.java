package com.application.baseuser;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BaseUserMapper {

    BaseUserDto toDto(BaseUser customer);

    BaseUser toEntity(BaseUserDto customerPostDTO);

}

