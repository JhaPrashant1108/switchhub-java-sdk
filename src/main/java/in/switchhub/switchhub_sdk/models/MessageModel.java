package in.switchhub.switchhub_sdk.models;

import java.io.Serializable;
import java.util.Objects;

public class MessageModel implements Serializable {

    private String switchName;
    private SwitchModel switchDetails;

    public MessageModel() {
    }

    public MessageModel(String switchName, SwitchModel switchDetails) {
        this.switchName = switchName;
        this.switchDetails = switchDetails;
    }

    public String getSwitchName() {
        return switchName;
    }

    public void setSwitchName(String switchName) {
        this.switchName = switchName;
    }

    public SwitchModel getSwitchDetails() {
        return switchDetails;
    }

    public void setSwitchDetails(SwitchModel switchDetails) {
        this.switchDetails = switchDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageModel that = (MessageModel) o;
        return Objects.equals(switchName, that.switchName) &&
                Objects.equals(switchDetails, that.switchDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(switchName, switchDetails);
    }

    @Override
    public String toString() {
        return "MessageModel{" +
                "switchName='" + switchName + '\'' +
                ", switchDetails=" + switchDetails +
                '}';
    }
}
