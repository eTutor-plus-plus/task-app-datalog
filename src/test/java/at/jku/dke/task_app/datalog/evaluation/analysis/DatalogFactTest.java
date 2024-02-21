package at.jku.dke.task_app.datalog.evaluation.analysis;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DatalogFactTest {

    @Test
    void getPredicate() {
        // Arrange
        DatalogPredicate predicate = new DatalogPredicate("predicate", List.of());
        DatalogFact fact = new DatalogFact(predicate, "term1,term2");

        // Act
        DatalogPredicate result = fact.getPredicate();

        // Assert
        assertEquals(predicate, result);
    }

    @Test
    void getArity() {
        // Arrange
        DatalogPredicate predicate = new DatalogPredicate("predicate", List.of());
        DatalogFact fact = new DatalogFact(predicate, "term1, term2");

        // Act
        int result = fact.getArity();

        // Assert
        assertEquals(2, result);
    }

    @Test
    void getTerms() {
        // Arrange
        DatalogPredicate predicate = new DatalogPredicate("predicate", List.of());
        DatalogFact fact = new DatalogFact(predicate, "term1,   term2");

        // Act
        String[] result = fact.getTerms();

        // Assert
        assertThat(result).containsExactly("term1", "term2");
    }

    @Test
    void testToString() {
        // Arrange
        DatalogPredicate predicate = new DatalogPredicate("predicate", List.of());
        DatalogFact fact = new DatalogFact(predicate, "term1, term2");

        // Act
        String result = fact.toString();

        // Assert
        assertEquals("predicate(term1, term2)", result);
    }

    @Test
    void equals_same() {
        // Arrange
        DatalogPredicate predicate = new DatalogPredicate("predicate", List.of());
        DatalogFact fact1 = new DatalogFact(predicate, "term1, term2");
        //noinspection UnnecessaryLocalVariable
        DatalogFact fact2 = fact1;

        // Act
        //noinspection ConstantValue
        boolean result = fact1.equals(fact2);

        // Assert
        assertTrue(result);
    }

    @Test
    void equals_null() {
        // Arrange
        DatalogPredicate predicate = new DatalogPredicate("predicate", List.of());
        DatalogFact fact = new DatalogFact(predicate, "term1, term2");

        // Act
        //noinspection ConstantValue
        boolean result = fact.equals(null);

        // Assert
        assertFalse(result);
    }

    @Test
    void equals_differentClass() {
        // Arrange
        DatalogPredicate predicate = new DatalogPredicate("predicate", List.of());
        DatalogFact fact = new DatalogFact(predicate, "term1, term2");

        // Act
        //noinspection EqualsBetweenInconvertibleTypes
        boolean result = fact.equals("test");

        // Assert
        assertFalse(result);
    }

    @Test
    void equals_differentPredicate() {
        // Arrange
        DatalogPredicate predicate1 = new DatalogPredicate("predicate1", List.of());
        DatalogPredicate predicate2 = new DatalogPredicate("predicate2", List.of());
        DatalogFact fact1 = new DatalogFact(predicate1, "term1, term2");
        DatalogFact fact2 = new DatalogFact(predicate2, "term1, term2");

        // Act
        boolean result = fact1.equals(fact2);

        // Assert
        assertFalse(result);
    }

    @Test
    void equals_differentTerms() {
        // Arrange
        DatalogPredicate predicate = new DatalogPredicate("predicate", List.of());
        DatalogFact fact1 = new DatalogFact(predicate, "term1, term2");
        DatalogFact fact2 = new DatalogFact(predicate, "term1, term3");

        // Act
        boolean result = fact1.equals(fact2);

        // Assert
        assertFalse(result);
    }

    @Test
    void equals_samePredicateAndTerms() {
        // Arrange
        DatalogPredicate predicate = new DatalogPredicate("predicate", List.of());
        DatalogFact fact1 = new DatalogFact(predicate, "term1, term2");
        DatalogFact fact2 = new DatalogFact(predicate, "term1, term2");

        // Act
        boolean result = fact1.equals(fact2);

        // Assert
        assertTrue(result);
    }

    @Test
    void testHashCode() {
        // Arrange
        DatalogPredicate predicate = new DatalogPredicate("predicate", List.of());
        DatalogFact fact = new DatalogFact(predicate, "term1, term2");
        DatalogFact fact2 = new DatalogFact(predicate, "term1, term3");

        // Act
        int result1 = fact.hashCode();
        int result2 = fact2.hashCode();

        // Assert
        assertNotEquals(result1, result2);
    }
}
