package com.mountblue.blogapplication.service;

import com.mountblue.blogapplication.entities.Comment;
import com.mountblue.blogapplication.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findCommentsById(postId);
    }

    public Comment addComment(Comment newComment) {
        return commentRepository.save(newComment);
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElse(null);
    }

    public void editComment(Long commentId, Comment updatedComment) {
        Comment existingComment=commentRepository.findById(commentId).orElse(null);
        if(existingComment !=null){
            existingComment.setComment(updatedComment.getComment());
            existingComment.setUpdatedAt(LocalDateTime.now());
            commentRepository.save(existingComment);
        }
    }
}