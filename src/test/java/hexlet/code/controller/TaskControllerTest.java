package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.LabelDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.model.Task;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.StatusRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.ID;
import static hexlet.code.utils.TestUtils.TEST_USERNAME;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class TaskControllerTest {
    private final String baseUrl = "/api" + TASK_CONTROLLER_PATH;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TestUtils testUtils;

    @AfterEach
    public final void clear() {
        testUtils.tearDown();
    }

    @Test
    public void updateTask() throws Exception {
        testUtils.regDefaultUser();
        testUtils.createDefaultStatus();
        testUtils.createDefaultLabel();

        final TaskStatus status = statusRepository.findAll().get(0);
        final User executor = userRepository.findByEmail(TEST_USERNAME).get();
        final Label label = labelRepository.findAll().get(0);

        final TaskDto taskDto = new TaskDto(
                "task",
                "description",
                status.getId(),
                executor.getId(),
                List.of(label.getId())
        );

        testUtils.perform(post(baseUrl)
                        .content(asJson(taskDto))
                        .contentType(APPLICATION_JSON),
                TEST_USERNAME);

        final long taskId = taskRepository.findAll().get(0).getId();

        final TaskDto updateDto = new TaskDto(
                "new name",
                "new description",
                status.getId(),
                executor.getId(),
                List.of(label.getId())
        );

        final var response = testUtils.perform(
                        put(baseUrl + ID, taskId)
                                .content(asJson(updateDto))
                                .contentType(APPLICATION_JSON),
                        TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Task task = fromJson(response.getContentAsString(), new TypeReference<Task>() {
        });

        assertTrue(taskRepository.existsById(taskId));
        assertEquals(task.getName(), updateDto.getName());
        assertEquals(task.getDescription(), updateDto.getDescription());
    }

    @Test
    public void createTask() throws Exception {
        testUtils.regDefaultUser();
        testUtils.createDefaultStatus();
        testUtils.createDefaultLabel();

        final TaskStatus status = statusRepository.findAll().get(0);
        final User executor = userRepository.findByEmail(TEST_USERNAME).get();
        final Label label = labelRepository.findAll().get(0);

        final TaskDto taskDto = new TaskDto(
                "task",
                "description",
                status.getId(),
                executor.getId(),
                List.of(label.getId())
        );

        final var request = post(baseUrl)
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON);
        testUtils.perform(request, TEST_USERNAME).andExpect(status().isCreated());
    }

    @Test
    public void createTaskFail() throws Exception {
        testUtils.regDefaultUser();
        testUtils.createDefaultStatus();
        testUtils.createDefaultLabel();

        final TaskStatus status = statusRepository.findAll().get(0);
        final User executor = userRepository.findByEmail(TEST_USERNAME).get();
        final Label label = labelRepository.findAll().get(0);

        final TaskDto taskDto = new TaskDto(
                "",
                "description",
                status.getId(),
                executor.getId(),
                List.of(label.getId())
        );

        final var request = post(baseUrl)
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON);
        testUtils.perform(request, TEST_USERNAME).andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void getTaskById() throws Exception {
        testUtils.regDefaultUser();
        testUtils.createDefaultStatus();
        testUtils.createDefaultLabel();

        final TaskStatus status = statusRepository.findAll().get(0);
        final User executor = userRepository.findByEmail(TEST_USERNAME).get();
        final Label label = labelRepository.findAll().get(0);

        final TaskDto taskDto = new TaskDto(
                "task",
                "description",
                status.getId(),
                executor.getId(),
                List.of(label.getId())
        );

        testUtils.perform(post(baseUrl)
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON),
                TEST_USERNAME);

        final Task expectedTask = taskRepository.findAll().get(0);

        final var response = testUtils.perform(
                get(baseUrl + ID, expectedTask.getId()),
                TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Task task = fromJson(response.getContentAsString(), new TypeReference<Task>() {
        });

        assertEquals(task.getName(), expectedTask.getName());
        assertEquals(task.getDescription(), expectedTask.getDescription());
        assertEquals(task.getStatus().getName(), status.getName());
        assertEquals(task.getLabels().get(0).getName(), label.getName());
        assertEquals(task.getExecutor().getFirstName(), executor.getFirstName());
    }

    @Test
    public void getAllTasks() throws Exception {
        testUtils.regDefaultUser();
        testUtils.createDefaultStatus();
        testUtils.createDefaultLabel();

        final TaskStatus status = statusRepository.findAll().get(0);
        final User executor = userRepository.findByEmail(TEST_USERNAME).get();
        final Label label = labelRepository.findAll().get(0);

        final TaskDto taskDto = new TaskDto(
                "task",
                "description",
                status.getId(),
                executor.getId(),
                List.of(label.getId())
        );

        testUtils.perform(post(baseUrl)
                        .content(asJson(taskDto))
                        .contentType(APPLICATION_JSON),
                TEST_USERNAME);

        final var response = testUtils.perform(
                        get(baseUrl),
                        TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Task> tasks = fromJson(response.getContentAsString(), new TypeReference<List<Task>>() {
        });

        assertThat(tasks).hasSize(1);
    }

    @Test
    public void deleteTask() throws Exception {
        testUtils.regDefaultUser();
        testUtils.createDefaultStatus();
        testUtils.createDefaultLabel();

        final TaskStatus status = statusRepository.findAll().get(0);
        final User executor = userRepository.findByEmail(TEST_USERNAME).get();
        final Label label = labelRepository.findAll().get(0);

        final TaskDto taskDto = new TaskDto(
                "task",
                "description",
                status.getId(),
                executor.getId(),
                List.of(label.getId())
        );

        testUtils.perform(post(baseUrl)
                        .content(asJson(taskDto))
                        .contentType(APPLICATION_JSON),
                        TEST_USERNAME);

        final long taskId = taskRepository.findAll().get(0).getId();

        testUtils.perform(delete(baseUrl + ID, taskId), TEST_USERNAME)
                .andExpect(status().isOk());

        assertEquals(taskRepository.count(), 0);
    }

//    @Test
//    public void deleteTaskFail() throws Exception {
//        testUtils.regDefaultUser();
//        testUtils.createDefaultStatus();
//        testUtils.createDefaultLabel();
//
//        final TaskStatus status = statusRepository.findAll().get(0);
//        final User executor = userRepository.findByEmail(TEST_USERNAME).get();
//        final Label label = labelRepository.findAll().get(0);
//
//        final TaskDto taskDto = new TaskDto(
//                "task",
//                "description",
//                status.getId(),
//                executor.getId(),
//                List.of(label.getId())
//        );
//
//        testUtils.perform(post(baseUrl)
//                        .content(asJson(taskDto))
//                        .contentType(APPLICATION_JSON),
//                TEST_USERNAME);
//
//        final long taskId = taskRepository.findAll().get(0).getId();
//
//        testUtils.perform(delete(baseUrl + ID, taskId))
//                .andExpect(status().isForbidden());
//
//        assertEquals(taskRepository.count(), 0);
//    }

    @Test
    public void getTasksByFilter() throws Exception {
        testUtils.regDefaultUser();
        testUtils.createDefaultStatus();
        testUtils.createDefaultLabel();

        final LabelDto label2Dto = new LabelDto("label 2");
        testUtils.perform(post("/api/labels")
                .content(asJson(label2Dto))
                .contentType(APPLICATION_JSON), TEST_USERNAME);

        final TaskStatus status = statusRepository.findAll().get(0);
        final User executor = userRepository.findByEmail(TEST_USERNAME).get();
        final Label label1 = labelRepository.findAll().get(0);
        final Label label2 = labelRepository.findAll().get(1);

        final TaskDto taskDto = new TaskDto(
                "task",
                "description",
                status.getId(),
                executor.getId(),
                List.of(label1.getId())
        );

        testUtils.perform(post(baseUrl)
                        .content(asJson(taskDto))
                        .contentType(APPLICATION_JSON),
                TEST_USERNAME);

        final TaskDto taskDto2 = new TaskDto(
                "task",
                "description",
                status.getId(),
                executor.getId(),
                List.of(label2.getId())
        );

        testUtils.perform(post(baseUrl)
                        .content(asJson(taskDto2))
                        .contentType(APPLICATION_JSON),
                TEST_USERNAME);

        final var response = testUtils.perform(
                        get(baseUrl
                                + "?taskStatus=" + status.getId()
                                + "&executorId=" + executor.getId()
                                + "&labels=" + label1.getId()),
                        TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Task> tasks = fromJson(response.getContentAsString(), new TypeReference<List<Task>>() {
        });

        assertThat(tasks).hasSize(1);
    }
}
