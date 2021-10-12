package com.example.joy_ocean.model;

import com.example.joy_ocean.model.audit.DateAudit;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name="painting")
public class Painting extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pno")
    private Long pno;

    private String title;

    private String description;

    private String file_uri;

    private String username;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="exhibition_no")
    private Exhibition exhibition;


    public Painting(String file_uri, String title, String description, String username){
        this.file_uri = file_uri;
        this.title = title;
        this.description = description;
        this.username = username;
    }

    public Painting() {

    }


}
