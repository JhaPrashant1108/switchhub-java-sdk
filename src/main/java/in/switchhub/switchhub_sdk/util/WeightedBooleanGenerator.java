package in.switchhub.switchhub_sdk.util;

import in.switchhub.switchhub_sdk.constants.SwitchHubConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WeightedBooleanGenerator {

    private final List<Boolean> values;
    private int index = 0;

    public WeightedBooleanGenerator(int truePercentage) {
        if (truePercentage < SwitchHubConstants.Validation.MIN_PERCENTAGE ||
            truePercentage > SwitchHubConstants.Validation.MAX_PERCENTAGE) {
            throw new IllegalArgumentException(SwitchHubConstants.ErrorMessages.PERCENTAGE_OUT_OF_RANGE);
        }

        values = new ArrayList<>(SwitchHubConstants.Validation.PERCENTAGE_POOL_SIZE);

        for (int i = 0; i < truePercentage; i++) {
            values.add(true);
        }
        for (int i = truePercentage; i < SwitchHubConstants.Validation.PERCENTAGE_POOL_SIZE; i++) {
            values.add(false);
        }

        Collections.shuffle(values);
    }

    public boolean next() {
        if (index >= values.size()) {
            Collections.shuffle(values);
            index = 0;
        }
        return values.get(index++);
    }
}
