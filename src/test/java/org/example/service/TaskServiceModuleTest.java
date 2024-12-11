package org.example.service;

import org.example.auditing.ApplicationAuditAware;
import org.example.entity.user.role.Role;
import org.example.exception.task.TaskResourceNotFoundException;
import org.example.kafka.KafkaTaskProducer;
import org.example.repository.TaskRepository;
import org.example.util.TaskMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.example.dto.TaskDto;
import org.example.entity.task.Task;
import org.example.entity.task.TaskStatus;
import org.example.entity.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceModuleTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private KafkaTaskProducer kafkaTaskProducer;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private ApplicationAuditAware applicationAuditAware;

    @InjectMocks
    private TaskService taskService;

    private TaskDto taskDto;
    private Task task;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@test.ru")
                .lock(false)
                .login("test")
                .password("test")
                .createdAt(Timestamp.valueOf(java.time.LocalDateTime.now()))
                .role(Role.USER)
                .build();

        taskDto = TaskDto.builder()
                .id(1L)
                .title("task title")
                .description("task description")
                .status(TaskStatus.NEW)
                .userId(user.getId())
                .build();
        taskDto.setStatus(TaskStatus.NEW);

        task = Task.builder()
                .id(1L)
                .user(user)
                .title("task title")
                .description("task description")
                .status(TaskStatus.NEW)
                .build();
    }

    @Test
    void saveTask() {
        when(applicationAuditAware.getCurrentUser()).thenReturn(user);
        when(taskMapper.toEntity(taskDto, user)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(taskDto);

        TaskDto result = taskService.saveTask(taskDto);

        assertNotNull(result);
        assertEquals(TaskStatus.NEW, result.getStatus());
        verify(taskRepository).save(task);
        verify(taskMapper).toDto(task);
    }

    @Test
    void testFindAllTask() {
        List<Task> tasks = List.of(task);
        when(taskRepository.findAll()).thenReturn(tasks);
        when(taskMapper.toDto(task)).thenReturn(taskDto);

        List<TaskDto> result = taskService.findAllTask();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(taskDto, result.get(0));
    }

    @Test
    void testFindById() {
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskMapper.toDto(task)).thenReturn(taskDto);

        TaskDto result = taskService.findById(taskId);

        assertNotNull(result);
        assertEquals(taskDto, result);
    }

    @Test
    void testErrorFindById() {
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskResourceNotFoundException.class, () -> taskService.findById(taskId));
    }

    @Test
    void testUpdateTask() {
        Long taskId = 1L;

        taskDto.setStatus(TaskStatus.IN_PROGRESS);

        Task updatedTask = Task.builder()
                .id(1L)
                .title(taskDto.getTitle())
                .description(taskDto.getDescription())
                .user(user)
                .status(taskDto.getStatus())
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(applicationAuditAware.getCurrentUser()).thenReturn(user);
        when(taskMapper.toEntity(taskDto, user)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(updatedTask);

        TaskDto result = taskService.updateTask(taskDto, taskId);

        assertNotNull(result);
        verify(taskRepository).save(task);
        verify(kafkaTaskProducer).send(result);
    }

    @Test
    void testDeleteTask() {
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskMapper.toDto(task)).thenReturn(taskDto);

        TaskDto result = taskService.deleteTask(taskId);

        assertNotNull(result);
        assertEquals(taskDto, result);
        verify(taskRepository).deleteById(taskId);
    }

    @Test
    void testErrorDeleteTask() {
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskResourceNotFoundException.class, () -> taskService.deleteTask(taskId));
    }
}