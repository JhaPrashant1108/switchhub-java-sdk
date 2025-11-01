package in.switchhub.switchhub_sdk.config;

import in.switchhub.switchhub_sdk.constants.SwitchHubConstants;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);

    @Autowired
    EnvConfig envConfig;


    @Bean
    public ConnectionFactory rabbitFactory() throws Exception{
        ConnectionFactory rabbitFactory = new ConnectionFactory();
        rabbitFactory.setHost(envConfig.getRabbitmqHostName());
        rabbitFactory.setPort(envConfig.getRabbitmqPort());
        rabbitFactory.setUsername(envConfig.getRabbitmqUserName());
        rabbitFactory.setVirtualHost(envConfig.getRabbitmqVirtualHost());
        rabbitFactory.setPassword(envConfig.getRabbitmqPassword());
        if(envConfig.isRabbitmqSslProtocolEnabled()) {
            rabbitFactory.useSslProtocol();
        }
        rabbitFactory.setAutomaticRecoveryEnabled(envConfig.isRabbitmqAutomaticRecoveryEnabled());

        // Heartbeat configuration - CRITICAL for preventing idle connection drops
        // For CloudAMQP with 60s load balancer timeout, use 10-15s heartbeat
        rabbitFactory.setRequestedHeartbeat(envConfig.getRabbitmqRequestedHeartBeat());

        // Connection timeout - only for initial connection establishment
        rabbitFactory.setConnectionTimeout(envConfig.getRabbitmqConnectionTimeout());

        // Socket configuration to prevent idle timeouts
        rabbitFactory.setSocketConfigurator(socket -> {
            // Enable TCP keepalive to prevent idle connection drops
            socket.setKeepAlive(true);

            // Set socket timeout for read operations (prevent indefinite blocking)
            // This should be higher than heartbeat interval
            // Recommended: (heartbeat * 2) + buffer, in milliseconds
            int socketTimeout = (envConfig.getRabbitmqRequestedHeartBeat() * 2 + 10) * 1000;
            socket.setSoTimeout(socketTimeout);

            log.debug("Socket configured with keepAlive=true, soTimeout={}ms", socketTimeout);
        });

        log.info("RabbitMQ factory configured - Host: {}, Port: {}, Heartbeat: {}s, ConnectionTimeout: {}ms",
                 envConfig.getRabbitmqHostName(),
                 envConfig.getRabbitmqPort(),
                 envConfig.getRabbitmqRequestedHeartBeat(),
                 envConfig.getRabbitmqConnectionTimeout());

        return rabbitFactory;
    }


    @Bean
    public CachingConnectionFactory rabbitMqconnectionFactory(ConnectionFactory rabbitFactory) throws Exception {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(rabbitFactory);
        cachingConnectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        cachingConnectionFactory.setPublisherReturns(true);
        cachingConnectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CHANNEL);
        cachingConnectionFactory.setRequestedHeartBeat(envConfig.getRabbitmqRequestedHeartBeat());

        // Add connection recovery settings for better resilience
        cachingConnectionFactory.getRabbitConnectionFactory().setNetworkRecoveryInterval(5000); // Retry every 5 seconds
        cachingConnectionFactory.getRabbitConnectionFactory().setTopologyRecoveryEnabled(true); // Recover queues/exchanges

        log.info("RabbitMQ connection factory configured with heartbeat: {} seconds, automatic recovery: {}",
                 envConfig.getRabbitmqRequestedHeartBeat(),
                 envConfig.isRabbitmqAutomaticRecoveryEnabled());

        return cachingConnectionFactory;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory rabbitMqconnectionFactory , MessageConverter jsonMessageConverter) throws Exception {
        RabbitTemplate template = new RabbitTemplate(rabbitMqconnectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        try {
            rabbitMqconnectionFactory.createConnection().createChannel(false);
            log.info(SwitchHubConstants.LogMessages.RABBITMQ_CONNECTION_ESTABLISHED);
        } catch (Exception e) {
            throw new RuntimeException(SwitchHubConstants.LogMessages.RABBITMQ_CONNECTION_FAILED, e);
        }
        return template;
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(SwitchHubConstants.RabbitMQ.EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue queue() {
        String queueName = SwitchHubConstants.RabbitMQ.QUEUE_PREFIX +
                          envConfig.getSwitchHubSdkEnvironmentName() + "." +
                          envConfig.getSwitchHubSdkApplicationName();
        return new Queue(queueName, true);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        String routingKey = SwitchHubConstants.RabbitMQ.ROUTING_KEY_PREFIX +
                           envConfig.getSwitchHubSdkEnvironmentName() + "." +
                           envConfig.getSwitchHubSdkApplicationName();
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }
}
