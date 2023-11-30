package com.mountblue.blogapplication.repository;

import com.mountblue.blogapplication.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN p.tags t WHERE p.author " +
            "LIKE %:keyword% OR p.content LIKE %:keyword% OR t.name LIKE %:keyword%")
    Page<Post> findPostsByKeywords(String keyword, Pageable pageable);

    Page<Post> findByAuthorInAndTagsIdIn(Set<String> authorNames, Set<Long> tagIds, Pageable pageable);

    Page<Post> findByAuthorIn(Set<String> authors, Pageable pageable);

    Page<Post> findByTagsIdIn(Set<Long> tags, Pageable pageable);

    @Query("SELECT DISTINCT p.author FROM Post p")
    Set<String> findAllAuthors();


}
