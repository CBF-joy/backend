package com.example.joy_ocean.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.example.joy_ocean.model.audit.DateAudit;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="rate")
public class Rate extends DateAudit{
    @Id
    @Column(name="rno")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno;

    @Column(name="username")
    private String username;

    @Column(name="rate")
    private Integer rate;
    

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="exhibit_id")
    private Exhibition exhibition;

    public Rate(){

    }

    public Rate(String username, Integer rate){
        this.username= username;
        this.rate = rate;
    }
}