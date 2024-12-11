package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.authentication.request.RegisterRequest;
import org.example.dto.TaskDto;
import org.example.entity.task.Task;
import org.example.entity.task.TaskStatus;
import org.example.entity.user.User;
import org.example.entity.user.role.Role;
import org.example.repository.TaskRepository;
import org.example.repository.UserRepository;
import org.example.service.AbstractContainerBaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;


import java.sql.Timestamp;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class TaskControllerTest  extends AbstractContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    private RegisterRequest registerRequest;

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    private Task taskFirst, taskSecond, updateTaskSecond;
    private TaskDto savedTask;
    private User user;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1L)
                .email("test@test.ru")
                .login("test_user")
                .password("test")
                .role(Role.USER)
                .createdAt(Timestamp.valueOf(java.time.LocalDateTime.now()))
                .lock(false)
                .build();

        taskFirst = Task.builder()
                .id(1L)
                .status(TaskStatus.NEW)
                .description("Description First Task")
                .title("Title First Task")
                .user(user)
                .build();
        taskSecond = Task.builder()
                .id(2L)
                .status(TaskStatus.IN_PROGRESS)
                .description("Description Second Task")
                .title("Title Second Task")
                .user(user)
                .build();

        updateTaskSecond = Task.builder()
                .id(2L)
                .status(TaskStatus.COMPLETED)
                .description("Description Second Task")
                .title("Title Second Task")
                .user(user)
                .build();

        savedTask = TaskDto.builder()
                .status(TaskStatus.NEW)
                .description("Description saved Task")
                .title("Title saved Task")
                .build();

        userRepository.save(user);
        taskRepository.save(taskFirst);
        taskRepository.save(taskSecond);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    @Test
    @DisplayName("Test get all tasks")
    @WithMockUser(username = "test_user", roles = {"USER"})
    void testGetAllTasks() throws Exception {

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Title First Task"))
                .andExpect(jsonPath("$[0].status").value("NEW"))
                .andExpect(jsonPath("$[0].description").value("Description First Task"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].title").value("Title Second Task"))
                .andExpect(jsonPath("$[1].status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$[1].description").value("Description Second Task"));
    }

    @Test
    @DisplayName("Test get a task by id ")
    @WithMockUser(username = "test_user", roles = {"USER"})
    void testGetTask() throws Exception {

        mockMvc.perform(get("/api/v1/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Title First Task"))
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.description").value("Description First Task"));
    }

    @Test
    @DisplayName("Test save tasks")
    @WithMockUser(username = "test_user", roles = {"USER"})
    void testSaveTask() throws Exception {

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title saved Task"))
                .andExpect(jsonPath("$.description").value("Description saved Task"));
    }

    @Test
    @DisplayName("Test update task")
    @WithMockUser(username = "test_user", roles = {"USER"})
    void testUpdateTask() throws Exception {

        mockMvc.perform(put("/api/v1/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTaskSecond)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.title").value("Title Second Task"))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.description").value("Description Second Task"));
    }

    @Test
    @DisplayName("Test delete task")
    @WithMockUser(username = "test_user", roles = {"USER"})
    void testDeleteTask() throws Exception {

        mockMvc.perform(delete("/api/v1/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Title First Task"))
                .andExpect(jsonPath("$.description").value("Description First Task"));
    }

    @Test
    @DisplayName("Test error delete task")
    @WithMockUser(username = "test_user", roles = {"USER"})
    void testErrorDeleteTask() throws Exception {

        mockMvc.perform(delete("/api/v1/tasks/5"))
                .andExpect(status().isNotFound());
    }
}