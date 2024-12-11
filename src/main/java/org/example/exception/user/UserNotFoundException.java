package org.example.exception.user;

public class UserNotFoundException extends  RuntimeException{
    private String userEmail;

    public UserNotFoundException(String message, String userEmail) {
        super(message);
        this.userEmail=userEmail;
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNotFoundException(String message) {
        super(message);
    }

    public String getUserEmail() {
        return userEmail;
    }
}
