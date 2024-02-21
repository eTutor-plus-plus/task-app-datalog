package at.jku.dke.task_app.datalog.evaluation;

import at.jku.dke.task_app.datalog.data.entities.TermDescription;
import at.jku.dke.task_app.datalog.evaluation.exceptions.ExecutionException;
import at.jku.dke.task_app.datalog.evaluation.exceptions.SyntaxException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Interface for the datalog executor.
 */
public interface DatalogExecutor {
    /**
     * Executes the datalog binary with the given input.
     *
     * @param input The input for the datalog binary.
     * @param args  Additional arguments for the datalog binary (e.g. -cautious).
     * @return The output of the datalog binary. The first element of the pair is the output, the second element is the exit code.
     * @throws IOException        If an I/O error occurs.
     * @throws ExecutionException If the process execution fails.
     */
    ExecutionOutput execute(String input, String[] args) throws IOException, ExecutionException;

    /**
     * Executes the datalog binary with the given input.
     *
     * @param facts   The datalog facts from the task group.
     * @param rules   The datalog rules from the submission.
     * @param queries The datalog queries from the task.
     * @return The result of the datalog execution.
     * @throws IOException        If an I/O error occurs.
     * @throws ExecutionException If the process execution fails.
     * @throws SyntaxException    If the datalog execution fails with a syntax error.
     */
    ExecutionResult execute(String facts, String rules, List<String> queries) throws IOException, ExecutionException;

    /**
     * Executes the datalog binary with the given input.
     *
     * @param facts          The datalog facts from the task group.
     * @param rules          The datalog rules from the submission.
     * @param queries        The datalog queries from the task.
     * @param uncheckedTerms The unchecked terms from the task.
     * @param encodeFacts    Whether the facts (except in unchecked terms) should be encoded.
     * @return The result of the datalog execution.
     * @throws IOException        If an I/O error occurs.
     * @throws ExecutionException If the process execution fails.
     * @throws SyntaxException    If the datalog execution fails with a syntax error.
     */
    ExecutionResult execute(String facts, String rules, List<String> queries, List<TermDescription> uncheckedTerms, boolean encodeFacts) throws IOException, ExecutionException;

    /**
     * Represents the output of an execution.
     *
     * @param output   The output.
     * @param exitCode The exit code.
     */
    record ExecutionOutput(String output, int exitCode) {
    }

    /**
     * Represents the result of an execution.
     *
     * @param output The output.
     * @param result The parsed query result. The key of the map is the predicate of the query, the value is the output of the query.
     */
    record ExecutionResult(String output, Map<String, List<String>> result) {
    }
}
