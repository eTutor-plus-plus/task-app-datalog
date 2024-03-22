package at.jku.dke.task_app.datalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * This class represents a data transfer object for submitting an ASP solution.
 *
 * @param input The user input.
 */
public record AspSubmissionDto(@Schema(example = ":- like(P1,P2), at(P1,T), not at(P2,T).") @NotNull String input) {
}
