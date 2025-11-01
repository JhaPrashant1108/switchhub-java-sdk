package in.switchhub.switchhub_sdk;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;

/**
 * Auto-configuration for Switchboard SDK.
 * This will be automatically loaded when the SDK is added to a Spring Boot project.
 *
 * To enable this SDK, add the following to your application.properties:
 * switchboard.sdk.enabled=true
 */
@AutoConfiguration
@ConditionalOnProperty(
    prefix = "switchHub.sdk",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = false
)
@ComponentScan(basePackages = "in.switchhub.switchhub_sdk")
public class SwitchHubSdkAutoConfiguration {

}
