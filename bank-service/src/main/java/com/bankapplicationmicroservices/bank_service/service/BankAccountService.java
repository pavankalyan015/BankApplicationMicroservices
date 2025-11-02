package com.bankapplicationmicroservices.bank_service.service;

import com.bankapplicationmicroservices.bank_service.client.CustomerClient;
import com.bankapplicationmicroservices.bank_service.client.TransactionClient;
import com.bankapplicationmicroservices.bank_service.dto.BankAccountDto;
import com.bankapplicationmicroservices.bank_service.dto.TransactionDto;
import com.bankapplicationmicroservices.bank_service.entity.AccountStatus;
import com.bankapplicationmicroservices.bank_service.entity.AccountType;
import com.bankapplicationmicroservices.bank_service.entity.BankAccount;
import com.bankapplicationmicroservices.bank_service.exception.*;
import com.bankapplicationmicroservices.bank_service.mapper.BankAccountMapper;
import com.bankapplicationmicroservices.bank_service.repository.BankAccountRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BankAccountService {

    private final BankAccountRepo repo;
    private final CustomerClient customers;
    private final TransactionClient txClient;
    private final BankAccountMapper mapper;

    public BankAccountDto create(BankAccountDto dto) {
        try {
            customers.getCustomer(dto.getCustomerId());
        } catch (Exception ex) {
            throw new NotACustomerException(dto.getCustomerId() + " is not a customer");
        }
        if (dto.getBalance() == null || dto.getBalance() < 1000) {
            throw new MinimumBalanceExpection("Minimum Balance Should be 1000");
        }
        if(dto.getInterestRate()==null)
            dto.setInterestRate(3.5);

        BankAccount entity = mapper.toEntity(dto);
        entity.setLastTransaction(LocalDateTime.now());
        BankAccount saved = repo.save(entity);
        return mapper.toDto(saved);
    }

    public List<BankAccountDto> all() {
        return repo.findAll().stream().map(mapper::toDto).toList();
    }

    public BankAccountDto get(Long id) {
        if (!repo.existsById(id)) throw new BankAccountNotFoundException("Account not found: " + id);
        return mapper.toDto(findEntity(id));
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new BankAccountNotFoundException("Account not found: " + id);
        repo.deleteById(id);
    }

    public BankAccountDto update(Long id, BankAccountDto dto) {
        if (!repo.existsById(id)) throw new BankAccountNotFoundException("Account not found: " + id);
        BankAccount entity = findEntity(id);
        mapper.updateEntityFromDto(dto, entity);
        entity.setUpdatedAt(LocalDateTime.now());
        return mapper.toDto(repo.save(entity));
    }

    public BankAccountDto deposit(Long id, double amount) {
        if (!repo.existsById(id)) throw new BankAccountNotFoundException("Account not found: " + id);
        if (amount > 100000) throw new HighValueTransactionException("Cannot be deposited more than 100000");
        BankAccount a = findEntity(id);
        double before = a.getBalance();

        a.setBalance(before + amount);
        a.setLastTransaction(LocalDateTime.now());
        BankAccount saved = repo.save(a);

        try {
            txClient.record(new TransactionDto(
                    null, saved.getAccountId(), amount, before, saved.getBalance(),
                    "DEPOSIT", "SUCCESS"
            ));
        } catch (Exception ignore) { }
        return mapper.toDto(saved);
    }

    public BankAccountDto withdraw(Long id, double amount) {
        if (!repo.existsById(id)) throw new BankAccountNotFoundException("Account not found: " + id);
        BankAccount a = findEntity(id);
        double before = a.getBalance();

        boolean failed = (a.getAccountType() == AccountType.SAVINGS && a.getBalance() < amount)
                || (a.getAccountType() == AccountType.CURRENT && (a.getBalance() + 5000) < amount);
        if (failed) {
            try {
                txClient.record(new TransactionDto(
                        a.getAccountId(), null, amount, before, before,
                        "WITHDRAW", "FAILED"
                ));
            } catch (Exception ignore) { }
            throw new InsufficientFundsException("Withdraw amount exceeds available amount: " + a.getBalance());
        }

        a.setBalance(before - amount);
        a.setLastTransaction(LocalDateTime.now());
        if (a.getAccountType() == AccountType.SAVINGS && a.getBalance() == 0) a.setStatus(AccountStatus.CLOSED);
        if (a.getAccountType() == AccountType.CURRENT && a.getBalance() == -5000) a.setStatus(AccountStatus.CLOSED);
        BankAccount saved = repo.save(a);

        try {
            txClient.record(new TransactionDto(
                    saved.getAccountId(), null, amount, before, saved.getBalance(),
                    "WITHDRAW", "SUCCESS"
            ));
        } catch (Exception ignore) { }
        return mapper.toDto(saved);
    }

    public List<BankAccountDto> top() {
        return repo.findTop5AccountsByBalance().stream().map(mapper::toDto).toList();
    }

    public List<BankAccountDto> byType(AccountType type) {
        return repo.findByAccountType(type).stream().map(mapper::toDto).toList();
    }

    private BankAccount findEntity(Long id) {
        return repo.findById(id).orElseThrow(() -> new BankAccountNotFoundException("Account not found: " + id));
    }
}
