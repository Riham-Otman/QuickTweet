package com.project.backend.filter;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {
  private final JwtService jwtService;

  public AuthenticationFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  /**
   * This method is used to authenticate requests to endpoints other than "/login". It implements
   * the doFilterInternal method from the OncePerRequestFilter class.
   */
  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    String jws = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (jws != null) {
      // verify user token and get user
      String user = jwtService.getAuthUser(request);
      // authenticate user
      Authentication authentication =
          new UsernamePasswordAuthenticationToken(user, null, java.util.Collections.emptyList());
      // store the details of the authenticated user
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    filterChain.doFilter(request, response);
  }

}
