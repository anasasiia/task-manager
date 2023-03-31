package hexlet.code.service;

import hexlet.code.model.Task;
import hexlet.code.dto.TaskDto;

public interface TaskService {
    Task createTask(TaskDto taskDto);

    Task updateTask(long id, TaskDto taskDto);
}
