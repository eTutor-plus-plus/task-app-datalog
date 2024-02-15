package at.jku.dke.task_app.datalog.evaluation.analysis;

import at.jku.dke.task_app.datalog.evaluation.exceptions.AnalysisException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Service that evaluates the datalog execution results.
 */
public class DatalogAnalysis {

    private final Map<String, List<String>> solutionResult;
    private final Map<String, List<String>> submissionResult;
    private final List<DatalogPredicate> missingPredicates;
    private final List<DatalogFact> missingFacts;
    private final List<DatalogFact> redundantFacts;
    private boolean isCorrect;

    /**
     * Creates a new instance of class {@link DatalogAnalysis}.
     *
     * @param solutionResult   The solution result.
     * @param submissionResult The student result.
     * @throws AnalysisException If the analysis fails.
     */
    public DatalogAnalysis(Map<String, List<String>> solutionResult, Map<String, List<String>> submissionResult) throws AnalysisException {
        this.solutionResult = Collections.unmodifiableMap(solutionResult);
        this.submissionResult = Collections.unmodifiableMap(submissionResult);
        this.missingPredicates = new ArrayList<>();
        this.missingFacts = new ArrayList<>();
        this.redundantFacts = new ArrayList<>();
        this.isCorrect = false;
        this.analyze();
    }

    /**
     * Returns whether the submission result matches the solution result.
     *
     * @return {@code true} if the submission result matches the solution result; {@code false} otherwise.
     */
    public boolean isCorrect() {
        return isCorrect;
    }

    //#region --- GETTER ---

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
     * Returns the predicates in the correct query result, that are missing in the predicates query result.
     *
     * @return The missing predicates.
     */
    public List<DatalogPredicate> getMissingPredicates() {
        return missingPredicates;
    }

    /**
     * Returns the facts in the correct query result, that are missing in the submitted query result.
     *
     * @return The missing facts.
     */
    public List<DatalogFact> getMissingFacts() {
        return missingFacts;
    }

    /**
     * Returns the facts in the submitted query result, that are not contained in the correct query result.
     *
     * @return The redundant facts.
     */
    public List<DatalogFact> getRedundantFacts() {
        return redundantFacts;
    }

    //#endregion

    /**
     * Analyzes the result.
     *
     * @throws AnalysisException If the analysis fails.
     */
    private void analyze() throws AnalysisException {
        var solutionModel = new DatalogModel(this.solutionResult);
        var submissionModel = new DatalogModel(this.submissionResult);

        if (!solutionModel.isConsistent() || !submissionModel.isConsistent()) {
            this.isCorrect = false;
            throw new AnalysisException("Analysis stopped, as one of the results is inconsistent.");
        }

        for (DatalogPredicate predSolution : solutionModel.getPredicates()) {
            var predSubmission = submissionModel.getPredicate(predSolution.getName());
            if (predSubmission.isEmpty() || predSubmission.get().getFacts().isEmpty()) {
                this.missingPredicates.add(predSolution);
            } else {
                this.comparePredicates(predSolution, predSubmission.get());
            }
        }

        this.isCorrect = this.missingFacts.isEmpty() && this.redundantFacts.isEmpty() && this.missingPredicates.isEmpty();
    }

    /**
     * Compares two predicates with regard to differences concerning their facts.
     *
     * @param predSolution   The solution predicate.
     * @param predSubmission The submission predicate.
     */
    private void comparePredicates(DatalogPredicate predSolution, DatalogPredicate predSubmission) {
        for (DatalogFact factSolution : predSolution.getFacts()) {
            var factSubmission = predSubmission.getFact(factSolution);
            if (factSubmission.isEmpty()) {
                this.missingFacts.add(factSolution);
            }
        }

        for (DatalogFact factSubmission : predSubmission.getFacts()) {
            var factSolution = predSolution.getFact(factSubmission);
            if (factSolution.isEmpty()) {
                this.redundantFacts.add(factSubmission);
            }
        }
    }
}
