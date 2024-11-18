package org.example.exception.task;

public class TaskServiceException extends RuntimeException{

    private final String message;

    public TaskServiceException(String message, Exception e) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
