package com.bankapplicationmicroservices.customer_service.service;

import com.bankapplicationmicroservices.customer_service.dto.CustomerDto;
import com.bankapplicationmicroservices.customer_service.entity.Customer;
import com.bankapplicationmicroservices.customer_service.exception.CustomerNotFoundException;
import com.bankapplicationmicroservices.customer_service.mapper.CustomerMapper;
import com.bankapplicationmicroservices.customer_service.repository.CustomerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepo customerRepo;
    private final CustomerMapper customerMapper;

    public CustomerDto createCustomer(CustomerDto dto){
        Customer entity = customerMapper.toEntity(dto);
        Customer saved = customerRepo.save(entity);
        return customerMapper.toDto(saved);
    }

    public CustomerDto getCustomerById(Long id){
        Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with id- " + id + " is not exists"));
        return customerMapper.toDto(customer);
    }


    public List<CustomerDto> getAllCustomers(){
        return customerMapper.toDtoList(customerRepo.findAll());
    }

    public void delete(Long id){
        Customer existing = customerRepo.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with id - " + id + " not found"));
        customerRepo.deleteById(id);
    }

    public CustomerDto upDateCustomer(Long id, CustomerDto dto){
        Customer existing = customerRepo.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with id - " + id + " not found"));
        customerMapper.updateEntityFromDto(dto, existing);
        Customer saved = customerRepo.save(existing);
        return customerMapper.toDto(saved);
    }
}


