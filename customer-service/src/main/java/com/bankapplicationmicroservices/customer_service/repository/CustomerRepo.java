package com.bankapplicationmicroservices.customer_service.repository;

import com.bankapplicationmicroservices.customer_service.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepo extends JpaRepository<Customer, Long> {

}
