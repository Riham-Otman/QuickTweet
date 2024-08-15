package com.project.backend.service.implementation;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.ArrayList;
import org.hibernate.Hibernate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.backend.config.SecurityConfigInterface;
import com.project.backend.model.AppAuthorization;
import com.project.backend.model.AppUser;
import com.project.backend.repository.AppAuthorizationRepository;
import com.project.backend.repository.UserRepository;
import com.project.backend.service.UserService;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;

@Service
public class UserServiceImpl implements UserService {
  private final UserRepository repository;
  private final SecurityConfigInterface securityConfig;
  private final Long AppId = 1L;
  private final AppAuthorizationRepository appAuthorizationRepository;

  @PersistenceContext
  private EntityManager entityManager;

  public UserServiceImpl(UserRepository repository, SecurityConfigInterface securityConfig,
      AppAuthorizationRepository appAuthorizationRepository) {
    this.repository = repository;
    this.securityConfig = securityConfig;
    this.appAuthorizationRepository = appAuthorizationRepository;
  }

  @Override
  public AppUser createUser(AppUser user) throws EntityExistsException {
    boolean isEmailEmpty = user.getEmail().isEmpty();
    boolean isPasswordEmpty = user.getPassword().isEmpty();
    boolean isUsernameEmpty = user.getUsername().isEmpty();
    boolean isSecurityQuestionEmpty = user.getSecurityQuestion().isEmpty();
    boolean isSecurityQuestionAnswerEmpty = user.getSecurityQuestionAnswer().isEmpty();

    boolean hasEmptyFields = isEmailEmpty || isPasswordEmpty || isUsernameEmpty
        || isSecurityQuestionEmpty || isSecurityQuestionAnswerEmpty;

    if (hasEmptyFields) {
      return null;
    }

    if (repository.findByUsername(user.getUsername()).isPresent()) {
      throw new EntityExistsException("Username is not unique.");
    }

    user.setPassword(securityConfig.passwordEncoder().encode(user.getPassword()));
    user.setPendingRequest(true);
    user.setRole("USER");
    repository.save(user);

    AppAuthorization app = appAuthorizationRepository.findById(AppId)
        .orElseThrow(() -> new IllegalArgumentException("App instance has not been created."));
    app.addPendingRequest(user);
    appAuthorizationRepository.save(app);
    return user;
  }

  @Override
  public String getSecurityQuestion(String username) throws UsernameNotFoundException {
    Optional<AppUser> optUser = repository.findByUsername(username);
    if (!optUser.isPresent()) {
      throw new UsernameNotFoundException("User does not exist.");
    }

    return optUser.get().getSecurityQuestion();
  }

  @Override
  public String updatePassword(String username, String password, String securityQuestionAnswer)
      throws Exception {
    Optional<AppUser> optUser = repository.findByUsername(username);
    if (!optUser.isPresent()) {
      throw new Exception("User does not exist.");
    }

    AppUser user = optUser.get();
    if (!user.getSecurityQuestionAnswer().equals(securityQuestionAnswer)) {
      throw new Exception("Incorrect answer.");
    }

    user.setPassword(securityConfig.passwordEncoder().encode(password));
    repository.save(user);
    return "Password Updated";
  }

  @Override
  @Transactional
  public ResponseEntity<String> deleteUser(Long id) {
    Optional<AppUser> optUser = repository.findById(id);
    if (!optUser.isPresent()) {
      return ResponseEntity.badRequest().body("User not found with id: " + id);
    }

    AppUser user = optUser.get();
    user = entityManager.merge(user);
    Hibernate.initialize(user.getFriends());
    Hibernate.initialize(user.getFriendRequests());

    for (AppUser friend : user.getFriends()) {
      friend.getFriends().remove(user);
      repository.save(friend);
    }

    for (AppUser friendRequest : user.getFriendRequests()) {
      friendRequest.getFriendRequests().remove(user);
      repository.save(friendRequest);
    }

    List<AppUser> allUsers = repository.findAll();
    for (AppUser otherUser : allUsers) {
      if (otherUser.getFriendRequests().contains(user)) {
        otherUser.getFriendRequests().remove(user);
        repository.save(otherUser);
      }
    }

    repository.delete(user);
    return ResponseEntity.ok().body("User deleted");
  }

  @Override
  public AppUser deleteUser(String username) throws EntityNotFoundException {
    Optional<AppUser> optUser = repository.findByUsername(username);
    if (!optUser.isPresent()) {
      throw new EntityNotFoundException("User not found with username: " + username);
    }

    repository.delete(optUser.get());
    return optUser.get();
  }

  @Override
  public ResponseEntity<String> updateUserRole(Long id, String adminUsername)
      throws SecurityException {

    repository.findByUsername(adminUsername).filter(user -> user.getRole().equals("ADMIN"))
        .orElseThrow(() -> new SecurityException("User is not authorized to access this."));

    Optional<AppUser> optUser = repository.findById(id);
    if (!optUser.isPresent()) {
      return ResponseEntity.badRequest().body("User not found with id: " + id);
    }

    AppUser user = optUser.get();
    String newRole = user.getRole().equals("ADMIN") ? "USER" : "ADMIN";
    user.setRole(newRole);
    repository.save(user);
    return ResponseEntity.ok().body("User role updated to " + newRole);
  }

  @Override
  public List<AppUser> getAllUsers() {
    return repository.findAll().stream().filter(user -> !user.isPendingRequest())
        .collect(Collectors.toList());
  }

  @Override
  public AppUser getUserById(Long id) {
    return repository.findById(id).map(user -> user).orElse(null);
  }

  @Override
  public AppUser updateUser(String username, AppUser user) {
    if (user == null) {
      return null;
    }

    Optional<AppUser> oldUserOpt = repository.findByUsername(username);
    if (!oldUserOpt.isPresent()) {
      return null;
    }

    AppUser oldUser = oldUserOpt.get();
    oldUser.setBio(user.getBio());
    oldUser.setPhoto(user.getPhoto());
    oldUser.setStatus(user.getStatus());
    oldUser.setInterests(user.getInterests());
    repository.save(oldUser);
    return oldUser;
  }

  @Override
  public AppUser getUserByUsername(String username) {
    return repository.findByUsername(username).map(user -> user).orElse(null);
  }

  @Override
  public String getUserStatus(String username) throws UsernameNotFoundException {
    Optional<AppUser> optUser = repository.findByUsername(username);
    if (!optUser.isPresent()) {
      throw new UsernameNotFoundException("User does not exist.");
    }
    return optUser.get().getStatus();
  }

  @Override
  public String updateUserStatus(String username, String status) throws Exception {
    if (status.isEmpty() || username.isEmpty()) {
      throw new Exception("Username and Status fields cannot be empty");
    }

    Optional<AppUser> userOpt = repository.findByUsername(username);
    if (!userOpt.isPresent()) {
      throw new Error("No user with specified username exists");
    }

    AppUser user = userOpt.get();
    user.setStatus(status.replace("\"", ""));
    repository.save(user);
    return status;
  }

  @Override
  public String addFriend(String username, String friendUsername)
      throws EntityExistsException, UsernameNotFoundException {
    if (username.isEmpty() || friendUsername.isEmpty()) {
      throw new UsernameNotFoundException("Username and friend's username cannot be empty");
    }

    Optional<AppUser> optUser = repository.findByUsername(username);
    if (!optUser.isPresent()) {
      throw new UsernameNotFoundException("User does not exist");
    }

    Optional<AppUser> optFriend = repository.findByUsername(friendUsername.replace("\"", ""));
    if (!optFriend.isPresent()) {
      throw new UsernameNotFoundException("No user with username " + friendUsername + "exist");
    }

    AppUser user = optUser.get();
    AppUser friend = optFriend.get();
    if (user.getFriends().contains(friend)) {
      throw new EntityExistsException("User is already friends with " + friendUsername);
    }

    user.addFriend(friend);
    friend.addFriend(user);
    user.deleteFriendRequest(friend);
    repository.save(user);
    return new String("User " + friendUsername + " added to friends list");
  }

  @Override
  public String addFriendRequest(String username, String friendUsername) throws Exception {
    if (username.isEmpty() || friendUsername.isEmpty()) {
      throw new Exception("Username and friend's username cannot be empty");
    }

    Optional<AppUser> optUser = repository.findByUsername(username);
    if (!optUser.isPresent()) {
      throw new Exception("User does not exist");
    }

    Optional<AppUser> optFriend = repository.findByUsername(friendUsername.replace("\"", ""));
    if (!optFriend.isPresent()) {
      throw new Exception("No user with username " + friendUsername + " exist");
    }

    AppUser user = optUser.get();
    AppUser friend = optFriend.get();
    if (friend.getFriendRequests().contains(user)) {
      throw new Exception("Friend request has already been sent to " + friendUsername);
    }

    friend.addFriendRequest(user);
    repository.save(friend);
    return new String("Sent friend request to " + friendUsername);
  }

  @Override
  public List<AppUser> getFriendRequests(String username) throws UsernameNotFoundException {
    if (username.isEmpty()) {
      throw new UsernameNotFoundException("username cannot be empty");
    }

    return repository.findByUsername(username).map(user -> user.getFriendRequests())
        .map(ArrayList::new)
        .orElseThrow(() -> new UsernameNotFoundException("User does not exist"));
  }

  @Override
  public Set<AppUser> getFriends(String username) throws UsernameNotFoundException {
    if (username.isEmpty()) {
      throw new UsernameNotFoundException("username cannot be empty");
    }

    Optional<AppUser> optUser = repository.findByUsername(username);
    if (!optUser.isPresent()) {
      throw new UsernameNotFoundException("user does not exist");
    }

    return optUser.get().getFriends();
  }

  @Override
  public String deleteFriend(String username, String friendUsername) throws Exception {
    if (username.isEmpty() || friendUsername.isEmpty()) {
      throw new Exception("Username and friend's username cannot be empty");
    }

    Optional<AppUser> optUser = repository.findByUsername(username);
    if (!optUser.isPresent()) {
      throw new Exception("User does not exist");
    }

    Optional<AppUser> optFriend = repository.findByUsername(friendUsername.replace("\"", ""));
    if (!optFriend.isPresent()) {
      throw new Exception("No user with username " + friendUsername + "exist");
    }

    AppUser user = optUser.get();
    AppUser friend = optFriend.get();
    if (!user.getFriends().contains(friend)) {
      throw new Exception("User is not friends with " + friendUsername);
    }

    user.deleteFriend(friend);
    friend.deleteFriend(user);
    repository.save(user);
    return new String("Deleted user " + friendUsername + " from friends list");
  }

  @Override
  public List<AppUser> getUserByInterests(Set<String> interests) {
    System.out.println(interests.iterator().next());
    return repository.findByInterestsIn(interests);
  }

  @Override
  public List<AppUser> searchUsers(String query) {
    return repository.findByUsernameContainingIgnoreCase(query);
  }

}
