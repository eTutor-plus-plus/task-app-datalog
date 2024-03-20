package at.jku.dke.task_app.datalog.services;

import at.jku.dke.etutor.task_app.dto.ModifyTaskDto;
import at.jku.dke.etutor.task_app.dto.TaskModificationResponseDto;
import at.jku.dke.etutor.task_app.services.BaseTaskInGroupService;
import at.jku.dke.task_app.datalog.data.entities.DatalogTask;
import at.jku.dke.task_app.datalog.data.entities.DatalogTaskGroup;
import at.jku.dke.task_app.datalog.data.repositories.DatalogTaskGroupRepository;
import at.jku.dke.task_app.datalog.data.repositories.DatalogTaskRepository;
import at.jku.dke.task_app.datalog.dto.ModifyDatalogTaskDto;
import at.jku.dke.task_app.datalog.evaluation.DatalogExecutor;
import at.jku.dke.task_app.datalog.evaluation.exceptions.ExecutionException;
import at.jku.dke.task_app.datalog.evaluation.exceptions.SyntaxException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * This class provides methods for managing {@link DatalogTask}s.
 */
@Service
public class DatalogTaskService extends BaseTaskInGroupService<DatalogTask, DatalogTaskGroup, ModifyDatalogTaskDto> {

    private final DatalogExecutor executor;

    /**
     * Creates a new instance of class {@link DatalogTaskService}.
     *
     * @param repository          The task repository.
     * @param taskGroupRepository The task group repository.
     * @param executor            The datalog executor.
     */
    public DatalogTaskService(DatalogTaskRepository repository, DatalogTaskGroupRepository taskGroupRepository, DatalogExecutor executor) {
        super(repository, taskGroupRepository);
        this.executor = executor;
    }

    @Override
    protected DatalogTask createTask(long id, ModifyTaskDto<ModifyDatalogTaskDto> modifyTaskDto) {
        if (!modifyTaskDto.taskType().equals("datalog"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid task type.");

        this.validate(modifyTaskDto.additionalData().solution());
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
        if (!task.getSolution().equals(modifyTaskDto.additionalData().solution()))
            this.validate(modifyTaskDto.additionalData().solution());

        task.setSolution(modifyTaskDto.additionalData().solution());
        task.setQuery(this.convertStringToList(modifyTaskDto.additionalData().query()));
        task.setUncheckedTermsRaw(modifyTaskDto.additionalData().uncheckedTerms());
        setPenaltyProperties(modifyTaskDto, task);
    }

    @Override
    protected TaskModificationResponseDto mapToReturnData(DatalogTask task, boolean create) {
        return new TaskModificationResponseDto(null, null);
    }

    /**
     * Validates the given facts.
     *
     * @param facts The facts to validate.
     * @throws ValidationException If the facts are not a valid datalog program.
     */
    private void validate(String facts) {
        try {
            this.executor.execute(facts, new String[0]);
        } catch (SyntaxException ex) {
            LOG.warn("Failed to parse datalog program.", ex);
            throw new ValidationException("Invalid datalog program: " + ex.getMessage());
        } catch (IOException | ExecutionException ex) {
            LOG.error("Failed to validate datalog program.", ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to validate datalog program.", ex);
        }
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
}
