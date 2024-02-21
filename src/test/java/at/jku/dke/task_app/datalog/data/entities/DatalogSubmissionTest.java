package at.jku.dke.task_app.datalog.data.entities;

import at.jku.dke.etutor.task_app.dto.SubmissionMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DatalogSubmissionTest {

    @Test
    void constructor1() {
        // Act
        var submission = new DatalogSubmission();

        // Assert
        assertNull(submission.getSubmission());
    }

    @Test
    void constructor2() {
        // Arrange
        var expected = "test";

        // Act
        var submission = new DatalogSubmission(expected);
        var actual = submission.getSubmission();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    void constructor3() {
        // Arrange
        var expected = "test";
        var task = new DatalogTask();
        var userId = "test";
        var assignmentId = "test";
        var language = "test";
        var feedbackLevel = 1;
        var mode = SubmissionMode.RUN;

        // Act
        var submission = new DatalogSubmission(userId, assignmentId, task, language, feedbackLevel, mode, expected);

        // Assert
        assertEquals(expected, submission.getSubmission());
        assertEquals(userId, submission.getUserId());
        assertEquals(assignmentId, submission.getAssignmentId());
        assertEquals(task, submission.getTask());
        assertEquals(language, submission.getLanguage());
        assertEquals(feedbackLevel, submission.getFeedbackLevel());
        assertEquals(mode, submission.getMode());
    }

    @Test
    void getSetSubmission() {
        // Arrange
        var submission = new DatalogSubmission();
        var expected = "test";

        // Act
        submission.setSubmission(expected);
        var actual = submission.getSubmission();

        // Assert
        assertEquals(expected, actual);
    }

}
