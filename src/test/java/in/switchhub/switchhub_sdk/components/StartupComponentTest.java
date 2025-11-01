package in.switchhub.switchhub_sdk.components;

import in.switchhub.switchhub_sdk.config.EnvConfig;
import in.switchhub.switchhub_sdk.dtos.FetchSwitchRequestDto;
import in.switchhub.switchhub_sdk.dtos.FetchSwitchResponseDto;
import in.switchhub.switchhub_sdk.models.SwitchModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StartupComponentTest {

    @Mock
    private SwitchHubApiComponent switchHubApiComponent;

    @Mock
    private SharedDataStoreComponent sharedDataStoreComponent;

    @Mock
    private EnvConfig envConfig;

    @InjectMocks
    private StartupComponent startupComponent;

    @BeforeEach
    void setUp() {
        when(envConfig.getSwitchHubSdkSwitches()).thenReturn(Arrays.asList("feature-1", "feature-2"));
        when(envConfig.getSwitchHubSdkEnvironmentName()).thenReturn("production");
        when(envConfig.getSwitchHubSdkApplicationName()).thenReturn("test-app");
    }

    @Test
    void testOnApplicationReady_Success() {
        Map<String, SwitchModel> switchDetails = new HashMap<>();

        SwitchModel switch1 = new SwitchModel();
        switch1.setSwitchId("switch-1");
        switch1.setStatus(true);
        switchDetails.put("feature-1", switch1);

        FetchSwitchResponseDto response = new FetchSwitchResponseDto(switchDetails);

        when(switchHubApiComponent.fetchSwitch(any(), anyString(), anyString())).thenReturn(response);

        startupComponent.onApplicationReady();

        verify(switchHubApiComponent).fetchSwitch(any(FetchSwitchRequestDto.class), eq("production"), eq("test-app"));
        verify(sharedDataStoreComponent).putDataMap("feature-1", switch1);
    }

    @Test
    void testOnApplicationReady_WithMeteredStatus() {
        Map<String, Map<String, Integer>> meteredStatus = new HashMap<>();
        Map<String, Integer> defaultMetering = new HashMap<>();
        defaultMetering.put("trueValue", 50);
        meteredStatus.put("default", defaultMetering);

        SwitchModel switch1 = new SwitchModel();
        switch1.setSwitchId("switch-1");
        switch1.setStatus(true);
        switch1.setMeteredStatus(meteredStatus);

        Map<String, SwitchModel> switchDetails = new HashMap<>();
        switchDetails.put("feature-1", switch1);

        FetchSwitchResponseDto response = new FetchSwitchResponseDto(switchDetails);

        when(switchHubApiComponent.fetchSwitch(any(), anyString(), anyString())).thenReturn(response);

        startupComponent.onApplicationReady();

        verify(sharedDataStoreComponent).putDataMap("feature-1", switch1);
        verify(sharedDataStoreComponent).registerWeights("feature-1", "default", 50);
    }

    @Test
    void testOnApplicationReady_WithMultipleMeteredKeys() {
        Map<String, Map<String, Integer>> meteredStatus = new HashMap<>();

        Map<String, Integer> defaultMetering = new HashMap<>();
        defaultMetering.put("trueValue", 25);
        meteredStatus.put("default", defaultMetering);

        Map<String, Integer> premiumMetering = new HashMap<>();
        premiumMetering.put("trueValue", 75);
        meteredStatus.put("premium", premiumMetering);

        SwitchModel switch1 = new SwitchModel();
        switch1.setSwitchId("switch-1");
        switch1.setMeteredStatus(meteredStatus);

        Map<String, SwitchModel> switchDetails = new HashMap<>();
        switchDetails.put("feature-1", switch1);

        FetchSwitchResponseDto response = new FetchSwitchResponseDto(switchDetails);

        when(switchHubApiComponent.fetchSwitch(any(), anyString(), anyString())).thenReturn(response);

        startupComponent.onApplicationReady();

        verify(sharedDataStoreComponent).registerWeights("feature-1", "default", 25);
        verify(sharedDataStoreComponent).registerWeights("feature-1", "premium", 75);
    }

    @Test
    void testOnApplicationReady_WithNullMeteredStatus() {
        SwitchModel switch1 = new SwitchModel();
        switch1.setSwitchId("switch-1");
        switch1.setMeteredStatus(null);

        Map<String, SwitchModel> switchDetails = new HashMap<>();
        switchDetails.put("feature-1", switch1);

        FetchSwitchResponseDto response = new FetchSwitchResponseDto(switchDetails);

        when(switchHubApiComponent.fetchSwitch(any(), anyString(), anyString())).thenReturn(response);

        startupComponent.onApplicationReady();

        verify(sharedDataStoreComponent).putDataMap("feature-1", switch1);
        verify(sharedDataStoreComponent).registerWeights("feature-1", "default", 100);
    }

    @Test
    void testOnApplicationReady_WithNullResponse() {
        when(switchHubApiComponent.fetchSwitch(any(), anyString(), anyString())).thenReturn(null);

        startupComponent.onApplicationReady();

        verify(switchHubApiComponent).fetchSwitch(any(), anyString(), anyString());
        verify(sharedDataStoreComponent, never()).putDataMap(anyString(), any());
    }

    @Test
    void testOnApplicationReady_WithNullSwitchDetails() {
        FetchSwitchResponseDto response = new FetchSwitchResponseDto(null);

        when(switchHubApiComponent.fetchSwitch(any(), anyString(), anyString())).thenReturn(response);

        startupComponent.onApplicationReady();

        verify(sharedDataStoreComponent, never()).putDataMap(anyString(), any());
    }

    @Test
    void testOnApplicationReady_WithEmptySwitchDetails() {
        FetchSwitchResponseDto response = new FetchSwitchResponseDto(new HashMap<>());

        when(switchHubApiComponent.fetchSwitch(any(), anyString(), anyString())).thenReturn(response);

        startupComponent.onApplicationReady();

        verify(sharedDataStoreComponent, never()).putDataMap(anyString(), any());
    }

    @Test
    void testOnApplicationReady_WithNullSwitchModel() {
        Map<String, SwitchModel> switchDetails = new HashMap<>();
        switchDetails.put("feature-1", null);

        FetchSwitchResponseDto response = new FetchSwitchResponseDto(switchDetails);

        when(switchHubApiComponent.fetchSwitch(any(), anyString(), anyString())).thenReturn(response);

        startupComponent.onApplicationReady();

        verify(sharedDataStoreComponent, never()).putDataMap(anyString(), any());
    }

    @Test
    void testOnApplicationReady_WithMultipleSwitches() {
        SwitchModel switch1 = new SwitchModel();
        switch1.setSwitchId("switch-1");

        SwitchModel switch2 = new SwitchModel();
        switch2.setSwitchId("switch-2");

        Map<String, SwitchModel> switchDetails = new HashMap<>();
        switchDetails.put("feature-1", switch1);
        switchDetails.put("feature-2", switch2);

        FetchSwitchResponseDto response = new FetchSwitchResponseDto(switchDetails);

        when(switchHubApiComponent.fetchSwitch(any(), anyString(), anyString())).thenReturn(response);

        startupComponent.onApplicationReady();

        verify(sharedDataStoreComponent).putDataMap("feature-1", switch1);
        verify(sharedDataStoreComponent).putDataMap("feature-2", switch2);
    }

    @Test
    void testOnApplicationReady_ApiThrowsException() {
        when(switchHubApiComponent.fetchSwitch(any(), anyString(), anyString()))
                .thenThrow(new RuntimeException("API Error"));

        assertDoesNotThrow(() -> startupComponent.onApplicationReady());

        verify(switchHubApiComponent).fetchSwitch(any(), anyString(), anyString());
        verify(sharedDataStoreComponent, never()).putDataMap(anyString(), any());
    }

    @Test
    void testOnApplicationReady_CorrectRequestDto() {
        List<String> expectedSwitches = Arrays.asList("feature-1", "feature-2");
        when(envConfig.getSwitchHubSdkSwitches()).thenReturn(expectedSwitches);

        FetchSwitchResponseDto response = new FetchSwitchResponseDto(new HashMap<>());
        when(switchHubApiComponent.fetchSwitch(any(), anyString(), anyString())).thenReturn(response);

        ArgumentCaptor<FetchSwitchRequestDto> requestCaptor = ArgumentCaptor.forClass(FetchSwitchRequestDto.class);

        startupComponent.onApplicationReady();

        verify(switchHubApiComponent).fetchSwitch(requestCaptor.capture(), anyString(), anyString());

        FetchSwitchRequestDto capturedRequest = requestCaptor.getValue();
        assertEquals(expectedSwitches, capturedRequest.getSwitchNames());
    }

    @Test
    void testOnApplicationReady_WithMeteredStatusMissingTrueValue() {
        Map<String, Map<String, Integer>> meteredStatus = new HashMap<>();
        Map<String, Integer> defaultMetering = new HashMap<>();
        // No "trueValue" key
        defaultMetering.put("falseValue", 50);
        meteredStatus.put("default", defaultMetering);

        SwitchModel switch1 = new SwitchModel();
        switch1.setSwitchId("switch-1");
        switch1.setMeteredStatus(meteredStatus);

        Map<String, SwitchModel> switchDetails = new HashMap<>();
        switchDetails.put("feature-1", switch1);

        FetchSwitchResponseDto response = new FetchSwitchResponseDto(switchDetails);

        when(switchHubApiComponent.fetchSwitch(any(), anyString(), anyString())).thenReturn(response);

        startupComponent.onApplicationReady();

        verify(sharedDataStoreComponent).putDataMap("feature-1", switch1);
        // Should not register weights when trueValue is null
        verify(sharedDataStoreComponent, never()).registerWeights(eq("feature-1"), eq("default"), anyInt());
    }
}
