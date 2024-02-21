package at.jku.dke.task_app.datalog.evaluation;

import at.jku.dke.etutor.task_app.dto.SubmissionMode;
import at.jku.dke.task_app.datalog.evaluation.analysis.DatalogAnalysis;
import at.jku.dke.task_app.datalog.evaluation.analysis.DatalogFact;
import at.jku.dke.task_app.datalog.evaluation.analysis.DatalogPredicate;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DatalogReportTest {

    @Test
    void constructor_invalidFeedbackLevel() {
        // Arrange
        MessageSource ms = mock(MessageSource.class);
        Locale locale = Locale.GERMAN;
        SubmissionMode mode = SubmissionMode.RUN;
        int feedbackLevel = -1;
        DatalogAnalysis analysis = mock(DatalogAnalysis.class);
        String rawOutput = "rawOutput";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new DatalogReport(ms, locale, mode, feedbackLevel, analysis, rawOutput));
    }

    @Test
    void getGeneralFeedback_run() {
        // Arrange
        MessageSource ms = mock(MessageSource.class);
        DatalogReport report = new DatalogReport(ms, Locale.GERMAN, SubmissionMode.RUN, 3, mock(DatalogAnalysis.class), "rawOutput");
        when(ms.getMessage("noSyntaxError", null, Locale.GERMAN)).thenReturn("noSyntaxError");

        // Act
        String result = report.getGeneralFeedback();

        // Assert
        assertEquals("noSyntaxError", result);
    }

    @Test
    void getGeneralFeedback_correct() {
        // Arrange
        MessageSource ms = mock(MessageSource.class);
        DatalogAnalysis analysis = mock(DatalogAnalysis.class);
        when(analysis.isCorrect()).thenReturn(true);
        DatalogReport report = new DatalogReport(ms, Locale.GERMAN, SubmissionMode.SUBMIT, 3, analysis, "rawOutput");
        when(ms.getMessage("correct", null, Locale.GERMAN)).thenReturn("correct");

        // Act
        String result = report.getGeneralFeedback();

        // Assert
        assertEquals("correct", result);
    }

    @Test
    void getGeneralFeedback_incorrect() {
        // Arrange
        MessageSource ms = mock(MessageSource.class);
        DatalogAnalysis analysis = mock(DatalogAnalysis.class);
        when(analysis.isCorrect()).thenReturn(false);
        DatalogReport report = new DatalogReport(ms, Locale.GERMAN, SubmissionMode.SUBMIT, 3, analysis, "rawOutput");
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
        DatalogReport report = new DatalogReport(ms, Locale.ENGLISH, SubmissionMode.RUN, 3, mock(DatalogAnalysis.class), "rawOutput");
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
        assertEquals("<div style=\"font-family: monospace;\">rawOutput</div>", c.feedback());
    }

    @Test
    void getCriteria_submit() {
        // Arrange
        MessageSource ms = mock(MessageSource.class);
        DatalogReport report = new DatalogReport(ms, Locale.ENGLISH, SubmissionMode.SUBMIT, 3, mock(DatalogAnalysis.class), "rawOutput");
        when(ms.getMessage("criterium.syntax", null, Locale.ENGLISH)).thenReturn("Syntax");
        when(ms.getMessage("criterium.syntax.valid", null, Locale.ENGLISH)).thenReturn("Syntax is valid");

        // Act
        var result = report.getCriteria();

        // Assert
        assertEquals(1, result.size());
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
        DatalogReport report = new DatalogReport(ms, Locale.ENGLISH, SubmissionMode.DIAGNOSE, 0, mock(DatalogAnalysis.class), "rawOutput");
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
        assertEquals("<div style=\"font-family: monospace;\">rawOutput</div>", c.feedback());
    }
    //#endregion

    //#region --- littleFeedback ---
    @Test
    void getCriteria_diagnose_littleFeedback_missingFacts() {
        // Arrange
        MessageSource ms = mock(MessageSource.class);
        DatalogAnalysis analysis = mock(DatalogAnalysis.class);
        DatalogReport report = new DatalogReport(ms, Locale.ENGLISH, SubmissionMode.DIAGNOSE, 1, analysis, "rawOutput");

        when(ms.getMessage("criterium.syntax", null, Locale.ENGLISH)).thenReturn("Syntax");
        when(ms.getMessage("criterium.syntax.valid", null, Locale.ENGLISH)).thenReturn("Syntax is valid");
        when(ms.getMessage("criterium.missingFacts", null, Locale.ENGLISH)).thenReturn("Missing Facts");
        when(ms.getMessage("criterium.missingFacts.noCount", null, Locale.ENGLISH)).thenReturn("Facts are missing");
        when(ms.getMessage("criterium.result", null, Locale.ENGLISH)).thenReturn("Result");
        when(analysis.getMissingFacts()).thenReturn(List.of(new DatalogFact(new DatalogPredicate("test", List.of("1, 2")), "1, 2")));

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
        assertEquals("Missing Facts", c.name());
        assertEquals("Facts are missing", c.feedback());
        assertNull(c.points());
        assertFalse(c.passed());

        c = result.get(2);
        assertEquals("Result", c.name());
        assertNull(c.points());
        assertFalse(c.passed());
        assertEquals("<div style=\"font-family: monospace;\">rawOutput</div>", c.feedback());
    }

    @Test
    void getCriteria_diagnose_littleFeedback_redundantFacts() {
        // Arrange
        MessageSource ms = mock(MessageSource.class);
        DatalogAnalysis analysis = mock(DatalogAnalysis.class);
        DatalogReport report = new DatalogReport(ms, Locale.ENGLISH, SubmissionMode.DIAGNOSE, 1, analysis, "rawOutput");

        when(ms.getMessage("criterium.syntax", null, Locale.ENGLISH)).thenReturn("Syntax");
        when(ms.getMessage("criterium.syntax.valid", null, Locale.ENGLISH)).thenReturn("Syntax is valid");
        when(ms.getMessage("criterium.redundantFacts", null, Locale.ENGLISH)).thenReturn("Error Title");
        when(ms.getMessage("criterium.redundantFacts.noCount", null, Locale.ENGLISH)).thenReturn("Error Details");
        when(ms.getMessage("criterium.result", null, Locale.ENGLISH)).thenReturn("Result");
        when(analysis.getRedundantFacts()).thenReturn(List.of(new DatalogFact(new DatalogPredicate("test", List.of("1, 2")), "1, 2")));

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
        assertEquals("Error Title", c.name());
        assertEquals("Error Details", c.feedback());
        assertNull(c.points());
        assertFalse(c.passed());

        c = result.get(2);
        assertEquals("Result", c.name());
        assertNull(c.points());
        assertFalse(c.passed());
        assertEquals("<div style=\"font-family: monospace;\">rawOutput</div>", c.feedback());
    }

    @Test
    void getCriteria_diagnose_littleFeedback_missingPredicates() {
        // Arrange
        MessageSource ms = mock(MessageSource.class);
        DatalogAnalysis analysis = mock(DatalogAnalysis.class);
        DatalogReport report = new DatalogReport(ms, Locale.ENGLISH, SubmissionMode.DIAGNOSE, 1, analysis, "rawOutput");

        when(ms.getMessage("criterium.syntax", null, Locale.ENGLISH)).thenReturn("Syntax");
        when(ms.getMessage("criterium.syntax.valid", null, Locale.ENGLISH)).thenReturn("Syntax is valid");
        when(ms.getMessage("criterium.missingPredicates", null, Locale.ENGLISH)).thenReturn("Error Title");
        when(ms.getMessage("criterium.missingPredicates.noCount", null, Locale.ENGLISH)).thenReturn("Error Details");
        when(ms.getMessage("criterium.result", null, Locale.ENGLISH)).thenReturn("Result");
        when(analysis.getMissingPredicates()).thenReturn(List.of(new DatalogPredicate("test", List.of("1, 2"))));

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
        assertEquals("Error Title", c.name());
        assertEquals("Error Details", c.feedback());
        assertNull(c.points());
        assertFalse(c.passed());

        c = result.get(2);
        assertEquals("Result", c.name());
        assertNull(c.points());
        assertFalse(c.passed());
        assertEquals("<div style=\"font-family: monospace;\">rawOutput</div>", c.feedback());
    }
    //#endregion

    //#region --- someFeedback ---
    @Test
    void getCriteria_diagnose_someFeedback_missingFacts() {
        // Arrange
        MessageSource ms = mock(MessageSource.class);
        DatalogAnalysis analysis = mock(DatalogAnalysis.class);
        DatalogReport report = new DatalogReport(ms, Locale.ENGLISH, SubmissionMode.DIAGNOSE, 2, analysis, "rawOutput");

        when(ms.getMessage("criterium.syntax", null, Locale.ENGLISH)).thenReturn("Syntax");
        when(ms.getMessage("criterium.syntax.valid", null, Locale.ENGLISH)).thenReturn("Syntax is valid");
        when(ms.getMessage("criterium.missingFacts", null, Locale.ENGLISH)).thenReturn("Missing Facts");
        when(ms.getMessage("criterium.missingFacts.count", new Object[]{1}, Locale.ENGLISH)).thenReturn("Facts are missing");
        when(ms.getMessage("criterium.result", null, Locale.ENGLISH)).thenReturn("Result");
        when(analysis.getMissingFacts()).thenReturn(List.of(new DatalogFact(new DatalogPredicate("test", List.of("1, 2")), "1, 2")));

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
        assertEquals("Missing Facts", c.name());
        assertEquals("Facts are missing", c.feedback());
        assertNull(c.points());
        assertFalse(c.passed());

        c = result.get(2);
        assertEquals("Result", c.name());
        assertNull(c.points());
        assertFalse(c.passed());
        assertEquals("<div style=\"font-family: monospace;\">rawOutput</div>", c.feedback());
    }

    @Test
    void getCriteria_diagnose_someFeedback_redundantFacts() {
        // Arrange
        MessageSource ms = mock(MessageSource.class);
        DatalogAnalysis analysis = mock(DatalogAnalysis.class);
        DatalogReport report = new DatalogReport(ms, Locale.ENGLISH, SubmissionMode.DIAGNOSE, 2, analysis, "rawOutput");

        when(ms.getMessage("criterium.syntax", null, Locale.ENGLISH)).thenReturn("Syntax");
        when(ms.getMessage("criterium.syntax.valid", null, Locale.ENGLISH)).thenReturn("Syntax is valid");
        when(ms.getMessage("criterium.redundantFacts", null, Locale.ENGLISH)).thenReturn("Error Title");
        when(ms.getMessage("criterium.redundantFacts.count", new Object[]{1}, Locale.ENGLISH)).thenReturn("Error Details");
        when(ms.getMessage("criterium.result", null, Locale.ENGLISH)).thenReturn("Result");
        when(analysis.getRedundantFacts()).thenReturn(List.of(new DatalogFact(new DatalogPredicate("test", List.of("1, 2")), "1, 2")));

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
        assertEquals("Error Title", c.name());
        assertEquals("Error Details", c.feedback());
        assertNull(c.points());
        assertFalse(c.passed());

        c = result.get(2);
        assertEquals("Result", c.name());
        assertNull(c.points());
        assertFalse(c.passed());
        assertEquals("<div style=\"font-family: monospace;\">rawOutput</div>", c.feedback());
    }

    @Test
    void getCriteria_diagnose_someFeedback_missingPredicates() {
        // Arrange
        MessageSource ms = mock(MessageSource.class);
        DatalogAnalysis analysis = mock(DatalogAnalysis.class);
        DatalogReport report = new DatalogReport(ms, Locale.ENGLISH, SubmissionMode.DIAGNOSE, 2, analysis, "rawOutput");

        when(ms.getMessage("criterium.syntax", null, Locale.ENGLISH)).thenReturn("Syntax");
        when(ms.getMessage("criterium.syntax.valid", null, Locale.ENGLISH)).thenReturn("Syntax is valid");
        when(ms.getMessage("criterium.missingPredicates", null, Locale.ENGLISH)).thenReturn("Error Title");
        when(ms.getMessage("criterium.missingPredicates.count", new Object[]{1}, Locale.ENGLISH)).thenReturn("Error Details");
        when(ms.getMessage("criterium.result", null, Locale.ENGLISH)).thenReturn("Result");
        when(analysis.getMissingPredicates()).thenReturn(List.of(new DatalogPredicate("test", List.of("1, 2"))));

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
        assertEquals("Error Title", c.name());
        assertEquals("Error Details", c.feedback());
        assertNull(c.points());
        assertFalse(c.passed());

        c = result.get(2);
        assertEquals("Result", c.name());
        assertNull(c.points());
        assertFalse(c.passed());
        assertEquals("<div style=\"font-family: monospace;\">rawOutput</div>", c.feedback());
    }
    //#endregion

    //#region --- muchFeedback ---
    @Test
    void getCriteria_diagnose_muchFeedback_missingFacts() {
        // Arrange
        MessageSource ms = mock(MessageSource.class);
        DatalogAnalysis analysis = mock(DatalogAnalysis.class);
        DatalogReport report = new DatalogReport(ms, Locale.ENGLISH, SubmissionMode.DIAGNOSE, 3, analysis, "rawOutput");

        when(ms.getMessage("criterium.syntax", null, Locale.ENGLISH)).thenReturn("Syntax");
        when(ms.getMessage("criterium.syntax.valid", null, Locale.ENGLISH)).thenReturn("Syntax is valid");
        when(ms.getMessage("criterium.missingFacts", null, Locale.ENGLISH)).thenReturn("Missing Facts");
        when(ms.getMessage("criterium.result", null, Locale.ENGLISH)).thenReturn("Result");
        when(analysis.getMissingFacts()).thenReturn(List.of(new DatalogFact(new DatalogPredicate("test", List.of("1, 2")), "1, 2")));

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
        assertEquals("Missing Facts", c.name());
        assertEquals("<pre>test(1, 2)\n</pre>", c.feedback());
        assertNull(c.points());
        assertFalse(c.passed());

        c = result.get(2);
        assertEquals("Result", c.name());
        assertNull(c.points());
        assertFalse(c.passed());
        assertEquals("<div style=\"font-family: monospace;\">rawOutput</div>", c.feedback());
    }

    @Test
    void getCriteria_diagnose_muchFeedback_redundantFacts() {
        // Arrange
        MessageSource ms = mock(MessageSource.class);
        DatalogAnalysis analysis = mock(DatalogAnalysis.class);
        DatalogReport report = new DatalogReport(ms, Locale.ENGLISH, SubmissionMode.DIAGNOSE, 3, analysis, "rawOutput");

        when(ms.getMessage("criterium.syntax", null, Locale.ENGLISH)).thenReturn("Syntax");
        when(ms.getMessage("criterium.syntax.valid", null, Locale.ENGLISH)).thenReturn("Syntax is valid");
        when(ms.getMessage("criterium.redundantFacts", null, Locale.ENGLISH)).thenReturn("Error Title");
        when(ms.getMessage("criterium.result", null, Locale.ENGLISH)).thenReturn("Result");
        when(analysis.getRedundantFacts()).thenReturn(List.of(new DatalogFact(new DatalogPredicate("test", List.of("1, 2")), "1, 2")));

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
        assertEquals("Error Title", c.name());
        assertEquals("<pre>test(1, 2)\n</pre>", c.feedback());
        assertNull(c.points());
        assertFalse(c.passed());

        c = result.get(2);
        assertEquals("Result", c.name());
        assertNull(c.points());
        assertFalse(c.passed());
        assertEquals("<div style=\"font-family: monospace;\">rawOutput</div>", c.feedback());
    }

    @Test
    void getCriteria_diagnose_muchFeedback_missingPredicates() {
        // Arrange
        MessageSource ms = mock(MessageSource.class);
        DatalogAnalysis analysis = mock(DatalogAnalysis.class);
        DatalogReport report = new DatalogReport(ms, Locale.ENGLISH, SubmissionMode.DIAGNOSE, 3, analysis, "rawOutput");

        when(ms.getMessage("criterium.syntax", null, Locale.ENGLISH)).thenReturn("Syntax");
        when(ms.getMessage("criterium.syntax.valid", null, Locale.ENGLISH)).thenReturn("Syntax is valid");
        when(ms.getMessage("criterium.missingPredicates", null, Locale.ENGLISH)).thenReturn("Error Title");
        when(ms.getMessage("criterium.result", null, Locale.ENGLISH)).thenReturn("Result");
        when(analysis.getMissingPredicates()).thenReturn(List.of(new DatalogPredicate("test", List.of("1, 2"))));

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
        assertEquals("Error Title", c.name());
        assertEquals("<pre>test(X, Y)\n</pre>", c.feedback());
        assertNull(c.points());
        assertFalse(c.passed());

        c = result.get(2);
        assertEquals("Result", c.name());
        assertNull(c.points());
        assertFalse(c.passed());
        assertEquals("<div style=\"font-family: monospace;\">rawOutput</div>", c.feedback());
    }
    //#endregion
}
