package at.jku.dke.task_app.datalog.evaluation;

import at.jku.dke.etutor.task_app.dto.CriterionDto;
import at.jku.dke.etutor.task_app.dto.SubmissionMode;
import at.jku.dke.task_app.datalog.evaluation.analysis.DatalogAnalysis;
import at.jku.dke.task_app.datalog.evaluation.analysis.DatalogFact;
import at.jku.dke.task_app.datalog.evaluation.analysis.DatalogPredicate;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    /**
     * Creates a new instance of class {@linkplain DatalogReport}.
     *
     * @param messageSource The message source.
     * @param locale        The locale.
     * @param mode          The submission mode.
     * @param feedbackLevel The feedback level.
     * @param analysis      The analysis.
     * @param rawOutput     The raw output of the execution.
     */
    DatalogReport(MessageSource messageSource, Locale locale, SubmissionMode mode, int feedbackLevel, DatalogAnalysis analysis, String rawOutput) {
        if (feedbackLevel < 0 || feedbackLevel > 3)
            throw new IllegalArgumentException("feedbackLevel must be between 0 and 3");

        this.messageSource = messageSource;
        this.locale = locale;
        this.feedbackLevel = feedbackLevel;
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
        if (this.mode == SubmissionMode.RUN)
            return this.messageSource.getMessage("noSyntaxError", null, this.locale);

        return this.analysis.isCorrect() ?
            this.messageSource.getMessage("correct", null, this.locale) :
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
        if (this.mode == SubmissionMode.RUN) {
            criteria.add(new CriterionDto(
                this.messageSource.getMessage("criterium.result", null, locale),
                null,
                true,
                "<div style=\"font-family: monospace;\">" + this.rawOutput + "</div>"
            ));
            return criteria;
        }
        if (this.mode != SubmissionMode.DIAGNOSE)
            return criteria;

        switch (this.feedbackLevel) {
            case 0: // no feedback
                break;
            case 1: // little feedback
                if (!analysis.getMissingFacts().isEmpty()) {
                    criteria.add(new CriterionDto(
                        this.messageSource.getMessage("criterium.missingFacts", null, locale),
                        null,
                        false,
                        this.messageSource.getMessage("criterium.missingFacts.noCount", null, locale)
                    ));
                }
                if (!analysis.getRedundantFacts().isEmpty()) {
                    criteria.add(new CriterionDto(
                        this.messageSource.getMessage("criterium.redundantFacts", null, locale),
                        null,
                        false,
                        this.messageSource.getMessage("criterium.redundantFacts.noCount", null, locale)
                    ));
                }
                if (!analysis.getMissingPredicates().isEmpty()) {
                    criteria.add(new CriterionDto(
                        this.messageSource.getMessage("criterium.missingPredicates", null, locale),
                        null,
                        false,
                        this.messageSource.getMessage("criterium.missingPredicates.noCount", null, locale)
                    ));
                }
                break;
            case 2: // some feedback
                if (!analysis.getMissingFacts().isEmpty()) {
                    criteria.add(new CriterionDto(
                        this.messageSource.getMessage("criterium.missingFacts", null, locale),
                        null,
                        false,
                        this.messageSource.getMessage("criterium.missingFacts.count", new Object[]{analysis.getMissingFacts().size()}, locale)
                    ));
                }
                if (!analysis.getRedundantFacts().isEmpty()) {
                    criteria.add(new CriterionDto(
                        this.messageSource.getMessage("criterium.redundantFacts", null, locale),
                        null,
                        false,
                        this.messageSource.getMessage("criterium.redundantFacts.count", new Object[]{analysis.getRedundantFacts().size()}, locale)
                    ));
                }
                if (!analysis.getMissingPredicates().isEmpty()) {
                    criteria.add(new CriterionDto(
                        this.messageSource.getMessage("criterium.missingPredicates", null, locale),
                        null,
                        false,
                        this.messageSource.getMessage("criterium.missingPredicates.count", new Object[]{analysis.getMissingPredicates().size()}, locale)
                    ));
                }
                break;
            case 3: // much feedback
                if (!analysis.getMissingFacts().isEmpty()) {
                    criteria.add(new CriterionDto(
                        this.messageSource.getMessage("criterium.missingFacts", null, locale),
                        null,
                        false,
                        buildDetailedFactReport(analysis.getMissingFacts())
                    ));
                }
                if (!analysis.getRedundantFacts().isEmpty()) {
                    criteria.add(new CriterionDto(
                        this.messageSource.getMessage("criterium.redundantFacts", null, locale),
                        null,
                        false,
                        buildDetailedFactReport(analysis.getRedundantFacts())
                    ));
                }
                if (!analysis.getMissingPredicates().isEmpty()) {
                    criteria.add(new CriterionDto(
                        this.messageSource.getMessage("criterium.missingPredicates", null, locale),
                        null,
                        false,
                        buildDetailedPredicateReport(analysis.getMissingPredicates())
                    ));
                }
                break;
        }

        criteria.add(new CriterionDto(
            this.messageSource.getMessage("criterium.result", null, locale),
            null,
            analysis.isCorrect(),
            "<div style=\"font-family: monospace;\">" + this.rawOutput + "</div>"
        ));

        return criteria;
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
