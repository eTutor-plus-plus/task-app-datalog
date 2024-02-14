package at.jku.dke.task_app.datalog.data.repositories;

import at.jku.dke.etutor.task_app.data.repositories.TaskRepository;
import at.jku.dke.task_app.datalog.data.entities.DatalogTask;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * Repository for entity {@link DatalogTask}.
 */
public interface DatalogTaskRepository extends TaskRepository<DatalogTask> {
    /**
     * Returns the task with the specified id including the task group eagerly loaded.
     *
     * @param id The id of the task.
     * @return The task with the specified id including the task group.
     */
    @Query("SELECT t FROM DatalogTask t LEFT JOIN FETCH t.taskGroup WHERE t.id = :id")
    Optional<DatalogTask> findByIdWithTaskGroup(Long id);
}
