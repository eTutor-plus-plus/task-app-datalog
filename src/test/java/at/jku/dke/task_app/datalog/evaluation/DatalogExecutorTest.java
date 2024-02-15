package at.jku.dke.task_app.datalog.evaluation;

import at.jku.dke.task_app.datalog.config.DatalogSettings;
import at.jku.dke.task_app.datalog.evaluation.exceptions.ExecutionException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class DatalogExecutorTest {

    @Test
    void execute() throws IOException, ExecutionException {
        // Arrange
        var executor = new DatalogExecutor(new DatalogSettings());

        // Act
        var result = executor.execute("""
            arc(a1, a2).
            arc(a2, a3).

            path(X,Y) :- arc(X,Y).
            path(X,Y) :- path(X,Z), arc(Z,Y).
            """, new String[0]);

        // Assert
        assertThat(result.exitCode()).isEqualTo(0);
        assertThat(result.output())
            .contains("arc(a1,a2)")
            .contains("arc(a2,a3)")
            .contains("path(a1,a2)")
            .contains("path(a1,a3)")
            .contains("path(a2,a3)");
    }

    @Test
    void execute_syntaxError() throws IOException, ExecutionException {
        // Arrange
        var executor = new DatalogExecutor(new DatalogSettings());

        // Act
        var result = executor.execute("""
            arc(a1, a2).
            arc(a2, a3)

            path(X,Y) :- arc(X,Y).
            path(X,Y) :- path(X,Z), arc(Z,Y).
            """, new String[0]);

        // Assert
        assertThat(result.exitCode()).isNotEqualTo(0);
        assertThat(result.output()).contains("syntax error");
    }

    @Test
    void execute_invalidExePath() {
        // Arrange
        var executor = new DatalogExecutor(new DatalogSettings("./some-invalid-bin", 10, "1"));

        // Act & Assert
        assertThrows(IOException.class, () -> executor.execute("""
            arc(a1, a2).
            arc(a2, a3).

            path(X,Y) :- arc(X,Y).
            path(X,Y) :- path(X,Z), arc(Z,Y).
            """, new String[0]));
    }

    @Test
    void executeQuery() throws IOException, ExecutionException {
        // Arrange
        var executor = new DatalogExecutor(new DatalogSettings());

        // Act
        var result = executor.execute("arc(a1, a2).arc(a2, a3).", "path(X,Y) :- arc(X,Y).path(X,Y) :- path(X,Z), arc(Z,Y).", List.of("path(X,Y)?"));

        // Assert
        assertThat(result.result()).containsEntry("path", Arrays.asList("a10, a20", "a10, a30", "a20, a30"));
    }

}
