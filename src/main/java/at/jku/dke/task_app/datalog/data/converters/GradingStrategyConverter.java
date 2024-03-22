package at.jku.dke.task_app.datalog.data.converters;

import at.jku.dke.task_app.datalog.data.entities.GradingStrategy;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

/**
 * Concerts the database {@code grading_strategy} enum to the java {@link GradingStrategy} enum and to/from string.
 */
@Converter
public class GradingStrategyConverter implements AttributeConverter<GradingStrategy, String> {
    /**
     * Creates a new instance of class {@linkplain GradingStrategyConverter}.
     */
    public GradingStrategyConverter() {
    }

    @Override
    public String convertToDatabaseColumn(GradingStrategy taskStatus) {
        if (taskStatus == null)
            return null;
        return taskStatus.name().toLowerCase();
    }

    @Override
    public GradingStrategy convertToEntityAttribute(String value) {
        if (value == null)
            return null;

        return Stream.of(GradingStrategy.values())
            .filter(g -> g.name().toLowerCase().equals(value))
            .findAny().orElseThrow(IllegalArgumentException::new);
    }
}
