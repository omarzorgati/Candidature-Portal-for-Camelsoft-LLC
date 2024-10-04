package com.camelsoft.portal.dtos;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CategoryRequest {
    @NotEmpty(message = "Title is mandatory")
    @NotBlank(message = "Title is mandatory")
    private String title;
}
