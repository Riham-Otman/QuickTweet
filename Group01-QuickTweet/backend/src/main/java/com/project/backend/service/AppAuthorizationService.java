package com.project.backend.service;

import java.util.Set;
import org.springframework.http.ResponseEntity;
import com.project.backend.model.AppUser;
import jakarta.persistence.EntityNotFoundException;

public interface AppAuthorizationService {
  public Set<AppUser> getPendingRequests(String username) throws EntityNotFoundException;

  public ResponseEntity<String> approvePendingRequest(String username)
      throws EntityNotFoundException;

  public ResponseEntity<String> rejectPendingRequest(String username)
      throws EntityNotFoundException;
}
