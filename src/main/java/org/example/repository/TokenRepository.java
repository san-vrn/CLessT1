package org.example.repository;

import org.example.entity.token.Token;
import org.example.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String jwt);
    List<Token> findAllValidTokenByUser(User user);
}
