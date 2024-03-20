package at.jku.dke.task_app.datalog.evaluation.analysis;

import java.util.List;
import java.util.Map;

public interface DatalogAnalysis {
    /**
     * Returns whether the submission result matches the solution result.
     *
     * @return {@code true} if the submission result matches the solution result; {@code false} otherwise.
     */
    boolean isCorrect();

    /**
     * Gets the solution result.
     *
     * @return The solution result.
     */
    Map<String, List<String>> getSolutionResult();

    /**
     * Sets the submission result.
     *
     * @return The submission result.
     */
    Map<String, List<String>> getSubmissionResult();

    /**
     * Returns the predicates in the correct query result, that are missing in the predicates query result.
     *
     * @return The missing predicates.
     */
    List<DatalogPredicate> getMissingPredicates();

    /**
     * Returns the facts in the correct query result, that are missing in the submitted query result.
     *
     * @return The missing facts.
     */
    List<DatalogFact> getMissingFacts();

    /**
     * Returns the facts in the submitted query result, that are not contained in the correct query result.
     *
     * @return The redundant facts.
     */
    List<DatalogFact> getSuperfluousFacts();
}
