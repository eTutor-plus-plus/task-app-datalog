package at.jku.dke.task_app.datalog.evaluation.analysis;

import java.util.List;
import java.util.Optional;

/**
 * Represents the predicates of the result of a Datalog query.
 */
public class DatalogPredicate {
    private final String name;
    private final int arity;
    private final List<DatalogFact> facts;
    private static final String CHARS = "XYZABCDEFGHIJKLMNOPQRSTUVW";

    /**
     * Creates a new instance of class {@linkplain DatalogPredicate}.
     *
     * @param name  The name of the predicate.
     * @param facts The facts of the predicate.
     */
    DatalogPredicate(String name, List<String> facts) {
        this.name = name;
        this.facts = facts.stream().map(f -> new DatalogFact(this, f)).toList();
        this.arity = this.facts.isEmpty() ? 0 : this.facts.getFirst().getArity();
    }

    /**
     * Returns whether the predicate is consistent. A predicate is consistent if all of its facts have the same arity.
     *
     * @return {@code true} if the predicate is consistent; {@code false} otherwise.
     */
    public boolean isConsistent() {
        return this.facts.stream().allMatch(f -> f.getArity() == this.arity);
    }

    /**
     * Returns the name of the predicate.
     *
     * @return The name of the predicate.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the arity of the predicate.
     *
     * @return The arity of the predicate.
     */
    public int getArity() {
        return arity;
    }

    /**
     * Returns the facts of the predicate.
     *
     * @return The facts of the predicate.
     */
    public List<DatalogFact> getFacts() {
        return facts;
    }

    /**
     * Returns the fact with the same terms as the specified fact.
     *
     * @param factSolution The fact to search for.
     * @return The fact with the specified terms.
     */
    public Optional<DatalogFact> getFact(DatalogFact factSolution) {
        return this.facts.stream().filter(factSolution::equals).findFirst();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(this.name).append('(');
        for (int i = 0; i < this.arity; i++) {
            builder.append(CHARS.charAt(i));
            if (i < this.arity - 1)
                builder.append(", ");
        }
        return builder.append(')').toString();
    }
}
