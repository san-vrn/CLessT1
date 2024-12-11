package org.example.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.security.oauthbearer.internals.secured.ValidateException;
import org.example.authentication.request.AuthenticationRequest;
import org.example.authentication.request.RegisterRequest;
import org.example.authentication.responce.AuthenticationResponse;
import org.example.dto.UserDto;
import org.example.entity.token.Token;
import org.example.entity.token.TokenType;
import org.example.entity.user.User;
import org.example.entity.user.role.Role;
import org.example.exception.authentication.BadAuthenticationRequestException;
import org.example.exception.user.UserIsExsistsRequestException;
import org.example.exception.user.UserNotFoundException;
import org.example.repository.TokenRepository;
import org.example.repository.UserRepository;
import org.example.util.UserMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository userRepository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final UserMapper userMapper;

  public UserDto register(RegisterRequest request) {
    try {
      validateRequest(request);
      if (userRepository.findByLogin(request.getLogin()).isPresent()) {
        throw new UserIsExsistsRequestException("A user with this login already exists", request.getEmail());
      }

      var user = User.builder()
              .email(request.getEmail())
              .login(request.getLogin())
              .password(passwordEncoder.encode(request.getPassword()))
              .role(Role.USER)
              .createdAt(Timestamp.valueOf(java.time.LocalDateTime.now()))
              .lock(false)
              .build();

      user = userRepository.save(user);
      return userMapper.toDto(user);
    }catch (UserIsExsistsRequestException userIsExsistsRequestException){
      throw userIsExsistsRequestException;
    } catch (BadAuthenticationRequestException badAuthenticationRequestException){
      throw badAuthenticationRequestException;
    } catch (Exception e){
      throw new AuthenticationServiceException(e.getMessage(),e);
    }
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    try {
      validateRequest(request);
      var user = userRepository.findByLogin(request.getLogin())
              .orElseThrow(()-> new UserNotFoundException(request.getLogin(),"User not found"));
      if(user!=null && !checkPassword(user,request.getPassword())
      ){
        throw new BadAuthenticationRequestException("The wrong password or login was entered");
      }
      if(user.getLock()){throw new BadAuthenticationRequestException("Your account is blocked");}

      var jwtToken = jwtService.generateToken(user);
      var refreshToken = jwtService.generateRefreshToken(user);
      revokeAllUserTokens(user);
      saveUserToken(user, refreshToken);
      return AuthenticationResponse.builder()
              .accessToken(jwtToken)
              .refreshToken(refreshToken)
              .login(user.getLogin())
              .email(user.getEmail())
              .build();
    }catch (BadAuthenticationRequestException exception){
      throw exception;
    }
    catch (Exception e){
      log.info(e.getMessage());
      throw e;
    }
  }

  private void saveUserToken(User user, String jwtToken) {
    var token = Token.builder()
            .user(user)
            .token(jwtToken)
            .tokenType(TokenType.BEARER)
            .expired(false)
            .revoked(false)
            .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user);
    if (validUserTokens.isEmpty()){ return;}
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

  public AuthenticationResponse refreshAccessToken(
          HttpServletRequest request,
          HttpServletResponse response
  ){
    final String refreshToken;
    final String login;
    final Optional<User> user;

    try {
      refreshToken = jwtService.extractJwt(request);
      login = jwtService.extractUsername(refreshToken);

      if (login == null) {throw new AuthenticationServiceException("username in jwt token is null");}

      user = Optional.ofNullable(this.userRepository.findByLogin(login)
              .orElseThrow(() -> new UserNotFoundException(login + " user not found")));
      if (!jwtService.isTokenValid(refreshToken, user.get())) { throw new ValidateException("jwt token is invalid");}
      var accessToken = jwtService.generateToken(user.get());
      revokeAllUserTokens(user.get());
      return AuthenticationResponse.builder()
              .accessToken(accessToken)
              .build();

    } catch (Exception e){
      throw new RuntimeException(e);
    }
  }

  private void validateRequest(Object request) {
    if (request == null) {
      return;
    }
    if (request instanceof AuthenticationResponse || request instanceof RegisterRequest) {
      try {
        for (Field field : request.getClass().getDeclaredFields()) {
          boolean originalAccessible = field.canAccess(request);
          field.setAccessible(true);
          Object value = field.get(request);
          if (!StringUtils.hasText(value.toString())) {
            throw new BadAuthenticationRequestException("all fields are required");
          }
          field.setAccessible(originalAccessible);
        }
      } catch (Exception e) {
        throw new AuthenticationServiceException(e.getMessage(), e);
      }
    }
  }

  private boolean checkPassword(User user, String password){
    return passwordEncoder.matches(password,user.getPassword());
  }
}



