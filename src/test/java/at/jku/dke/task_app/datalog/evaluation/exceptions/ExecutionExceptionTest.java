package at.jku.dke.task_app.datalog.evaluation.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExecutionExceptionTest {
    @Test
    void constructor1() {
        final String msg = "test";
        final ExecutionException ex = new ExecutionException(msg);
        assertEquals(msg, ex.getMessage());
    }

    @Test
    void constructor2() {
        final String msg = "test";
        final Throwable cause = new Throwable();
        final ExecutionException ex = new ExecutionException(msg, cause);
        assertEquals(msg, ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
}
