package com.example.joy_ocean.model;

import com.example.joy_ocean.model.audit.DateAudit;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name="post")
public class Post extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "postno")
    private Long postno;

    private String user_address;
    private Long painting_no;
    private String painting_title;
    private String painting_username;
    private Double klay;

    private Long exhibit_no;
    private String exhibit_title;

    public Post(String user_address, Painting painting, Double klay){
        this.user_address = user_address;
        this.painting_no = painting.getPno();
        this.painting_title = painting.getTitle();
        this.painting_username = painting.getUsername();
        this.exhibit_no = painting.getExhibition().getEno();
        this.exhibit_title = painting.getExhibition().getTitle();
        this.klay = klay;
    }

    public Post() {

    }

}
