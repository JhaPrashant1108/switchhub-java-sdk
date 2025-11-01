package in.switchhub.switchhub_sdk.components;

import in.switchhub.switchhub_sdk.config.EnvConfig;
import in.switchhub.switchhub_sdk.constants.SwitchHubConstants;
import in.switchhub.switchhub_sdk.dtos.FetchSwitchRequestDto;
import in.switchhub.switchhub_sdk.dtos.FetchSwitchResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SwitchHubApiComponent {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EnvConfig envConfig;

    public FetchSwitchResponseDto fetchSwitch(FetchSwitchRequestDto requestBody, String environmentName, String applicationName) {
        String url = envConfig.getSwitchHubBackendBaseUrl() + SwitchHubConstants.Api.FETCH_SWITCH_ENDPOINT;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(SwitchHubConstants.Api.HEADER_ENVIRONMENT_NAME, environmentName);
        headers.set(SwitchHubConstants.Api.HEADER_APPLICATION_NAME, applicationName);

        HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);

        return restTemplate.postForObject(url, entity, FetchSwitchResponseDto.class);
    }
}
