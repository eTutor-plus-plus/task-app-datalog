package at.jku.dke.task_app.datalog.config;

import org.junit.jupiter.api.Test;
import uk.org.webcompere.systemstubs.properties.SystemProperties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static uk.org.webcompere.systemstubs.SystemStubs.withEnvironmentVariable;

class DatalogSettingsTest {

    @Test
    void emptyConstructor() {
        // Act
        var settings = new DatalogSettings();

        // Assert
        assertEquals("", settings.exe());
        assertEquals(10, settings.maxExecutionTime());
        assertEquals("0", settings.factEncodingSuffix());
    }

    @Test
    void getExecutable_set() {
        // Arrange
        var settings = new DatalogSettings("/path/to/dlv", 10, "0");

        // Act
        var executable = settings.getExecutable();

        // Assert
        assertEquals("/path/to/dlv", executable);
    }

    @Test
    void getExecutable_win() throws Exception {
        new SystemProperties("os.name", "Windows 10").execute(() -> {
            // Arrange
            var settings = new DatalogSettings();

            // Act
            var executable = settings.getExecutable();

            // Assert
            assertThat(executable).endsWith("bin/dlv-win.exe");
        });
    }

    @Test
    void getExecutable_mac() throws Exception {
        new SystemProperties("os.name", "Mac OS X").execute(() -> {
            // Arrange
            var settings = new DatalogSettings();

            // Act
            var executable = settings.getExecutable();

            // Assert
            assertThat(executable).endsWith("bin/dlv-mac-m1");
        });
    }

    @Test
    void getExecutable_linux() throws Exception {
        new SystemProperties("os.name", "Linux").execute(() -> {
            // Arrange
            var settings = new DatalogSettings();

            // Act
            var executable = settings.getExecutable();

            // Assert
            assertThat(executable).endsWith("bin/dlv-linux.bin");
        });
    }

    @Test
    void getExecutable_unknown() throws Exception {
        new SystemProperties("os.name", "Unknown OS").execute(() -> {
            // Arrange
            var settings = new DatalogSettings();

            // Act & Assert
            assertThrows(IllegalStateException.class, settings::getExecutable);
        });
    }

}
