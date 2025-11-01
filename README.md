# SwitchHub Java SDK

A Java SDK for SwitchHub - A feature flag and configuration management system that enables dynamic feature toggling with real-time updates.

[![Maven Central](https://img.shields.io/badge/maven--central-v0.0.1-blue)](https://central.sonatype.com/)
[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen)](https://spring.io/projects/spring-boot)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Build](https://github.com/JhaPrashant1108/switchhub-java-sdk/workflows/Release%20on%20Version%20Change/badge.svg)](https://github.com/JhaPrashant1108/switchhub-java-sdk/actions)

## Overview

SwitchHub Java SDK provides a simple and efficient way to integrate feature flags into your Java applications. It enables you to control feature rollouts, perform A/B testing, and manage configurations dynamically without requiring code deployments.

## Features

- **Feature Flag Management** - Toggle features on/off remotely without code deployments
- **Real-time Updates** - Automatic synchronization of feature flags via RabbitMQ messaging
- **Metered Rollouts** - Gradual feature rollouts with weighted distribution for A/B testing and canary releases
- **Context-aware Switching** - Support for contextual feature flags based on user segments or custom criteria
- **Spring Boot Integration** - Seamless integration with Spring Boot via auto-configuration
- **In-memory Caching** - Fast flag evaluation with local caching for optimal performance
- **Environment Support** - Manage flags across different environments (dev, staging, production)

## Requirements

- **Java**: 21 or higher
- **Spring Boot**: 3.5.6 or compatible version
- **RabbitMQ**: Message broker for real-time flag updates
- **Maven**: 3.6+ (for building from source)

## Installation

### Maven

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.switchhub</groupId>
    <artifactId>switchhub-sdk</artifactId>
    <version>0.0.1</version>
</dependency>
```

### Gradle

Add the following to your `build.gradle`:

```gradle
implementation 'io.switchhub:switchhub-sdk:0.0.1'
```

## Configuration

Add the following properties to your `application.properties` or `application.yml`:

**Important:** The SDK must be explicitly enabled by setting `switchHub.sdk.enabled=true`

### application.properties

```properties
# SwitchHub SDK Configuration
switchHub.sdk.enabled=true
switchHub.backend.baseUrl=https://your-switchhub-backend.com
switchHub.sdk.applicationName=your-app-name
switchHub.sdk.environmentName=production
switchHub.sdk.switches=feature-flag-1,feature-flag-2,feature-flag-3

# RabbitMQ Configuration
rabbitmq.hostName=your-rabbitmq-host
rabbitmq.port=5672
rabbitmq.userName=guest
rabbitmq.password=guest
rabbitmq.virtualHost=/
rabbitmq.sslProtocolEnabled=false
rabbitmq.automaticRecoveryEnabled=true
rabbitmq.requestedHeartBeat=60
rabbitmq.connectionTimeout=30000
```

### application.yml

```yaml
switchHub:
  sdk:
    enabled: true
    applicationName: your-app-name
    environmentName: production
    switches: feature-flag-1,feature-flag-2,feature-flag-3
  backend:
    baseUrl: https://your-switchhub-backend.com

rabbitmq:
  hostName: your-rabbitmq-host
  port: 5672
  userName: guest
  password: guest
  virtualHost: /
  sslProtocolEnabled: false
  automaticRecoveryEnabled: true
  requestedHeartBeat: 60
  connectionTimeout: 30000
```

## Usage

### Basic Feature Flag Check

Inject `SwitchUtil` into your Spring components and check feature flags:

```java
import io.switchhub.switchhub_sdk.util.SwitchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyService {

    @Autowired
    private SwitchUtil switchUtil;

    public void myMethod() {
        if (switchUtil.getSwitchValue("new-feature", null)) {
            // Execute new feature code
            System.out.println("New feature is enabled!");
        } else {
            // Execute old code path
            System.out.println("Using legacy implementation");
        }
    }
}
```

### Context-aware Feature Flags

Use context maps for user-specific or segment-specific feature flags:

```java
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private SwitchUtil switchUtil;

    public void processUser(String userId) {
        Map<String, String> context = new HashMap<>();
        context.put("userId", userId);

        if (switchUtil.getSwitchValue("premium-feature", context)) {
            // Show premium features
            enablePremiumFeatures();
        } else {
            // Show standard features
            enableStandardFeatures();
        }
    }
}
```

### Metered Rollouts (A/B Testing)

The SDK supports weighted distribution for gradual rollouts:

```java
// The SDK automatically handles metered rollouts based on backend configuration
// For example: 20% of users see the new feature, 80% see the old one

Map<String, String> context = new HashMap<>();
context.put("userId", userId);

if (switchUtil.getSwitchValue("gradual-rollout-feature", context)) {
    // New feature (20% of traffic)
    implementNewFeature();
} else {
    // Old feature (80% of traffic)
    implementOldFeature();
}
```

## How It Works

1. **Initialization** - On application startup, the SDK connects to the SwitchHub backend and fetches all configured feature flags
2. **Local Caching** - Flags are stored in-memory for fast evaluation without network calls
3. **Real-time Updates** - The SDK subscribes to RabbitMQ queues to receive real-time flag updates
4. **Flag Evaluation** - When you call `getSwitchValue()`, the SDK checks the local cache and applies any metered rollout logic
5. **Automatic Sync** - Any changes to flags in the SwitchHub dashboard are instantly propagated to all connected instances

## Project Structure

```
src/main/java/io/switchhub/switchhub_sdk/
├── components/          # Core components
│   ├── SharedDataStoreComponent.java      # In-memory cache
│   ├── StartupComponent.java              # Initialization logic
│   ├── SwitchBoardApiComponent.java       # Backend API client
│   └── SwitchConsumer.java                # RabbitMQ consumer
├── config/              # Configuration classes
│   ├── EnvConfig.java                     # Environment configuration
│   ├── RabbitMQConfig.java                # RabbitMQ setup
│   └── RestTemplateConfig.java            # HTTP client config
├── dtos/                # Data transfer objects
├── models/              # Domain models
│   ├── SwitchModel.java                   # Feature flag model
│   └── MessageModel.java                  # Message model
├── util/                # Utility classes
│   ├── SwitchUtil.java                    # Main SDK interface
│   └── WeightedBooleanGenerator.java      # Metered rollout logic
└── SwitchhubSdkAutoConfiguration.java     # Spring Boot auto-config
```

## Building from Source

```bash
# Clone the repository
git clone https://github.com/JhaPrashant1108/switchhub-java-sdk.git
cd switchhub-java-sdk

# Build with Maven
./mvnw clean install

# Run tests
./mvnw test

# Generate code coverage report
./mvnw clean test jacoco:report
```

## Testing

The SDK includes comprehensive unit tests with JaCoCo code coverage reporting:

```bash
# Run tests with coverage
./mvnw clean test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

### Current Test Coverage

- **Overall Coverage**: 99%
- **Components Package**: 100%
- **Config Package**: 100%
- **Utils Package**: 100%

## API Reference

### SwitchUtil

Main interface for interacting with feature flags.

#### Methods

**`boolean getSwitchValue(String switchName, Map<String, String> contextMap)`**

Evaluates a feature flag and returns its status.

**Parameters:**
- `switchName` - The name of the feature flag to evaluate
- `contextMap` - Optional context for metered rollouts (can be `null`)

**Returns:**
- `true` if the feature is enabled
- `false` if the feature is disabled or not found

**Example:**
```java
boolean isEnabled = switchUtil.getSwitchValue("my-feature", null);
```

**With Context:**
```java
Map<String, String> context = new HashMap<>();
context.put("userId", "user123");
boolean isEnabled = switchUtil.getSwitchValue("my-feature", context);
```

## Continuous Integration

This project uses GitHub Actions for automated building, testing, and deployment:

- **Build & Deploy** - Automatically deploys to Maven Central when version changes
- **PR Validation** - Runs tests and coverage checks on pull requests
- **Code Coverage** - Maintains minimum 50% code coverage

See [.github/workflows/README.md](.github/workflows/README.md) for more details.

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Run tests (`./mvnw test`)
5. Ensure code coverage is maintained
6. Commit your changes (`git commit -m 'Add amazing feature'`)
7. Push to the branch (`git push origin feature/amazing-feature`)
8. Open a Pull Request

### Development Guidelines

- Follow existing code style and conventions
- Write unit tests for new features
- Maintain or improve code coverage (minimum 50%)
- Update documentation as needed
- Ensure all tests pass before submitting PR

## Versioning

This project follows [Semantic Versioning](https://semver.org/):

- **MAJOR** - Breaking changes (e.g., 1.0.0 → 2.0.0)
- **MINOR** - New features, backward compatible (e.g., 1.0.0 → 1.1.0)
- **PATCH** - Bug fixes (e.g., 1.0.0 → 1.0.1)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For issues, questions, or contributions:

- **Issues**: [GitHub Issues](https://github.com/JhaPrashant1108/switchhub-java-sdk/issues)
- **Discussions**: [GitHub Discussions](https://github.com/JhaPrashant1108/switchhub-java-sdk/discussions)
- **Email**: prashant.jha0008@gmail.com

## Author

**Prashant Jha**
- GitHub: [@JhaPrashant1108](https://github.com/JhaPrashant1108)
- Email: prashant.jha0008@gmail.com

## Acknowledgments

- Spring Boot team for the excellent framework
- RabbitMQ team for the reliable messaging system
- All contributors who help improve this SDK

---

**Note**: This SDK is under active development. For production use, please ensure proper testing in your environment.
