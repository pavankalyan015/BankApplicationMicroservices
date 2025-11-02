package com.bankapplicationmicroservices.transaction_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID transactionId;

    private Long fromAccountId;
    private Long toAccountId;

    @Column(nullable=false)
    private double amount;

    @Column(nullable=false)
    private double initialBalance;

    @Column(nullable=false)
    private double remainingBalance;

    @Enumerated(EnumType.STRING) @Column(nullable=false)
    private TransactionType transactionType;

    @Column(nullable=false)
    private LocalDateTime timeStamp;

    @Enumerated(EnumType.STRING) @Column(nullable=false)
    private Status status;

    @PrePersist
    protected void onCreate() { this.timeStamp = LocalDateTime.now(); }
}
