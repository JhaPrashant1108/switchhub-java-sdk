package in.switchhub.switchhub_sdk.models;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class SwitchModel implements Serializable {

    private String switchId;
    private String switchName;
    private String applicationName;
    private boolean status;
    private Map<String, Map<String, Integer>> meteredStatus;
    private String createdBy;
    private String createdAt;
    private String updatedBy;
    private String updatedAt;

    public SwitchModel() {
    }

    public SwitchModel(String switchId, String switchName, String applicationName, boolean status,
                       Map<String, Map<String, Integer>> meteredStatus, String createdBy,
                       String createdAt, String updatedBy, String updatedAt) {
        this.switchId = switchId;
        this.switchName = switchName;
        this.applicationName = applicationName;
        this.status = status;
        this.meteredStatus = meteredStatus;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedBy = updatedBy;
        this.updatedAt = updatedAt;
    }

    public String getSwitchId() {
        return switchId;
    }

    public void setSwitchId(String switchId) {
        this.switchId = switchId;
    }

    public String getSwitchName() {
        return switchName;
    }

    public void setSwitchName(String switchName) {
        this.switchName = switchName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Map<String, Map<String, Integer>> getMeteredStatus() {
        return meteredStatus;
    }

    public void setMeteredStatus(Map<String, Map<String, Integer>> meteredStatus) {
        this.meteredStatus = meteredStatus;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SwitchModel that = (SwitchModel) o;
        return status == that.status &&
                Objects.equals(switchId, that.switchId) &&
                Objects.equals(switchName, that.switchName) &&
                Objects.equals(applicationName, that.applicationName) &&
                Objects.equals(meteredStatus, that.meteredStatus) &&
                Objects.equals(createdBy, that.createdBy) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(updatedBy, that.updatedBy) &&
                Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(switchId, switchName, applicationName, status, meteredStatus,
                createdBy, createdAt, updatedBy, updatedAt);
    }

    @Override
    public String toString() {
        return "SwitchModel{" +
                "switchId='" + switchId + '\'' +
                ", switchName='" + switchName + '\'' +
                ", applicationName='" + applicationName + '\'' +
                ", status=" + status +
                ", meteredStatus=" + meteredStatus +
                ", createdBy='" + createdBy + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}