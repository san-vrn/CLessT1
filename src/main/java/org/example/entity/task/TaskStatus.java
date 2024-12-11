package org.example.entity.task;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum TaskStatus {
    NEW,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}
