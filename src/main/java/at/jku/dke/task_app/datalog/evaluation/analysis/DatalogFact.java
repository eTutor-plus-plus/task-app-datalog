package at.jku.dke.task_app.datalog.evaluation.analysis;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a datalog fact.
 */
public class DatalogFact {
    private final DatalogPredicate predicate;
    private final String[] terms;

    /**
     * Creates a new instance of class {@linkplain DatalogFact}.
     *
     * @param predicate The predicate of the fact.
     * @param terms     The terms of the fact.
     */
    DatalogFact(DatalogPredicate predicate, String terms) {
        this.predicate = predicate;
        this.terms = terms.split(", ");
    }

    /**
     * Gets the predicate of this fact.
     *
     * @return The predicate.
     */
    public DatalogPredicate getPredicate() {
        return predicate;
    }

    /**
     * Gets the arity of the fact.
     *
     * @return The arity.
     */
    public int getArity() {
        return terms.length;
    }

    /**
     * Gets the terms of the fact.
     *
     * @return The terms.
     */
    public String[] getTerms() {
        return terms;
    }

    @Override
    public String toString() {
        return this.predicate.getName() + '(' + String.join(", ", terms) + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatalogFact that = (DatalogFact) o;
        return Objects.equals(predicate.getName(), that.predicate.getName()) && Arrays.equals(terms, that.terms);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(predicate.getName());
        result = 31 * result + Arrays.hashCode(terms);
        return result;
    }
}
