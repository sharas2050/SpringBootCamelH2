package com.springboot.camel.h2.service;

import com.springboot.camel.h2.entity.SubTask;
import com.springboot.camel.h2.entity.Task;
import com.springboot.camel.h2.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> findAllTasks() {
        return taskRepository.findAll();
    }

    public List<Task> findTaskByName(String name) {
        return taskRepository.findTaskByName(name);
    }

    public Task findTaskById(Integer id) {

            return Optional.ofNullable(taskRepository.findById(id).get()).orElse(null);
    }

    public Task addTask(Task task) {

        System.out.println(task.toString());

        List<Boolean> subtaskStatuses = new ArrayList<>();
        List<SubTask> subTaskList = task.getSubTasks();


        for (SubTask subTask : subTaskList) {
            subtaskStatuses.add(subTask.getFinished());
        }
        System.out.println(subtaskStatuses);
        task.setFinished(!subtaskStatuses.contains(false));

        return taskRepository.save(task);
    }

    public void removeTask(int taskId) {
        taskRepository.deleteById(taskId);
    }

    public Task updateTask(Integer id, Task task) {

        task.setId(id);
        return taskRepository.save(task);
    }
}
