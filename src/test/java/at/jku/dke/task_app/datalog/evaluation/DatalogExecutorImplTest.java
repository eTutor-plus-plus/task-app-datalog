package at.jku.dke.task_app.datalog.evaluation;

import at.jku.dke.task_app.datalog.config.DatalogSettings;
import at.jku.dke.task_app.datalog.evaluation.exceptions.ExecutionException;
import at.jku.dke.task_app.datalog.evaluation.exceptions.SyntaxException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class DatalogExecutorImplTest {

    @Test
    void execute() throws IOException, ExecutionException {
        // Arrange
        var executor = new DatalogExecutorImpl(DatalogSettings.EMPTY);

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
        var executor = new DatalogExecutorImpl(DatalogSettings.EMPTY);

        // Act
        var result = executor.execute("""
            arc(a1, a2).
            arc(a2, a3)

            path(X,Y) :- arc(X,Y).
            path(X,Y) :- path(X,Z), arc(Z,Y).
            """, new String[0]);

        // Assert
        assertThat(result.exitCode()).isNotEqualTo(0);
        assertThat(result.output()).contains("syntax error").contains("submission.dlv");
    }

    @Test
    void execute_invalidExePath() {
        // Arrange
        var executor = new DatalogExecutorImpl(new DatalogSettings("./some-invalid-bin", 10, "1", ""));

        // Act & Assert
        assertThrows(IOException.class, () -> executor.execute("""
            arc(a1, a2).
            arc(a2, a3).

            path(X,Y) :- arc(X,Y).
            path(X,Y) :- path(X,Z), arc(Z,Y).
            """, new String[0]));
    }

    @Test
    void execute_timeout() {
        // Arrange
        //noinspection DataFlowIssue
        var executor = new DatalogExecutorImpl(new DatalogSettings("", 0, "1", ""));

        // Act & Assert
        assertThrows(ExecutionException.class, () -> executor.execute("""
            arc(a1, a2).
            arc(a2, a3).

            path(X,Y) :- arc(X,Y).
            path(X,Y) :- path(X,Z), arc(Z,Y).
            """, new String[0]));
    }

    @Test
    void executeRules_valid() throws IOException, ExecutionException {
        // Arrange
        var executor = new DatalogExecutorImpl(DatalogSettings.EMPTY);

        // Act
        var result = executor.execute("""
            arc(a1, a2).
            arc(a2, a3).
            """, """
            path(X,Y) :- arc(X,Y).
            path(X,Y) :- path(X,Z), arc(Z,Y).
            """, null);

        // Assert
        assertThat(result)
            .contains("path(a1,a2)")
            .contains("path(a1,a3)")
            .contains("path(a2,a3)");
    }

    @Test
    void executeRules_syntaxError() {
        // Arrange
        var executor = new DatalogExecutorImpl(DatalogSettings.EMPTY);

        // Act & Assert
        assertThrows(SyntaxException.class, () -> executor.execute("""
            arc(a1, a2).
            arc(a2, a3).
            """, """
            path(X,Y) - arc(X,Y).
            path(X,Y) :- path(X,Z), arc(Z,Y).
            """, null));
    }

    @Test
    void executeQuery_valid() throws IOException, ExecutionException {
        // Arrange
        var executor = new DatalogExecutorImpl(DatalogSettings.EMPTY);

        // Act
        var result = executor.query("arc(a1, a2).arc(a2, a3).", "path(X,Y) :- arc(X,Y).path(X,Y) :- path(X,Z), arc(Z,Y).", List.of("path(X,Y)?"));

        // Assert
        assertThat(result.result()).containsEntry("path", Arrays.asList("a10, a20", "a10, a30", "a20, a30"));
    }

    @Test
    void executeQuery_twoQueries() throws IOException, ExecutionException {
        // Arrange
        var executor = new DatalogExecutorImpl(DatalogSettings.EMPTY);

        // Act
        var result = executor.query("arc(a1, a2).arc(a2, a3).", "path(X,Y) :- arc(X,Y).path(X,Y) :- path(X,Z), arc(Z,Y).", List.of("path(X,Y)?", "arc(X,Y)?"), List.of(), false);

        // Assert
        assertThat(result.result())
            .containsEntry("path", Arrays.asList("a1, a2", "a1, a3", "a2, a3"))
            .containsEntry("arc", Arrays.asList("a1, a2", "a2, a3"));
    }

    @Test
    void executeQuery_syntaxError() {
        // Arrange
        var executor = new DatalogExecutorImpl(DatalogSettings.EMPTY);

        // Act & Assert
        assertThrows(SyntaxException.class, () -> executor.query("arc(a1, a2).arc(a2, a3).", "path(X,Y) :- arc(X,Y).path(X,Y) : path(X,Z), arc(Z,Y).", List.of("path(X,Y)?")));
    }

    @Test
    void executeQuery_semanticError() {
        // Arrange
        var executor = new DatalogExecutorImpl(DatalogSettings.EMPTY);

        // Act & Assert
        assertThrowsExactly(ExecutionException.class, () -> executor.query("arc(a1, a2).arc(a2, a3).", "a(X, 1) v a(X, 2) :- arc(X).", List.of("path(X,Y)?")));
    }
}
