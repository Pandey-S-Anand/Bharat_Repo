package com.mountblue.blogapplication.service;

import com.mountblue.blogapplication.entities.Post;
import com.mountblue.blogapplication.repository.PostRepository;
import com.mountblue.blogapplication.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.Set;

@Service
public class PostService {

    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Page<Post> getAllPosts(PageRequest pageRequest) {
        return postRepository.findAll(pageRequest);
    }

    public Post getPostById(Long postId) {
        return postRepository.findById(postId).orElse(null);
    }

    public void updatePost(Post updatedPost) {

        postRepository.save(updatedPost);
    }

    public void createPost(Post newPost) {
        postRepository.save(newPost);
    }

    public void deletePost(Long postId) {

        postRepository.deleteById(postId);
    }

    public Set<String> getAllAuthors() {
        return postRepository.findAllAuthors();
    }

    public Page<Post> filterPosts(Set<String> authors, Set<Long> tags, Pageable pageable) {
        if (authors != null && !authors.isEmpty() && tags != null && !tags.isEmpty()) {
            // Both authors and tags are selected
            return postRepository.findByAuthorInAndTagsIdIn(authors, tags, pageable);
        } else if (authors != null && !authors.isEmpty()) {
            // Only authors are selected
            return postRepository.findByAuthorIn(authors, pageable);
        } else if (tags != null && !tags.isEmpty()) {
            // Only tags are selected
            return postRepository.findByTagsIdIn(tags, pageable);
        } else {
            // No filter selected, return all posts
            return postRepository.findAll(pageable);
        }
    }


    public Page<Post> searchPost(String searchTerm, PageRequest pageRequest) {
        return postRepository.findPostsByKeywords(searchTerm, pageRequest);
    }
}
