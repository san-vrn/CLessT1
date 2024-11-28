package org.example.entity.task;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TaskStatus {
    NEW,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}
