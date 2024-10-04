package com.camelsoft.portal.dtos;



import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
public class ProfileRequest {
    @NotEmpty(message = "firstname is mandatory")
    @NotBlank(message = "firstname is mandatory")
    private String firstname;
    @NotEmpty(message = "lastname is mandatory")
    @NotBlank(message = "lastname is mandatory")
    private String lastname;
    @Column(unique = true)
    @NotEmpty(message = "Email is mandatory")
    @NotBlank(message = "Email is mandatory")
    @Email(message = "email is not formatted")
    private String email;
    @NotEmpty(message = "phoneNumber is mandatory")
    @NotBlank(message = "phoneNumber is mandatory")
    @Column(unique = true)
    private String phoneNumber;
    @NotEmpty(message = "city is mandatory")
    @NotBlank(message = "city is mandatory")
    private String city;
    @NotEmpty(message = "lastPost is mandatory")
    @NotBlank(message = "lastPost is mandatory")
    private String lastPost;
    @NotEmpty(message = "degreeLevel is mandatory")
    @NotBlank(message = "degreeLevel is mandatory")
    private String degreeLevel;
    @NotEmpty(message = "englishLevel is mandatory")
    @NotBlank(message = "englishLevel is mandatory")
    private String englishLevel;
    @NotEmpty(message = "englishLevel is mandatory")
    @NotBlank(message = "englishLevel is mandatory")
    private String contractType;
}
