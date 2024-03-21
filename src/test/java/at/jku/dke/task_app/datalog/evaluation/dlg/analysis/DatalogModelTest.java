package at.jku.dke.task_app.datalog.evaluation.dlg.analysis;

import at.jku.dke.task_app.datalog.evaluation.DatalogPredicate;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DatalogModelTest {

    @Test
    void isConsistent_true() {
        // Arrange
        Map<String, List<String>> queryResult = Map.of(
            "p1", List.of("a", "b"),
            "p2", List.of("c,d", "e,f")
        );
        DatalogModel model = new DatalogModel(queryResult);

        // Act
        boolean result = model.isConsistent();

        // Assert
        assertTrue(result);
    }

    @Test
    void isConsistent_false() {
        // Arrange
        Map<String, List<String>> queryResult = Map.of(
            "p1", List.of("a", "b"),
            "p2", List.of("c,d", "e,f,g")
        );
        DatalogModel model = new DatalogModel(queryResult);

        // Act
        boolean result = model.isConsistent();

        // Assert
        assertFalse(result);
    }

    @Test
    void getPredicates() {
        // Arrange
        Map<String, List<String>> queryResult = Map.of(
            "p1", List.of("a", "b"),
            "p2", List.of("c,d", "e,f")
        );
        DatalogModel model = new DatalogModel(queryResult);

        // Act
        List<DatalogPredicate> result = model.getPredicates();

        // Assert
        assertEquals(2, result.size());
        assertThat(result).extracting(DatalogPredicate::getName).containsExactlyInAnyOrder("p1", "p2");
    }

    @Test
    void getPredicatesNames() {
        // Arrange
        Map<String, List<String>> queryResult = Map.of(
            "p1", List.of("a", "b"),
            "p2", List.of("c,d", "e,f")
        );
        DatalogModel model = new DatalogModel(queryResult);

        // Act
        List<String> result = model.getPredicatesNames();

        // Assert
        assertEquals(2, result.size());
        assertThat(result).containsExactlyInAnyOrder("p1", "p2");
    }

    @Test
    void getPredicate_exists() {
        // Arrange
        Map<String, List<String>> queryResult = Map.of(
            "p1", List.of("a", "b"),
            "p2", List.of("c,d", "e,f")
        );
        DatalogModel model = new DatalogModel(queryResult);

        // Act
        var result = model.getPredicate("p2");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("p2", result.get().getName());
    }

    @Test
    void getPredicate_notExists() {
        // Arrange
        Map<String, List<String>> queryResult = Map.of(
            "p1", List.of("a", "b"),
            "p2", List.of("c,d", "e,f")
        );
        DatalogModel model = new DatalogModel(queryResult);

        // Act
        var result = model.getPredicate("p3");

        // Assert
        assertTrue(result.isEmpty());
    }
}
