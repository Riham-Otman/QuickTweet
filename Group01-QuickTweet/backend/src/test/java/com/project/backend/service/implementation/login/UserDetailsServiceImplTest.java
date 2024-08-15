package com.project.backend.service.implementation.login;

import org.junit.jupiter.api.Test;
import com.project.backend.config.UserDetailsServiceImpl;
import com.project.backend.model.AppUser;

import com.project.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserDetailsServiceImplTest {

  @InjectMocks
  UserDetailsServiceImpl userDetailsService;

  @Mock
  UserRepository userRepository;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testLoadUserByUsername() {
    AppUser user = new AppUser("root", "root@dal.ca", "password", "admin",
        "What is the name of your first pet?", "Leo");

    when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

    assertNotNull(userDetails);
    assertEquals(user.getUsername(), userDetails.getUsername());
    assertEquals(user.getPassword(), userDetails.getPassword());
  }

  @Test
  public void testLoadUserByUsernameNotFound() {
    String username = "nonexistent";
    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

    assertThrows(UsernameNotFoundException.class,
        () -> userDetailsService.loadUserByUsername(username));
  }
}
