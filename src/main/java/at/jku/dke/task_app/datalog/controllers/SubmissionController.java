package at.jku.dke.task_app.datalog.controllers;

import at.jku.dke.etutor.task_app.controllers.BaseSubmissionController;
import at.jku.dke.task_app.datalog.data.entities.DatalogSubmission;
import at.jku.dke.task_app.datalog.dto.DatalogSubmissionDto;
import at.jku.dke.task_app.datalog.services.DatalogSubmissionService;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing {@link DatalogSubmission}s.
 */
@RestController
public class SubmissionController extends BaseSubmissionController<DatalogSubmissionDto> {
    /**
     * Creates a new instance of class {@link SubmissionController}.
     *
     * @param submissionService The input service.
     */
    public SubmissionController(DatalogSubmissionService submissionService) {
        super(submissionService);
    }
}
