package com.bankapplicationmicroservices.customer_service.controller;


import com.bankapplicationmicroservices.customer_service.dto.CustomerDto;
import com.bankapplicationmicroservices.customer_service.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping(path="/")
    public String bankApplication(){
        return "Customer-Service";
    }

    @PostMapping(path="/create/{id}")
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody @Valid CustomerDto customerDto,
                                                      @PathVariable Long id) {

        customerDto.setCustomerId(id);

        CustomerDto created = customerService.createCustomer(customerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @GetMapping(path="/{id}")
    public CustomerDto getCustomerById(@PathVariable Long id){
        return customerService.getCustomerById(id);
    }

    @GetMapping(path="/all")
    public List<CustomerDto> getAllCustomers(){
        return customerService.getAllCustomers();
    }

    @DeleteMapping(path="{id}/delete")
    public void deleteCustomer(@PathVariable Long id){
        customerService.delete(id);
    }

    @PutMapping(path = "{id}/update")
    public CustomerDto updateCustomer(@PathVariable Long id, @RequestBody @Valid CustomerDto customerDto){
        return customerService.upDateCustomer(id, customerDto);
    }

}

