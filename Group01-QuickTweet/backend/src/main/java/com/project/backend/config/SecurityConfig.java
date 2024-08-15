package com.project.backend.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import static org.springframework.security.config.Customizer.withDefaults;

import com.project.backend.error.AuthEntryPoint;
import com.project.backend.filter.AuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig implements SecurityConfigInterface {
  private final UserDetailsServiceImpl userDetailsService;
  private final AuthenticationFilter authenticationFilter;
  private final AuthEntryPoint exceptionHandler;

  public SecurityConfig(UserDetailsServiceImpl userDetailsServiceImpl,
      AuthenticationFilter authenticationFilter, AuthEntryPoint exceptionHandler) {
    this.userDetailsService = userDetailsServiceImpl;
    this.authenticationFilter = authenticationFilter;
    this.exceptionHandler = exceptionHandler;
  }

  /**
   * This method is used to configure the global security settings.
   * 
   * @param auth - The authentication manager builder
   * @throws Exception
   */
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
  }

  /**
   * This method is used to create a password encoder.
   * 
   * @return - The password encoder
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * This method is used to create an authentication manager.
   * 
   * @param authConfig - The authentication configuration
   * @return - The authentication manager
   * @throws Exception - If the authentication manager cannot be created
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
      throws Exception {
    return authConfig.getAuthenticationManager();
  }

  /**
   * This method is used to create a security filter chain.
   * 
   * @param http - The http security
   * @return - The security filter chain
   * @throws Exception
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf((csrf) -> csrf.disable()).cors(withDefaults())
        .sessionManagement((sessionManagement) -> sessionManagement
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
            .requestMatchers(HttpMethod.POST, "/login").permitAll()
            .requestMatchers(HttpMethod.POST, "/users").permitAll()
            .requestMatchers(HttpMethod.GET, "/users/forgotPassword/{username}").permitAll()
            .requestMatchers(HttpMethod.POST, "/users/forgotPassword/{username}").permitAll()
            .anyRequest().authenticated())
        .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(
            (exceptionHandling) -> exceptionHandling.authenticationEntryPoint(exceptionHandler));

    return http.build();
  }

  /**
   * This method is used to create a CORS configuration source.
   * 
   * @return - The CORS configuration source
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(Arrays.asList("*"));
    config.setAllowedMethods(Arrays.asList("*"));
    config.setAllowedHeaders(Arrays.asList("*"));
    config.setAllowCredentials(false);
    config.applyPermitDefaultValues();

    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
