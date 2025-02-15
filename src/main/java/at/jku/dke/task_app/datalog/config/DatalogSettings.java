package at.jku.dke.task_app.datalog.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.nio.file.Path;

/**
 * Configuration properties for the Datalog executable.
 *
 * @param exe                The path to the Datalog executable (if the String is empty, the executable is determined automatically from the Operating System).
 * @param maxExecutionTime   The maximum execution time in seconds.
 * @param factEncodingSuffix The suffix for the fact encoding.
 * @param docUrl The public facts URL.
 */
@Validated
@ConfigurationProperties(prefix = "datalog")
public record DatalogSettings(@NotNull String exe, @Min(1) int maxExecutionTime, @NotNull String factEncodingSuffix, @NotNull String docUrl) {
    /**
     * Empty datalog settings for testing purposes.
     */
    public static final DatalogSettings EMPTY = new DatalogSettings("", 10, "0", "http://localhost:8081/dlg/");

    /**
     * Returns the path to the Datalog executable.
     * <p>
     * If {@link #exe()} is empty, the executable is determined automatically from the Operating System.
     * This feature is mainly used for testing/development purposes.
     *
     * @return The path to the Datalog executable.
     */
    public String getExecutable() {
        if (this.exe.isBlank()) {
            var workingDir = Path.of(System.getProperty("user.dir"));

            if (System.getProperty("os.name").toLowerCase().contains("windows"))
                return workingDir.resolve("bin/dlv-windows.exe").toString();
            if (System.getProperty("os.name").toLowerCase().contains("mac"))
                return workingDir.resolve("bin/dlv-mac-m1").toString();
            if (System.getProperty("os.name").toLowerCase().contains("linux"))
                return workingDir.resolve("bin/dlv-linux.bin").toString();

            throw new IllegalStateException("Unsupported operating system: " + System.getProperty("os.name"));
        }
        return exe;
    }
}
