package com.example.joy_ocean.payload;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserProfile{
    private Long id;
    private String username;
    private String name;
    private String email;
    private Set roles;
    private String address;
    private String public_key;


    public UserProfile(Long id, String username, String name, String email, Set roles , String address, String public_key){
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.roles = roles;
        this.address = address;
        this.public_key = public_key;
    }

}