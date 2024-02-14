package at.jku.dke.task_app.datalog.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * This class represents a data transfer object for modifying a datalog task group.
 *
 * @param diagnoseFacts   The diagnose datalog facts.
 * @param submissionFacts The submission datalog facts.
 */
public record ModifyDatalogTaskGroupDto(@NotNull String diagnoseFacts, @NotNull String submissionFacts) implements Serializable {
}
