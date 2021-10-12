package com.example.joy_ocean.payload;

import com.example.joy_ocean.model.Exhibition;
import com.example.joy_ocean.model.Poster;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.ParseException;
import java.util.Date;

@Getter
@Setter
public class ExhibitionForPainting {
    private Long eno;
    private String sponsor;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fromDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date toDate;
    private String title;
    private String description;
    private String status;
    private Poster poster;

    public ExhibitionForPainting(Exhibition exhibition) throws ParseException {
        this.sponsor = exhibition.getSponsor();
        this.fromDate =  exhibition.getFromDate();
        this.toDate = exhibition.getToDate();
        this.title = exhibition.getTitle();
        this.description = exhibition.getDescription();
        this.eno = exhibition.getEno();
        this.poster = exhibition.getPoster();
        this.status = exhibition.getStatus();
    }


    public ExhibitionForPainting() {

    }

}
