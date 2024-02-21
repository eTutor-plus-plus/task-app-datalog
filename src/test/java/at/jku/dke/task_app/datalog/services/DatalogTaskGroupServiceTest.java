package at.jku.dke.task_app.datalog.services;

import at.jku.dke.etutor.task_app.dto.ModifyTaskGroupDto;
import at.jku.dke.etutor.task_app.dto.TaskStatus;
import at.jku.dke.task_app.datalog.data.entities.DatalogTaskGroup;
import at.jku.dke.task_app.datalog.dto.ModifyDatalogTaskGroupDto;
import at.jku.dke.task_app.datalog.evaluation.DatalogExecutorImpl;
import at.jku.dke.task_app.datalog.evaluation.exceptions.ExecutionException;
import at.jku.dke.task_app.datalog.evaluation.exceptions.SyntaxException;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DatalogTaskGroupServiceTest {

    //#region --- createTaskGroup ---
    @Test
    void createTaskGroup() {
        // Arrange
        var dto = new ModifyTaskGroupDto<>("datalog", TaskStatus.APPROVED, new ModifyDatalogTaskGroupDto("person(mike).", "person(steve)."));
        var exec = mock(DatalogExecutorImpl.class);
        var service = new DatalogTaskGroupService(null, null, exec);

        // Act
        var result = service.createTaskGroup(1, dto);

        // Assert
        assertEquals(dto.additionalData().diagnoseFacts(), result.getDiagnoseFacts());
        assertEquals(dto.additionalData().submissionFacts(), result.getSubmissionFacts());
    }

    @Test
    void createTaskGroup_invalidType() {
        // Arrange
        var dto = new ModifyTaskGroupDto<>("xquery", TaskStatus.APPROVED, new ModifyDatalogTaskGroupDto("person(mike).", "person(steve)."));
        var exec = mock(DatalogExecutorImpl.class);
        var service = new DatalogTaskGroupService(null, null, exec);

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> service.createTaskGroup(1, dto));
    }

    @Test
    void createTaskGroup_invalidSyntax() throws IOException, ExecutionException {
        // Arrange
        var dto = new ModifyTaskGroupDto<>("datalog", TaskStatus.APPROVED, new ModifyDatalogTaskGroupDto("person(mike).", "person(steve)."));
        var exec = mock(DatalogExecutorImpl.class);
        var service = new DatalogTaskGroupService(null, null, exec);
        when(exec.execute(anyString(), any())).thenThrow(new SyntaxException("Invalid syntax."));

        // Act & Assert
        assertThrows(ValidationException.class, () -> service.createTaskGroup(1, dto));
    }

    @Test
    void createTaskGroup_executorProblem() throws IOException, ExecutionException {
        // Arrange
        var dto = new ModifyTaskGroupDto<>("datalog", TaskStatus.APPROVED, new ModifyDatalogTaskGroupDto("person(mike).", "person(steve)."));
        var exec = mock(DatalogExecutorImpl.class);
        var service = new DatalogTaskGroupService(null, null, exec);
        when(exec.execute(anyString(), any())).thenThrow(new ExecutionException("Some problem."));

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> service.createTaskGroup(1, dto));
    }
    //#endregion

    //#region --- createTaskGroup ---
    @Test
    void updateTaskGroup() {
        // Arrange
        var dto = new ModifyTaskGroupDto<>("datalog", TaskStatus.APPROVED, new ModifyDatalogTaskGroupDto("person(mike).", "person(steve)."));
        var exec = mock(DatalogExecutorImpl.class);
        var service = new DatalogTaskGroupService(null, null, exec);
        var taskGroup = new DatalogTaskGroup("diagnose", "submit");

        // Act
        service.updateTaskGroup(taskGroup, dto);

        // Assert
        assertEquals(dto.additionalData().diagnoseFacts(), taskGroup.getDiagnoseFacts());
        assertEquals(dto.additionalData().submissionFacts(), taskGroup.getSubmissionFacts());
    }

    @Test
    void updateTaskGroup_notValidateDiagnoseOnUnchanged() {
        // Arrange
        var dto = new ModifyTaskGroupDto<>("datalog", TaskStatus.APPROVED, new ModifyDatalogTaskGroupDto("diagnose", "person(steve)."));
        var exec = mock(DatalogExecutorImpl.class);
        var service = new DatalogTaskGroupService(null, null, exec);
        var taskGroup = new DatalogTaskGroup("diagnose", "person(mike).");

        // Act
        service.updateTaskGroup(taskGroup, dto);

        // Assert
        assertEquals(dto.additionalData().diagnoseFacts(), taskGroup.getDiagnoseFacts());
        assertEquals(dto.additionalData().submissionFacts(), taskGroup.getSubmissionFacts());
    }

    @Test
    void updateTaskGroup_notValidateSubmitOnUnchanged() {
        // Arrange
        var dto = new ModifyTaskGroupDto<>("datalog", TaskStatus.APPROVED, new ModifyDatalogTaskGroupDto("person(mike).", "submit"));
        var exec = mock(DatalogExecutorImpl.class);
        var service = new DatalogTaskGroupService(null, null, exec);
        var taskGroup = new DatalogTaskGroup("person(steve).", "submit");

        // Act
        service.updateTaskGroup(taskGroup, dto);

        // Assert
        assertEquals(dto.additionalData().diagnoseFacts(), taskGroup.getDiagnoseFacts());
        assertEquals(dto.additionalData().submissionFacts(), taskGroup.getSubmissionFacts());
    }

    @Test
    void updateTaskGroup_invalidType() {
        // Arrange
        var dto = new ModifyTaskGroupDto<>("xquery", TaskStatus.APPROVED, new ModifyDatalogTaskGroupDto("person(mike).", "person(steve)."));
        var exec = mock(DatalogExecutorImpl.class);
        var service = new DatalogTaskGroupService(null, null, exec);
        var taskGroup = new DatalogTaskGroup("diagnose", "submit");

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> service.updateTaskGroup(taskGroup, dto));
    }

    @Test
    void updateTaskGroup_invalidSyntax() throws IOException, ExecutionException {
        // Arrange
        var dto = new ModifyTaskGroupDto<>("datalog", TaskStatus.APPROVED, new ModifyDatalogTaskGroupDto("person(mike).", "person(steve)."));
        var exec = mock(DatalogExecutorImpl.class);
        var service = new DatalogTaskGroupService(null, null, exec);
        var taskGroup = new DatalogTaskGroup("diagnose", "submit");
        when(exec.execute(anyString(), any())).thenThrow(new SyntaxException("Invalid syntax."));

        // Act & Assert
        assertThrows(ValidationException.class, () -> service.updateTaskGroup(taskGroup, dto));
    }

    @Test
    void updateTaskGroup_executorProblem() throws IOException, ExecutionException {
        // Arrange
        var dto = new ModifyTaskGroupDto<>("datalog", TaskStatus.APPROVED, new ModifyDatalogTaskGroupDto("person(mike).", "person(steve)."));
        var exec = mock(DatalogExecutorImpl.class);
        var service = new DatalogTaskGroupService(null, null, exec);
        var taskGroup = new DatalogTaskGroup("diagnose", "submit");
        when(exec.execute(anyString(), any())).thenThrow(new IOException("Some error."));

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> service.updateTaskGroup(taskGroup, dto));
    }
    //#endregion

    @Test
    void mapToReturnData() {
        // Arrange
        MessageSource ms = mock(MessageSource.class);
        var service = new DatalogTaskGroupService(null, ms, null);
        var taskGroup = new DatalogTaskGroup("""
            empty.
            person(mike).
            person(steve). hasChild(mike, steve).
                hasParent(steve, mike).
            somePredicateWithALotOfArguments(1, ab, 5,cd,   435, 234,de,td).
            """, "submit");
        taskGroup.setId(55L);
        when(ms.getMessage(anyString(), any(), any(Locale.class))).thenAnswer(i -> i.getArgument(1, Object[].class)[1] + ":::" + i.getArgument(1, Object[].class)[0]);

        // Act
        var result = service.mapToReturnData(taskGroup, true);

        // Assert
        assertNotNull(result);
        assertThat(result.descriptionDe())
            .contains(":::")
            .contains("<li>empty</li>")
            .contains("<li>person(X)</li>")
            .contains("<li>hasChild(X, Y)</li>")
            .contains("<li>hasParent(X, Y)</li>")
            .contains("<li>somePredicateWithALotOfArguments(X, Y, Z, U, V, W, A, B)</li>");
        var id = Arrays.stream(result.descriptionDe().split(":::")).findFirst();
        assertEquals(55L, HashIds.decode(id.orElseThrow()));
    }
}
