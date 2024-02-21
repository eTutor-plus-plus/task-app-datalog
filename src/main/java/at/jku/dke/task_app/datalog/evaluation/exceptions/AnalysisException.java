package at.jku.dke.task_app.datalog.evaluation.exceptions;

/**
 * Exception thrown when an error occurs during Datalog analysis.
 */
public class AnalysisException extends Exception {
    /**
     * Creates a new instance of class {@linkplain AnalysisException}.
     *
     * @param message The message.
     */
    public AnalysisException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of class {@linkplain AnalysisException}.
     *
     * @param message The message.
     * @param cause   The cause of the exception.
     */
    public AnalysisException(String message, Throwable cause) {
        super(message, cause);
    }
}
