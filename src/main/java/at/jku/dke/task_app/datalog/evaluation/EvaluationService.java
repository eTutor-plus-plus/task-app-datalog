package at.jku.dke.task_app.datalog.evaluation;

import at.jku.dke.etutor.task_app.dto.GradingDto;
import at.jku.dke.etutor.task_app.dto.SubmitSubmissionDto;

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
     */
    GradingDto evaluate(SubmitSubmissionDto<T> submission);
}
