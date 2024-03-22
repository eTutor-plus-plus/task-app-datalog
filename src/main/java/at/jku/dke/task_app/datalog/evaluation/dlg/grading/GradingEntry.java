package at.jku.dke.task_app.datalog.evaluation.dlg.grading;

import java.math.BigDecimal;

/**
 * Represents a grading entry.
 *
 * @param errorCategory The error category (must be one of the error category constant values).
 * @param minusPoints   The minus points (must not be negative).
 */
public record GradingEntry(String errorCategory, BigDecimal minusPoints) {
    public GradingEntry {
        if (minusPoints == null || minusPoints.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Minus points must not be negative.");
        if (errorCategory == null || errorCategory.isBlank())
            throw new IllegalArgumentException("Error category must not be null or blank.");
    }

    /**
     * Error Category: Missing Predicate
     */
    public static final String MISSING_PREDICATE = "missingPredicate";

    /**
     * Error Category: Missing Fact
     */
    public static final String MISSING_FACT = "missingFact";

    /**
     * Error Category: Superfluous Fact
     */
    public static final String SUPERFLUOUS_FACT = "superfluousFact";
}
