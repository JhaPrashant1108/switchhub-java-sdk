package in.switchhub.switchhub_sdk.dtos;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class FetchSwitchRequestDto implements Serializable {

    private List<String> switchNames;

    public FetchSwitchRequestDto() {
    }

    public FetchSwitchRequestDto(List<String> switchNames) {
        this.switchNames = switchNames;
    }

    public List<String> getSwitchNames() {
        return switchNames;
    }

    public void setSwitchNames(List<String> switchNames) {
        this.switchNames = switchNames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FetchSwitchRequestDto that = (FetchSwitchRequestDto) o;
        return Objects.equals(switchNames, that.switchNames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(switchNames);
    }

    @Override
    public String toString() {
        return "FetchSwitchRequestDto{" +
                "switchNames=" + switchNames +
                '}';
    }
}
