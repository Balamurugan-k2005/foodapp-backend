package com.ecommerce.app.mapper;

import com.ecommerce.app.dto.UserDto;
import com.ecommerce.app.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", source = "role")
    UserDto toDto(User user);
}
