package hexlet.code.controller;

import hexlet.code.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;


public class TaskStatusControllerTest {
    private final String baseUrl = "/api";

    @Autowired
    private TaskStatusRepository taskStatusRepository;



}
