package at.jku.dke.task_app.datalog.controllers;

import at.jku.dke.etutor.task_app.controllers.BaseTaskGroupController;
import at.jku.dke.task_app.datalog.data.entities.DatalogTaskGroup;
import at.jku.dke.task_app.datalog.dto.DatalogTaskGroupDto;
import at.jku.dke.task_app.datalog.dto.ModifyDatalogTaskGroupDto;
import at.jku.dke.task_app.datalog.services.DatalogTaskGroupService;
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
        return new DatalogTaskGroupDto(taskGroup.getFacts());
    }

}
