package at.jku.dke.task_app.datalog.services;

import at.jku.dke.etutor.task_app.dto.ModifyTaskDto;
import at.jku.dke.etutor.task_app.dto.TaskModificationResponseDto;
import at.jku.dke.etutor.task_app.services.BaseTaskInGroupService;
import at.jku.dke.task_app.datalog.data.entities.DatalogTask;
import at.jku.dke.task_app.datalog.data.entities.DatalogTaskGroup;
import at.jku.dke.task_app.datalog.data.repositories.DatalogTaskGroupRepository;
import at.jku.dke.task_app.datalog.data.repositories.DatalogTaskRepository;
import at.jku.dke.task_app.datalog.dto.ModifyDatalogTaskDto;
import edu.harvard.seas.pl.abcdatalog.parser.DatalogParseException;
import edu.harvard.seas.pl.abcdatalog.parser.DatalogParser;
import edu.harvard.seas.pl.abcdatalog.parser.DatalogTokenizer;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.StringReader;

/**
 * This class provides methods for managing {@link DatalogTask}s.
 */
@Service
public class DatalogTaskService extends BaseTaskInGroupService<DatalogTask, DatalogTaskGroup, ModifyDatalogTaskDto> {

    /**
     * Creates a new instance of class {@link DatalogTaskService}.
     *
     * @param repository          The task repository.
     * @param taskGroupRepository The task group repository.
     */
    public DatalogTaskService(DatalogTaskRepository repository, DatalogTaskGroupRepository taskGroupRepository) {
        super(repository, taskGroupRepository);
    }

    @Override
    protected DatalogTask createTask(long id, ModifyTaskDto<ModifyDatalogTaskDto> modifyTaskDto) {
        if (!modifyTaskDto.taskType().equals("datalog"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid task type.");

//        this.validate(modifyTaskDto.additionalData().solution());
        return new DatalogTask(modifyTaskDto.additionalData().solution(), modifyTaskDto.additionalData().query(), modifyTaskDto.additionalData().uncheckedTerms());
    }

    @Override
    protected void updateTask(DatalogTask task, ModifyTaskDto<ModifyDatalogTaskDto> modifyTaskDto) {
        if (!modifyTaskDto.taskType().equals("datalog"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid task type.");
//        if (!task.getSolution().equals(modifyTaskDto.additionalData().solution()))
//            this.validate(modifyTaskDto.additionalData().solution());

        task.setSolution(modifyTaskDto.additionalData().solution());
        task.setQuery(modifyTaskDto.additionalData().query());
        task.setUncheckedTerms(modifyTaskDto.additionalData().uncheckedTerms());
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
        try (var r = new StringReader(facts)) {
            var tokenizer = new DatalogTokenizer(r);
            DatalogParser.parseProgram(tokenizer);
        } catch (DatalogParseException ex) {
            LOG.warn("Failed to parse datalog program.", ex);
            throw new ValidationException("Invalid datalog program: " + ex.getMessage());
        }
    }
}
