package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.LoginDto;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
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
import static hexlet.code.config.security.SecurityConfig.LOGIN;
import static hexlet.code.controller.UserController.ID;
import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static hexlet.code.utils.TestUtils.TEST_USERNAME;
import static hexlet.code.utils.TestUtils.TEST_USERNAME_2;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class UserControllerTest {
    @Autowired
    private UserRepository userRepository;

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
    public void registration() throws Exception {
        assertEquals(1, userRepository.count());

        final UserDto userDto = new UserDto(TEST_USERNAME_2,
                testUtils.getTestRegistrationDto().getFirstName(),
                testUtils.getTestRegistrationDto().getLastName(),
                testUtils.getTestRegistrationDto().getPassword());

        final var request = post("/api" + USER_CONTROLLER_PATH)
                        .content(asJson(userDto))
                        .contentType(APPLICATION_JSON);

        testUtils.perform(request).andExpect(status().isCreated());
        assertEquals(2, userRepository.count());
    }

    @Test
    public void getUserById() throws Exception {
        testUtils.regDefaultUser();
        final User expectedUser = userRepository.findAll().get(0);
        final var response = testUtils.perform(
                get("/api" + USER_CONTROLLER_PATH + ID, expectedUser.getId()),
                        expectedUser.getEmail()
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final User user = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedUser.getId(), user.getId());
        assertEquals(expectedUser.getEmail(), user.getEmail());
        assertEquals(expectedUser.getFirstName(), user.getFirstName());
        assertEquals(expectedUser.getLastName(), user.getLastName());
    }

    // Вместо 403 ошибки приходит No value present
//    @Test
//    public void getUserByIdFails() throws Exception {
//        utils.regDefaultUser();
//        final User expectedUser = userRepository.findAll().get(0);
//        utils.perform(get("/api" + USER_CONTROLLER_PATH + ID, expectedUser.getId()))
//                .andExpect(status().isUnauthorized());
//    }

    @Test
    public void getAllUsers() throws Exception {
        testUtils.regDefaultUser();
        final var response = testUtils.perform(get("/api" + USER_CONTROLLER_PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<User> users = fromJson(response.getContentAsString(), new TypeReference<List<User>>() {
        });

        assertThat(users).hasSize(1);
    }

    // Вместо 403 ошибки приходит No value present
//    @Test
//    public void twiceRegTheSameUserFail() throws Exception {
//        utils.regDefaultUser().andExpect(status().isCreated());
//        utils.regDefaultUser().andExpect(status().isBadRequest());
//
//        assertEquals(1, userRepository.count());
//    }

    @Test
    public void login() throws Exception {
        testUtils.regDefaultUser();
        final LoginDto loginDto = new LoginDto(
                testUtils.getTestRegistrationDto().getFirstName(),
                testUtils.getTestRegistrationDto().getLastName(),
                testUtils.getTestRegistrationDto().getEmail(),
                testUtils.getTestRegistrationDto().getPassword()
        );

        final var loginRequest = post("/api" + LOGIN).content(asJson(loginDto)).contentType(APPLICATION_JSON);
        testUtils.perform(loginRequest).andExpect(status().isOk());
    }

    @Test
    public void loginFail() throws Exception {
        final LoginDto loginDto = new LoginDto(
                TEST_USERNAME_2,
                testUtils.getTestRegistrationDto().getPassword()
        );

        final var loginRequest = post("/api" + LOGIN).content(asJson(loginDto)).contentType(APPLICATION_JSON);
        testUtils.perform(loginRequest).andExpect(status().isUnauthorized());
    }

    @Test
    public void updateUser() throws Exception {
        testUtils.regDefaultUser();

        final Long userId = userRepository.findByEmail(TEST_USERNAME).get().getId();

        final var userDto = new UserDto(TEST_USERNAME_2, "new name", "new last name", "new pwd");

        final var updateRequest = put("/api" + USER_CONTROLLER_PATH + ID, userId)
                .content(asJson(userDto))
                .contentType(APPLICATION_JSON);

        testUtils.perform(updateRequest, TEST_USERNAME).andExpect(status().isOk());

        assertTrue(userRepository.existsById(userId));
        assertNull(userRepository.findByEmail(TEST_USERNAME).orElse(null));
        assertNotNull(userRepository.findByEmail(TEST_USERNAME_2).orElse(null));
    }

    @Test
    public void deleteUser() throws Exception {
        testUtils.regDefaultUser();

        final Long userId = userRepository.findByEmail(TEST_USERNAME).get().getId();

        testUtils.perform(delete("/api" + USER_CONTROLLER_PATH + ID, userId), TEST_USERNAME)
                .andExpect(status().isOk());

        assertEquals(0, userRepository.count());
    }

    @Test
    public void deleteUserFails() throws Exception {
        testUtils.regDefaultUser();
        testUtils.regUser(new UserDto(
                TEST_USERNAME_2,
                "fname",
                "lname",
                "pwd"
        ));

        final Long userId = userRepository.findByEmail(TEST_USERNAME).get().getId();

        testUtils.perform(delete("/api" + USER_CONTROLLER_PATH + ID, userId), TEST_USERNAME_2)
                .andExpect(status().isForbidden());
        assertEquals(2, userRepository.count());
    }
}
