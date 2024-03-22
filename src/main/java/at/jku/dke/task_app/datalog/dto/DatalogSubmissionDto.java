package at.jku.dke.task_app.datalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * This class represents a data transfer object for submitting a solution.
 *
 * @param input The user input.
 */
public record DatalogSubmissionDto(@Schema(example = "child(X) :- hasChild(_, X).") @NotNull String input) {
}
