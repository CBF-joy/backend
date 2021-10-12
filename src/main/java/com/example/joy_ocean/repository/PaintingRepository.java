package com.example.joy_ocean.repository;

import com.example.joy_ocean.model.Painting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PaintingRepository extends JpaRepository<Painting, Long> {

    @Query(value="select * from painting where exhibition_no = ?1", nativeQuery = true)
    Collection<Painting> getPaintingsByExhibition(Long id);

    @Query(value="select * from painting p where p.username= ?1", nativeQuery = true)
    List<Painting> getByUsername(String username);

    @Query(value="select * from painting p where p.exhibition_no= ?1 and p.file_uri = ?2", nativeQuery = true)
    Collection<Painting> findByExhibitionAndFileUri(Long id, String file_uri);

}
