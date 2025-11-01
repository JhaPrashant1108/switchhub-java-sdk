package in.switchhub.switchhub_sdk.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = EnvConfig.class)
@TestPropertySource(properties = {
        "rabbitmq.hostName=localhost",
        "rabbitmq.port=5672",
        "rabbitmq.userName=guest",
        "rabbitmq.virtualHost=/",
        "rabbitmq.password=guest",
        "rabbitmq.sslProtocolEnabled=false",
        "rabbitmq.automaticRecoveryEnabled=true",
        "rabbitmq.requestedHeartBeat=30",
        "rabbitmq.connectionTimeout=60000",
        "switchHub.backend.baseUrl=http://localhost:8080",
        "switchHub.sdk.switches=feature1,feature2,feature3",
        "switchHub.sdk.applicationName=test-app",
        "switchHub.sdk.environmentName=dev"
})
class EnvConfigTest {

    @Autowired
    private EnvConfig envConfig;

    @Test
    void testRabbitmqHostName() {
        assertEquals("localhost", envConfig.getRabbitmqHostName());
    }

    @Test
    void testRabbitmqPort() {
        assertEquals(5672, envConfig.getRabbitmqPort());
    }

    @Test
    void testRabbitmqUserName() {
        assertEquals("guest", envConfig.getRabbitmqUserName());
    }

    @Test
    void testRabbitmqVirtualHost() {
        assertEquals("/", envConfig.getRabbitmqVirtualHost());
    }

    @Test
    void testRabbitmqPassword() {
        assertEquals("guest", envConfig.getRabbitmqPassword());
    }

    @Test
    void testRabbitmqSslProtocolEnabled() {
        assertFalse(envConfig.isRabbitmqSslProtocolEnabled());
    }

    @Test
    void testRabbitmqAutomaticRecoveryEnabled() {
        assertTrue(envConfig.isRabbitmqAutomaticRecoveryEnabled());
    }

    @Test
    void testRabbitmqRequestedHeartBeat() {
        assertEquals(30, envConfig.getRabbitmqRequestedHeartBeat());
    }

    @Test
    void testRabbitmqConnectionTimeout() {
        assertEquals(60000, envConfig.getRabbitmqConnectionTimeout());
    }

    @Test
    void testSwitchHubBackendBaseUrl() {
        assertEquals("http://localhost:8080", envConfig.getSwitchHubBackendBaseUrl());
    }

    @Test
    void testSwitchHubSdkSwitches() {
        assertNotNull(envConfig.getSwitchHubSdkSwitches());
        assertEquals(3, envConfig.getSwitchHubSdkSwitches().size());
        assertTrue(envConfig.getSwitchHubSdkSwitches().contains("feature1"));
        assertTrue(envConfig.getSwitchHubSdkSwitches().contains("feature2"));
        assertTrue(envConfig.getSwitchHubSdkSwitches().contains("feature3"));
    }

    @Test
    void testSwitchHubSdkApplicationName() {
        assertEquals("test-app", envConfig.getSwitchHubSdkApplicationName());
    }

    @Test
    void testSwitchHubSdkEnvironmentName() {
        assertEquals("dev", envConfig.getSwitchHubSdkEnvironmentName());
    }
}
