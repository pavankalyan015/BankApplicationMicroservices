package com.bankapplicationmicroservices.bank_service.dto;

import com.bankapplicationmicroservices.bank_service.entity.AccountType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class BankAccountDto {

    private Long accountId;

    @NotNull(message = "Customer is required")
    private Long customerId;

    @NotBlank @Size(min = 5, max = 50)
    @Pattern(regexp = "^[A-Za-z ]+$")
    private String accountHolderName;

    @NotNull @PositiveOrZero
    private Double balance;

    @NotNull
    private AccountType accountType;

    @PositiveOrZero
    private Double interestRate;
}
