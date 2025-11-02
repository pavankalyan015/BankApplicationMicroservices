package com.bankapplicationmicroservices.transaction_service.service;

import com.bankapplicationmicroservices.transaction_service.client.BankAccountClient;
import com.bankapplicationmicroservices.transaction_service.dto.TransactionDto;
import com.bankapplicationmicroservices.transaction_service.entity.Status;
import com.bankapplicationmicroservices.transaction_service.entity.Transaction;
import com.bankapplicationmicroservices.transaction_service.entity.TransactionType;
import com.bankapplicationmicroservices.transaction_service.exception.BankAccountNotFoundException;
import com.bankapplicationmicroservices.transaction_service.exception.HighValueTransactionException;
import com.bankapplicationmicroservices.transaction_service.exception.InsufficientFundsException;
import com.bankapplicationmicroservices.transaction_service.repository.TransactionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepo repo;
    private final BankAccountClient accountClient;
    private static final double MAX_TRANSFER = 100_000d;

    public void record(TransactionDto r){

        TransactionType type = TransactionType.valueOf(r.getTransactionType().toUpperCase());
        Status status = Status.valueOf(r.getStatus().toUpperCase());

        Transaction t = new Transaction();
        switch (type) {
            case DEPOSIT -> { t.setFromAccountId(null); t.setToAccountId(r.getToAccountId()); }
            case WITHDRAW -> { t.setFromAccountId(r.getFromAccountId()); t.setToAccountId(null); }
        }
        t.setAmount(r.getAmount());
        t.setInitialBalance(r.getInitialBalance());
        t.setRemainingBalance(r.getRemainingBalance());
        t.setTransactionType(type);
        t.setStatus(status);
        repo.save(t);
    }

    public void transfer(Long fromId, Long toId, double amount) {

        if (fromId == null || toId == null) {
            throw new BankAccountNotFoundException("Source and destination account ids are required");
        }
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Source and destination accounts must be different");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }
        if (amount > MAX_TRANSFER) {
            throw new HighValueTransactionException("Cannot be transferred more than " + (long) MAX_TRANSFER);
        }

        if (!accountExists(fromId)) {
            throw new BankAccountNotFoundException("Source account " + fromId + " not found");
        }
        if (!accountExists(toId)) {
            throw new BankAccountNotFoundException("Destination account " + toId + " not found");
        }

        Map<String, Object> from = accountClient.get(fromId);
        Map<String, Object> to = accountClient.get(toId);

        double fromBal = ((Number) from.get("balance")).doubleValue();

        Transaction tx = new Transaction();
        tx.setFromAccountId(fromId);
        tx.setToAccountId(toId);
        tx.setAmount(amount);
        tx.setInitialBalance(fromBal);
        tx.setTransactionType(TransactionType.TRANSFER);

        try {
            accountClient.withdraw(fromId, amount);

            accountClient.deposit(toId, amount);

            Map<String, Object> updatedFrom = accountClient.get(fromId);
            double remaining = (updatedFrom != null && updatedFrom.get("balance") instanceof Number)
                    ? ((Number) updatedFrom.get("balance")).doubleValue()
                    : (fromBal - amount);

            tx.setRemainingBalance(remaining);
            tx.setStatus(Status.SUCCESS);
            repo.save(tx);

        } catch (Exception ex) {
            try { accountClient.deposit(fromId, amount); } catch (Exception ignore) {  }

            tx.setRemainingBalance(fromBal);
            tx.setStatus(Status.FAILED);
            repo.save(tx);
            throw ex;
        }
    }

    public List<Transaction> list() { return repo.findAll(); }
    public Optional<Transaction> byId(UUID id) { return repo.findById(id); }
    public List<Transaction> forAccount(Long accountId) {
        return repo.findByFromAccountIdOrToAccountIdOrderByTimeStampDesc(accountId, accountId);
    }

    private boolean accountExists(Long accountId) {
        try {
            return accountClient.get(accountId) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
