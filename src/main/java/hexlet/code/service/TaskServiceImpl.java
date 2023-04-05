package hexlet.code.service;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;

import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.StatusRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    private final UserService userService;

    private final LabelRepository labelRepository;

    private final UserRepository userRepository;

    private final StatusRepository statusRepository;

    @Override
    public Task createTask(final TaskDto taskDto) {
        final Task task = new Task();
        final User author = userService.getCurrentUser();
        final TaskStatus status = statusRepository.findById(taskDto.getTaskStatusId()).get();
        final User executor = userRepository.findById(taskDto.getExecutorId()).get();

        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());
        task.setTaskStatus(status);
        task.setAuthor(author);
        task.setExecutor(executor);

        if (taskDto.getLabelIds() != null) {
            addLabels(task, taskDto);
        }

        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(final long id, final TaskDto taskDto) {
        Task task = taskRepository.findById(id).get();
        final User executor = userRepository.findById(taskDto.getExecutorId()).get();
        final TaskStatus status = statusRepository.findById(taskDto.getTaskStatusId()).get();

        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());
        task.setTaskStatus(status);
        task.setExecutor(executor);

        if (taskDto.getLabelIds() != null) {
            addLabels(task, taskDto);
        }

        return taskRepository.save(task);
    }

    private void addLabels(Task task, TaskDto taskDto) {
        List<Label> labels = new ArrayList<>();
        taskDto.getLabelIds()
                .forEach(id -> labels.add(labelRepository.findById(id).get()));
        task.setLabels(labels);
    }

}
