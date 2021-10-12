package com.example.joy_ocean.repository;

import com.example.joy_ocean.model.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {

    @Query(value="Select * from auction a where a.owner_username = ?1", nativeQuery = true)
    Collection<Auction> findbyUsername(String owner_username);

    @Query(value="Select * from auction a where a.ano = ?1", nativeQuery = true)
    Optional<Auction> findbyId(Long id);

    @Query(value="Select * from auction a where a.token_id = ?1 and a.status = 'ing'", nativeQuery = true)
    Optional<Auction> findByToken(String id);

    @Query(value="Select * from auction a where a.status = ?1", nativeQuery = true)
    Collection<Auction> findByStatus(String status);


    @Query(value="Select * from auction a where a.token_id = ?1 and a.status = 'ing'", nativeQuery = true)
    Optional<Auction> findIngByToken(String id);


}
