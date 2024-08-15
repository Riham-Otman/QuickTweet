package com.project.backend.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.project.backend.filter.JwtService;
import com.project.backend.model.AccountCredentials;
import com.project.backend.model.AppUser;
import com.project.backend.repository.UserRepository;

@RestController
public class LoginController {
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;

  public LoginController(JwtService jwtService, AuthenticationManager authenticationManager,
      UserRepository userRepository) {
    this.jwtService = jwtService;
    this.authenticationManager = authenticationManager;
    this.userRepository = userRepository;
  }

  /**
   * This method is used to authenticate the user and return a JWT token.
   * 
   * @param credentials - The user credentials
   * @return - JWT token
   * @throws Exception - If the user does not exist
   */
  @PostMapping("/login")
  public ResponseEntity<?> getToken(@RequestBody AccountCredentials credentials) throws Exception {
    AppUser user = userRepository.findByUsername(credentials.username())
        .orElseThrow(() -> new UsernameNotFoundException("User does not exist"));

    if (user.isPendingRequest()) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body("User account creation request is pending.");
    }

    UsernamePasswordAuthenticationToken creds =
        new UsernamePasswordAuthenticationToken(credentials.username(), credentials.password());
    Authentication auth = authenticationManager.authenticate(creds);
    String jwts = jwtService.getToken(auth.getName());
    return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + jwts)
        .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization").build();
  }
}
