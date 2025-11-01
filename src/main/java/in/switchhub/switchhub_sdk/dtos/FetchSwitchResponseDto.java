package in.switchhub.switchhub_sdk.dtos;

import in.switchhub.switchhub_sdk.models.SwitchModel;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class FetchSwitchResponseDto implements Serializable {

    private Map<String, SwitchModel> switchDetails;

    public FetchSwitchResponseDto() {
    }

    public FetchSwitchResponseDto(Map<String, SwitchModel> switchDetails) {
        this.switchDetails = switchDetails;
    }

    public Map<String, SwitchModel> getSwitchDetails() {
        return switchDetails;
    }

    public void setSwitchDetails(Map<String, SwitchModel> switchDetails) {
        this.switchDetails = switchDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FetchSwitchResponseDto that = (FetchSwitchResponseDto) o;
        return Objects.equals(switchDetails, that.switchDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(switchDetails);
    }

    @Override
    public String toString() {
        return "FetchSwitchResponseDto{" +
                "switchDetails=" + switchDetails +
                '}';
    }
}

