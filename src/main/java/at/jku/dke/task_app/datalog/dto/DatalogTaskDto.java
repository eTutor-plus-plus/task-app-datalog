package at.jku.dke.task_app.datalog.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * DTO for {@link at.jku.dke.task_app.datalog.data.entities.DatalogTask}
 *
 * @param solution       The solution.
 * @param query          The query.
 * @param uncheckedTerms The unchecked terms.
 */
public record DatalogTaskDto(@NotNull String solution, @NotNull String query, String uncheckedTerms) implements Serializable {
}
