package org.example.authentication.request;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
public class RegisterRequest {

  private String login;
  private String password;
  private String email;

}
