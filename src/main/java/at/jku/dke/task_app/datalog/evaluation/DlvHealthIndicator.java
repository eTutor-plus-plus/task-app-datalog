package at.jku.dke.task_app.datalog.evaluation;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * This class is a health indicator providing the DLV version.
 */
@Component
public class DlvHealthIndicator implements HealthIndicator {

    private final DatalogExecutor executor;
    private DatalogExecutor.ExecutionOutput executorOutput;

    /**
     * Creates a new instance of class {@link DlvHealthIndicator}.
     *
     * @param executor The executor to use.
     */
    public DlvHealthIndicator(DatalogExecutor executor) {
        this.executor = executor;
    }

    /**
     * Return an indication of health.
     *
     * @return the health
     */
    @Override
    public Health health() {
        try {
            this.executorOutput = this.executorOutput == null ? this.executor.execute("-help") : this.executorOutput;
            if (this.executorOutput.exitCode() != 0) {
                return Health.down()
                    .withDetail("exitCode", this.executorOutput.exitCode())
                    .withDetail("output", this.executorOutput.output())
                    .build();
            }
            return Health.up()
                .withDetail("version", this.executorOutput.output().split("\n")[0])
                .build();
        } catch (Exception ex) {
            return Health.down()
                .withException(ex)
                .build();
        }
    }

}
