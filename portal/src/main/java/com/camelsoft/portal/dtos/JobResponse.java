package com.camelsoft.portal.dtos;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
public class JobResponse {
    private Integer id;
    private String title;
    private String description;
    private String responsibilities;
    private String experienceLevel;
    private String jobType;
    private String location;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private Date createdDate;
    private Date postDate;
    private Date closeDate;
    private String status;
    private Set<String> categories;
    private Integer viewCount ;
}
