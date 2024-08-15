package com.project.backend.service;

import com.project.backend.model.Post;

import java.util.List;

public interface PostService {
    Post createPost(String username, Post post);

    List<Post> getAllPosts();

    List<Post> getPostByUsername(String username);

    Post updatePost(String newContent, Long postID);

    String deletePost(Long postID);

}
