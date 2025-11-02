package com.bankapplicationmicroservices.bank_service.client;

import com.bankapplicationmicroservices.bank_service.dto.TransactionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "transaction-service")
public interface TransactionClient {
    @PostMapping("/trans/create")
    void record(@RequestBody TransactionDto request);
}
