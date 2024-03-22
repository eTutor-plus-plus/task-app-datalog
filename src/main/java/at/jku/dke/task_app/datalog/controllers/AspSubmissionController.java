package at.jku.dke.task_app.datalog.controllers;

import at.jku.dke.etutor.task_app.controllers.BaseSubmissionControllerWithoutRequestMapping;
import at.jku.dke.task_app.datalog.dto.AspSubmissionDto;
import at.jku.dke.task_app.datalog.services.AspSubmissionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

/**
 * Controller for managing {@link at.jku.dke.task_app.datalog.data.entities.AspSubmission}s.
 */
@RestController
@RequestMapping("/api/submission/asp")
public class AspSubmissionController extends BaseSubmissionControllerWithoutRequestMapping<AspSubmissionDto> {
    /**
     * Creates a new instance of class {@link AspSubmissionController}.
     *
     * @param submissionService The input service.
     */
    public AspSubmissionController(AspSubmissionService submissionService) {
        super(submissionService);
    }

    @Override
    protected URI createDetailsUri(UUID id) {
        return URI.create("/api/submission/dlg/" + id + "/result");
    }
}
