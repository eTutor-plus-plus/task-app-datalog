package at.jku.dke.task_app.datalog.services;

import at.jku.dke.etutor.task_app.dto.GradingDto;
import at.jku.dke.etutor.task_app.dto.ModifyTaskDto;
import at.jku.dke.etutor.task_app.dto.TaskStatus;
import at.jku.dke.task_app.datalog.data.entities.DatalogTask;
import at.jku.dke.task_app.datalog.data.entities.TermDescription;
import at.jku.dke.task_app.datalog.dto.ModifyDatalogTaskDto;
import at.jku.dke.task_app.datalog.evaluation.dlg.DatalogEvaluationService;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DatalogTaskServiceTest {

    //#region --- createTask ---
    @Test
    void createTask() {
        // Arrange
        var dto = new ModifyTaskDto<>(3L, BigDecimal.TEN, "datalog", TaskStatus.APPROVED, new ModifyDatalogTaskDto("hasParent(X, Y) :- hasChild(Y, X). parent(X) :- hasParent(_, X).", """
            hasChild(X, Y)?;
            parent(X)?
            """, "hasChild(mike, _)."));
        var evalService = mock(DatalogEvaluationService.class);
        var service = new DatalogTaskService(null, null, evalService);
        when(evalService.evaluate(any())).thenReturn(new GradingDto(BigDecimal.TEN, BigDecimal.TEN, "", List.of()));

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
        var evalService = mock(DatalogEvaluationService.class);
        var service = new DatalogTaskService(null, null, evalService);

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> service.createTask(3, dto));
    }

    @Test
    void createTask_invalidSyntax() {
        // Arrange
        var dto = new ModifyTaskDto<>(3L, BigDecimal.TEN, "datalog", TaskStatus.APPROVED, new ModifyDatalogTaskDto("hasParent(X, Y) :- hasChild(Y, X). parent(X) :- hasParent(_, X).", "hasChild(X, Y)?", "hasChild(mike, _)."));
        var evalService = mock(DatalogEvaluationService.class);
        var service = new DatalogTaskService(null, null, evalService);
        when(evalService.evaluate(any())).thenReturn(new GradingDto(BigDecimal.TEN, BigDecimal.ZERO, "invalid syntax", List.of()));

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> service.afterCreate(new DatalogTask(), dto));
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
        var evalService = mock(DatalogEvaluationService.class);
        var service = new DatalogTaskService(null, null, evalService);
        var task = new DatalogTask("hasParent(X, Y) :- hasChild(Y, X). parent(X) :- hasParent(_, X).", List.of("hasChild(X, Y)?", "parent(X)?"), null);
        when(evalService.evaluate(any())).thenReturn(new GradingDto(BigDecimal.TEN, BigDecimal.TEN, "", List.of()));

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
        var evalService = mock(DatalogEvaluationService.class);
        var service = new DatalogTaskService(null, null, evalService);
        var task = new DatalogTask("hasParent(X, Y) :- hasChild(Y, X). parent(X) :- hasParent(_, X).", List.of("hasChild(X, Y)?", "parent(X)?"), null);

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> service.updateTask(task, dto));
    }

    @Test
    void updateTask_invalidSyntax() {
        // Arrange
        var dto = new ModifyTaskDto<>(3L, BigDecimal.TEN, "datalog", TaskStatus.APPROVED, new ModifyDatalogTaskDto("hasParent(X, Y) :- hasChild(Y, X).", """
            hasChild(X)?;
            parent(X)?
            """, "hasChild(mike, _)."));
        var evalService = mock(DatalogEvaluationService.class);
        var service = new DatalogTaskService(null, null, evalService);
        var task = new DatalogTask("hasParent(X, Y) :- hasChild(Y, X). parent(X) :- hasParent(_, X).", List.of("hasChild(X, Y)?", "parent(X)?"), null);
        when(evalService.evaluate(any())).thenReturn(new GradingDto(BigDecimal.TEN, BigDecimal.ZERO, "invalid syntax", List.of()));

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> service.afterCreate(task, dto));
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
