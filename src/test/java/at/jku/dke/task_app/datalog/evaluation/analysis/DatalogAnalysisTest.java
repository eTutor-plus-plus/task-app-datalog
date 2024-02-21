package at.jku.dke.task_app.datalog.evaluation.analysis;

import at.jku.dke.task_app.datalog.evaluation.exceptions.AnalysisException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DatalogAnalysisTest {

    @Test
    void constructor_inconsistentQuery(){
        // Arrange
        Map<String, List<String>> submissionResult = Map.of(
            "p1", List.of("a", "b"),
            "p2", List.of("c,d", "e,f,g")
        );
        Map<String, List<String>> solutionResult = Map.of(
            "p1", List.of("a", "b"),
            "p2", List.of("c,d", "e,g")
        );

        // Act & Assert
        assertThrows(AnalysisException.class, () -> new DatalogAnalysis(solutionResult, submissionResult));
    }

    @Test
    void constructor_inconsistentSubmission(){
        // Arrange
        Map<String, List<String>> submissionResult = Map.of(
            "p1", List.of("a", "b"),
            "p2", List.of("c,d", "e,f")
        );
        Map<String, List<String>> solutionResult = Map.of(
            "p1", List.of("a", "b"),
            "p2", List.of("c,d", "e")
        );

        // Act & Assert
        assertThrows(AnalysisException.class, () -> new DatalogAnalysis(solutionResult, submissionResult));
    }

    @Test
    void isCorrect_validResult() throws AnalysisException {
        // Arrange
        Map<String, List<String>> submissionResult = Map.of(
            "p1", List.of("a", "b"),
            "p2", List.of("c,d", "e,f")
        );
        Map<String, List<String>> solutionResult = Map.of(
            "p1", List.of("a", "b"),
            "p2", List.of("c,d", "e,f")
        );
        DatalogAnalysis analysis = new DatalogAnalysis(solutionResult, submissionResult);

        // Act
        boolean result = analysis.isCorrect();

        // Assert
        assertTrue(result);
    }

    @Test
    void isCorrect_invalidResult() throws AnalysisException {
        // Arrange
        Map<String, List<String>> submissionResult = Map.of(
            "p1", List.of("a", "b"),
            "p2", List.of("c,d", "e,f")
        );
        Map<String, List<String>> solutionResult = Map.of(
            "p1", List.of("a", "b"),
            "p2", List.of("c,d", "e,g")
        );
        DatalogAnalysis analysis = new DatalogAnalysis(solutionResult, submissionResult);

        // Act
        boolean result = analysis.isCorrect();

        // Assert
        assertFalse(result);
    }

    @Test
    void getSolutionResult() throws AnalysisException {
        // Arrange
        Map<String, List<String>> submissionResult = Map.of(
            "p1", List.of("a", "b"),
            "p2", List.of("c,d", "e,f")
        );
        Map<String, List<String>> solutionResult = Map.of(
            "p1", List.of("a", "b"),
            "p2", List.of("c,d", "e,f")
        );
        DatalogAnalysis analysis = new DatalogAnalysis(solutionResult, submissionResult);

        // Act
        Map<String, List<String>> result = analysis.getSolutionResult();

        // Assert
        assertEquals(solutionResult, result);
    }

    @Test
    void getSubmissionResult() throws AnalysisException {
        // Arrange
        Map<String, List<String>> submissionResult = Map.of(
            "p1", List.of("a", "b"),
            "p2", List.of("c,d", "e,f")
        );
        Map<String, List<String>> solutionResult = Map.of(
            "p1", List.of("a", "b"),
            "p2", List.of("c,d", "e,f")
        );
        DatalogAnalysis analysis = new DatalogAnalysis(solutionResult, submissionResult);

        // Act
        Map<String, List<String>> result = analysis.getSubmissionResult();

        // Assert
        assertEquals(submissionResult, result);
    }

    @Test
    void getMissingPredicates() throws AnalysisException {
        // Arrange
        Map<String, List<String>> submissionResult = Map.of(
            "p1", List.of("a", "b")
        );
        Map<String, List<String>> solutionResult = Map.of(
            "p1", List.of("a", "b"),
            "p2", List.of("c,d", "e,f")
        );
        DatalogAnalysis analysis = new DatalogAnalysis(solutionResult, submissionResult);

        // Act
        List<DatalogPredicate> result = analysis.getMissingPredicates();

        // Assert
        assertEquals(1, result.size());
        assertEquals("p2", result.getFirst().getName());
    }

    @Test
    void getMissingPredicates2() throws AnalysisException {
        // Arrange
        Map<String, List<String>> submissionResult = Map.of(
            "p1", List.of("a", "b"),
            "p2", List.of()
        );
        Map<String, List<String>> solutionResult = Map.of(
            "p1", List.of("a", "b"),
            "p2", List.of("c,d", "e,f")
        );
        DatalogAnalysis analysis = new DatalogAnalysis(solutionResult, submissionResult);

        // Act
        List<DatalogPredicate> result = analysis.getMissingPredicates();

        // Assert
        assertEquals(1, result.size());
        assertEquals("p2", result.getFirst().getName());
    }

    @Test
    void getMissingFacts() throws AnalysisException {
        // Arrange
        Map<String, List<String>> submissionResult = Map.of(
            "p1", List.of("a", "b"),
            "p2", List.of("c,d")
        );
        Map<String, List<String>> solutionResult = Map.of(
            "p1", List.of("a", "b"),
            "p2", List.of("c,d", "e,f")
        );
        DatalogAnalysis analysis = new DatalogAnalysis(solutionResult, submissionResult);

        // Act
        List<DatalogFact> result = analysis.getMissingFacts();

        // Assert
        assertEquals(1, result.size());
        assertEquals("p2(e, f)", result.getFirst().toString());
    }

    @Test
    void getRedundantFacts() throws AnalysisException {
        // Arrange
        Map<String, List<String>> submissionResult = Map.of(
            "p1", List.of("a", "b"),
            "p2", List.of("c,d", "e,f")
        );
        Map<String, List<String>> solutionResult = Map.of(
            "p1", List.of("a", "b"),
            "p2", List.of("c,d")
        );
        DatalogAnalysis analysis = new DatalogAnalysis(solutionResult, submissionResult);

        // Act
        List<DatalogFact> result = analysis.getRedundantFacts();

        // Assert
        assertEquals(1, result.size());
        assertEquals("p2(e, f)", result.getFirst().toString());
    }

}
