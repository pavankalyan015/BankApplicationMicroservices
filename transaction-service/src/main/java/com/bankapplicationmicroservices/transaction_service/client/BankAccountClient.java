package com.bankapplicationmicroservices.transaction_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name="bank-service")
public interface BankAccountClient {
    @GetMapping("/accounts/{id}")
    Map<String,Object> get(@PathVariable("id") Long id);
    @PutMapping("/accounts/{id}/withdraw") Map<String,Object> withdraw(@PathVariable("id") Long id, @RequestParam("amount") double amount);
    @PutMapping("/accounts/{id}/deposit") Map<String,Object> deposit(@PathVariable("id") Long id, @RequestParam("amount") double amount);
}