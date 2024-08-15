package com.project.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.backend.model.Post;
import com.project.backend.service.PostService;

@RestController
public class PostController {
  private final PostService postService;

  public PostController(PostService postService) {
    this.postService = postService;
  }

  /**
   * This method is used to create a post.
   * 
   * @param username - The username of the user
   * @param post - The post to be created
   * @return - The created post
   */
  @PostMapping("/posts/{username}")
  public Post createPost(@PathVariable("username") String username, @RequestBody Post post) {
    return postService.createPost(username, post);
  }

  /**
   * This method is used to get all posts.
   * 
   * @return - A list of all posts
   */
  @GetMapping("/posts")
  public List<Post> getAllPosts() {
    return postService.getAllPosts();
  }

  /**
   * This method is used to get a post by its ID.
   * 
   * @param username - The username of the user
   * @return - A list of posts by the user
   */
  @GetMapping("/posts/{username}")
  public List<Post> getPostByID(@PathVariable("username") String username) {
    return postService.getPostByUsername(username);
  }

  /**
   * This method is used to update a post.
   * 
   * @param newContent - The new content of the post
   * @param postID - The ID of the post
   * @return - The updated post
   */
  @PutMapping("posts/{id}")
  public Post updatePost(@RequestParam String newContent, @PathVariable("id") Long postID) {
    return postService.updatePost(newContent, postID);
  }

  /**
   * This method is used to delete a post.
   * 
   * @param postID - The ID of the post
   * @return - A message indicating the success of the deletion
   */
  @DeleteMapping("posts/{id}")
  public String deletePost(@PathVariable("id") Long postID) {
    return postService.deletePost(postID);
  }
}
