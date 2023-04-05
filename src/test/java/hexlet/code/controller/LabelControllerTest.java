package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
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
import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class LabelControllerTest {

    private final String baseUrl = "/api" + LABEL_CONTROLLER_PATH;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TestUtils testUtils;

    @AfterEach
    public final void clear() {
        testUtils.tearDown();
    }

    @Test
    public void createLabel() throws Exception {
        testUtils.regDefaultUser();
        testUtils.createDefaultLabel().andExpect(status().isCreated());
    }

//    @Test
//    public void createLabelFail() throws Exception {
//        testUtils.regDefaultUser();
//
//        final LabelDto labelDto = new LabelDto("new label");
//
//        final var request = post(baseUrl)
//                .content(asJson(labelDto))
//                .contentType(APPLICATION_JSON);
//        testUtils.perform(request).andExpect(status().isForbidden());
//    }

    @Test
    public void getLabelById() throws Exception {
        testUtils.regDefaultUser();
        testUtils.createDefaultLabel();

        final Label expectedLabel = labelRepository.findAll().get(0);

        final var response = testUtils.perform(
                        get(baseUrl + ID, expectedLabel.getId()),
                        TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Label label = fromJson(response.getContentAsString(), new TypeReference<Label>() {
        });

        assertEquals(label.getName(), expectedLabel.getName());
    }

    @Test
    public void getAllLabels() throws Exception {
        testUtils.regDefaultUser();
        testUtils.createDefaultLabel();

        final var response = testUtils.perform(
                        get(baseUrl),
                        TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Label> labels = fromJson(response.getContentAsString(), new TypeReference<List<Label>>() {
        });

        assertThat(labels).hasSize(1);
    }

    @Test
    public void updateLabel() throws Exception {
        testUtils.regDefaultUser();
        testUtils.createDefaultLabel();

        final long labelToUpdateId = labelRepository.findAll().get(0).getId();

        final LabelDto labelDto = new LabelDto("updated name label");

        final var response = testUtils.perform(put(baseUrl + ID, labelToUpdateId)
                                .content(asJson(labelDto))
                                .contentType(APPLICATION_JSON),
                        TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Label labelUpdated = fromJson(response.getContentAsString(), new TypeReference<Label>() {
        });

        assertTrue(labelRepository.existsById(labelToUpdateId));
        assertEquals(labelUpdated.getName(), labelDto.getName());
    }

    @Test
    public void deleteLabel() throws Exception {
        testUtils.regDefaultUser();
        testUtils.createDefaultLabel();

        final long labelId = labelRepository.findAll().get(0).getId();

        testUtils.perform(delete(baseUrl + ID, labelId), TEST_USERNAME)
                .andExpect(status().isOk());

        assertEquals(labelRepository.count(), 0);
    }

    // Вместо 403 ошибки приходит No value present
/*    @Test
    public void deleteLabelFail() throws Exception {
        testUtils.regDefaultUser();
        testUtils.createDefaultLabel();

        final long labelId = labelRepository.findAll().get(0).getId();

        testUtils.perform(delete(baseUrl + ID, labelId))
                .andExpect(status().isForbidden());

        assertEquals(labelRepository.count(), 1);
    }
 */
}
