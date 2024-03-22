package at.jku.dke.task_app.datalog.evaluation.asp.analysis;

import at.jku.dke.task_app.datalog.evaluation.DatalogPredicate;
import at.jku.dke.task_app.datalog.evaluation.exceptions.AnalysisException;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service that evaluates the ASP execution results.
 */
public class AspAnalysisImpl implements AspAnalysis {

    private static final Pattern MODEL_PATTERN = Pattern.compile("\\{([^\\{\\}]*)\\}", Pattern.MULTILINE);
    private static final Pattern PREDICATE_PATTERN = Pattern.compile("([\\w\\d]+?)\\((.*?)\\)", Pattern.MULTILINE);
    private boolean isCorrect;
    private boolean hasSameAmountOfModels;
    private List<Set<DatalogPredicate>> missingModels;
    private List<Set<DatalogPredicate>> superfluousModels;
    private final List<Set<DatalogPredicate>> solutionResult;
    private final List<Set<DatalogPredicate>> submissionResult;

    /**
     * Creates a new instance of class {@link AspAnalysisImpl}.
     *
     * @param solutionResult   The solution result.
     * @param submissionResult The student result.
     * @throws AnalysisException If the analysis fails.
     */
    public AspAnalysisImpl(String solutionResult, String submissionResult) throws AnalysisException {
        this.isCorrect = false;
        this.hasSameAmountOfModels = false;
        this.solutionResult = parseResult(solutionResult);
        this.submissionResult = parseResult(submissionResult);
        this.missingModels = new ArrayList<>();
        this.superfluousModels = new ArrayList<>();
        this.analyze();
    }

    /**
     * Returns whether the submission result matches the solution result.
     *
     * @return {@code true} if the submission result matches the solution result; {@code false} otherwise.
     */
    @Override
    public boolean isCorrect() {
        return isCorrect;
    }

    /**
     * Returns whether the submission result and the solution result have the same amount of models.
     *
     * @return {@code true} if both results have the same amount of solutions; {@code false} otherwise.
     */
    @Override
    public boolean hasSameAmountOfModels() {
        return hasSameAmountOfModels;
    }

    /**
     * Gets the solution result.
     * <p>
     * Each set represents a model.
     *
     * @return The solution result.
     */
    @Override
    public List<Set<DatalogPredicate>> getSolutionResult() {
        return solutionResult;
    }

    /**
     * Gets the submission result.
     * <p>
     * Each set represents a model.
     *
     * @return The submission result.
     */
    @Override
    public List<Set<DatalogPredicate>> getSubmissionResult() {
        return submissionResult;
    }

    /**
     * Gets the missing models.
     *
     * @return The missing models.
     */
    @Override
    public List<Set<DatalogPredicate>> getMissingModels() {
        return missingModels;
    }

    /**
     * Gets the superfluous models.
     *
     * @return The superfluous models.
     */
    @Override
    public List<Set<DatalogPredicate>> getSuperfluousModels() {
        return superfluousModels;
    }

    /**
     * Analyzes the result.
     * <p>
     * This is just a quite primitive implementation of the analysis.
     *
     * @throws AnalysisException If the analysis fails.
     */
    private void analyze() throws AnalysisException {
        // Check consistency
        if (this.solutionResult.stream().flatMap(Collection::stream).anyMatch(x -> !x.isConsistent()) ||
            this.submissionResult.stream().flatMap(Collection::stream).anyMatch(x -> !x.isConsistent())) {
            this.isCorrect = false;
            throw new AnalysisException("Analysis stopped, as one of the results is inconsistent.");
        }

        // Check amount of models
        this.hasSameAmountOfModels = this.solutionResult.size() == this.submissionResult.size();
        if (!this.hasSameAmountOfModels) {
            this.isCorrect = false;
            return;
        }

        // Check equality of models
        for (Set<DatalogPredicate> model : this.solutionResult) {
            if (!this.submissionResult.contains(model)) {
                this.missingModels.add(model);
            }
        }
        for (Set<DatalogPredicate> model : this.submissionResult) {
            if (!this.solutionResult.contains(model)) {
                this.superfluousModels.add(model);
            }
        }
        this.isCorrect = this.missingModels.isEmpty() && this.superfluousModels.isEmpty();
    }

    /**
     * Parses the datalog output to a set of predicates.
     *
     * @param result The result to parse.
     * @return The set of predicates.
     */
    private static List<Set<DatalogPredicate>> parseResult(String result) {
        // Parse
        List<Map<String, List<String>>> data = new ArrayList<>();
        var modelMatcher = MODEL_PATTERN.matcher(result);
        while (modelMatcher.find()) {
            var group = modelMatcher.group(1);
            var predicateMatcher = PREDICATE_PATTERN.matcher(group);
            Map<String, List<String>> predicates = new HashMap<>();
            while (predicateMatcher.find()) {
                var predicate = predicateMatcher.group(1);
                var args = predicateMatcher.group(2);

                if (predicates.containsKey(predicate)) {
                    predicates.get(predicate).add(args);
                } else {
                    predicates.put(predicate, new ArrayList<>(List.of(args)));
                }
            }
            data.add(predicates);
        }

        // Convert
        return data.stream()
            .map(x -> x.entrySet()
                .stream()
                .map(e -> new DatalogPredicate(e.getKey(), e.getValue()))
                .collect(Collectors.toSet()))
            .toList();
    }
}
