package com.camelsoft.portal.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requirement")
public class Requirement {
    @Id
    @GeneratedValue
    private Integer id;
    private String description;
    @CreatedDate
    private Date createdDate;

    @ManyToOne
    @JoinColumn(name = "job_id")
    @JsonIgnore
    private Job job;
}
