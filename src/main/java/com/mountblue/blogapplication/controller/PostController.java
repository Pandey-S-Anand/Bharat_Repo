package com.mountblue.blogapplication.controller;

import com.mountblue.blogapplication.entities.Comment;
import com.mountblue.blogapplication.entities.Post;
import com.mountblue.blogapplication.entities.Tag;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final TagService tagService;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    @Autowired
    public PostController(PostService postService, CommentService commentService, TagService tagService,
                          TagRepository tagRepository, UserRepository userRepository) {
        this.postService = postService;
        this.commentService = commentService;
        this.tagService = tagService;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
    }
    @PostMapping("/goToHomepage")
    public String redirectToHome(){
        return "redirect:/";
    }

    @GetMapping("/newPost")
    public String createPostForm(Model model) {
        model.addAttribute("post", new Post());
        return "createPost";
    }

    @PostMapping("/create")
    public String addPostToDataBase(@ModelAttribute("post") Post newPost, @RequestParam("tag") String tagsInput) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());

        if(user.getRole().equals("author")){
            newPost.setAuthor(user.getName());

        }
        user.getPosts().add(newPost);
        userRepository.save(user);
        newPost.setCreatedAt(LocalDateTime.now());
        newPost.setPublishedAt(LocalDateTime.now());
        postService.createPost(newPost);
        tagService.addTagsToPost(newPost.getId(), tagsInput);
        return "redirect:/";
    }

    @GetMapping("/")
    public String showFrontPage(@RequestParam(defaultValue = "0") Integer pageNo,
                                @RequestParam(defaultValue = "createdAt") String sortField,
                                @RequestParam(defaultValue = "asc") String sortOrder,
                                @RequestParam(required = false) String searchTerm,
                                @RequestParam(required = false) Set<String> authors,
                                @RequestParam(required = false) Set<Long> tags,
                                Model model) {
        int pageSize = 6;
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortField);
        Page<Post> postPage;

        if ((authors != null && !authors.isEmpty()) || (tags != null && !tags.isEmpty())) {
            // Filter logic
            postPage = postService.filterPosts(authors, tags, PageRequest.of(pageNo, pageSize, sort));
        } else if (searchTerm != null && !searchTerm.isEmpty()) {
            // Search logic
            postPage = postService.searchPost(searchTerm, PageRequest.of(pageNo, pageSize, sort));
        } else {
            // Display all posts
            postPage = postService.getAllPosts(PageRequest.of(pageNo, pageSize, sort));
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user=userRepository.findByEmail(authentication.getName());

        model.addAttribute("user",user);
        // Common model attributes for pagination and posts
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", postPage.getTotalPages());

        // Other model attributes
        model.addAttribute("authors", postService.getAllAuthors());
        model.addAttribute("tags", tagRepository.findAll());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortOrder", sortOrder);

        return "homepage";
    }



    @GetMapping("/posts/{postId}")
    public String showSinglePost(@PathVariable Long postId, Model model) {
        List<Comment> commentsList = commentService.getCommentsByPostId(postId);
        model.addAttribute("commentsList", commentsList);
        Post post = postService.getPostById(postId);
        model.addAttribute("post", post);
        Set<Tag> tags = post.getTags();
        model.addAttribute("tags", tags);
        return "post";
    }

    @PostMapping("/delete/{postId}")
    public String deletePost(@PathVariable Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());
        Post post = postService.getPostById(postId);
        if (user.getRole().equals("author")) {
            if (!user.getPosts().contains(post)) {
                return "access-denied";
            }
        }
        user.getPosts().remove(post);
        userRepository.save(user);
        postService.deletePost(postId);
        return "redirect:/";
    }

    @GetMapping("/update/{postId}")
    public String showUpdateForm(@PathVariable Long postId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());
        Post post = postService.getPostById(postId);
        if (user.getRole().equals("author")) {
            if (!user.getPosts().contains(post)) {
                return "access-denied";
            }
        }
        Post existingPost = postService.getPostById(postId);
        Set<Tag> existingTags = tagService.getTagsByPostId(postId);
        ArrayList<String> listOfExistingTags = new ArrayList<>();
        for (Tag tag : existingTags) {
            listOfExistingTags.add(tag.getName());
        }
        String commaSeparatedTags = String.join(",", listOfExistingTags);
        model.addAttribute("tags", commaSeparatedTags);
        model.addAttribute("post", existingPost);
        return "updatePost";
    }

    @PostMapping("/update/{postId}")
    public String updatePost(@PathVariable Long postId, @ModelAttribute Post updatedPost,
                             @RequestParam("tag") String updatedTags) {
        updatedPost.setUpdatedAt(LocalDateTime.now());
        Post existingPost = postService.getPostById(postId);
        updatedPost.setAuthor(existingPost.getAuthor());
        updatedPost.setCreatedAt(existingPost.getCreatedAt());
        updatedPost.setId(postId);
        postService.updatePost(updatedPost);
        tagService.addTagsToPost(postId, updatedTags);
        return "redirect:/posts/" + postId;
    }
}
