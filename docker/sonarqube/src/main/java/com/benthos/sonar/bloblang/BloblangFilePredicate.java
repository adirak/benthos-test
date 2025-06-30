package com.benthos.sonar.bloblang;

import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.scanner.ScannerSide;

/**
 * File predicate for identifying Bloblang files
 */
@ScannerSide
public class BloblangFilePredicate {

    private final FileSystem fileSystem;

    public BloblangFilePredicate(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public FilePredicate getBloblangFilePredicate() {
        return fileSystem.predicates().or(
            // Direct Bloblang files
            fileSystem.predicates().hasLanguage(BloblangLanguage.KEY),
            
            // YAML files that might contain Bloblang
            fileSystem.predicates().and(
                fileSystem.predicates().matchesPathPatterns("**/*.yaml", "**/*.yml"),
                fileSystem.predicates().hasType(InputFile.Type.MAIN)
            )
        );
    }
} 