package com.camelsoft.portal.dtos;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RequirementRequest {
    @NotEmpty(message = "Description is mandatory")
    @NotBlank(message = "Description is mandatory")
    private String description;
}
