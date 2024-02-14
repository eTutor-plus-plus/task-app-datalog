package at.jku.dke.task_app.datalog.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * DTO for {@link at.jku.dke.task_app.datalog.data.entities.DatalogTaskGroup}
 *
 * @param diagnoseFacts   The diagnose datalog facts.
 * @param submissionFacts The submission datalog facts.
 */
public record DatalogTaskGroupDto(@NotNull String diagnoseFacts, @NotNull String submissionFacts) implements Serializable {
}
