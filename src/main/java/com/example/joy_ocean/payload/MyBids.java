package com.example.joy_ocean.payload;


import com.example.joy_ocean.model.Bid;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyBids {

    private Long bno;

    private String token_id;

    private String user_address;

    private Double price;

    private String isSuccess;

    private AuctionForBid auction;

    public MyBids(Bid bid){
        this.bno = bid.getBno();
        this.token_id = bid.getToken_id();
        this.user_address = bid.getUser_address();
        this.price = bid.getPrice();
        this.isSuccess = bid.getIsSuccess();
        AuctionForBid ex = new AuctionForBid(bid.getAuction());
        this.auction = ex;
    }



}
