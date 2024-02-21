package at.jku.dke.task_app.datalog.evaluation.analysis;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatalogPredicateTest {

    @Test
    void isConsistent_true() {
        // Arrange
        DatalogPredicate predicate = new DatalogPredicate("test", List.of("a(1,2,3)", "a(4,5,6)"));

        // Act
        boolean result = predicate.isConsistent();

        // Assert
        assertTrue(result);
    }

    @Test
    void isConsistent_false() {
        // Arrange
        DatalogPredicate predicate = new DatalogPredicate("test", List.of("a(1,2,3)", "a(4,5)"));

        // Act
        boolean result = predicate.isConsistent();

        // Assert
        assertFalse(result);
    }

    @Test
    void getName() {
        // Arrange
        final String name = "test";
        DatalogPredicate predicate = new DatalogPredicate(name, List.of("a(1,2,3)", "a(4,5,6)"));

        // Act
        String result = predicate.getName();

        // Assert
        assertEquals(name, result);
    }

    @Test
    void getArity_zero() {
        // Arrange
        DatalogPredicate predicate = new DatalogPredicate("test", List.of());

        // Act
        int result = predicate.getArity();

        // Assert
        assertEquals(0, result);
    }

    @Test
    void getArity() {
        // Arrange
        DatalogPredicate predicate = new DatalogPredicate("test", List.of("a(1,2,3)", "a(4,5,6)"));

        // Act
        int result = predicate.getArity();

        // Assert
        assertEquals(3, result);
    }

    @Test
    void getFacts() {
        // Arrange
        DatalogPredicate predicate = new DatalogPredicate("test", List.of("1,2,3", "4,5,6"));

        // Act
        List<DatalogFact> result = predicate.getFacts();

        // Assert
        assertEquals(2, result.size());
        assertEquals("test(1, 2, 3)", result.get(0).toString());
        assertEquals("test(4, 5, 6)", result.get(1).toString());
    }

    @Test
    void getFact() {
        // Arrange
        DatalogPredicate predicate = new DatalogPredicate("test", List.of("1,2,3", "4,5,6"));
        DatalogFact fact = new DatalogFact(predicate, "1, 2,3");

        // Act
        var result = predicate.getFact(fact);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("test(1, 2, 3)", result.get().toString());
    }

    @Test
    void testToString() {
        // Arrange
        DatalogPredicate predicate = new DatalogPredicate("test", List.of("1,2,3", "4,5,6"));

        // Act
        String result = predicate.toString();

        // Assert
        assertEquals("test(X, Y, Z)", result);
    }

    @Test
    void equals_same() {
        // Arrange
        DatalogPredicate predicate = new DatalogPredicate("test", List.of("1,2,3", "4,5,6"));

        // Act
        //noinspection EqualsWithItself
        boolean result = predicate.equals(predicate);

        // Assert
        assertTrue(result);
    }

    @Test
    void equals_null() {
        // Arrange
        DatalogPredicate predicate = new DatalogPredicate("test", List.of("1,2,3", "4,5,6"));

        // Act
        //noinspection ConstantValue
        boolean result = predicate.equals(null);

        // Assert
        assertFalse(result);
    }

    @Test
    void equals_differentClass() {
        // Arrange
        DatalogPredicate predicate = new DatalogPredicate("test", List.of("1,2,3", "4,5,6"));

        // Act
        //noinspection EqualsBetweenInconvertibleTypes
        boolean result = predicate.equals("test");

        // Assert
        assertFalse(result);
    }

    @Test
    void equals_differentName() {
        // Arrange
        DatalogPredicate predicate1 = new DatalogPredicate("test1", List.of("1,2,3", "4,5,6"));
        DatalogPredicate predicate2 = new DatalogPredicate("test2", List.of("1,2,3", "4,5,6"));

        // Act
        boolean result = predicate1.equals(predicate2);

        // Assert
        assertFalse(result);
    }

    @Test
    void equals_differentArity() {
        // Arrange
        DatalogPredicate predicate1 = new DatalogPredicate("test", List.of("1,2,3", "4,5,6"));
        DatalogPredicate predicate2 = new DatalogPredicate("test", List.of("1,2,3,4", "4,5,6,7"));

        // Act
        boolean result = predicate1.equals(predicate2);

        // Assert
        assertFalse(result);
    }

    @Test
    void equals_differentFacts() {
        // Arrange
        DatalogPredicate predicate1 = new DatalogPredicate("test", List.of("1,2,3", "4,5,6"));
        DatalogPredicate predicate2 = new DatalogPredicate("test", List.of("1,2,3", "4,5,7"));

        // Act
        boolean result = predicate1.equals(predicate2);

        // Assert
        assertFalse(result);
    }

    @Test
    void equals_equal() {
        // Arrange
        DatalogPredicate predicate1 = new DatalogPredicate("test", List.of("1,2,3", "4,5,6"));
        DatalogPredicate predicate2 = new DatalogPredicate("test", List.of("1,2,3", "4,5,6"));

        // Act
        boolean result = predicate1.equals(predicate2);

        // Assert
        assertTrue(result);
    }

    @Test
    void testHashCode() {
        // Arrange
        DatalogPredicate predicate1 = new DatalogPredicate("test", List.of("1,2,3", "4,5,6"));
        DatalogPredicate predicate2 = new DatalogPredicate("test", List.of("1,2,3", "4,5,6"));

        // Act
        int result1 = predicate1.hashCode();
        int result2 = predicate2.hashCode();

        // Assert
        assertEquals(result1, result2);
    }
}
