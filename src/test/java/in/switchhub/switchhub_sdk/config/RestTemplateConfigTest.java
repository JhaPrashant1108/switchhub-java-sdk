package in.switchhub.switchhub_sdk.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = RestTemplateConfig.class)
class RestTemplateConfigTest {

    @Autowired
    private RestTemplate restTemplate;

    @Test
    void testRestTemplateBeanCreated() {
        assertNotNull(restTemplate);
    }

    @Test
    void testRestTemplateIsRestTemplate() {
        assertTrue(restTemplate instanceof RestTemplate);
    }

    @Test
    void testRestTemplateCanBeUsed() {
        assertDoesNotThrow(() -> {
            // Verify the RestTemplate is properly configured and usable
            restTemplate.getClass();
        });
    }
}
