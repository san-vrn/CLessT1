package org.example.util;

import org.example.dto.TaskDto;
import org.example.entity.task.Task;
import org.example.entity.task.TaskStatus;
import org.example.entity.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TaskMapperTest {

    @Mock
    private User user;

    @InjectMocks
    private TaskMapper taskMapper;

    private TaskDto taskDto;


    @BeforeEach
    void setUp() {
        taskDto = TaskDto.builder()
                .id(1L)
                .title("Test Task")
                .description("Test description")
                .status(TaskStatus.NEW)
                .build();
    }

    @Test
    void toEntity() {
        Task task = taskMapper.toEntity(taskDto, user);

        assertNotNull(task);
        assertEquals(taskDto.getTitle(), task.getTitle());
        assertEquals(taskDto.getDescription(), task.getDescription());
        assertEquals(taskDto.getStatus(), task.getStatus());
        assertEquals(user, task.getUser());
    }

    @Test
    void toDto() {
        Task task = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("Test description")
                .status(TaskStatus.NEW)
                .user(user)
                .build();

        TaskDto result = taskMapper.toDto(task);

        assertNotNull(result);
        assertEquals(task.getId(), result.getId());
        assertEquals(task.getTitle(), result.getTitle());
        assertEquals(task.getDescription(), result.getDescription());
        assertEquals(task.getStatus(), result.getStatus());
        assertEquals(task.getUser().getId(), result.getUserId());
    }
}