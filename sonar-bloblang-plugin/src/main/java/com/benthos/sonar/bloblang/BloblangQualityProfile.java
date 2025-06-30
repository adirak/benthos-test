package com.benthos.sonar.bloblang;

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;

/**
 * Default quality profile for Bloblang language
 */
public class BloblangQualityProfile implements BuiltInQualityProfilesDefinition {

    @Override
    public void define(Context context) {
        NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile("Sonar way", BloblangLanguage.KEY);
        profile.setDefault(true);
        profile.done();
    }
}
