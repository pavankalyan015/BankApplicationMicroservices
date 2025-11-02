package com.bankapplicationmicroservices.transaction_service.exception;

public class NotACustomerException extends RuntimeException {
    public NotACustomerException(String message) {
        super(message);
    }
}
