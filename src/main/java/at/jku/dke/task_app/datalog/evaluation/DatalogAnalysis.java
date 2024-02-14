package at.jku.dke.task_app.datalog.evaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Service that evaluates the datalog execution results.
 */
class DatalogAnalysis {

    private final Map<String, List<String>> solutionResult;
    private final Map<String, List<String>> submissionResult;
    private final List<Object> missingPredicates;
    private final List<Object> missingFacts;
    private final List<Object> redundantFacts;
    private boolean isCorrect;

    /**
     * Creates a new instance of class {@link DatalogAnalysis}.
     *
     * @param solutionResult   The solution result.
     * @param submissionResult The student result.
     */
    public DatalogAnalysis(Map<String, List<String>> solutionResult, Map<String, List<String>> submissionResult) {
        this.solutionResult = Collections.unmodifiableMap(solutionResult);
        this.submissionResult = Collections.unmodifiableMap(submissionResult);
        this.missingPredicates = new ArrayList<>();
        this.missingFacts = new ArrayList<>();
        this.redundantFacts = new ArrayList<>();
        this.isCorrect = false;
    }

    /**
     * Returns whether the submission result matches the solution result.
     *
     * @return {@code true} if the submission result matches the solution result; {@code false} otherwise.
     */
    public boolean isCorrect() {
        return isCorrect;
    }

    /**
     * Gets the solution result.
     *
     * @return The solution result.
     */
    public Map<String, List<String>> getSolutionResult() {
        return solutionResult;
    }

    /**
     * Sets the submission result.
     *
     * @return The submission result.
     */
    public Map<String, List<String>> getSubmissionResult() {
        return submissionResult;
    }

    /**
     * Gets the missing predicates.
     *
     * @return The missing predicates.
     */
    public List<Object> getMissingPredicates() {
        return missingPredicates;
    }

    /**
     * Gets the missing facts.
     *
     * @return The missing facts.
     */
    public List<Object> getMissingFacts() {
        return missingFacts;
    }

    /**
     * Gets the redundant facts.
     *
     * @return The redundant facts.
     */
    public List<Object> getRedundantFacts() {
        return redundantFacts;
    }
}
