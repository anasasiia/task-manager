package hexlet.code.service;

import hexlet.code.dto.StatusDto;
import hexlet.code.model.TaskStatus;

public interface StatusService {
    TaskStatus createTaskStatus(StatusDto statusDto);

    TaskStatus updateTaskStatus(long id, StatusDto statusDto);
}
