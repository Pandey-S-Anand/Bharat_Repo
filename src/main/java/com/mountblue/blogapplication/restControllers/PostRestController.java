package com.mountblue.blogapplication.restControllers;

import com.mountblue.blogapplication.entities.Post;
import com.mountblue.blogapplication.entities.User;
import com.mountblue.blogapplication.repository.TagRepository;
import com.mountblue.blogapplication.repository.UserRepository;
import com.mountblue.blogapplication.service.CommentService;
import com.mountblue.blogapplication.service.PostService;
import com.mountblue.blogapplication.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/posts")
public class PostRestController {

    private final PostService postService;
    private final CommentService commentService;
    private final TagService tagService;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    @Autowired
    public PostRestController(PostService postService, CommentService commentService, TagService tagService,
                          TagRepository tagRepository, UserRepository userRepository) {
        this.postService = postService;
        this.commentService = commentService;
        this.tagService = tagService;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<Post> addPostToDatabase(@RequestBody Post newPost, @RequestParam("tag") String tagsInput) {


//        user.getPosts().add(newPost);
//        userRepository.save(user);
        newPost.setCreatedAt(LocalDateTime.now());
        newPost.setPublishedAt(LocalDateTime.now());
        postService.createPost(newPost);
        tagService.addTagsToPost(newPost.getId(), tagsInput);

        return ResponseEntity.ok(newPost);
    }


        @GetMapping("/all")
        public ResponseEntity<List<Post>> getAllPosts(
                @RequestParam(defaultValue = "0") Integer pageNo,
                @RequestParam(defaultValue = "createdAt") String sortField,
                @RequestParam(defaultValue = "asc") String sortOrder,
                @RequestParam(required = false) String searchTerm,
                @RequestParam(required = false) Set<String> authors,
                @RequestParam(required = false) Set<Long> tags) {

            int pageSize = 6;
            Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortField);
            Page<Post> postPage;

            if ((authors != null && !authors.isEmpty()) || (tags != null && !tags.isEmpty())) {
                postPage = postService.filterPosts(authors, tags, PageRequest.of(pageNo, pageSize, sort));
            } else if (searchTerm != null && !searchTerm.isEmpty()) {
                postPage = postService.searchPost(searchTerm, PageRequest.of(pageNo, pageSize, sort));
            } else {
                postPage = postService.getAllPosts(PageRequest.of(pageNo, pageSize, sort));
            }

            List<Post> posts = postPage.getContent();
            return ResponseEntity.ok(posts);
        }


    @GetMapping("/{postId}")
    public ResponseEntity<Post> getSinglePost(@PathVariable Long postId) {
        Post post = postService.getPostById(postId);
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        User user = userRepository.findByEmail(authentication.getName());
//        Post post = postService.getPostById(postId);
//
//        if (user.getRole().equals("author") && !user.getPosts().contains(post)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
//        }
//
//        user.getPosts().remove(post);
//        userRepository.save(user);
        postService.deletePost(postId);
        return ResponseEntity.ok("Post deleted successfully");
    }

    @PutMapping("/update/{postId}")
    public ResponseEntity<String> updatePost(@PathVariable Long postId, @RequestBody Post updatedPost,
                                             @RequestParam("tag") String updatedTags) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());
        Post existingPost = postService.getPostById(postId);

        //        if (user.getRole().equals("author") && !user.getPosts().contains(existingPost)) {
 //            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
 //        }

        updatedPost.setUpdatedAt(LocalDateTime.now());
        updatedPost.setAuthor(existingPost.getAuthor());
        updatedPost.setCreatedAt(existingPost.getCreatedAt());
        updatedPost.setId(postId);
        postService.updatePost(updatedPost);
        tagService.addTagsToPost(postId, updatedTags);
        return ResponseEntity.ok("Post updated successfully");
    }
}

