package at.jku.dke.task_app.datalog.controllers;

import at.jku.dke.etutor.task_app.controllers.BaseTaskControllerWithoutRequestMapping;
import at.jku.dke.task_app.datalog.data.entities.AspTask;
import at.jku.dke.task_app.datalog.dto.AspTaskDto;
import at.jku.dke.task_app.datalog.dto.ModifyAspTaskDto;
import at.jku.dke.task_app.datalog.services.AspTaskService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * Controller for managing {@link AspTask}s.
 */
@RestController
@RequestMapping("/api/task/asp")
public class AspTaskController extends BaseTaskControllerWithoutRequestMapping<AspTask, AspTaskDto, ModifyAspTaskDto> {

    /**
     * Creates a new instance of class {@link AspTaskController}.
     *
     * @param taskService The task service.
     */
    public AspTaskController(AspTaskService taskService) {
        super(taskService);
    }

    @Override
    protected AspTaskDto mapToDto(AspTask task) {
        return new AspTaskDto(task.getSolution(), task.getMaxN());
    }

    @Override
    protected URI createDetailsUri(long id) {
        return URI.create("/api/task/asp/" + id);
    }

}
