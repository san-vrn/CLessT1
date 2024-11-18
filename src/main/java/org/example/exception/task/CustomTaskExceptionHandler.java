package org.example.exception.task;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class CustomTaskExceptionHandler {

    @ExceptionHandler(TaskResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleTaskServiceException(TaskResourceNotFoundException ex) {
        return ErrorResponse.create(ex, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(TaskServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleTaskServiceException(TaskServiceException ex) {
        return ErrorResponse.create(ex, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
}