package com.bankapplicationmicroservices.customer_service.dto;

/*import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;*/
import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class CustomerDto {
    private Long customerId;

    @NotBlank
    @Size(min=5, max=100)
    @Pattern(regexp="^[A-Za-z ]+$", message="Name must not contain special chars or numbers")
    private String name;

    @NotBlank @Size(min=8)
    @Pattern(regexp="^[A-Za-z0-9]+$")
    private String pan;

    @Email
    private String email;

    @Pattern(regexp="^(?:\\+91[\\-\\s]?|0)?[6-9]\\d{9}$", message="Phone must be a valid Indian number")
    private String phone;
}
