package at.jku.dke.task_app.datalog.evaluation;

import at.jku.dke.task_app.datalog.config.DatalogSettings;
import at.jku.dke.task_app.datalog.data.entities.TermDescription;
import at.jku.dke.task_app.datalog.evaluation.exceptions.ExecutionException;
import at.jku.dke.task_app.datalog.evaluation.exceptions.SyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Executor that executes the datalog binary.
 */
@Service
public class DatalogExecutorImpl implements DatalogExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(DatalogExecutorImpl.class);

    private final DatalogSettings datalogSettings;
    private final Path workingDirectory;

    /**
     * Creates a new instance of class {@linkplain DatalogExecutorImpl}.
     *
     * @param datalogSettings The datalog settings.
     */
    public DatalogExecutorImpl(DatalogSettings datalogSettings) {
        this.datalogSettings = datalogSettings;
        Path dir;
        try {
            dir = Files.createTempDirectory("datalog");
        } catch (IOException ex) {
            LOG.warn("Could not create temp directory, using current directory as working directory", ex);
            dir = Path.of(".");
        }
        this.workingDirectory = dir;
    }

    /**
     * Executes the datalog binary with the given input.
     *
     * @param input The input for the datalog binary.
     * @param args  Additional arguments for the datalog binary (e.g. -cautious).
     * @return The output of the datalog binary.
     * @throws IOException        If an I/O error occurs.
     * @throws ExecutionException If the process execution fails.
     */
    @Override
    public ExecutionOutput execute(String input, String[] args) throws IOException, ExecutionException {
        // Write file contents
        var id = UUID.randomUUID().toString();
        File file = File.createTempFile(id, ".dlv", this.workingDirectory.toFile());
        LOG.debug("Writing input {} to temporary file {}", input, file);
        Files.writeString(file.toPath(), input);

        // Build process
        List<String> cmd = new ArrayList<>();
        cmd.add(this.datalogSettings.getExecutable());
        cmd.add("-silent");
        cmd.addAll(Arrays.stream(args).toList());
        cmd.add(file.getAbsolutePath());
        var successFile = File.createTempFile(id, ".success", this.workingDirectory.toFile());
        var errorFile = File.createTempFile(id, ".error", this.workingDirectory.toFile());
        var pb = new ProcessBuilder(cmd)
            .directory(this.workingDirectory.toFile())
            .redirectOutput(successFile)
            .redirectError(errorFile);
        LOG.info("Executing process {}", cmd);

        // Execute process
        Process process = pb.start();
        LOG.debug("Process started {}", process.pid());
        try {
            boolean exited = process.waitFor(this.datalogSettings.maxExecutionTime(), java.util.concurrent.TimeUnit.SECONDS);
            if (!exited) {
                LOG.warn("Process did not exit in time, killing process");
                process.destroy();
                throw new ExecutionException("Process did not exit in time");
            }
        } catch (InterruptedException ex) {
            LOG.warn("Process interrupted", ex);
            throw new ExecutionException("Process interrupted", ex);
        }

        // Read output
        String output;
        if (process.exitValue() == 0) {
            output = Files.readString(successFile.toPath());
        } else {
            output = Files.readString(errorFile.toPath());
            output = output.replace(file.getAbsolutePath(), "submission.dlv");
        }

        // Clean up
        file.delete();
        successFile.delete();
        errorFile.delete();

        // Return
        return new ExecutionOutput(output, process.exitValue());
    }

    /**
     * Executes the datalog binary with the given input (with -nofacts flag).
     *
     * @param facts The datalog facts from the task group.
     * @param rules The datalog rules from the submission.
     * @param maxN  Limit integers to [0,<maxN>] (-N option). (can be {@code null})
     * @return The output of the datalog execution.
     * @throws IOException        If an I/O error occurs.
     * @throws ExecutionException If the process execution fails.
     */
    @Override
    public String execute(String facts, String rules, Integer maxN) throws IOException, ExecutionException {
        String input = facts + System.lineSeparator() + rules;
        String[] args = maxN != null ? new String[]{"-nofacts", "-N=" + maxN} : new String[]{"-nofacts"};
        var rawResult = this.execute(input, args);
        if (rawResult.exitCode() != 0) {
            if (rawResult.output().contains(".dlv")) {
                LOG.debug("Datalog execution failed with syntax error: {}", rawResult.output());
                throw new SyntaxException(rawResult.output());
            }

            LOG.warn("Datalog execution failed with error output {}", rawResult.output());
            throw new ExecutionException("Datalog execution failed with error output " + rawResult.output());
        }
        return rawResult.output();
    }

    /**
     * Executes the datalog binary with the given input.
     *
     * @param facts   The datalog facts from the task group.
     * @param rules   The datalog rules from the submission.
     * @param queries The datalog queries from the task.
     * @return The result of the datalog execution.
     * @throws IOException        If an I/O error occurs.
     * @throws ExecutionException If the process execution fails.
     * @throws SyntaxException    If the datalog execution fails with a syntax error.
     */
    @Override
    public ExecutionResult query(String facts, String rules, List<String> queries) throws IOException, ExecutionException {
        return query(facts, rules, queries, Collections.emptyList(), true);
    }

    /**
     * Executes the datalog binary with the given input.
     *
     * @param facts          The datalog facts from the task group.
     * @param rules          The datalog rules from the submission.
     * @param queries        The datalog queries from the task.
     * @param uncheckedTerms The unchecked terms from the task.
     * @param encodeFacts    Whether the facts (except in unchecked terms) should be encoded.
     * @return The result of the datalog execution.
     * @throws IOException        If an I/O error occurs.
     * @throws ExecutionException If the process execution fails.
     * @throws SyntaxException    If the datalog execution fails with a syntax error.
     */
    @Override
    public ExecutionResult query(String facts, String rules, List<String> queries, List<TermDescription> uncheckedTerms, boolean encodeFacts) throws IOException, ExecutionException {
        if (encodeFacts)
            facts = this.encodeFacts(facts, uncheckedTerms);

        // Execute raw for return to caller
        String input = facts + System.lineSeparator() +
                       rules + System.lineSeparator();
        var rawResult = this.execute(input, new String[]{"-nofacts"});
        if (rawResult.exitCode() != 0) {
            if (rawResult.output().contains(".dlv")) {
                LOG.debug("Datalog execution failed with syntax error: {}", rawResult.output());
                throw new SyntaxException(rawResult.output());
            }

            LOG.warn("Datalog execution failed with error output {}", rawResult.output());
            throw new ExecutionException("Datalog execution failed with error output " + rawResult.output());
        }

        Map<String, List<String>> result = new HashMap<>();
        for (String query : queries) {
            String predicate = getPredicateFromQuery(query);

            LOG.debug("Executing datalog with facts: {}, rules: {}, query: {}", facts, rules, query);

            // Execute
            String queryInput = input + query;
            var executionResult = this.execute(queryInput, new String[]{"-cautious"});
            if (executionResult.exitCode() != 0) {
                LOG.warn("Datalog query execution failed with error output {}", executionResult.output());
                throw new ExecutionException("Datalog query execution failed with error output " + executionResult.output());
            }

            // Parse output
            List<String> output = Arrays.stream(executionResult.output().split(System.lineSeparator())).filter(x -> !x.isBlank()).toList();
            result.put(predicate, output);
        }
        return new ExecutionResult(rawResult.output(), result);
    }

    /**
     * Encodes the facts by adding a suffix {@link DatalogSettings#factEncodingSuffix()} to the terms, except for the unchecked terms.
     *
     * @param facts          The facts.
     * @param uncheckedTerms The unchecked terms.
     * @return The encoded facts.
     */
    private String encodeFacts(String facts, List<TermDescription> uncheckedTerms) {
        var factList = Arrays.stream(facts.split("\\.")).toList();
        var encodedFacts = new StringBuilder();

        for (String fact : factList) {
            if (fact.isBlank())
                continue;

            var index = fact.indexOf("(");
            var predicate = fact
                .substring(0, index >= 0 ? index : fact.length())
                .replace("\n", "")
                .replace("\r", "")
                .replace(" ", "");

            var newFact = new StringBuilder(predicate);
            if (index >= 0) {
                newFact.append("(");

                var terms = fact.substring(index + 1, fact.length() - 1).split(",");
                for (int i = 0; i < terms.length; i++) {
                    if (i > 0)
                        newFact.append(',');
                    newFact.append(terms[i].strip());

                    int termPosition = i + 1;
                    final int finalIForStream = i;
                    if (uncheckedTerms.stream()
                        .filter(t -> t.predicate().equals(predicate))
                        .filter(t -> t.position() == termPosition)
                        .noneMatch(t -> t.term().replace(" ", "").equals(terms[finalIForStream].replace(" ", "")))) {
                        newFact.append(this.datalogSettings.factEncodingSuffix());
                    }
                }
                newFact.append(").").append(System.lineSeparator());
            }

            encodedFacts.append(newFact);
        }

        return encodedFacts.toString();
    }

    /**
     * Takes a query in the form of "predicate(terms...)?" and returns the predicate (e.g. predicate).
     *
     * @param query The query.
     * @return The predicate.
     */
    private String getPredicateFromQuery(String query) {
        if (query.contains("(")) {
            return query.substring(0, query.indexOf("("));
        }
        return query;
    }

}
