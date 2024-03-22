package at.jku.dke.task_app.datalog.services;

import at.jku.dke.etutor.task_app.dto.SubmissionMode;
import at.jku.dke.etutor.task_app.dto.SubmitSubmissionDto;
import at.jku.dke.task_app.datalog.data.entities.DatalogSubmission;
import at.jku.dke.task_app.datalog.dto.DatalogSubmissionDto;
import at.jku.dke.task_app.datalog.evaluation.EvaluationService;
import at.jku.dke.task_app.datalog.evaluation.dlg.DatalogEvaluationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class DatalogSubmissionServiceTest {

    @Test
    void createSubmissionEntity() {
        // Arrange
        var service = new DatalogSubmissionService(null, null, null);
        var dto = new SubmitSubmissionDto<>("test-user", "test-quiz", 7L, "de", SubmissionMode.SUBMIT, 3, new DatalogSubmissionDto("test-input"));

        // Act
        var submission = service.createSubmissionEntity(dto);

        // Assert
        assertEquals(dto.submission().input(), submission.getSubmission());
    }

    @Test
    void mapSubmissionToSubmissionData() {
        // Arrange
        var service = new DatalogSubmissionService(null, null, null);
        var submission = new DatalogSubmission("test-input");

        // Act
        var dto = service.mapSubmissionToSubmissionData(submission);

        // Assert
        assertEquals(submission.getSubmission(), dto.input());
    }

    @Test
    void evaluate() {
        // Arrange
        var evalService = mock(DatalogEvaluationService.class);
        var dto = new SubmitSubmissionDto<>("test-user", "test-quiz", 7L, "de", SubmissionMode.SUBMIT, 3, new DatalogSubmissionDto("test-input"));
        var service = new DatalogSubmissionService(null, null, evalService);

        // Act
        service.evaluate(dto);

        // Assert
        verify(evalService).evaluate(dto);
    }
}
