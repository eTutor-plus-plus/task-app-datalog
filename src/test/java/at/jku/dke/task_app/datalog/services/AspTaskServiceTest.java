package at.jku.dke.task_app.datalog.services;

import at.jku.dke.etutor.task_app.dto.GradingDto;
import at.jku.dke.etutor.task_app.dto.ModifyTaskDto;
import at.jku.dke.etutor.task_app.dto.TaskStatus;
import at.jku.dke.task_app.datalog.data.entities.AspTask;
import at.jku.dke.task_app.datalog.dto.ModifyAspTaskDto;
import at.jku.dke.task_app.datalog.evaluation.asp.AspEvaluationService;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AspTaskServiceTest {

    //#region --- createTask ---
    @Test
    void createTask() {
        // Arrange
        var dto = new ModifyTaskDto<>(3L, BigDecimal.TEN, "asp", TaskStatus.APPROVED, new ModifyAspTaskDto("hasParent(X, Y) :- hasChild(Y, X). parent(X) :- hasParent(_, X).", 3));
        var evalService = mock(AspEvaluationService.class);
        var service = new AspTaskService(null, null, evalService);
        when(evalService.evaluate(any())).thenReturn(new GradingDto(BigDecimal.TEN, BigDecimal.TEN, "", List.of()));

        // Act
        var result = service.createTask(3, dto);

        // Assert
        assertEquals(dto.additionalData().solution(), result.getSolution());
        assertEquals(dto.additionalData().maxN(), result.getMaxN());
    }

    @Test
    void createTask_invalidType() {
        // Arrange
        var dto = new ModifyTaskDto<>(3L, BigDecimal.TEN, "xquery", TaskStatus.APPROVED, new ModifyAspTaskDto("hasParent(X, Y) :- hasChild(Y, X). parent(X) :- hasParent(_, X).", null));
        var evalService = mock(AspEvaluationService.class);
        var service = new AspTaskService(null, null, evalService);

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> service.createTask(3, dto));
    }

    @Test
    void createTask_invalidSyntax() {
        // Arrange
        var dto = new ModifyTaskDto<>(3L, BigDecimal.TEN, "asp", TaskStatus.APPROVED, new ModifyAspTaskDto("hasParent(X, Y) :- hasChild(Y, X). parent(X) :- hasParent(_, X).", null));
        var evalService = mock(AspEvaluationService.class);
        var service = new AspTaskService(null, null, evalService);
        when(evalService.evaluate(any())).thenReturn(new GradingDto(BigDecimal.TEN, BigDecimal.ZERO, "invalid syntax", List.of()));

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> service.afterCreate(new AspTask(), dto));
    }
    //#endregion

    //#region --- updateTask ---
    @Test
    void updateTask() {
        // Arrange
        var dto = new ModifyTaskDto<>(3L, BigDecimal.TEN, "asp", TaskStatus.APPROVED, new ModifyAspTaskDto("hasParent(X, Y) :- hasChild(Y, X).", 9));
        var evalService = mock(AspEvaluationService.class);
        var service = new AspTaskService(null, null, evalService);
        var task = new AspTask("hasParent(X, Y) :- hasChild(Y, X). parent(X) :- hasParent(_, X).", null);
        when(evalService.evaluate(any())).thenReturn(new GradingDto(BigDecimal.TEN, BigDecimal.TEN, "", List.of()));

        // Act
        service.updateTask(task, dto);

        // Assert
        assertEquals(dto.additionalData().solution(), task.getSolution());
        assertEquals(dto.additionalData().maxN(), task.getMaxN());
    }

    @Test
    void updateTask_invalidType() {
        // Arrange
        var dto = new ModifyTaskDto<>(3L, BigDecimal.TEN, "xquery", TaskStatus.APPROVED, new ModifyAspTaskDto("hasParent(X, Y) :- hasChild(Y, X).", 9));
        var evalService = mock(AspEvaluationService.class);
        var service = new AspTaskService(null, null, evalService);
        var task = new AspTask("hasParent(X, Y) :- hasChild(Y, X). parent(X) :- hasParent(_, X).", null);

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> service.updateTask(task, dto));
    }

    @Test
    void updateTask_invalidSyntax() {
        // Arrange
        var dto = new ModifyTaskDto<>(3L, BigDecimal.TEN, "asp", TaskStatus.APPROVED, new ModifyAspTaskDto("hasParent(X, Y) :- hasChild(Y, X).", 9));
        var evalService = mock(AspEvaluationService.class);
        var service = new AspTaskService(null, null, evalService);
        var task = new AspTask("hasParent(X, Y) :- hasChild(Y, X). parent(X) :- hasParent(_, X).", null);
        when(evalService.evaluate(any())).thenReturn(new GradingDto(BigDecimal.TEN, BigDecimal.ZERO, "invalid syntax", List.of()));

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> service.afterCreate(task, dto));
    }
    //#endregion

    @Test
    void mapToReturnData() {
        // Arrange
        var task = new AspTask("hasParent(X, Y) :- hasChild(Y, X). parent(X) :- hasParent(_, X).", 3);
        var service = new AspTaskService(null, null, null);

        // Act
        var result = service.mapToReturnData(task, true);

        // Assert
        assertNotNull(result);
    }
}
