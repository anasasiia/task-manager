package hexlet.code.controller;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
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

import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("${base-url}" + TASK_CONTROLLER_PATH)
@AllArgsConstructor
public class TaskController {
    public static final String TASK_CONTROLLER_PATH = "/tasks";
    public static final String ID = "/{id}";

    private static final String ONLY_OWNER_BY_EMAIL = """
            @taskRepository.findById(#id).get().getAuthor().getEmail() == authentication.getName()
        """;

    private TaskRepository taskRepository;

    private TaskService taskService;

    @Operation(summary = "Create a task")
    @ApiResponse(responseCode = "201", description = "Task has been created")
    @PostMapping
    @ResponseStatus(CREATED)
    public Task createTask(@RequestBody @Valid final TaskDto dto) {
        return taskService.createTask(dto);
    }

    @Operation(summary = "Get list of all tasks")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Task.class)))
    @GetMapping
    public Iterable<Task> getAllTasks(@QuerydslPredicate(root = Task.class) Predicate predicate) {
        return taskRepository.findAll(predicate);
    }

    @Operation(summary = "Get task by ID")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Task.class)))
    @GetMapping(ID)
    public Task getTaskById(@PathVariable final long id) {
        return taskRepository.findById(id).get();
    }

    @Operation(summary = "Update a task")
    @ApiResponse(responseCode = "200", description = "Task has been updated")
    @PutMapping(ID)
    public Task updateTask(@PathVariable final long id, @RequestBody @Valid final TaskDto dto) {
        return taskService.updateTask(id, dto);
    }

    @Operation(summary = "Delete a task")
    @ApiResponse(responseCode = "200", description = "Task has been deleted")
    @DeleteMapping(ID)
    @PreAuthorize(ONLY_OWNER_BY_EMAIL)
    public void deleteTask(@PathVariable final long id) {
        taskRepository.deleteById(id);
    }

}
