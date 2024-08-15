package com.project.backend.service.implementation.user;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.project.backend.config.SecurityConfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import com.project.backend.model.AppUser;
import com.project.backend.repository.UserRepository;
import com.project.backend.service.implementation.UserServiceImpl;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class UserServiceImplTest {
  @Autowired
  private UserServiceImpl userService;

  @Autowired
  private SecurityConfig securityConfig;

  @Autowired
  private UserRepository repository;

  @Test
  void testCreateUser() {
    AppUser user = new AppUser("root", "root@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");

    assertThat(userService.createUser(user)).isEqualTo(user);
  }

  @Test
  void testCreateUserWithEmptyEmail() {
    AppUser user = new AppUser("root", "", "password",
        "admin", "What is the name of your first pet?", "Leo");

    assertThat(userService.createUser(user)).isNull();
  }

  @Test
  void testCreateUserWithEmptyUsername() {
    AppUser user = new AppUser("", "root@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");

    assertThat(userService.createUser(user)).isNull();
  }

  @Test
  void testCreateUserWithEmptyPassword() {
    AppUser user = new AppUser("root", "root@dal.ca", "", "admin",
        "What is the name of your first pet?", "Leo");

    assertThat(userService.createUser(user)).isNull();
  }

  @Test
  void testGetSecurityQuestion() {
    AppUser user = new AppUser("root", "root@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");
    userService.createUser(user);
    assertThat(userService.getSecurityQuestion(user.getUsername()))
        .isEqualTo(user.getSecurityQuestion());
  }

  @Test
  void testGetSecurityQuestionMultipleUsers() {
    AppUser user1 = new AppUser("user1", "user1@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");
    AppUser user2 = new AppUser("user2", "user2@dal.ca",
        "password", "user",
        "What is the name of your first pet?", "Not Leo");
    userService.createUser(user1);
    userService.createUser(user2);

    assertThat(userService.getSecurityQuestion(user1.getUsername()))
        .isEqualTo(user1.getSecurityQuestion());
    assertThat(userService.getSecurityQuestion(user1.getUsername()))
        .isEqualTo(user1.getSecurityQuestion());
  }

  @Test
  void testUpdatePassword() {
    AppUser user = new AppUser("root", "root@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");
    userService.createUser(user);

    String newPassword = "newPassword";
    try {
      userService.updatePassword(user.getUsername(), newPassword, "Leo");
    } catch (Exception e) {
      fail("An exception should not have been thrown");
    }
    AppUser updatedUser = userService.getUserByUsername(user.getUsername());
    assertTrue(securityConfig.passwordEncoder().matches(newPassword, updatedUser.getPassword()));
  }

  @Test
  void testUpdatePasswordMissingUser() {
    AppUser user = new AppUser("root", "root@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");

    String newPassword = "newPassword";
    try {
      userService.updatePassword(user.getUsername(), newPassword, "Leo");
    } catch (Exception e) {
      assertThat(e.getMessage()).isEqualTo("User does not exist.");
    }
  }

  @Test
  void testDeleteUser() {
    AppUser user = new AppUser("root", "root@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");

    userService.createUser(user);
    assertThat(userService.deleteUser(user.getUsername())).isEqualTo(user);
  }

  @Test
  void testDeleteUserById() {
    AppUser user = new AppUser("root", "root@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");
    AppUser createdUser = userService.createUser(user);
    assertThat(userService.deleteUser(createdUser.getId()))
        .isEqualTo(ResponseEntity.ok().body("User deleted"));
  }

  @Test
  void testUpdateUserRole() {
    AppUser user = new AppUser("root", "root@dal.ca",
        "password", "ADMIN",
        "What is the name of your first pet?", "Leo");
    repository.save(user);
    String newRole = "USER";
    userService.updateUserRole(user.getId(), user.getUsername());
    AppUser updatedUser = repository.findById(user.getId()).get();
    assertThat(updatedUser.getRole()).isEqualTo(newRole);
  }

  @Test
  void testUpdateUserRoleBadRole() {
    AppUser user = new AppUser("root", "root@dal.ca",
        "password", "USER",
        "What is the name of your first pet?", "Leo");
    repository.save(user);
    assertThrows(SecurityException.class, () -> {
      userService.updateUserRole(user.getId(), user.getUsername());
    });
  }

  @Test
  void testGetAllUsers() {
    AppUser user = new AppUser("root", "root@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");

    userService.createUser(user);
    user.setPendingRequest(false);
    repository.save(user);
    assertThat(userService.getAllUsers().size()).isEqualTo(1);
  }

  @Test
  void testGetAllUsersMultipleUsers() {
    AppUser user1 = new AppUser("user1", "user1@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");
    AppUser user2 = new AppUser("user2", "user2@dal.ca",
        "password", "user",
        "What is the name of your first pet?", "Not Leo");
    userService.createUser(user1);
    userService.createUser(user2);
    user1.setPendingRequest(false);
    user2.setPendingRequest(false);
    repository.save(user1);
    repository.save(user2);
    assertThat(userService.getAllUsers().size()).isEqualTo(2);
  }

  @Test
  void testGetUserById() {
    AppUser user = new AppUser("root", "root@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");
    AppUser createdUser = userService.createUser(user);
    assertThat(userService.getUserById(createdUser.getId())).isEqualTo(user);
  }

  @Test
  void testGetUserByIdNotFound() {
    Long invalidUserId = 999L;
    assertNull(userService.getUserById(invalidUserId));
  }

  @Test
  void testUpdateUser() {
    AppUser user = new AppUser("root", "root@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");
    userService.createUser(user);

    user.setEmail("root2@dal.ca");
    assertThat(userService.updateUser(user.getUsername(), user)).isEqualTo(user);
  }

  @Test
  void testUpdateNonExistentUser() {
    AppUser nonExistentUser = new AppUser("nonexistent", "nonexistent@dal.ca",
        "password", "user",
        "What is your favorite color?", "Blue");

    assertNull(userService.updateUser(nonExistentUser.getUsername(), nonExistentUser));
  }

  @Test
  void testGetUserByUsername() {
    AppUser user = new AppUser("root", "root@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");
    userService.createUser(user);
    assertThat(userService.getUserByUsername(user.getUsername())).isEqualTo(user);
  }

  @Test
  void testGetUserByUsernameNotFound() {
    String nonExistentUsername = "nonexistent";

    assertNull(userService.getUserByUsername(nonExistentUsername));
  }

  @Test
  void testGetUserStatus() {
    AppUser user = new AppUser("root", "root@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");
    userService.createUser(user);
    assertThat(userService.getUserStatus(user.getUsername())).isEqualTo(user.getStatus());
  }

  @Test
  void testGetUserStatusNotFound() {
    String nonExistentUsername = "nonexistent";

    assertThrows(UsernameNotFoundException.class, () -> {
      userService.getUserStatus(nonExistentUsername);
    });
  }

  @Test
  void testUpdateUserStatus() {
    AppUser user = new AppUser("root", "root@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");

    userService.createUser(user);
    String newStatus = "available";
    try {
      userService.updateUserStatus(user.getUsername(), newStatus);
    } catch (Exception e) {
      fail("An exception should not have been thrown");
    }
    AppUser updatedUser = userService.getUserByUsername(user.getUsername());
    assertThat(updatedUser.getStatus()).isEqualTo(newStatus);
  }

  @Test
  void testUpdateUserStatusNotFound() {
    String nonExistentUsername = "nonexistent";
    String newStatus = "available";

    Error error = assertThrows(Error.class, () -> {
      userService.updateUserStatus(nonExistentUsername, newStatus);
    });
    assertThat(error.getMessage()).isEqualTo("No user with specified username exists");
  }

  @Test
  void testFriends() {
    AppUser user1 = new AppUser("root", "root@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");

    AppUser user2 = new AppUser("reee", "reee2@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");

    Set<AppUser> user1_friends = new HashSet<>(Arrays.asList(user2));
    Set<AppUser> user2_friends = new HashSet<>(Arrays.asList(user1));
    repository.save(user1);
    repository.save(user2);
    user1.addFriend(user2);
    user2.addFriend(user1);
    repository.save(user1);
    assertAll(() -> assertThat(user1.getFriends()).isEqualTo(user1_friends),
        () -> assertThat(user2.getFriends()).isEqualTo(user2_friends));
  }

  @Test
  void testAddFriendRequestWithEmptyUsername() {
    Exception exception = assertThrows(Exception.class, () -> {
      userService.addFriendRequest("", "friendUsername");
    });

    assertThat(exception.getMessage()).isEqualTo("Username and friend's username cannot be empty");
  }

  @Test
  void testAddFriendRequestWithEmptyFriendUsername() {
    Exception exception = assertThrows(Exception.class, () -> {
      userService.addFriendRequest("username", "");
    });

    assertThat(exception.getMessage()).isEqualTo("Username and friend's username cannot be empty");
  }

  @Test
  void testAddFriendRequestWithNonExistentUser() {
    Exception exception = assertThrows(Exception.class, () -> {
      userService.addFriendRequest("nonExistentUser", "friendUsername");
    });

    assertThat(exception.getMessage()).isEqualTo("User does not exist");
  }

  @Test
  void testAddFriendRequestWithNonExistentFriend() {
    AppUser user = new AppUser("user", "user@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");
    repository.save(user);

    Exception exception = assertThrows(Exception.class, () -> {
      userService.addFriendRequest("user", "nonExistentFriend");
    });

    assertThat(exception.getMessage()).isEqualTo("No user with username nonExistentFriend exist");
  }

  @Test
  void testAddFriendRequestAlreadySent() {
    AppUser user = new AppUser("user", "user@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");
    AppUser friend = new AppUser("friend", "friend@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");
    repository.save(user);
    repository.save(friend);
    friend.addFriendRequest(user);
    repository.save(friend);

    Exception exception = assertThrows(Exception.class, () -> {
      userService.addFriendRequest("user", "friend");
    });

    assertThat(exception.getMessage()).isEqualTo("Friend request has already been sent to friend");
  }

  @Test
  void testAddFriendRequestSuccess() throws Exception {
    AppUser user = new AppUser("user", "user@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");
    AppUser friend = new AppUser("friend", "friend@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");
    repository.save(user);
    repository.save(friend);

    String response = userService.addFriendRequest("user", "friend");

    assertThat(response).isEqualTo("Sent friend request to friend");
    Optional<AppUser> updatedFriend = repository.findByUsername("friend");
    assertThat(updatedFriend.get().getFriendRequests()).contains(user);
  }

  @Test
  void testGetFriendRequestsWithEmptyUsername() {
    Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
      userService.getFriendRequests("");
    });

    assertThat(exception.getMessage()).isEqualTo("username cannot be empty");
  }

  @Test
  void testGetFriendRequestsWithNonExistentUser() {
    Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
      userService.getFriendRequests("nonExistentUser");
    });

    assertThat(exception.getMessage()).isEqualTo("User does not exist");
  }

  @Test
  void testGetFriendRequests() {
    AppUser user = new AppUser("root", "root@gmail.com",
        "password", "admin",
        "What is the name of your first pet?", "Leo");
    AppUser user2 = new AppUser("reee", "reee2@gmail.com",
        "password", "admin",
        "What is the name of your first pet?", "Leo");

    Set<AppUser> user_pending = new HashSet<>(Arrays.asList(user2));
    Set<AppUser> user2_pending = new HashSet<>(Arrays.asList(user));
    repository.save(user);
    repository.save(user2);
    user.addFriendRequest(user2);
    user2.addFriendRequest(user);
    repository.save(user);
    assertAll(() -> assertThat(user.getFriendRequests()).isEqualTo(user_pending),
        () -> assertThat(user2.getFriendRequests()).isEqualTo(user2_pending));
  }

  @Test
  void testGetFriendsWithEmptyUsername() {
    Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
      userService.getFriends("");
    });

    assertThat(exception.getMessage()).isEqualTo("username cannot be empty");
  }

  @Test
  void testGetFriendsWithNonExistentUser() {
    Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
      userService.getFriends("nonExistentUser");
    });

    assertThat(exception.getMessage()).isEqualTo("user does not exist");
  }

  @Test
  void testGetFriendsSuccess() {
    AppUser user = new AppUser("user", "user@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");
    AppUser friend = new AppUser("friend", "friend@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");
    repository.save(user);
    repository.save(friend);
    user.addFriend(friend);
    friend.addFriend(user);
    repository.save(user);
    repository.save(friend);

    Set<AppUser> friends = userService.getFriends("user");

    assertThat(friends).containsExactly(friend);
  }

  @Test
  void testDeleteFriendWithEmptyUsername() {
    Exception exception = assertThrows(Exception.class, () -> {
      userService.deleteFriend("", "friend");
    });

    assertThat(exception.getMessage()).isEqualTo("Username and friend's username cannot be empty");
  }

  @Test
  void testDeleteFriendWithEmptyFriendUsername() {
    Exception exception = assertThrows(Exception.class, () -> {
      userService.deleteFriend("user", "");
    });

    assertThat(exception.getMessage()).isEqualTo("Username and friend's username cannot be empty");
  }

  @Test
  void testDeleteFriendWithNonExistentUser() {
    Exception exception = assertThrows(Exception.class, () -> {
      userService.deleteFriend("nonExistentUser", "friend");
    });

    assertThat(exception.getMessage()).isEqualTo("User does not exist");
  }

  @Test
  void testDeleteFriendWithNonExistentFriend() {
    AppUser user = new AppUser("user", "user@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");
    repository.save(user);

    Exception exception = assertThrows(Exception.class, () -> {
      userService.deleteFriend("user", "nonExistentFriend");
    });

    assertThat(exception.getMessage()).isEqualTo("No user with username nonExistentFriendexist");
  }

  @Test
  void testDeleteFriendSuccess() throws Exception {
    AppUser user = new AppUser("user", "user@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");
    AppUser friend = new AppUser("friend", "friend@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");
    repository.save(user);
    repository.save(friend);
    user.addFriend(friend);
    friend.addFriend(user);
    repository.save(user);
    repository.save(friend);

    String result = userService.deleteFriend("user", "friend");

    assertThat(result).isEqualTo("Deleted user friend from friends list");
    assertThat(user.getFriends()).isEmpty();
    assertThat(friend.getFriends()).isEmpty();
  }

  @Test
  void testGetUserByInterests() {
    AppUser user1 = new AppUser("user1", "user1@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");
    user1.setInterests(new HashSet<>(Arrays.asList("technology", "sports")));
    AppUser user2 = new AppUser("user2", "user2@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");
    user2.setInterests(new HashSet<>(Arrays.asList("music", "sports")));
    repository.save(user1);
    repository.save(user2);

    Set<String> interests = new HashSet<>(Arrays.asList("sports"));

    List<AppUser> users = userService.getUserByInterests(interests);

    assertThat(users).containsExactlyInAnyOrder(user1, user2);
  }

  @Test
  void testGetUserByNonExistingInterest() {
    AppUser user1 = new AppUser("user1", "user1@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");
    user1.setInterests(new HashSet<>(Arrays.asList("technology", "sports")));
    AppUser user2 = new AppUser("user2", "user2@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");
    user2.setInterests(new HashSet<>(Arrays.asList("music", "sports")));
    repository.save(user1);
    repository.save(user2);

    Set<String> interests = new HashSet<>(Arrays.asList("gaming"));

    List<AppUser> users = userService.getUserByInterests(interests);

    assertThat(users).isEmpty();
  }

  @Test
  void testGetUserByMultipleInterests() {
    AppUser user1 = new AppUser("user1", "user1@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");
    user1.setInterests(new HashSet<>(Arrays.asList("technology", "sports")));
    AppUser user2 = new AppUser("user2", "user2@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");
    user2.setInterests(new HashSet<>(Arrays.asList("music", "sports")));
    repository.save(user1);
    repository.save(user2);

    Set<String> interests = new HashSet<>(Arrays.asList("sports", "technology"));

    List<AppUser> users = userService.getUserByInterests(interests);

    assertThat(users).containsExactlyInAnyOrder(user1, user2);
  }

  @Test
  void testSearchUsers() {

    AppUser user1 = new AppUser("user1", "user1@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");
    AppUser user2 = new AppUser("user2", "user2@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");
    repository.save(user1);
    repository.save(user2);
    String query = "user1";

    List<AppUser> users = userService.searchUsers(query);

    assertThat(users).containsExactly(user1);
  }

  @Test
  void testSearchUsersWithNoResults() {
    AppUser user1 = new AppUser("user1", "user1@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");
    AppUser user2 = new AppUser("user2", "user2@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");
    repository.save(user1);
    repository.save(user2);

    String query = "does not exist";

    List<AppUser> users = userService.searchUsers(query);

    assertThat(users).isEmpty();
  }

  @Test
  void testSearchUsersCaseInsensitive() {
    AppUser user1 = new AppUser("user1", "user1@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");
    AppUser user2 = new AppUser("user2", "user2@dal.ca",
        "password", "admin",
        "What is the name of your first pet?", "Leo");

    repository.save(user1);
    repository.save(user2);

    String query = "USER2";

    List<AppUser> users = userService.searchUsers(query);

    assertThat(users).containsExactly(user2);
  }
}
