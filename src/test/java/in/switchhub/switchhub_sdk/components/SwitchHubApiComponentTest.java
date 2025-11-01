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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SwitchHubApiComponentTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private EnvConfig envConfig;

    @InjectMocks
    private SwitchHubApiComponent switchHubApiComponent;

    private FetchSwitchRequestDto requestDto;
    private FetchSwitchResponseDto expectedResponse;

    @BeforeEach
    void setUp() {
        requestDto = new FetchSwitchRequestDto(Arrays.asList("feature-1", "feature-2"));

        Map<String, SwitchModel> switchDetails = new HashMap<>();
        SwitchModel switch1 = new SwitchModel();
        switch1.setSwitchId("switch-1");
        switchDetails.put("feature-1", switch1);

        expectedResponse = new FetchSwitchResponseDto(switchDetails);
    }

    @Test
    void testFetchSwitch_Success() {
        String baseUrl = "http://localhost:8080";
        String environmentName = "production";
        String applicationName = "my-app";

        when(envConfig.getSwitchHubBackendBaseUrl()).thenReturn(baseUrl);
        when(restTemplate.postForObject(
                anyString(),
                any(HttpEntity.class),
                eq(FetchSwitchResponseDto.class)
        )).thenReturn(expectedResponse);

        FetchSwitchResponseDto response = switchHubApiComponent.fetchSwitch(
                requestDto, environmentName, applicationName
        );

        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(envConfig).getSwitchHubBackendBaseUrl();
        verify(restTemplate).postForObject(
                eq(baseUrl + "/fetchswitch"),
                any(HttpEntity.class),
                eq(FetchSwitchResponseDto.class)
        );
    }

    @Test
    void testFetchSwitch_CorrectUrl() {
        String baseUrl = "https://api.switchHub.com";
        when(envConfig.getSwitchHubBackendBaseUrl()).thenReturn(baseUrl);
        when(restTemplate.postForObject(anyString(), any(), eq(FetchSwitchResponseDto.class)))
                .thenReturn(expectedResponse);

        switchHubApiComponent.fetchSwitch(requestDto, "dev", "test-app");

        verify(restTemplate).postForObject(
                eq("https://api.switchHub.com/fetchswitch"),
                any(HttpEntity.class),
                eq(FetchSwitchResponseDto.class)
        );
    }

    @Test
    void testFetchSwitch_CorrectHeaders() {
        String baseUrl = "http://localhost:8080";
        String environmentName = "staging";
        String applicationName = "mobile-app";

        when(envConfig.getSwitchHubBackendBaseUrl()).thenReturn(baseUrl);
        when(restTemplate.postForObject(anyString(), any(), eq(FetchSwitchResponseDto.class)))
                .thenReturn(expectedResponse);

        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        switchHubApiComponent.fetchSwitch(requestDto, environmentName, applicationName);

        verify(restTemplate).postForObject(anyString(), entityCaptor.capture(), eq(FetchSwitchResponseDto.class));

        HttpEntity capturedEntity = entityCaptor.getValue();
        HttpHeaders headers = capturedEntity.getHeaders();

        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
        assertEquals("staging", headers.getFirst("environmentName"));
        assertEquals("mobile-app", headers.getFirst("applicationName"));
    }

    @Test
    void testFetchSwitch_CorrectRequestBody() {
        String baseUrl = "http://localhost:8080";
        when(envConfig.getSwitchHubBackendBaseUrl()).thenReturn(baseUrl);
        when(restTemplate.postForObject(anyString(), any(), eq(FetchSwitchResponseDto.class)))
                .thenReturn(expectedResponse);

        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        switchHubApiComponent.fetchSwitch(requestDto, "dev", "test-app");

        verify(restTemplate).postForObject(anyString(), entityCaptor.capture(), eq(FetchSwitchResponseDto.class));

        HttpEntity capturedEntity = entityCaptor.getValue();
        assertEquals(requestDto, capturedEntity.getBody());
    }

    @Test
    void testFetchSwitch_WithEmptyRequestDto() {
        FetchSwitchRequestDto emptyRequest = new FetchSwitchRequestDto();
        String baseUrl = "http://localhost:8080";

        when(envConfig.getSwitchHubBackendBaseUrl()).thenReturn(baseUrl);
        when(restTemplate.postForObject(anyString(), any(), eq(FetchSwitchResponseDto.class)))
                .thenReturn(new FetchSwitchResponseDto());

        FetchSwitchResponseDto response = switchHubApiComponent.fetchSwitch(
                emptyRequest, "dev", "test-app"
        );

        assertNotNull(response);
        verify(restTemplate).postForObject(anyString(), any(), eq(FetchSwitchResponseDto.class));
    }

    @Test
    void testFetchSwitch_WithNullResponse() {
        String baseUrl = "http://localhost:8080";

        when(envConfig.getSwitchHubBackendBaseUrl()).thenReturn(baseUrl);
        when(restTemplate.postForObject(anyString(), any(), eq(FetchSwitchResponseDto.class)))
                .thenReturn(null);

        FetchSwitchResponseDto response = switchHubApiComponent.fetchSwitch(
                requestDto, "dev", "test-app"
        );

        assertNull(response);
    }

    @Test
    void testFetchSwitch_WithDifferentEnvironments() {
        String baseUrl = "http://localhost:8080";

        when(envConfig.getSwitchHubBackendBaseUrl()).thenReturn(baseUrl);
        when(restTemplate.postForObject(anyString(), any(), eq(FetchSwitchResponseDto.class)))
                .thenReturn(expectedResponse);

        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        // Test with different environments
        switchHubApiComponent.fetchSwitch(requestDto, "production", "app1");
        verify(restTemplate, times(1)).postForObject(anyString(), entityCaptor.capture(), eq(FetchSwitchResponseDto.class));

        HttpHeaders headers = entityCaptor.getValue().getHeaders();
        assertEquals("production", headers.getFirst("environmentName"));
        assertEquals("app1", headers.getFirst("applicationName"));
    }

    @Test
    void testFetchSwitch_MultipleCallsWithDifferentParameters() {
        String baseUrl = "http://localhost:8080";

        when(envConfig.getSwitchHubBackendBaseUrl()).thenReturn(baseUrl);
        when(restTemplate.postForObject(anyString(), any(), eq(FetchSwitchResponseDto.class)))
                .thenReturn(expectedResponse);

        switchHubApiComponent.fetchSwitch(requestDto, "dev", "app1");
        switchHubApiComponent.fetchSwitch(requestDto, "staging", "app2");
        switchHubApiComponent.fetchSwitch(requestDto, "production", "app3");

        verify(restTemplate, times(3)).postForObject(anyString(), any(), eq(FetchSwitchResponseDto.class));
    }
}
