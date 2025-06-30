package com.benthos.sonar.bloblang;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.io.IOException;

/**
 * Sensor for analyzing Bloblang files and YAML files with Bloblang content
 */
public class BloblangSensor implements Sensor {

    private static final Logger LOG = Loggers.get(BloblangSensor.class);

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor
            .name("Bloblang Sensor")
            .onlyOnLanguage(BloblangLanguage.KEY);
    }

    @Override
    public void execute(SensorContext context) {
        FileSystem fileSystem = context.fileSystem();
        
        // Analyze Bloblang files
        analyzeBloblangFiles(context, fileSystem);
    }

    private void analyzeBloblangFiles(SensorContext context, FileSystem fileSystem) {
        Iterable<InputFile> bloblangFiles = fileSystem.inputFiles(
            fileSystem.predicates().and(
                fileSystem.predicates().hasType(InputFile.Type.MAIN),
                fileSystem.predicates().hasLanguage(BloblangLanguage.KEY)
            )
        );

        for (InputFile inputFile : bloblangFiles) {
            analyzeFile(context, inputFile);
        }
    }

    private void analyzeFile(SensorContext context, InputFile inputFile) {
        try {
            String content = inputFile.contents();
            
            // Count lines of code
            BloblangMetrics metrics = calculateMetrics(content);
            
            // Save metrics to SonarQube
            context.<Integer>newMeasure()
                .forMetric(CoreMetrics.LINES)
                .on(inputFile)
                .withValue(metrics.lines)
                .save();

            context.<Integer>newMeasure()
                .forMetric(CoreMetrics.NCLOC)
                .on(inputFile)
                .withValue(metrics.linesOfCode)
                .save();

            context.<Integer>newMeasure()
                .forMetric(CoreMetrics.COMMENT_LINES)
                .on(inputFile)
                .withValue(metrics.commentLines)
                .save();

            LOG.info("Analyzed Bloblang file: {} - Lines: {}, NCLOC: {}, Comments: {}", 
                inputFile.filename(), metrics.lines, metrics.linesOfCode, metrics.commentLines);

        } catch (IOException e) {
            LOG.warn("Cannot analyze file: " + inputFile.filename(), e);
        }
    }

    private BloblangMetrics calculateMetrics(String content) {
        String[] lines = content.split("\r?\n");
        
        int totalLines = lines.length;
        int linesOfCode = 0;
        int commentLines = 0;
        
        for (String line : lines) {
            String trimmedLine = line.trim();
            
            if (trimmedLine.isEmpty()) {
                // Empty line - don't count
                continue;
            } else if (trimmedLine.startsWith("#") || trimmedLine.startsWith("//")) {
                // Comment line
                commentLines++;
            } else if (isBloblangCodeLine(trimmedLine)) {
                // Actual code line
                linesOfCode++;
            }
        }
        
        return new BloblangMetrics(totalLines, linesOfCode, commentLines);
    }

    private boolean isBloblangCodeLine(String line) {
        // Check if line contains Bloblang/Benthos syntax
        return line.contains(":") ||  // YAML key-value pairs
               line.contains("=") ||  // Bloblang assignments
               line.contains("map") ||
               line.contains("filter") ||
               line.contains("select") ||
               line.contains("root") ||
               line.contains("this") ||
               line.contains("deleted()") ||
               line.contains("match") ||
               line.matches(".*\\.(length|type|string|number).*") ||  // Bloblang methods
               line.matches(".*\\$\\{.*\\}.*"); // Variable interpolation
    }

    private static class BloblangMetrics {
        final int lines;
        final int linesOfCode;
        final int commentLines;

        BloblangMetrics(int lines, int linesOfCode, int commentLines) {
            this.lines = lines;
            this.linesOfCode = linesOfCode;
            this.commentLines = commentLines;
        }
    }
}
