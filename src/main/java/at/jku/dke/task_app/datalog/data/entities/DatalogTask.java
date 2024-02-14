package at.jku.dke.task_app.datalog.data.entities;

import at.jku.dke.etutor.task_app.data.entities.BaseTaskInGroup;
import at.jku.dke.etutor.task_app.dto.TaskStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Represents a datalog task.
 */
@Entity
@Table(name = "task")
public class DatalogTask extends BaseTaskInGroup<DatalogTaskGroup> {
    @NotNull
    @Column(name = "solution", nullable = false)
    private String solution;

    @NotNull
    @Column(name = "query", nullable = false)
    private String query;

    @Column(name = "unchecked_terms")
    private String uncheckedTerms;

    /**
     * Creates a new instance of class {@link DatalogTask}.
     */
    public DatalogTask() {
    }

    /**
     * Creates a new instance of class {@link DatalogTask}.
     *
     * @param solution       The solution.
     * @param query          The query.
     * @param uncheckedTerms The unchecked terms.
     */
    public DatalogTask(String solution, String query, String uncheckedTerms) {
        this.solution = solution;
        this.query = query;
        this.uncheckedTerms = uncheckedTerms;
    }

    /**
     * Creates a new instance of class {@link DatalogTask}.
     *
     * @param maxPoints      The maximum points.
     * @param status         The status.
     * @param taskGroup      The task group.
     * @param solution       The solution.
     * @param query          The query.
     * @param uncheckedTerms The unchecked terms.
     */
    public DatalogTask(BigDecimal maxPoints, TaskStatus status, DatalogTaskGroup taskGroup, String solution, String query, String uncheckedTerms) {
        super(maxPoints, status, taskGroup);
        this.solution = solution;
        this.query = query;
        this.uncheckedTerms = uncheckedTerms;
    }

    /**
     * Creates a new instance of class {@link DatalogTask}.
     *
     * @param id             The identifier.
     * @param maxPoints      The maximum points.
     * @param status         The status.
     * @param taskGroup      The task group.
     * @param solution       The solution.
     * @param query          The query.
     * @param uncheckedTerms The unchecked terms.
     */
    public DatalogTask(Long id, BigDecimal maxPoints, TaskStatus status, DatalogTaskGroup taskGroup, String solution, String query, String uncheckedTerms) {
        super(id, maxPoints, status, taskGroup);
        this.solution = solution;
        this.query = query;
        this.uncheckedTerms = uncheckedTerms;
    }

    /**
     * Gets the solution.
     *
     * @return The solution.
     */
    public String getSolution() {
        return solution;
    }

    /**
     * Sets the solution.
     *
     * @param solution The solution.
     */
    public void setSolution(String solution) {
        this.solution = solution;
    }

    /**
     * Gets the query.
     *
     * @return The query.
     */
    public String getQuery() {
        return query;
    }

    /**
     * Sets the query.
     *
     * @param query The query.
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * Gets the unchecked terms.
     *
     * @return The unchecked terms.
     */
    public String getUncheckedTerms() {
        return uncheckedTerms;
    }

    /**
     * Sets the unchecked terms.
     *
     * @param uncheckedTerms The unchecked terms.
     */
    public void setUncheckedTerms(String uncheckedTerms) {
        this.uncheckedTerms = uncheckedTerms;
    }
}
