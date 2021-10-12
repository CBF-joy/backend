package com.example.joy_ocean.payload;

import com.example.joy_ocean.model.Exhibition;
import com.example.joy_ocean.model.Poster;
import com.example.joy_ocean.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class CountPostResponse {
    private Double all_klay;

    private Long eno;

    private String sponsor;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fromDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date toDate;

    private String title;

    private String description;

    private String status;

    private User user;

    private Poster poster;

    public CountPostResponse(){

    }

    public CountPostResponse(Double all_klay, Exhibition exhibition){
        this.all_klay = all_klay;
        this.eno = exhibition.getEno();
        this.sponsor = exhibition.getSponsor();
        this.fromDate = exhibition.getFromDate();
        this.toDate = exhibition.getToDate();
        this.title = exhibition.getTitle();
        this.description = exhibition.getDescription();
        this.status = exhibition.getStatus();
        this.user = exhibition.getUser();
        this.poster = exhibition.getPoster();
    }

}
