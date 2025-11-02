package com.bankapplicationmicroservices.bank_service.exception;

public class NotACustomerException extends RuntimeException {
    public NotACustomerException(String message) {
        super(message);
    }
}
