package at.jku.dke.task_app.datalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.Serializable;

/**
 * DTO for {@link at.jku.dke.task_app.datalog.data.entities.AspTask}
 *
 * @param solution The solution.
 * @param maxN     The max N (upper integer limit).
 */
public record AspTaskDto(@Schema(example = ":- like(P1,P2), at(P1,T), not at(P2,T).") @NotNull String solution,
                         @Positive Integer maxN) implements Serializable {
}
