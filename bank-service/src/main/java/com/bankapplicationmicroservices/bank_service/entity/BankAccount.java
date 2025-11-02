package com.bankapplicationmicroservices.bank_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="bank_account")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {
    @Id
    @TableGenerator(
            name="account_gen", table="id_generator",
            pkColumnName="gen_name", valueColumnName="gen_value",
            pkColumnValue="account_id", initialValue=10000000, allocationSize=1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "account_gen")
    private Long accountId;

    @NotNull(message="Customer is required")
    private Long customerId;

    @Column(nullable=false, length=50)
    @NotBlank
    @Size(min=5, max=50) @Pattern(regexp="^[A-Za-z ]+$")
    private String accountHolderName;

    @Enumerated(EnumType.STRING) @NotNull
    private AccountType accountType;

    @Column(nullable=false) @NotNull
    private Double balance;

    private Double interestRate;

    @Enumerated(EnumType.STRING)
    private AccountStatus status = AccountStatus.ACTIVE;

    private LocalDateTime createdAt;
    private LocalDateTime lastTransaction;
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        if (this.accountType == AccountType.SAVINGS && this.interestRate == null) this.interestRate = 3.5;
        else if (this.accountType == AccountType.CURRENT && this.interestRate == null) this.interestRate = 0.0;
    }
    @PreUpdate void onUpdate(){ this.updatedAt = LocalDateTime.now(); }
}
