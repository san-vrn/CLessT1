package org.example.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.security.oauthbearer.internals.secured.ValidateException;
import org.example.entity.token.Token;
import org.example.entity.token.TokenType;
import org.example.entity.user.User;
import org.example.exception.user.UserNotFoundException;
import org.example.repository.TokenRepository;
import org.example.repository.UserRepository;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class TokenService {

    private UserRepository userRepository;
    private TokenRepository tokenRepository;
    private JwtService jwtService;

    public String refreshAccessToken(HttpServletRequest request){
        String refreshToken = jwtService.extractJwt(request);
        String login = jwtService.extractUsername(refreshToken);
        var user = Optional.ofNullable(this.userRepository.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException(login + " user not found")));
        if(user.get().getLock()){ throw new LockedException("User account is locked");}
        if (jwtService.isTokenValid(refreshToken, user.get())) { throw new ValidateException("jwt token is invalid");}
        revokeAllUserTokens(user.get());
        return jwtService.generateToken(user.get());
    }

    public void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user);
        if (validUserTokens.isEmpty()){ return;}
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void revokeUserToken(String token){
        var validUserToken = tokenRepository.findByToken(token);
        if (validUserToken.isEmpty()){ return;}
        validUserToken.get().setExpired(true);
        validUserToken.get().setRevoked(true);
        tokenRepository.save(validUserToken.get());
    }

    public void saveUserToken(User user, String refreshToken) {
        var token = Token.builder()
                .user(user)
                .token(refreshToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }
}
