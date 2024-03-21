package at.jku.dke.task_app.datalog.services;

import at.jku.dke.etutor.task_app.dto.GradingDto;
import at.jku.dke.etutor.task_app.dto.SubmitSubmissionDto;
import at.jku.dke.etutor.task_app.services.BaseSubmissionService;
import at.jku.dke.task_app.datalog.data.entities.AspSubmission;
import at.jku.dke.task_app.datalog.data.entities.AspTask;
import at.jku.dke.task_app.datalog.data.repositories.AspSubmissionRepository;
import at.jku.dke.task_app.datalog.data.repositories.AspTaskRepository;
import at.jku.dke.task_app.datalog.dto.AspSubmissionDto;
import at.jku.dke.task_app.datalog.evaluation.asp.AspEvaluationService;
import org.springframework.stereotype.Service;

/**
 * This class provides methods for managing {@link AspSubmission}s.
 */
@Service
public class AspSubmissionService extends BaseSubmissionService<AspTask, AspSubmission, AspSubmissionDto> {

    private final AspEvaluationService evaluationService;

    /**
     * Creates a new instance of class {@link AspSubmissionService}.
     *
     * @param submissionRepository The input repository.
     * @param taskRepository       The task repository.
     * @param evaluationService    The evaluation service.
     */
    public AspSubmissionService(AspSubmissionRepository submissionRepository, AspTaskRepository taskRepository, AspEvaluationService evaluationService) {
        super(submissionRepository, taskRepository);
        this.evaluationService = evaluationService;
    }

    @Override
    protected AspSubmission createSubmissionEntity(SubmitSubmissionDto<AspSubmissionDto> submitSubmissionDto) {
        return new AspSubmission(submitSubmissionDto.submission().input());
    }

    @Override
    protected GradingDto evaluate(SubmitSubmissionDto<AspSubmissionDto> submitSubmissionDto) {
        return this.evaluationService.evaluate(submitSubmissionDto);
    }

    @Override
    protected AspSubmissionDto mapSubmissionToSubmissionData(AspSubmission submission) {
        return new AspSubmissionDto(submission.getSubmission());
    }

}
