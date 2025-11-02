package com.bankapplicationmicroservices.bank_service.client;

import com.bankapplicationmicroservices.bank_service.dto.CustomerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service")
public interface CustomerClient {
    @GetMapping("/customers/{id}")
    CustomerDto getCustomer(@PathVariable("id") Long id);
}
