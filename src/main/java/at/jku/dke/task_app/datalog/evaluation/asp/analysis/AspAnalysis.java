package at.jku.dke.task_app.datalog.evaluation.asp.analysis;

import at.jku.dke.task_app.datalog.evaluation.DatalogPredicate;

import java.util.List;
import java.util.Set;

public interface AspAnalysis {
    /**
     * Returns whether the submission result matches the solution result.
     *
     * @return {@code true} if the submission result matches the solution result; {@code false} otherwise.
     */
    boolean isCorrect();

    /**
     * Returns whether the submission result and the solution result have the same amount of models.
     *
     * @return {@code true} if both results have the same amount of solutions; {@code false} otherwise.
     */
    boolean hasSameAmountOfModels();

    /**
     * Gets the solution result.
     * <p>
     * Each set represents a model.
     *
     * @return The solution result.
     */
    List<Set<DatalogPredicate>> getSolutionResult();

    /**
     * Gets the submission result.
     * <p>
     * Each set represents a model.
     *
     * @return The submission result.
     */
    List<Set<DatalogPredicate>> getSubmissionResult();

    /**
     * Gets the missing models.
     *
     * @return The missing models.
     */
    List<Set<DatalogPredicate>> getMissingModels();

    /**
     * Gets the superfluous models.
     *
     * @return The superfluous models.
     */
    List<Set<DatalogPredicate>> getSuperfluousModels();
}
