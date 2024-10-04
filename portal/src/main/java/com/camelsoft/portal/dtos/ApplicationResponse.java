package com.camelsoft.portal.dtos;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class ApplicationResponse {
    private Integer id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private Date createdDate;
    private String cvPath;
    private boolean archived;
    private String type;
    private String status;
    private String jobTitle;
    private String userName;

}
