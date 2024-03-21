package at.jku.dke.task_app.datalog.data.entities;

import at.jku.dke.etutor.task_app.data.entities.BaseTaskInGroup;
import at.jku.dke.etutor.task_app.dto.TaskStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Represents an answer set programming task.
 */
@Entity
@Table(name = "asp_task")
public class AspTask extends BaseTaskInGroup<DatalogTaskGroup> {
    @NotNull
    @Column(name = "solution", nullable = false, length = Integer.MAX_VALUE)
    private String solution;

    @Column(name = "max_n")
    private Integer maxN;

    /**
     * Creates a new instance of class {@link AspTask}.
     */
    public AspTask() {
    }

    /**
     * Creates a new instance of class {@link AspTask}.
     *
     * @param solution The solution.
     * @param maxN     The max n.
     */
    public AspTask(String solution, Integer maxN) {
        this.solution = solution;
        this.maxN = maxN;
    }

    /**
     * Creates a new instance of class {@link AspTask}.
     *
     * @param maxPoints The maximum points.
     * @param status    The status.
     * @param taskGroup The task group.
     * @param solution  The solution.
     * @param maxN      The max n.
     */
    public AspTask(BigDecimal maxPoints, TaskStatus status, DatalogTaskGroup taskGroup, String solution, Integer maxN) {
        super(maxPoints, status, taskGroup);
        this.solution = solution;
        this.maxN = maxN;
    }

    /**
     * Creates a new instance of class {@link AspTask}.
     *
     * @param id        The identifier.
     * @param maxPoints The maximum points.
     * @param status    The status.
     * @param taskGroup The task group.
     * @param solution  The solution.
     * @param maxN      The max n.
     */
    public AspTask(Long id, BigDecimal maxPoints, TaskStatus status, DatalogTaskGroup taskGroup, String solution, Integer maxN) {
        super(id, maxPoints, status, taskGroup);
        this.solution = solution;
        this.maxN = maxN;
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
     * Gets the max N (upper integer limit).
     *
     * @return The max N.
     */
    public Integer getMaxN() {
        return maxN;
    }

    /**
     * Sets the max N (upper integer limit).
     *
     * @param maxN The max N.
     */
    public void setMaxN(Integer maxN) {
        this.maxN = maxN;
    }
}
