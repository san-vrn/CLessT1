package org.example.exception.authentication;

public class BadAuthenticationRequestException extends RuntimeException {
    public BadAuthenticationRequestException(String message) {
        super(message);
    }
}
