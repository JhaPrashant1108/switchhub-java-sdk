package in.switchhub.switchhub_sdk.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class EnvConfig {

    @Value("${rabbitmq.hostName}")
    private String rabbitmqHostName;

    @Value("${rabbitmq.port}")
    private int rabbitmqPort;

    @Value("${rabbitmq.userName}")
    private String rabbitmqUserName;

    @Value("${rabbitmq.virtualHost}")
    private String rabbitmqVirtualHost;

    @Value("${rabbitmq.password}")
    private String rabbitmqPassword;

    @Value("${rabbitmq.sslProtocolEnabled}")
    private boolean rabbitmqSslProtocolEnabled;

    @Value("${rabbitmq.automaticRecoveryEnabled}")
    private boolean rabbitmqAutomaticRecoveryEnabled;

    @Value("${rabbitmq.requestedHeartBeat}")
    private int rabbitmqRequestedHeartBeat;

    @Value("${rabbitmq.connectionTimeout}")
    private int rabbitmqConnectionTimeout;

    @Value("${rabbitmq.networkRecoveryInterval}")
    private int rabbitmqNetworkRecoveryInterval;

    @Value("${rabbitmq.topologyRecoverEnabled}")
    private boolean rabbitmqTopologyRecoverEnabled;

    @Value("${switchHub.backend.baseUrl}")
    private String switchHubBackendBaseUrl;

    @Value("#{'${switchHub.sdk.switches}'.split(',')}")
    private List<String> switchHubSdkSwitches;

    @Value("${switchHub.sdk.applicationName}")
    private String switchHubSdkApplicationName;

    @Value("${switchHub.sdk.environmentName}")
    private String switchHubSdkEnvironmentName;

    public String getRabbitmqHostName() {
        return rabbitmqHostName;
    }

    public int getRabbitmqPort() {
        return rabbitmqPort;
    }

    public String getRabbitmqUserName() {
        return rabbitmqUserName;
    }

    public String getRabbitmqVirtualHost() {
        return rabbitmqVirtualHost;
    }

    public String getRabbitmqPassword() {
        return rabbitmqPassword;
    }

    public boolean isRabbitmqSslProtocolEnabled() {
        return rabbitmqSslProtocolEnabled;
    }

    public boolean isRabbitmqAutomaticRecoveryEnabled() {
        return rabbitmqAutomaticRecoveryEnabled;
    }

    public int getRabbitmqRequestedHeartBeat() {
        return rabbitmqRequestedHeartBeat;
    }

    public int getRabbitmqConnectionTimeout() {
        return rabbitmqConnectionTimeout;
    }

    public int getRabbitmqNetworkRecoveryInterval() {
        return rabbitmqNetworkRecoveryInterval;
    }

    public boolean isRabbitmqTopologyRecoverEnabled() {
        return rabbitmqTopologyRecoverEnabled;
    }

    public String getSwitchHubBackendBaseUrl() {
        return switchHubBackendBaseUrl;
    }

    public List<String> getSwitchHubSdkSwitches() {
        return switchHubSdkSwitches;
    }

    public String getSwitchHubSdkApplicationName() {
        return switchHubSdkApplicationName;
    }

    public String getSwitchHubSdkEnvironmentName() {
        return switchHubSdkEnvironmentName;
    }
}
