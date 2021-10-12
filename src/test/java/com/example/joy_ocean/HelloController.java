package com.example.joy_ocean;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController{
    @GetMapping("/api/hello")
    public String hello(){
        return "Hello, the time at the server is now";
    }
}