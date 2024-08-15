package com.project.backend.controller.app;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.backend.controller.AppAuthorizationController;
import com.project.backend.model.AppUser;
import com.project.backend.service.implementation.AppAuthorizationServiceImpl;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class AppAuthorizationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  private static ObjectMapper objectMapper;

  @MockBean
  private AppAuthorizationServiceImpl appAuthorizationService;

  @InjectMocks
  private AppAuthorizationController appAuthorizationController;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
  }

  @Test
  @WithMockUser
  void testGetPendingRequests() throws Exception {
    Set<AppUser> pendingRequests = new HashSet<>();
    AppUser user = new AppUser("user", "user@dal.ca", "password", "USER",
        "What is the name of your first pet?", "Leo");
    pendingRequests.add(user);

    when(appAuthorizationService.getPendingRequests("root")).thenReturn(pendingRequests);

    mockMvc.perform(get("/admin/requests/root").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(objectMapper.writeValueAsString(pendingRequests)));
  }

  @Test
  @WithMockUser
  void testGetPendingRequestsNoRequests() throws Exception {
    Set<AppUser> pendingRequests = new HashSet<>();

    when(appAuthorizationService.getPendingRequests("root")).thenReturn(pendingRequests);

    mockMvc.perform(get("/admin/requests/root").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(objectMapper.writeValueAsString(pendingRequests)));
  }

  @Test
  @WithMockUser
  void testApprovePendingRequestSuccess() throws Exception {
    String responseMessage = "User request has been approved.";

    when(appAuthorizationService.approvePendingRequest(anyString()))
        .thenReturn(ResponseEntity.ok().body(responseMessage));

    mockMvc.perform(put("/admin/requests/user").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(content().string(responseMessage));
  }

  @Test
  @WithMockUser
  void testApprovePendingRequestFailure() throws Exception {
    String responseMessage = "User does not exist.";

    when(appAuthorizationService.approvePendingRequest(anyString()))
        .thenReturn(ResponseEntity.badRequest().body(responseMessage));

    mockMvc.perform(put("/admin/requests/user").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest()).andExpect(content().string(responseMessage));
  }

  @Test
  @WithMockUser
  public void testRejectPendingRequestSuccess() throws Exception {
    String responseMessage = "User request has been rejected";

    when(appAuthorizationService.rejectPendingRequest(anyString()))
        .thenReturn(ResponseEntity.ok().body(responseMessage));

    mockMvc.perform(delete("/admin/requests/username")).andExpect(status().isOk())
        .andExpect(content().string(responseMessage));
  }

  @Test
  @WithMockUser
  public void testRejectPendingRequestFailure() throws Exception {
    String responseMessage = "User does not exist.";

    when(appAuthorizationService.rejectPendingRequest(anyString()))
        .thenReturn(ResponseEntity.badRequest().body(responseMessage));

    mockMvc.perform(delete("/admin/requests/username")).andExpect(status().isBadRequest())
        .andExpect(content().string(responseMessage));
  }

}
