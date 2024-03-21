package at.jku.dke.task_app.datalog.data.repositories;

import at.jku.dke.etutor.task_app.data.repositories.TaskRepository;
import at.jku.dke.task_app.datalog.data.entities.AspTask;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * Repository for entity {@link AspTask}.
 */
public interface AspTaskRepository extends TaskRepository<AspTask> {
    /**
     * Returns the task with the specified id including the task group eagerly loaded.
     *
     * @param id The id of the task.
     * @return The task with the specified id including the task group.
     */
    @Query("SELECT t FROM AspTask t LEFT JOIN FETCH t.taskGroup WHERE t.id = :id")
    Optional<AspTask> findByIdWithTaskGroup(Long id);
}
