package in.switchhub.switchhub_sdk.components;

import in.switchhub.switchhub_sdk.models.MessageModel;
import in.switchhub.switchhub_sdk.models.SwitchModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SwitchConsumerTest {

    @Mock
    private SharedDataStoreComponent sharedDataStoreComponent;

    @InjectMocks
    private SwitchConsumer switchConsumer;

    private SwitchModel switchModel;
    private MessageModel messageModel;

    @BeforeEach
    void setUp() {
        switchModel = new SwitchModel();
        switchModel.setSwitchId("switch-123");
        switchModel.setSwitchName("test-feature");
        switchModel.setStatus(true);

        messageModel = new MessageModel();
        messageModel.setSwitchName("test-feature");
        messageModel.setSwitchDetails(switchModel);
    }

    @Test
    void testHandleFlagChange_BasicSwitch() {
        switchModel.setMeteredStatus(null);

        switchConsumer.handleFlagChange(messageModel);

        verify(sharedDataStoreComponent).putDataMap("test-feature", switchModel);
        verify(sharedDataStoreComponent).registerWeights("test-feature", "default", 100);
    }

    @Test
    void testHandleFlagChange_WithMeteredStatus() {
        Map<String, Map<String, Integer>> meteredStatus = new HashMap<>();
        Map<String, Integer> defaultMetering = new HashMap<>();
        defaultMetering.put("trueValue", 50);
        meteredStatus.put("default", defaultMetering);

        switchModel.setMeteredStatus(meteredStatus);

        switchConsumer.handleFlagChange(messageModel);

        verify(sharedDataStoreComponent).putDataMap("test-feature", switchModel);
        verify(sharedDataStoreComponent).resetWeightedMeteredMap("test-feature");
        verify(sharedDataStoreComponent).registerWeights("test-feature", "default", 50);
    }

    @Test
    void testHandleFlagChange_WithMultipleMeteredKeys() {
        Map<String, Map<String, Integer>> meteredStatus = new HashMap<>();

        Map<String, Integer> defaultMetering = new HashMap<>();
        defaultMetering.put("trueValue", 30);
        meteredStatus.put("default", defaultMetering);

        Map<String, Integer> premiumMetering = new HashMap<>();
        premiumMetering.put("trueValue", 80);
        meteredStatus.put("premium", premiumMetering);

        switchModel.setMeteredStatus(meteredStatus);

        switchConsumer.handleFlagChange(messageModel);

        verify(sharedDataStoreComponent).putDataMap("test-feature", switchModel);
        verify(sharedDataStoreComponent).resetWeightedMeteredMap("test-feature");
        verify(sharedDataStoreComponent).registerWeights("test-feature", "default", 30);
        verify(sharedDataStoreComponent).registerWeights("test-feature", "premium", 80);
    }

    @Test
    void testHandleFlagChange_ResetsWeightsBeforeRegistering() {
        Map<String, Map<String, Integer>> meteredStatus = new HashMap<>();
        Map<String, Integer> defaultMetering = new HashMap<>();
        defaultMetering.put("trueValue", 75);
        meteredStatus.put("default", defaultMetering);

        switchModel.setMeteredStatus(meteredStatus);

        switchConsumer.handleFlagChange(messageModel);

        // Verify reset is called before registering new weights
        verify(sharedDataStoreComponent).resetWeightedMeteredMap("test-feature");
        verify(sharedDataStoreComponent).registerWeights("test-feature", "default", 75);
    }

    @Test
    void testHandleFlagChange_WithNullMeteredStatus() {
        switchModel.setMeteredStatus(null);

        switchConsumer.handleFlagChange(messageModel);

        verify(sharedDataStoreComponent).putDataMap("test-feature", switchModel);
        verify(sharedDataStoreComponent, never()).resetWeightedMeteredMap(anyString());
        verify(sharedDataStoreComponent).registerWeights("test-feature", "default", 100);
    }

    @Test
    void testHandleFlagChange_WithMeteredStatusMissingTrueValue() {
        Map<String, Map<String, Integer>> meteredStatus = new HashMap<>();
        Map<String, Integer> defaultMetering = new HashMap<>();
        // No "trueValue" key
        defaultMetering.put("falseValue", 50);
        meteredStatus.put("default", defaultMetering);

        switchModel.setMeteredStatus(meteredStatus);

        switchConsumer.handleFlagChange(messageModel);

        verify(sharedDataStoreComponent).putDataMap("test-feature", switchModel);
        verify(sharedDataStoreComponent).resetWeightedMeteredMap("test-feature");
        // Should not register weights when trueValue is null
        verify(sharedDataStoreComponent, never()).registerWeights(eq("test-feature"), eq("default"), anyInt());
    }

    @Test
    void testHandleFlagChange_UpdateExistingSwitch() {
        // First flag change
        switchConsumer.handleFlagChange(messageModel);

        // Modify the switch
        switchModel.setStatus(false);
        MessageModel updatedMessage = new MessageModel("test-feature", switchModel);

        // Second flag change
        switchConsumer.handleFlagChange(updatedMessage);

        // Should be called twice
        verify(sharedDataStoreComponent, times(2)).putDataMap("test-feature", switchModel);
    }

    @Test
    void testHandleFlagChange_DifferentSwitchNames() {
        MessageModel message1 = new MessageModel("feature-1", switchModel);
        MessageModel message2 = new MessageModel("feature-2", switchModel);

        switchConsumer.handleFlagChange(message1);
        switchConsumer.handleFlagChange(message2);

        verify(sharedDataStoreComponent).putDataMap("feature-1", switchModel);
        verify(sharedDataStoreComponent).putDataMap("feature-2", switchModel);
    }

    @Test
    void testGetFlag_ExistingFlag() {
        when(sharedDataStoreComponent.getDataMap("test-feature")).thenReturn(switchModel);

        SwitchModel result = switchConsumer.getFlag("test-feature");

        assertNotNull(result);
        assertEquals(switchModel, result);
        verify(sharedDataStoreComponent).getDataMap("test-feature");
    }

    @Test
    void testGetFlag_NonExistingFlag() {
        when(sharedDataStoreComponent.getDataMap("non-existent")).thenReturn(null);

        SwitchModel result = switchConsumer.getFlag("non-existent");

        assertNull(result);
        verify(sharedDataStoreComponent).getDataMap("non-existent");
    }

    @Test
    void testGetFlag_MultipleFlags() {
        SwitchModel switch1 = new SwitchModel();
        switch1.setSwitchId("switch-1");

        SwitchModel switch2 = new SwitchModel();
        switch2.setSwitchId("switch-2");

        when(sharedDataStoreComponent.getDataMap("feature-1")).thenReturn(switch1);
        when(sharedDataStoreComponent.getDataMap("feature-2")).thenReturn(switch2);

        SwitchModel result1 = switchConsumer.getFlag("feature-1");
        SwitchModel result2 = switchConsumer.getFlag("feature-2");

        assertEquals("switch-1", result1.getSwitchId());
        assertEquals("switch-2", result2.getSwitchId());
    }

    @Test
    void testHandleFlagChange_WithEmptyMeteredStatus() {
        Map<String, Map<String, Integer>> meteredStatus = new HashMap<>();
        switchModel.setMeteredStatus(meteredStatus);

        switchConsumer.handleFlagChange(messageModel);

        verify(sharedDataStoreComponent).putDataMap("test-feature", switchModel);
        verify(sharedDataStoreComponent).resetWeightedMeteredMap("test-feature");
        // No weights should be registered since map is empty
        verify(sharedDataStoreComponent, never()).registerWeights(anyString(), anyString(), anyInt());
    }

    @Test
    void testHandleFlagChange_WithMixedMeteredKeys() {
        Map<String, Map<String, Integer>> meteredStatus = new HashMap<>();

        // One key with trueValue
        Map<String, Integer> validMetering = new HashMap<>();
        validMetering.put("trueValue", 40);
        meteredStatus.put("valid", validMetering);

        // One key without trueValue
        Map<String, Integer> invalidMetering = new HashMap<>();
        invalidMetering.put("otherKey", 60);
        meteredStatus.put("invalid", invalidMetering);

        switchModel.setMeteredStatus(meteredStatus);

        switchConsumer.handleFlagChange(messageModel);

        verify(sharedDataStoreComponent).putDataMap("test-feature", switchModel);
        verify(sharedDataStoreComponent).resetWeightedMeteredMap("test-feature");
        verify(sharedDataStoreComponent).registerWeights("test-feature", "valid", 40);
        verify(sharedDataStoreComponent, never()).registerWeights(eq("test-feature"), eq("invalid"), anyInt());
    }

    @Test
    void testHandleFlagChange_SwitchFromMeteredToNonMetered() {
        // First change with metered status
        Map<String, Map<String, Integer>> meteredStatus = new HashMap<>();
        Map<String, Integer> defaultMetering = new HashMap<>();
        defaultMetering.put("trueValue", 50);
        meteredStatus.put("default", defaultMetering);
        switchModel.setMeteredStatus(meteredStatus);

        switchConsumer.handleFlagChange(messageModel);

        verify(sharedDataStoreComponent).resetWeightedMeteredMap("test-feature");
        verify(sharedDataStoreComponent).registerWeights("test-feature", "default", 50);

        // Second change without metered status
        switchModel.setMeteredStatus(null);
        MessageModel updatedMessage = new MessageModel("test-feature", switchModel);

        switchConsumer.handleFlagChange(updatedMessage);

        // Reset should not be called for null metered status
        verify(sharedDataStoreComponent, times(1)).resetWeightedMeteredMap("test-feature");
        verify(sharedDataStoreComponent).registerWeights("test-feature", "default", 100);
    }
}
