package com.bankapplicationmicroservices.bank_service.mapper;

import com.bankapplicationmicroservices.bank_service.dto.BankAccountDto;
import com.bankapplicationmicroservices.bank_service.entity.BankAccount;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BankAccountMapper {

    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastTransaction", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    BankAccount toEntity(BankAccountDto dto);

    BankAccountDto toDto(BankAccount entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastTransaction", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(BankAccountDto dto, @MappingTarget BankAccount entity);
}
