package at.jku.dke.task_app.datalog.evaluation.asp;

import at.jku.dke.etutor.task_app.dto.CriterionDto;
import at.jku.dke.etutor.task_app.dto.GradingDto;
import at.jku.dke.etutor.task_app.dto.SubmissionMode;
import at.jku.dke.etutor.task_app.dto.SubmitSubmissionDto;
import at.jku.dke.task_app.datalog.data.repositories.AspTaskRepository;
import at.jku.dke.task_app.datalog.dto.AspSubmissionDto;
import at.jku.dke.task_app.datalog.evaluation.DatalogExecutor;
import at.jku.dke.task_app.datalog.evaluation.EvaluationService;
import at.jku.dke.task_app.datalog.evaluation.asp.analysis.AspAnalysisImpl;
import at.jku.dke.task_app.datalog.evaluation.exceptions.AnalysisException;
import at.jku.dke.task_app.datalog.evaluation.exceptions.ExecutionException;
import at.jku.dke.task_app.datalog.evaluation.exceptions.SyntaxException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Service that evaluates ASP submissions.
 */
@Service
public class AspEvaluationService implements EvaluationService<AspSubmissionDto> {
    private static final Logger LOG = LoggerFactory.getLogger(AspEvaluationService.class);

    private final AspTaskRepository taskRepository;
    private final MessageSource messageSource;
    private final DatalogExecutor executor;

    /**
     * Creates a new instance of class {@link AspEvaluationService}.
     *
     * @param taskRepository The task repository.
     * @param messageSource  The message source.
     * @param executor       The datalog executor.
     */
    public AspEvaluationService(AspTaskRepository taskRepository, MessageSource messageSource, DatalogExecutor executor) {
        this.taskRepository = taskRepository;
        this.messageSource = messageSource;
        this.executor = executor;
    }

    @Override
    public GradingDto evaluate(SubmitSubmissionDto<AspSubmissionDto> submission) {
        // find task
        var task = this.taskRepository.findByIdWithTaskGroup(submission.taskId())
            .orElseThrow(() -> new EntityNotFoundException("Task " + submission.taskId() + " does not exist."));

        // prepare
        LOG.info("Evaluating input for asp-task {} with mode {} and feedback-level {}", submission.taskId(), submission.mode(), submission.feedbackLevel());
        Locale locale = Locale.of(submission.language());

        String facts = submission.mode() == SubmissionMode.SUBMIT ?
            task.getTaskGroup().getSubmissionFacts() :
            task.getTaskGroup().getDiagnoseFacts();
        String solutionResult;
        String submissionResult;

        // execute submission
        try {
            submissionResult = this.executor.execute(facts, submission.submission().input(), task.getMaxN());
        } catch (SyntaxException ex) {
            LOG.warn("Syntax error in input for task {}", submission.taskId());
            List<CriterionDto> criteria = new ArrayList<>();
            criteria.add(new CriterionDto(
                this.messageSource.getMessage("criterium.syntax", null, locale),
                null,
                false,
                "<pre>" + HtmlUtils.htmlEscape(ex.getMessage().replaceFirst("line \\d+: ", "").trim()) + "</pre>"));
            return new GradingDto(task.getMaxPoints(), BigDecimal.ZERO, this.messageSource.getMessage("syntaxError", null, locale), criteria);
        } catch (ExecutionException | IOException ex) {
            LOG.error("Error while evaluating input for task {}", submission.taskId(), ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while evaluating input for task " + submission.taskId(), ex);
        }

        // execute solution
        if (submission.mode() == SubmissionMode.RUN) {
            solutionResult = submissionResult;
        } else {
            try {
                solutionResult = this.executor.execute(facts, task.getSolution(), task.getMaxN());
            } catch (ExecutionException | IOException ex) {
                LOG.error("Error while evaluating solution for task {}", submission.taskId(), ex);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while evaluating solution for task " + submission.taskId(), ex);
            }
        }

        // analyze, grade, feedback
        try {
            var analysis = new AspAnalysisImpl(solutionResult, submissionResult);
            var points = analysis.isCorrect() ? task.getMaxPoints() : BigDecimal.ZERO;
            var reporter = new AspReport(this.messageSource, locale, submission.mode(), submission.feedbackLevel(), analysis, submissionResult);
            return new GradingDto(task.getMaxPoints(), points, reporter.getGeneralFeedback(), reporter.getCriteria());
        } catch (AnalysisException ex) {
            LOG.error("Error while analyzing query result for task {}", submission.taskId(), ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while analysing query result for task " + submission.taskId(), ex);
        }
    }
}
