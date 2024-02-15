package at.jku.dke.task_app.datalog.evaluation.analysis;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents the result of a datalog query.
 */
class DatalogModel {
    private final List<DatalogPredicate> predicates;
    private final List<String> predicatesNames;

    /**
     * Creates a new instance of class {@link DatalogModel}.
     *
     * @param result The query result.
     */
    DatalogModel(Map<String, List<String>> result) {
        this.predicates = result.entrySet().stream()
            .filter(x -> !x.getValue().isEmpty())
            .map(e -> new DatalogPredicate(e.getKey(), e.getValue())).toList();
        this.predicatesNames = this.predicates.stream().map(DatalogPredicate::getName).toList();
    }

    /**
     * Returns whether the model is consistent. A model is consistent if all of its predicates are consistent.
     *
     * @return {@code true} if the model is consistent; {@code false} otherwise.
     */
    public boolean isConsistent() {
        return this.predicates.stream().allMatch(DatalogPredicate::isConsistent);
    }

    /**
     * Gets the predicates.
     *
     * @return The predicates.
     */
    public List<DatalogPredicate> getPredicates() {
        return predicates;
    }

    /**
     * Gets the predicates names.
     *
     * @return The predicates names.
     */
    public List<String> getPredicatesNames() {
        return predicatesNames;
    }

    /**
     * Gets the predicate with the specified name.
     *
     * @param name The name of the predicate.
     * @return The predicate with the specified name.
     */
    public Optional<DatalogPredicate> getPredicate(String name) {
        return this.predicates.stream().filter(p -> p.getName().equals(name)).findFirst();
    }
}
