package com.camelsoft.portal.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotEmpty(message = "password is mandatory")
        @NotBlank(message = "password is mandatory")
        @Size(min = 8,max = 20,message = "password should be between 8 and 20 characters")
        String password,
        @NotEmpty(message = "password is mandatory")
        @NotBlank(message = "password is mandatory")
        @Size(min = 8,max = 20,message = "password should be between 8 and 20 characters")
        String repeatPassword) {
}
