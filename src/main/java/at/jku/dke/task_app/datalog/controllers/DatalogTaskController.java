package at.jku.dke.task_app.datalog.controllers;

import at.jku.dke.etutor.task_app.controllers.BaseTaskControllerWithoutRequestMapping;
import at.jku.dke.task_app.datalog.data.entities.DatalogTask;
import at.jku.dke.task_app.datalog.dto.DatalogTaskDto;
import at.jku.dke.task_app.datalog.dto.ModifyDatalogTaskDto;
import at.jku.dke.task_app.datalog.services.DatalogTaskService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * Controller for managing {@link DatalogTask}s.
 */
@RestController
@RequestMapping("/api/task/dlg")
public class DatalogTaskController extends BaseTaskControllerWithoutRequestMapping<DatalogTask, DatalogTaskDto, ModifyDatalogTaskDto> {

    /**
     * Creates a new instance of class {@link DatalogTaskController}.
     *
     * @param taskService The task service.
     */
    public DatalogTaskController(DatalogTaskService taskService) {
        super(taskService);
    }

    @Override
    protected DatalogTaskDto mapToDto(DatalogTask task) {
        return new DatalogTaskDto(
            task.getSolution(),
            String.join(";", task.getQuery()),
            task.getUncheckedTermsRaw(),
            task.getMissingPredicatePenalty(),
            task.getMissingFactPenalty(),
            task.getSuperfluousFactPenalty(),
            task.getMissingPredicateStrategy(),
            task.getMissingFactStrategy(),
            task.getSuperfluousFactStrategy());
    }

    @Override
    protected URI createDetailsUri(long id) {
        return URI.create("/api/task/dlg/" + id);
    }

}
