package com.example.joy_ocean.payload;

import com.example.joy_ocean.model.Auction;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class AuctionForBid {

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


    public AuctionForBid(Auction auction){
        this.ano = auction.getAno();
        this.token_id = auction.getToken_id();
        this.fromDate =  auction.getFromDate();
        this.toDate = auction.getToDate();
        this.owner_address = auction.getOwner_address();
        this.owner_username = auction.getOwner_username();
        this.minium_price = auction.getMinium_price();
        this.status = auction.getStatus();
        this.successful_bid_price = auction.getSuccessful_bid_price();
        this.successful_bid_useraddress = auction.getSuccessful_bid_useraddress();
    }

}
