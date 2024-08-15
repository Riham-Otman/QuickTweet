package com.project.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.backend.model.AppUser;
import java.util.List;
import java.util.Set;


@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
  Optional<AppUser> findByEmail(String email);

  Optional<AppUser> findByUsername(String username);

  List<AppUser> findByInterestsIn(Set<String> interests);

  List<AppUser> findByUsernameContainingIgnoreCase(String username);

}
