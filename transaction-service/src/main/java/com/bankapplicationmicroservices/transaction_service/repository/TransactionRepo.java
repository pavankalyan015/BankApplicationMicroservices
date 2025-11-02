package com.bankapplicationmicroservices.transaction_service.repository;

import com.bankapplicationmicroservices.transaction_service.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepo extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByFromAccountIdOrToAccountIdOrderByTimeStampDesc(Long from, Long to);
}
