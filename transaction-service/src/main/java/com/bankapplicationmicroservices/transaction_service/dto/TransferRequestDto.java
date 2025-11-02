package com.bankapplicationmicroservices.transaction_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDto {
    private Long fromAccountId;
    private Long toAccountId;
    private double amount;
}
