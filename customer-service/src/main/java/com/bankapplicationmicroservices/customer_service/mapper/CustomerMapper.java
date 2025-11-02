package com.bankapplicationmicroservices.customer_service.mapper;

import com.bankapplicationmicroservices.customer_service.dto.CustomerDto;
import com.bankapplicationmicroservices.customer_service.entity.Customer;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerDto toDto(Customer customer);
    List<CustomerDto> toDtoList(List<Customer> customers);

    @Mappings({
            @Mapping(target = "customerId", ignore = true),
            @Mapping(target = "createdAt",  ignore = true),
            @Mapping(target = "updatedAt",  ignore = true)
    })
    Customer toEntity(CustomerDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
            @Mapping(target = "customerId", ignore = true),
            @Mapping(target = "createdAt",  ignore = true),
            @Mapping(target = "updatedAt",  ignore = true)
    })
    void updateEntityFromDto(CustomerDto dto, @MappingTarget Customer entity);
}

