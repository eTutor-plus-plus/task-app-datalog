package at.jku.dke.task_app.datalog.data.entities;

import at.jku.dke.etutor.task_app.dto.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DatalogTaskGroupTest {

    @Test
    void constructor1() {
        // Act
        DatalogTaskGroup datalogTaskGroup = new DatalogTaskGroup();
        String diagnoseFacts = datalogTaskGroup.getDiagnoseFacts();
        String submissionFacts = datalogTaskGroup.getSubmissionFacts();

        // Assert
        assertNull(diagnoseFacts);
        assertNull(submissionFacts);
    }

    @Test
    void constructor2() {
        // Arrange
        String diagnoseFacts = "diagnoseFacts";
        String submissionFacts = "submissionFacts";

        // Act
        DatalogTaskGroup datalogTaskGroup = new DatalogTaskGroup(diagnoseFacts, submissionFacts);
        String resultDiagnoseFacts = datalogTaskGroup.getDiagnoseFacts();
        String resultSubmissionFacts = datalogTaskGroup.getSubmissionFacts();

        // Assert
        assertEquals(diagnoseFacts, resultDiagnoseFacts);
        assertEquals(submissionFacts, resultSubmissionFacts);
    }

    @Test
    void constructor3() {
        // Arrange
        TaskStatus status = TaskStatus.READY_FOR_APPROVAL;
        String diagnoseFacts = "diagnoseFacts";
        String submissionFacts = "submissionFacts";

        // Act
        DatalogTaskGroup datalogTaskGroup = new DatalogTaskGroup(status, diagnoseFacts, submissionFacts);
        String resultDiagnoseFacts = datalogTaskGroup.getDiagnoseFacts();
        String resultSubmissionFacts = datalogTaskGroup.getSubmissionFacts();
        TaskStatus resultStatus = datalogTaskGroup.getStatus();

        // Assert
        assertEquals(diagnoseFacts, resultDiagnoseFacts);
        assertEquals(submissionFacts, resultSubmissionFacts);
        assertEquals(status, resultStatus);
    }

    @Test
    void constructor4() {
        // Arrange
        Long id = 4L;
        TaskStatus status = TaskStatus.READY_FOR_APPROVAL;
        String diagnoseFacts = "diagnoseFacts";
        String submissionFacts = "submissionFacts";

        // Act
        DatalogTaskGroup datalogTaskGroup = new DatalogTaskGroup(id, status, diagnoseFacts, submissionFacts);
        String resultDiagnoseFacts = datalogTaskGroup.getDiagnoseFacts();
        String resultSubmissionFacts = datalogTaskGroup.getSubmissionFacts();
        TaskStatus resultStatus = datalogTaskGroup.getStatus();
        Long resultId = datalogTaskGroup.getId();

        // Assert
        assertEquals(diagnoseFacts, resultDiagnoseFacts);
        assertEquals(submissionFacts, resultSubmissionFacts);
        assertEquals(status, resultStatus);
        assertEquals(id, resultId);
    }

    @Test
    void getSetDiagnoseFacts() {
        // Arrange
        DatalogTaskGroup datalogTaskGroup = new DatalogTaskGroup();
        String diagnoseFacts = "diagnoseFacts";

        // Act
        datalogTaskGroup.setDiagnoseFacts(diagnoseFacts);
        String result = datalogTaskGroup.getDiagnoseFacts();

        // Assert
        assertEquals(diagnoseFacts, result);
    }

    @Test
    void getSetSubmissionFacts() {
        // Arrange
        DatalogTaskGroup datalogTaskGroup = new DatalogTaskGroup();
        String submissionFacts = "submissionFacts";

        // Act
        datalogTaskGroup.setSubmissionFacts(submissionFacts);
        String result = datalogTaskGroup.getSubmissionFacts();

        // Assert
        assertEquals(submissionFacts, result);
    }

}
