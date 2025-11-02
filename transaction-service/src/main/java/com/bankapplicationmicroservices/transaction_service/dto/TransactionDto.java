package com.bankapplicationmicroservices.transaction_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
