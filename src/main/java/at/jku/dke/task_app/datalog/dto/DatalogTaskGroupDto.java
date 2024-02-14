package at.jku.dke.task_app.datalog.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * DTO for {@link at.jku.dke.task_app.datalog.data.entities.DatalogTaskGroup}
 *
 * @param facts The datalog facts.
 */
public record DatalogTaskGroupDto(@NotNull String facts) implements Serializable {
}
