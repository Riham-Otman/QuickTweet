package com.project.backend.service.implementation.login;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import com.project.backend.filter.JwtService;

public class JwtServiceTest {


  @InjectMocks
  JwtService jwtService;

  @Mock
  HttpServletRequest request;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testGetToken() {
    String username = "testUser";
    String token = jwtService.getToken(username);

    assertNotNull(token);
  }

  @Test
  public void testGetAuthUser() {
    String username = "testUser";
    String token = jwtService.getToken(username);

    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(JwtService.PREFIX + " " + token);

    String authUser = jwtService.getAuthUser(request);

    assertEquals(username, authUser);
  }
}
