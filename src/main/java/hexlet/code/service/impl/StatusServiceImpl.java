package hexlet.code.service.impl;


import hexlet.code.dto.StatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.StatusRepository;
import hexlet.code.service.StatusService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class StatusServiceImpl implements StatusService {
    private final StatusRepository statusRepository;

    @Override
    public TaskStatus createTaskStatus(final StatusDto statusDto) {
        final TaskStatus taskStatus = new TaskStatus();
        taskStatus.setName(statusDto.getName());
        return statusRepository.save(taskStatus);
    }

    @Override
    public TaskStatus updateTaskStatus(final long id, final StatusDto statusDto) {
        final TaskStatus taskStatus = statusRepository.findById(id).get();
        taskStatus.setName(statusDto.getName());
        return statusRepository.save(taskStatus);
    }
}
