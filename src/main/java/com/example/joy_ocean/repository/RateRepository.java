package com.example.joy_ocean.repository;

import java.util.Optional;

import com.example.joy_ocean.model.Rate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RateRepository extends JpaRepository<Rate, Long>{
    @Query(value= "Select * from rate r where r.exhibit_id = ?1 and r.username = ?2", nativeQuery = true)
    Optional<Rate> getRateByEx_Id(Long id, String user);

    @Query(value="Select avg(rate) from rate where exhibit_id = ?1", nativeQuery = true)
    Double getAvgRate(Long id);
}