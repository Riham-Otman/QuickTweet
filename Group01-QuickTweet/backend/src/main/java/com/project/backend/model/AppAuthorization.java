package com.project.backend.model;

import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;

@Entity
public class AppAuthorization {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, updatable = false)
  private Long id;

  @ElementCollection
  @JoinTable(name = "PENDING_REQUESTS", joinColumns = @JoinColumn(name = "APP_ID"), inverseJoinColumns = @JoinColumn(name = "USER_ID"))
  Set<AppUser> pendingRequests = new HashSet<>();

  public AppAuthorization() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Set<AppUser> getPendingRequests() {
    return pendingRequests;
  }

  public void setPendingRequests(Set<AppUser> pendingRequests) {
    this.pendingRequests = pendingRequests;
  }

  public void addPendingRequest(AppUser user) {
    this.pendingRequests.add(user);
  }

  public void deletePendingRequest(AppUser user) {
    this.pendingRequests.remove(user);
  }
}
