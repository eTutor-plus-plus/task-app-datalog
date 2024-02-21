package at.jku.dke.task_app.datalog.data.entities;

import java.io.Serializable;

/**
 * Describes a term of a predicate.
 *
 * @param predicate The predicate name.
 * @param term      The term.
 * @param position  The position of the term in the predicate.
 */
public record TermDescription(String predicate, String term, int position) implements Serializable {
}
