package at.jku.dke.task_app.datalog.services;

import at.jku.dke.etutor.task_app.dto.GradingDto;
import at.jku.dke.etutor.task_app.dto.SubmitSubmissionDto;
import at.jku.dke.etutor.task_app.services.BaseSubmissionService;
import at.jku.dke.task_app.datalog.data.entities.DatalogSubmission;
import at.jku.dke.task_app.datalog.data.entities.DatalogTask;
import at.jku.dke.task_app.datalog.data.repositories.DatalogSubmissionRepository;
import at.jku.dke.task_app.datalog.data.repositories.DatalogTaskRepository;
import at.jku.dke.task_app.datalog.dto.DatalogSubmissionDto;
import at.jku.dke.task_app.datalog.evaluation.EvaluationService;
import org.springframework.stereotype.Service;

/**
 * This class provides methods for managing {@link DatalogSubmission}s.
 */
@Service
public class DatalogSubmissionService extends BaseSubmissionService<DatalogTask, DatalogSubmission, DatalogSubmissionDto> {

    private final EvaluationService evaluationService;

    /**
     * Creates a new instance of class {@link DatalogSubmissionService}.
     *
     * @param submissionRepository The input repository.
     * @param taskRepository       The task repository.
     * @param evaluationService    The evaluation service.
     */
    public DatalogSubmissionService(DatalogSubmissionRepository submissionRepository, DatalogTaskRepository taskRepository, EvaluationService evaluationService) {
        super(submissionRepository, taskRepository);
        this.evaluationService = evaluationService;
    }

    @Override
    protected DatalogSubmission createSubmissionEntity(SubmitSubmissionDto<DatalogSubmissionDto> submitSubmissionDto) {
        return new DatalogSubmission(submitSubmissionDto.submission().input());
    }

    @Override
    protected GradingDto evaluate(SubmitSubmissionDto<DatalogSubmissionDto> submitSubmissionDto) {
        return this.evaluationService.evaluate(submitSubmissionDto);
    }

    @Override
    protected DatalogSubmissionDto mapSubmissionToSubmissionData(DatalogSubmission submission) {
        return new DatalogSubmissionDto(submission.getSubmission());
    }

}
