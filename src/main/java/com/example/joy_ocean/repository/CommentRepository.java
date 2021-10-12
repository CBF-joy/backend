package com.example.joy_ocean.repository;

import java.util.Collection;

import com.example.joy_ocean.model.Comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query(value="Select * from comment c where c.ex_id = ?1", nativeQuery = true)
    Collection<Comment> getComment(Long id);
}