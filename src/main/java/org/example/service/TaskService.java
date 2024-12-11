package org.example.service;

import lombok.AllArgsConstructor;
import org.example.auditing.ApplicationAuditAware;
import org.example.dto.TaskDto;
import org.example.entity.task.Task;
import org.example.entity.task.TaskStatus;
import org.example.exception.task.TaskResourceNotFoundException;
import org.example.exception.task.TaskServiceException;
import org.example.kafka.KafkaTaskProducer;
import org.example.repository.TaskRepository;
import org.example.util.TaskMapper;
import org.springframework.stereotype.Service;
import org.starter.example.annotation.LogExecution;
import org.starter.example.annotation.LogThrowing;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final KafkaTaskProducer kafkaTaskProducer;
    private final TaskMapper taskMapper;
    private final ApplicationAuditAware applicationAuditAware;

    @LogThrowing
    @LogExecution
    public TaskDto saveTask(TaskDto task){
        try {
            task.setStatus(TaskStatus.NEW);
            return taskMapper.toDto(taskRepository.save(taskMapper.toEntity(task, applicationAuditAware.getCurrentUser())));
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
            task = taskMapper.toEntity(updateTask, applicationAuditAware.getCurrentUser());
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