package org.example.controller;

import org.example.entity.Task;
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
    public List<Task> getAllTasks(){return taskService.findAllTask();}


    @GetMapping("/{task_id}")
    public Task getTask(@PathVariable("task_id") Long taskId){return taskService.findById(taskId);}


    @PostMapping
    public Task saveTask(@RequestBody Task task){return taskService.saveTask(task);}

    @PutMapping("/{task_id}")
    public Task updateTask(
            @RequestBody Task task,
            @PathVariable("task_id") Long taskId
    ){return taskService.updateTask(task,taskId);}


    @DeleteMapping("/{task_id}")
    public Task deleteTask(
            @PathVariable("task_id") Long taskId
    ){return taskService.deleteTask(taskId);}
}
