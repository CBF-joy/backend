package com.example.joy_ocean.repository;

import com.example.joy_ocean.model.Exhibition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Collection;

@Repository
public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {

    @Query(value="Select * from exhibition e where e.user_no = ?1", nativeQuery = true)
    Collection<Exhibition> findbyUser(Long id);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value="delete from exhibition e where e.eno = ?1",nativeQuery = true)
    void deleteByEno(Long id);


    @Query(value="Select * from exhibition e where e.status = ?1", nativeQuery = true)
    Collection<Exhibition> findbyStatus(String status);


    @Query(value="Select * from exhibition e ORDER BY klay desc ", nativeQuery = true)
    Collection<Exhibition> findRankingbyKlay();


}
