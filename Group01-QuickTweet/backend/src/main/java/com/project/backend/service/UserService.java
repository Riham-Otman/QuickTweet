package com.project.backend.service;

import java.util.List;
import java.util.Set;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.project.backend.model.AppUser;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

public interface UserService {
  AppUser createUser(AppUser user) throws EntityExistsException;

  List<AppUser> getAllUsers();

  String getSecurityQuestion(String username) throws UsernameNotFoundException;

  String updatePassword(String username, String password, String securityQuestionAnswer)
      throws Exception;

  AppUser getUserById(Long id);

  AppUser getUserByUsername(String username);

  AppUser updateUser(String username, AppUser user);

  ResponseEntity<String> deleteUser(Long id);

  AppUser deleteUser(String username) throws EntityNotFoundException;

  String getUserStatus(String username) throws UsernameNotFoundException;

  String updateUserStatus(String username, String status) throws Exception;

  String addFriend(String username, String friendUsername)
      throws EntityExistsException, UsernameNotFoundException;

  String addFriendRequest(String username, String friendUsername) throws Exception;

  Set<AppUser> getFriends(String username) throws UsernameNotFoundException;

  List<AppUser> getFriendRequests(String username) throws UsernameNotFoundException;

  String deleteFriend(String username, String friendUsername) throws Exception;

  List<AppUser> getUserByInterests(Set<String> interests);

  ResponseEntity<String> updateUserRole(Long id, String adminUsername) throws SecurityException;

  List<AppUser> searchUsers(String query);

}
