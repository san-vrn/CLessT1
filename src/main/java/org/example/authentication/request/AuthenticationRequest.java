package org.example.authentication.request;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
public class AuthenticationRequest {

    private String login;
    private String password;
}

