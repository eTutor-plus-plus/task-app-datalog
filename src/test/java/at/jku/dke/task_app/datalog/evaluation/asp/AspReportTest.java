package at.jku.dke.task_app.datalog.evaluation.asp;

import at.jku.dke.etutor.task_app.dto.SubmissionMode;
import at.jku.dke.task_app.datalog.data.entities.DatalogTask;
import at.jku.dke.task_app.datalog.evaluation.DatalogFact;
import at.jku.dke.task_app.datalog.evaluation.DatalogPredicate;
import at.jku.dke.task_app.datalog.evaluation.asp.analysis.AspAnalysis;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AspReportTest {

    @Test
    void constructor_invalidFeedbackLevel() {
        // Arrange
        MessageSource ms = mock(MessageSource.class);
        Locale locale = Locale.GERMAN;
        SubmissionMode mode = SubmissionMode.RUN;
        int feedbackLevel = -1;
        AspAnalysis analysis = mock(AspAnalysis.class);
        var task = new DatalogTask();
        task.setMaxPoints(BigDecimal.TEN);
        String rawOutput = "rawOutput";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new AspReport(ms, locale, mode, feedbackLevel, analysis, rawOutput));
    }

    @Test
    void getGeneralFeedback_run() {
        // Arrange
        MessageSource ms = mock(MessageSource.class);
        var task = new DatalogTask();
        task.setMaxPoints(BigDecimal.TEN);
        AspReport report = new AspReport(ms, Locale.GERMAN, SubmissionMode.RUN, 3, mock(AspAnalysis.class), "rawOutput");
        when(ms.getMessage("noSyntaxError", null, Locale.GERMAN)).thenReturn("noSyntaxError");

        // Act
        String result = report.getGeneralFeedback();

        // Assert
        assertEquals("noSyntaxError", result);
    }

    @Test
    void getGeneralFeedback_submit_correct() {
        // Arrange
        MessageSource ms = mock(MessageSource.class);
        AspAnalysis analysis = mock(AspAnalysis.class);
        when(analysis.isCorrect()).thenReturn(true);
        var task = new DatalogTask();
        task.setMaxPoints(BigDecimal.TEN);
        AspReport report = new AspReport(ms, Locale.GERMAN, SubmissionMode.SUBMIT, 3, analysis, "rawOutput");
        when(ms.getMessage("correct", null, Locale.GERMAN)).thenReturn("correct");

        // Act
        String result = report.getGeneralFeedback();

        // Assert
        assertEquals("correct", result);
    }

    @Test
    void getGeneralFeedback_diagnose_correct() {
        // Arrange
        MessageSource ms = mock(MessageSource.class);
        AspAnalysis analysis = mock(AspAnalysis.class);
        when(analysis.isCorrect()).thenReturn(true);
        var task = new DatalogTask();
        task.setMaxPoints(BigDecimal.TEN);
        AspReport report = new AspReport(ms, Locale.GERMAN, SubmissionMode.DIAGNOSE, 3, analysis, "rawOutput");
        when(ms.getMessage("possiblyCorrect", null, Locale.GERMAN)).thenReturn("possiblyCorrect");

        // Act
        String result = report.getGeneralFeedback();

        // Assert
        assertEquals("possiblyCorrect", result);
    }

    @Test
    void getGeneralFeedback_incorrect() {
        // Arrange
        MessageSource ms = mock(MessageSource.class);
        AspAnalysis analysis = mock(AspAnalysis.class);
        when(analysis.isCorrect()).thenReturn(false);
        var task = new DatalogTask();
        task.setMaxPoints(BigDecimal.TEN);
        AspReport report = new AspReport(ms, Locale.GERMAN, SubmissionMode.SUBMIT, 3, analysis, "rawOutput");
        when(ms.getMessage("incorrect", null, Locale.GERMAN)).thenReturn("incorrect");

        // Act
        String result = report.getGeneralFeedback();

        // Assert
        assertEquals("incorrect", result);
    }

    @Test
    void getCriteria_run() {
        // Arrange
        MessageSource ms = mock(MessageSource.class);
        AspAnalysis analysis = mock(AspAnalysis.class);
        when(analysis.isCorrect()).thenReturn(true);
        var task = new DatalogTask();
        task.setMaxPoints(BigDecimal.TEN);
        AspReport report = new AspReport(ms, Locale.ENGLISH, SubmissionMode.RUN, 3, mock(AspAnalysis.class), "rawOutput");
        when(ms.getMessage("criterium.syntax", null, Locale.ENGLISH)).thenReturn("Syntax");
        when(ms.getMessage("criterium.syntax.valid", null, Locale.ENGLISH)).thenReturn("Syntax is valid");
        when(ms.getMessage("criterium.result", null, Locale.ENGLISH)).thenReturn("Result");

        // Act
        var result = report.getCriteria();

        // Assert
        assertEquals(2, result.size());
        var c = result.getFirst();
        assertEquals("Syntax", c.name());
        assertEquals("Syntax is valid", c.feedback());
        assertNull(c.points());
        assertTrue(c.passed());

        c = result.get(1);
        assertEquals("Result", c.name());
        assertNull(c.points());
        assertTrue(c.passed());
        assertEquals("<pre>rawOutput</pre>", c.feedback());
    }

    @Test
    void getCriteria_submit() {
        // Arrange
        MessageSource ms = mock(MessageSource.class);
        var task = new DatalogTask();
        task.setMaxPoints(BigDecimal.TEN);
        AspReport report = new AspReport(ms, Locale.ENGLISH, SubmissionMode.SUBMIT, 3, mock(AspAnalysis.class), "rawOutput");
        when(ms.getMessage("criterium.syntax", null, Locale.ENGLISH)).thenReturn("Syntax");
        when(ms.getMessage("criterium.syntax.valid", null, Locale.ENGLISH)).thenReturn("Syntax is valid");

        // Act
        var result = report.getCriteria();

        // Assert
        assertEquals(2, result.size());
        var c = result.getFirst();
        assertEquals("Syntax", c.name());
        assertEquals("Syntax is valid", c.feedback());
        assertNull(c.points());
        assertTrue(c.passed());
    }

    //#region --- noFeedback ---
    @Test
    void getCriteria_diagnose_noFeedback() {
        // Arrange
        MessageSource ms = mock(MessageSource.class);
        var task = new DatalogTask();
        task.setMaxPoints(BigDecimal.TEN);
        AspReport report = new AspReport(ms, Locale.ENGLISH, SubmissionMode.DIAGNOSE, 0, mock(AspAnalysis.class), "rawOutput");
        when(ms.getMessage("criterium.syntax", null, Locale.ENGLISH)).thenReturn("Syntax");
        when(ms.getMessage("criterium.syntax.valid", null, Locale.ENGLISH)).thenReturn("Syntax is valid");
        when(ms.getMessage("criterium.result", null, Locale.ENGLISH)).thenReturn("Result");

        // Act
        var result = report.getCriteria();

        // Assert
        assertEquals(2, result.size());
        var c = result.getFirst();
        assertEquals("Syntax", c.name());
        assertEquals("Syntax is valid", c.feedback());
        assertNull(c.points());
        assertTrue(c.passed());

        c = result.get(1);
        assertEquals("Result", c.name());
        assertNull(c.points());
        assertFalse(c.passed());
        assertEquals("<pre>rawOutput</pre>", c.feedback());
    }
    //#endregion

    //#region --- littleFeedback ---
    @Test
    void getCriteria_diagnose_littleFeedback_missingModels() {
        // Arrange
        MessageSource ms = mock(MessageSource.class);
        AspAnalysis analysis = mock(AspAnalysis.class);
        var task = new DatalogTask();
        task.setMaxPoints(BigDecimal.TEN);
        AspReport report = new AspReport(ms, Locale.ENGLISH, SubmissionMode.DIAGNOSE, 1, analysis, "rawOutput");

        when(ms.getMessage("criterium.syntax", null, Locale.ENGLISH)).thenReturn("Syntax");
        when(ms.getMessage("criterium.syntax.valid", null, Locale.ENGLISH)).thenReturn("Syntax is valid");
        when(ms.getMessage("criterium.missingModels", null, Locale.ENGLISH)).thenReturn("Missing Models");
        when(ms.getMessage("criterium.missingModels.noCount", null, Locale.ENGLISH)).thenReturn("Models are missing");
        when(ms.getMessage("criterium.result", null, Locale.ENGLISH)).thenReturn("Result");
        when(ms.getMessage("criterium.count", null, Locale.ENGLISH)).thenReturn("Count");
        when(ms.getMessage("criterium.count.invalid", null, Locale.ENGLISH)).thenReturn("Count invalid");
        when(analysis.getMissingModels()).thenReturn(List.of(Set.of(new DatalogPredicate("test", List.of("1, 2")))));

        // Act
        var result = report.getCriteria();

        // Assert
        assertEquals(4, result.size());
        var c = result.getFirst();
        assertEquals("Syntax", c.name());
        assertEquals("Syntax is valid", c.feedback());
        assertNull(c.points());
        assertTrue(c.passed());

        c = result.get(1);
        assertEquals("Count", c.name());
        assertEquals("Count invalid", c.feedback());
        assertNull(c.points());
        assertFalse(c.passed());

        c = result.get(2);
        assertEquals("Missing Models", c.name());
        assertEquals("Models are missing", c.feedback());
        assertNull(c.points());
        assertFalse(c.passed());

        c = result.get(3);
        assertEquals("Result", c.name());
        assertNull(c.points());
        assertFalse(c.passed());
        assertEquals("<pre>rawOutput</pre>", c.feedback());
    }

    @Test
    void getCriteria_diagnose_littleFeedback_superfluousModels() {
        // Arrange
        MessageSource ms = mock(MessageSource.class);
        AspAnalysis analysis = mock(AspAnalysis.class);
        var task = new DatalogTask();
        task.setMaxPoints(BigDecimal.TEN);
        AspReport report = new AspReport(ms, Locale.ENGLISH, SubmissionMode.DIAGNOSE, 1, analysis, "rawOutput");

        when(ms.getMessage("criterium.syntax", null, Locale.ENGLISH)).thenReturn("Syntax");
        when(ms.getMessage("criterium.syntax.valid", null, Locale.ENGLISH)).thenReturn("Syntax is valid");
        when(ms.getMessage("criterium.superfluousModels", null, Locale.ENGLISH)).thenReturn("Error Title");
        when(ms.getMessage("criterium.superfluousModels.noCount", null, Locale.ENGLISH)).thenReturn("Error Details");
        when(ms.getMessage("criterium.result", null, Locale.ENGLISH)).thenReturn("Result");
        when(ms.getMessage("criterium.count", null, Locale.ENGLISH)).thenReturn("Count");
        when(ms.getMessage("criterium.count.invalid", null, Locale.ENGLISH)).thenReturn("Count invalid");
        when(analysis.getSuperfluousModels()).thenReturn(List.of(Set.of(new DatalogPredicate("test", List.of("1, 2")))));

        // Act
        var result = report.getCriteria();

        // Assert
        assertEquals(4, result.size());
        var c = result.getFirst();
        assertEquals("Syntax", c.name());
        assertEquals("Syntax is valid", c.feedback());
        assertNull(c.points());
        assertTrue(c.passed());

        c = result.get(1);
        assertEquals("Count", c.name());
        assertEquals("Count invalid", c.feedback());
        assertNull(c.points());
        assertFalse(c.passed());

        c = result.get(2);
        assertEquals("Error Title", c.name());
        assertEquals("Error Details", c.feedback());
        assertNull(c.points());
        assertFalse(c.passed());

        c = result.get(3);
        assertEquals("Result", c.name());
        assertNull(c.points());
        assertFalse(c.passed());
        assertEquals("<pre>rawOutput</pre>", c.feedback());
    }

    @Test
    void getCriteria_diagnose_littleFeedback_count() {
        // Arrange
        MessageSource ms = mock(MessageSource.class);
        AspAnalysis analysis = mock(AspAnalysis.class);
        var task = new DatalogTask();
        task.setMaxPoints(BigDecimal.TEN);
        AspReport report = new AspReport(ms, Locale.ENGLISH, SubmissionMode.DIAGNOSE, 1, analysis, "rawOutput");

        when(ms.getMessage("criterium.syntax", null, Locale.ENGLISH)).thenReturn("Syntax");
        when(ms.getMessage("criterium.syntax.valid", null, Locale.ENGLISH)).thenReturn("Syntax is valid");
        when(ms.getMessage("criterium.result", null, Locale.ENGLISH)).thenReturn("Result");
        when(ms.getMessage("criterium.count", null, Locale.ENGLISH)).thenReturn("Count");
        when(ms.getMessage("criterium.count.invalid", null, Locale.ENGLISH)).thenReturn("Count invalid");
        when(analysis.hasSameAmountOfModels()).thenReturn(false);

        // Act
        var result = report.getCriteria();

        // Assert
        assertEquals(3, result.size());
        var c = result.getFirst();
        assertEquals("Syntax", c.name());
        assertEquals("Syntax is valid", c.feedback());
        assertNull(c.points());
        assertTrue(c.passed());

        c = result.get(1);
        assertEquals("Count", c.name());
        assertEquals("Count invalid", c.feedback());
        assertNull(c.points());
        assertFalse(c.passed());

        c = result.get(2);
        assertEquals("Result", c.name());
        assertNull(c.points());
        assertFalse(c.passed());
        assertEquals("<pre>rawOutput</pre>", c.feedback());
    }
    //#endregion

    //#region --- someFeedback ---
    @Test
    void getCriteria_diagnose_someFeedback_missingModels() {
        // Arrange
        MessageSource ms = mock(MessageSource.class);
        AspAnalysis analysis = mock(AspAnalysis.class);
        var task = new DatalogTask();
        task.setMaxPoints(BigDecimal.TEN);
        AspReport report = new AspReport(ms, Locale.ENGLISH, SubmissionMode.DIAGNOSE, 2, analysis, "rawOutput");

        when(ms.getMessage("criterium.syntax", null, Locale.ENGLISH)).thenReturn("Syntax");
        when(ms.getMessage("criterium.syntax.valid", null, Locale.ENGLISH)).thenReturn("Syntax is valid");
        when(ms.getMessage("criterium.missingModels", null, Locale.ENGLISH)).thenReturn("Missing Models");
        when(ms.getMessage("criterium.missingModels.count", new Object[]{1}, Locale.ENGLISH)).thenReturn("Models are missing");
        when(ms.getMessage("criterium.result", null, Locale.ENGLISH)).thenReturn("Result");
        when(ms.getMessage("criterium.count", null, Locale.ENGLISH)).thenReturn("Count");
        when(ms.getMessage("criterium.count.invalid", null, Locale.ENGLISH)).thenReturn("Count invalid");
        when(analysis.getMissingModels()).thenReturn(List.of(Set.of(new DatalogPredicate("test", List.of("1, 2")))));

        // Act
        var result = report.getCriteria();

        // Assert
        assertEquals(4, result.size());
        var c = result.getFirst();
        assertEquals("Syntax", c.name());
        assertEquals("Syntax is valid", c.feedback());
        assertNull(c.points());
        assertTrue(c.passed());

        c = result.get(1);
        assertEquals("Count", c.name());
        assertEquals("Count invalid", c.feedback());
        assertNull(c.points());
        assertFalse(c.passed());

        c = result.get(2);
        assertEquals("Missing Models", c.name());
        assertEquals("Models are missing", c.feedback());
        assertNull(c.points());
        assertFalse(c.passed());

        c = result.get(3);
        assertEquals("Result", c.name());
        assertNull(c.points());
        assertFalse(c.passed());
        assertEquals("<pre>rawOutput</pre>", c.feedback());
    }

    @Test
    void getCriteria_diagnose_someFeedback_superfluousModels() {
        // Arrange
        MessageSource ms = mock(MessageSource.class);
        AspAnalysis analysis = mock(AspAnalysis.class);
        var task = new DatalogTask();
        task.setMaxPoints(BigDecimal.TEN);
        AspReport report = new AspReport(ms, Locale.ENGLISH, SubmissionMode.DIAGNOSE, 2, analysis, "rawOutput");

        when(ms.getMessage("criterium.syntax", null, Locale.ENGLISH)).thenReturn("Syntax");
        when(ms.getMessage("criterium.syntax.valid", null, Locale.ENGLISH)).thenReturn("Syntax is valid");
        when(ms.getMessage("criterium.superfluousModels", null, Locale.ENGLISH)).thenReturn("Error Title");
        when(ms.getMessage("criterium.superfluousModels.count", new Object[]{1}, Locale.ENGLISH)).thenReturn("Error Details");
        when(ms.getMessage("criterium.result", null, Locale.ENGLISH)).thenReturn("Result");
        when(ms.getMessage("criterium.count", null, Locale.ENGLISH)).thenReturn("Count");
        when(ms.getMessage("criterium.count.invalid", null, Locale.ENGLISH)).thenReturn("Count invalid");
        when(analysis.getSuperfluousModels()).thenReturn(List.of(Set.of(new DatalogPredicate("test", List.of("1, 2")))));

        // Act
        var result = report.getCriteria();

        // Assert
        assertEquals(4, result.size());
        var c = result.getFirst();
        assertEquals("Syntax", c.name());
        assertEquals("Syntax is valid", c.feedback());
        assertNull(c.points());
        assertTrue(c.passed());

        c = result.get(1);
        assertEquals("Count", c.name());
        assertEquals("Count invalid", c.feedback());
        assertNull(c.points());
        assertFalse(c.passed());

        c = result.get(2);
        assertEquals("Error Title", c.name());
        assertEquals("Error Details", c.feedback());
        assertNull(c.points());
        assertFalse(c.passed());

        c = result.get(3);
        assertEquals("Result", c.name());
        assertNull(c.points());
        assertFalse(c.passed());
        assertEquals("<pre>rawOutput</pre>", c.feedback());
    }

    @Test
    void getCriteria_diagnose_someFeedback_count() {
        // Arrange
        MessageSource ms = mock(MessageSource.class);
        AspAnalysis analysis = mock(AspAnalysis.class);
        var task = new DatalogTask();
        task.setMaxPoints(BigDecimal.TEN);
        AspReport report = new AspReport(ms, Locale.ENGLISH, SubmissionMode.DIAGNOSE, 2, analysis, "rawOutput");

        when(ms.getMessage("criterium.syntax", null, Locale.ENGLISH)).thenReturn("Syntax");
        when(ms.getMessage("criterium.syntax.valid", null, Locale.ENGLISH)).thenReturn("Syntax is valid");
        when(ms.getMessage("criterium.result", null, Locale.ENGLISH)).thenReturn("Result");
        when(ms.getMessage("criterium.count", null, Locale.ENGLISH)).thenReturn("Count");
        when(ms.getMessage("criterium.count.invalid", null, Locale.ENGLISH)).thenReturn("Count invalid");
        when(analysis.hasSameAmountOfModels()).thenReturn(false);

        // Act
        var result = report.getCriteria();

        // Assert
        assertEquals(3, result.size());
        var c = result.getFirst();
        assertEquals("Syntax", c.name());
        assertEquals("Syntax is valid", c.feedback());
        assertNull(c.points());
        assertTrue(c.passed());

        c = result.get(1);
        assertEquals("Count", c.name());
        assertEquals("Count invalid", c.feedback());
        assertNull(c.points());
        assertFalse(c.passed());

        c = result.get(2);
        assertEquals("Result", c.name());
        assertNull(c.points());
        assertFalse(c.passed());
        assertEquals("<pre>rawOutput</pre>", c.feedback());
    }
    //#endregion

    //#region --- muchFeedback ---
    @Test
    void getCriteria_diagnose_muchFeedback_missingModels() {
        // Arrange
        MessageSource ms = mock(MessageSource.class);
        AspAnalysis analysis = mock(AspAnalysis.class);
        var task = new DatalogTask();
        task.setMaxPoints(BigDecimal.TEN);
        AspReport report = new AspReport(ms, Locale.ENGLISH, SubmissionMode.DIAGNOSE, 3, analysis, "rawOutput");

        when(ms.getMessage("criterium.syntax", null, Locale.ENGLISH)).thenReturn("Syntax");
        when(ms.getMessage("criterium.syntax.valid", null, Locale.ENGLISH)).thenReturn("Syntax is valid");
        when(ms.getMessage("criterium.missingModels", null, Locale.ENGLISH)).thenReturn("Missing Models");
        when(ms.getMessage("criterium.missingModels.count", new Object[]{1}, Locale.ENGLISH)).thenReturn("Models are missing");
        when(ms.getMessage("criterium.result", null, Locale.ENGLISH)).thenReturn("Result");
        when(ms.getMessage("criterium.count", null, Locale.ENGLISH)).thenReturn("Count");
        when(ms.getMessage("criterium.count.invalid", null, Locale.ENGLISH)).thenReturn("Count invalid");
        when(analysis.getMissingModels()).thenReturn(List.of(Set.of(new DatalogPredicate("test", List.of("1, 2")))));

        // Act
        var result = report.getCriteria();

        // Assert
        assertEquals(4, result.size());
        var c = result.getFirst();
        assertEquals("Syntax", c.name());
        assertEquals("Syntax is valid", c.feedback());
        assertNull(c.points());
        assertTrue(c.passed());

        c = result.get(1);
        assertEquals("Count", c.name());
        assertEquals("Count invalid", c.feedback());
        assertNull(c.points());
        assertFalse(c.passed());

        c = result.get(2);
        assertEquals("Missing Models", c.name());
        assertEquals("<pre><pre>{test(X, Y)}\n</pre></pre>", c.feedback());
        assertNull(c.points());
        assertFalse(c.passed());

        c = result.get(3);
        assertEquals("Result", c.name());
        assertNull(c.points());
        assertFalse(c.passed());
        assertEquals("<pre>rawOutput</pre>", c.feedback());
    }

    @Test
    void getCriteria_diagnose_muchFeedback_superfluousModels() {
        // Arrange
        MessageSource ms = mock(MessageSource.class);
        AspAnalysis analysis = mock(AspAnalysis.class);
        var task = new DatalogTask();
        task.setMaxPoints(BigDecimal.TEN);
        AspReport report = new AspReport(ms, Locale.ENGLISH, SubmissionMode.DIAGNOSE, 3, analysis, "rawOutput");

        when(ms.getMessage("criterium.syntax", null, Locale.ENGLISH)).thenReturn("Syntax");
        when(ms.getMessage("criterium.syntax.valid", null, Locale.ENGLISH)).thenReturn("Syntax is valid");
        when(ms.getMessage("criterium.superfluousModels", null, Locale.ENGLISH)).thenReturn("Error Title");
        when(ms.getMessage("criterium.superfluousModels.count", new Object[]{1}, Locale.ENGLISH)).thenReturn("Error Details");
        when(ms.getMessage("criterium.result", null, Locale.ENGLISH)).thenReturn("Result");
        when(ms.getMessage("criterium.count", null, Locale.ENGLISH)).thenReturn("Count");
        when(ms.getMessage("criterium.count.invalid", null, Locale.ENGLISH)).thenReturn("Count invalid");
        when(analysis.getSuperfluousModels()).thenReturn(List.of(Set.of(new DatalogPredicate("test", List.of("1, 2")))));

        // Act
        var result = report.getCriteria();

        // Assert
        assertEquals(4, result.size());
        var c = result.getFirst();
        assertEquals("Syntax", c.name());
        assertEquals("Syntax is valid", c.feedback());
        assertNull(c.points());
        assertTrue(c.passed());

        c = result.get(1);
        assertEquals("Count", c.name());
        assertEquals("Count invalid", c.feedback());
        assertNull(c.points());
        assertFalse(c.passed());

        c = result.get(2);
        assertEquals("Error Title", c.name());
        assertEquals("<pre><pre>{test(X, Y)}\n</pre></pre>", c.feedback());
        assertNull(c.points());
        assertFalse(c.passed());

        c = result.get(3);
        assertEquals("Result", c.name());
        assertNull(c.points());
        assertFalse(c.passed());
        assertEquals("<pre>rawOutput</pre>", c.feedback());
    }

    @Test
    void getCriteria_diagnose_muchFeedback_count() {
        // Arrange
        MessageSource ms = mock(MessageSource.class);
        AspAnalysis analysis = mock(AspAnalysis.class);
        var task = new DatalogTask();
        task.setMaxPoints(BigDecimal.TEN);
        AspReport report = new AspReport(ms, Locale.ENGLISH, SubmissionMode.DIAGNOSE, 3, analysis, "rawOutput");

        when(ms.getMessage("criterium.syntax", null, Locale.ENGLISH)).thenReturn("Syntax");
        when(ms.getMessage("criterium.syntax.valid", null, Locale.ENGLISH)).thenReturn("Syntax is valid");
        when(ms.getMessage("criterium.result", null, Locale.ENGLISH)).thenReturn("Result");
        when(ms.getMessage("criterium.count", null, Locale.ENGLISH)).thenReturn("Count");
        when(ms.getMessage("criterium.count.invalid", null, Locale.ENGLISH)).thenReturn("Count invalid");
        when(analysis.hasSameAmountOfModels()).thenReturn(false);

        // Act
        var result = report.getCriteria();

        // Assert
        assertEquals(3, result.size());
        var c = result.getFirst();
        assertEquals("Syntax", c.name());
        assertEquals("Syntax is valid", c.feedback());
        assertNull(c.points());
        assertTrue(c.passed());

        c = result.get(1);
        assertEquals("Count", c.name());
        assertEquals("Count invalid", c.feedback());
        assertNull(c.points());
        assertFalse(c.passed());

        c = result.get(2);
        assertEquals("Result", c.name());
        assertNull(c.points());
        assertFalse(c.passed());
        assertEquals("<pre>rawOutput</pre>", c.feedback());
    }
    //#endregion
}
