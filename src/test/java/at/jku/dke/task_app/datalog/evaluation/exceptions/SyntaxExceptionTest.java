package at.jku.dke.task_app.datalog.evaluation.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SyntaxExceptionTest {
    @Test
    void constructor1(){
        final String msg = "test";
        final SyntaxException ex = new SyntaxException(msg);
        assertEquals(msg, ex.getMessage());
    }

    @Test
    void constructor2(){
        final String msg = "test";
        final Throwable cause = new Throwable();
        final SyntaxException ex = new SyntaxException(msg, cause);
        assertEquals(msg, ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
}
