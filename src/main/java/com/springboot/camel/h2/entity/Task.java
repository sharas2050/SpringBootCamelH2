package com.springboot.camel.h2.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "task")
@Getter
@Setter
@ToString
public class Task {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    private String name;
    private String duration;
    private String taskGroup;
    private String assignee;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "task_id", referencedColumnName = "id")
    @NotNull
    @JsonProperty("subTasks")
    private List<SubTask> subTasks = new ArrayList<>();


    private Boolean finished = false;
}
