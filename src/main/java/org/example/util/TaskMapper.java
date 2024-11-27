package org.example.util;

import org.example.dto.TaskDto;
import org.example.entity.task.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public Task toEntity(TaskDto taskDto){
        return Task.builder()
                .title(taskDto.getTitle())
                .description(taskDto.getDescription())
                .status(taskDto.getStatus())
                .userId(taskDto.getUserId())
                .build();
    }

    public TaskDto toDto(Task task){
        return TaskDto.builder()
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .userId(task.getUserId())
                .build();
    }
}
