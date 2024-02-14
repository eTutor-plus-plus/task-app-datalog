package at.jku.dke.task_app.datalog.services;

import at.jku.dke.etutor.task_app.dto.ModifyTaskGroupDto;
import at.jku.dke.etutor.task_app.dto.TaskGroupModificationResponseDto;
import at.jku.dke.etutor.task_app.services.BaseTaskGroupService;
import at.jku.dke.task_app.datalog.data.entities.DatalogTaskGroup;
import at.jku.dke.task_app.datalog.data.repositories.DatalogTaskGroupRepository;
import at.jku.dke.task_app.datalog.dto.ModifyDatalogTaskGroupDto;
import edu.harvard.seas.pl.abcdatalog.ast.PositiveAtom;
import edu.harvard.seas.pl.abcdatalog.parser.DatalogParseException;
import edu.harvard.seas.pl.abcdatalog.parser.DatalogParser;
import edu.harvard.seas.pl.abcdatalog.parser.DatalogTokenizer;
import jakarta.validation.ValidationException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.StringReader;
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

    /**
     * Creates a new instance of class {@link DatalogTaskGroupService}.
     *
     * @param repository    The task group repository.
     * @param messageSource The message source.
     */
    public DatalogTaskGroupService(DatalogTaskGroupRepository repository, MessageSource messageSource) {
        super(repository);
        this.messageSource = messageSource;
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
        try (var r = new StringReader(taskGroup.getDiagnoseFacts())) {
            var tokenizer = new DatalogTokenizer(r);
            var clauses = DatalogParser.parseProgram(tokenizer);

            Set<String> alreadyUsed = new HashSet<>();
            for (var c : clauses) {
                if (c.getHead() instanceof PositiveAtom pa && !alreadyUsed.contains(pa.getPred().getSym())) {
                    list.append("<li>").append(pa.getPred().getSym()).append('(');
                    for (int i = 0; i < pa.getPred().getArity(); i++) {
                        if (i > 0)
                            list.append(", ");
                        list.append(VARIABLE_NAMES.charAt(i));
                    }
                    list.append(")</li>");
                    alreadyUsed.add(pa.getPred().getSym());
                }
            }
        } catch (DatalogParseException ignore) {
            // this should never happen
        }
        list.append("</ul>");

        String id = HashIds.encode(taskGroup.getId());
        return new TaskGroupModificationResponseDto(
            this.messageSource.getMessage("defaultTaskGroupDescription", new Object[]{list.toString(), id}, Locale.GERMAN),
            this.messageSource.getMessage("defaultTaskGroupDescription", new Object[]{list.toString(), id}, Locale.ENGLISH));
    }

    /**
     * Validates the given facts.
     *
     * @param part  The part from which the facts are (used for error message).
     * @param facts The facts to validate.
     * @throws ValidationException If the facts are not a valid datalog program.
     */
    private void validate(String part, String facts) {
        try (var r = new StringReader(facts)) {
            var tokenizer = new DatalogTokenizer(r);
            DatalogParser.parseProgram(tokenizer);
        } catch (DatalogParseException ex) {
            LOG.warn("Failed to parse " + part + " datalog program.", ex);
            throw new ValidationException("Invalid " + part + " datalog program: " + ex.getMessage());
        }
    }
}
