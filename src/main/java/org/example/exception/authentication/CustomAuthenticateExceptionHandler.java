package org.example.exception.authentication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class CustomAuthenticateExceptionHandler {

    @ExceptionHandler(BadAuthenticationRequestException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorResponse handleAuthenticationServiceException(BadAuthenticationRequestException ex) {
        log.info(ex.getMessage());
        return ErrorResponse.create(ex, HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(AuthenticatoinServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleAuthenticationServiceException(AuthenticatoinServiceException ex) {
        log.info(ex.getMessage());
        return ErrorResponse.create(ex, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
}