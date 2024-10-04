package com.camelsoft.portal.dtos;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class RegistrationRequest {
    @NotEmpty(message = "Firstname is mandatory")
    @NotBlank(message = "Firstname is mandatory")
    private String firstname;
    @NotEmpty(message = "Lastname is mandatory")
    @NotBlank(message = "Lastname is mandatory")
    private String lastname;
    @NotEmpty(message = "Email is mandatory")
    @NotBlank(message = "Email is mandatory")
    @Email(message = "email is not formatted")
    private String email;
    @NotEmpty(message = "phoneNumber is mandatory")
    @NotBlank(message = "phoneNumber is mandatory")
    @Pattern(regexp = "^[0-9]{7,15}$", message = "Invalid phone number format")
    private String phoneNumber;
    @NotEmpty(message = "password is mandatory")
    @NotBlank(message = "password is mandatory")
    @Size(min = 8,max = 20,message = "password should be between 8 and 20 characters")
    private String password;
    @NotEmpty(message = "Repeat password is mandatory")
    @NotBlank(message = "Repeat password is mandatory")
    @Size(min = 8,max = 20,message = "password should be between 8 and 20 characters")
    private String repeatPassword;
}
