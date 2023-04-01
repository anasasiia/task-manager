package hexlet.code.controller;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.service.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
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

import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@AllArgsConstructor
@RequestMapping("${base-url}" + LABEL_CONTROLLER_PATH)
public class LabelController {
    public static final String LABEL_CONTROLLER_PATH = "/labels";
    public static final String ID = "/{id}";

    private final LabelRepository labelRepository;

    private final LabelService labelService;

    @Operation(summary = "Get all labels")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Label.class)))
    @GetMapping
    public List<Label> getAllLabels() {
        return labelRepository.findAll().stream().toList();
    }

    @Operation(summary = "Create a label")
    @ApiResponse(responseCode = "201", description = "Label has been created")
    @PostMapping
    @ResponseStatus(CREATED)
    public Label createLabel(@RequestBody final LabelDto labelDto) {
        return labelService.createLabel(labelDto);
    }

    @Operation(summary = "Get label by ID")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Label.class)))
    @GetMapping(ID)
    public Label getLabelById(@PathVariable final long id) {
        return labelRepository.findById(id).get();
    }

    @Operation(summary = "Update a label")
    @ApiResponse(responseCode = "200", description = "Label has been updated")
    @PutMapping(ID)
    public Label updateLabel(@PathVariable final long id, @RequestBody final LabelDto labelDto) {
        return labelService.updateLabel(id, labelDto);
    }

    @Operation(summary = "Delete a label")
    @ApiResponse(responseCode = "200", description = "Label has been deleted")
    @DeleteMapping(ID)
    public void deleteLabel(@PathVariable final long id) {
        labelRepository.deleteById(id);
    }
}
