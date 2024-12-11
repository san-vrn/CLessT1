package org.example.util;

import org.example.dto.TaskDto;
import org.example.entity.task.Task;
import org.example.entity.user.User;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public Task toEntity(TaskDto taskDto, User user){
        return Task.builder()
                .title(taskDto.getTitle())
                .description(taskDto.getDescription())
                .status(taskDto.getStatus())
                .user(user)
                .build();
    }

    public TaskDto toDto(Task task){
        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .userId(task.getUser().getId())
                .build();
    }
}
