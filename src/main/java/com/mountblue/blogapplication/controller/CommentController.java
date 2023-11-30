package com.mountblue.blogapplication.controller;

import com.mountblue.blogapplication.entities.Comment;
import com.mountblue.blogapplication.entities.Post;
import com.mountblue.blogapplication.entities.User;
import com.mountblue.blogapplication.repository.UserRepository;
import com.mountblue.blogapplication.service.CommentService;
import com.mountblue.blogapplication.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;
    private final UserRepository userRepository;

    @Autowired
    public CommentController(CommentService commentService, PostService postService, UserRepository userRepository)
    {
        this.commentService = commentService;
        this.postService = postService;
        this.userRepository = userRepository;
    }

    @PostMapping("/comment/{postId}")
    public String addNewComment(@PathVariable Long postId, @ModelAttribute("newComment") Comment newComment) {
        Post targetPost = postService.getPostById(postId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());
        if(user!=null) {
            if (user.getRole().equals("author")) {
                newComment.setName(user.getName());
            }
        }
        newComment.setCreatedAt(LocalDateTime.now());
        targetPost.getComments().add(newComment);
        postService.updatePost(targetPost);
        commentService.addComment(newComment);

        return "redirect:/posts/" + postId;
    }

    @PostMapping("/deleteComment/{commentId}")
    public String deleteComment(@PathVariable Long commentId, @RequestParam Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());
        Post post = postService.getPostById(postId);
            if (user.getRole().equals("author")) {
                if (!user.getPosts().contains(post)) {
                    return "access-denied";
                }
            }

        commentService.deleteComment(commentId);
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/editComment")

    public String editComment(@RequestParam("commentId") Long commentId,
                              @RequestParam("postId") Long postId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());
        Post post = postService.getPostById(postId);
        if (user.getRole().equals("author")) {
            if (!user.getPosts().contains(post)) {
                return "access-denied";
            }
        }
        Comment comment = commentService.getCommentById(commentId);
        model.addAttribute("postId",postId);
        model.addAttribute("updateComment", comment);
        return "editComment";
    }

    @PostMapping("/updateComment")
    public String updateComment(@RequestParam("commentId") Long commentId,@RequestParam("postId")
    Long postId , @ModelAttribute("updateComment") Comment updatedComment) {
        commentService.editComment(commentId, updatedComment);
        return "redirect:/posts/" + postId ;
    }
}