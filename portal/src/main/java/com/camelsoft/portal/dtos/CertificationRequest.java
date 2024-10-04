package com.camelsoft.portal.dtos;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CertificationRequest {
    @NotBlank(message = "Degree title is mandatory")
    @NotEmpty(message = "Degree title is mandatory")
    private String title;
    private String description;
    private Date date;
}
