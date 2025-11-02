package com.bankapplicationmicroservices.transaction_service.exception;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BankAccountNotFoundException.class)
    public ResponseEntity<ExceptionResponce> handleBankAccountNotFound(BankAccountNotFoundException ex, HttpServletRequest req){
        ExceptionResponce exceptionResponce = new ExceptionResponce(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Account Not Found",
                ex.getMessage(),
                req.getRequestURI()
        );
        return new ResponseEntity<>(exceptionResponce,HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ExceptionResponce> handleCustomerNotFound(CustomerNotFoundException ex, HttpServletRequest req){
        ExceptionResponce response = new ExceptionResponce(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Customer Not Found",
                ex.getMessage(),
                req.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ExceptionResponce> handleTransactionNotFound(TransactionNotFoundException ex, HttpServletRequest req){
        ExceptionResponce response = new ExceptionResponce(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Transaction Not Found",
                ex.getMessage(),
                req.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HighValueTransactionException.class)
    public ResponseEntity<ExceptionResponce> handleHighValueTransaction(HighValueTransactionException ex, HttpServletRequest req){
        ExceptionResponce response = new ExceptionResponce(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "High Value Transaction",
                ex.getMessage(),
                req.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ExceptionResponce> handleInsufficientFunds(InsufficientFundsException ex, HttpServletRequest req){
        ExceptionResponce response = new ExceptionResponce(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Insufficient Funds",
                ex.getMessage(),
                req.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotACustomerException.class)
    public ResponseEntity<ExceptionResponce> handleNotACustomer(NotACustomerException ex, HttpServletRequest req){
        ExceptionResponce response = new ExceptionResponce(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Not a Customer",
                ex.getMessage(),
                req.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponce> handleAll(Exception ex, WebRequest request) {
        ex.printStackTrace();
        ExceptionResponce err = new ExceptionResponce(LocalDateTime.now(),500, "Internal Server Error", ex.getMessage(), request.getDescription(false).replace("uri=",""));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }


}
