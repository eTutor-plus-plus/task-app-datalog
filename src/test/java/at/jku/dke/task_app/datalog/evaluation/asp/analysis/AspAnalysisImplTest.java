package at.jku.dke.task_app.datalog.evaluation.asp.analysis;

import at.jku.dke.task_app.datalog.evaluation.DatalogPredicate;
import at.jku.dke.task_app.datalog.evaluation.exceptions.AnalysisException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AspAnalysisImplTest {

    @Test
    void constructor_inconsistentSolution() {
        // Arrange
        String solutionResult = "{p1(a,b). p1(c,d,e).}";
        String submissionResult = "{p1(a,b). p1(c,d).}";

        // Act & Assert
        assertThrows(AnalysisException.class, () -> new AspAnalysisImpl(solutionResult, submissionResult));
    }

    @Test
    void constructor_inconsistentSubmission() {
        // Arrange
        String solutionResult = "{p1(a,b). p1(c,d).}";
        String submissionResult = "{p1(a,b). p1(c,d,e).}";

        // Act & Assert
        assertThrows(AnalysisException.class, () -> new AspAnalysisImpl(solutionResult, submissionResult));
    }

    @Test
    void isCorrect_validResult() throws AnalysisException {
        // Arrange
        String solutionResult = "{p1(a,b). p1(c,d).}{p1(b,c). p1(a,d).}";
        String submissionResult = "{p1(a,b). p1(c,d).}{p1(b,c). p1(a,d).}";
        AspAnalysis analysis = new AspAnalysisImpl(solutionResult, submissionResult);

        // Act
        boolean result = analysis.isCorrect();

        // Assert
        assertTrue(result);
    }

    @Test
    void isCorrect_invalidResult() throws AnalysisException {
        // Arrange
        String solutionResult = "{p1(a,b). p1(c,d).}{p1(b,c). p1(a,d).}";
        String submissionResult = "{p1(a,b). p1(c,d).}{p1(b,c). p1(a,e).}";
        AspAnalysis analysis = new AspAnalysisImpl(solutionResult, submissionResult);

        // Act
        boolean result = analysis.isCorrect();

        // Assert
        assertFalse(result);
    }

    @Test
    void hasSameAmountOfModels_validResult() throws AnalysisException {
        // Arrange
        String solutionResult = "{p1(a,b). p1(c,d).}{p1(b,c). p1(a,d).}";
        String submissionResult = "{p1(a,b). p1(c,d).}{p1(b,c). p1(a,d).}";
        AspAnalysis analysis = new AspAnalysisImpl(solutionResult, submissionResult);

        // Act
        boolean result = analysis.hasSameAmountOfModels();

        // Assert
        assertTrue(result);
    }

    @Test
    void hasSameAmountOfModels_invalidResult() throws AnalysisException {
        // Arrange
        String solutionResult = "{p1(a,b). p1(c,d).}{p1(b,c). p1(a,d).}";
        String submissionResult = "{p1(a,b). p1(c,d).}";
        AspAnalysis analysis = new AspAnalysisImpl(solutionResult, submissionResult);

        // Act
        boolean result = analysis.hasSameAmountOfModels();

        // Assert
        assertFalse(result);
    }

    @Test
    void getSolutionResult() throws AnalysisException {
        // Arrange
        String solutionResult = "{p1(a,b). p1(c,d).}{p1(b,c). p1(a,d).}";
        String submissionResult = "{p1(a,b). p1(c,d).}{p1(b,c). p1(a,b).}";
        AspAnalysis analysis = new AspAnalysisImpl(solutionResult, submissionResult);

        // Act
        var result = analysis.getSolutionResult();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.getFirst())
            .anyMatch(p -> p.getName().equals("p1") && p.getFacts().stream().anyMatch(f -> Arrays.asList(f.getTerms()).contains("a") && Arrays.asList(f.getTerms()).contains("b")))
            .anyMatch(p -> p.getName().equals("p1") && p.getFacts().stream().anyMatch(f -> Arrays.asList(f.getTerms()).contains("c") && Arrays.asList(f.getTerms()).contains("d")));
        assertThat(result.getLast())
            .anyMatch(p -> p.getName().equals("p1") && p.getFacts().stream().anyMatch(f -> Arrays.asList(f.getTerms()).contains("c") && Arrays.asList(f.getTerms()).contains("b")))
            .anyMatch(p -> p.getName().equals("p1") && p.getFacts().stream().anyMatch(f -> Arrays.asList(f.getTerms()).contains("a") && Arrays.asList(f.getTerms()).contains("d")));
    }

    @Test
    void getSubmissionResult() throws AnalysisException {
        // Arrange
        String solutionResult = "{p1(a,b). p1(c,d).}{p1(b,c). p1(a,d).}";
        String submissionResult = "{p1(a,b). p1(c,d).}{p1(b,c). p1(a,b).}";
        AspAnalysis analysis = new AspAnalysisImpl(solutionResult, submissionResult);

        // Act
        var result = analysis.getSubmissionResult();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.getFirst())
            .anyMatch(p -> p.getName().equals("p1") && p.getFacts().stream().anyMatch(f -> Arrays.asList(f.getTerms()).contains("a") && Arrays.asList(f.getTerms()).contains("b")))
            .anyMatch(p -> p.getName().equals("p1") && p.getFacts().stream().anyMatch(f -> Arrays.asList(f.getTerms()).contains("c") && Arrays.asList(f.getTerms()).contains("d")));
        assertThat(result.getLast())
            .anyMatch(p -> p.getName().equals("p1") && p.getFacts().stream().anyMatch(f -> Arrays.asList(f.getTerms()).contains("c") && Arrays.asList(f.getTerms()).contains("b")))
            .anyMatch(p -> p.getName().equals("p1") && p.getFacts().stream().anyMatch(f -> Arrays.asList(f.getTerms()).contains("a") && Arrays.asList(f.getTerms()).contains("b")));

    }

    @Test
    void getMissingModels() throws AnalysisException {
        // Arrange
        String solutionResult = "{p1(a,b). p1(c,d).}{p1(b,c). p1(a,d).}";
        String submissionResult = "{p1(a,b). p1(c,d).}{p1(a,c). p1(a,d).}";
        AspAnalysis analysis = new AspAnalysisImpl(solutionResult, submissionResult);

        // Act
        List<Set<DatalogPredicate>> result = analysis.getMissingModels();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getFirst())
            .anyMatch(p -> p.getName().equals("p1") && p.getFacts().stream().anyMatch(f -> Arrays.asList(f.getTerms()).contains("c") && Arrays.asList(f.getTerms()).contains("b")))
            .anyMatch(p -> p.getName().equals("p1") && p.getFacts().stream().anyMatch(f -> Arrays.asList(f.getTerms()).contains("a") && Arrays.asList(f.getTerms()).contains("d")));
    }

    @Test
    void getSuperfluousModels() throws AnalysisException {
        // Arrange
        String solutionResult = "{p1(a,b). p1(c,d).}{p1(a,c). p1(a,d).}";
        String submissionResult = "{p1(a,b). p1(c,d).}{p1(b,c). p1(a,d).}";
        AspAnalysis analysis = new AspAnalysisImpl(solutionResult, submissionResult);

        // Act
        List<Set<DatalogPredicate>> result = analysis.getSuperfluousModels();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getFirst())
            .anyMatch(p -> p.getName().equals("p1") && p.getFacts().stream().anyMatch(f -> Arrays.asList(f.getTerms()).contains("c") && Arrays.asList(f.getTerms()).contains("b")))
            .anyMatch(p -> p.getName().equals("p1") && p.getFacts().stream().anyMatch(f -> Arrays.asList(f.getTerms()).contains("a") && Arrays.asList(f.getTerms()).contains("d")));
    }
}
