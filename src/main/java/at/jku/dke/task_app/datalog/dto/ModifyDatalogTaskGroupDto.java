package at.jku.dke.task_app.datalog.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * This class represents a data transfer object for modifying a datalog task group.
 *
 * @param facts The datalog facts.
 */
public record ModifyDatalogTaskGroupDto(@NotNull String facts) implements Serializable {
}
