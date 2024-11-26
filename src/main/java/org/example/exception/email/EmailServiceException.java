package org.example.exception.email;

public class EmailServiceException extends RuntimeException {
    public EmailServiceException(String message, Throwable throwable) {super(message,throwable);}
}
