package at.jku.dke.task_app.datalog.controllers;

import at.jku.dke.task_app.datalog.data.repositories.DatalogTaskGroupRepository;
import at.jku.dke.task_app.datalog.services.HashIds;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for accessing datalog facts.
 */
@RestController
@RequestMapping("/dlg")
@Tag(name = "Datalog", description = "Access to datalog facts.")
public class DatalogController {

    private final DatalogTaskGroupRepository taskGroupRepository;

    /**
     * Creates a new instance of class {@link DatalogController}.
     *
     * @param taskGroupRepository The task group repository.
     */
    public DatalogController(DatalogTaskGroupRepository taskGroupRepository) {
        this.taskGroupRepository = taskGroupRepository;
    }

    /**
     * Returns the datalog facts for the given task group.
     *
     * @param id The task group id.
     * @return The datalog facts.
     */
    @GetMapping(value = "/{id}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getFacts(@PathVariable String id) {
        long taskGroupId = HashIds.decode(id);
        var taskGroup = this.taskGroupRepository.findById(taskGroupId).orElseThrow(() -> new EntityNotFoundException("Datalog facts not found."));
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_PLAIN)
            .body(taskGroup.getFacts());
    }
}
