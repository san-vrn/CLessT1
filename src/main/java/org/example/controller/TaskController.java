package org.example.controller;

import org.example.dto.TaskDto;
import org.example.entity.task.Task;
import org.example.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<TaskDto> getAllTasks(){return taskService.findAllTask();}


    @GetMapping("/{task_id}")
    public TaskDto getTask(@PathVariable("task_id") Long taskId){return taskService.findById(taskId);}


    @PostMapping
    public TaskDto saveTask(@RequestBody TaskDto task){return taskService.saveTask(task);}

    @PutMapping("/{task_id}")
    public TaskDto updateTask(
            @RequestBody TaskDto task,
            @PathVariable("task_id") Long taskId
    ){return taskService.updateTask(task,taskId);}


    @DeleteMapping("/{task_id}")
    public TaskDto deleteTask(
            @PathVariable("task_id") Long taskId
    ){return taskService.deleteTask(taskId);}
}
