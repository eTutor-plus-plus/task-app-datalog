package at.jku.dke.task_app.datalog.evaluation.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnalysisExceptionTest {
    @Test
    void constructor1(){
        final String msg = "test";
        final AnalysisException ex = new AnalysisException(msg);
        assertEquals(msg, ex.getMessage());
    }

    @Test
    void constructor2(){
        final String msg = "test";
        final Throwable cause = new Throwable();
        final AnalysisException ex = new AnalysisException(msg, cause);
        assertEquals(msg, ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
}
