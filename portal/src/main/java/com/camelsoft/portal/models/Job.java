package com.camelsoft.portal.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "job")
@EntityListeners(AuditingEntityListener.class)
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    private String description;
    private String responsibilities;
    private String experienceLevel;
    private String jobType;
    private String location;
    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private Date createdDate;
    private Date postDate;
    private Date closeDate;
    private Integer viewCount;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private Set<Requirement> requirements;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private Set<Category> categories;


    @OneToMany(mappedBy = "job")
    private Set<Application> applications;


    @ManyToMany
    @JoinTable(
            name = "viewers",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> viewers;

    @ManyToMany
    @JoinTable(
            name = "user_job",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users = new HashSet<>();
}




