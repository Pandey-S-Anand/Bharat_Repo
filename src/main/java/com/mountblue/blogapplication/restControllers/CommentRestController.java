package com.mountblue.blogapplication.restControllers;

import com.mountblue.blogapplication.entities.Comment;
import com.mountblue.blogapplication.entities.Post;
import com.mountblue.blogapplication.entities.User;
import com.mountblue.blogapplication.repository.UserRepository;
import com.mountblue.blogapplication.service.CommentService;
import com.mountblue.blogapplication.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/comments")
public class CommentRestController {

    private final CommentService commentService;
    private final PostService postService;
    private final UserRepository userRepository;

    @Autowired
    public CommentRestController(CommentService commentService, PostService postService, UserRepository userRepository) {
        this.commentService = commentService;
        this.postService = postService;
        this.userRepository = userRepository;
    }

    @PostMapping("/add/{postId}")
    public ResponseEntity<String> addNewComment(@PathVariable Long postId, @RequestBody Comment newComment) {
        Post targetPost = postService.getPostById(postId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());
        if (user != null) {
            if (user.getRole().equals("author")) {
                newComment.setName(user.getName());
            }
        }
        newComment.setCreatedAt(LocalDateTime.now());
        targetPost.getComments().add(newComment);
        postService.updatePost(targetPost);
        commentService.addComment(newComment);

        return ResponseEntity.ok("Comment added successfully");
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId, @RequestParam Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());
        Post post = postService.getPostById(postId);
//        if (user.getRole().equals("author")) {
//            if (!user.getPosts().contains(post)) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
//            }
//        }

        commentService.deleteComment(commentId);
        return ResponseEntity.ok("Comment deleted successfully");
    }

    @PostMapping("/edit/{commentId}")
    public ResponseEntity<Comment> editComment(@PathVariable Long commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());
        Comment comment = commentService.getCommentById(commentId);

        // Check if the user has the right to edit the comment
//        if (user.getRole().equals("author")) {
//            Post post = postService.getPostById(comment.getPostId());
//            if (!user.getPosts().contains(post)) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//            }
//        }

        return ResponseEntity.ok(comment);
    }

    @PutMapping("/update/{commentId}")
    public ResponseEntity<String> updateComment(@PathVariable Long commentId, @RequestBody Comment updatedComment) {
        commentService.editComment(commentId, updatedComment);
        return ResponseEntity.ok("Comment updated successfully");
    }
}

