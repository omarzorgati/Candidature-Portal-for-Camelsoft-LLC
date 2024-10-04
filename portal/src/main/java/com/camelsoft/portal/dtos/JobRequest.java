package com.camelsoft.portal.dtos;


import com.camelsoft.portal.models.JobStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class JobRequest {
    @NotEmpty(message = "title is mandatory")
    @NotBlank(message = "title is mandatory")
    private String title;
    @NotEmpty(message = "description is mandatory")
    @NotBlank(message = "description is mandatory")
    private String description;
    @NotEmpty(message = "responsibilities is mandatory")
    @NotBlank(message = "responsibilities is mandatory")
    private String responsibilities;
    @NotEmpty(message = "experienceLevel is mandatory")
    @NotBlank(message = "experienceLevel is mandatory")
    private String experienceLevel;
    @NotEmpty(message = "jobType is mandatory")
    @NotBlank(message = "jobType is mandatory")
    private String jobType;
    @NotEmpty(message = "location is mandatory")
    @NotBlank(message = "location is mandatory")
    private String location;
    @NotNull(message = "Post date is mandatory")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date postDate;
    @NotNull(message = "Close date is mandatory")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date closeDate;
    private JobStatus status;

}
