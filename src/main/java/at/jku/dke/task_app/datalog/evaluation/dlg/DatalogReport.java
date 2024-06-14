package at.jku.dke.task_app.datalog.evaluation.dlg;

import at.jku.dke.etutor.task_app.dto.CriterionDto;
import at.jku.dke.etutor.task_app.dto.SubmissionMode;
import at.jku.dke.task_app.datalog.evaluation.DatalogFact;
import at.jku.dke.task_app.datalog.evaluation.DatalogPredicate;
import at.jku.dke.task_app.datalog.evaluation.dlg.analysis.DatalogAnalysis;
import at.jku.dke.task_app.datalog.evaluation.dlg.grading.DatalogGrading;
import at.jku.dke.task_app.datalog.evaluation.dlg.grading.GradingEntry;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Class for generating a report for a datalog evaluation.
 */
public class DatalogReport {
    private final MessageSource messageSource;
    private final Locale locale;
    private final SubmissionMode mode;
    private final int feedbackLevel;
    private final DatalogAnalysis analysis;
    private final String rawOutput;
    private final DatalogGrading grading;

    /**
     * Creates a new instance of class {@linkplain DatalogReport}.
     *
     * @param messageSource The message source.
     * @param locale        The locale.
     * @param mode          The submission mode.
     * @param feedbackLevel The feedback level.
     * @param analysis      The analysis.
     * @param rawOutput     The raw output of the execution.
     * @param grading       The grading.
     */
    DatalogReport(MessageSource messageSource, Locale locale, SubmissionMode mode, int feedbackLevel, DatalogAnalysis analysis, String rawOutput, DatalogGrading grading) {
        if (feedbackLevel < 0 || feedbackLevel > 3)
            throw new IllegalArgumentException("feedbackLevel must be between 0 and 3");

        this.messageSource = messageSource;
        this.locale = locale;
        this.feedbackLevel = feedbackLevel;
        this.analysis = analysis;
        this.mode = mode;
        this.rawOutput = rawOutput;
        this.grading = grading;
    }

    /**
     * Gets the general feedback.
     *
     * @return The general feedback.
     */
    public String getGeneralFeedback() {
        if (this.mode == SubmissionMode.RUN) // we assume here that the syntax is valid because otherwise, the evaluation service will abort earlier
            return this.messageSource.getMessage("noSyntaxError", null, this.locale);

        return this.analysis.isCorrect() ?
            this.messageSource.getMessage(this.mode == SubmissionMode.SUBMIT ? "correct" : "possiblyCorrect", null, this.locale) :
            this.messageSource.getMessage("incorrect", null, this.locale);
    }

    /**
     * Gets the detailed feedback.
     *
     * @return The detailed feedback.
     */
    public List<CriterionDto> getCriteria() {
        var criteria = new ArrayList<CriterionDto>();

        // Syntax (because if syntax is invalid, this method will not be called)
        criteria.add(new CriterionDto(
            this.messageSource.getMessage("criterium.syntax", null, locale),
            null,
            true,
            this.messageSource.getMessage("criterium.syntax.valid", null, locale)));

        // Semantics
        this.createCriterion("missingPredicates", GradingEntry.MISSING_PREDICATE, analysis::getMissingPredicates, true).ifPresent(criteria::add);
        this.createCriterion("missingFacts", GradingEntry.MISSING_FACT, analysis::getMissingFacts, false).ifPresent(criteria::add);
        this.createCriterion("superfluousFacts", GradingEntry.SUPERFLUOUS_FACT, analysis::getSuperfluousFacts, false).ifPresent(criteria::add);

        // Execution result
        if (this.mode != SubmissionMode.SUBMIT) {
            criteria.add(new CriterionDto(
                this.messageSource.getMessage("criterium.result", null, locale),
                null,
                this.mode == SubmissionMode.RUN || analysis.isCorrect(),
                "<div style=\"font-family: monospace;\">" + this.rawOutput + "</div>"
            ));
        }

        return criteria;
    }

    private Optional<CriterionDto> createCriterion(String translationKey, String errorCategory, Supplier<List<?>> listSupplier, boolean showOnRun) {
        if (this.mode == SubmissionMode.RUN && !showOnRun)
            return Optional.empty();
        if (listSupplier.get().isEmpty())
            return Optional.empty();
        if (this.mode == SubmissionMode.SUBMIT || this.mode == SubmissionMode.RUN)
            return Optional.of(new CriterionDto(
                this.messageSource.getMessage("criterium." + translationKey, null, locale),
                this.grading.getDetails(errorCategory).map(e -> e.minusPoints().negate()).orElse(null),
                false,
                this.messageSource.getMessage("criterium." + translationKey + ".noCount", null, locale)
            ));

        return switch (this.feedbackLevel) {
            case 0 -> // no feedback
                Optional.empty();
            case 1 -> // little feedback
                Optional.of(new CriterionDto(
                    this.messageSource.getMessage("criterium." + translationKey, null, locale),
                    null,
                    false,
                    this.messageSource.getMessage("criterium." + translationKey + ".noCount", null, locale)
                ));
            case 2 -> // some feedback
                Optional.of(new CriterionDto(
                    this.messageSource.getMessage("criterium." + translationKey, null, locale),
                    this.grading.getDetails(errorCategory).map(e -> e.minusPoints().negate()).orElse(null),
                    false,
                    this.messageSource.getMessage("criterium." + translationKey + ".count", new Object[]{listSupplier.get().size()}, locale)
                ));
            case 3 -> // much feedback
                //noinspection unchecked
                Optional.of(new CriterionDto(
                    this.messageSource.getMessage("criterium." + translationKey, null, locale),
                    this.grading.getDetails(errorCategory).map(e -> e.minusPoints().negate()).orElse(null),
                    false,
                    listSupplier.get().getFirst() instanceof DatalogFact ?
                        buildDetailedFactReport((List<DatalogFact>) listSupplier.get()) :
                        buildDetailedPredicateReport((List<DatalogPredicate>) listSupplier.get())
                ));
            default -> Optional.empty();
        };
    }

    private static String buildDetailedFactReport(List<DatalogFact> facts) {
        var sb = new StringBuilder("<pre>");
        for (var fact : facts) {
            sb.append(fact.toString()).append('\n');
        }
        return sb.append("</pre>").toString();
    }

    private static String buildDetailedPredicateReport(List<DatalogPredicate> predicates) {
        var sb = new StringBuilder("<pre>");
        for (var pred : predicates) {
            sb.append(pred.toString()).append('\n');
        }
        return sb.append("</pre>").toString();
    }
}
