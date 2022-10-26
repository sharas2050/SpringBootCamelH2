package com.springboot.camel.h2.service;

import com.springboot.camel.h2.entity.SubTask;
import com.springboot.camel.h2.entity.Task;
import com.springboot.camel.h2.repository.TaskRepository;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class TaskServiceTest {

    @InjectMocks
    private TaskService service;

    @Mock
    private TaskRepository repoMock;

    private Task task1,task2,finishedTask;

    private SubTask subTask1,subTask2,subTask3,subTask4;

    @Before
    public void init() {

        subTask1 = new SubTask();
        subTask1.setId(1);
        subTask1.setName("SubTask0");
        subTask1.setFinished(false);

        subTask2 = new SubTask();
        subTask2.setId(2);
        subTask2.setName("SubTask1");
        subTask2.setFinished(false);

        subTask3 = new SubTask();
        subTask3.setId(3);
        subTask3.setName("SubTask3");
        subTask3.setFinished(true);

        subTask4 = new SubTask();
        subTask4.setId(4);
        subTask4.setName("SubTask4");
        subTask4.setFinished(true);

        task1 = new Task();
        task1.setId(1);
        task1.setName("MainTask");
        task1.setDuration("00:15:23");
        task1.setTaskGroup("Group0");
        task1.setAssignee("Sarunas");
        task1.getSubTasks().add(subTask1);
        task1.getSubTasks().add(subTask2);

        task2 = new Task();
        task2.setId(2);
        task2.setName("Camel in Action");
        task2.setDuration("00:10:23");
        task2.setTaskGroup("Group0");
        task2.setAssignee("Sarunas");

        //Expected finished = true
        finishedTask = new Task();
        finishedTask.setId(1);
        finishedTask.setName("FinishedTask");
        finishedTask.setDuration("01:15:23");
        finishedTask.setTaskGroup("Group0");
        finishedTask.setAssignee("Sarunas");
        finishedTask.getSubTasks().add(subTask3);
        finishedTask.getSubTasks().add(subTask4);
    }

    @Test
    public void testfindAllTasks() {

        // Data preparation
        Mockito.when(repoMock.findAll()).thenReturn(Arrays.asList(task1, task2));
        // Method call
        List<Task> taskList = service.findAllTasks();

        Assertions.assertEquals(2, taskList.size(), "findAllTasks() should return 2 tasks");
    }

    @Test
    public void testFindTaskByName() {

        List<Task> taskList = List.of(task2);
        Mockito.when(repoMock.findTaskByName("MainTask")).thenReturn(taskList);
        // Execute the service call
        List<Task> task = service.findTaskByName("MainTask");
        // Assert the response
        Assertions.assertSame(task, taskList, "Task returned was not the same as the mock");
    }

    @Test
    public void testFindTaskById() {

        Mockito.when(repoMock.findById(1)).thenReturn(Optional.ofNullable(task1));
        // Execute the service call
        Task task = service.findTaskById(1);
        // Assert the response
        Assertions.assertSame(task, task1, "Task returned was not the same as the mock");
    }

    @Test
    public void testSaveTask() {

        Mockito.when(repoMock.save(task1)).thenReturn(task1);
        // Execute the service call
        Task task = service.addTask(task1);
        // Assert the response
        Assertions.assertSame(task, task1, "Task returned was not the same as the mock");
    }

    @Test
    public void testFinishedTask() {

        Mockito.when(repoMock.save(finishedTask)).thenReturn(finishedTask);
        // Execute the service call
        Task task = service.addTask(finishedTask);
        // Assert the response
        Assertions.assertSame(task, finishedTask, "Task returned was not the same as the mock");
        Assertions.assertSame(true, task.getFinished());
    }
}