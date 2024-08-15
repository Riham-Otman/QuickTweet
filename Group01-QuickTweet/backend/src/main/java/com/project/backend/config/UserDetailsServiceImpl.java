package com.project.backend.config;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.stereotype.Service;

import com.project.backend.model.AppUser;
import com.project.backend.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserRepository repository;

  public UserDetailsServiceImpl(UserRepository repository) {
    this.repository = repository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<AppUser> userOpt = repository.findByUsername(username);
    UserBuilder builder = null;
    if (userOpt.isPresent()) {
      AppUser user = userOpt.get();
      builder = org.springframework.security.core.userdetails.User.withUsername(username);
      builder.password(user.getPassword());
      builder.roles(user.getRole());
    } else {
      throw new UsernameNotFoundException("User not found");
    }

    return builder.build();
  }
}
