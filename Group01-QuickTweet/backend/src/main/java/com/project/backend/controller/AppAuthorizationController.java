package com.project.backend.controller;

import java.util.Set;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import com.project.backend.model.AppUser;
import com.project.backend.service.implementation.AppAuthorizationServiceImpl;

@RestController
public class AppAuthorizationController {
  private final AppAuthorizationServiceImpl appAuthorizationService;

  public AppAuthorizationController(AppAuthorizationServiceImpl appAuthorizationService) {
    this.appAuthorizationService = appAuthorizationService;
  }

  @GetMapping("/admin/requests/{username}")
  public Set<AppUser> getPendingRequests(@PathVariable("username") String username) {
    return appAuthorizationService.getPendingRequests(username);
  }

  @PutMapping("/admin/requests/{username}")
  public ResponseEntity<String> approvePendingRequest(@PathVariable("username") String username) {
    return appAuthorizationService.approvePendingRequest(username);
  }

  @DeleteMapping("/admin/requests/{username}")
  public ResponseEntity<String> rejectPendingRequest(@PathVariable("username") String username) {
    return appAuthorizationService.rejectPendingRequest(username);
  }

}

