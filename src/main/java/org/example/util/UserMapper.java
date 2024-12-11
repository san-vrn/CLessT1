package org.example.util;

import org.example.dto.UserDto;
import org.example.entity.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserDto userDto){
        return User.builder()
                .email(userDto.getEmail())
                .login(userDto.getLogin())
                .build();
    }

    public UserDto toDto(User user){
        return UserDto.builder()
                .email(user.getEmail())
                .login(user.getLogin())
                .lock(user.getLock())
                .build();
    }
}
