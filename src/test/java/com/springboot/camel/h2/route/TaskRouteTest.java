package com.springboot.camel.h2.route;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.camel.h2.SpringBootCamelH2Application;
import com.springboot.camel.h2.entity.Task;
import org.apache.camel.*;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.engine.JupiterTestEngine;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@CamelSpringBootTest
@EnableAutoConfiguration
@MockEndpoints
@SpringBootTest
public class TaskRouteTest {

	private static Logger logger = LoggerFactory.getLogger(TaskRouteTest.class);

	@EndpointInject("mock:direct:saveTask")
	private MockEndpoint mockSave;

	@Autowired
	ProducerTemplate producerTemplate;

	@BeforeEach
	public void init() throws JsonProcessingException {

		ObjectMapper objectMapper = new ObjectMapper();

		String requestJson0 =
			"{\"name\": \"Camel in Action\",\"duration\": \"00:15:23\",\"taskGroup\": \"Group0\",\"assignee\": \"Sarunas\",\"subTasks\": [{\"name\": \"SubTask0\",\"finished\": false},{\"name\": \"SubTask1\",\"finished\": false}]}";
		String requestJson1 =
			"{\"name\": \"MainTask\",\"duration\": \"00:10:23\",\"taskGroup\": \"Group0\",\"assignee\": \"Sarunas\",\"subTasks\": [{\"name\": \"SubTask0\",\"finished\": false},{\"name\": \"SubTask1\",\"finished\": false}]}";

		Task task0 = objectMapper.readValue(requestJson0, Task.class);
		Task task1 = objectMapper.readValue(requestJson1, Task.class);

		producerTemplate.sendBody("direct:saveTask", task0);
		producerTemplate.sendBody("direct:saveTask", task1);

	}

	@Test
	public void testReceivedCounter() throws InterruptedException {
		logger.info("Starting testReceivedCounter test");

		mockSave.getReceivedCounter();
		producerTemplate.requestBody("direct:findAllTasks", "", java.util.List.class);
		mockSave.assertIsSatisfied();
	}

	@Test
	public void testFindAllTasksRespond() throws InterruptedException, JsonProcessingException {

		logger.info("Starting testFindAllTasksRespond() test");

		ObjectMapper objectMapper = new ObjectMapper();

		String respondJson = "[\n" +
				"{\"id\": 1,\"name\": \"Camel in Action\",\"duration\": \"00:15:23\",\"taskGroup\": \"Group0\",\"assignee\": \"Sarunas\",\"finished\": false,\"subTasks\": [{\"id\": 1,\"name\": \"SubTask0\",\"finished\": false},{\"id\": 2,\"name\": \"SubTask1\",\"finished\": false}]},\n" +
				"{\"id\": 2,\"name\": \"MainTask\",\"duration\": \"00:10:23\",\"taskGroup\": \"Group0\",\"assignee\": \"Sarunas\",\"finished\": false,\"subTasks\": [{\"id\": 3,\"name\": \"SubTask0\",\"finished\": false},{\"id\": 4,\"name\": \"SubTask1\",\"finished\": false}]}\n" +
				"]";

		Task[] respondValue = objectMapper.readValue(respondJson, Task[].class);
		@SuppressWarnings("unchecked")
		final List<Task> ret = producerTemplate.requestBody("direct:findAllTasks", "", java.util.List.class);
		mockSave.assertIsSatisfied();
		assertEquals(Arrays.toString(Arrays.stream(respondValue).toArray()),ret.toString());

	}

	@Test
	public void testFindTaskByIdRespond() throws JsonProcessingException {

		logger.info("Starting findTaskById() test");

		ObjectMapper objectMapper = new ObjectMapper();

		String respondJson = "{\"id\": 1,\"name\": \"Camel in Action\",\"duration\": \"00:15:23\",\"taskGroup\": \"Group0\",\"assignee\": \"Sarunas\",\"finished\": false,\"subTasks\": [{\"id\": 1,\"name\": \"SubTask0\",\"finished\": false},{\"id\": 2,\"name\": \"SubTask1\",\"finished\": false}]}";

		Task respondValue = objectMapper.readValue(respondJson, Task.class);

		final Task ret = producerTemplate.requestBodyAndHeader("direct:findTaskById","", "taskId", "1", Task.class);
		assertEquals(respondValue.toString(),ret.toString());

	}

	@Test
	public void testFindTaskByName() throws JsonProcessingException {

		logger.info("Starting findTaskByName() test");

		ObjectMapper objectMapper = new ObjectMapper();

		String respondJson = "{\"id\": 2,\"name\": \"MainTask\",\"duration\": \"00:10:23\",\"taskGroup\": \"Group0\",\"assignee\": \"Sarunas\",\"finished\": false,\"subTasks\": [{\"id\": 3,\"name\": \"SubTask0\",\"finished\": false},{\"id\": 4,\"name\": \"SubTask1\",\"finished\": false}]}";

		Task respondValue = objectMapper.readValue(respondJson, Task.class);

		final Task ret = producerTemplate.requestBodyAndHeader("direct:findTaskByName","", "name", "MainTask", Task.class);
		assertEquals(respondValue.toString(),ret.toString());

	}

	@Test
	public void testRemoveTask() throws JsonProcessingException {

		logger.info("Starting RemoveTask() test");

		ObjectMapper objectMapper = new ObjectMapper();

		String respondJson = "{\"id\": 2,\"name\": \"MainTask\",\"duration\": \"00:10:23\",\"taskGroup\": \"Group0\",\"assignee\": \"Sarunas\",\"finished\": false,\"subTasks\": [{\"id\": 3,\"name\": \"SubTask0\",\"finished\": false},{\"id\": 4,\"name\": \"SubTask1\",\"finished\": false}]}";

		Task respondValue = objectMapper.readValue(respondJson, Task.class);

		final String ret = producerTemplate.requestBodyAndHeader("direct:removeTask","", "taskId", "1", String.class);
		assertEquals("",ret);

		final Task retAll = producerTemplate.requestBody("direct:findAllTasks", "", Task.class);
		assertEquals(respondValue.toString(),retAll.toString());

	}
}
