package com.bankapplicationmicroservices.bank_service.repository;

import com.bankapplicationmicroservices.bank_service.entity.AccountType;
import com.bankapplicationmicroservices.bank_service.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepo extends JpaRepository<BankAccount, Long> {
    @Query(value = "SELECT * FROM bank_account ORDER BY balance DESC LIMIT 5", nativeQuery = true)
    List<BankAccount> findTop5AccountsByBalance();

    List<BankAccount> findByAccountType(AccountType accountType);

    // ADD: list accounts for a customer
    List<BankAccount> findByCustomerId(Long customerId);

    // ADD: get account only if it belongs to that customer
    Optional<BankAccount> findByAccountIdAndCustomerId(Long accountId, Long customerId);

}
