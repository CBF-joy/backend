package com.example.joy_ocean.model;


import com.example.joy_ocean.model.audit.DateAudit;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name="poster")
public class Poster extends DateAudit {

    @Id
    @Column(name="posno")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long posno;

    @Column(name="file_name")
    private String fileName;

    @Column(name="file_type")
    private String fileType;

    @Column(name="file_uri")
    private String fileUri;

    @Column(name="file_size")
    private Long fileSize;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    @OneToOne
    @JoinColumn(name="ex_no")
    private Exhibition exhibition;

    public Poster(){}

    public Poster(String fileName, String fileType, String fileUri, Long fileSize){
        this.fileName = fileName;
        this.fileUri = fileUri;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }


}
