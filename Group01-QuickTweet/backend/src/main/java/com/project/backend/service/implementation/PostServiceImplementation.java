package com.project.backend.service.implementation;

import com.project.backend.model.AppUser;
import com.project.backend.model.Post;
import com.project.backend.repository.PostRepository;
import com.project.backend.repository.UserRepository;
import com.project.backend.service.PostService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImplementation implements PostService {
  private final PostRepository postRepository;
  private final UserRepository userRepository;

  public PostServiceImplementation(PostRepository postRepository, UserRepository userRepository) {
    this.postRepository = postRepository;
    this.userRepository = userRepository;
  }

  @Override
  public Post createPost(String username, Post post) {
    if (post == null || post.getContent().isEmpty()) {
      return null;
    }

    Optional<AppUser> optUser = userRepository.findByUsername(username);
    if (!optUser.isPresent()) {
      return null;
    }

    long currentTimeMillis = System.currentTimeMillis();
    post.setCreateDate(new Timestamp(currentTimeMillis));
    post.setUser(optUser.get());
    postRepository.save(post);
    return post;
  }

  @Override
  public List<Post> getAllPosts() {
    if (postRepository.findAll().isEmpty()) {
      return null;
    } else {
      return postRepository.findAll();
    }
  }

  @Override
  public List<Post> getPostByUsername(String username) {
    if (username.isEmpty()) {
      return null;
    }

    Optional<AppUser> optUser = userRepository.findByUsername(username);
    if (!optUser.isPresent()) {
      return null;
    }

    List<String> friendUsernames =
        optUser.get().getFriends().stream().map(user -> user.getUsername()).toList();
    List<String> combinedUsernames = new ArrayList<>();
    combinedUsernames.add(username);
    combinedUsernames.addAll(friendUsernames);
    return postRepository.findByUserUsernameInOrderByCreateDateDesc(combinedUsernames);
  }

  @Override
  public Post updatePost(String newContent, Long postID) {
    Optional<Post> tempPost = postRepository.findById(postID);
    if (tempPost.isEmpty()) {
      return null;
    }
    Post post = tempPost.get();
    post.setContent(newContent);
    postRepository.save(post);
    return post;
  }

  @Override
  public String deletePost(Long postID) {
    if (postRepository.findById(postID).isEmpty()) {
      return "No such post exists";
    }
    postRepository.deleteById(postID);
    return "Post deleted successfully";
  }
}
