package at.jku.dke.task_app.datalog.evaluation;

import at.jku.dke.task_app.datalog.evaluation.exceptions.ExecutionException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Status;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DlvHealthIndicatorTest {

    @Test
    void health_up() throws IOException, ExecutionException {
        // Arrange
        var executor = mock(DatalogExecutor.class);
        when(executor.execute(any())).thenReturn(new DatalogExecutor.ExecutionOutput("dlg-version", 0));
        var indicator = new DlvHealthIndicator(executor);

        // Act
        var result = indicator.health();

        // Assert
        assertEquals(Status.UP, result.getStatus());
        assertEquals("dlg-version", result.getDetails().get("version"));
    }

    @Test
    void health_down() throws IOException, ExecutionException {
        // Arrange
        var executor = mock(DatalogExecutor.class);
        when(executor.execute(any())).thenReturn(new DatalogExecutor.ExecutionOutput("some error", 1));
        var indicator = new DlvHealthIndicator(executor);

        // Act
        var result = indicator.health();

        // Assert
        assertEquals(Status.DOWN, result.getStatus());
        assertEquals(1, result.getDetails().get("exitCode"));
        assertEquals("some error", result.getDetails().get("output"));
    }

    @Test
    void health_exception() throws IOException, ExecutionException {
        // Arrange
        var executor = mock(DatalogExecutor.class);
        when(executor.execute(any())).thenThrow(new FileNotFoundException("file dlv.exe not found"));
        var indicator = new DlvHealthIndicator(executor);

        // Act
        var result = indicator.health();

        // Assert
        assertEquals(Status.DOWN, result.getStatus());
        assertEquals("java.io.FileNotFoundException: file dlv.exe not found", result.getDetails().get("error"));
    }

    @Test
    void health_cache() throws IOException, ExecutionException {
        // Arrange
        var executor = mock(DatalogExecutor.class);
        when(executor.execute(any())).thenReturn(new DatalogExecutor.ExecutionOutput("dlg-version", 0));
        var indicator = new DlvHealthIndicator(executor);

        // Act
        var result = indicator.health();
        var result2 = indicator.health();

        // Assert
        assertEquals(result, result2);
        verify(executor, times(1)).execute(any());
    }

}
