package at.jku.dke.task_app.datalog.data.entities;

import at.jku.dke.etutor.task_app.dto.TaskStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AspTaskTest {

    @Test
    void constructor1() {
        // Act
        var task = new AspTask();

        // Assert
        assertNull(task.getSolution());
        assertNull(task.getMaxN());
    }

    @Test
    void constructor2() {
        // Arrange
        var expectedSolution = "solution";
        var expectedMaxN = 3;

        // Act
        var task = new AspTask(expectedSolution, expectedMaxN);

        // Assert
        assertEquals(expectedSolution, task.getSolution());
        assertEquals(expectedMaxN, task.getMaxN());
    }

    @Test
    void constructor3() {
        // Arrange
        var expectedPoints = BigDecimal.TEN;
        var expectedStatus = TaskStatus.APPROVED;
        var expectedGroup = new DatalogTaskGroup();
        var expectedSolution = "solution";
        var expectedMaxN = 3;


        // Act
        var task = new AspTask(expectedPoints, expectedStatus, expectedGroup, expectedSolution, expectedMaxN);

        // Assert
        assertEquals(expectedSolution, task.getSolution());
        assertEquals(expectedPoints, task.getMaxPoints());
        assertEquals(expectedStatus, task.getStatus());
        assertEquals(expectedGroup, task.getTaskGroup());
        assertEquals(expectedMaxN, task.getMaxN());
    }

    @Test
    void constructor4() {
        // Arrange
        var expectedId = 99L;
        var expectedPoints = BigDecimal.TEN;
        var expectedStatus = TaskStatus.APPROVED;
        var expectedGroup = new DatalogTaskGroup();
        var expectedSolution = "solution";
        var expectedMaxN = 3;

        // Act
        var task = new AspTask(expectedId, expectedPoints, expectedStatus, expectedGroup, expectedSolution, expectedMaxN);

        // Assert
        assertEquals(expectedId, task.getId());
        assertEquals(expectedSolution, task.getSolution());
        assertEquals(expectedPoints, task.getMaxPoints());
        assertEquals(expectedStatus, task.getStatus());
        assertEquals(expectedGroup, task.getTaskGroup());
        assertEquals(expectedMaxN, task.getMaxN());
    }

    @Test
    void getSetSolution() {
        // Arrange
        var task = new AspTask();
        var expected = "solution";

        // Act
        task.setSolution(expected);
        var result = task.getSolution();

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void getSetMaxN() {
        // Arrange
        var task = new AspTask();
        var expected = 9;

        // Act
        task.setMaxN(expected);
        var result = task.getMaxN();

        // Assert
        assertEquals(expected, result);
    }

}
