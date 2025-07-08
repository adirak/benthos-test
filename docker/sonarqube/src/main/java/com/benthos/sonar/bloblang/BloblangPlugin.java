package com.benthos.sonar.bloblang;

import org.sonar.api.Plugin;

/**
 * Main plugin class for Bloblang language support in SonarQube
 */
public class BloblangPlugin implements Plugin {

    @Override
    public void define(Context context) {
        // Register the Bloblang language
        context.addExtension(BloblangLanguage.class);
        
        // Register the sensor for analyzing Bloblang files
        context.addExtension(BloblangSensor.class);
        
        // Register quality profile
        context.addExtension(BloblangQualityProfile.class);
        
        // Register file predicates
        context.addExtension(BloblangFilePredicate.class);
    }
} 