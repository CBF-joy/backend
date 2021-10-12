package com.example.joy_ocean.model;

import com.example.joy_ocean.model.audit.DateAudit;
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
@Table(name = "auction")
public class Auction extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ano")
    private Long ano;

    private String token_id;

    private String owner_address;

    private String owner_username;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fromDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date toDate;

    private Double minium_price;

    private String status;

    private String successful_bid_price;

    private String successful_bid_useraddress;

    @JsonManagedReference
    @OneToMany(fetch=FetchType.LAZY,
            cascade = CascadeType.ALL,
            mappedBy="auction")
    private Set<Bid> bids = new HashSet<>();


    public Auction(String token_id, String fromDate, String toDate, String owner_address,
                      String owner_username, Double minium_price) throws ParseException {
        this.token_id = token_id;
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.fromDate =  transFormat.parse(fromDate);
        this.toDate = transFormat.parse(toDate);
        this.owner_address = owner_address;
        this.owner_username = owner_username;
        this.minium_price = minium_price;
        this.status = "ing";
    }


    public Auction() {

    }


}
