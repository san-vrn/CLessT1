package org.example.service;

import org.example.aspect.LogExecution;
import org.example.aspect.LogThrowing;
import org.example.dto.TaskDto;
import org.example.entity.task.Task;
import org.example.entity.task.TaskStatus;
import org.example.exception.task.TaskResourceNotFoundException;
import org.example.exception.task.TaskServiceException;
import org.example.kafka.KafkaTaskProducer;
import org.example.repository.TaskRepository;
import org.example.util.TaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final KafkaTaskProducer kafkaTaskProducer;
    private final TaskMapper taskMapper;

    @Autowired
    public TaskService(TaskRepository taskRepository, KafkaTaskProducer kafkaTaskProducer, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.kafkaTaskProducer = kafkaTaskProducer;
        this.taskMapper = taskMapper;
    }

    @LogThrowing
    @LogExecution
    public TaskDto saveTask(TaskDto task){
       try {
           task.setStatus(TaskStatus.NEW);
           taskRepository.save(taskMapper.toEntity(task));
           return task;
       }
       catch (Exception e){
           throw new TaskServiceException("Ошибка при сохранении задачи", e);
       }
    }

    @LogThrowing
    @LogExecution
    public List<TaskDto> findAllTask(){
        return taskRepository.findAll().stream().map(taskMapper::toDto).toList();
    }

    @LogThrowing
    @LogExecution
    public TaskDto findById(Long taskId){
        try {
            return taskMapper.toDto(taskRepository.findById(taskId)
                    .orElseThrow(() -> new TaskResourceNotFoundException(taskId)));
        }catch (TaskResourceNotFoundException exception){
            throw exception;
        }catch (Exception exception){
            throw new TaskServiceException(exception.getMessage(), exception);
        }
    }

    @LogThrowing
    @LogExecution
    public TaskDto updateTask(TaskDto updateTask, Long id){

        if (id == null){throw new TaskServiceException("Передан null id");}
        if(updateTask == null){throw new TaskServiceException("Передан null task");}

        try {
            Task task = taskRepository.findById(id).orElseThrow(() -> new TaskResourceNotFoundException(id));
            TaskStatus taskStatus = task.getStatus();
            task = taskMapper.toEntity(updateTask);
            task.setId(id);
            taskRepository.save(task);
            if(!task.getStatus().equals(taskStatus)){kafkaTaskProducer.send(updateTask);}
            return updateTask;
        }catch (TaskResourceNotFoundException exception){
            throw exception;
        }catch (Exception exception){
            throw new TaskServiceException(exception.getMessage(), exception);
        }
    }

    @LogThrowing
    @LogExecution
    public TaskDto deleteTask(Long taskId){

        try {
            Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskResourceNotFoundException(taskId));
            taskRepository.deleteById(taskId);
            return taskMapper.toDto(task);
        }catch (TaskResourceNotFoundException exception){
            throw exception;
        }catch (Exception exception){
            throw new TaskServiceException(exception.getMessage(), exception);
        }
    }
}
