package com.bankapplicationmicroservices.customer_service.exception;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

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
