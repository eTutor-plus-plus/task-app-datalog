package at.jku.dke.task_app.datalog.controllers;

import at.jku.dke.etutor.task_app.controllers.BaseTaskGroupController;
import at.jku.dke.task_app.datalog.data.entities.DatalogTaskGroup;
import at.jku.dke.task_app.datalog.dto.DatalogTaskGroupDto;
import at.jku.dke.task_app.datalog.dto.ModifyDatalogTaskGroupDto;
import at.jku.dke.task_app.datalog.services.DatalogTaskGroupService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing {@link DatalogTaskGroup}s.
 */
@RestController
public class TaskGroupController extends BaseTaskGroupController<DatalogTaskGroup, DatalogTaskGroupDto, ModifyDatalogTaskGroupDto> {

    /**
     * Creates a new instance of class {@link TaskGroupController}.
     *
     * @param taskGroupService The task group service.
     */
    public TaskGroupController(DatalogTaskGroupService taskGroupService) {
        super(taskGroupService);
    }

    @Override
    protected DatalogTaskGroupDto mapToDto(DatalogTaskGroup taskGroup) {
        return new DatalogTaskGroupDto(taskGroup.getDiagnoseFacts(), taskGroup.getSubmissionFacts());
    }

    /**
     * Returns the public URL of the diagnose document for the specified task group.
     *
     * @param id The id of the task group.
     * @return The public URL to the diagnose document.
     */
    @GetMapping(value = "{id}/public", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getPublicUrl(@PathVariable long id) {
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_PLAIN)
            .body(((DatalogTaskGroupService) this.taskGroupService).getPublicUrl(id));
    }
}
