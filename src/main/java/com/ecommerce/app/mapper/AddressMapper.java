package com.ecommerce.app.mapper;

import com.ecommerce.app.dto.AddressDto;
import com.ecommerce.app.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressDto toDto(Address address);

    @Mapping(target = "user", ignore = true)
    Address toEntity(AddressDto addressDto);
}
