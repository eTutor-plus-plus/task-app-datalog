package at.jku.dke.task_app.datalog.evaluation;

/**
 * Exception that is thrown if a syntax error occurs.
 */
public class SyntaxException extends ExecutionException {

    /**
     * Creates a new instance of class {@linkplain SyntaxException}.
     *
     * @param message The message.
     */
    public SyntaxException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of class {@linkplain SyntaxException}.
     *
     * @param message The message.
     * @param cause   The cause.
     */
    public SyntaxException(String message, Throwable cause) {
        super(message, cause);
    }

}
