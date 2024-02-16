package at.jku.dke.task_app.datalog.evaluation;

import at.jku.dke.etutor.task_app.dto.CriterionDto;
import at.jku.dke.etutor.task_app.dto.GradingDto;
import at.jku.dke.etutor.task_app.dto.SubmissionMode;
import at.jku.dke.etutor.task_app.dto.SubmitSubmissionDto;
import at.jku.dke.task_app.datalog.data.repositories.DatalogTaskRepository;
import at.jku.dke.task_app.datalog.dto.DatalogSubmissionDto;
import at.jku.dke.task_app.datalog.evaluation.analysis.DatalogAnalysis;
import at.jku.dke.task_app.datalog.evaluation.exceptions.AnalysisException;
import at.jku.dke.task_app.datalog.evaluation.exceptions.ExecutionException;
import at.jku.dke.task_app.datalog.evaluation.exceptions.SyntaxException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Service that evaluates submissions.
 */
@Service
public class EvaluationService {
    private static final Logger LOG = LoggerFactory.getLogger(EvaluationService.class);

    private final DatalogTaskRepository taskRepository;
    private final MessageSource messageSource;
    private final DatalogExecutor executor;

    /**
     * Creates a new instance of class {@link EvaluationService}.
     *
     * @param taskRepository The task repository.
     * @param messageSource  The message source.
     * @param executor       The datalog executor.
     */
    public EvaluationService(DatalogTaskRepository taskRepository, MessageSource messageSource, DatalogExecutor executor) {
        this.taskRepository = taskRepository;
        this.messageSource = messageSource;
        this.executor = executor;
    }

    /**
     * Evaluates a input.
     *
     * @param submission The input to evaluate.
     * @return The evaluation result.
     */
    @Transactional
    public GradingDto evaluate(SubmitSubmissionDto<DatalogSubmissionDto> submission) {
        // find task
        var task = this.taskRepository.findByIdWithTaskGroup(submission.taskId())
            .orElseThrow(() -> new EntityNotFoundException("Task " + submission.taskId() + " does not exist."));

        // prepare
        LOG.info("Evaluating input for task {} with mode {} and feedback-level {}", submission.taskId(), submission.mode(), submission.feedbackLevel());
        Locale locale = Locale.of(submission.language());
        BigDecimal points = BigDecimal.ZERO;

        // execute
        String facts = submission.mode() == SubmissionMode.SUBMIT ? task.getTaskGroup().getSubmissionFacts() : task.getTaskGroup().getDiagnoseFacts();
        boolean encodeFacts = submission.mode() == SubmissionMode.SUBMIT;
        DatalogExecutor.ExecutionResult solutionResult;
        DatalogExecutor.ExecutionResult submissionResult;
        try {
            solutionResult = this.executor.execute(facts, task.getSolution(), task.getQuery(), task.getUncheckedTerms(), encodeFacts);
        } catch (ExecutionException | IOException ex) {
            LOG.error("Error while evaluating solution for task {}", submission.taskId(), ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while evaluating solution for task " + submission.taskId(), ex);
        }

        try {
            submissionResult = this.executor.execute(facts, submission.submission().input(), task.getQuery(), task.getUncheckedTerms(), encodeFacts);
        } catch (SyntaxException ex) {
            LOG.warn("Syntax error in input for task {}", submission.taskId());
            List<CriterionDto> criteria = new ArrayList<>();
            criteria.add(new CriterionDto(
                this.messageSource.getMessage("criterium.syntax", null, locale),
                null,
                false,
                "<pre>" + HtmlUtils.htmlEscape(ex.getMessage()) + "</pre>"));
            return new GradingDto(task.getMaxPoints(), points, this.messageSource.getMessage("syntaxError", null, locale), criteria);
        } catch (ExecutionException | IOException ex) {
            LOG.error("Error while evaluating input for task {}", submission.taskId(), ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while evaluating input for task " + submission.taskId(), ex);
        }

        // analyze, grade, feedback
        try {
            var analysis = new DatalogAnalysis(solutionResult.result(), submissionResult.result());
            points = analysis.isCorrect() ? task.getMaxPoints() : BigDecimal.ZERO;
            var reporter = new DatalogReport(this.messageSource, locale, submission.mode(), submission.feedbackLevel(), analysis, submissionResult.output());
            return new GradingDto(task.getMaxPoints(), points, reporter.getGeneralFeedback(), reporter.getCriteria());
        } catch (AnalysisException ex) {
            LOG.error("Error while analyzing query result for task {}", submission.taskId(), ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while analysing query result for task " + submission.taskId(), ex);
        }
    }
}