package com.bankapplicationmicroservices.bank_service.exception;

public class MinimumBalanceExpection extends RuntimeException {
    public MinimumBalanceExpection(String message) {
        super(message);
    }
}
