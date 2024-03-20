package at.jku.dke.task_app.datalog.controllers;

import at.jku.dke.etutor.task_app.controllers.BaseTaskController;
import at.jku.dke.task_app.datalog.data.entities.DatalogTask;
import at.jku.dke.task_app.datalog.dto.DatalogTaskDto;
import at.jku.dke.task_app.datalog.dto.ModifyDatalogTaskDto;
import at.jku.dke.task_app.datalog.services.DatalogTaskService;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing {@link DatalogTask}s.
 */
@RestController
public class TaskController extends BaseTaskController<DatalogTask, DatalogTaskDto, ModifyDatalogTaskDto> {

    /**
     * Creates a new instance of class {@link TaskController}.
     *
     * @param taskService The task service.
     */
    public TaskController(DatalogTaskService taskService) {
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

}
