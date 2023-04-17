package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.StatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.StatusRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.controller.StatusController.STATUS_CONTROLLER_PATH;
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
public class StatusControllerTest {
    private final String baseUrl = "/api" + STATUS_CONTROLLER_PATH;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private TestUtils testUtils;

    @BeforeEach
    public final void registerUser() throws Exception {
        testUtils.regDefaultUser();
    }

    @AfterEach
    public final void clear() {
        testUtils.tearDown();
    }

    @Test
    public void getStatusById() throws Exception {
        final StatusDto dto = new StatusDto("New task status");
        final var createTaskStatusRequest = post(baseUrl).content(asJson(dto)).contentType(APPLICATION_JSON);
        testUtils.perform(createTaskStatusRequest, TEST_USERNAME).andExpect(status().isCreated());

        final TaskStatus expectedStatus = statusRepository.findAll().get(0);
        final var response = testUtils.perform(
                        get(baseUrl + ID, expectedStatus.getId()),
                        TEST_USERNAME
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final TaskStatus status = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(status.getName(), expectedStatus.getName());
        assertEquals(status.getId(), expectedStatus.getId());
    }

    @Test
    public void getAllTaskStatuses() throws Exception {
        final StatusDto dto = new StatusDto("Task status 1");
        final var createTaskStatusRequest1 = post(baseUrl).content(asJson(dto)).contentType(APPLICATION_JSON);
        testUtils.perform(createTaskStatusRequest1, TEST_USERNAME);

        final var response = testUtils.perform(
                        get(baseUrl),
                        TEST_USERNAME
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<TaskStatus> statuses = fromJson(response.getContentAsString(),
                new TypeReference<List<TaskStatus>>() {
        });

        assertThat(statuses).hasSize(1);
    }

    @Test
    public void createNewTaskStatus() throws Exception {
        final StatusDto dto = new StatusDto("New task status");
        final var response = testUtils.perform(
                        post(baseUrl)
                        .content(asJson(dto))
                        .contentType(APPLICATION_JSON),
                        TEST_USERNAME
        ).andExpect(status().isCreated())
        .andReturn()
        .getResponse();

        final TaskStatus status = fromJson(response.getContentAsString(), new TypeReference<TaskStatus>() {
        });

        assertEquals(status.getName(), dto.getName());
        assertEquals(1, statusRepository.count());
    }

    @Test
    public void updateTaskStatus() throws Exception {
        final StatusDto dto = new StatusDto("Task status 1");

        final var createTaskStatusRequest1 = post(baseUrl).content(asJson(dto)).contentType(APPLICATION_JSON);
        testUtils.perform(createTaskStatusRequest1, TEST_USERNAME);

        final var taskStatusId = statusRepository.findAll().get(0).getId();

        final StatusDto dtoUpdated = new StatusDto("Task status 2");

        final var response = testUtils.perform(put(baseUrl + ID, taskStatusId)
                .content(asJson(dtoUpdated))
                .contentType(APPLICATION_JSON),
                TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final TaskStatus taskStatusUpdated = fromJson(response.getContentAsString(), new TypeReference<TaskStatus>() {
        });

        assertTrue(statusRepository.existsById(taskStatusId));
        assertEquals(taskStatusUpdated.getName(), dtoUpdated.getName());
    }

    @Test
    public void deleteTaskStatus() throws Exception {
        final StatusDto dto = new StatusDto("Task status 1");
        final var createTaskStatusRequest1 = post(baseUrl).content(asJson(dto)).contentType(APPLICATION_JSON);
        testUtils.perform(createTaskStatusRequest1, TEST_USERNAME);

        final long taskStatusId = statusRepository.findAll().get(0).getId();

        testUtils.perform(delete(baseUrl + ID, taskStatusId), TEST_USERNAME)
                .andExpect(status().isOk());

        assertEquals(statusRepository.count(), 0);
    }

}
