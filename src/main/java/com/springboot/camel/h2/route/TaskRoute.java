package com.springboot.camel.h2.route;

import com.springboot.camel.h2.entity.Task;
import com.springboot.camel.h2.service.TaskService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class TaskRoute extends RouteBuilder {

    @Autowired
    private Environment env;

    @Override
    public void configure() throws Exception {

        restConfiguration().component("servlet").bindingMode(RestBindingMode.json)
//                .contextPath(env.getProperty("camel.component.servlet.mapping.contextPath", "/api/*"))
                .dataFormatProperty("prettyPrint", "true")
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "Spring Boot Camel H2 Rest API")
                .apiProperty("api.version", "1.0")
                .apiProperty("cors", "true")
                .apiContextRouteId("doc-api")
                .contextPath("/api")
                .port(env.getProperty("server.port", "8080"));


        rest("/task")
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces(MediaType.APPLICATION_JSON_VALUE)

                .get("/{name}").description("Get task data by name")
                .param().name("name").description("The name of the task").endParam()
                .route().to("{{route.findTaskByName}}")
                .endRest()

                .get("/id/{taskId}").description("Get task data by id")
                .param().name("taskId").description("Id of the task").endParam()
                .route()
                .to("{{route.findTaskById}}")
                .endRest()

                .put("/id/{taskId}")
                .description("Update task data")
                .param().name("taskId").description("Id of the task").endParam()
                .param().name("body").type(RestParamType.body).description("Payload for task").endParam()
                .route()
                .marshal().json()
                .unmarshal(getJacksonDataFormat(Task.class))
                .to("{{route.updateTask}}")
                .endRest()

                .get("/")
                .description("Get all tasks")
                .route().to("{{route.findAllTasks}}")
                .endRest()

                .post("/")
                .description("Add new task")
                .param().name("body").type(RestParamType.body).description("Payload for task").endParam()
                .route()
                .marshal().json()
                .unmarshal(getJacksonDataFormat(Task.class))
                .to("{{route.saveTask}}")
                .endRest()

                .delete("/{taskId}")
                .description("Remove task")
                .param().name("taskId").description("Id of the task").endParam()
                .route()
                .to("{{route.removeTask}}")
                .end();

        from("{{route.findTaskById}}")
                .log("Received id : ${header.taskId}")
                .bean(TaskService.class, "findTaskById(${header.taskId})");

        from("{{route.findTaskByName}}")
                .log("Received name : ${header.name}")
                .bean(TaskService.class, "findTaskByName(${header.name})");

        from("{{route.findAllTasks}}")
                .bean(TaskService.class, "findAllTasks");

        from("{{route.saveTask}}")
                .log("Received Body ${body}")
                .bean(TaskService.class, "addTask(${body})");

        from("{{route.updateTask}}")
                .log("Received header : ${header.taskId}")
                .log("Received Body ${body}")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Task bodyIn = (Task) exchange.getIn().getBody();
                        exchange.getIn().setBody(bodyIn);
                    }
                })
                .bean(TaskService.class, "updateTask(${header.taskId}, ${body})");

        from("{{route.removeTask}}")
                .log("Received header : ${header.taskId}")
                .bean(TaskService.class, "removeTask(${header.taskId})");

    }

    private JacksonDataFormat getJacksonDataFormat(Class<?> unmarshalType) {
        JacksonDataFormat format = new JacksonDataFormat();
        format.setUnmarshalType(unmarshalType);
        return format;
    }
}
