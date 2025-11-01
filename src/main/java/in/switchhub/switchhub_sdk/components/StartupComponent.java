package in.switchhub.switchhub_sdk.components;

import in.switchhub.switchhub_sdk.config.EnvConfig;
import in.switchhub.switchhub_sdk.constants.SwitchHubConstants;
import in.switchhub.switchhub_sdk.dtos.FetchSwitchRequestDto;
import in.switchhub.switchhub_sdk.dtos.FetchSwitchResponseDto;
import in.switchhub.switchhub_sdk.models.SwitchModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StartupComponent {

    private static final Logger log = LoggerFactory.getLogger(StartupComponent.class);

    @Autowired
    private SwitchHubApiComponent switchHubApiComponent;

    @Autowired
    private SharedDataStoreComponent sharedDataStoreComponent;

    @Autowired
    private EnvConfig envConfig;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        FetchSwitchRequestDto requestDto = new FetchSwitchRequestDto(envConfig.getSwitchHubSdkSwitches());

        try {
            FetchSwitchResponseDto response = switchHubApiComponent.fetchSwitch(
                    requestDto,
                    envConfig.getSwitchHubSdkEnvironmentName(),
                    envConfig.getSwitchHubSdkApplicationName()
            );
            populateSharedDataStoreComponent(response);
            log.info(SwitchHubConstants.LogMessages.STARTUP_API_SUCCESS, response);
        } catch (Exception e) {
            log.error(SwitchHubConstants.LogMessages.STARTUP_API_FAILED, e.getMessage());
        }
    }

    private void populateSharedDataStoreComponent(FetchSwitchResponseDto fetchSwitchResponse) {
        if (fetchSwitchResponse == null || fetchSwitchResponse.getSwitchDetails() == null) {
            return;
        }

        Map<String, SwitchModel> switchDetails = fetchSwitchResponse.getSwitchDetails();
        for (Map.Entry<String, SwitchModel> entry : switchDetails.entrySet()) {
            String switchKey = entry.getKey();
            SwitchModel switchModel = entry.getValue();

            if (switchModel == null) {
                continue;
            }

            sharedDataStoreComponent.putDataMap(switchKey, switchModel);
            registerMeteredWeights(switchKey, switchModel);
        }
    }

    private void registerMeteredWeights(String switchKey, SwitchModel switchModel) {
        Map<String, Map<String, Integer>> meteredStatus = switchModel.getMeteredStatus();

        if (meteredStatus != null) {
            for (Map.Entry<String, Map<String, Integer>> meteredEntry : meteredStatus.entrySet()) {
                String meteredKey = meteredEntry.getKey();
                Integer trueValue = meteredEntry.getValue().get(SwitchHubConstants.Switch.METERED_TRUE_VALUE_KEY);
                if (trueValue != null) {
                    sharedDataStoreComponent.registerWeights(switchKey, meteredKey, trueValue);
                }
            }
        } else {
            sharedDataStoreComponent.registerWeights(switchKey, SwitchHubConstants.Switch.DEFAULT_KEY, SwitchHubConstants.Validation.MAX_PERCENTAGE);
        }
    }
}
