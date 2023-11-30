package com.mountblue.blogapplication.service;

import com.mountblue.blogapplication.entities.Post;
import com.mountblue.blogapplication.entities.Tag;
import com.mountblue.blogapplication.repository.PostRepository;
import com.mountblue.blogapplication.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class TagService {

    private final TagRepository tagRepository;
    private final PostRepository postRepository;

    @Autowired
    public TagService(TagRepository tagRepository, PostRepository postRepository) {
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
    }

    public void addTagsToPost(Long postId, String tagsInput) {
        Post post = postRepository.findById(postId).orElse(null);
        String[] tagNames = tagsInput.split(",");
        Set<Tag> postTags = post.getTags();

        for (String tagName : tagNames) {
            String trimmedTagName = tagName.trim();
            Tag tag;

            if (tagRepository.findByName(trimmedTagName) != null) {
                tag = tagRepository.findByName(trimmedTagName);
            } else {
                tag = createNewTag(trimmedTagName);
            }

            postTags.add(tag);
            tag.getPosts().add(post);
        }

        postRepository.save(post);
    }

    private Tag createNewTag(String trimmedTagName) {
        Tag newTag = new Tag();
        newTag.setName(trimmedTagName);
        newTag.setCreatedAt(LocalDateTime.now());
        newTag.setUpdatedAt(LocalDateTime.now());
        return tagRepository.save(newTag);
    }

    public Set<Tag> getTagsByPostId(Long postId) {
        return tagRepository.findByPostsId(postId);
    }
}
