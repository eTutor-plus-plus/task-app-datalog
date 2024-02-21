package at.jku.dke.task_app.datalog.services;

import at.jku.dke.etutor.task_app.dto.ModifyTaskDto;
import at.jku.dke.etutor.task_app.dto.TaskStatus;
import at.jku.dke.task_app.datalog.data.entities.DatalogTask;
import at.jku.dke.task_app.datalog.data.entities.TermDescription;
import at.jku.dke.task_app.datalog.dto.ModifyDatalogTaskDto;
import at.jku.dke.task_app.datalog.evaluation.DatalogExecutorImpl;
import at.jku.dke.task_app.datalog.evaluation.exceptions.ExecutionException;
import at.jku.dke.task_app.datalog.evaluation.exceptions.SyntaxException;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class DatalogTaskServiceTest {

    //#region --- createTask ---
    @Test
    void createTask() {
        // Arrange
        var dto = new ModifyTaskDto<>(3L, BigDecimal.TEN, "datalog", TaskStatus.APPROVED, new ModifyDatalogTaskDto("hasParent(X, Y) :- hasChild(Y, X). parent(X) :- hasParent(_, X).", """
            hasChild(X, Y)?;
            parent(X)?
            """, "hasChild(mike, _)."));
        var exec = mock(DatalogExecutorImpl.class);
        var service = new DatalogTaskService(null, null, exec);

        // Act
        var result = service.createTask(3, dto);

        // Assert
        assertEquals(dto.additionalData().solution(), result.getSolution());
        assertEquals(List.of("hasChild(X, Y)?", "parent(X)?"), result.getQuery());
        assertEquals(List.of(
            new TermDescription("hasChild", "mike", 1),
            new TermDescription("hasChild", "_", 2)
        ), result.getUncheckedTerms());
    }

    @Test
    void createTask_invalidType() {
        // Arrange
        var dto = new ModifyTaskDto<>(3L, BigDecimal.TEN, "xquery", TaskStatus.APPROVED, new ModifyDatalogTaskDto("hasParent(X, Y) :- hasChild(Y, X). parent(X) :- hasParent(_, X).", """
            hasChild(X, Y)?;
            parent(X)?
            """, "hasChild(mike, _)."));
        var exec = mock(DatalogExecutorImpl.class);
        var service = new DatalogTaskService(null, null, exec);

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> service.createTask(3, dto));
    }

    @Test
    void createTask_invalidSyntax() throws IOException, ExecutionException {
        // Arrange
        var dto = new ModifyTaskDto<>(3L, BigDecimal.TEN, "datalog", TaskStatus.APPROVED, new ModifyDatalogTaskDto("hasParent(X, Y) :- hasChild(Y, X). parent(X) :- hasParent(_, X).", "hasChild(X, Y)?", "hasChild(mike, _)."));
        var exec = mock(DatalogExecutorImpl.class);
        var service = new DatalogTaskService(null, null, exec);
        when(exec.execute(anyString(), any())).thenThrow(new SyntaxException("Invalid syntax."));

        // Act & Assert
        assertThrows(ValidationException.class, () -> service.createTask(3, dto));
    }

    @Test
    void createTask_executorProblem() throws IOException, ExecutionException {
        // Arrange
        var dto = new ModifyTaskDto<>(3L, BigDecimal.TEN, "datalog", TaskStatus.APPROVED, new ModifyDatalogTaskDto("hasParent(X, Y) :- hasChild(Y, X). parent(X) :- hasParent(_, X).", "hasChild(X, Y)?", "hasChild(mike, _)."));
        var exec = mock(DatalogExecutorImpl.class);
        var service = new DatalogTaskService(null, null, exec);
        when(exec.execute(anyString(), any())).thenThrow(new ExecutionException("Invalid executor path."));

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> service.createTask(3, dto));
    }
    //#endregion

    //#region --- updateTask ---
    @Test
    void updateTask() {
        // Arrange
        var dto = new ModifyTaskDto<>(3L, BigDecimal.TEN, "datalog", TaskStatus.APPROVED, new ModifyDatalogTaskDto("hasParent(X, Y) :- hasChild(Y, X).", """
            hasChild(X)?;
            parent(X)?
            """, "hasChild(mike, _)."));
        var exec = mock(DatalogExecutorImpl.class);
        var service = new DatalogTaskService(null, null, exec);
        var task = new DatalogTask("hasParent(X, Y) :- hasChild(Y, X). parent(X) :- hasParent(_, X).", List.of("hasChild(X, Y)?", "parent(X)?"), null);

        // Act
        service.updateTask(task, dto);

        // Assert
        assertEquals(dto.additionalData().solution(), task.getSolution());
        assertEquals(List.of("hasChild(X)?", "parent(X)?"), task.getQuery());
        assertEquals(List.of(
            new TermDescription("hasChild", "mike", 1),
            new TermDescription("hasChild", "_", 2)
        ), task.getUncheckedTerms());
    }

    @Test
    void updateTask_invalidType() {
        // Arrange
        var dto = new ModifyTaskDto<>(3L, BigDecimal.TEN, "xquery", TaskStatus.APPROVED, new ModifyDatalogTaskDto("hasParent(X, Y) :- hasChild(Y, X).", """
            hasChild(X)?;
            parent(X)?
            """, "hasChild(mike, _)."));
        var exec = mock(DatalogExecutorImpl.class);
        var service = new DatalogTaskService(null, null, exec);
        var task = new DatalogTask("hasParent(X, Y) :- hasChild(Y, X). parent(X) :- hasParent(_, X).", List.of("hasChild(X, Y)?", "parent(X)?"), null);

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> service.updateTask(task, dto));
    }

    @Test
    void updateTask_invalidSyntax() throws IOException, ExecutionException {
        // Arrange
        var dto = new ModifyTaskDto<>(3L, BigDecimal.TEN, "datalog", TaskStatus.APPROVED, new ModifyDatalogTaskDto("hasParent(X, Y) :- hasChild(Y, X).", """
            hasChild(X)?;
            parent(X)?
            """, "hasChild(mike, _)."));
        var exec = mock(DatalogExecutorImpl.class);
        var service = new DatalogTaskService(null, null, exec);
        var task = new DatalogTask("hasParent(X, Y) :- hasChild(Y, X). parent(X) :- hasParent(_, X).", List.of("hasChild(X, Y)?", "parent(X)?"), null);
        when(exec.execute(anyString(), any())).thenThrow(new SyntaxException("Invalid syntax."));

        // Act & Assert
        assertThrows(ValidationException.class, () -> service.updateTask(task, dto));
    }

    @Test
    void updateTask_executorProblem() throws IOException, ExecutionException {
        // Arrange
        var dto = new ModifyTaskDto<>(3L, BigDecimal.TEN, "datalog", TaskStatus.APPROVED, new ModifyDatalogTaskDto("hasParent(X, Y) :- hasChild(Y, X).", """
            hasChild(X)?;
            parent(X)?
            """, "hasChild(mike, _)."));
        var exec = mock(DatalogExecutorImpl.class);
        var service = new DatalogTaskService(null, null, exec);
        var task = new DatalogTask("hasParent(X, Y) :- hasChild(Y, X). parent(X) :- hasParent(_, X).", List.of("hasChild(X, Y)?", "parent(X)?"), null);
        when(exec.execute(anyString(), any())).thenThrow(new IOException("Some error."));

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> service.updateTask(task, dto));
    }

    @Test
    void updateTask_notSolutionOnUnchanged() throws IOException, ExecutionException {
        // Arrange
        var dto = new ModifyTaskDto<>(3L, BigDecimal.TEN, "datalog", TaskStatus.APPROVED, new ModifyDatalogTaskDto("hasParent(X, Y) :- hasChild(Y, X).", """
            hasChild(X)?;
            parent(X)?
            """, "hasChild(mike, _)."));
        var exec = mock(DatalogExecutorImpl.class);
        var service = new DatalogTaskService(null, null, exec);
        var task = new DatalogTask("hasParent(X, Y) :- hasChild(Y, X).", List.of("hasChild(X, Y)?", "parent(X)?"), null);

        // Act
        service.updateTask(task, dto);

        // Assert
        verify(exec, times(0)).execute(anyString(), any());
    }
    //#endregion

    @Test
    void mapToReturnData() {
        // Arrange
        var task = new DatalogTask("hasParent(X, Y) :- hasChild(Y, X). parent(X) :- hasParent(_, X).", List.of("hasChild(X, Y)?", "parent(X)?"), "hasChild(mike, _).");
        var service = new DatalogTaskService(null, null, null);

        // Act
        var result = service.mapToReturnData(task, true);

        // Assert
        assertNotNull(result);
    }
}
