package at.jku.dke.task_app.datalog.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * This class represents a data transfer object for modifying a datalog task.
 *
 * @param solution       The solution.
 * @param query          The query.
 * @param uncheckedTerms The unchecked terms.
 */
public record ModifyDatalogTaskDto(@NotNull String solution, @NotNull String query, String uncheckedTerms) implements Serializable {
}
