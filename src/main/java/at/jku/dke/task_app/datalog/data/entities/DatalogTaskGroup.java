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
    @Column(name = "diagnose_facts", nullable = false)
    private String diagnoseFacts;

    @NotNull
    @Column(name = "submission_facts", nullable = false)
    private String submissionFacts;

    /**
     * Creates a new instance of class {@link DatalogTaskGroup}.
     */
    public DatalogTaskGroup() {
    }

    /**
     * Creates a new instance of class {@link DatalogTaskGroup}.
     *
     * @param diagnoseFacts   The diagnose datalog-facts.
     * @param submissionFacts The submission datalog-facts.
     */
    public DatalogTaskGroup(String diagnoseFacts, String submissionFacts) {
        this.diagnoseFacts = diagnoseFacts;
        this.submissionFacts = submissionFacts;
    }

    /**
     * Creates a new instance of class {@link DatalogTaskGroup}.
     *
     * @param status          The status.
     * @param diagnoseFacts   The diagnose datalog-facts.
     * @param submissionFacts The submission datalog-facts.
     */
    public DatalogTaskGroup(TaskStatus status, String diagnoseFacts, String submissionFacts) {
        super(status);
        this.diagnoseFacts = diagnoseFacts;
        this.submissionFacts = submissionFacts;
    }

    /**
     * Creates a new instance of class {@link DatalogTaskGroup}.
     *
     * @param id              The id.
     * @param status          The status.
     * @param diagnoseFacts   The diagnose datalog-facts.
     * @param submissionFacts The submission datalog-facts.
     */
    public DatalogTaskGroup(Long id, TaskStatus status, String diagnoseFacts, String submissionFacts) {
        super(id, status);
        this.diagnoseFacts = diagnoseFacts;
        this.submissionFacts = submissionFacts;
    }

    /**
     * Returns the diagnose datalog-facts.
     *
     * @return The diagnose datalog-facts.
     */
    public String getDiagnoseFacts() {
        return diagnoseFacts;
    }

    /**
     * Sets the diagnose datalog-facts.
     *
     * @param diagnoseFacts The diagnose datalog-facts.
     */
    public void setDiagnoseFacts(String diagnoseFacts) {
        this.diagnoseFacts = diagnoseFacts;
    }

    /**
     * Returns the submission datalog-facts.
     *
     * @return The submission datalog-facts.
     */
    public String getSubmissionFacts() {
        return submissionFacts;
    }

    /**
     * Sets the submission datalog-facts.
     *
     * @param submissionFacts The submission datalog-facts.
     */
    public void setSubmissionFacts(String submissionFacts) {
        this.submissionFacts = submissionFacts;
    }
}
