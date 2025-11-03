package com.bankapplicationmicroservices.bank_service.controller;

import com.bankapplicationmicroservices.bank_service.dto.BankAccountDto;
import com.bankapplicationmicroservices.bank_service.entity.AccountType;
import com.bankapplicationmicroservices.bank_service.service.BankAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountService service;
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public BankAccountDto create(@Valid @RequestBody BankAccountDto dto) {
        return service.create(dto);
    }

    @GetMapping("/all")
    public List<BankAccountDto> all() {
        return service.all();
    }

    @GetMapping(params = "customerId")
    public List<BankAccountDto> byCustomer(@RequestParam Long customerId) {
        return service.byCustomer(customerId);
    }

    @GetMapping(value = "/{id}", params = "customerId")
    public BankAccountDto getForCustomer(@PathVariable("id") Long accountId,
                                         @RequestParam Long customerId) {
        return service.getForCustomer(accountId, customerId);
    }

    @GetMapping("/{id}")
    public BankAccountDto get(@PathVariable Long id) {
        return service.get(id);
    }


    @DeleteMapping("/{id}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PutMapping("/{id}/update")
    public BankAccountDto update(@PathVariable Long id, @Valid @RequestBody BankAccountDto dto) {
        return service.update(id, dto);
    }

    @GetMapping("/{id}/balance")
    public Double balance(@PathVariable Long id) {
        return service.get(id).getBalance();
    }

    @PutMapping("/{id}/withdraw")
    public BankAccountDto withdraw(@PathVariable Long id, @RequestParam double amount) {
        return service.withdraw(id, amount);
    }

    @PutMapping("/{id}/deposit")
    public BankAccountDto deposit(@PathVariable Long id, @RequestParam double amount) {
        return service.deposit(id, amount);
    }

    @GetMapping("/report/topaccounts")
    public List<BankAccountDto> top() {
        return service.top();
    }

    @GetMapping("/by-type")
    public List<BankAccountDto> byType(@RequestParam AccountType type) {
        return service.byType(type);
    }
}
