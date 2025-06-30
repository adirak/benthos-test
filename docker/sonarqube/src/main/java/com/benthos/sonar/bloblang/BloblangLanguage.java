package com.benthos.sonar.bloblang;

import org.sonar.api.resources.AbstractLanguage;

/**
 * Definition of Bloblang language for SonarQube
 */
public class BloblangLanguage extends AbstractLanguage {

    public static final String KEY = "bloblang";
    public static final String NAME = "Bloblang";
    
    // File extensions for Bloblang files - only pure Bloblang files
    public static final String[] FILE_SUFFIXES = {".blobl", ".bloblang"};

    public BloblangLanguage() {
        super(KEY, NAME);
    }

    @Override
    public String[] getFileSuffixes() {
        return FILE_SUFFIXES;
    }
    
    /**
     * Check if a file contains Bloblang content based on YAML structure
     */
    public static boolean isBloblangYaml(String content) {
        if (content == null) return false;
        
        // Check for Benthos/Bloblang specific keywords
        return content.contains("pipeline:") ||
               content.contains("processors:") ||
               content.contains("mapping:") ||
               content.contains("bloblang:") ||
               content.contains("input:") && content.contains("output:") ||
               content.contains("resources:") && content.contains("transform:");
    }
} 