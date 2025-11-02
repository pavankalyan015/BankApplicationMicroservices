package com.bankapplicationmicroservices.bank_service.dto;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private Long fromAccountId;
    private Long toAccountId;
    private double amount;
    private double initialBalance;
    private double remainingBalance;
    private String transactionType;
    private String status;
}
