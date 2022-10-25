package com.springboot.camel.h2.repository;

import com.springboot.camel.h2.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
    public interface TaskRepository extends JpaRepository<Task, Integer> {

        List<Task> findTaskByName(String name);

}
