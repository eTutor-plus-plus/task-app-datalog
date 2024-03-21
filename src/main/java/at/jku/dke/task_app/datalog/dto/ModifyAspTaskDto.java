package at.jku.dke.task_app.datalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.Serializable;

/**
 * This class represents a data transfer object for modifying an ASP task.
 *
 * @param solution The solution.
 * @param maxN     The max N (upper integer limit).
 */
public record ModifyAspTaskDto(@Schema(example = "child(X) :- hasChild(_, X).") @NotNull String solution,
                               @Positive Integer maxN) implements Serializable {
}
