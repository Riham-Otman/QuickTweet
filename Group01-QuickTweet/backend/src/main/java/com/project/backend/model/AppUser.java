package com.project.backend.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity
public class AppUser {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, updatable = false)
  private Long id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String role;

  @Column(nullable = false)
  private String securityQuestion;

  @Column(nullable = false)
  private String securityQuestionAnswer;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,
      cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
  @JsonIgnore
  private List<Post> posts;

  @ManyToMany(cascade = CascadeType.PERSIST)
  @JsonIgnore
  @JoinTable(name = "FRIENDS",
      joinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "id", nullable = false),
      inverseJoinColumns = @JoinColumn(name = "FRIEND_ID", referencedColumnName = "id",
          nullable = false))
  private Set<AppUser> friends = new HashSet<>();

  @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
  @JoinTable(name = "FRIEND_REQUESTS",
      joinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "id", nullable = false),
      inverseJoinColumns = @JoinColumn(name = "FRIEND_ID", referencedColumnName = "id",
          nullable = false))
  private Set<AppUser> friendRequests = new HashSet<>();

  private String bio;

  private String photo;

  private String status;

  @ElementCollection
  private Set<String> interests;

  private boolean pendingRequest;

  public AppUser(String username, String email, String password, String role,
      String securityQuestion, String securityQuestionAnswer) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.role = role;
    this.securityQuestion = securityQuestion;
    this.securityQuestionAnswer = securityQuestionAnswer;
  }

  public AppUser() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getPhoto() {
    return photo;
  }

  public void setPhoto(String photo) {
    this.photo = photo;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getSecurityQuestion() {
    return securityQuestion;
  }

  public void setSecurityQuestion(String securityQuestion) {
    this.securityQuestion = securityQuestion;
  }

  public String getSecurityQuestionAnswer() {
    return securityQuestionAnswer;
  }

  public void setSecurityQuestionAnswer(String securityQuestionAnswer) {
    this.securityQuestionAnswer = securityQuestionAnswer;
  }

  public Set<AppUser> getFriends() {
    return friends;
  }

  public void setFriends(Set<AppUser> friends) {
    this.friends = friends;
  }

  public void addFriend(AppUser friend) {
    this.friends.add(friend);
  }

  public void deleteFriend(AppUser friend) {
    this.friends.remove(friend);
  }

  public Set<AppUser> getFriendRequests() {
    return friendRequests;
  }

  public void setFriendRequests(Set<AppUser> friendRequests) {
    this.friendRequests = friendRequests;
  }

  public void addFriendRequest(AppUser friend) {
    this.friendRequests.add(friend);
  }

  public void deleteFriendRequest(AppUser friend) {
    this.friendRequests.remove(friend);
  }

  public Set<String> getInterests() {
    return interests;
  }

  public void setInterests(Set<String> interests) {
    this.interests = interests;
  }

  public boolean isPendingRequest() {
    return pendingRequest;
  }

  public void setPendingRequest(boolean pendingRequest) {
    this.pendingRequest = pendingRequest;
  }

}
