package at.jku.dke.task_app.datalog.evaluation.grading;

import at.jku.dke.task_app.datalog.data.entities.DatalogTask;
import at.jku.dke.task_app.datalog.data.entities.GradingStrategy;
import at.jku.dke.task_app.datalog.evaluation.analysis.DatalogAnalysis;
import at.jku.dke.task_app.datalog.evaluation.analysis.DatalogFact;
import at.jku.dke.task_app.datalog.evaluation.analysis.DatalogPredicate;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DatalogGradingTest {

    @Test
    void getTask() {
        // Arrange
        DatalogAnalysis analysis = mock(DatalogAnalysis.class);
        DatalogTask task = new DatalogTask();
        task.setMaxPoints(BigDecimal.TEN);

        // Act
        var grading = new DatalogGrading(task, analysis);

        // Assert
        assertEquals(task, grading.getTask());
    }

    @Test
    void getAnalysis() {
        // Arrange
        DatalogAnalysis analysis = mock(DatalogAnalysis.class);
        DatalogTask task = new DatalogTask();
        task.setMaxPoints(BigDecimal.TEN);

        // Act
        var grading = new DatalogGrading(task, analysis);

        // Assert
        assertEquals(analysis, grading.getAnalysis());
    }

    @Test
    void grading_correct() {
        // Arrange
        DatalogAnalysis analysis = mock(DatalogAnalysis.class);
        when(analysis.isCorrect()).thenReturn(true);
        DatalogTask task = new DatalogTask();
        task.setMaxPoints(BigDecimal.TEN);

        // Act
        var grading = new DatalogGrading(task, analysis);

        // Assert
        assertEquals(BigDecimal.TEN, grading.getPoints());
        assertThat(grading.getDetails()).isEmpty();
        assertTrue(grading.isCorrect());
    }

    @Test
    void grading_missingPredicate() {
        // Arrange
        DatalogAnalysis analysis = mock(DatalogAnalysis.class);
        when(analysis.isCorrect()).thenReturn(false);
        when(analysis.getMissingPredicates()).thenReturn(List.of(new DatalogPredicate("p1", List.of()), new DatalogPredicate("p1", List.of())));

        DatalogTask task = new DatalogTask();
        task.setMaxPoints(BigDecimal.TEN);
        task.setMissingPredicateStrategy(GradingStrategy.EACH);
        task.setMissingPredicatePenalty(BigDecimal.ONE);

        // Act
        var grading = new DatalogGrading(task, analysis);

        // Assert
        assertEquals(BigDecimal.valueOf(8), grading.getPoints());
        assertFalse(grading.isCorrect());
        assertThat(grading.getDetails())
            .containsExactly(new GradingEntry(GradingEntry.MISSING_PREDICATE, task.getMissingPredicatePenalty().multiply(BigDecimal.valueOf(2))));
    }

    @Test
    void grading_missingFact() {
        // Arrange
        DatalogAnalysis analysis = mock(DatalogAnalysis.class);
        when(analysis.isCorrect()).thenReturn(false);
        when(analysis.getMissingFacts()).thenReturn(List.of(new DatalogFact(new DatalogPredicate("p1", List.of()), "test,person"), new DatalogFact(new DatalogPredicate("p2", List.of()), "test,person")));

        DatalogTask task = new DatalogTask();
        task.setMaxPoints(BigDecimal.TEN);
        task.setMissingFactStrategy(GradingStrategy.GROUP);
        task.setMissingFactPenalty(BigDecimal.ONE);

        // Act
        var grading = new DatalogGrading(task, analysis);

        // Assert
        assertEquals(BigDecimal.valueOf(9), grading.getPoints());
        assertFalse(grading.isCorrect());
        assertThat(grading.getDetails())
            .containsExactly(new GradingEntry(GradingEntry.MISSING_FACT, task.getMissingFactPenalty()));
    }

    @Test
    void grading_superfluousFact() {
        // Arrange
        DatalogAnalysis analysis = mock(DatalogAnalysis.class);
        when(analysis.isCorrect()).thenReturn(false);
        when(analysis.getSuperfluousFacts()).thenReturn(List.of(new DatalogFact(new DatalogPredicate("p1", List.of()), "test,person"), new DatalogFact(new DatalogPredicate("p2", List.of()), "test,person")));

        DatalogTask task = new DatalogTask();
        task.setMaxPoints(BigDecimal.TEN);
        task.setMissingPredicateStrategy(GradingStrategy.KO);
        task.setMissingPredicatePenalty(BigDecimal.ONE);

        // Act
        var grading = new DatalogGrading(task, analysis);
        var details = grading.getDetails(GradingEntry.SUPERFLUOUS_FACT);

        // Assert
        assertEquals(BigDecimal.ZERO, grading.getPoints());
        assertFalse(grading.isCorrect());
        assertNotNull(details);
        assertThat(grading.getDetails())
            .containsExactly(new GradingEntry(GradingEntry.SUPERFLUOUS_FACT, task.getMaxPoints()));
    }
}
