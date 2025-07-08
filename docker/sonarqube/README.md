# SonarQube Bloblang Language Plugin

A SonarQube plugin that adds support for analyzing Benthos Bloblang language files and YAML configurations containing Bloblang mappings.

## Features

- **Language Recognition**: Recognizes `.blobl`, `.bloblang` files and YAML files with Bloblang content
- **Lines of Code Counting**: Properly counts NCLOC (Non-Commented Lines of Code) for Bloblang syntax
- **YAML Integration**: Automatically detects YAML files containing Benthos/Bloblang configurations
- **Metrics Collection**: Provides standard SonarQube metrics (lines, NCLOC, comment lines)

## Supported File Types

1. **Direct Bloblang files**: `.blobl`, `.bloblang`
2. **YAML files with Bloblang content**: `.yaml`, `.yml` containing:
   - `pipeline:` configurations
   - `processors:` with Bloblang mappings
   - `mapping:` blocks
   - `bloblang:` syntax
   - Benthos `input:`/`output:` configurations
   - `resources:` with `transform:` mappings

## Building the Plugin

### Prerequisites

- Java 11 or higher
- Maven 3.6+
- SonarQube 8.9+ (for deployment)

### Build Steps

```bash
# Clone the repository
git clone <repository-url>
cd sonar-bloblang-plugin

# Build the plugin
mvn clean package

# The plugin JAR will be created in target/sonar-bloblang-plugin-1.0.0.jar
```

## Installation

1. **Build the plugin** (see above)
2. **Copy the JAR** to your SonarQube extensions directory:
   ```bash
   cp target/sonar-bloblang-plugin-1.0.0.jar $SONARQUBE_HOME/extensions/plugins/
   ```
3. **Restart SonarQube** server
4. **Verify installation** in SonarQube Administration > Marketplace

## Configuration

### SonarQube Project Configuration

Update your `sonar-project.properties`:

```properties
# Project identification
sonar.projectKey=my-benthos-project
sonar.projectName=My Benthos Project
sonar.projectVersion=1.0

# Source directories
sonar.sources=.

# Include Bloblang and YAML files
sonar.inclusions=**/*.blobl,**/*.bloblang,**/*.yaml,**/*.yml

# Language-specific settings
sonar.bloblang.file.suffixes=.blobl,.bloblang
```

### Language Detection

The plugin automatically detects Bloblang content in YAML files by looking for:

- `pipeline:` - Benthos pipeline configurations
- `processors:` - Processing pipeline definitions
- `mapping:` - Bloblang mapping blocks
- `bloblang:` - Explicit Bloblang syntax
- `input:`/`output:` - Benthos I/O configurations
- `resources:`/`transform:` - Resource transformations

## Example Usage

### Direct Bloblang File (`.blobl`)

```bloblang
# This is a comment
root.user_id = this.id.string()
root.email = this.contact.email.lowercase()
root.created_at = now()
```

### YAML with Bloblang Content (`.yaml`)

```yaml
pipeline:
  processors:
    - mapping: |
        root.processed_data = this.raw_data.map_each(item -> {
          "id": item.id,
          "value": item.value * 2
        })
```

## Metrics Collected

- **Lines**: Total lines in file
- **NCLOC**: Non-commented lines of code
- **Comment Lines**: Lines containing comments (`#` or `//`)

## Development

### Project Structure

```
src/
├── main/java/com/benthos/sonar/bloblang/
│   ├── BloblangPlugin.java           # Main plugin class
│   ├── BloblangLanguage.java         # Language definition
│   ├── BloblangSensor.java           # File analysis logic
│   ├── BloblangQualityProfile.java   # Quality profile
│   └── BloblangFilePredicate.java    # File detection
└── main/resources/
    └── org/sonar/l10n/bloblang.properties  # Localization
```

### Adding New Rules

To add custom analysis rules:

1. Create rule classes extending `org.sonar.api.batch.rule.Rule`
2. Register them in `BloblangPlugin.define()`
3. Implement detection logic in `BloblangSensor`

### Testing

```bash
# Run tests
mvn test

# Integration testing with SonarQube
mvn verify -Dsonar.runtimeVersion=9.9.0.65466
```

## Troubleshooting

### Plugin Not Loading

1. Check SonarQube logs: `$SONARQUBE_HOME/logs/sonar.log`
2. Verify Java version compatibility
3. Ensure plugin JAR is in correct directory

### Files Not Analyzed

1. Check file extensions match configuration
2. Verify `sonar.inclusions` patterns
3. Review YAML content detection logic

### No Lines of Code Counted

1. Ensure files contain recognizable Bloblang syntax
2. Check for proper YAML structure in mixed files
3. Review sensor logs for analysis details

## Contributing

1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality
4. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Related Projects

- [Benthos](https://github.com/benthosdev/benthos) - Stream processing toolkit
- [Bloblang](https://www.benthos.dev/docs/guides/bloblang/about) - Data mapping language
- [SonarQube](https://www.sonarqube.org/) - Code quality platform 