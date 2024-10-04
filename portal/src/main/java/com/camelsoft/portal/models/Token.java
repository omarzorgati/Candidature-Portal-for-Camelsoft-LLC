package com.camelsoft.portal.models;


import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Token {
    @Id
    @GeneratedValue
    private Integer id;
    private String token;

    private boolean expired;
    private boolean revoked;



    @ManyToOne
    //work cv !!
    @JoinColumn(name = "userId", nullable = false)
    private User user;

}
