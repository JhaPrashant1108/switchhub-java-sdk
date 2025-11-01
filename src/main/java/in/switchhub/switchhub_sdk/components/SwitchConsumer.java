package in.switchhub.switchhub_sdk.components;

import in.switchhub.switchhub_sdk.constants.SwitchHubConstants;
import in.switchhub.switchhub_sdk.models.MessageModel;
import in.switchhub.switchhub_sdk.models.SwitchModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SwitchConsumer {

    private static final Logger log = LoggerFactory.getLogger(SwitchConsumer.class);

    @Autowired
    private SharedDataStoreComponent sharedDataStoreComponent;

    @RabbitListener(queues = "#{ 'queue.' + '${switchHub.sdk.environmentName}' + '.' + '${switchHub.sdk.applicationName}' }")
    public void handleFlagChange(MessageModel messageModel) {
        log.info(SwitchHubConstants.LogMessages.FLAG_CHANGE_RECEIVED, messageModel.toString());

        String switchName = messageModel.getSwitchName();
        SwitchModel switchDetails = messageModel.getSwitchDetails();

        sharedDataStoreComponent.putDataMap(switchName, switchDetails);
        registerMeteredWeights(switchName, switchDetails);
    }

    private void registerMeteredWeights(String switchName, SwitchModel switchDetails) {
        Map<String, Map<String, Integer>> meteredStatus = switchDetails.getMeteredStatus();

        if (meteredStatus != null) {
            sharedDataStoreComponent.resetWeightedMeteredMap(switchName);
            for (Map.Entry<String, Map<String, Integer>> meteredEntry : meteredStatus.entrySet()) {
                String meteredKey = meteredEntry.getKey();
                Integer trueValue = meteredEntry.getValue().get(SwitchHubConstants.Switch.METERED_TRUE_VALUE_KEY);
                if (trueValue != null) {
                    sharedDataStoreComponent.registerWeights(switchName, meteredKey, trueValue);
                }
            }
        } else {
            sharedDataStoreComponent.registerWeights(switchName, SwitchHubConstants.Switch.DEFAULT_KEY, SwitchHubConstants.Validation.MAX_PERCENTAGE);
        }
    }

    public SwitchModel getFlag(String flagName) {
        return sharedDataStoreComponent.getDataMap(flagName);
    }
}
