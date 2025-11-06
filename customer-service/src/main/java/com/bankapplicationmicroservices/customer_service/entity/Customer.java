package com.bankapplicationmicroservices.customer_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name="customer")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customerId;

    @Column(nullable=false, length=100)
    @NotBlank @Size(min=5, max=100)
    @Pattern(regexp="^[A-Za-z ]+$", message="Name must not contain special chars or numbers")
    private String name;

    @Column(nullable=false, length=20)
    @NotBlank @Size(min=8) @Pattern(regexp="^[A-Za-z0-9]+$")
    private String pan;

    @Column(length=100) @Email
    private String email;

    @Column(length=20)
    @Pattern(regexp="^(?:\\+91[\\-\\s]?|0)?[6-9]\\d{9}$", message="Phone must be a valid Indian number")
    private String phone;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist void prePersist(){ createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate  void preUpdate(){ updatedAt = LocalDateTime.now(); }
}
