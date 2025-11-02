package com.bankapplicationmicroservices.api_gateway.repository;

import com.bankapplicationmicroservices.api_gateway.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
