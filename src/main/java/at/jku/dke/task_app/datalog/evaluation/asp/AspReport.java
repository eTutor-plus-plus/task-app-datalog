package at.jku.dke.task_app.datalog.evaluation.asp;

import at.jku.dke.etutor.task_app.dto.CriterionDto;
import at.jku.dke.etutor.task_app.dto.SubmissionMode;
import at.jku.dke.task_app.datalog.evaluation.DatalogPredicate;
import at.jku.dke.task_app.datalog.evaluation.asp.analysis.AspAnalysis;
import org.springframework.context.MessageSource;

import java.util.*;
import java.util.function.Supplier;

/**
 * Class for generating a report for an ASP evaluation.
 */
public class AspReport {
    private final MessageSource messageSource;
    private final Locale locale;
    private final SubmissionMode mode;
    private final int feedbackLevel;
    private final AspAnalysis analysis;
    private final String rawOutput;

    /**
     * Creates a new instance of class {@linkplain AspReport}.
     *
     * @param messageSource The message source.
     * @param locale        The locale.
     * @param mode          The submission mode.
     * @param feedbackLevel The feedback level.
     * @param analysis      The analysis.
     * @param rawOutput     The raw output of the execution.
     */
    AspReport(MessageSource messageSource, Locale locale, SubmissionMode mode, int feedbackLevel, AspAnalysis analysis, String rawOutput) {
        if (feedbackLevel < 0 || feedbackLevel > 3)
            throw new IllegalArgumentException("feedbackLevel must be between 0 and 3");

        this.messageSource = messageSource;
        this.locale = locale;
        this.feedbackLevel = mode == SubmissionMode.SUBMIT ? (feedbackLevel == 0 ? 0 : 1) : feedbackLevel;
        this.analysis = analysis;
        this.mode = mode;
        this.rawOutput = rawOutput;
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
        if (!this.analysis.hasSameAmountOfModels())
            this.createCountCriterion().ifPresent(criteria::add);
        this.createCriterion("missingModels", analysis::getMissingModels).ifPresent(criteria::add);
        this.createCriterion("superfluousModels", analysis::getSuperfluousModels).ifPresent(criteria::add);

        // Execution result
        if (this.mode != SubmissionMode.SUBMIT) {
            criteria.add(new CriterionDto(
                this.messageSource.getMessage("criterium.result", null, locale),
                null,
                this.mode == SubmissionMode.RUN || analysis.isCorrect(),
                "<pre>" + this.rawOutput + "</pre>"
            ));
        }

        return criteria;
    }

    private Optional<CriterionDto> createCountCriterion() {
        if (this.mode == SubmissionMode.RUN || this.feedbackLevel == 0)
            return Optional.empty();

        return Optional.of(new CriterionDto(
            this.messageSource.getMessage("criterium.count", null, locale),
            null,
            false,
            this.messageSource.getMessage("criterium.count.invalid", null, locale)
        ));
    }

    private Optional<CriterionDto> createCriterion(String translationKey, Supplier<List<Set<DatalogPredicate>>> listSupplier) {
        if (this.mode == SubmissionMode.RUN)
            return Optional.empty();
        if (listSupplier.get().isEmpty())
            return Optional.empty();

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
                    null,
                    false,
                    this.messageSource.getMessage("criterium." + translationKey + ".count", new Object[]{listSupplier.get().size()}, locale)
                ));
            case 3 -> // much feedback
                Optional.of(new CriterionDto(
                    this.messageSource.getMessage("criterium." + translationKey, null, locale),
                    null,
                    false,
                    "<pre>" + buildDetailedPredicateReport(listSupplier.get()) + "</pre>"
                ));
            default -> Optional.empty();
        };
    }

    private static String buildDetailedPredicateReport(List<Set<DatalogPredicate>> predicates) {
        var sb = new StringBuilder("<pre>");
        for (var pred : predicates) {
            sb.append("{");
            for (var p : pred) {
                for (var f : p.getFacts()) {
                    sb.append(f).append(", ");
                }
            }
            sb.delete(sb.length() - 2, sb.length());
            sb.append("}\n");
        }
        return sb.append("</pre>").toString();
    }
}
