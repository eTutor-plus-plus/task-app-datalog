package at.jku.dke.task_app.datalog.data.entities;

import at.jku.dke.etutor.task_app.data.entities.BaseTaskInGroup;
import at.jku.dke.etutor.task_app.dto.TaskStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "query", nullable = false)
    private List<String> query;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "unchecked_terms", columnDefinition = "jsonb")
    private List<TermDescription> uncheckedTerms;

    @Column(name = "unchecked_term_raw")
    private String uncheckedTermsRaw;

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
    public DatalogTask(String solution, List<String> query, String uncheckedTerms) {
        this.solution = solution;
        this.query = query;
        this.uncheckedTermsRaw = uncheckedTerms;
        this.uncheckedTerms = convertStringToTermDescriptionList(uncheckedTerms);
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
    public DatalogTask(BigDecimal maxPoints, TaskStatus status, DatalogTaskGroup taskGroup, String solution, List<String> query, String uncheckedTerms) {
        super(maxPoints, status, taskGroup);
        this.solution = solution;
        this.query = query;
        this.uncheckedTermsRaw = uncheckedTerms;
        this.uncheckedTerms = convertStringToTermDescriptionList(uncheckedTerms);
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
    public DatalogTask(Long id, BigDecimal maxPoints, TaskStatus status, DatalogTaskGroup taskGroup, String solution, List<String> query, String uncheckedTerms) {
        super(id, maxPoints, status, taskGroup);
        this.solution = solution;
        this.query = query;
        this.uncheckedTermsRaw = uncheckedTerms;
        this.uncheckedTerms = convertStringToTermDescriptionList(uncheckedTerms);
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
    public List<String> getQuery() {
        return query;
    }

    /**
     * Sets the query.
     *
     * @param query The query.
     */
    public void setQuery(List<String> query) {
        this.query = query;
    }

    /**
     * Gets the unchecked terms.
     *
     * @return The unchecked terms.
     */
    public List<TermDescription> getUncheckedTerms() {
        return uncheckedTerms;
    }

    /**
     * Sets the unchecked terms.
     *
     * @param uncheckedTerms The unchecked terms.
     */
    protected void setUncheckedTerms(List<TermDescription> uncheckedTerms) {
        this.uncheckedTerms = uncheckedTerms;
    }

    /**
     * Gets the raw unchecked terms.
     *
     * @return The raw unchecked terms.
     */
    public String getUncheckedTermsRaw() {
        return uncheckedTermsRaw;
    }

    /**
     * Sets the raw unchecked terms.
     *
     * @param uncheckedTermsRaw The raw unchecked terms.
     */
    public void setUncheckedTermsRaw(String uncheckedTermsRaw) {
        this.uncheckedTermsRaw = uncheckedTermsRaw;
        this.uncheckedTerms = convertStringToTermDescriptionList(uncheckedTermsRaw);
    }

    /**
     * Converts the given string to a list of term descriptions by splitting semicolon.
     *
     * @param s The string to convert.
     * @return The list of term descriptions.
     */
    private static List<TermDescription> convertStringToTermDescriptionList(String s) {
        List<String> facts = Arrays.stream(s.split("\\.")).toList();
        List<TermDescription> td = new ArrayList<>(facts.size());

        for (String fact : facts) {
            if (fact.isBlank())
                continue;

            var index = fact.indexOf("(");
            if (index < 0)
                continue;

            var predicate = fact
                .substring(0, index)
                .replace("\t", "")
                .replace("\n", "")
                .replace("\r", "")
                .replace(" ", "");
            var terms = fact.substring(index + 1, fact.length() - 1).split(",");
            for (int i = 0; i < terms.length; i++) {
                td.add(new TermDescription(predicate, terms[i].strip(), i + 1));
            }
        }

        return td;
    }
}
