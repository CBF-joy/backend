package com.example.joy_ocean.model;

import com.example.joy_ocean.model.audit.DateAudit;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name="bid")
public class Bid extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bno")
    private Long bno;

    private String token_id;

    private String user_address;

    private Double price;

    private String isSuccess;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="auction_no")
    private Auction auction;


    public Bid(String token_id, String user_address, Double price, Auction auction){
        this.token_id = token_id;
        this.user_address = user_address;
        this.price = price;
        this.auction = auction;
        this.isSuccess = "no";
    }

    public Bid() {

    }

}
