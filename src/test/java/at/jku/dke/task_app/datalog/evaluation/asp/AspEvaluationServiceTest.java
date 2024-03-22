package at.jku.dke.task_app.datalog.evaluation.asp;

import at.jku.dke.etutor.task_app.dto.SubmissionMode;
import at.jku.dke.etutor.task_app.dto.SubmitSubmissionDto;
import at.jku.dke.task_app.datalog.data.entities.AspTask;
import at.jku.dke.task_app.datalog.data.entities.DatalogTaskGroup;
import at.jku.dke.task_app.datalog.data.repositories.AspTaskRepository;
import at.jku.dke.task_app.datalog.dto.AspSubmissionDto;
import at.jku.dke.task_app.datalog.evaluation.DatalogExecutor;
import at.jku.dke.task_app.datalog.evaluation.exceptions.ExecutionException;
import at.jku.dke.task_app.datalog.evaluation.exceptions.SyntaxException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AspEvaluationServiceTest {

    @Test
    void evaluate_notExistingTask() {
        // Arrange
        var repository = mock(AspTaskRepository.class);
        var ms = mock(MessageSource.class);
        var exec = Mockito.mock(DatalogExecutor.class);
        var service = new AspEvaluationService(repository, ms, exec);

        when(repository.findByIdWithTaskGroup(any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> service.evaluate(new SubmitSubmissionDto<>(null, null, 1L, "de", SubmissionMode.SUBMIT, 1, new AspSubmissionDto(""))));
    }

    @Test
    void evaluate_solution_executionError() throws IOException, ExecutionException {
        // Arrange
        var repository = mock(AspTaskRepository.class);
        var ms = mock(MessageSource.class);
        var exec = mock(DatalogExecutor.class);
        var service = new AspEvaluationService(repository, ms, exec);
        var task = new AspTask("mySolution", null);
        task.setId(1L);
        task.setTaskGroup(new DatalogTaskGroup("diagnose", "submit"));

        when(repository.findByIdWithTaskGroup(any())).thenReturn(Optional.of(task));
        when(exec.execute(anyString(), eq("myInput"), any())).thenReturn("{}");
        when(exec.execute(anyString(), eq("mySolution"), any())).thenThrow(new ExecutionException(""));

        // Act & Assert
        assertThrows(ResponseStatusException.class,
            () -> service.evaluate(new SubmitSubmissionDto<>(null, null, 1L, "de", SubmissionMode.SUBMIT, 1, new AspSubmissionDto("myInput"))),
            "Error while evaluating solution for task 1");
    }

    @Test
    void evaluate_submission_executionError() throws IOException, ExecutionException {
        // Arrange
        var repository = mock(AspTaskRepository.class);
        var ms = mock(MessageSource.class);
        var exec = mock(DatalogExecutor.class);
        var service = new AspEvaluationService(repository, ms, exec);
        var task = new AspTask("mySolution", null);
        task.setTaskGroup(new DatalogTaskGroup("diagnose", "submit"));
        task.setId(1L);

        when(repository.findByIdWithTaskGroup(any())).thenReturn(Optional.of(task));
        when(exec.execute(anyString(), eq("myInput"), any())).thenThrow(new ExecutionException(""));

        // Act & Assert
        assertThrows(ResponseStatusException.class,
            () -> service.evaluate(new SubmitSubmissionDto<>(null, null, 1L, "de", SubmissionMode.SUBMIT, 1, new AspSubmissionDto("myInput"))),
            "Error while evaluating input for task 1");
    }

    @Test
    void evaluate_submission_syntaxError() throws IOException, ExecutionException {
        // Arrange
        var repository = mock(AspTaskRepository.class);
        var ms = mock(MessageSource.class);
        var exec = mock(DatalogExecutor.class);
        var service = new AspEvaluationService(repository, ms, exec);
        var task = new AspTask("mySolution", null);
        task.setTaskGroup(new DatalogTaskGroup("diagnose", "submit"));
        task.setId(1L);

        when(repository.findByIdWithTaskGroup(any())).thenReturn(Optional.of(task));
        when(exec.execute(anyString(), eq("myInput"), any())).thenThrow(new SyntaxException("Syntax"));

        // Act
        var result = service.evaluate(new SubmitSubmissionDto<>(null, null, 1L, "de", SubmissionMode.DIAGNOSE, 1, new AspSubmissionDto("myInput")));

        // Assert
        assertEquals(BigDecimal.ZERO, result.points());
        assertEquals(1, result.criteria().size());
        assertFalse(result.criteria().getFirst().passed());
        assertEquals("<pre>Syntax</pre>", result.criteria().getFirst().feedback());
    }

    @Test
    void evaluate_analysisError() throws IOException, ExecutionException {
        // Arrange
        var repository = mock(AspTaskRepository.class);
        var ms = mock(MessageSource.class);
        var exec = mock(DatalogExecutor.class);
        var service = new AspEvaluationService(repository, ms, exec);
        var task = new AspTask("mySolution", null);
        task.setTaskGroup(new DatalogTaskGroup("diagnose", "submit"));
        task.setId(1L);

        when(repository.findByIdWithTaskGroup(any())).thenReturn(Optional.of(task));
        when(exec.execute(anyString(), anyString(), any())).thenReturn("{p1(a, b). p1(a).}");

        // Act & Assert
        assertThrows(ResponseStatusException.class,
            () -> service.evaluate(new SubmitSubmissionDto<>(null, null, 1L, "de", SubmissionMode.DIAGNOSE, 1, new AspSubmissionDto("myInput"))),
            "Error while analysing query result for task 1");
    }

    @Test
    void evaluate_submit() throws IOException, ExecutionException {
        // Arrange
        var repository = mock(AspTaskRepository.class);
        var ms = mock(MessageSource.class);
        var exec = mock(DatalogExecutor.class);
        var service = new AspEvaluationService(repository, ms, exec);
        var task = new AspTask("mySolution", 10);
        task.setTaskGroup(new DatalogTaskGroup("diagnose", "submit"));
        task.setId(1L);

        when(repository.findByIdWithTaskGroup(any())).thenReturn(Optional.of(task));
        when(exec.execute(anyString(), anyString(), any())).thenReturn("{}");

        // Act
        service.evaluate(new SubmitSubmissionDto<>(null, null, 1L, "de", SubmissionMode.SUBMIT, 1, new AspSubmissionDto("myInput")));

        // Assert
        verify(exec, times(1)).execute(eq("submit"), eq("mySolution"), eq(10));
        verify(exec, times(1)).execute(eq("submit"), eq("myInput"), eq(10));
    }

    @Test
    void evaluate_diagnose() throws IOException, ExecutionException {
        // Arrange
        var repository = mock(AspTaskRepository.class);
        var ms = mock(MessageSource.class);
        var exec = mock(DatalogExecutor.class);
        var service = new AspEvaluationService(repository, ms, exec);
        var task = new AspTask("mySolution", 10);
        task.setTaskGroup(new DatalogTaskGroup("diagnose", "submit"));
        task.setId(1L);

        when(repository.findByIdWithTaskGroup(any())).thenReturn(Optional.of(task));
        when(exec.execute(anyString(), anyString(), any())).thenReturn("{}");

        // Act
        service.evaluate(new SubmitSubmissionDto<>(null, null, 1L, "de", SubmissionMode.DIAGNOSE, 1, new AspSubmissionDto("myInput")));

        // Assert
        verify(exec, times(1)).execute(eq("diagnose"), eq("mySolution"), eq(10));
        verify(exec, times(1)).execute(eq("diagnose"), eq("myInput"), eq(10));
    }

    @Test
    void evaluate_run() throws IOException, ExecutionException {
        // Arrange
        var repository = mock(AspTaskRepository.class);
        var ms = mock(MessageSource.class);
        var exec = mock(DatalogExecutor.class);
        var service = new AspEvaluationService(repository, ms, exec);
        var task = new AspTask("mySolution", 10);
        task.setTaskGroup(new DatalogTaskGroup("diagnose", "submit"));
        task.setId(1L);

        when(repository.findByIdWithTaskGroup(any())).thenReturn(Optional.of(task));
        when(exec.execute(anyString(), anyString(), any())).thenReturn("{}");

        // Act
        service.evaluate(new SubmitSubmissionDto<>(null, null, 1L, "de", SubmissionMode.RUN, 1, new AspSubmissionDto("myInput")));

        // Assert
        verify(exec, times(0)).execute(eq("diagnose"), eq("mySolution"), eq(10));
        verify(exec, times(1)).execute(eq("diagnose"), eq("myInput"), eq(10));
    }

}
