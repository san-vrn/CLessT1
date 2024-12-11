package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.TaskDto;
import org.example.entity.task.Task;
import org.example.entity.task.TaskStatus;
import org.example.entity.user.User;
import org.example.entity.user.role.Role;
import org.example.repository.TaskRepository;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TaskServiceIntegrationTest extends AbstractContainerBaseTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    private TaskDto taskActual, savedTask, secondTaskDto;

    private User user;

    @BeforeAll
    void beforeAll() {

        user = User.builder()
                .id(1L)
                .email("test@test.ru")
                .login("test_user")
                .password("test")
                .role(Role.USER)
                .createdAt(Timestamp.valueOf(java.time.LocalDateTime.now()))
                .lock(false)
                .build();

        taskActual = TaskDto.builder()
                .id(1L)
                .title("task title")
                .description("task description")
                .status(TaskStatus.NEW)
                .userId(user.getId())
                .build();

        userRepository.save(user);
    }

    @BeforeEach
    void setUp() {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

    }

    @Test
    @DisplayName("save task")
    @Order(1)
    void saveTask() {
        TaskDto taskDto = TaskDto.builder()
                .title("task title")
                .description("task description")
                .status(TaskStatus.NEW)
                .build();

        secondTaskDto = TaskDto.builder()
                .title("second task title")
                .description("second task description")
                .status(TaskStatus.IN_PROGRESS)
                .build();

        savedTask = taskService.saveTask(taskDto);
        secondTaskDto = taskService.saveTask(secondTaskDto);
        assertEquals(savedTask,taskActual);
    }

    @Test
    @DisplayName("find all task")
    @Order(2)
    void findAllTask() {
        List<TaskDto> taskDtoList = new ArrayList<>(taskService.findAllTask());
        List<TaskDto> taskDtos = new ArrayList(Arrays.asList(secondTaskDto, savedTask));
        Collections.sort(taskDtoList, (task1, task2)-> Long.compare(task1.getId(),task2.getId()));
        Collections.sort(taskDtos, (task1, task2)-> Long.compare(task1.getId(),task2.getId()));

        assertEquals(taskDtoList.size(), taskDtos.size());
        for(int i = 0; i<taskDtoList.size(); i++){
            assertEquals(taskDtoList.get(i), taskDtos.get(i));
        }
    }

    @Test
    @DisplayName("find task by id")
    @Order(3)
    void findById() {
        TaskDto foundTask = taskService.findById(1L);
        assertEquals(foundTask,taskActual);
    }

    @Test
    @DisplayName("update task")
    @Order(4)
    void updateTask() {

        Optional<Task> originalTask = taskRepository.findById(1L);

        TaskDto taskDto = TaskDto.builder()
                .id(1L)
                .title("updated task title")
                .description("updated task description")
                .status(TaskStatus.IN_PROGRESS)
                .userId(user.getId())
                .build();

        taskService.updateTask(taskDto, user.getId());

        originalTask.get().setTitle(taskDto.getTitle());
        originalTask.get().setDescription(taskDto.getDescription());
        originalTask.get().setStatus(taskDto.getStatus());

        Optional<Task> updatedTask = taskRepository.findById(1L);

        assertEquals(originalTask,updatedTask);
    }

    @Test
    @DisplayName("delete task")
    @Order(5)
    void deleteTask() {
        Optional<Task> originalTask = taskRepository.findById(1L);
        taskService.deleteTask(1L);
        Optional<Task> deletedTask = taskRepository.findById(1L);
        assertTrue(originalTask.isPresent());
        assertFalse(deletedTask.isPresent());
    }
}
