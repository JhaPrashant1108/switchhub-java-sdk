package in.switchhub.switchhub_sdk.config;

import com.rabbitmq.client.ConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import com.rabbitmq.client.Channel;

import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RabbitMQConfigTest {

    @Mock(lenient = true)
    private EnvConfig envConfig;

    @InjectMocks
    private RabbitMQConfig rabbitMQConfig;

    @BeforeEach
    void setUp() {
        lenient().when(envConfig.getRabbitmqHostName()).thenReturn("localhost");
        lenient().when(envConfig.getRabbitmqPort()).thenReturn(5672);
        lenient().when(envConfig.getRabbitmqUserName()).thenReturn("guest");
        lenient().when(envConfig.getRabbitmqVirtualHost()).thenReturn("/");
        lenient().when(envConfig.getRabbitmqPassword()).thenReturn("guest");
        lenient().when(envConfig.isRabbitmqSslProtocolEnabled()).thenReturn(false);
        lenient().when(envConfig.isRabbitmqAutomaticRecoveryEnabled()).thenReturn(true);
        lenient().when(envConfig.getRabbitmqRequestedHeartBeat()).thenReturn(30);
        lenient().when(envConfig.getRabbitmqConnectionTimeout()).thenReturn(60000);
        lenient().when(envConfig.getSwitchHubSdkEnvironmentName()).thenReturn("dev");
        lenient().when(envConfig.getSwitchHubSdkApplicationName()).thenReturn("test-app");
    }

    @Test
    void testRabbitFactory_BasicConfiguration() throws Exception {
        ConnectionFactory connectionFactory = rabbitMQConfig.rabbitFactory();

        assertNotNull(connectionFactory);
        assertEquals("localhost", connectionFactory.getHost());
        assertEquals(5672, connectionFactory.getPort());
        assertEquals("guest", connectionFactory.getUsername());
        assertEquals("/", connectionFactory.getVirtualHost());
        assertEquals(60000, connectionFactory.getConnectionTimeout());
        assertEquals(30, connectionFactory.getRequestedHeartbeat());
        assertTrue(connectionFactory.isAutomaticRecoveryEnabled());
    }

    @Test
    void testRabbitFactory_WithSslEnabled() throws Exception {
        when(envConfig.isRabbitmqSslProtocolEnabled()).thenReturn(true);

        ConnectionFactory connectionFactory = rabbitMQConfig.rabbitFactory();

        assertNotNull(connectionFactory);
        assertTrue(connectionFactory.isSSL());
    }

    @Test
    void testRabbitFactory_WithSslDisabled() throws Exception {
        when(envConfig.isRabbitmqSslProtocolEnabled()).thenReturn(false);

        ConnectionFactory connectionFactory = rabbitMQConfig.rabbitFactory();

        assertNotNull(connectionFactory);
        assertFalse(connectionFactory.isSSL());
    }

    @Test
    void testRabbitFactory_WithCustomPort() throws Exception {
        when(envConfig.getRabbitmqPort()).thenReturn(5673);

        ConnectionFactory connectionFactory = rabbitMQConfig.rabbitFactory();

        assertEquals(5673, connectionFactory.getPort());
    }

    @Test
    void testRabbitFactory_WithCustomVirtualHost() throws Exception {
        when(envConfig.getRabbitmqVirtualHost()).thenReturn("/custom");

        ConnectionFactory connectionFactory = rabbitMQConfig.rabbitFactory();

        assertEquals("/custom", connectionFactory.getVirtualHost());
    }

    @Test
    void testRabbitFactory_WithAutomaticRecoveryDisabled() throws Exception {
        when(envConfig.isRabbitmqAutomaticRecoveryEnabled()).thenReturn(false);

        ConnectionFactory connectionFactory = rabbitMQConfig.rabbitFactory();

        assertFalse(connectionFactory.isAutomaticRecoveryEnabled());
    }

    @Test
    void testRabbitMqConnectionFactory_BasicConfiguration() throws Exception {
        ConnectionFactory mockConnectionFactory = mock(ConnectionFactory.class);

        CachingConnectionFactory cachingConnectionFactory =
                rabbitMQConfig.rabbitMqconnectionFactory(mockConnectionFactory);

        assertNotNull(cachingConnectionFactory);
        assertTrue(cachingConnectionFactory.isPublisherReturns());
    }

    @Test
    void testRabbitMqConnectionFactory_WithHeartbeat() throws Exception {
        when(envConfig.getRabbitmqRequestedHeartBeat()).thenReturn(60);
        ConnectionFactory mockConnectionFactory = mock(ConnectionFactory.class);

        CachingConnectionFactory cachingConnectionFactory =
                rabbitMQConfig.rabbitMqconnectionFactory(mockConnectionFactory);

        assertNotNull(cachingConnectionFactory);
        // Heartbeat is set correctly in the underlying ConnectionFactory
        verify(envConfig, atLeastOnce()).getRabbitmqRequestedHeartBeat();
    }

    @Test
    void testJsonMessageConverter() {
        MessageConverter messageConverter = rabbitMQConfig.jsonMessageConverter();

        assertNotNull(messageConverter);
        assertEquals("org.springframework.amqp.support.converter.Jackson2JsonMessageConverter",
                messageConverter.getClass().getName());
    }

    @Test
    void testRabbitTemplate_Configuration() throws Exception {
        // Mock the connection factory
        CachingConnectionFactory cachingConnectionFactory = mock(CachingConnectionFactory.class);
        org.springframework.amqp.rabbit.connection.Connection mockConnection =
                mock(org.springframework.amqp.rabbit.connection.Connection.class);
        Channel mockChannel = mock(Channel.class);

        when(cachingConnectionFactory.createConnection()).thenReturn(mockConnection);
        when(mockConnection.createChannel(false)).thenReturn(mockChannel);

        MessageConverter messageConverter = rabbitMQConfig.jsonMessageConverter();

        RabbitTemplate rabbitTemplate = rabbitMQConfig.rabbitTemplate(
                cachingConnectionFactory, messageConverter);

        assertNotNull(rabbitTemplate);
        assertEquals(messageConverter, rabbitTemplate.getMessageConverter());
        verify(cachingConnectionFactory).createConnection();
    }

    @Test
    void testRabbitTemplate_ConnectionFailure() throws Exception {
        CachingConnectionFactory cachingConnectionFactory = mock(CachingConnectionFactory.class);

        when(cachingConnectionFactory.createConnection()).thenThrow(new RuntimeException("Connection failed"));

        MessageConverter messageConverter = rabbitMQConfig.jsonMessageConverter();

        assertThrows(RuntimeException.class, () -> {
            rabbitMQConfig.rabbitTemplate(cachingConnectionFactory, messageConverter);
        });
    }

    @Test
    void testExchange() {
        TopicExchange exchange = rabbitMQConfig.exchange();

        assertNotNull(exchange);
        assertEquals("app_exchange", exchange.getName());
        assertTrue(exchange.isDurable());
    }

    @Test
    void testQueue_WithDefaultEnvironment() {
        when(envConfig.getSwitchHubSdkEnvironmentName()).thenReturn("production");
        when(envConfig.getSwitchHubSdkApplicationName()).thenReturn("my-app");

        Queue queue = rabbitMQConfig.queue();

        assertNotNull(queue);
        assertEquals("queue.production.my-app", queue.getName());
        assertTrue(queue.isDurable());
    }

    @Test
    void testQueue_WithDevelopmentEnvironment() {
        when(envConfig.getSwitchHubSdkEnvironmentName()).thenReturn("dev");
        when(envConfig.getSwitchHubSdkApplicationName()).thenReturn("test-service");

        Queue queue = rabbitMQConfig.queue();

        assertNotNull(queue);
        assertEquals("queue.dev.test-service", queue.getName());
        assertTrue(queue.isDurable());
    }

    @Test
    void testQueue_WithStagingEnvironment() {
        when(envConfig.getSwitchHubSdkEnvironmentName()).thenReturn("staging");
        when(envConfig.getSwitchHubSdkApplicationName()).thenReturn("api-service");

        Queue queue = rabbitMQConfig.queue();

        assertNotNull(queue);
        assertEquals("queue.staging.api-service", queue.getName());
    }

    @Test
    void testBinding_WithDefaultConfiguration() {
        Queue queue = rabbitMQConfig.queue();
        TopicExchange exchange = rabbitMQConfig.exchange();

        Binding binding = rabbitMQConfig.binding(queue, exchange);

        assertNotNull(binding);
        assertEquals("queue.dev.test-app", binding.getDestination());
        assertEquals("app.dev.test-app", binding.getRoutingKey());
        assertEquals(Binding.DestinationType.QUEUE, binding.getDestinationType());
    }

    @Test
    void testBinding_WithProductionEnvironment() {
        when(envConfig.getSwitchHubSdkEnvironmentName()).thenReturn("production");
        when(envConfig.getSwitchHubSdkApplicationName()).thenReturn("prod-service");

        Queue queue = rabbitMQConfig.queue();
        TopicExchange exchange = rabbitMQConfig.exchange();

        Binding binding = rabbitMQConfig.binding(queue, exchange);

        assertNotNull(binding);
        assertEquals("queue.production.prod-service", binding.getDestination());
        assertEquals("app.production.prod-service", binding.getRoutingKey());
    }

    @Test
    void testBinding_RoutingKeyMatchesQueueName() {
        Queue queue = rabbitMQConfig.queue();
        TopicExchange exchange = rabbitMQConfig.exchange();

        Binding binding = rabbitMQConfig.binding(queue, exchange);

        String expectedEnvironment = envConfig.getSwitchHubSdkEnvironmentName();
        String expectedApplication = envConfig.getSwitchHubSdkApplicationName();
        String expectedQueueName = "queue." + expectedEnvironment + "." + expectedApplication;
        String expectedRoutingKey = "app." + expectedEnvironment + "." + expectedApplication;

        assertEquals(expectedQueueName, binding.getDestination());
        assertEquals(expectedRoutingKey, binding.getRoutingKey());
    }

    @Test
    void testBinding_ExchangeName() {
        Queue queue = rabbitMQConfig.queue();
        TopicExchange exchange = rabbitMQConfig.exchange();

        Binding binding = rabbitMQConfig.binding(queue, exchange);

        assertEquals("app_exchange", binding.getExchange());
    }

    @Test
    void testRabbitFactory_AllPropertiesUsedFromEnvConfig() throws Exception {
        ConnectionFactory connectionFactory = rabbitMQConfig.rabbitFactory();

        // These are called multiple times (config setting + logging)
        verify(envConfig, atLeastOnce()).getRabbitmqHostName();
        verify(envConfig, atLeastOnce()).getRabbitmqPort();
        verify(envConfig).getRabbitmqUserName();
        verify(envConfig).getRabbitmqVirtualHost();
        verify(envConfig).getRabbitmqPassword();
        verify(envConfig).isRabbitmqSslProtocolEnabled();
        verify(envConfig).isRabbitmqAutomaticRecoveryEnabled();
        verify(envConfig, atLeastOnce()).getRabbitmqRequestedHeartBeat();
        verify(envConfig, atLeastOnce()).getRabbitmqConnectionTimeout();
    }

    @Test
    void testQueue_UsesCorrectEnvironmentAndApplication() {
        rabbitMQConfig.queue();

        verify(envConfig, atLeastOnce()).getSwitchHubSdkEnvironmentName();
        verify(envConfig, atLeastOnce()).getSwitchHubSdkApplicationName();
    }

    @Test
    void testBinding_UsesCorrectEnvironmentAndApplication() {
        Queue queue = rabbitMQConfig.queue();
        TopicExchange exchange = rabbitMQConfig.exchange();

        rabbitMQConfig.binding(queue, exchange);

        verify(envConfig, atLeastOnce()).getSwitchHubSdkEnvironmentName();
        verify(envConfig, atLeastOnce()).getSwitchHubSdkApplicationName();
    }

    @Test
    void testRabbitMqConnectionFactory_CreatesValidInstance() throws Exception {
        ConnectionFactory mockConnectionFactory = mock(ConnectionFactory.class);

        CachingConnectionFactory cachingConnectionFactory =
                rabbitMQConfig.rabbitMqconnectionFactory(mockConnectionFactory);

        assertNotNull(cachingConnectionFactory);
        // Verify it's properly configured
        assertTrue(cachingConnectionFactory.isPublisherReturns());
    }

    @Test
    void testRabbitFactory_SocketConfiguratorIsApplied() throws Exception {
        // Create the factory with socket configurator
        ConnectionFactory connectionFactory = rabbitMQConfig.rabbitFactory();

        // Get the socket configurator and test it
        Socket mockSocket = mock(Socket.class);

        // Apply the socket configurator
        connectionFactory.getSocketConfigurator().configure(mockSocket);

        // Verify socket configuration was applied
        verify(mockSocket).setKeepAlive(true);
        verify(mockSocket).setSoTimeout(anyInt());
    }
}
