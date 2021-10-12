package com.example.joy_ocean.repository;

import com.example.joy_ocean.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {

    @Query(value="Select * from bid b where b.auction_no = ?1 and b.user_address = ?2", nativeQuery = true)
    Collection<Bid> findSameBid(Long id, String address);

    @Query(value="Select avg(price) from bid b where b.auction_no = ?1", nativeQuery = true)
    Double getAvgBidByAuction(Long id);

    @Query(value="Select COUNT(*) from bid b where b.auction_no = ?1", nativeQuery = true)
    Long getCountBidByAuction(Long id);

    @Query(value="Select * from bid where price = (Select  MAX(price) from bid b where b.auction_no = ?1) LIMIT 1", nativeQuery = true)
    Optional<Bid> getMaxBidByAuction(Long id);


    @Query(value="Select * from bid b where b.user_address = ?1", nativeQuery = true)
    List<Bid> findByUser(String address);


    @Query(value="Select * from bid b where b.user_address = ?1 and b.auction_no = ?2", nativeQuery = true)
    Optional<Bid> findByUserandAuction(String address, Long id);

    @Query(value="Select * from bid b where b.auction_no = ?1 and b.user_address = ?2", nativeQuery = true)
    Optional<Bid> findByUserandAuction(Long id, String address);

}
