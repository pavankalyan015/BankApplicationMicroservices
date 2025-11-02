package com.bankapplicationmicroservices.transaction_service.exception;


//@ResponseStatus(code= HttpStatus.NOT_FOUND)
public class BankAccountNotFoundException extends RuntimeException {
    public BankAccountNotFoundException(String message) {
        super(message);
    }
}
