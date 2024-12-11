package org.example.exception.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class CustomUserExceptionHandler {

    Logger logger = LoggerFactory.getLogger(CustomUserExceptionHandler.class.getName());

    @ExceptionHandler(UserIsExsistsRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse userIsExsistsRequestExceptionHandle(UserIsExsistsRequestException ex){
        logger.info(ex.getMessage());
        return ErrorResponse.create(ex, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(value = {UserNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse userNotFoundRequestExceptionHandle(UserNotFoundException ex){
        logger.info(ex.getMessage());
        return ErrorResponse.create(ex, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(value = {UsernameNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse userNameNotFoundRequestExceptionHandle(UsernameNotFoundException ex){
        logger.info(ex.getMessage());
        return ErrorResponse.create(ex, HttpStatus.NOT_FOUND, ex.getMessage());
    }
}
