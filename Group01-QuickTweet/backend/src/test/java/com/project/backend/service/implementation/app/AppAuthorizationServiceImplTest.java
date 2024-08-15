package com.project.backend.service.implementation.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import com.project.backend.model.AppAuthorization;
import com.project.backend.model.AppUser;
import com.project.backend.repository.AppAuthorizationRepository;
import com.project.backend.repository.UserRepository;
import com.project.backend.service.implementation.AppAuthorizationServiceImpl;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class AppAuthorizationServiceImplTest {
  @Autowired
  AppAuthorizationRepository appAuthorizationRepository;

  @Autowired
  AppAuthorizationServiceImpl service;

  @Autowired
  UserRepository userRepository;

  @Test
  void testGetPendingRequests() {
    AppUser admin =
        new AppUser("admin", "admin@dal.ca", "password", "ADMIN", "Question?", "Answer");

    AppUser user1 =
        new AppUser("testUser", "testUser@dal.ca", "password", "USER", "Question?", "Answer");

    AppUser user2 =
        new AppUser("testUser2", "testUser2@dal.ca", "password", "USER", "Question?", "Answer");

    admin.setPendingRequest(false);
    admin.setRole("ADMIN");
    userRepository.save(admin);
    userRepository.save(user1);
    userRepository.save(user2);
    AppAuthorization app = new AppAuthorization();
    app.addPendingRequest(user1);
    app.addPendingRequest(user2);
    app.setId(1L);
    appAuthorizationRepository.save(app);
    assertAll(() -> assertThat(service.getPendingRequests(admin.getUsername())).isNotNull(),
        () -> assertThat(app.getId()).isEqualTo(1L),
        () -> assertThat(service.getPendingRequests(admin.getUsername()).size()).isEqualTo(2),
        () -> assertThat(service.getPendingRequests(admin.getUsername()).contains(user1)),
        () -> assertThat(service.getPendingRequests(admin.getUsername()).contains(user2)));
  }

  @Test
  void testApprovePendingRequest() {
    AppUser user =
        new AppUser("testUser", "testUser@dal.ca", "password", "USER", "Question?", "Answer");

    userRepository.save(user);
    AppAuthorization app = new AppAuthorization();
    app.setId(1L);
    appAuthorizationRepository.save(app);
    assertThat(service.approvePendingRequest("testUser"))
        .isEqualTo(ResponseEntity.ok("User request has been approved."))
        .withFailMessage("Expected user request to be approved");
  }

  @Test
  void testApprovePendingRequestWithInvalidUser() {
    AppAuthorization app = new AppAuthorization();
    app.setId(1L);
    appAuthorizationRepository.save(app);
    assertThat(service.approvePendingRequest("testUser"))
        .isEqualTo(ResponseEntity.badRequest().body("User does not exist."))
        .withFailMessage("Expected approval to fail");
  }

  @Test
  void testRejectPendingRequest() {
    AppUser user =
        new AppUser("testUser", "testUser@dal.ca", "password", "USER", "Question?", "Answer");

    userRepository.save(user);
    AppAuthorization app = new AppAuthorization();
    app.setId(1L);
    appAuthorizationRepository.save(app);
    assertThat(service.rejectPendingRequest("testUser"))
        .isEqualTo(ResponseEntity.ok("User request has been rejected"))
        .withFailMessage("Expected user request to be approved");
  }

  @Test
  void testRejectPendingRequestWithInvalidUser() {
    AppAuthorization app = new AppAuthorization();
    app.setId(1L);
    appAuthorizationRepository.save(app);
    assertThat(service.rejectPendingRequest("testUser"))
        .isEqualTo(ResponseEntity.badRequest().body("User does not exist."))
        .withFailMessage("Expected approval to fail");
  }

}
