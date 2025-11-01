package in.switchhub.switchhub_sdk.util;

import in.switchhub.switchhub_sdk.components.SharedDataStoreComponent;
import in.switchhub.switchhub_sdk.constants.SwitchHubConstants;
import in.switchhub.switchhub_sdk.models.SwitchModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SwitchUtil {

    private static final Logger log = LoggerFactory.getLogger(SwitchUtil.class);

    @Autowired
    private SharedDataStoreComponent sharedDataStoreComponent;

    public boolean getSwitchValue(String switchName, Map<String, String> contextMap) {
        if (!sharedDataStoreComponent.dataMapContainsKey(switchName)) {
            log.error("{}: {}", SwitchHubConstants.ErrorMessages.SWITCH_NOT_FOUND_IN_MEMORY, switchName);
            return false;
        }

        SwitchModel switchModel = sharedDataStoreComponent.getDataMap(switchName);

        if (!switchModel.isStatus()) {
            return false;
        }

        if (switchModel.getMeteredStatus() == null) {
            return true;
        }

        String contextKey = getContextKey(contextMap);
        return sharedDataStoreComponent.getNextWeight(switchName, contextKey);
    }

    private String getContextKey(Map<String, String> contextMap) {
        if (contextMap != null && contextMap.containsKey(SwitchHubConstants.Switch.CONTEXT_SUB_KEY)) {
            return contextMap.get(SwitchHubConstants.Switch.CONTEXT_SUB_KEY);
        }
        return SwitchHubConstants.Switch.DEFAULT_KEY;
    }
}
