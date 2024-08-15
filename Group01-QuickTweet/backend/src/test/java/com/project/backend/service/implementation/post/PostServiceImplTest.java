package com.project.backend.service.implementation.post;

import static org.assertj.core.api.Assertions.assertThat;

import com.project.backend.model.AppUser;
import com.project.backend.model.Post;
import com.project.backend.repository.UserRepository;
import com.project.backend.service.implementation.PostServiceImplementation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class PostServiceImplTest {

  @Autowired
  private PostServiceImplementation postService;

  @Autowired
  private UserRepository userRepository;

  @Test
  void testCreatePost() {
    AppUser user = new AppUser("testUser", "testUser@dal.ca", "password", "USER", "Question?", "Answer");
    userRepository.save(user);

    Post post = new Post("Test Content");
    Post createdPost = postService.createPost(user.getUsername(), post);

    assertThat(createdPost).isNotNull();
    assertThat(createdPost.getContent()).isEqualTo("Test Content");
    assertThat(createdPost.getCreateDate()).isNotNull();
    assertThat(createdPost.getUser()).isEqualTo(user);
  }

  @Test
  void testCreatePostWithEmptyContent() {
    AppUser user = new AppUser("testUser", "testUser@dal.ca", "password", "USER", "Question?", "Answer");
    userRepository.save(user);

    Post post = new Post("");
    Post createdPost = postService.createPost(user.getUsername(), post);

    assertThat(createdPost).isNull();
  }

  @Test
  void testCreatePostWithNonexistentUser() {
    Post post = new Post("Test Content");
    Post createdPost = postService.createPost("nonexistentUser", post);

    assertThat(createdPost).isNull();
  }

  @Test
  void testGetAllPosts() {
    AppUser user = new AppUser("testUser", "testUser@dal.ca", "password", "USER", "Question?", "Answer");
    userRepository.save(user);

    Post post = new Post("Test Content");
    postService.createPost(user.getUsername(), post);

    List<Post> posts = postService.getAllPosts();
    assertThat(posts).isNotEmpty();
    assertThat(posts.size()).isEqualTo(1);
  }

  @Test
  void testGetAllPostsNoPosts() {
    assertThat(postService.getAllPosts()).isNull();
  }

  @Test
  void testGetPostByUsername() {
    AppUser user = new AppUser("testUser", "testUser@dal.ca", "password", "USER", "Question?", "Answer");
    userRepository.save(user);

    Post post = new Post("Test Content");
    postService.createPost(user.getUsername(), post);

    List<Post> foundPosts = postService.getPostByUsername(user.getUsername());
    assertThat(foundPosts).isNotEmpty();
    assertThat(foundPosts.get(0).getContent()).isEqualTo("Test Content");
  }

  @Test
  void testGetPostByUsernameWithNonexistentUser() {
    List<Post> foundPosts = postService.getPostByUsername("nonexistentUser");
    assertThat(foundPosts).isNull();
  }

  @Test
  void testUpdatePost() {
    AppUser user = new AppUser("testUser", "testUser@dal.ca", "password", "USER", "Question?", "Answer");
    userRepository.save(user);

    Post post = new Post("Original Content");
    postService.createPost(user.getUsername(), post);

    Post updatedPost = postService.updatePost("Updated Content", post.getId());

    assertThat(updatedPost).isNotNull();
    assertThat(updatedPost.getContent()).isEqualTo("Updated Content");
  }

  @Test
  void testUpdatePostNonExistent() {
    Post updatedPost = postService.updatePost("Updated Content", 999L);

    assertThat(updatedPost).isNull();
  }

  @Test
  void testDeletePost() {
    AppUser user = new AppUser("testUser", "testUser@dal.ca", "password", "USER", "Question?", "Answer");
    userRepository.save(user);

    Post post = new Post("Test Content");
    postService.createPost(user.getUsername(), post);

    String response = postService.deletePost(post.getId());
    assertThat(response).isEqualTo("Post deleted successfully");
  }

  @Test
  void testDeletePostWhenNotExist() {
    String response = postService.deletePost(999L);
    assertThat(response).isEqualTo("No such post exists");
  }
}
