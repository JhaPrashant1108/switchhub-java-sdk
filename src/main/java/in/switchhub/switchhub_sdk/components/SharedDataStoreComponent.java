package in.switchhub.switchhub_sdk.components;

import in.switchhub.switchhub_sdk.constants.SwitchHubConstants;
import in.switchhub.switchhub_sdk.models.SwitchModel;
import in.switchhub.switchhub_sdk.util.WeightedBooleanGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SharedDataStoreComponent {

    private static final Logger log = LoggerFactory.getLogger(SharedDataStoreComponent.class);

    private final Map<String, SwitchModel> dataMap = new ConcurrentHashMap<>();
    private final Map<String, Map<String, WeightedBooleanGenerator>> weightedMeteredMap = new HashMap<>();

    public void putDataMap(String key, SwitchModel value) {
        dataMap.put(key, value);
    }

    public SwitchModel getDataMap(String key) {
        return dataMap.get(key);
    }

    public boolean dataMapContainsKey(String key) {
        return dataMap.containsKey(key);
    }

    public void dataMapRemove(String key) {
        dataMap.remove(key);
    }

    public Map<String, SwitchModel> dataMapGetAll() {
        return dataMap;
    }

    public void registerWeights(String key, String meteredKey, int truePercentage) {
        Map<String, WeightedBooleanGenerator> weightedMap = weightedMeteredMap.computeIfAbsent(key, k -> new HashMap<>());
        weightedMap.put(meteredKey, new WeightedBooleanGenerator(truePercentage));
    }

    public boolean getNextWeight(String key, String meteredKey) {
        if (!weightedMeteredMap.containsKey(key)) {
            log.error("{}: {}", SwitchHubConstants.ErrorMessages.NO_GENERATOR_FOUND, key);
            return false;
        }

        Map<String, WeightedBooleanGenerator> generatorMap = weightedMeteredMap.get(key);
        WeightedBooleanGenerator generator = generatorMap.get(meteredKey);

        if (generator == null) {
            log.error("{}: {} for meteredKey: {}", SwitchHubConstants.ErrorMessages.NO_GENERATOR_FOUND, key, meteredKey);
            generator = generatorMap.get(SwitchHubConstants.Switch.DEFAULT_KEY);
        }

        return generator != null && generator.next();
    }

    public boolean weightedMeteredMapContainsKey(String key) {
        return weightedMeteredMap.containsKey(key);
    }

    public void resetWeightedMeteredMap(String key) {
        weightedMeteredMap.remove(key);
    }
}
