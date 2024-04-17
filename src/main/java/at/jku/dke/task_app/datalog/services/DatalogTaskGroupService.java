package at.jku.dke.task_app.datalog.services;

import at.jku.dke.etutor.task_app.dto.ModifyTaskGroupDto;
import at.jku.dke.etutor.task_app.dto.TaskGroupModificationResponseDto;
import at.jku.dke.etutor.task_app.services.BaseTaskGroupService;
import at.jku.dke.task_app.datalog.config.DatalogSettings;
import at.jku.dke.task_app.datalog.data.entities.DatalogTaskGroup;
import at.jku.dke.task_app.datalog.data.repositories.DatalogTaskGroupRepository;
import at.jku.dke.task_app.datalog.dto.ModifyDatalogTaskGroupDto;
import at.jku.dke.task_app.datalog.evaluation.DatalogExecutor;
import at.jku.dke.task_app.datalog.evaluation.exceptions.ExecutionException;
import at.jku.dke.task_app.datalog.evaluation.exceptions.SyntaxException;
import jakarta.validation.ValidationException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * This class provides methods for managing {@link DatalogTaskGroup}s.
 */
@Service
public class DatalogTaskGroupService extends BaseTaskGroupService<DatalogTaskGroup, ModifyDatalogTaskGroupDto> {

    private static final String VARIABLE_NAMES = "XYZUVWABCDEFGHIJKLMNOPQRST";
    private final MessageSource messageSource;
    private final DatalogExecutor executor;
    private final DatalogSettings settings;

    /**
     * Creates a new instance of class {@link DatalogTaskGroupService}.
     *
     * @param repository    The task group repository.
     * @param messageSource The message source.
     * @param executor      The datalog executor.
     * @param settings      The datalog settings.
     */
    public DatalogTaskGroupService(DatalogTaskGroupRepository repository, MessageSource messageSource, DatalogExecutor executor, DatalogSettings settings) {
        super(repository);
        this.messageSource = messageSource;
        this.executor = executor;
        this.settings = settings;
    }

    @Override
    protected DatalogTaskGroup createTaskGroup(long id, ModifyTaskGroupDto<ModifyDatalogTaskGroupDto> modifyTaskGroupDto) {
        if (!modifyTaskGroupDto.taskGroupType().equals("datalog"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid task group type.");

        this.validate("diagnose", modifyTaskGroupDto.additionalData().diagnoseFacts());
        this.validate("submit", modifyTaskGroupDto.additionalData().submissionFacts());

        return new DatalogTaskGroup(modifyTaskGroupDto.additionalData().diagnoseFacts(), modifyTaskGroupDto.additionalData().submissionFacts());
    }

    @Override
    protected void updateTaskGroup(DatalogTaskGroup taskGroup, ModifyTaskGroupDto<ModifyDatalogTaskGroupDto> modifyTaskGroupDto) {
        if (!modifyTaskGroupDto.taskGroupType().equals("datalog"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid task group type.");

        if (!taskGroup.getDiagnoseFacts().equals(modifyTaskGroupDto.additionalData().diagnoseFacts()))
            this.validate("diagnose", modifyTaskGroupDto.additionalData().diagnoseFacts());
        if (!taskGroup.getSubmissionFacts().equals(modifyTaskGroupDto.additionalData().submissionFacts()))
            this.validate("submit", modifyTaskGroupDto.additionalData().submissionFacts());

        taskGroup.setDiagnoseFacts(modifyTaskGroupDto.additionalData().diagnoseFacts());
        taskGroup.setSubmissionFacts(modifyTaskGroupDto.additionalData().submissionFacts());
    }

    @Override
    protected TaskGroupModificationResponseDto mapToReturnData(DatalogTaskGroup taskGroup, boolean create) {
        StringBuilder list = new StringBuilder("<ul>");
        String diagnose = taskGroup.getDiagnoseFacts().replaceAll("(?m)^%.*", ""); // remove comments

        String[] facts = diagnose.split("\\.");

        Set<String> alreadyUsed = new HashSet<>();
        for (String fact : facts) {
            if (fact.isBlank())
                continue;

            var index = fact.indexOf("(");
            if (index < 0) {
                if (!alreadyUsed.contains(fact))
                    list.append("<li>").append(fact).append("</li>");
                alreadyUsed.add(fact);
                continue;
            }

            var predicate = fact
                .substring(0, index)
                .replace("\n", "")
                .replace("\r", "")
                .replace("\t", "")
                .replace(" ", "");
            if (alreadyUsed.contains(predicate))
                continue;

            list.append("<li>");
            var terms = fact.substring(index + 1, fact.length() - 1).split(",");
            list.append(predicate).append('(');
            for (int j = 0; j < terms.length; j++) {
                if (j > 0)
                    list.append(", ");
                list.append(VARIABLE_NAMES.charAt(j));
            }
            list.append(")</li>");
            alreadyUsed.add(predicate);
        }
        list.append("</ul>");

        String id = HashIds.encode(taskGroup.getId());
        return new TaskGroupModificationResponseDto(
            this.messageSource.getMessage("defaultTaskGroupDescription", new Object[]{list.toString(), this.settings.docUrl(), id}, Locale.GERMAN),
            this.messageSource.getMessage("defaultTaskGroupDescription", new Object[]{list.toString(), this.settings.docUrl(), id}, Locale.ENGLISH));
    }

    /**
     * Returns the public URL of the diagnose facts for the specified task group.
     *
     * @param id The task group identifier.
     * @return The public URL.
     */
    public String getPublicUrl(long id) {
        return this.settings.docUrl() + HashIds.encode(id);
    }

    /**
     * Validates the given facts.
     *
     * @param part  The part from which the facts are (used for error message).
     * @param facts The facts to validate.
     * @throws ValidationException If the facts are not a valid datalog program.
     */
    private void validate(String part, String facts) {
        try {
            this.executor.execute(facts, new String[0]);
        } catch (SyntaxException ex) {
            LOG.warn("Failed to parse " + part + " datalog program.", ex);
            throw new ValidationException("Invalid " + part + " datalog program: " + ex.getMessage());
        } catch (IOException | ExecutionException ex) {
            LOG.error("Failed to validate " + part + " datalog program.", ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to validate " + part + " datalog program.", ex);
        }
    }
}
