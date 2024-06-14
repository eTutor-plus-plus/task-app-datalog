package at.jku.dke.task_app.datalog.evaluation;

import at.jku.dke.etutor.task_app.dto.GradingDto;
import at.jku.dke.etutor.task_app.dto.SubmitSubmissionDto;
import at.jku.dke.task_app.datalog.dto.DatalogSubmissionDto;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service for evaluating submissions.
 *
 * @param <T> The type of the submission.
 */
public interface EvaluationService<T> {
    /**
     * Evaluates a datalog input.
     *
     * @param submission The input to evaluate.
     * @return The evaluation result.
     * @throws ResponseStatusException If an internal error occurs.
     */
    GradingDto evaluate(SubmitSubmissionDto<T> submission);

    /**
     * Executes the submission and returns the query result.
     *
     * @param submission The submission data.
     * @return The query result.
     * @throws ResponseStatusException If an error occurs.
     */
    DatalogExecutor.ExecutionResult execute(SubmitSubmissionDto<DatalogSubmissionDto> submission);
}
