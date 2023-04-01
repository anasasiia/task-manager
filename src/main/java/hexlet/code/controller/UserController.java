package hexlet.code.controller;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@AllArgsConstructor
@RequestMapping("${base-url}" + USER_CONTROLLER_PATH)
public class UserController {
    public static final String USER_CONTROLLER_PATH = "/users";
    public static final String ID = "/{id}";

    private static final String ONLY_OWNER_BY_ID = """
            @userRepository.findById(#id).get().getEmail() == authentication.getName()
        """;
    private final UserService service;
    private final UserRepository userRepository;

    @Operation(summary = "Create a user")
    @ApiResponse(responseCode = "201", description = "User has been created")
    @ApiResponse(responseCode = "422", description = "Invalid data")
    @PostMapping
    @ResponseStatus(CREATED)
    public User createUser(@RequestBody @Valid final UserDto dto) {
        return service.createNewUser(dto);
    }

    @Operation(summary = "Get list of all users")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = User.class)))
    @GetMapping
    public List<User> getAll() {
        return userRepository.findAll()
                .stream().toList();
    }

    @Operation(summary = "Get user by ID")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = User.class)))
    @GetMapping(ID)
    public User getUserById(@PathVariable final long id) {
        return userRepository.findById(id).get();
    }

    @Operation(summary = "Update user")
    @ApiResponse(responseCode = "200", description = "User has been updated")
    @PutMapping(ID)
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public User update(@PathVariable final long id, @RequestBody @Valid final UserDto dto) {
        return service.updateUser(id, dto);
    }

    @Operation(summary = "Delete a user")
    @ApiResponse(responseCode = "200", description = "User has been deleted")
    @DeleteMapping(ID)
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public void delete(@PathVariable final long id) {
        userRepository.deleteById(id);
    }
}
