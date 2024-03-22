package at.jku.dke.task_app.datalog.dto;

import at.jku.dke.task_app.datalog.data.entities.GradingStrategy;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * This class represents a data transfer object for modifying a datalog task.
 *
 * @param solution                 The solution.
 * @param query                    The query.
 * @param uncheckedTerms           The unchecked terms.
 * @param missingPredicatePenalty  The penalty for missing predicates.
 * @param missingFactPenalty       The penalty for missing facts.
 * @param superfluousFactPenalty   The penalty for superfluous facts.
 * @param missingPredicateStrategy The grading strategy for missing predicates.
 * @param missingFactStrategy      The grading strategy for missing facts.
 * @param superfluousFactStrategy  The grading strategy for superfluous facts.
 */
public record ModifyDatalogTaskDto(@Schema(example = "child(X) :- hasChild(_, X).") @NotNull String solution,
                                   @Schema(example = "child(X)?") @NotNull String query,
                                   @Schema(example = "hatKind(Peter, _).") String uncheckedTerms,
                                   @NotNull @PositiveOrZero BigDecimal missingPredicatePenalty,
                                   @NotNull @PositiveOrZero BigDecimal missingFactPenalty,
                                   @NotNull @PositiveOrZero BigDecimal superfluousFactPenalty,
                                   @NotNull GradingStrategy missingPredicateStrategy,
                                   @NotNull GradingStrategy missingFactStrategy,
                                   @NotNull GradingStrategy superfluousFactStrategy) implements Serializable {
    /**
     * Creates a new instance of class {@link ModifyDatalogTaskDto}.
     *
     * @param solution       The solution.
     * @param query          The query.
     * @param uncheckedTerms The unchecked terms.
     */
    public ModifyDatalogTaskDto(String solution, String query, String uncheckedTerms) {
        this(solution, query, uncheckedTerms, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, GradingStrategy.KO, GradingStrategy.KO, GradingStrategy.KO);
    }
}
