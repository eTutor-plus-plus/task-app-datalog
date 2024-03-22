package at.jku.dke.task_app.datalog.data.entities;

import at.jku.dke.etutor.task_app.data.entities.BaseSubmission;
import at.jku.dke.etutor.task_app.dto.SubmissionMode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Represents an ASP input.
 */
@Entity
@Table(name = "asp_submission")
public class AspSubmission extends BaseSubmission<AspTask> {
    @NotNull
    @Column(name = "submission", nullable = false)
    private String submission;

    /**
     * Creates a new instance of class {@link AspSubmission}.
     */
    public AspSubmission() {
    }

    /**
     * Creates a new instance of class {@link AspSubmission}.
     *
     * @param submission The input.
     */
    public AspSubmission(String submission) {
        this.submission = submission;
    }

    /**
     * Creates a new instance of class {@link AspSubmission}.
     *
     * @param userId        The user id.
     * @param assignmentId  The assignment id.
     * @param task          The task.
     * @param language      The language.
     * @param feedbackLevel The feedback level.
     * @param mode          The mode.
     * @param submission    The input.
     */
    public AspSubmission(String userId, String assignmentId, AspTask task, String language, int feedbackLevel, SubmissionMode mode, String submission) {
        super(userId, assignmentId, task, language, feedbackLevel, mode);
        this.submission = submission;
    }

    /**
     * Gets the input.
     *
     * @return The input.
     */
    public String getSubmission() {
        return submission;
    }

    /**
     * Sets the input.
     *
     * @param submission The input.
     */
    public void setSubmission(String submission) {
        this.submission = submission;
    }
}
