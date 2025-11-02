package com.bankapplicationmicroservices.transaction_service.controller;

import com.bankapplicationmicroservices.transaction_service.client.BankAccountClient;
import com.bankapplicationmicroservices.transaction_service.dto.TransactionDto;
import com.bankapplicationmicroservices.transaction_service.dto.TransferRequestDto;
import com.bankapplicationmicroservices.transaction_service.entity.Transaction;
import com.bankapplicationmicroservices.transaction_service.service.TransactionService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Data
@RestController
@RequestMapping("/trans")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/create")
    public void create(@RequestBody TransactionDto txDto){
        transactionService.record(txDto);
    }

    @PutMapping("/transfer")
    public void transfer(@RequestBody TransferRequestDto dto){
        transactionService.transfer(dto.getFromAccountId(), dto.getToAccountId(), dto.getAmount());
    }

    @GetMapping("/all")
    public List<Transaction> transactions(@RequestParam(required=false) Long accountId){
        return (accountId == null) ? transactionService.list() : transactionService.forAccount(accountId);
    }

    @GetMapping("/{id}")
    public Transaction get(@PathVariable UUID id){
        return transactionService.byId(id).orElseThrow(() -> new RuntimeException("Transaction not found: "+id));
    }
}
