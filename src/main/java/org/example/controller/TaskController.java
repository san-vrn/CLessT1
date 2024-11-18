package org.example.controller;

import org.example.entity.Task;
import org.example.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(
    ){
        return ResponseEntity.ok(taskService.findAllTask());
    }


    @GetMapping("/{task_id}")
    public ResponseEntity<Task> getTask(
            @PathVariable("task_id") Long taskId
    ){
        return ResponseEntity.ok(taskService.findById(taskId));
    }


    @PostMapping
    public ResponseEntity<Task> saveTask(
            @RequestBody Task task
    ){
        return ResponseEntity.ok(taskService.saveTask(task));
    }


    @PutMapping("/{task_id}")
    public ResponseEntity<Task> updateTask(
            @RequestBody Task task,
            @PathVariable("task_id") Long taskId
    ){
        return ResponseEntity.ok(taskService.updateTask(task,taskId));
    }


    @DeleteMapping("/{task_id}")
    public ResponseEntity<Task> deleteTask(
            @PathVariable("task_id") Long taskId
    ){
        return ResponseEntity.ok(taskService.deleteTask(taskId));
    }


}
