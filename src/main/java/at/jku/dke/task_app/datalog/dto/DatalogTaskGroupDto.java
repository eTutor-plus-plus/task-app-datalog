package at.jku.dke.task_app.datalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * DTO for {@link at.jku.dke.task_app.datalog.data.entities.DatalogTaskGroup}
 *
 * @param diagnoseFacts   The diagnose datalog facts.
 * @param submissionFacts The submission datalog facts.
 */
public record DatalogTaskGroupDto(@Schema(example = "hasChild(Max, Lisa). hasChild(Lisa, Franz).") @NotNull String diagnoseFacts,
                                  @Schema(example = "hasChild(Franz, Lisa). hasChild(Lisa, Max).") @NotNull String submissionFacts) implements Serializable {
}
