package org.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.authentication.request.AuthenticationRequest;
import org.example.authentication.request.RegisterRequest;
import org.example.authentication.responce.AuthenticationResponse;
import org.example.dto.UserDto;
import org.example.service.AuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;

  @PostMapping("/register")
  public UserDto register(
      @RequestBody RegisterRequest request
  ) {
    return service.register(request);
  }

  @PostMapping("/authenticate")
  public AuthenticationResponse authenticate(
      @RequestBody AuthenticationRequest request
  ) {
    return service.authenticate(request);
  }

  @PostMapping("/refresh-token")
  public AuthenticationResponse refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ){
    return service.refreshAccessToken(request, response);
  }
}
