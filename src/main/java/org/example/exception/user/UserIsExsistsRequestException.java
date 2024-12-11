package org.example.exception.user;

public class UserIsExsistsRequestException extends  RuntimeException{
    private String userEmail;
    public UserIsExsistsRequestException(String message, String userEmail) {
        super(message);
        this.userEmail = userEmail;
    }

    public UserIsExsistsRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getUserEmail() {
        return userEmail;
    }
}
