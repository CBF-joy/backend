package com.example.joy_ocean.repository;


import com.example.joy_ocean.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query(value="Select * from post p where p.user_address = ?1", nativeQuery = true)
    Collection<Post> findbyUser(String address);

    @Query(value="Select SUM(klay) from post p where p.exhibit_no = ?1", nativeQuery = true)
    Double getSumKlayByExhibition(Long id);


    @Query(value="Select exhibit_no, SUM(klay) from post GROUP BY exhibit_no ORDER BY SUM(klay) desc LIMIT 100", nativeQuery = true)
    Map<Long,Double> getSumMaxKlay();

}
