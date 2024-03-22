package at.jku.dke.task_app.datalog.data.entities;

/**
 * Defines the grading strategy.
 */
public enum GradingStrategy {
    /**
     * Each error has to be considered when grading.
     */
    EACH,

    /**
     * An error has to be considered only once for the whole error category when grading.
     */
    GROUP,

    /**
     * If an error of a certain category was detected, this is a K.O. criterion for the whole query.
     * The consequence are zero achieved points.
     */
    KO
}
