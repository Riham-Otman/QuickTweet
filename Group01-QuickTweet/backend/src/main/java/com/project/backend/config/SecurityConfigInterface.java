package com.project.backend.config;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

public interface SecurityConfigInterface {
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception;

  public PasswordEncoder passwordEncoder();

  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
      throws Exception;

  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception;

  public CorsConfigurationSource corsConfigurationSource();
}
