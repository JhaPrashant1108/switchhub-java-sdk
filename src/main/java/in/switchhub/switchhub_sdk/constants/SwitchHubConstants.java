package in.switchhub.switchhub_sdk.constants;

public final class SwitchHubConstants {

    private SwitchHubConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final class RabbitMQ {
        public static final String EXCHANGE_NAME = "app_exchange";
        public static final String ROUTING_KEY_PREFIX = "app.";
        public static final String QUEUE_PREFIX = "queue.";

        private RabbitMQ() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }
    }

    public static final class Api {
        public static final String FETCH_SWITCH_ENDPOINT = "/fetchswitch";
        public static final String HEADER_ENVIRONMENT_NAME = "environmentName";
        public static final String HEADER_APPLICATION_NAME = "applicationName";

        private Api() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }
    }

    public static final class Switch {
        public static final String DEFAULT_KEY = "default";
        public static final String CONTEXT_SUB_KEY = "subKey";
        public static final String METERED_TRUE_VALUE_KEY = "trueValue";

        private Switch() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }
    }

    public static final class Validation {
        public static final int MIN_PERCENTAGE = 0;
        public static final int MAX_PERCENTAGE = 100;
        public static final int PERCENTAGE_POOL_SIZE = 100;

        private Validation() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }
    }

    public static final class ErrorMessages {
        public static final String PERCENTAGE_OUT_OF_RANGE = "Percentage must be between 0 and 100";
        public static final String SWITCH_NOT_FOUND_IN_MEMORY = "Switch not found in memory";
        public static final String NO_GENERATOR_FOUND = "No generator found for key";

        private ErrorMessages() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }
    }

    public static final class LogMessages {
        public static final String RABBITMQ_CONNECTION_ESTABLISHED = "RabbitMQ connection established at startup";
        public static final String RABBITMQ_CONNECTION_FAILED = "Failed to connect to RabbitMQ at startup";
        public static final String STARTUP_API_SUCCESS = "Startup API success: {}";
        public static final String STARTUP_API_FAILED = "Startup API failed: {}";
        public static final String FLAG_CHANGE_RECEIVED = "Received flag change: {}";

        private LogMessages() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }
    }
}
