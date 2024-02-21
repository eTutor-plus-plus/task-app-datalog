package at.jku.dke.task_app.datalog.evaluation;

import at.jku.dke.etutor.task_app.dto.SubmissionMode;
import at.jku.dke.etutor.task_app.dto.SubmitSubmissionDto;
import at.jku.dke.task_app.datalog.data.entities.DatalogTask;
import at.jku.dke.task_app.datalog.data.entities.DatalogTaskGroup;
import at.jku.dke.task_app.datalog.data.repositories.DatalogTaskRepository;
import at.jku.dke.task_app.datalog.dto.DatalogSubmissionDto;
import at.jku.dke.task_app.datalog.evaluation.exceptions.ExecutionException;
import at.jku.dke.task_app.datalog.evaluation.exceptions.SyntaxException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
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

class EvaluationServiceTest {

    @Test
    void evaluate_notExistingTask() {
        // Arrange
        var repository = mock(DatalogTaskRepository.class);
        var ms = mock(MessageSource.class);
        var exec = mock(DatalogExecutor.class);
        var service = new EvaluationService(repository, ms, exec);

        when(repository.findByIdWithTaskGroup(any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> service.evaluate(new SubmitSubmissionDto<>(null, null, 1L, "de", SubmissionMode.SUBMIT, 1, new DatalogSubmissionDto(""))));
    }

    @Test
    void evaluate_solution_executionError() throws IOException, ExecutionException {
        // Arrange
        var repository = mock(DatalogTaskRepository.class);
        var ms = mock(MessageSource.class);
        var exec = mock(DatalogExecutor.class);
        var service = new EvaluationService(repository, ms, exec);
        var task = new DatalogTask("mySolution", List.of("myquery"), null);
        task.setId(1L);
        task.setTaskGroup(new DatalogTaskGroup("diagnose", "submit"));

        when(repository.findByIdWithTaskGroup(any())).thenReturn(Optional.of(task));
        when(exec.execute(anyString(), anyString(), any(), any(), anyBoolean())).thenThrow(new ExecutionException(""));

        // Act & Assert
        assertThrows(ResponseStatusException.class,
            () -> service.evaluate(new SubmitSubmissionDto<>(null, null, 1L, "de", SubmissionMode.SUBMIT, 1, new DatalogSubmissionDto(""))),
            "Error while evaluating solution for task 1");
    }

    @Test
    void evaluate_submission_executionError() throws IOException, ExecutionException {
        // Arrange
        var repository = mock(DatalogTaskRepository.class);
        var ms = mock(MessageSource.class);
        var exec = mock(DatalogExecutor.class);
        var service = new EvaluationService(repository, ms, exec);
        var task = new DatalogTask("mySolution", List.of("myquery"), null);
        task.setTaskGroup(new DatalogTaskGroup("diagnose", "submit"));
        task.setId(1L);

        when(repository.findByIdWithTaskGroup(any())).thenReturn(Optional.of(task));
        when(exec.execute(anyString(), eq("myInput"), any(), any(), anyBoolean())).thenThrow(new ExecutionException(""));

        // Act & Assert
        assertThrows(ResponseStatusException.class,
            () -> service.evaluate(new SubmitSubmissionDto<>(null, null, 1L, "de", SubmissionMode.SUBMIT, 1, new DatalogSubmissionDto("myInput"))),
            "Error while evaluating input for task 1");
    }

    @Test
    void evaluate_submission_syntaxError() throws IOException, ExecutionException {
        // Arrange
        var repository = mock(DatalogTaskRepository.class);
        var ms = mock(MessageSource.class);
        var exec = mock(DatalogExecutor.class);
        var service = new EvaluationService(repository, ms, exec);
        var task = new DatalogTask("mySolution", List.of("myquery"), null);
        task.setTaskGroup(new DatalogTaskGroup("diagnose", "submit"));
        task.setId(1L);

        when(repository.findByIdWithTaskGroup(any())).thenReturn(Optional.of(task));
        when(exec.execute(anyString(), eq("myInput"), any(), any(), anyBoolean())).thenThrow(new SyntaxException("Syntax"));

        // Act
        var result = service.evaluate(new SubmitSubmissionDto<>(null, null, 1L, "de", SubmissionMode.DIAGNOSE, 1, new DatalogSubmissionDto("myInput")));

        // Assert
        assertEquals(BigDecimal.ZERO, result.points());
        assertEquals(1, result.criteria().size());
        assertFalse(result.criteria().getFirst().passed());
        assertEquals("<pre>Syntax</pre>", result.criteria().getFirst().feedback());
    }

    @Test
    void evaluate_analysisError() throws IOException, ExecutionException {
        // Arrange
        var repository = mock(DatalogTaskRepository.class);
        var ms = mock(MessageSource.class);
        var exec = mock(DatalogExecutor.class);
        var service = new EvaluationService(repository, ms, exec);
        var task = new DatalogTask("mySolution", List.of("myquery"), null);
        task.setTaskGroup(new DatalogTaskGroup("diagnose", "submit"));
        task.setId(1L);

        when(repository.findByIdWithTaskGroup(any())).thenReturn(Optional.of(task));
        when(exec.execute(anyString(), anyString(), any(), any(), anyBoolean())).thenReturn(new DatalogExecutorImpl.ExecutionResult("", Map.of("p1", List.of("a", "a,b"))));

        // Act & Assert
        assertThrows(ResponseStatusException.class,
            () -> service.evaluate(new SubmitSubmissionDto<>(null, null, 1L, "de", SubmissionMode.DIAGNOSE, 1, new DatalogSubmissionDto("myInput"))),
            "Error while analysing query result for task 1");
    }

    @Test
    void evaluate_submit() throws IOException, ExecutionException {
        // Arrange
        var repository = mock(DatalogTaskRepository.class);
        var ms = mock(MessageSource.class);
        var exec = mock(DatalogExecutor.class);
        var service = new EvaluationService(repository, ms, exec);
        var task = new DatalogTask("mySolution", List.of("myquery"), null);
        task.setTaskGroup(new DatalogTaskGroup("diagnose", "submit"));
        task.setId(1L);

        when(repository.findByIdWithTaskGroup(any())).thenReturn(Optional.of(task));
        when(exec.execute(anyString(), anyString(), any(), any(), anyBoolean())).thenReturn(new DatalogExecutorImpl.ExecutionResult("", Map.of()));

        // Act
        service.evaluate(new SubmitSubmissionDto<>(null, null, 1L, "de", SubmissionMode.SUBMIT, 1, new DatalogSubmissionDto("myInput")));

        // Assert
        verify(exec, times(1)).execute(eq("submit"), eq("mySolution"), eq(List.of("myquery")), eq(List.of()), eq(true));
        verify(exec, times(1)).execute(eq("submit"), eq("myInput"), eq(List.of("myquery")), eq(List.of()), eq(true));
    }

    @Test
    void evaluate_diagnose() throws IOException, ExecutionException {
        // Arrange
        var repository = mock(DatalogTaskRepository.class);
        var ms = mock(MessageSource.class);
        var exec = mock(DatalogExecutor.class);
        var service = new EvaluationService(repository, ms, exec);
        var task = new DatalogTask("mySolution", List.of("myquery"), null);
        task.setTaskGroup(new DatalogTaskGroup("diagnose", "submit"));
        task.setId(1L);

        when(repository.findByIdWithTaskGroup(any())).thenReturn(Optional.of(task));
        when(exec.execute(anyString(), anyString(), any(), any(), anyBoolean())).thenReturn(new DatalogExecutorImpl.ExecutionResult("", Map.of()));

        // Act
        service.evaluate(new SubmitSubmissionDto<>(null, null, 1L, "de", SubmissionMode.DIAGNOSE, 1, new DatalogSubmissionDto("myInput")));

        // Assert
        verify(exec, times(1)).execute(eq("diagnose"), eq("mySolution"), eq(List.of("myquery")), eq(List.of()), eq(false));
        verify(exec, times(1)).execute(eq("diagnose"), eq("myInput"), eq(List.of("myquery")), eq(List.of()), eq(false));
    }

    @Test
    void evaluate_run() throws IOException, ExecutionException {
        // Arrange
        var repository = mock(DatalogTaskRepository.class);
        var ms = mock(MessageSource.class);
        var exec = mock(DatalogExecutor.class);
        var service = new EvaluationService(repository, ms, exec);
        var task = new DatalogTask("mySolution", List.of("myquery"), null);
        task.setTaskGroup(new DatalogTaskGroup("diagnose", "submit"));
        task.setId(1L);

        when(repository.findByIdWithTaskGroup(any())).thenReturn(Optional.of(task));
        when(exec.execute(anyString(), anyString(), any(), any(), anyBoolean())).thenReturn(new DatalogExecutorImpl.ExecutionResult("", Map.of()));

        // Act
        service.evaluate(new SubmitSubmissionDto<>(null, null, 1L, "de", SubmissionMode.RUN, 1, new DatalogSubmissionDto("myInput")));

        // Assert
        verify(exec, times(0)).execute(eq("diagnose"), eq("mySolution"), eq(List.of("myquery")), eq(List.of()), eq(false));
        verify(exec, times(1)).execute(eq("diagnose"), eq("myInput"), eq(List.of("myquery")), eq(List.of()), eq(false));
    }

}
