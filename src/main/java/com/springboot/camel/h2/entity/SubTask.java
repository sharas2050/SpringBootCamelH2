package com.springboot.camel.h2.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity(name = "subtasks")
@Getter
@Setter
@ToString
public class SubTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private Boolean finished;
}
