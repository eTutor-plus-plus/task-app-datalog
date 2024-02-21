package at.jku.dke.task_app.datalog.evaluation.exceptions;

/**
 * Exception thrown when an error occurs during Datalog execution.
 */
public class ExecutionException extends Exception {
    /**
     * Creates a new instance of class {@linkplain ExecutionException}.
     *
     * @param message The message.
     */
    public ExecutionException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of class {@linkplain ExecutionException}.
     *
     * @param message The message.
     * @param cause   The cause of the exception.
     */
    public ExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
