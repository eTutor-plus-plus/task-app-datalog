package at.jku.dke.task_app.datalog.data.entities;

import at.jku.dke.etutor.task_app.dto.TaskStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DatalogTaskTest {

    @Test
    void constructor1() {
        // Act
        var task = new DatalogTask();

        // Assert
        assertNull(task.getQuery());
        assertNull(task.getSolution());
        assertNull(task.getUncheckedTerms());
        assertNull(task.getUncheckedTermsRaw());
    }

    @Test
    void constructor2() {
        // Arrange
        var expectedSolution = "solution";
        var expectedQuery = List.of("query?", "query2?");
        var expectedRaw = "pred1(term1).";
        var expectedUncheckedTerms = List.of(new TermDescription("pred1", "term1", 1));

        // Act
        var task = new DatalogTask(expectedSolution, expectedQuery, expectedRaw);

        // Assert
        assertEquals(expectedSolution, task.getSolution());
        assertEquals(expectedQuery, task.getQuery());
        assertEquals(expectedRaw, task.getUncheckedTermsRaw());
        assertThat(task.getUncheckedTerms()).containsAll(expectedUncheckedTerms);
    }

    @Test
    void constructor3() {
        // Arrange
        var expectedPoints = BigDecimal.TEN;
        var expectedStatus = TaskStatus.APPROVED;
        var expectedGroup = new DatalogTaskGroup();
        var expectedSolution = "solution";
        var expectedQuery = List.of("query?", "query2?");
        var expectedRaw = "pred1(term1).";
        var expectedUncheckedTerms = List.of(new TermDescription("pred1", "term1", 1));

        // Act
        var task = new DatalogTask(expectedPoints, expectedStatus, expectedGroup, expectedSolution, expectedQuery, expectedRaw);

        // Assert
        assertEquals(expectedSolution, task.getSolution());
        assertEquals(expectedQuery, task.getQuery());
        assertEquals(expectedPoints, task.getMaxPoints());
        assertEquals(expectedStatus, task.getStatus());
        assertEquals(expectedGroup, task.getTaskGroup());
        assertEquals(expectedRaw, task.getUncheckedTermsRaw());
        assertThat(task.getUncheckedTerms()).containsAll(expectedUncheckedTerms);
    }

    @Test
    void constructor4() {
        // Arrange
        var expectedId = 99L;
        var expectedPoints = BigDecimal.TEN;
        var expectedStatus = TaskStatus.APPROVED;
        var expectedGroup = new DatalogTaskGroup();
        var expectedSolution = "solution";
        var expectedQuery = List.of("query?", "query2?");
        var expectedRaw = "pred1(term1).";
        var expectedUncheckedTerms = List.of(new TermDescription("pred1", "term1", 1));

        // Act
        var task = new DatalogTask(expectedId, expectedPoints, expectedStatus, expectedGroup, expectedSolution, expectedQuery, expectedRaw);

        // Assert
        assertEquals(expectedId, task.getId());
        assertEquals(expectedSolution, task.getSolution());
        assertEquals(expectedQuery, task.getQuery());
        assertEquals(expectedPoints, task.getMaxPoints());
        assertEquals(expectedStatus, task.getStatus());
        assertEquals(expectedGroup, task.getTaskGroup());
        assertEquals(expectedRaw, task.getUncheckedTermsRaw());
        assertThat(task.getUncheckedTerms()).containsAll(expectedUncheckedTerms);
    }

    @Test
    void getSetSolution() {
        // Arrange
        var task = new DatalogTask();
        var expected = "solution";

        // Act
        task.setSolution(expected);
        var result = task.getSolution();

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void getSetQuery() {
        // Arrange
        var task = new DatalogTask();
        var expected = List.of("query?", "query2?");

        // Act
        task.setQuery(expected);
        var result = task.getQuery();

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void getSetUncheckedTerms() {
        // Arrange
        var task = new DatalogTask();
        var expected = new ArrayList<TermDescription>();
        expected.add(new TermDescription("pred1", "term1", 1));
        expected.add(new TermDescription("pred2", "term2", 2));
        expected.add(new TermDescription("pred2", "term1", 1));

        // Act
        task.setUncheckedTerms(expected);
        var result = task.getUncheckedTerms();

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void getSetUncheckedTermsRaw() {
        // Arrange
        var task = new DatalogTask();
        var input = """
            empty..
            pred1(term1,
            term2).
            pred2(term1).pred3(term4)""";
        var expected = new ArrayList<TermDescription>();
        expected.add(new TermDescription("pred1", "term1", 1));
        expected.add(new TermDescription("pred1", "term2", 2));
        expected.add(new TermDescription("pred2", "term1", 1));
        expected.add(new TermDescription("pred3", "term4", 1));

        // Act
        task.setUncheckedTermsRaw(input);
        var result = task.getUncheckedTerms();
        var rawResult = task.getUncheckedTermsRaw();

        // Assert
        assertEquals(input, rawResult);
        assertThat(result).containsAll(expected);
    }

    @Test
    void getSetMissingPredicatePenalty() {
        // Arrange
        var task = new DatalogTask();
        var expected = BigDecimal.TEN;

        // Act
        task.setMissingPredicatePenalty(expected);
        var result = task.getMissingPredicatePenalty();

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void getSetMissingPredicateStrategy() {
        // Arrange
        var task = new DatalogTask();
        var expected = GradingStrategy.EACH;

        // Act
        task.setMissingPredicateStrategy(expected);
        var result = task.getMissingPredicateStrategy();

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void getSetMissingFactPenalty() {
        // Arrange
        var task = new DatalogTask();
        var expected = BigDecimal.TEN;

        // Act
        task.setMissingFactPenalty(expected);
        var result = task.getMissingFactPenalty();

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void getSetMissingFactStrategy() {
        // Arrange
        var task = new DatalogTask();
        var expected = GradingStrategy.EACH;

        // Act
        task.setMissingFactStrategy(expected);
        var result = task.getMissingFactStrategy();

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void getSetSuperfluousFactPenalty() {
        // Arrange
        var task = new DatalogTask();
        var expected = BigDecimal.TEN;

        // Act
        task.setSuperfluousFactPenalty(expected);
        var result = task.getSuperfluousFactPenalty();

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void getSetSuperfluousFactStrategy() {
        // Arrange
        var task = new DatalogTask();
        var expected = GradingStrategy.EACH;

        // Act
        task.setSuperfluousFactStrategy(expected);
        var result = task.getSuperfluousFactStrategy();

        // Assert
        assertEquals(expected, result);
    }

}
