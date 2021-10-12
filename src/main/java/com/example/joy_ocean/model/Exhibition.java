package com.example.joy_ocean.model;

import com.example.joy_ocean.model.audit.DateAudit;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "exhibition")
public class Exhibition extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eno")
    private Long eno;

    private String sponsor;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fromDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date toDate;

    private String title;

    private String description;

    private String status;
    // pre -> ing -> post

    private Double klay;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="user_no")
    private User user;

    @JsonManagedReference
    @OneToOne(fetch=FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "exhibition")
    private Poster poster;

    @JsonManagedReference
    @OneToMany(fetch=FetchType.LAZY,
            cascade = CascadeType.ALL,
            mappedBy="exhibition")
    private Set<Comment> comments = new HashSet<>();

    @JsonManagedReference
    @OneToMany(fetch=FetchType.LAZY,
            cascade = CascadeType.ALL,
            mappedBy = "exhibition")
    private Set<Rate> rate = new HashSet<>();

    @JsonManagedReference
    @OneToMany(fetch=FetchType.LAZY,
            cascade = CascadeType.ALL,
            mappedBy = "exhibition")
    private Set<Painting> painting = new HashSet<>();


    public Exhibition(String sponsor, String fromDate, String toDate, String title,
                      String description, User user, Poster poster) throws ParseException {
        this.sponsor = sponsor;
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.fromDate =  transFormat.parse(fromDate);
        this.toDate = transFormat.parse(toDate);
        this.title = title;
        this.description = description;
        this.user = user;
        this.poster = poster;
        this.status = "pre";
        this.klay = 0.0;
    }


    public Exhibition() {

    }
}
