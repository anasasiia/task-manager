package hexlet.code.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.component.JWTHelper;
import hexlet.code.dto.LabelDto;
import hexlet.code.dto.StatusDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.UserDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.StatusRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Map;

import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
import static hexlet.code.controller.StatusController.STATUS_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class TestUtils {
    public static final String TEST_USERNAME = "email@email.com";
    public static final String TEST_USERNAME_2 = "email2@email.com";
    private final UserDto testRegistrationDto = new UserDto(
            TEST_USERNAME,
            "fname",
            "lname",
            "pwd"
    );

    private final StatusDto testStatusDto = new StatusDto("New");

    private final LabelDto testLabelDto = new LabelDto("test bug");

    public StatusDto getTestStatusDto() {
        return testStatusDto;
    }

    public UserDto getTestRegistrationDto() {
        return testRegistrationDto;
    }

    public LabelDto getTestLabelDto() {
        return testLabelDto;
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private JWTHelper jwtHelper;

    public void tearDown() {
        taskRepository.deleteAll();
        labelRepository.deleteAll();
        statusRepository.deleteAll();
        userRepository.deleteAll();
    }

    public User getUserByEmail(final String email) {
        return userRepository.findByEmail(email).get();
    }

    public ResultActions regDefaultUser() throws Exception {
        return regUser(testRegistrationDto);
    }

    public ResultActions regUser(final UserDto userDto) throws Exception {
        final var request = post("/api" + USER_CONTROLLER_PATH)
                .content(asJson(userDto))
                .contentType(APPLICATION_JSON);
        return perform(request);
    }

    public ResultActions createDefaultStatus() throws Exception {
        return createStatus(getTestStatusDto());
    }

    public ResultActions createDefaultLabel() throws Exception {
        return createLabel(getTestLabelDto());
    }

    private ResultActions createLabel(LabelDto testLabelDto) throws Exception{
        final var request = post("/api" + LABEL_CONTROLLER_PATH)
                .content(asJson(testLabelDto))
                .contentType(APPLICATION_JSON);
        return perform(request, TEST_USERNAME);
    }

    public ResultActions createStatus(final StatusDto statusDto) throws Exception {
        final var request = post("/api" + STATUS_CONTROLLER_PATH)
                .content(asJson(statusDto))
                .contentType(APPLICATION_JSON);
        return perform(request, TEST_USERNAME);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder requestBuilder,
                                 final String byUser) throws Exception {
        final String token = jwtHelper.expiring(Map.of("username", byUser));
        requestBuilder.header(AUTHORIZATION, token);

        return perform(requestBuilder);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder requestBuilder) throws Exception {
        return mockMvc.perform(requestBuilder);
    }

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    public static String asJson(final Object object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }

    public static <T> T fromJson(final String json, final TypeReference<T> to) throws JsonProcessingException {
        return MAPPER.readValue(json, to);
    }
}
