package in.switchhub.switchhub_sdk.util;

import in.switchhub.switchhub_sdk.components.SharedDataStoreComponent;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SwitchUtilTest {

    @Mock
    private SharedDataStoreComponent sharedDataStoreComponent;

    @InjectMocks
    private SwitchUtil switchUtil;

    private SwitchModel switchModel;

    @BeforeEach
    void setUp() {
        switchModel = new SwitchModel();
        switchModel.setSwitchId("switch-123");
        switchModel.setSwitchName("test-feature");
        switchModel.setStatus(true);
    }

    @Test
    void testGetSwitchValue_SwitchNotFound() {
        String switchName = "non-existent-switch";

        when(sharedDataStoreComponent.dataMapContainsKey(switchName)).thenReturn(false);

        boolean result = switchUtil.getSwitchValue(switchName, null);

        assertFalse(result);
        verify(sharedDataStoreComponent).dataMapContainsKey(switchName);
        verify(sharedDataStoreComponent, never()).getDataMap(anyString());
    }

    @Test
    void testGetSwitchValue_SwitchDisabled() {
        String switchName = "disabled-switch";
        switchModel.setStatus(false);

        when(sharedDataStoreComponent.dataMapContainsKey(switchName)).thenReturn(true);
        when(sharedDataStoreComponent.getDataMap(switchName)).thenReturn(switchModel);

        boolean result = switchUtil.getSwitchValue(switchName, null);

        assertFalse(result);
        verify(sharedDataStoreComponent).dataMapContainsKey(switchName);
        verify(sharedDataStoreComponent).getDataMap(switchName);
    }

    @Test
    void testGetSwitchValue_SwitchEnabled_NoMetering() {
        String switchName = "simple-switch";
        switchModel.setStatus(true);
        switchModel.setMeteredStatus(null);

        when(sharedDataStoreComponent.dataMapContainsKey(switchName)).thenReturn(true);
        when(sharedDataStoreComponent.getDataMap(switchName)).thenReturn(switchModel);

        boolean result = switchUtil.getSwitchValue(switchName, null);

        assertTrue(result);
        verify(sharedDataStoreComponent).dataMapContainsKey(switchName);
        verify(sharedDataStoreComponent).getDataMap(switchName);
        verify(sharedDataStoreComponent, never()).getNextWeight(anyString(), anyString());
    }

    @Test
    void testGetSwitchValue_SwitchEnabled_WithMetering_DefaultKey() {
        String switchName = "metered-switch";
        Map<String, Map<String, Integer>> meteredStatus = new HashMap<>();
        Map<String, Integer> defaultMetering = new HashMap<>();
        defaultMetering.put("trueValue", 50);
        meteredStatus.put("default", defaultMetering);

        switchModel.setStatus(true);
        switchModel.setMeteredStatus(meteredStatus);

        when(sharedDataStoreComponent.dataMapContainsKey(switchName)).thenReturn(true);
        when(sharedDataStoreComponent.getDataMap(switchName)).thenReturn(switchModel);
        when(sharedDataStoreComponent.getNextWeight(switchName, "default")).thenReturn(true);

        boolean result = switchUtil.getSwitchValue(switchName, null);

        assertTrue(result);
        verify(sharedDataStoreComponent).dataMapContainsKey(switchName);
        verify(sharedDataStoreComponent).getDataMap(switchName);
        verify(sharedDataStoreComponent).getNextWeight(switchName, "default");
    }

    @Test
    void testGetSwitchValue_SwitchEnabled_WithMetering_CustomSubKey() {
        String switchName = "metered-switch";
        Map<String, Map<String, Integer>> meteredStatus = new HashMap<>();
        Map<String, Integer> customMetering = new HashMap<>();
        customMetering.put("trueValue", 75);
        meteredStatus.put("premium", customMetering);

        switchModel.setStatus(true);
        switchModel.setMeteredStatus(meteredStatus);

        Map<String, String> contextMap = new HashMap<>();
        contextMap.put("subKey", "premium");

        when(sharedDataStoreComponent.dataMapContainsKey(switchName)).thenReturn(true);
        when(sharedDataStoreComponent.getDataMap(switchName)).thenReturn(switchModel);
        when(sharedDataStoreComponent.getNextWeight(switchName, "premium")).thenReturn(false);

        boolean result = switchUtil.getSwitchValue(switchName, contextMap);

        assertFalse(result);
        verify(sharedDataStoreComponent).dataMapContainsKey(switchName);
        verify(sharedDataStoreComponent).getDataMap(switchName);
        verify(sharedDataStoreComponent).getNextWeight(switchName, "premium");
    }

    @Test
    void testGetSwitchValue_WithNullContextMap() {
        String switchName = "metered-switch";
        Map<String, Map<String, Integer>> meteredStatus = new HashMap<>();
        Map<String, Integer> defaultMetering = new HashMap<>();
        defaultMetering.put("trueValue", 50);
        meteredStatus.put("default", defaultMetering);

        switchModel.setStatus(true);
        switchModel.setMeteredStatus(meteredStatus);

        when(sharedDataStoreComponent.dataMapContainsKey(switchName)).thenReturn(true);
        when(sharedDataStoreComponent.getDataMap(switchName)).thenReturn(switchModel);
        when(sharedDataStoreComponent.getNextWeight(switchName, "default")).thenReturn(true);

        boolean result = switchUtil.getSwitchValue(switchName, null);

        assertTrue(result);
        verify(sharedDataStoreComponent).getNextWeight(switchName, "default");
    }

    @Test
    void testGetSwitchValue_WithEmptyContextMap() {
        String switchName = "metered-switch";
        Map<String, Map<String, Integer>> meteredStatus = new HashMap<>();
        Map<String, Integer> defaultMetering = new HashMap<>();
        defaultMetering.put("trueValue", 50);
        meteredStatus.put("default", defaultMetering);

        switchModel.setStatus(true);
        switchModel.setMeteredStatus(meteredStatus);

        when(sharedDataStoreComponent.dataMapContainsKey(switchName)).thenReturn(true);
        when(sharedDataStoreComponent.getDataMap(switchName)).thenReturn(switchModel);
        when(sharedDataStoreComponent.getNextWeight(switchName, "default")).thenReturn(true);

        boolean result = switchUtil.getSwitchValue(switchName, new HashMap<>());

        assertTrue(result);
        verify(sharedDataStoreComponent).getNextWeight(switchName, "default");
    }

    @Test
    void testGetSwitchValue_WithContextMapButNoSubKey() {
        String switchName = "metered-switch";
        Map<String, Map<String, Integer>> meteredStatus = new HashMap<>();
        Map<String, Integer> defaultMetering = new HashMap<>();
        defaultMetering.put("trueValue", 50);
        meteredStatus.put("default", defaultMetering);

        switchModel.setStatus(true);
        switchModel.setMeteredStatus(meteredStatus);

        Map<String, String> contextMap = new HashMap<>();
        contextMap.put("otherKey", "someValue");

        when(sharedDataStoreComponent.dataMapContainsKey(switchName)).thenReturn(true);
        when(sharedDataStoreComponent.getDataMap(switchName)).thenReturn(switchModel);
        when(sharedDataStoreComponent.getNextWeight(switchName, "default")).thenReturn(false);

        boolean result = switchUtil.getSwitchValue(switchName, contextMap);

        assertFalse(result);
        verify(sharedDataStoreComponent).getNextWeight(switchName, "default");
    }

    @Test
    void testGetSwitchValue_MultipleCallsWithSameSwitch() {
        String switchName = "metered-switch";
        Map<String, Map<String, Integer>> meteredStatus = new HashMap<>();
        Map<String, Integer> defaultMetering = new HashMap<>();
        defaultMetering.put("trueValue", 50);
        meteredStatus.put("default", defaultMetering);

        switchModel.setStatus(true);
        switchModel.setMeteredStatus(meteredStatus);

        when(sharedDataStoreComponent.dataMapContainsKey(switchName)).thenReturn(true);
        when(sharedDataStoreComponent.getDataMap(switchName)).thenReturn(switchModel);
        when(sharedDataStoreComponent.getNextWeight(switchName, "default"))
                .thenReturn(true)
                .thenReturn(false)
                .thenReturn(true);

        boolean result1 = switchUtil.getSwitchValue(switchName, null);
        boolean result2 = switchUtil.getSwitchValue(switchName, null);
        boolean result3 = switchUtil.getSwitchValue(switchName, null);

        assertTrue(result1);
        assertFalse(result2);
        assertTrue(result3);
        verify(sharedDataStoreComponent, times(3)).getNextWeight(switchName, "default");
    }

    @Test
    void testGetSwitchValue_WithDifferentSubKeys() {
        String switchName = "metered-switch";
        Map<String, Map<String, Integer>> meteredStatus = new HashMap<>();
        meteredStatus.put("default", Map.of("trueValue", 50));
        meteredStatus.put("premium", Map.of("trueValue", 75));

        switchModel.setStatus(true);
        switchModel.setMeteredStatus(meteredStatus);

        when(sharedDataStoreComponent.dataMapContainsKey(switchName)).thenReturn(true);
        when(sharedDataStoreComponent.getDataMap(switchName)).thenReturn(switchModel);
        when(sharedDataStoreComponent.getNextWeight(switchName, "default")).thenReturn(true);
        when(sharedDataStoreComponent.getNextWeight(switchName, "premium")).thenReturn(false);

        Map<String, String> defaultContext = new HashMap<>();
        defaultContext.put("subKey", "default");

        Map<String, String> premiumContext = new HashMap<>();
        premiumContext.put("subKey", "premium");

        boolean resultDefault = switchUtil.getSwitchValue(switchName, defaultContext);
        boolean resultPremium = switchUtil.getSwitchValue(switchName, premiumContext);

        assertTrue(resultDefault);
        assertFalse(resultPremium);
        verify(sharedDataStoreComponent).getNextWeight(switchName, "default");
        verify(sharedDataStoreComponent).getNextWeight(switchName, "premium");
    }
}
