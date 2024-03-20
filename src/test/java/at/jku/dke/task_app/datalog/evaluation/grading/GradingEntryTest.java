package at.jku.dke.task_app.datalog.evaluation.grading;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GradingEntryTest {

    @Test
    void constructor_nullPoints(){
        // Arrange
        String errorCategory = "error";
        BigDecimal minusPoints = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new GradingEntry(errorCategory, minusPoints));
    }

    @Test
    void constructor_negativePoints(){
        // Arrange
        String errorCategory = "error";
        BigDecimal minusPoints = BigDecimal.TEN.negate();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new GradingEntry(errorCategory, minusPoints));
    }

    @Test
    void constructor_nullCategory(){
        // Arrange
        String errorCategory = null;
        BigDecimal minusPoints = BigDecimal.TEN;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new GradingEntry(errorCategory, minusPoints));
    }

    @Test
    void constructor_emptyCategory(){
        // Arrange
        String errorCategory = "  ";
        BigDecimal minusPoints = BigDecimal.TEN;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new GradingEntry(errorCategory, minusPoints));
    }

    @Test
    void errorCategory() {
        // Arrange
        String errorCategory = "error";
        BigDecimal minusPoints = BigDecimal.TEN;

        // Act
        GradingEntry entry = new GradingEntry(errorCategory, minusPoints);

        // Assert
        assertEquals(errorCategory, entry.errorCategory());
    }

    @Test
    void minusPoints() {
        // Arrange
        String errorCategory = "error";
        BigDecimal minusPoints = BigDecimal.TEN;

        // Act
        GradingEntry entry = new GradingEntry(errorCategory, minusPoints);

        // Assert
        assertEquals(minusPoints, entry.minusPoints());
    }

}
