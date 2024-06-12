package at.jku.dke.task_app.datalog.services;

import at.jku.dke.etutor.task_app.dto.*;
import at.jku.dke.etutor.task_app.services.BaseTaskInGroupService;
import at.jku.dke.task_app.datalog.data.entities.DatalogTask;
import at.jku.dke.task_app.datalog.data.entities.DatalogTaskGroup;
import at.jku.dke.task_app.datalog.data.repositories.DatalogTaskGroupRepository;
import at.jku.dke.task_app.datalog.data.repositories.DatalogTaskRepository;
import at.jku.dke.task_app.datalog.dto.DatalogSubmissionDto;
import at.jku.dke.task_app.datalog.dto.ModifyDatalogTaskDto;
import at.jku.dke.task_app.datalog.evaluation.dlg.DatalogEvaluationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

/**
 * This class provides methods for managing {@link DatalogTask}s.
 */
@Service
public class DatalogTaskService extends BaseTaskInGroupService<DatalogTask, DatalogTaskGroup, ModifyDatalogTaskDto> {

    private final DatalogEvaluationService evaluationService;

    /**
     * Creates a new instance of class {@link DatalogTaskService}.
     *
     * @param repository          The task repository.
     * @param taskGroupRepository The task group repository.
     * @param evaluationService   The datalog evaluation service.
     */
    public DatalogTaskService(DatalogTaskRepository repository, DatalogTaskGroupRepository taskGroupRepository, DatalogEvaluationService evaluationService) {
        super(repository, taskGroupRepository);
        this.evaluationService = evaluationService;
    }

    @Override
    protected DatalogTask createTask(long id, ModifyTaskDto<ModifyDatalogTaskDto> modifyTaskDto) {
        if (!modifyTaskDto.taskType().equals("datalog"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid task type.");

        var task = new DatalogTask(
            modifyTaskDto.additionalData().solution(),
            this.convertStringToList(modifyTaskDto.additionalData().query()),
            modifyTaskDto.additionalData().uncheckedTerms());

        setPenaltyProperties(modifyTaskDto, task);

        return task;
    }

    @Override
    protected void updateTask(DatalogTask task, ModifyTaskDto<ModifyDatalogTaskDto> modifyTaskDto) {
        if (!modifyTaskDto.taskType().equals("datalog"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid task type.");

        task.setSolution(modifyTaskDto.additionalData().solution());
        task.setQuery(this.convertStringToList(modifyTaskDto.additionalData().query()));
        task.setUncheckedTermsRaw(modifyTaskDto.additionalData().uncheckedTerms());
        setPenaltyProperties(modifyTaskDto, task);
    }

    @Override
    protected TaskModificationResponseDto mapToReturnData(DatalogTask task, boolean create) {
        return new TaskModificationResponseDto(null, null);
    }

    @Override
    protected void afterCreate(DatalogTask task, ModifyTaskDto<ModifyDatalogTaskDto> dto) {
        // Validate grading
        var result = this.evaluationService.evaluate(new SubmitSubmissionDto<>("task-admin", "task-create", task.getId(), "en", SubmissionMode.DIAGNOSE, 3, new DatalogSubmissionDto(task.getSolution())));
        if (!result.points().equals(result.maxPoints()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, convertGradingDtoToString(result));

        // Validate not empty solution on diagnose and submit facts
        var executionResult = this.evaluationService.execute(new SubmitSubmissionDto<>("task-admin", "task-create", task.getId(), "en", SubmissionMode.DIAGNOSE, 3, new DatalogSubmissionDto(task.getSolution())));
        if (executionResult.result().values().stream().anyMatch(List::isEmpty))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Query result for mode DIAGNOSE is empty!");

        executionResult = this.evaluationService.execute(new SubmitSubmissionDto<>("task-admin", "task-create", task.getId(), "en", SubmissionMode.SUBMIT, 3, new DatalogSubmissionDto(task.getSolution())));
        if (executionResult.result().values().stream().anyMatch(List::isEmpty))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Query result for mode SUBMIT is empty!");
    }

    @Override
    protected void afterUpdate(DatalogTask task, ModifyTaskDto<ModifyDatalogTaskDto> dto) {
        // Validate grading
        var result = this.evaluationService.evaluate(new SubmitSubmissionDto<>("task-admin", "task-create", task.getId(), "en", SubmissionMode.DIAGNOSE, 3, new DatalogSubmissionDto(task.getSolution())));
        if (!result.points().equals(result.maxPoints()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, convertGradingDtoToString(result));

        // Validate not empty solution on diagnose and submit facts
        var executionResult = this.evaluationService.execute(new SubmitSubmissionDto<>("task-admin", "task-create", task.getId(), "en", SubmissionMode.DIAGNOSE, 3, new DatalogSubmissionDto(task.getSolution())));
        if (executionResult.result().values().stream().anyMatch(List::isEmpty))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Query result for mode DIAGNOSE is empty!");

        executionResult = this.evaluationService.execute(new SubmitSubmissionDto<>("task-admin", "task-create", task.getId(), "en", SubmissionMode.SUBMIT, 3, new DatalogSubmissionDto(task.getSolution())));
        if (executionResult.result().values().stream().anyMatch(List::isEmpty))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Query result for mode SUBMIT is empty!");
    }

    /**
     * Converts the given string to a list of strings by splitting semicolon.
     *
     * @param s The string to convert.
     * @return The list of strings.
     */
    private List<String> convertStringToList(String s) {
        return Arrays.stream(s.split(";")).map(String::strip).filter(x -> !x.isBlank()).toList();
    }

    private static void setPenaltyProperties(ModifyTaskDto<ModifyDatalogTaskDto> modifyTaskDto, DatalogTask task) {
        task.setMissingPredicatePenalty(modifyTaskDto.additionalData().missingPredicatePenalty());
        task.setMissingFactPenalty(modifyTaskDto.additionalData().missingFactPenalty());
        task.setSuperfluousFactPenalty(modifyTaskDto.additionalData().superfluousFactPenalty());

        task.setMissingPredicateStrategy(modifyTaskDto.additionalData().missingPredicateStrategy());
        task.setMissingFactStrategy(modifyTaskDto.additionalData().missingFactStrategy());
        task.setSuperfluousFactStrategy(modifyTaskDto.additionalData().superfluousFactStrategy());
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
