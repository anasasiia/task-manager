package hexlet.code.controller;

import hexlet.code.dto.StatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.StatusRepository;
import hexlet.code.service.StatusService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static hexlet.code.controller.StatusController.STATUS_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@AllArgsConstructor
@RequestMapping("${base-url}" + STATUS_CONTROLLER_PATH)
public class StatusController {
    public static final String STATUS_CONTROLLER_PATH = "/statuses";
    public static final String ID = "/{id}";

    private final StatusService statusService;

    private final StatusRepository statusRepository;

    @GetMapping(ID)
    public TaskStatus getTaskStatusById(@PathVariable long id) {
        return statusRepository.findById(id).get();
    }

    @GetMapping
    public List<TaskStatus> getAllTaskStatuses() {
        return statusRepository.findAll()
                .stream().toList();
    }

    @PostMapping()
    @ResponseStatus(CREATED)
    public TaskStatus createTaskStatus(@RequestBody @Valid StatusDto statusDto) {
        return statusService.createTaskStatus(statusDto);
    }

    @PutMapping(ID)
    public TaskStatus updateTaskStatus(@PathVariable long id, @RequestBody StatusDto statusDto) {
        return statusService.updateTaskStatus(id, statusDto);
    }

    @DeleteMapping(ID)
    public void deleteTaskStatus(@PathVariable long id) {
        statusRepository.deleteById(id);
    }

}
