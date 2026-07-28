package com.aubrian.bank_api.security.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.aubrian.bank_api.security.dto.LoginRequest;
import com.aubrian.bank_api.security.dto.LoginResponse;

@Service
public class AuthenticationService {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  public AuthenticationService(AuthenticationManager authenticationManager, JwtService jwtService) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
  }

  public LoginResponse authenticate(LoginRequest loginRequest) {
    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken
        .unauthenticated(loginRequest.username(), loginRequest.password());
    Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    String token = jwtService.generateToken(userDetails.getUsername());
    return new LoginResponse(token);
  }
}
