package org.example.exception.authentication;

public class AuthServiceException extends RuntimeException {
    public AuthServiceException(String message, Throwable throwable) {
        super(message,throwable);
    }
    public AuthServiceException(String message) {
        super(message);
    }

}
