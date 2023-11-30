package com.mountblue.blogapplication.repository;

import com.mountblue.blogapplication.entities.Comment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT p.comments FROM Post p where p.id=(:id)")
    List<Comment> findCommentsById(Long id);

    @Transactional
    void deleteById(Long id);
}