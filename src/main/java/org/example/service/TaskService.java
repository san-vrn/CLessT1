package org.example.service;

import org.example.aspect.LogExecution;
import org.example.aspect.LogThrowing;
import org.example.entity.Task;
import org.example.exception.task.TaskResourceNotFoundException;
import org.example.exception.task.TaskServiceException;
import org.example.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @LogThrowing
    @LogExecution
    public Task saveTask(Task task){
       try {
           return taskRepository.save(task);
       }
       catch (Exception e){
           throw new TaskServiceException("Ошибка при сохранении задачи", e);
       }
    }

    @LogThrowing
    @LogExecution
    public List<Task> findAllTask(){
        return taskRepository.findAll();
    }

    @LogThrowing
    @LogExecution
    public Task findById(Long taskId){

        try {
            return taskRepository.findById(taskId)
                    .orElseThrow(() -> new TaskResourceNotFoundException(taskId));
        }catch (TaskResourceNotFoundException exception){
            throw exception;
        }catch (Exception exception){
            throw new TaskServiceException(exception.getMessage(), exception);
        }
    }

    @LogThrowing
    @LogExecution
    public Task updateTask(Task updateTask, Long id){

        try {
            Task task = taskRepository.findById(id).orElseThrow(() -> new TaskResourceNotFoundException(id));
            updateTask.setId(id);
            return taskRepository.save(updateTask);
        }catch (TaskResourceNotFoundException exception){
            throw exception;
        }catch (Exception exception){
            throw new TaskServiceException(exception.getMessage(), exception);
        }
    }

    @LogThrowing
    @LogExecution
    public Task deleteTask(Long taskId){

        try {
            Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskResourceNotFoundException(taskId));
            taskRepository.deleteById(taskId);
            return task;
        }catch (TaskResourceNotFoundException exception){
            throw exception;
        }catch (Exception exception){
            throw new TaskServiceException(exception.getMessage(), exception);
        }
    }
}
