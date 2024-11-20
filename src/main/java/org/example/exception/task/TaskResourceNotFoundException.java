package org.example.exception.task;

public class TaskResourceNotFoundException extends RuntimeException{

    private final String message;

    public TaskResourceNotFoundException(Long id) {
        this.message = "Task с id " + id + " не найден";
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
