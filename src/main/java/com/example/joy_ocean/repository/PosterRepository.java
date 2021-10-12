package com.example.joy_ocean.repository;

import com.example.joy_ocean.model.Poster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface PosterRepository extends JpaRepository<Poster, Long> {

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value="Delete from poster where posno = ?1", nativeQuery = true)
    void deletePoster(Long id);

    @Query(value="Select * from poster p where p.ex_no = ?1", nativeQuery = true)
    Optional<Poster> getPosterByExhibition(Long id);

    @Query(value="Select ex_no, file_uri from poster", nativeQuery=true)
    Optional<Poster> getFileUrl();

}
