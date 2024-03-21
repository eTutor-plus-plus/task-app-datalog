package at.jku.dke.task_app.datalog.services;

import at.jku.dke.etutor.task_app.dto.*;
import at.jku.dke.etutor.task_app.services.BaseTaskInGroupService;
import at.jku.dke.task_app.datalog.data.entities.AspTask;
import at.jku.dke.task_app.datalog.data.entities.DatalogTaskGroup;
import at.jku.dke.task_app.datalog.data.repositories.AspTaskRepository;
import at.jku.dke.task_app.datalog.data.repositories.DatalogTaskGroupRepository;
import at.jku.dke.task_app.datalog.dto.AspSubmissionDto;
import at.jku.dke.task_app.datalog.dto.ModifyAspTaskDto;
import at.jku.dke.task_app.datalog.evaluation.asp.AspEvaluationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * This class provides methods for managing {@link AspTask}s.
 */
@Service
public class AspTaskService extends BaseTaskInGroupService<AspTask, DatalogTaskGroup, ModifyAspTaskDto> {

    private final AspEvaluationService evaluationService;

    /**
     * Creates a new instance of class {@link AspTaskService}.
     *
     * @param repository          The task repository.
     * @param taskGroupRepository The task group repository.
     * @param evaluationService   The evaluation service.
     */
    public AspTaskService(AspTaskRepository repository, DatalogTaskGroupRepository taskGroupRepository, AspEvaluationService evaluationService) {
        super(repository, taskGroupRepository);
        this.evaluationService = evaluationService;
    }

    @Override
    protected AspTask createTask(long id, ModifyTaskDto<ModifyAspTaskDto> modifyTaskDto) {
        if (!modifyTaskDto.taskType().equals("asp"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid task type.");

        return new AspTask(modifyTaskDto.additionalData().solution(), modifyTaskDto.additionalData().maxN());
    }

    @Override
    protected void updateTask(AspTask task, ModifyTaskDto<ModifyAspTaskDto> modifyTaskDto) {
        if (!modifyTaskDto.taskType().equals("asp"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid task type.");

        task.setSolution(modifyTaskDto.additionalData().solution());
        task.setMaxN(modifyTaskDto.additionalData().maxN());
    }

    @Override
    protected TaskModificationResponseDto mapToReturnData(AspTask task, boolean create) {
        return new TaskModificationResponseDto(null, null);
    }

    @Override
    protected void afterCreate(AspTask task, ModifyTaskDto<ModifyAspTaskDto> dto) {
        var result = this.evaluationService.evaluate(new SubmitSubmissionDto<>("task-admin", "task-create", task.getId(), "en", SubmissionMode.DIAGNOSE, 3, new AspSubmissionDto(task.getSolution())));
        if (!result.points().equals(result.maxPoints()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, convertGradingDtoToString(result));
    }

    @Override
    protected void afterUpdate(AspTask task, ModifyTaskDto<ModifyAspTaskDto> dto) {
        var result = this.evaluationService.evaluate(new SubmitSubmissionDto<>("task-admin", "task-create", task.getId(), "en", SubmissionMode.DIAGNOSE, 3, new AspSubmissionDto(task.getSolution())));
        if (!result.points().equals(result.maxPoints()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, convertGradingDtoToString(result));
    }

    private static String convertGradingDtoToString(GradingDto grading) {
        var sb = new StringBuilder(grading.generalFeedback());
        grading.criteria().stream()
            .filter(c -> !c.passed())
            .filter(c -> !c.feedback().contains("<style>"))
            .forEach(c -> {
                sb.append("\n");
                sb.append(c.name());
                sb.append(": ");
                sb.append(c.feedback());
            });
        return sb.toString();
    }
}
