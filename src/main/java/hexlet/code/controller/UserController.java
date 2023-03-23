package hexlet.code.controller;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
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

import java.util.List;

import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@AllArgsConstructor
@RequestMapping("${base-url}" + USER_CONTROLLER_PATH)
public final class UserController {
    public static final String USER_CONTROLLER_PATH = "/users";
    public static final String ID = "/{id}";

    private static final String ONLY_OWNER_BY_ID = """
            @userRepository.findById(#id).get().getEmail() == authentication.getName()
        """;
    private final UserService service;
    private final UserRepository userRepository;

    @PostMapping
    @ResponseStatus(CREATED)
    public User create(@RequestBody final UserDto dto) {
        return service.createNewUser(dto);
    }

    @GetMapping
    public List<User> getAll() {
        return userRepository.findAll()
                .stream().toList();
    }
    @GetMapping(ID)
    public User getUserById(@PathVariable final long id) {
        return userRepository.findById(id).get();
    }

    @PutMapping(ID)
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public User update(@PathVariable final long id, @RequestBody final UserDto dto) {
        return service.updateUser(id, dto);
    }
    @DeleteMapping(ID)
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public void delete(@PathVariable final long id) {
        userRepository.deleteById(id);
    }
}
