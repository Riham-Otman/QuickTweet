package com.project.backend.controller.post;

import com.project.backend.controller.PostController;
import com.project.backend.model.Post;
import com.project.backend.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PostControllerTest {

  private MockMvc mockMvc;

  @Mock
  private PostService postService;

  @InjectMocks
  private PostController postController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    this.mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
  }

  @Test
  void testCreatePost() throws Exception {
    Post post = new Post("This is a post");
    post.setId(1L);
    post.setCreateDate(new Timestamp(System.currentTimeMillis()));

    when(postService.createPost(anyString(), any(Post.class))).thenReturn(post);

    mockMvc
        .perform(post("/posts/testuser").contentType(MediaType.APPLICATION_JSON)
            .content("{\"content\":\"This is a post\"}"))
        .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.content").value("This is a post"));
  }

  @Test
  void testGetAllPosts() throws Exception {
    Post post1 = new Post("First post");
    Post post2 = new Post("Second post");
    List<Post> posts = Arrays.asList(post1, post2);

    when(postService.getAllPosts()).thenReturn(posts);

    mockMvc.perform(get("/posts")).andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].content").value("First post"))
        .andExpect(jsonPath("$[1].content").value("Second post"));
  }

  @Test
  void testGetAllPostsWithNoPostsCreated() throws Exception {
    when(postService.getAllPosts()).thenReturn(null);
    mockMvc.perform(get("/posts")).andExpect(content().string(""));
  }

  @Test
  void testGetPostByID() throws Exception {
    Post post = new Post("A specific post");

    when(postService.getPostByUsername(anyString())).thenReturn(List.of(post));

    mockMvc.perform(get("/posts/testuser")).andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].content").value("A specific post"));
  }

  @Test
  void testGetPostByInvalidID() throws Exception {
    when(postService.getPostByUsername("testuser")).thenReturn(null);

    mockMvc.perform(get("/posts/testuser")).andExpect(content().string(""));
  }

  @Test
  void testUpdatePost() throws Exception {
    Post post = new Post("Updated content");
    post.setId(1L);

    when(postService.updatePost(anyString(), anyLong())).thenReturn(post);

    mockMvc.perform(put("/posts/1").param("newContent", "Updated content"))
        .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.content").value("Updated content"));
  }

  @Test
  void testUpdatePostWithInvalidPostID() throws Exception {
    when(postService.updatePost(anyString(), anyLong())).thenReturn(null);

    mockMvc.perform(put("/posts/1").param("newContent", "")).andExpect(content().string(""));
  }

  @Test
  void testDeletePost() throws Exception {
    when(postService.deletePost(anyLong())).thenReturn("Post deleted");

    mockMvc.perform(delete("/posts/1")).andExpect(status().isOk())
        .andExpect(content().string("Post deleted"));
  }

  @Test
  void testDeletePostWithInvalidID() throws Exception {
    when(postService.deletePost(0L)).thenReturn("No such post exists");

    mockMvc.perform(delete("/posts/0")).andExpect(content().string("No such post exists"));
  }
}
