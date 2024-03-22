package at.jku.dke.task_app.datalog.controllers;

import at.jku.dke.etutor.task_app.controllers.BaseSubmissionControllerWithoutRequestMapping;
import at.jku.dke.task_app.datalog.data.entities.DatalogSubmission;
import at.jku.dke.task_app.datalog.dto.DatalogSubmissionDto;
import at.jku.dke.task_app.datalog.services.DatalogSubmissionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

/**
 * Controller for managing {@link DatalogSubmission}s.
 */
@RestController
@RequestMapping("/api/submission/dlg")
public class DatalogSubmissionController extends BaseSubmissionControllerWithoutRequestMapping<DatalogSubmissionDto> {
    /**
     * Creates a new instance of class {@link DatalogSubmissionController}.
     *
     * @param submissionService The input service.
     */
    public DatalogSubmissionController(DatalogSubmissionService submissionService) {
        super(submissionService);
    }

    @Override
    protected URI createDetailsUri(UUID id) {
        return URI.create("/api/submission/dlg/" + id + "/result");
    }
}
