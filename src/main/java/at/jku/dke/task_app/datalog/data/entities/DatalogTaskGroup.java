package at.jku.dke.task_app.datalog.data.entities;

import at.jku.dke.etutor.task_app.data.entities.BaseTaskGroup;
import at.jku.dke.etutor.task_app.dto.TaskStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a datalog task group.
 */
@Entity
@Table(name = "task_group")
public class DatalogTaskGroup extends BaseTaskGroup {
    @NotNull
    @Column(name = "facts", nullable = false)
    private String facts;

    /**
     * Creates a new instance of class {@link DatalogTaskGroup}.
     */
    public DatalogTaskGroup() {
    }

    /**
     * Creates a new instance of class {@link DatalogTaskGroup}.
     *
     * @param facts The datalog facts.
     */
    public DatalogTaskGroup(String facts) {
        this.facts = facts;
    }

    /**
     * Creates a new instance of class {@link DatalogTaskGroup}.
     *
     * @param status The status.
     * @param facts  The datalog facts.
     */
    public DatalogTaskGroup(TaskStatus status, String facts) {
        super(status);
        this.facts = facts;
    }

    /**
     * Creates a new instance of class {@link DatalogTaskGroup}.
     *
     * @param id     The id.
     * @param status The status.
     * @param facts  The datalog facts.
     */
    public DatalogTaskGroup(Long id, TaskStatus status, String facts) {
        super(id, status);
        this.facts = facts;
    }

    /**
     * Gets the datalog facts.
     *
     * @return The datalog facts.
     */
    public String getFacts() {
        return facts;
    }

    /**
     * Sets the datalog facts.
     *
     * @param facts The datalog facts.
     */
    public void setFacts(String facts) {
        this.facts = facts;
    }
}
